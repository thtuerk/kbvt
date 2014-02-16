package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.*;

/**
* Diese Exception wird geworfen, wenn versucht wird, 
* ein Medium zu
* speichern, dessen EAN schon verwendet wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class EANSchonVergebenException extends 
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue EANSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Medium, 
   * der den eindeutigen Schlüssel schon verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktMedium das Medium, das die EAN schon verwendet
   */
  public EANSchonVergebenException(String meldung,
    Medium konfliktMedium) {

    super(meldung, konfliktMedium);
  }

  /**
   * Liefert das Medium, das die Mediennr. bereits verwendet.
   * @return das Medium, das die Mediennr. bereits verwendet
   */
  public Medium getKonfliktMedium() {
    return (Medium) super.getKonfliktDatensatz();
  }

}
