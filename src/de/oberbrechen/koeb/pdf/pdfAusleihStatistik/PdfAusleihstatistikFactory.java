package de.oberbrechen.koeb.pdf.pdfAusleihStatistik;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfigurationAusleihzeitraumTagesDaten;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfigurationDaten;
import de.oberbrechen.koeb.datenbankzugriff.AusleiheFactory;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

/**
 * Erstellt ein PdfDokument, das eine Liste aller aktuellen Mahnungen
 * enthaelt.
 */
public class PdfAusleihstatistikFactory extends PdfTemplateDokumentFactory {  
  
  private static SimpleDateFormat monatDateFormat = new SimpleDateFormat("MMMM yyyy");    

  protected int jahr;
  protected int monat;
  protected AuswahlKonfiguration statistik;
  
  public PdfAusleihstatistikFactory(int jahr, AuswahlKonfiguration statistik) {
    this(-1, jahr, statistik);
  }

  public PdfAusleihstatistikFactory(int monat, int jahr, AuswahlKonfiguration statistik) {
    this.monat = monat;
    this.jahr = jahr;
    this.statistik = statistik;
  }
  
  private static String getMonatName(int monat, int jahr) {
    Calendar monatKalender = Calendar.getInstance();
    monatKalender.set(jahr, monat-1, 1);
    return monatDateFormat.format(monatKalender.getTime());      
  }

  private static String getTitel(int monat, int jahr) {
    if (monat > 0) {
      return "Ausleihstatistik "+getMonatName(monat, jahr);      
    } else {
      return "Ausleihstatistik "+jahr;
    }        
  }

  public PdfDokument createPdfDokument() throws Exception {
    String titel = getTitel(monat, jahr);
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(
      "Ausleihstatistik "+titel);
    result.addHauptUeberschrift(titel, null, null);
    
    AusleihStatistikTabellenModell[] statistiken = initStatistiken(monat, jahr, statistik);
    if (monat > 0) {
      AusleihStatistikTabellenModell tabellenModell = statistiken[monat];
      
      if (tabellenModell.getZeilenAnzahl() > 0) {
        PdfTabelle tabelle = new PdfTabelle(tabellenModell);
        result.addPdfTemplateFactory(tabelle);        
      }    
    } else {
      //Gesamtsumme
      TabellenModell tabellenModell = statistiken[0];
      PdfTabelle tabelle = new PdfTabelle(tabellenModell);
      tabelle.setAbstandFactor(2);
      result.addPdfTemplateFactory(tabelle);        
      
      for (int m=1; m < 13; m++) {
        tabellenModell = statistiken[m];
        
        if (tabellenModell.getZeilenAnzahl() > 1) {
          result.addMittlereUeberschrift(getMonatName(m, jahr), null, null);
          tabelle = new PdfTabelle(tabellenModell);
          tabelle.setAbstandFactor(2);
          result.addPdfTemplateFactory(tabelle);        
        }
      }  
    }            

    return result;    
  }

  
  private AusleihStatistikTabellenModell[] initStatistiken(int monat, int jahr, 
      AuswahlKonfiguration statistik) throws DatenbankInkonsistenzException {

    //lade passende Ausleihzeitraeume    
    AusleiheFactory ausleiheFactory =
      Datenbank.getInstance().getAusleiheFactory();
    Zeitraum zeitraum = monat > 0?new Zeitraum(monat, jahr):new Zeitraum(jahr);
    AusleihzeitraumListe liste =
      ausleiheFactory.getGetaetigteAusleihzeitraeumeInZeitraum(zeitraum);

    AuswahlKonfigurationDaten<Ausleihzeitraum> daten = statistik.bewerte(liste);
    daten.ueberpruefeChecks();
    
    AuswahlKonfigurationAusleihzeitraumTagesDaten tagesDaten =
      new AuswahlKonfigurationAusleihzeitraumTagesDaten(daten);
       
    AusleihStatistikTabellenModell[] statistiken = new AusleihStatistikTabellenModell[13];
    if (monat > 0) {
      statistiken[monat] = new AusleihStatistikTabellenModell(tagesDaten, monat, jahr); 
    } else {
      for(int m=1; m <= 12; m++) {
        statistiken[m] = new AusleihStatistikTabellenModell(tagesDaten, m, jahr); 
      }      
      statistiken[0] = new AusleihStatistikTabellenModell(tagesDaten, jahr);
    }
    
    return statistiken;
  }

}

