package de.oberbrechen.koeb.datenstrukturen;

/**
* Diese Exception wird geworfen, wenn eine Inkonsistenz bei einem Zeitraum
* bemerkt wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class ZeitraumInkonsistenzException extends Exception {

  public ZeitraumInkonsistenzException(String s) {
    super(s);
  }

}
