package de.oberbrechen.koeb.ausgaben;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.pdf.PdfDokument;

/**
* Dieses Interface repräsentiert eine Ausgabe, die in eine Datei gespeichert werden kann.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public abstract class PdfSpeicherbareAusgabe implements Ausgabe {

  /**
   * Diese Methode liefert das PDF-Dokument, das gespeichert werden soll. Da evtl. dafür ein Diaglog angezeigt werden 
   * muss, in dem weitere Optinen festgelegt werden können, wird ein Frame übergeben. Außerdem kann über einen Parameter
   * bestimmt werden, ob ein solcher Dialog verwendet werden soll. Das abschalten des Dialoges und das verwenden von 
   * Standardeinstellungen dient zur nicht interaktiven Ausführung mehrerer Ausgaben.
   * @param hauptFenster
   * @param zeigeDialog
   * @return das Dokument
   * @throws Exception
   */
  protected abstract PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception;
  
  public boolean istSpeicherbar() {
    return true;
  }

  public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    PdfDokument dokument = getPdfDokument(hauptFenster, zeigeDialog);
    if (dokument != null) dokument.zeige(false);
  }
  
  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
    PdfDokument dokument = getPdfDokument(hauptFenster, zeigeDialog);
    if (dokument != null) dokument.schreibeInDatei(datei);
  }    

  public String getStandardErweiterung() {
    return "pdf";
  }
}