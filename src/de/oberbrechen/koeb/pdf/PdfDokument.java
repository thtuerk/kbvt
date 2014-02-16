package de.oberbrechen.koeb.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse repräsentiert ein allgemeines PDF-Dokument und bietet allgemeine
 * Methoden zur Bearbeitung desselben an.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class PdfDokument {

  Rectangle pageSize;
  
  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public PdfDokument() {
    this(false); 
  }
  
  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public PdfDokument(boolean querFormat) {
    this(PdfDokumentEinstellungen.getInstance().getPageSize(), querFormat);
  }

  /**
   * Erstellt ein neues PdfDokument in der übergebenen Papiergröße.
   */
  public PdfDokument(Rectangle pageSize, boolean querFormat) {
    if (querFormat) {
      this.pageSize = pageSize.rotate();
    } else {
      this.pageSize = pageSize;
    }
  }
    
  /**
   * Schreibt das Dokument in die übergebene Datei
   *
   * @param datei die Datei, in die das Dokument geschrieben werden soll
   * @throws Exception falls Probleme auftreten
   */
  public void schreibeInDatei(File datei) throws Exception {
    Document doc = new Document(this.pageSize);
    PdfWriter pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream(datei));
    doc.open();

    schreibeInDokument(pdfWriter, doc);

    doc.close();
  }

  /**
   * Gibt das Dokument im übergebenen PdfWriter und dem übergebenen Dokument
   * aus. Dieser Befehl kann benutzt werden, um verschiedene Dokumente
   * hintereinander in einer Datei auszugeben.
   *
   * @param writer der PdfWriter, in dem Ausgegebn werden soll
   * @throws Exception bei Problemen
   */
  public abstract void schreibeInDokument(PdfWriter writer, Document document)
    throws Exception;

  /**
   * Schreibt das Dokument in eine temporäre Datei
   *
   * @return die Datei, in die das Dokument geschrieben wurde
   * @throws Exception bei Problemen
   */
  public File schreibeInTempDatei() throws Exception {
    File ausgabeDatei = File.createTempFile("temp", ".pdf");
    ausgabeDatei.deleteOnExit();
    schreibeInDatei(ausgabeDatei);
    return ausgabeDatei;
  }

  /**
   * Zeigt das übergebene PDF-Dokument mit dem in der Konfigurationsdatei
   * eingestellten PDF-Viewer an
   *
   * @param pdf die Datei, die angezeigt werden soll
   * @param warten bestimmt, ob auf das Beenden des PDF-Viewers gewartet werden soll
   * @throws Exception falls ein Fehler beim Anzeigen auftritt
   */
  public static void zeigePdfDatei(File pdf, 
    boolean warten) throws Exception {
    
    String befehl = PdfDokumentEinstellungen.getInstance().getAnzeigeBefehl();    
    if (befehl == null || befehl.equals("")) {
      ErrorHandler.getInstance().handleError("Es ist kein PDF-Viewer konfiguriert. Daher " +        "kann keine PDF-Datei angezeigt werden!", false); 
      return;
    }

    Process zeigeProcess = Runtime.getRuntime().exec(befehl+" " + pdf);
    if (warten) zeigeProcess.waitFor();
  }

  /**
   * Schreibt das Dokument in eine temporäre PDF-Datei und zeigt diese an.
   * Die temporäre PDF-Datei wird sofort nach Programmende gelöscht. Erfolgt
   * das Anzeigen gegen Ende des Programms, kann es passieren, dass die
   * temporäre Datei gelöscht wird, bevor der PDF-Viewer sie öffnen kann.
   * In einem solchen Fall sollte die Warte-Option verwendet werden.
   *
   * @param Mitarbeiter der Mitarbeiter
   * @param warten bestimmt, ob auf das Beenden des PDF-Viewers gewartet werden soll
   * @throws IOException bei Problemen beim Zugriff aus die temporäre Datei
   * @throws Exception falls ein Fehler beim Anzeigen auftritt
   */
  public void zeige(boolean warten) throws Exception {
    File ausgabeDatei = schreibeInTempDatei();
    zeigePdfDatei(ausgabeDatei, warten);
  }
    
}