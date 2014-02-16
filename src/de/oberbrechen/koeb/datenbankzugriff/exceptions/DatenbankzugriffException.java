package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn irgendein unerwartetes Ereignis
* beim Zugriff auf die Datenbank auftritt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class DatenbankzugriffException extends Exception {

  public DatenbankzugriffException(String s) {
    super(s);
  }

}
