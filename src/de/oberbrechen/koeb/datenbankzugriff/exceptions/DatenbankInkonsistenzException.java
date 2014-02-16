package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn eine Inkonsistenz in der Datenbank
* bemerkt wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class DatenbankInkonsistenzException extends DatenbankzugriffException {

  public DatenbankInkonsistenzException(String s) {
    super(s);
  }

}
