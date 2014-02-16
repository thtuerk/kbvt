package de.oberbrechen.koeb.gui.components.listenKeySelectionManager;

/**
 * Über dieses Interface greift der KeySelectionManager auf die
 * Daten der Liste zu.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface ListenKeySelectionManagerListenDaten {

  /**
   * Liefert den Eintrag, der in der übergebenen Zeile der 
   * Liste steht.
   * @param row die Zeilennr
   * @return String den Eintrag
   */
  public String getKeySelectionValue(int row);

  /**
   * Liefert die Zeilenanzahl der Daten.
   * @return die Zeilenanzahl des Modells
   */
  public int size();

}
