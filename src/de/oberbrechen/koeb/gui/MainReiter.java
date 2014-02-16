package de.oberbrechen.koeb.gui;

import javax.swing.JMenu;

/**
 * Dieses Interface repräsentiert einen beliebigen Reiter, der
 * in Main eingebunden werden kann.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface MainReiter {

  /**
   * Aktualisiert den Reiter, d.h. alle Daten werden erneut aus der Datenbank
   * geladen und die Anzeige aktualisiert.
   */
  public void aktualisiere();

  /**
   * Teilt dem Reiter mit, dass er jetzt angezeigt wird, und seine Daten
   * aktualisieren soll. Im Gegensatz zu aktualisiere sollen dafür nicht
   * alle Daten erneut aus der Datenbank gelesen werden.
   */
  public void refresh();
  
  /**
   * Teilt dem Reiter mit, dass er jetzt nicht mehr angezeigt wird, und 
   * abschließende Aktionen ausführen soll.
   */
  public void focusLost();

  /**
   * Liefert ein Menue, dass nur für diesen Reiter angezeigt werden soll
   * @return
   */
  public JMenu getMenu();
}