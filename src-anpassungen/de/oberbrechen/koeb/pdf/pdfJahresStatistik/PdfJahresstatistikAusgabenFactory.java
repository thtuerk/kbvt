package de.oberbrechen.koeb.pdf.pdfJahresStatistik;

import java.util.Calendar;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.*;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.PdfDokument;


/**
* Diese Klasse erstellt eine Jahresstatistik 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class PdfJahresstatistikAusgabenFactory implements AusgabenFactory {

  AuswahlKonfiguration ausleihStatistik = null;
  AuswahlKonfiguration bestandStatistik = null;
  
  public void setParameter(String name, String wert) throws ParameterException {
    if (name != null && name.equals("AusleihStatistik")) { 
      ausleihStatistik = parseAuswahlKonfiguration(wert);
    } else if (name != null && name.equals("BestandStatistik")) { 
      bestandStatistik = parseAuswahlKonfiguration(wert);
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }

  private AuswahlKonfiguration parseAuswahlKonfiguration(String file) throws ParameterException {
    try {
      return new AuswahlKonfiguration(file);
    } catch (Exception e) {
      throw ParameterException.parseFehler(file, e);
    }        
  }
  public void addToKnoten(AusgabenTreeKnoten knoten) throws Exception {
    if (ausleihStatistik == null)
      throw ParameterException.ParameterNichtGesetzt("AusleihStatistik");
    
    Zeitraum zeitraum = Datenbank.getInstance().getAusleiheFactory().getAusleihenZeitraum();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(zeitraum.getBeginn());
    int beginnJahr = calendar.get(Calendar.YEAR);
    calendar.setTime(zeitraum.getEnde());
    int endeJahr = calendar.get(Calendar.YEAR);
    
    for (int i=beginnJahr; i <= endeJahr; i++) {
      addToKnoten(knoten, i);    
    }
  }
  
  public void addToKnoten(AusgabenTreeKnoten knoten, final int jahr) throws Exception {
    knoten.addAusgabe(new PdfSpeicherbareAusgabe() {
      public String getName() {
        return "Jahresstatistik "+ jahr;
      }

      public String getBeschreibung() {
        return "Statistik über Ausleihen, Bestand, Veranstaltungen, etc. für das Jahr "+jahr+"!";
      }

      protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
        return new PdfJahresstatistikFactory(jahr, ausleihStatistik, bestandStatistik).
          createPdfDokument();
      }
      
      public boolean benoetigtGUI() {
        return false;
      }                  
    });
  }

  public String getName() {
    return "Jahresstatistik-Factory";
  }

  public String getBeschreibung() {
    return null;
  }
}
