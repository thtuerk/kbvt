package de.oberbrechen.koeb.einstellungen;

/**
* Ein Factory-Interface für Buecherei.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface BuechereiFactory {

  /**
   * Erstellt eine neue Buecherei
   * @return die neue Buecherei
   */
  public Buecherei createBuecherei();
}