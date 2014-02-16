package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn versucht wird, auf eine
* Einstellung falsch zuzugreifen, d.h. z.B. auf eine Einstellung
* als Zahl, die nicht so interpretiert werden kann.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class UnpassendeEinstellungException extends DatenbankzugriffException {

  public UnpassendeEinstellungException(String s) {
    super(s);
  }

}
