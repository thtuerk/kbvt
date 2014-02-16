package de.oberbrechen.koeb.einstellungen;

/**
* Eine Factory-Klasse für KonfigurierbareBuecherei.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class KonfigurierbareBuechereiFactory implements BuechereiFactory {

  public Buecherei createBuecherei() {
    return new KonfigurierbareBuecherei();
  }
}