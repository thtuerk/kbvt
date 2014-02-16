package de.oberbrechen.koeb.pdf.pdfMahnungsListe;

import java.util.Vector;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.MahnungslisteAusgabenFactory;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.PdfDokument;

/**
* Diese Factory erstellt eine Ausgabe, die eine PDF-Liste aller 
* Mahnungen ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class PdfMahnungslisteAusgabenFactory implements 
  MahnungslisteAusgabenFactory, AusgabenFactory {

  private Vector<Integer> ueberziehungen;
   
  public PdfMahnungslisteAusgabenFactory() {
    ueberziehungen = new Vector<Integer>();
    ueberziehungen.add(new Integer(0));
  }

  private Ausgabe createMahnungslisteAusgabe(final int minUeberziehungInWochen) {
    String wochen = null;
    //if (minUeberziehungInWochen == 0) wochen = null;
    if (minUeberziehungInWochen == 1) wochen = "1 Woche";
    if (minUeberziehungInWochen > 1) wochen = minUeberziehungInWochen+" Wochen";
    
    final String titel;
    final String beschreibung;
    if (wochen == null) {
      titel = "Mahnungsliste";
      beschreibung = "Erstellt eine PDF-Liste aller aktuellen Mahnungen!";
    } else {
      titel = "Mahnungsliste - "+wochen;      
      beschreibung = "Erstellt eine PDF-Liste aller aktuellen " +        "Mahnungen, bei denen ein Medium mindestens "+wochen+" überzogen ist!";
    }

    return new PdfSpeicherbareAusgabe() {
      public java.lang.String getName() {
        return titel;
      }

      public java.lang.String getBeschreibung() {
        return beschreibung;
      }

      protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
        return new PdfMahnungsListeFactory(7*minUeberziehungInWochen, titel).
        createPdfDokument();  
      }      
            
      public boolean benoetigtGUI() {
        return false;
      }            
    };
  }
  
    
  public Ausgabe createMahnungslisteAusgabe() {
    return createMahnungslisteAusgabe(0);
  }

  public String getName() {
    return "PDF-Mahngungslisten";
  }

  public String getBeschreibung() {
    return null;
  }

  public void setParameter(String name, String wert) throws ParameterException {
    if (name != null && name.equals("MindestUeberziehungInWochen")) {    
      try {
        int intWert = Integer.parseInt(wert);
        ueberziehungen.add(new Integer(intWert));
      } catch (NumberFormatException e) {
        ErrorHandler.getInstance().handleException(e, "Dem Parameter 'MindestUeberziehungInWochen' " +
          "dürfen nur Integer-Werte zugewiesen werden. Der Wert '"+wert+"' kann aber nicht als Integer " +
          "interpretiert werden!", false);
      }
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) {
    for (int i=0; i < ueberziehungen.size(); i++) {
      knoten.addAusgabe(createMahnungslisteAusgabe(ueberziehungen.get(i).intValue()));
    }
  }
}