package de.oberbrechen.koeb.gui.veranstaltungen.veranstaltungenReiter;

import java.util.Observable;
import java.util.Observer;

import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;
import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManagerListenDaten;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von Medien.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class VeranstaltungenTableModel extends AbstractListeTableModel<Veranstaltung> 
  implements Observer, ListenKeySelectionManagerListenDaten {
  
  public VeranstaltungenTableModel() {    
    VeranstaltungenListe neueDaten = new VeranstaltungenListe();
    neueDaten.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);
    neueDaten.addObserver(this);
    
    initDaten(neueDaten);
  }
  
  public int getColumnCount() {
    return 3;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Titel";
    if (columnIndex == 1) return "Kurz-Titel";
    if (columnIndex == 2) return "Ansprechpartner";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.size()) return null;
    Veranstaltung gewaehlteVeranstaltung = (Veranstaltung) daten.get(rowIndex);
    if (columnIndex == 0) return gewaehlteVeranstaltung.getTitel();
    if (columnIndex == 1) return gewaehlteVeranstaltung.getKurzTitel();
    if (columnIndex == 2) return gewaehlteVeranstaltung.getAnsprechpartner();
    return "nicht definierte Spalte";
  }

  public void update(Observable o, Object arg) {
    this.fireTableDataChanged();
  }


  public String getKeySelectionValue(int row) {
    return getValueAt(row, 0).toString();
  }

  public int size() {
    return getRowCount();
  }
}
