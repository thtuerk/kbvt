package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn versucht wird, auf eine nicht existierende
* Daten zuzugreifen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class DatenNichtGefundenException extends DatenbankzugriffException {

  public DatenNichtGefundenException(String s) {
    super(s);
  }

}
