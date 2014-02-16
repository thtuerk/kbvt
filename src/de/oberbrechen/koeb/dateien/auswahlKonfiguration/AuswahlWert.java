package de.oberbrechen.koeb.dateien.auswahlKonfiguration;


/**
 * Diese Klasse repräsentiert eine konkreten Wert, der in
 * einer Auswahl verwendet wird.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
class AuswahlWert {
    
  public static final int ZAHL = 1;
  public static final int STRING = 2;

  public static final int AUSLEIHE_BENUTZERALTER = 30;
  public static final int MEDIUM_EINSTELLUNGSDAUER_TAGE = 31;
  public static final int BENUTZER_ANMELDEDAUER_TAGE = 32;  
  public static final int BENUTZER_ANMELDEDAUER_JAHRE = 33;  
  public static final int BENUTZER_BENUTZERALTER = 34;    
  
  int typ;
  Object value;
  
  public AuswahlWert(int typ, Object value) {
    this.typ = typ;
    this.value= value;
  }
        
  public AuswahlWert(int typ) {
    this(typ, null);
  }
  
  protected int getWertTyp() {
    switch (typ) {
      case ZAHL:
      case STRING:
        return Auswahl.AUSWAHL_ALLE_TYPEN;
      case AUSLEIHE_BENUTZERALTER:
        return Auswahl.AUSWAHL_TYP_AUSLEIHZEITRAUM;
      case MEDIUM_EINSTELLUNGSDAUER_TAGE:
        return Auswahl.AUSWAHL_TYP_AUSLEIHZEITRAUM+Auswahl.AUSWAHL_TYP_MEDIUM;
      case BENUTZER_BENUTZERALTER:
      case BENUTZER_ANMELDEDAUER_JAHRE:
      case BENUTZER_ANMELDEDAUER_TAGE:
        return Auswahl.AUSWAHL_TYP_BENUTZER;
      default:
        throw new RuntimeException("Unbeachteter Fall "+typ);        
    }
  }
  
  public Object getValue(AuswahlObject o) {
    switch (typ) {
      case ZAHL:
      case STRING:
        return value;
      case AUSLEIHE_BENUTZERALTER:
        if (o.getBenutzer() == null || 
            o.getBenutzer().getGeburtsdatum() == null)
          return null;
        
        return new Double(o.getBenutzer().getAlter(o.getZeitraum().getBeginn()));
      case BENUTZER_BENUTZERALTER:
        if (o.getBenutzer() == null || 
            o.getBenutzer().getGeburtsdatum() == null)
          return null;
        
        return new Double(o.getBenutzer().getAlter());
      case BENUTZER_ANMELDEDAUER_TAGE:
        if (o.getBenutzer() == null || 
            o.getBenutzer().getAnmeldedatum() == null)
          return null;
          
        return new Double(o.getBenutzer().getAnmeldeDauer());
      case BENUTZER_ANMELDEDAUER_JAHRE:
        if (o.getBenutzer() == null || 
            o.getBenutzer().getAnmeldedatum() == null)
          return null;
        
        return new Double(o.getBenutzer().getAnmeldeDauer());
      case MEDIUM_EINSTELLUNGSDAUER_TAGE:
        if (o.getMedium() == null ||
            o.getMedium().getEinstellungsdatum() == null)
          return null;
        
        return new Double(o.getMedium().getEinstellungsdauerInTagen());
      default:
        throw new RuntimeException("Unbeachteter Fall "+typ);        
    }    
  }
}