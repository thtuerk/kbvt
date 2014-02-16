package de.oberbrechen.koeb.dateien.medienAusgabenKonfiguration;

/**
* Diese Exception wird geworfen, wenn beim Erzeugen einer MedienAusgabeFactory
* ein Problem auftritt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class MedienAusgabeFactoryErzeugungsException extends Exception {

  public MedienAusgabeFactoryErzeugungsException(String s) {
    super(s);
  }

}
