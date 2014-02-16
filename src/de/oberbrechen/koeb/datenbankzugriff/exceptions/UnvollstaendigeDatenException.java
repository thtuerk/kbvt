package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn versucht wird, Daten zu speichern, die
* nicht alle nötigen Informationen enthalten.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class UnvollstaendigeDatenException extends DatenbankzugriffException {

  public UnvollstaendigeDatenException(String s) {
    super(s);
  }

}
