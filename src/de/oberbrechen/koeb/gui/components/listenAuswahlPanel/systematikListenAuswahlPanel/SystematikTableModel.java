package de.oberbrechen.koeb.gui.components.listenAuswahlPanel.systematikListenAuswahlPanel;

import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
 * Diese Klasse ist ein Tabellenmodell für eine Tabelle von Systematiken.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class SystematikTableModel extends AbstractListeSortierbaresTableModel<Systematik> {

  public SystematikTableModel(Liste<Systematik> daten) {
    initDaten(daten);
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
    Systematik gewaehlteSystematik = (Systematik) daten.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return gewaehlteSystematik.getName();
      case 1:
        return gewaehlteSystematik.getBeschreibung();
    }
    return "nicht definierte Spalte";
  }

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(0, false);
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    if (spalte == 0) { 
      daten.setSortierung(SystematikListe.alphabetischeSortierung, umgekehrteSortierung);
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return (spalte == 0);
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 50;
      case 1: return 500;
    }
    return 50;
  }

  public boolean zeigeSortierung() {
    return false;
  }

}
