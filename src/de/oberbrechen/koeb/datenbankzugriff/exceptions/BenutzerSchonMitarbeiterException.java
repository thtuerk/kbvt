package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.*;

/**
* Diese Exception wird geworfen, wenn versucht wird, einen Mitarbeiter zu
* speichern, der schon unter einer anderen Mitarbeiternr als Mitarbeiter
* eingetragen ist.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class BenutzerSchonMitarbeiterException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue BenutzernameSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Mitarbeiter, der dem
   * zu speichernden Mitarbeiter entspricht.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktMitarbeiter der Mitarbeiter, der dem
   *   zu speichernden Mitarbeiter entspricht.
   */
  public BenutzerSchonMitarbeiterException(String meldung,
    Mitarbeiter konfliktMitarbeiter) {

    super(meldung, konfliktMitarbeiter);
  }

  /**
   * Erstellt eine neue MitarbeiterbenutzernameSchonVergebenException mit
   * einer Standardfehlermeldung und dem übergebenen Mitarbeiter, der dem
   * zu speichernden Mitarbeiter entspricht.
   *
   * @param konfliktMitarbeiter der Mitarbeiter, der dem
   *   zu speichernden Mitarbeiter entspricht.
   */
  public BenutzerSchonMitarbeiterException(Mitarbeiter konfliktMitarbeiter) {
    super("Der Benutzer '"+ konfliktMitarbeiter.getBenutzer().getName()+
      "' ist bereits unter der ID "+
      konfliktMitarbeiter.getId()+" als Mitarbeiter eingetragen.",
      konfliktMitarbeiter);
  }

  /**
   * Liefert den Mitarbeiter, der dem zu speichernden Mitarbeiter entspricht.
   * @return den Mitarbeiter, der dem zu speichernden Mitarbeiter entspricht.
   */
  public Mitarbeiter getKonfliktMitarbeiter() {
    return (Mitarbeiter) super.getKonfliktDatensatz();
  }

}
