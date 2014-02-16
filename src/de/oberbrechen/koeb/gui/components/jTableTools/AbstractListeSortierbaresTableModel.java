package de.oberbrechen.koeb.gui.components.jTableTools;

import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManagerListenDaten;

/**
 * Diese Klasse ist eine abstrakte Oberklasse für
 * Tabellenmodelle, die ihre Daten als Liste abspeichern und
 * sortierbar sind.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractListeSortierbaresTableModel<T> 
  extends AbstractListeTableModel<T>
  implements SortierbaresTableModel, ListenKeySelectionManagerListenDaten {

  /**
   * Diese Variable gibt speichert, nach welcher Variable
   * die Spalten sortiert sind. Implementierungen der Methoden
   * sortiereNachSpalte und sortiereNachStandardSortierung müssen
   * den Wert updaten.
   */
  protected int sortierteSpalte;
  
  @SuppressWarnings("unchecked")
  public void initDaten(Liste<? extends T> daten) {
    this.daten = (Liste<T>) daten;
    sortiereNachStandardSortierung();
    fireTableDataChanged();
  }    
  
  public String getKeySelectionValue(int row) {
    Object value = getValueAt(row, getSortierteSpalte());
    return value==null?"":value.toString();
  }
  
  public void sortiereNachSpalte(int spalte, boolean umgekehrteSortierung) {    
    if (!istSortierbarNachSpalte(spalte)) return;
    
    try {
      sortiereNachSpalteIntern(spalte, umgekehrteSortierung);      
      sortierteSpalte = spalte;
      fireTableDataChanged();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Sortieren der Daten!", false);
      fireTableDataChanged();
    }
  }

  /**
   * Interne Umsetzung von sortiereNachSpalte. Gleiche Aufgabe und Funktion.
   */
  protected abstract void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung);
    
  public int getSortierteSpalte() {
    return sortierteSpalte;
  }
  
  public int size() {
    return getRowCount();
  }
  
  /**
   * Liefert den Spaltennamen der Spalte, nach der sortiert ist
   * @return
   */
  public String getSortierungSpalteName() {
    return getColumnName(getSortierteSpalte());
  }  
}
