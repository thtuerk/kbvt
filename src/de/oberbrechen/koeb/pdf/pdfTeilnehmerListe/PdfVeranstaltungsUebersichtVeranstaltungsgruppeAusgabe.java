package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabe;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.pdf.PdfDokument;

public class PdfVeranstaltungsUebersichtVeranstaltungsgruppeAusgabe 
  extends PdfSpeicherbareAusgabe implements VeranstaltungsgruppeAusgabe {
  
  private Veranstaltungsgruppe veranstaltungsgruppe;
  private boolean zeigeBemerkungen;

  public PdfVeranstaltungsUebersichtVeranstaltungsgruppeAusgabe(boolean zeigeBemerkungen) {
    this.zeigeBemerkungen = zeigeBemerkungen;
  }
    
  public String getName() {
    return "Veranstaltungsübersicht";
  }

  public String getBeschreibung() {
    return "Gibt eine Übersicht über die Veranstaltungen einer Veranstaltungsgruppe aus!";
  }

  protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    if (veranstaltungsgruppe == null) throw new NullPointerException("Veranstaltungsgruppe nicht gesetzt!");
    
    PdfVeranstaltungsUebersichtVeranstaltungsgruppeFactory factory = 
      new PdfVeranstaltungsUebersichtVeranstaltungsgruppeFactory(veranstaltungsgruppe);      
    factory.setZeigeBemerkungen(zeigeBemerkungen);
    return factory.createPdfDokument();
  }

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe daten) {
    veranstaltungsgruppe = daten;
  }        
  
  public boolean benoetigtGUI() {
    return false;
  }              
}
