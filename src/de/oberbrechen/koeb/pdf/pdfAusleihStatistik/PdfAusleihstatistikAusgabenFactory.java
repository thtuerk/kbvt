package de.oberbrechen.koeb.pdf.pdfAusleihStatistik;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.PdfDokument;

/**
 * Diese Klasse erstellt eine Ausleihstatistik für einen bestimmten Monat
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfAusleihstatistikAusgabenFactory implements AusgabenFactory {

  SimpleDateFormat monatDateFormat = new SimpleDateFormat("MMMM");
  AuswahlKonfiguration statistik = null;
  
  public void setParameter(String name, String wert) throws ParameterException {
    if (name != null && name.equals("Statistik")) {
      try {
        statistik = new AuswahlKonfiguration(wert);
      } catch (Exception e) {
        throw ParameterException.parseFehler(wert, e);
      }    
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }

  public void addJahrToKnoten(AusgabenTreeKnoten knoten, final int jahr, final int startMonat, final int endMonat) throws Exception {
    knoten.addAusgabe(new PdfSpeicherbareAusgabe() {
      public String getName() {
        return "Ausleihstatistik "+jahr;
      }

      public String getBeschreibung() {
        return "Ausleihstatistik für das Jahr "+jahr+"!";
      }

      protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
        return new PdfAusleihstatistikFactory(jahr, statistik).createPdfDokument();
      }
            
      public boolean benoetigtGUI() {
        return false;
      }            
    });
    
    knoten = knoten.addKnoten("Ausleihstatistiken "+jahr, false);    

    for (int i=startMonat; i <= endMonat; i++) {
      Calendar monatKalender = Calendar.getInstance();
      monatKalender.set(jahr, i-1, 1);
      final String monat = monatDateFormat.format(monatKalender.getTime());
      final int monatNr = i;      

      knoten.addAusgabe(monat+" "+jahr, new PdfSpeicherbareAusgabe() {
        public String getName() {
          return "Ausleihstatistik "+monat+" "+jahr;
        }
  
        public String getBeschreibung() {
          return "Ausleihstatistik für "+monat + " "+jahr+"!";
        }
  
        protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
          return new PdfAusleihstatistikFactory(monatNr, jahr, statistik).
          createPdfDokument();
        }
        
        
        public boolean benoetigtGUI() {
          return false;
        }              
      });
    }       
  }

  public String getName() {
    return "Jahresstatistik-Factory";      
  }

  public String getBeschreibung() {
    return null;
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) throws Exception {
    Zeitraum zeitraum = Datenbank.getInstance().getAusleiheFactory().
      getAusleihenZeitraum();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(zeitraum.getBeginn());
    int beginnMonat = calendar.get(Calendar.MONTH)+1;
    int beginnJahr = calendar.get(Calendar.YEAR);

    calendar.setTime(zeitraum.getEnde());
    int endeMonat = calendar.get(Calendar.MONTH)+1;
    int endeJahr = calendar.get(Calendar.YEAR);

    
    if (beginnJahr == endeJahr) {
      addJahrToKnoten(knoten, beginnJahr, beginnMonat, endeMonat);      
    } else {
      addJahrToKnoten(knoten, beginnJahr, beginnMonat, 12);            
      for (int i=beginnJahr+1; i < endeJahr; i++) {
        addJahrToKnoten(knoten, i, 1, 12);
      }
      addJahrToKnoten(knoten, endeJahr, 1, endeMonat);            
    }
  }
}
