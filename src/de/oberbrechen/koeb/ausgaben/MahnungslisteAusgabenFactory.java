package de.oberbrechen.koeb.ausgaben;

/**
* Dieses Interface stellt eine Factory dar, die
* die eine Ausgabe erstellt, die eine Liste aller 
* Mahnungen ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface MahnungslisteAusgabenFactory {

  /**
   * Erstellt eine Ausgabe, die eine Liste aller Mahnungen ausgibt.
   * @return die erzeugte Ausgabe
   */
  public Ausgabe createMahnungslisteAusgabe();
}