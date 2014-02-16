package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;

/**
 * Diese Klasse repräsentiert eine AuswahlKonfiguration.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AuswahlKonfiguration {
  
   AusgabeAuswahl[] ausgaben= null;
   CheckAuswahl[] checks = null;
   
   //Cache
   AuswahlObject cacheObject = null;
   Hashtable<Auswahl, Boolean> cacheHash = new Hashtable<Auswahl, Boolean>();
   
   @SuppressWarnings("unchecked")
  public AuswahlKonfiguration(Reader reader) {
       AuswahlKonfigurationParser parser =
         new AuswahlKonfigurationParser(reader);
      parser.parseWithExceptions();
      
      init((AusgabeAuswahl[]) 
        parser.ausgaben.toArray(new AusgabeAuswahl[parser.ausgaben.size()]),
        (CheckAuswahl[]) 
        parser.checks.toArray(new CheckAuswahl[parser.checks.size()]));
   }

   public AuswahlKonfiguration(AusgabeAuswahl[] ausgaben, 
       CheckAuswahl[] checks) {
     init(ausgaben, checks);
   }
   
   private void init(AusgabeAuswahl[] ausgaben, 
       CheckAuswahl[] checks) {
     
     this.ausgaben = ausgaben;
     this.checks = checks;
          
   }
   
   public AuswahlKonfiguration(File file) throws UnsupportedEncodingException, FileNotFoundException {
      this(new InputStreamReader(new FileInputStream(file), "UTF-8"));
   }

   public AuswahlKonfiguration(String file) throws UnsupportedEncodingException, FileNotFoundException {
      this(new InputStreamReader(new FileInputStream(file), "UTF-8"));
   }      
   
   /**
    * Liefert die eingelesene Ausgabe mit der entsprechenden Nummer.
    * Die Nummerierung beginnt bei 0.
    * @param die Nummer
    * @return die Ausgabe
    */
   public AusgabeAuswahl getAusgabe(int i) {
     return ausgaben[i];
   }

   /**
    * Liefert die Anzahl der enthaltenen Ausgaben.
    * @return die Anzahl der Checks
    */
   public int getAusgabeAnzahl() {
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
   public int getCheckAnzahl() {
     return checks.length;
   }

   protected boolean istInAuswahl(AuswahlObject o, Auswahl a) {
     if (o != cacheObject) {
       cacheObject = o;
       cacheHash.clear();
     }
     
     Boolean erg = cacheHash.get(a);
     if (erg != null) return erg.booleanValue();
     
     boolean ergBoolean = a.istInAuswahl(o, this);
     cacheHash.put(a, new Boolean(ergBoolean));
     
     return ergBoolean;
   }
      
   /**
    * Bewertet die Übergebene Liste
    * @param liste
    * @return
    */
   public AuswahlKonfigurationDaten<Ausleihzeitraum> bewerte(AusleihzeitraumListe liste) {
     //richtiger Typ?
     for (int i = 0; i < ausgaben.length;i++) {
       if (!ausgaben[i].getAuswahl().istAusleihzeitraumAuswahl())
         throw new IllegalArgumentException("Ausgabe "+ausgaben[i].getTitel()+
             " ist keine Ausleihzeitraum-Ausgabe!");
     }
     for (int i = 0; i < checks.length;i++) {
       if (!checks[i].getAuswahl().istAusleihzeitraumAuswahl())
         throw new IllegalArgumentException("Check "+checks[i].getTitel()+
         " ist kein Ausleihzeitraum-Check!");
     }
     
     //Speicher initialisieren     
     AusleihzeitraumListe[] ausgabenResult = 
       new AusleihzeitraumListe[ausgaben.length];
     for (int i = 0; i < ausgaben.length;i++) 
       ausgabenResult[i] = new AusleihzeitraumListe();
     
     AusleihzeitraumListe[] checksResult = 
       new AusleihzeitraumListe[checks.length]; 
     for (int i = 0; i < checks.length;i++) 
       checksResult[i] = new AusleihzeitraumListe();
       
     //eigentliche Bewertung
     for (Ausleihzeitraum zeitraum : liste) {
       AuswahlObject eintrag = new AuswahlObject();
       eintrag.setDaten(zeitraum);

       for (int i = 0; i < ausgaben.length;i++) {
         if (ausgaben[i].getAuswahl().istInAuswahl(eintrag, this))
           ausgabenResult[i].addNoDuplicate(zeitraum);
       }
       for (int i = 0; i < checks.length;i++) {
         if (!checks[i].getAuswahl().istInAuswahl(eintrag, this))
           checksResult[i].addNoDuplicate(zeitraum);
       }              
     }

     //Ergebnis
     return new AuswahlKonfigurationDaten<Ausleihzeitraum>(ausgaben, checks, 
         ausgabenResult, checksResult);
   }

   /**
    * Bewertet die Übergebene Liste
    * @param liste
    * @return
    */
   public AuswahlKonfigurationDaten<Medium> bewerte(MedienListe liste) {
     //richtiger Typ?
     for (int i = 0; i < ausgaben.length;i++) {
       if (!ausgaben[i].getAuswahl().istMediumAuswahl())
         throw new IllegalArgumentException("Ausgabe "+ausgaben[i].getTitel()+
         " ist keine Medien-Ausgabe!");
     }
     for (int i = 0; i < checks.length;i++) {
       if (!checks[i].getAuswahl().istMediumAuswahl())
         throw new IllegalArgumentException("Check "+checks[i].getTitel()+
         " ist kein Medien-Check!");
     }
     
     //Speicher initialisieren     
     MedienListe[] ausgabenResult = 
       new MedienListe[ausgaben.length];
     for (int i = 0; i < ausgaben.length;i++) 
       ausgabenResult[i] = new MedienListe();
     
     MedienListe[] checksResult = 
       new MedienListe[checks.length]; 
     for (int i = 0; i < checks.length;i++) 
       checksResult[i] = new MedienListe();
     
     //eigentliche Bewertung
     for (Medium medium : liste) {
       AuswahlObject eintrag = new AuswahlObject();
       eintrag.setDaten(medium);

       for (int i = 0; i < ausgaben.length;i++) {
         if (ausgaben[i].getAuswahl().istInAuswahl(eintrag, this))
           ausgabenResult[i].addNoDuplicate(medium);
       }
       for (int i = 0; i < checks.length;i++) {
         if (!checks[i].getAuswahl().istInAuswahl(eintrag, this))
           checksResult[i].addNoDuplicate(medium);
       }              
     }

     //Ergebnis
     return new AuswahlKonfigurationDaten<Medium>(ausgaben, checks, 
         ausgabenResult, checksResult);
   }   
}
