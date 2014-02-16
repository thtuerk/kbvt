package de.oberbrechen.koeb.dateien.ausgabenKonfiguration;

/**
* Diese Exception wird geworfen, wenn beim Erzeugen einer AusgabenFactory
* ein Problem auftritt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class AusgabenFactoryErzeugungsException extends Exception {

  public AusgabenFactoryErzeugungsException(String s) {
    super(s);
  }

}
