package de.oberbrechen.koeb.datenstrukturen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Diese Klasse repräsentiert einen Zeitraum. Beginn repräsentiert den
 * ersten Zeitpunkt der zum Zeitraum gehört, Ende den ersten Zeitpunkt,
 * der nicht mehr zum Zeitraum gehört.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class Zeitraum {

  static public final int ZEITRAUMFORMAT_LANG = 1;
  static public final int ZEITRAUMFORMAT_LANG_MIT_UHRZEIT = 2;
  static public final int ZEITRAUMFORMAT_KURZ = 3;
  static public final int ZEITRAUMFORMAT_KURZ_MIT_UHRZEIT = 4;
  static public final int ZEITRAUMFORMAT_MITTEL = 5;
  static public final int ZEITRAUMFORMAT_MITTEL_MIT_UHRZEIT = 6;
  static public final int ZEITRAUMFORMAT_UHRZEIT = 7;
  
  static final SimpleDateFormat datumsFormatLang = 
    new SimpleDateFormat("EE, d. MMMM yyyy");
  static final SimpleDateFormat datumsFormatMittel = 
    new SimpleDateFormat("EE, dd.MM.yyyy");
  static final SimpleDateFormat datumsFormatKurz = 
    new SimpleDateFormat("dd.MM.yyyy");
  static final SimpleDateFormat zeitFormat = 
    new SimpleDateFormat("H:mm");

  protected Date beginn, ende;

  /**
   * Erstellt einen neuen Zeitraum.
   * @param beginn der Beginn
   * @param ende das Ende
   */
  public Zeitraum(java.util.Date beginn, java.util.Date ende) {
    this.beginn = beginn;
    this.ende = ende;
  }
  
  /**
   * Erstellt einen neuen Zeitraum, der den übergebenen Monat des übergebenen
   * Jahres umfaßt.
   * @param monat
   * @param jahr
   */
  public Zeitraum(int monat, int jahr) {
     Calendar calendar = Calendar.getInstance();
     calendar.set(jahr, monat-1, 1,0, 0, 0);
     calendar.set(Calendar.MILLISECOND, 0);
     this.beginn = calendar.getTime();
    
     calendar.set(jahr, monat, 1);
     this.ende = calendar.getTime();        
  } 

  /**
   * Erstellt einen neuen Zeitraum, der das übergebene Jahr umfaßt.
   * @param jahr
   */
  public Zeitraum(int jahr) {
     Calendar calendar = Calendar.getInstance();
     calendar.set(jahr, 0, 1,0,0,0);
     calendar.set(Calendar.MILLISECOND, 0);
     this.beginn = calendar.getTime();
    
     calendar.set(jahr+1, 0, 1);
     this.ende = calendar.getTime();        
  } 

  static Date cloneDate(Date date) {
    if (date == null) return null;
    return (Date) date.clone();
  }  
  
  /**
   * Liefert den Beginn-Zeitpunkt des Zeitraums
   * @return den Beginn-Zeitpunkt des Zeitraums
   */
  public Date getBeginn() {
    return cloneDate(beginn);
  }

  /**
   * Liefert den End-Zeitpunkt des Zeitraums
   * @return den End-Zeitpunkt des Zeitraums
   */
  public Date getEnde() {
    return cloneDate(ende);
  }

  /**
   * Setzt den Beginn-Zeitpunkt des Zeitraums
   * @param beginn der neue Beginn-Zeitpunkt des Zeitraums
   */
  public void setBeginn(Date beginn) {
    this.beginn = cloneDate(beginn);
  }

  /**
   * Setzt den End-Zeitpunkt des Zeitraums
   * @param ende der neue End-Zeitpunkt des Zeitraums
   */
  public void setEnde(Date ende) {
    this.ende = cloneDate(ende);
  }

  /**
   * Prüft, ob der Zeitraum sinnvoll ist. Also ob bspw. der Beginn vor dem 
   * Ende liegt, ein Beginn gesetzt ist, etc. Tritt ein Problem auf, wird eine
   * ZeitraumInkosistenzException geworfen.
   */
  public void check() throws ZeitraumInkonsistenzException {
    if (beginn != null && ende != null && !beginn.before(ende)) { 
      throw new ZeitraumInkonsistenzException(
        "Der Beginn eines Zeitraums muss vor seinem Ende liegen!"); 
    }
  }
  
  /**
   * Liefert eine String-Repräsentation des Zeitraums.
   * Das Format kann über den Parameter bestimmt werden. Die
   * möglichen Formate sind als öffentliche Konstanten dieser Klasse
   * verfügbar.
   *
   * @return die String-Repräsentation
   */
  public String getZeitraumFormat(int format) {
    return getZeitraumFormat(beginn, ende, format);
  }
    
  /**
   * Liefert eine String-Repräsentation des Zeitraums vom
   * übergebenen Beginn bis zum übergebenen Ende.
   * Das Format kann über den Parameter bestimmt werden. Die
   * möglichen Formate sind als öffentliche Konstanten dieser Klasse
   * verfügbar.
   *
   * @return die String-Repräsentation
   */
  public static String getZeitraumFormat(Date beginn, Date ende, int format) {
    //Nur Uhrzeit
    if (format == ZEITRAUMFORMAT_UHRZEIT) {
      String erg = zeitFormat.format(beginn);
      if (ende != null) {
        erg += " - "+zeitFormat.format(ende)+ " Uhr";        
      }
      return erg;
    }
    
    //Datum + evt. Uhrzeit
    boolean zeigeUhrzeit = false;
    SimpleDateFormat datumsFormat = null;
    switch (format) {
      case ZEITRAUMFORMAT_KURZ_MIT_UHRZEIT:
        zeigeUhrzeit = true;
      case ZEITRAUMFORMAT_KURZ:
        datumsFormat = datumsFormatKurz;
        break;
      case ZEITRAUMFORMAT_MITTEL_MIT_UHRZEIT:
        zeigeUhrzeit = true;
      case ZEITRAUMFORMAT_MITTEL:
        datumsFormat = datumsFormatMittel;
        break;
      case ZEITRAUMFORMAT_LANG_MIT_UHRZEIT:
        zeigeUhrzeit = true;
      case ZEITRAUMFORMAT_LANG:
        datumsFormat = datumsFormatLang;
        break;
      default:
        throw new IllegalArgumentException("Ein Format mit der Nummer "+
          format + " ist unbekannt!");
    }
          
    if (beginn == null) return "-";

    String erg = datumsFormat.format(beginn);
    if (zeigeUhrzeit)
      erg += " "+zeitFormat.format(beginn)+" Uhr";

    if (ende == null) return erg;
    
    erg += " - ";
    if (datumsFormat.format(beginn).equals(datumsFormat.format(ende))) {
      if (zeigeUhrzeit)
        erg += zeitFormat.format(ende)+ " Uhr";
      else 
        erg += datumsFormat.format(ende);      
    } else {
      erg += datumsFormat.format(ende);            
      if (zeigeUhrzeit)
        erg += " "+zeitFormat.format(ende)+ " Uhr";
    }

    return erg;
  }

  public String toString() {
    return getZeitraumFormat(ZEITRAUMFORMAT_KURZ_MIT_UHRZEIT);
  }

  public Object clone() {
    return new Zeitraum(beginn, ende);
  }
  
  /**
   * Bestimmt, ob der übergebene Zeitpunkt im Zeitraum liegt.
   * @param zeitpunkt der zu testende Zeitpunkt
   * @return <code>true</code> gdw. der übergebene Zeitpunkt im Zeitraum liegt.
   */
  public boolean liegtIn(Date zeitpunkt) {
    return !zeitpunkt.before(getBeginn()) && zeitpunkt.before(getEnde()); 
  }
}