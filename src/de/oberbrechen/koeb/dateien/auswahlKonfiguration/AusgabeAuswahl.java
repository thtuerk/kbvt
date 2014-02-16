package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;

/**
 * Diese Klasse repräsentiert eine Ausgabe, die auf einer
 * Auswahl basiert. Dies ist eine Auswahl mit zusätzlichen Informationen. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AusgabeAuswahl {
    
  String titel;
  int level;
  Auswahl auswahl;
  
  public AusgabeAuswahl(String titel, int level, Auswahl auswahl) {
    this.titel = titel;
    this.level = level;
    this.auswahl = auswahl;
  }
  
  /**
   * Liefert die Auswahl
   * @return die Auswahl
   */
  protected Auswahl getAuswahl() {
    return auswahl;
  }

  /**
   * Liefert den Level der Ausgabe. Dabei handelt es sich um
   * einen speziellen Parameter, der für verschiedene 
   * Zwecke genutzt werden kann, je nachdem in welchem Zusammenhang
   * die Ausgabe ausgewertet wird.
   * @return den Level
   */
  public int getLevel() {
    return level;
  }
  
  /**
   * Liefert den Titel der Ausgabe
   * @return den Titel
   */
  public String getTitel() {
    return titel;
  }
  
  public String toString() {
    return toDebugString();
  }
  
  public String toDebugString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(titel+" ("+level+")\n");
    buffer.append(auswahl.toDebugString());
    return buffer.toString();
  }
  
  public boolean istAusleihzeitraumAuswahl() {
    return auswahl.istAusleihzeitraumAuswahl();
  }

  public boolean istMediumAuswahl() {
    return auswahl.istMediumAuswahl();
  }
  
  public boolean istBenutzerAuswahl() {
    return auswahl.istBenutzerAuswahl();
  }
  
  public MedienListe bewerte(MedienListe eingabe) {
    if (!istMediumAuswahl()) 
      throw new IllegalArgumentException("Ausgabe '"+getTitel()+"' ist keine "+
          "Medien-Ausgabe!");
    
    return getAuswahl().bewerte(eingabe);
  }
  
  /**
   * Bestimmt, ob das übergebene Medium in der Ausgabe enthalten ist.
   * @param medium
   * @return ob das übergebene Medium in der Ausgabe enthalten ist
   */
  public boolean bewerte(Medium medium) {
    AuswahlObject auswahlObject = new AuswahlObject();
    auswahlObject.setDaten(medium);
    return getAuswahl().istInAuswahl(auswahlObject);
  }

  /**
   * Bestimmt, ob der übergebene Benutzer in der Ausgabe enthalten ist.
   * @param benutzer
   * @return ob der übergebene Benutzer in der Ausgabe enthalten ist
   */
  public boolean bewerte(Benutzer benutzer) {
    AuswahlObject auswahlObject = new AuswahlObject();
    auswahlObject.setDaten(benutzer);
    return getAuswahl().istInAuswahl(auswahlObject);
  }  
}
