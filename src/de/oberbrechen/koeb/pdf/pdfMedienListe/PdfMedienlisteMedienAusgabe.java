package de.oberbrechen.koeb.pdf.pdfMedienListe;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.*;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.pdf.PdfDokument;

class PdfMedienlisteMedienAusgabe extends AbstractMedienAusgabe 
  implements KonfigurierbareAusgabe {

  PdfMedienlisteMedienAusgabeKonfigDialog konfigDialog;
  boolean querFormat;
  
  public PdfMedienlisteMedienAusgabe() {
  }
    
  public String getName() {
    return "PDF-Medienliste";
  }

  public String getBeschreibung() {
    return "Gibt die Medien als Tabelle in eine PDF-Datei aus!";
  }

  protected PdfDokument getPdfDokument() throws Exception {
    if (daten == null) daten = new MedienListe();
    if (konfigDialog == null) 
      konfigDialog = new PdfMedienlisteMedienAusgabeKonfigDialog(false);
    
    PdfMedienListeFactory pdfMedienListeFactory = 
      new PdfMedienListeFactory(daten);
    pdfMedienListeFactory.setTitel(datenTitel);
    pdfMedienListeFactory.setSortierung(datenSortierung, datenUmgekehrteSortierung);
    pdfMedienListeFactory.setZeigeSortierteSpalteHintergrund(
        konfigDialog.getZeigeSpaltenHintergrund());
    pdfMedienListeFactory.setZeigeZeilenHintergrund(
        konfigDialog.getZeigeZeilenHintergrund());
    pdfMedienListeFactory.setQuerformat(querFormat);
    return pdfMedienListeFactory.createPdfDokument();
  }        

  public void konfiguriere(JFrame main) {
    if (konfigDialog == null) 
      konfigDialog = new PdfMedienlisteMedienAusgabeKonfigDialog(false);
    
    konfigDialog.show(main);
    this.setSortierung(
        konfigDialog.getSortierung(), konfigDialog.getUmgekehrteSortierung());
    this.setTitel(konfigDialog.getTitel());
    this.querFormat = konfigDialog.getQuerFormat();
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
