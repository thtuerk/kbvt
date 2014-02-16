package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn versucht wird, eine
* Obersystematik OS einer Systematik S zu setzen, wobei OS
* eine Untersystematik von S ist.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class UnpassendeObersystematikException extends DatenbankzugriffException {

  public UnpassendeObersystematikException(String s) {
    super(s);
  }

}
