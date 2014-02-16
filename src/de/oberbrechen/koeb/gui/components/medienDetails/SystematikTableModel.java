package de.oberbrechen.koeb.gui.components.medienDetails;

import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class SystematikTableModel extends AbstractListeTableModel<Systematik> {

  /**
   * Erstellt ein neues Modell ohne Daten, das nach dem Sollrückgabedatum
   * der Ausleihen sortiert ist
   */
  public SystematikTableModel() {    
    daten = new SystematikListe();
    daten.setSortierung(SystematikListe.alphabetischeSortierung);
  }
  
  public int getColumnCount() {
    return 2;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Systematik";
    if (columnIndex == 1) return "Beschreibung";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.size()) return null;
    Systematik gewaehlteSystematik = daten.get(rowIndex);
    if (columnIndex == 0) return gewaehlteSystematik.getName();
    if (columnIndex == 1) return gewaehlteSystematik.getBeschreibung();

    return "nicht definierte Spalte";
  }
}
