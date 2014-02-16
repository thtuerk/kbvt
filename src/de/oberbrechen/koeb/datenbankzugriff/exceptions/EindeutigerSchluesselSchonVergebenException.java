package de.oberbrechen.koeb.datenbankzugriff.exceptions;

/**
* Diese Exception wird geworfen, wenn versucht wird, einen Datensatz zu
* speichern, aber ein in diesem Datensatz benutzer eindeutiger Schlüssel schon
* verwendet wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class EindeutigerSchluesselSchonVergebenException extends
  DatenbankzugriffException {

  private static final long serialVersionUID = 1L;
  private Object konflikt;


  /**
   * Liefert den Datensatz, der den eindeutigen Schlüssel bereits verwendet.
   * @return den Datensatz, der den eindeutigen Schlüssel bereits verwendet
   */
  public Object getKonfliktDatensatz() {
    return konflikt;
  }

  /**
   * Erstellt eine neue EindeutigerSchluesselSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Datensatz, der den
   * eindeutigen Schlüssel schon verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktDatensatz der Datensatz, der den eindeutigen Schlüssel
   *   schon verwendet
   */
  public EindeutigerSchluesselSchonVergebenException
    (String meldung, Object konfliktDatensatz) {
    super(meldung);
    konflikt = konfliktDatensatz;
  }
}
