package de.oberbrechen.koeb.ausgaben;

import java.util.Collection;

import de.oberbrechen.koeb.datenbankzugriff.Medium;

/**
* Dieses Interface repräsentiert eine Ausgabe für Medien.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public interface MedienAusgabe extends Ausgabe{

  /**
   * Setzt die von der Ausgabe anzuzeigenden Medien.
   * @param daten die neuen Daten
   */
  public void setDaten(Collection<? extends Medium> daten);
  
  /**
   * Setzt die von der Ausgabe zu verwendenden Titel für die Medienliste.
   * @param titel der neue Titel
   */
  public void setTitel(String titel);
    
  /**
   * Setzt die von der Ausgabe zu verwendende Sortierung.
   * @param sortierung Konstante aus Medienliste, die die Sortierung bestimmt
   * @param umgekehrteSortierung bestimmt, ob die Sortierung umgekehrt werden soll
   */
  public void setSortierung(int Sortierung, boolean umgekehrteSortierung);
}