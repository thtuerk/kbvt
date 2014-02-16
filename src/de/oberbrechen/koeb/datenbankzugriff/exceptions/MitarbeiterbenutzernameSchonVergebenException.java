package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.*;

/**
* Diese Exception wird geworfen, wenn versucht wird, einen Mitarbeiter zu
* speichern, dessen Mitarbeiterbenutzername schon verwendet wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class MitarbeiterbenutzernameSchonVergebenException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue MitarbeiterbenutzernameSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Mitarbeiter, der den
   * Mitarbeiterbenutzernamen schon verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktMitarbeiter der Mitarbeiter, der den
   *   Mitarbeiterbenutzernamen schon verwendet
   */
  public MitarbeiterbenutzernameSchonVergebenException(String meldung,
    Mitarbeiter konfliktMitarbeiter) {

    super(meldung, konfliktMitarbeiter);
  }

  /**
   * Erstellt eine neue MitarbeiterbenutzernameSchonVergebenException mit
   * einer Standardfehlermeldung und dem übergebenen Mitarbeiter, der den
   * Benutzermitarbeiternamen schon verwendet.
   *
   * @param konfliktMitarbeiter der Mitarbeiter, der den
   *   Mitarbeiterbenutzernamen schon verwendet
   */
  public MitarbeiterbenutzernameSchonVergebenException(Mitarbeiter konfliktMitarbeiter) {
    super("Der Mitarbeiterbenutzername '"+
      konfliktMitarbeiter.getMitarbeiterBenutzername()+"' wird "+
      "bereits von "+konfliktMitarbeiter.getBenutzer().getName()+" verwendet.",
      konfliktMitarbeiter);
  }

  /**
   * Liefert den Mitarbeiter, der den Mitarbeiterbenutzernamen
   * bereits verwendet.
   * @return den Mitarbeiter, der den Mitarbeiterbenutzernamen bereits verwendet
   */
  public Mitarbeiter getKonfliktMitarbeiter() {
    return (Mitarbeiter) super.getKonfliktDatensatz();
  }

}
