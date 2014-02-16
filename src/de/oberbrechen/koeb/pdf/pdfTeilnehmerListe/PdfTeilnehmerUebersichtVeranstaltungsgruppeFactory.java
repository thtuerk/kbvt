package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

public class PdfTeilnehmerUebersichtVeranstaltungsgruppeFactory 
  extends PdfTemplateDokumentFactory {

  Veranstaltungsgruppe gruppe;
  
  public PdfTeilnehmerUebersichtVeranstaltungsgruppeFactory(Veranstaltungsgruppe veranstaltungsgruppe) {
    this.gruppe = veranstaltungsgruppe;
  }
    

  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(
        "Teilnehmerübersicht "+gruppe.getName());

    String teilnehmerAnzahl;
    int teilnehmerAnzahlInt = Datenbank.getInstance().
    getVeranstaltungsteilnahmeFactory().getTeilnehmerAnzahl(gruppe);
    teilnehmerAnzahl = teilnehmerAnzahlInt == 0?
      "keine Teilnehmer":teilnehmerAnzahlInt+" Teilnehmer";
    
    TabellenModell modell = new PdfTeilnehmerUebersichtVeranstaltungsgruppeTabellenModell(gruppe);    
    PdfTabelle tabelle = new PdfTabelle(modell);

    result.addHauptUeberschrift("Teilnehmerübersicht "+gruppe.getName(), null, teilnehmerAnzahl);    
    result.addPdfTemplateFactory(tabelle);
    
    return result;
  }
}
