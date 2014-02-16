package de.oberbrechen.koeb.ausgaben;

import java.util.Collection;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;

/**
* Dieses Interface repräsentiert eine Ausgabe für Benutzer.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public interface BenutzerAusgabe extends Ausgabe{

  /**
   * Setzt die von der Ausgabe anzuzeigenden Benutzer.
   * @param daten die neuen Daten
   */
  public void setDaten(Collection<? extends Benutzer> daten);
  
  /**
   * Setzt die von der Ausgabe zu verwendenden Titel für die Benutzerliste.
   * @param titel der neue Titel
   */
  public void setTitel(String titel);
    
  /**
   * Setzt die von der Ausgabe zu verwendende Sortierung.
   * @param sortierung Konstante aus BenutzerListe, die die Sortierung bestimmt
   * @param umgekehrteSortierung bestimmt, ob die Sortierung umgekehrt werden soll
   */
  public void setSortierung(int Sortierung, boolean umgekehrteSortierung);
}