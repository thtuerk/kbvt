package de.oberbrechen.koeb.gui.components.medienDetails;

import de.oberbrechen.koeb.datenbankzugriff.Medium;


/**
 * Dieses Interface dient dazu, auf eine Änderung des angezeigten
 * Mediums in einem MedienDetailsPanel reagieren zu können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface MedienDetailsListener {
  
  /**
   * Diese Methode wird aufgerufen, wenn im MedienDetailsPanel
   * ein neues Medium ausgewählt wurde.
   * @param medium das neue Medium
   */
  public void neuesMediumAusgewaehlt(Medium medium);
  
}