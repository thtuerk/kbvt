package de.oberbrechen.koeb.einstellungen;

/**
* Diese Exception wird geworfen, wenn versucht wird, eine Operation
* durchzuführen, die der Ausleihordnung widerspricht.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class WidersprichtAusleihordnungException extends Exception {

  public WidersprichtAusleihordnungException(String meldung)  {
    super(meldung);
  }

}
