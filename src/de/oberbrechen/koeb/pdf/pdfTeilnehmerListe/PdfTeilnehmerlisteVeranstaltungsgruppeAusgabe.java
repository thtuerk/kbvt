package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabe;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.pdf.PdfDokument;

public class PdfTeilnehmerlisteVeranstaltungsgruppeAusgabe 
  extends PdfSpeicherbareAusgabe implements VeranstaltungsgruppeAusgabe{
  
  private Veranstaltungsgruppe veranstaltungsgruppe;
  private boolean zeigeZusammenfassung;
  private boolean zeigeBemerkungen;
  private boolean querFormat; 
  
  public PdfTeilnehmerlisteVeranstaltungsgruppeAusgabe(boolean zeigeZusammenfassung,
      boolean zeigeBemerkungen, boolean querFormat) {
    this.zeigeZusammenfassung = zeigeZusammenfassung;
    this.zeigeBemerkungen = zeigeBemerkungen;
    this.querFormat = querFormat;
  }
    
  public String getName() {
    return "Teilnehmerliste";
  }

  public String getBeschreibung() {
    return "Gibt die Teilnehmer an einer Veranstaltungsgruppe aus!";
  }

  protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    if (veranstaltungsgruppe == null) return null;
    
    PdfTeilnehmerListeVeranstaltungsgruppeFactory factory =
      new PdfTeilnehmerListeVeranstaltungsgruppeFactory(veranstaltungsgruppe);
    
    factory.setZeigeBemerkungen(zeigeBemerkungen);
    factory.setZeigeZusammenfassung(zeigeZusammenfassung);
    factory.setQuerformat(querFormat);
    return factory.createPdfDokument();
  }

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe daten) {
    veranstaltungsgruppe = daten;
  }
  
  public boolean benoetigtGUI() {
    return false;
  }              
}
