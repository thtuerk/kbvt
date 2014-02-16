package de.oberbrechen.koeb.ausgaben;

import de.oberbrechen.koeb.ausgaben.Ausgabe;

/**
* Dieses Interface repräsentiert Knoten im AusgabenTreeModel.
* Es kapselt die wichtigen Methoden des eigentlichen Knotens.
* Als wichtig werden dabei nur Methoden erachtet, die neue Knoten
* hinzufügen. Ein Löschen von Knoten oder Zugriff auf Informationen
* wird dagegen verweigert!
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface AusgabenTreeKnoten {

  /**
   * Fügt eine Ausgabe zum Knoten hinzu
   * @param ausgabe die hinzufügende Ausgabe
   */
  public void addAusgabe(Ausgabe ausgabe);

  /**
   * Fügt eine Ausgabe zum Knoten hinzu
   * @param name der Name des neuen Knotens
   * @param ausgabe die hinzufügende Ausgabe
   */
  public void addAusgabe(String name, Ausgabe ausgabe);

  /**
   * Fügt einen neuen Unterknoten ein
   * @param name der Name des neuen Knotens
   * @param sortiert bestimmt, ob die Ausgaben und Ordner des Unterknotens
   *  alphabetisch sortiert werden sollen  
   * @return den neuen Unterknoten
   */
  public AusgabenTreeKnoten addKnoten(String name, boolean sortiert);
  
  /**
   * Liefert einen Unterknoten des übergebenen Knotens mit dem übergebenen
   * Namen. Existiert ein solcher Ordner nicht, wird <code>null</code>
   * zurückgeliefert.
   * @param name der Name des zu suchenden Knotens
   * @return den gefundenen Unterknoten oder <code>null</code>
   */
  public AusgabenTreeKnoten getKnoten(String name);
}