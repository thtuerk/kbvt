package de.oberbrechen.koeb.dateien.einstellungenDoku;

import javax.swing.JFrame;

import de.oberbrechen.koeb.datenbankzugriff.Einstellung;

/**
 * Dieses Interface repräsentiert einen Test für Einstellungen,
 * der unter der grafischen Oberfläche ausgeführt wird.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface EinstellungTest {

  /**
   * Testet eine Einstellung. Dabei können Exceptions geworfen werden.
   * Falls nötig kann eine grafische Interaktion über den
   * übergebenen JFrame stattfinden.
   * @throws Exception
   */
  public void testeEinstellung(JFrame main, Einstellung einstellung) throws Exception;
}
