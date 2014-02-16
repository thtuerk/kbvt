package de.oberbrechen.koeb.gui.components.jTableTools;

import javax.swing.table.TableModel;



/**
 * Ein Modell der Daten für einen Table-Renderer
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface SortierbaresTableModel extends TableModel {

  /**
   * Sortiert die Daten im Modell nach der übergebenen Spalte
   * @param spalte
   * @param umgekehrteSortierung
   */
  public void sortiereNachSpalte(int spalte, boolean umgekehrteSortierung);
  
  
  /**
   * Bestimmt, ob die Daten im Modell nach der übergebenen Spalte sortierbar sind
   * @param spalte
   */
  public boolean istSortierbarNachSpalte(int spalte);
  
  /**
   * Sortiert die Daten nach der Standardsortierung. Hierzu sollte die Methode
   * sortiereNachSpalte intern verwendet werden. Mittels dieser Methode kann
   * eine Standardsortierung vorgegeben werden.
   */
  public void sortiereNachStandardSortierung();    
  
  /**
   * Liefert die Nummer der Spalte, nach der die Daten sortiert sind
   * @return die Nummer der Spalte, nach der die Daten sortiert sind
   */
  public int getSortierteSpalte();
}