package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;

/**
 * Dieses Interface stellt eine Factory für Mahnungen dar.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface MahnungFactory {

  /**
   * Erstellt eine Mahnung für den übergebenen Benutzers zum aktuellen Zeitpunkt
   * @param benutzer der Benutzer, für den eine Mahnung erstellt werden soll
   */
  public Mahnung erstelleMahnungFuerBenutzer(Benutzer benutzer);

  /**
   * Liefert eine Liste aller Benutzer, die zum aktuellen Zeitpunkt
   * Medien überzogen haben / hatten.
   */
  public BenutzerListe getAlleBenutzerMitMahnung();
}