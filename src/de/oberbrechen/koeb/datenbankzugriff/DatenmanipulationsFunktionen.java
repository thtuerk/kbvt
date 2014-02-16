package de.oberbrechen.koeb.datenbankzugriff;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese Klasse stellt einfache Funktionen bereit, die in vielen
 * Implementierungen von Datenbankzugriff benötigt werden. Bspw.
 * die Konvertierung zwischen java.sql.Date und java.util.Date.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class DatenmanipulationsFunktionen {

  private static final long milliSekundenProTag = 1000*60*60*24;

  private static SimpleDateFormat datumsFormatierer = new
    SimpleDateFormat("dd.MM.yyyy");

  /**
   * Konvertiert ein java.util.Date-Objekt in ein
   * java.sql.Date-Objekt.
   * @param date das zu konvertierende Date
   * @return das sql-Date
   */
  public static java.sql.Date utilDate2sqlDate(java.util.Date date) {
    if (date == null) return null;
    return new java.sql.Date(date.getTime());
  }
  
  /**
   * Konvertiert ein java.util.Date-Objekt in ein
   * java.sql.Timestamp-Objekt.
   * @param date das zu konvertierende Date
   * @return das sql-Timestamp
   */
  public static java.sql.Timestamp utilDate2sqlTimestamp(java.util.Date date) {
    if (date == null) return null;
    return new java.sql.Timestamp(date.getTime());
  }
  
  /**
   * Liefert die Anzahl der Tage, die die übergebene Anzahl in Millisekunden
   * entspricht. Dabei wird jeweils abgerundet.
   */
  public static int milliSekunden2Tage(long millis) {
    return (int) Math.floor(
      ((double) millis) / milliSekundenProTag);    
  }

  /**
   * Liefert die Anzahl der Tage, die zwischen dem erten und dem zweiten 
   * übergebenen Datum liegen.
   */
  public static int getDifferenzTage(java.util.Date beginn, java.util.Date ende) {
    return milliSekunden2Tage(ende.getTime()-beginn.getTime());
  }

  /**
   * Formatiert den übergebenen String. Es werden führende und
   * beendende Leerzeichen entfernt und ein leerer String in eine
   * <code>null</code>-Referenz konvertiert.
   */
  public static String formatString(String string) {
    if (string == null) return null;
    
    String erg = string.trim();
    if (erg.length() == 0) return null;
    return erg;
  }
  
  public static String entferneNull(String string) {
    if (string == null) return "";
    return string;
  }

  public static String ersetzeNull(String string, String ersatz) {
    if (string == null) return ersatz;
    return string;
  }

  /**
   * Formatiert das übergebene Datum im Format 'dd.MM.yyyy'. 
   * Wird <code>null</code> übergeben, liefert die Methode '-'.
   * @param date das zu formatierende Datum
   * @return das formatierte Datum
   */
  public static String formatDate(Date date) {
    if (date == null) return "-";    
    return datumsFormatierer.format(date);
  }  
  
  /**
   * Clont ein Datum. D.h. wenn eine null-Referenz übergeben wird,
   * wird null zurückgeliefert, ansonsten wird die Methode clone des Date-Objekts
   * aufgerufen und das Ergebnis zurückgeliefert.
   */
  public static Date cloneDate(Date date) {
    if (date == null) return null;
    return (Date) date.clone();
  }

  /**
   * Bestimmt, ob die beiden übergebenen Zeitpunkte höchstens 24 Stunden 
   * auseinanderliegen.
   */
  public static boolean entsprichtGleichemDatum(Date date, Date date2) {
    long milliSekundenDifferenz =  date.getTime() - date2.getTime(); 

    return (milliSekundenDifferenz < milliSekundenProTag &&
            milliSekundenDifferenz > -milliSekundenProTag);
  }

  /**
   * Bestimmt, ob die der übergebenen Zeitpunkte höchstens 24 Stunden 
   * vom jetzigen abweicht. Wird null übergeben, so lautet das Ergebnis
   * false
   */
  public static boolean entsprichtHeutigemDatum(Date date) {
    if (date == null) return false;
    
    long milliSekundenDifferenz =  date.getTime() - System.currentTimeMillis(); 

    return (milliSekundenDifferenz < milliSekundenProTag &&
            milliSekundenDifferenz > -milliSekundenProTag);
  }
  
  
  /**
   * Extrahiert den ersten Namen aus einer uebergebenen Liste. Die Liste sollte die Form
   * "Vorname1 Vorname1a Nachname1, Vorname2 Nachname2, ..." haben.
   * Der gelieferte Name ist dann "Nachname1, Vorname1 Vorname1a"
   */
  public static String formatiereNamenListe(String namenListe) {
    namenListe = formatString(namenListe);
    if (namenListe == null) return null;
    String[] autoren = namenListe.split(",");  
    String[] namesteile = autoren[0].split(" ");
    if (namesteile.length == 1) {
      return namesteile[0]; 
    }
    
    StringBuffer sb = new StringBuffer(namesteile[namesteile.length-1]+",");
    for (int i = 0; i < namesteile.length-1; i++) {
      if (namesteile[i].length() > 0) {
         sb.append(" ");
         sb.append(namesteile[i]);
      }
    }
    return sb.toString();
 }
  
}