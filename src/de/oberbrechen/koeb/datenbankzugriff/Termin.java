package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.datenstrukturen.ZeitraumInkonsistenzException;


/**
 * Diese Klasse repräsentiert einen Termin einer Veranstaltung der Bücherei.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class Termin extends Zeitraum {

  /**
   * Erstellt einen neuen Termin.
   * @param beginn der Beginn
   * @param ende das Ende
   */
  public Termin(java.util.Date beginn, java.util.Date ende) {
    super(beginn, ende);
  }

  public Object clone() {
    return new Termin(beginn, ende);
  }

  public void check() throws ZeitraumInkonsistenzException  {
    if (beginn == null) { 
      throw new ZeitraumInkonsistenzException(
        "Ein Termin muss einen Beginn haben!");
    }  
    super.check();
  }
}