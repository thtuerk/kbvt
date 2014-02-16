package de.oberbrechen.koeb;

import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.server.medienabfragen.Thalia;
import de.oberbrechen.koeb.server.medienabfragen.Medienabfrage;

/**
 * Dummy-Klasse für Tests.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Main {
  
  public static void main(String[] args) throws Exception {       
//    PdfTeilnehmerListeVeranstaltungsgruppe pdf =
//      new PdfTeilnehmerListeVeranstaltungsgruppe(
//          ((Veranstaltung) (Datenbank.getInstance().getVeranstaltungFactory().get(158))).getVeranstaltungsgruppe(),
//          false, true, true);
//    PdfVeranstaltungsUebersichtVeranstaltungsgruppe pdf = 
//      new PdfVeranstaltungsUebersichtVeranstaltungsgruppe(
//          ((Veranstaltung)
//      Datenbank.getInstance().getVeranstaltungFactory().get(158)).getVeranstaltungsgruppe(), true);
//    pdf.zeige(true);
    
//    PdfTeilnehmerListeVeranstaltung pdf = 
//      new PdfTeilnehmerListeVeranstaltung();
//    pdf.setVeranstaltung(
//          ((Veranstaltung)
//          Datenbank.getInstance().getVeranstaltungFactory().get(158)));

    Medienabfrage abfrage = Medienabfrage.getInstance();
    System.out.println (abfrage.isbnNachschlagenMitTimeout(new ISBN("3833006870")).toDebugString());
    System.out.println (abfrage.isbnNachschlagenMitTimeout(new ISBN("3830432003")).toDebugString());
  }
}
