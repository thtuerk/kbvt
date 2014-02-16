package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Diese Klasse repräsentiert einen Ausleihzeitraum einer Ausleihe.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Ausleihzeitraum extends Zeitraum {
  
  Mitarbeiter mitarbeiter;
  Date taetigungsdatum;
  Ausleihe ausleihe;
    
  public Ausleihzeitraum(Mitarbeiter mitarbeiter, Ausleihe ausleihe,
                         Date beginn, Date ende) {
    super(beginn, ende);
    this.mitarbeiter = mitarbeiter;
    this.ausleihe = ausleihe;
    this.taetigungsdatum = new Date();
  }
  
  public Ausleihzeitraum(Mitarbeiter mitarbeiter, Ausleihe ausleihe,
                         Date beginn, Date ende, Date taetigungsdatum) {
    this(mitarbeiter, ausleihe, beginn, ende);
    this.taetigungsdatum = taetigungsdatum;
  }

  /**
   * Liefert den Mitarbeiter, der das Medium herausgab
   * @return den Mitarbeiter, der das Medium herausgab
   */
  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }
  
  public Object clone() {
    return 
      new Ausleihzeitraum(mitarbeiter, ausleihe, beginn, ende, taetigungsdatum);
  }  

  /**
   * Setzt den Mitarbeiter, der das Medium herausgab
   * @param mitarbeiter der neue Mitarbeiter, der das Medium herausgab
   */
  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  /**
   * Liefert das Datum, an dem die Ausleihe für diesen Zeitraum getätigt wurde 
   * @return das Datum, an dem die Ausleihe für diesen Zeitraum getätigt wurde
   */
  public Date getTaetigungsdatum() {
    return taetigungsdatum;
  }

  /**
   * Setzt das Datum, an dem die Ausleihe für diesen Zeitraum getätigt wurde 
   * @param taetigungsdatum das Datum, an dem die Ausleihe für diesen 
   *   Zeitraum getätigt wurde
   */
  public void setTaetigungsdatum(Date taetigungsdatum) {
    this.taetigungsdatum = taetigungsdatum;
  }

  /**
   * Liefert die Ausleihe, zu der der Zeitraum gehört
   * @return die Ausleihe, zu der der Zeitraum gehört
   */
  public Ausleihe getAusleihe() {
    return ausleihe;
  }

  /**
   * Setzt die Ausleihe, zu der der Zeitraum gehört
   * @param ausleihe die Ausleihe, zu der der Zeitraum gehört
   */
  public void setAusleihe(Ausleihe ausleihe) {
    this.ausleihe = ausleihe;
  }
  
  
  /**
   * Bestimmt, ob der Ausleihzeitraum überzogen ist, d.h. 
   * das Ende vor dem Beginn des nächsten Zeitraums liegt.
   *
   * @return <code>true</code> gdw. die Ausleihe überzogen ist
   */
  public boolean istUeberzogen() {
    Ausleihzeitraum nextAusleihzeitraum =
      ausleihe.getNaechstenAusleihzeitraum(this);
    
    Date refDatum;
    if (nextAusleihzeitraum != null)
      refDatum = nextAusleihzeitraum.getBeginn();
    else
      refDatum = new Date();
    
    //Bei unvollständigen Daten nicht auswerten!
    if (this.getEnde() == null || refDatum == null) return false;
    
    return DatenmanipulationsFunktionen.getDifferenzTage(
        this.getEnde(), refDatum) > 0;
  }
  
  /**
   * Bestimmt, ob der Ausleihzeitraum überzogen ist, d.h. 
   * das Ende vor dem Beginn des nächsten Zeitraums liegt.
   *
   * @return <code>true</code> gdw. die Ausleihe überzogen ist
   */
  public boolean getUeberzogeTage() {
    Ausleihzeitraum nextAusleihzeitraum =
      ausleihe.getNaechstenAusleihzeitraum(this);
    
    Date refDatum;
    if (nextAusleihzeitraum != null)
      refDatum = nextAusleihzeitraum.getBeginn();
    else
      refDatum = new Date();
    
    //Bei unvollständigen Daten nicht auswerten!
    if (this.getEnde() == null || refDatum == null) return false;
    
    return DatenmanipulationsFunktionen.getDifferenzTage(
        this.getEnde(), refDatum) > 0;
  }
  
  public boolean equals(Object arg0) {
    if (! (arg0 instanceof Ausleihzeitraum))
      return false;
    
    Ausleihzeitraum z2 = (Ausleihzeitraum) arg0;
    
    if (ausleihe == null && z2.ausleihe != null) return false;
    if (ausleihe != null && !this.ausleihe.equals(z2.ausleihe)) return false;
    if (beginn == null && z2.beginn != null) return false;
    if (beginn != null && !beginn.equals(z2.beginn)) return false;
    if (ende == null && z2.ende != null) return false;
    if (ende != null && !ende.equals(z2.ende)) return false;
    
    return true;
  }
  
  public String toString() {
    return toDebugString();
  }

  public String toDebugString() {
    return "Ausleihe "+ausleihe.getId()+": "+
      getZeitraumFormat(ZEITRAUMFORMAT_KURZ);
  }
}