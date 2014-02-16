package de.oberbrechen.koeb.einstellungen;

/**
* Ein Factory-Interface für Ausleihordnungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface AusleihordnungFactory {

  /**
   * Erstellt eine neue Ausleihordnung
   * @return die neue Ausleihordnung
   */
  public Ausleihordnung createAusleihordnung();
}