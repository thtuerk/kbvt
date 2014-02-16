package de.oberbrechen.koeb.einstellungen;

/**
* Eine Factory-Klasse für die KonfigurierbareAusleihordnung
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class KonfigurierbareAusleihordnungFactory 
  implements AusleihordnungFactory {

  public Ausleihordnung createAusleihordnung() {
    return new KonfigurierbareAusleihordnung();
  }
}