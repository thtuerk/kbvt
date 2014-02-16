package de.oberbrechen.koeb.pdf;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.*;

/**
 * Eine Ausgabe, die als Aktion ein PdfDokument oeffnet
 * @author thtuerk
 */
public class PdfDokumentAusgabe implements Ausgabe {

  private String name;
  private String beschreibung;
  private PdfDokument pdfDokument;
  private PdfDokumentFactory factory;
  
  public PdfDokumentAusgabe(String name, String beschreibung,
    PdfDokument pdfDokument) {
    this.name = name;
    this.beschreibung = beschreibung;
    this.pdfDokument = pdfDokument;
    this.factory = null;
  }

  public PdfDokumentAusgabe(String name, String beschreibung,
      PdfDokumentFactory factory) {
    this.name = name;
    this.beschreibung = beschreibung;
    this.pdfDokument = null;
    this.factory = factory;
  }
  
  public String getName() {
    return name;
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public void run(javax.swing.JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    if (factory != null) pdfDokument = factory.createPdfDokument();
    pdfDokument.zeige(false);
  }

  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
    if (factory != null) pdfDokument = factory.createPdfDokument();
    pdfDokument.schreibeInDatei(datei);
  }

  public boolean istSpeicherbar() {
    return true;
  }
  
  public String getStandardErweiterung() {
    return "pdf";
  }    
  
  public boolean benoetigtGUI() {
    return false;
  }    
}
