package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse kapselt Daten, die aus der Auswertung 
 * einer AuswahlKonfiguration stammen.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AuswahlKonfigurationDaten<T> {
  
   AusgabeAuswahl[] ausgaben= null;
   CheckAuswahl[] checks = null;
   Liste<T>[] ausgabenResult;
   Liste<T>[] checksResult;
   
   protected AuswahlKonfigurationDaten(AusgabeAuswahl[] ausgaben, 
       CheckAuswahl[] checks, Liste<T>[] ausgabenResult, Liste<T>[] checksResult) {
     this.ausgaben = ausgaben;
     this.checks = checks;
     this.ausgabenResult = ausgabenResult;
     this.checksResult = checksResult;
   }   
   
   protected AuswahlKonfigurationDaten(AuswahlKonfigurationDaten<T> daten) {
     this.ausgaben = daten.ausgaben;
     this.checks = daten.checks;
     this.ausgabenResult = daten.ausgabenResult;
     this.checksResult = daten.checksResult;
   }   

   /**
    * Liefert die Ausgabe mit der entsprechenden Nummer.
    * Die Nummerierung beginnt bei 0.
    * @param die Nummer
    * @return die Ausgabe
    */
   public AusgabeAuswahl getAusgabe(int i) {
     return ausgaben[i];
   }

   /**
    * Liefert die Treffer für die Ausgabe mit der entsprechenden Nummer,
    * d.h. die Objekte, die der Ausgabe entsprechen.
    * Die Nummerierung beginnt bei 0.
    * @param die Nummer
    * @return die Treffer für die Ausgabe
    */
   public Liste<T> getAusgabeTreffer(int i) {
     return ausgabenResult[i];
   }

   /**
    * Liefert die Treffer für den Check mit der entsprechenden Nummer,
    * d.h. die Objekte, die den Check verletzen.
    * Die Nummerierung beginnt bei 0.
    * @param die Nummer
    * @return die Treffer für die Ausgabe
    */
   public Liste<T> getCheckTreffer(int i) {
     return checksResult[i];
   }

   /**
    * Liefert die Anzahl der enthaltenen Ausgaben.
    * @return die Anzahl der Ausgaben
    */
   public int getAusgabenAnzahl() {
     return ausgaben.length;
   }

   /**
    * Liefert den eingelesenen Check mit der entsprechenden Nummer.
    * Die Nummerierung beginnt bei 0.
    * @param die Nummer
    * @return den Check
    */
   public CheckAuswahl getCheck(int i) {
     return checks[i];
   }

   /**
    * Liefert die Anzahl der enthaltenen Checks.
    * @return die Anzahl der Checks
    */
   public int getChecksAnzahl() {
     return checks.length;
   }
   
   /**
    * Überprüft, ob alle Checks erfüllt sind.
    * Ist dies nicht der Fall, wird eine Warnung über den
    * ErrorHandler ausgegeben.
    * 
    * @return ob alle Checks erfüllt sind
    */
   public boolean ueberpruefeChecks() {
     boolean checksVerletzt = false;
     StringBuffer error = new StringBuffer();

     for (int i=0; i < getChecksAnzahl(); i++) {
       Liste<T> liste = getCheckTreffer(i);
       
       if (liste.size() > 0) {
         CheckAuswahl currentCheck = getCheck(i);
         checksVerletzt = true;
         error.append("Check '");
         error.append(currentCheck.getTitel());
         error.append("' wird von verletzt durch:\n");
         for (int j=0; j < liste.size(); j++) {
           Object eintrag = liste.get(j);
           error.append(eintrag.toString());
           error.append("\n");
         }
         error.append("\n");
       }
     }
     if (checksVerletzt) {
       ErrorHandler.getInstance().handleError(error.toString(), false);
     }
     
     return checksVerletzt;
   }
}
