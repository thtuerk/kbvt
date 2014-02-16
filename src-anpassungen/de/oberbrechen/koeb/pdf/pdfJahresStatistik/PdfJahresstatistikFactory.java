package de.oberbrechen.koeb.pdf.pdfJahresStatistik;
 
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfAusleihStatistik.AusleihStatistikTabellenModell;
import de.oberbrechen.koeb.pdf.pdfInternetStatistik.InternetStatistikJahrTabellenModell;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

/**
 * Erstellt eine Jahres-Statistik.
 */
public class PdfJahresstatistikFactory extends PdfTemplateDokumentFactory {
  
  int jahr;
  AuswahlKonfiguration ausleihStatistik;
  AuswahlKonfiguration bestandStatistik;
  
  public PdfJahresstatistikFactory(int jahr, AuswahlKonfiguration ausleihStatistik,
      AuswahlKonfiguration bestandStatistik) {

    this.jahr = jahr;
    this.ausleihStatistik = ausleihStatistik;
    this.bestandStatistik = bestandStatistik;
  }
  
  public PdfDokument createPdfDokument() throws Exception {    
    String titel = "Jahresstatistik "+jahr;  
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(
        titel);    
    result.addHauptUeberschrift(titel, null, null);

    //Ausleihstatistik
    TabellenModell tabellenModell = 
      AusleihStatistikTabellenModell.createStatistikTabellenModell(
        ausleihStatistik, jahr);    
    PdfTabelle tabelle = new PdfTabelle(tabellenModell);
    result.addUnterUeberschrift("Ausleihstatistik", null, null); 
    result.addPdfTemplateFactory(tabelle);
    
    //Bestandstatistik
    tabellenModell = 
      new BestandStatistikJahrTabellenModell(jahr, bestandStatistik);
    tabelle = new PdfTabelle(tabellenModell);
    result.addUnterUeberschrift("Bestandstatistik", null, null); 
    result.addPdfTemplateFactory(tabelle);
    
    //Internetstatistik
    tabellenModell = 
      new InternetStatistikJahrTabellenModell(jahr, true);
    tabelle = new PdfTabelle(tabellenModell);
    result.addUnterUeberschrift("Internetstatistik", null, null); 
    result.addPdfTemplateFactory(tabelle);
    
    //Benutzer und Veranstaltungen
    tabellenModell = 
      new BenutzerUndVeranstaltungenStatistikJahrTabellenModell(jahr);
    tabelle = new PdfTabelle(tabellenModell);
    tabelle.setZeigeSpaltenKopf(false);
    result.addUnterUeberschrift("Benutzer und Veranstaltungen", null, null); 
    result.addPdfTemplateFactory(tabelle);
    
    //aktive Leser
    tabellenModell = 
      new AktiveLeserStatistikJahrTabellenModell(jahr, 25);
    tabelle = new PdfTabelle(tabellenModell);
    result.addUnterUeberschrift("Die 25 aktivsten Leser", null, null); 
    result.addPdfTemplateFactory(tabelle);
    
    return result;
  }
}
