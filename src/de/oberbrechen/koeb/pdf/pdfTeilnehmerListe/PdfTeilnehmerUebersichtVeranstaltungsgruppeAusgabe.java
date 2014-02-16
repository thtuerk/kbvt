package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabe;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.pdf.PdfDokument;

public class PdfTeilnehmerUebersichtVeranstaltungsgruppeAusgabe extends PdfSpeicherbareAusgabe 
  implements VeranstaltungsgruppeAusgabe {
  
  private Veranstaltungsgruppe veranstaltungsgruppe;

  public PdfTeilnehmerUebersichtVeranstaltungsgruppeAusgabe() {
  }
    
  public String getName() {
    return "Teilnehmerübersicht";
  }

  public String getBeschreibung() {
    return "Gibt eine Übersicht über die Teilnehmer an einer Veranstaltungsgruppe aus!";
  }

  protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    if (veranstaltungsgruppe == null) throw new NullPointerException("Veranstaltungsgruppe nicht gesetzt!");
    
    PdfTeilnehmerUebersichtVeranstaltungsgruppeFactory factory = new
      PdfTeilnehmerUebersichtVeranstaltungsgruppeFactory(veranstaltungsgruppe);
    return factory.createPdfDokument();       
  }

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe daten) {
    veranstaltungsgruppe = daten;
  }
  
  public boolean benoetigtGUI() {
    return false;
  }              
}
