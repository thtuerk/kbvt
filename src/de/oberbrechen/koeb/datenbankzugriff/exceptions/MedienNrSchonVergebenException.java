package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.Medium;

/**
* Diese Exception wird geworfen, wenn versucht wird, ein Medium zu
* speichern, dessen Mediennr. schon verwendet wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class MedienNrSchonVergebenException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue MedienNrSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Medium, der den
   * eindeutigen Schlüssel schon verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktBenutzer der Benutzer, der den Benutzernamen
   *   schon verwendet
   */
  public MedienNrSchonVergebenException(String meldung,
    Medium konfliktMedium) {

    super(meldung, konfliktMedium);
  }

  /**
   * Erstellt eine neue MedienNrSchonVergebenException mit
   * einer Standardfehlermeldung und dem übergebenen Medium, 
   * das den eindeutigen Schlüssel schon verwendet.
   *
   * @param konfliktMedium das Medium, das das Medium
   *   schon verwendet
   */
  public MedienNrSchonVergebenException(Medium konfliktMedium) {
   super("Die Medien-Nr. '"+konfliktMedium.getMedienNr()+"' wird "+
        "bereits verwendet.", konfliktMedium);
  }

  /**
   * Liefert das Medium, das die Mediennr. bereits verwendet.
   * @return das Medium, das die Mediennr. bereits verwendet
   */
  public Medium getKonfliktMedium() {
    return (Medium) super.getKonfliktDatensatz();
  }

}
