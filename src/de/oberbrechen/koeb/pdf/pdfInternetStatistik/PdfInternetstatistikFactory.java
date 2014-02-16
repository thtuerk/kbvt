package de.oberbrechen.koeb.pdf.pdfInternetStatistik;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

/**
 * Erstellt ein PdfDokument, das eine Liste aller aktuellen Mahnungen
 * enthaelt.
 */
public class PdfInternetstatistikFactory extends PdfTemplateDokumentFactory {

  private static SimpleDateFormat monatDateFormat = new SimpleDateFormat("MMMM");
  
  int monat, jahr;
  boolean versteckeKostenloseFreigaben;
  
  public PdfInternetstatistikFactory(int jahr) {
    this(-1, jahr);
  }

  public PdfInternetstatistikFactory(int monat, int jahr){
    this.monat = monat;
    this.jahr = jahr;
    this.versteckeKostenloseFreigaben = true;
  }
    
  

  public PdfDokument createPdfDokument() throws Exception {
    String titel = getTitel(monat, jahr);
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(titel);
    result.addHauptUeberschrift(titel, null, null);
    
    if (monat > 0) {
      addMonatsTemplateFactories(monat, jahr, result);
    } else {
      addJahresTemplateFactories(jahr, result);          
      
      for (int i=1; i <= 12; i++) {
        addMonatsTemplateFactories(i, jahr, result);
      }
    }
    
    return result;
  }

  private static String getTitel(int monat, int jahr) {
    String titel;
    if (monat > 0) {
      Calendar monatKalender = Calendar.getInstance();
      monatKalender.set(jahr, monat-1, 1);
      titel = "Internetstatistik "+monatDateFormat.format(monatKalender.getTime())+" "+jahr;      
    } else {
      titel = "Internetstatistik "+jahr;
    }
    return titel;
  }

  /**
   * Bestimmt, ob Internetfreigaben, für die keine Kosten erhoben werden,
   * versteckt werden sollen.
   */
  public void setVersteckeKostenloseFreigaben(boolean verstecke) {
    this.versteckeKostenloseFreigaben = verstecke;
  }
    
  private void addMonatsTemplateFactories(int monat, int jahr, PdfTemplateDokument result) throws Exception {
    
    InternetStatistikMonatTabellenModell tabellenModell = 
      new InternetStatistikMonatTabellenModell(monat, jahr, versteckeKostenloseFreigaben); 
    if (tabellenModell.getZeilenAnzahl() > 1) {
      result.addUnterUeberschrift(
          getMonatsNamen(monat)+" "+jahr, null, null);
            
      PdfTabelle tabelle = new PdfTabelle(tabellenModell);
      result.addPdfTemplateFactory(tabelle);
    }        
  }
  
  private void addJahresTemplateFactories(int jahr, PdfTemplateDokument result) 
    throws Exception {

    InternetStatistikJahrTabellenModell tabellenModell = 
      new InternetStatistikJahrTabellenModell(jahr, versteckeKostenloseFreigaben); 
    result.addMittlereUeberschrift("Jahresstatistik", null, null);
      
    PdfTabelle tabelle = new PdfTabelle(tabellenModell);
    result.addPdfTemplateFactory(tabelle);
    result.addAbstandRelativ(2);
  }

  private String getMonatsNamen(int monat) {
    SimpleDateFormat monatDateFormat = new SimpleDateFormat("MMMM");
    Calendar monatKalender = Calendar.getInstance();
    monatKalender.set(2002, monat-1, 1);
    return monatDateFormat.format(monatKalender.getTime());    
  }

}
