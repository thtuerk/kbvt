package de.oberbrechen.koeb.pdf.pdfBenutzerListe;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.AbstractBenutzerAusgabe;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.pdf.PdfDokument;

public class PdfBenutzerlisteBenutzerAusgabe extends AbstractBenutzerAusgabe {

  public PdfBenutzerlisteBenutzerAusgabe() {
  }
    
  public String getName() {
    return "detaillierte PDF-Benutzerliste";
  }

  public String getBeschreibung() {
    return "Gibt die Benutzer mit Details als Liste in eine PDF-Datei aus!";
  }

  protected PdfDokument getPdfDokument() throws Exception {
    if (daten == null) daten = new BenutzerListe();
    
    PdfBenutzerListeFactory pdfBenutzerListeFactory = 
      new PdfBenutzerListeFactory(daten);
    pdfBenutzerListeFactory.setTitel(datenTitel);
    pdfBenutzerListeFactory.setSortierung(datenSortierung, datenUmgekehrteSortierung);
    pdfBenutzerListeFactory.setQuerformat(false);
    return pdfBenutzerListeFactory.createPdfDokument();
  }        

  public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    getPdfDokument().zeige(false);
  }

  public boolean istSpeicherbar() {
    return true;
  }

  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
    getPdfDokument().schreibeInDatei(datei);
  }
  
  public String getStandardErweiterung() {
    return "pdf";
  }
  
  public boolean benoetigtGUI() {
    return false;
  }            
}
