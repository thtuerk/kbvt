package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;

/**
* Diese Exception wird geworfen, wenn versucht wird, einen Benutzer zu
* speichern, dessen Benutzername schon verwendet wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class BenutzernameSchonVergebenException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue BenutzernameSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Benutzer, der den
   * eindeutigen Schlüssel schon verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktBenutzer der Benutzer, der den Benutzernamen
   *   schon verwendet
   */
  public BenutzernameSchonVergebenException(String meldung,
    Benutzer konfliktBenutzer) {

    super(meldung, konfliktBenutzer);
  }

  /**
   * Erstellt eine neue BenutzernameSchonVergebenException mit
   * einer Standardfehlermeldung und dem übergebenen Benutzer, der den
   * eindeutigen Schlüssel schon verwendet.
   *
   * @param konfliktBenutzer der Benutzer, der den Benutzernamen
   *   schon verwendet
   */
  public BenutzernameSchonVergebenException(Benutzer konfliktBenutzer) {
    super("Der Benutzername '"+konfliktBenutzer.getBenutzername()+"' wird "+
        "bereits von "+konfliktBenutzer.getName()+" verwendet.", konfliktBenutzer);
  }

  /**
   * Liefert den Benutzer, der den Benutzernamen bereits verwendet.
   * @return den Benutzer, der den Benutzernamen bereits verwendet
   */
  public Benutzer getKonfliktBenutzer() {
    return (Benutzer) super.getKonfliktDatensatz();
  }

}
