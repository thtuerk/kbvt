package de.oberbrechen.koeb.gui.bestand;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.gui.MainReiter;

/**
 * Dieses Interface repräsentiert einen beliebigen Reiter, der
 * in Main eingebunden werden kann.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface BestandMainReiter extends MainReiter {

  /**
   * Teilt dem Reiter mit, dass vom Barcodescanner der EAN-Code des
   * übergebenen Mediums gelesen wurde.
   * @param medium das gelesene Medium
   */
  public void mediumEANGelesen(Medium medium);

  /**
   * Teilt dem Reiter mit, dass vom Barcodescanner der EAN-Code der
   * übergebenen ISBN gelesen wurde.
   * @param isbn die gelesene ISBN
   */
  public void ISBNGelesen(ISBN isbn);
}