package de.oberbrechen.koeb.gui.ausleihe;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.gui.MainReiter;

/**
 * Dieses Interface repräsentiert einen beliebigen Reiter, der
 * in Main eingebunden werden kann.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface AusleiheMainReiter extends MainReiter {

  /**
   * Teilt dem Reiter mit, dass sich der aktuelle Benutzer geändert hat.
   * @param benutzer der neue Benutzer
   */
  public void setBenutzer(Benutzer benutzer);

  /**
   * Teilt dem Reiter mit, dass vom Barcodescanner der EAN-Code des
   * übergebenen Mediums gelesen wurde.
   * @param medium das gelesene Medium
   */
  public void mediumEANGelesen(Medium medium);
}