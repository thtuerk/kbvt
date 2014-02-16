package de.oberbrechen.koeb.ausgaben;


/**
* Dieses Interface stellt eine Factory dar, die
* Benutzerausgaben erstellt. 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface BenutzerAusgabeFactory  {

  /**
   * Erstellt eine Benutzerausgabe
   * @return die erzeugte Benutzerausgabe
   */
  public BenutzerAusgabe createBenutzerAusgabe();
}