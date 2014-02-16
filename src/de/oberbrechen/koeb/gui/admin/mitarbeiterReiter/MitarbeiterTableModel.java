package de.oberbrechen.koeb.gui.admin.mitarbeiterReiter;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.MitarbeiterFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von Einstellungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MitarbeiterTableModel extends AbstractListeTableModel<Mitarbeiter> {

  public MitarbeiterTableModel() {
    refresh();
  }
  
  public void refresh() {
    MitarbeiterFactory mitarbeiterFactory =
      Datenbank.getInstance().getMitarbeiterFactory();
    daten = mitarbeiterFactory.getAlleMitarbeiter();
    daten.setSortierung(MitarbeiterListe.NachnameVornameSortierung);
    fireTableDataChanged();
  }
      
  public int getColumnCount() {
    return 6;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "ID";
    if (columnIndex == 1) return "Name";
    if (columnIndex == 2) return "Aktiv";
    if (columnIndex == 3) return "Bestand - Berechtigung";
    if (columnIndex == 4) return "Veranstaltungen - Berechtigung";
    if (columnIndex == 5) return "Administration - Berechtigung";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return Integer.class;
    if (columnIndex == 1) return String.class;
    if (columnIndex == 2) return Boolean.class;
    if (columnIndex == 3) return Boolean.class;
    if (columnIndex == 4) return Boolean.class;
    if (columnIndex == 5) return Boolean.class;
    
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Mitarbeiter gewaehlterMitarbeiter = (Mitarbeiter) get(rowIndex);
    
    if (columnIndex == 0) return new Integer(gewaehlterMitarbeiter.
      getId());
    if (columnIndex == 1) 
      return gewaehlterMitarbeiter.getBenutzer().getNameFormal();
    if (columnIndex == 2) 
      return new Boolean(gewaehlterMitarbeiter.istAktiv());
    if (columnIndex == 3) 
      return new Boolean(gewaehlterMitarbeiter.besitztBerechtigung(
          Mitarbeiter.BERECHTIGUNG_BESTAND_EINGABE));
    if (columnIndex == 4) 
      return new Boolean(gewaehlterMitarbeiter.besitztBerechtigung(
        Mitarbeiter.BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE));
    if (columnIndex == 5) 
      return new Boolean(gewaehlterMitarbeiter.besitztBerechtigung(
        Mitarbeiter.BERECHTIGUNG_ADMIN));
    return "nicht definierte Spalte";
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex > 1;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Mitarbeiter gewaehlterMitarbeiter = (Mitarbeiter) get(rowIndex);
    boolean value = ((Boolean) aValue).booleanValue();
    
    if (columnIndex == 2) 
      gewaehlterMitarbeiter.setAktiv(value);
    if (columnIndex == 3) 
      gewaehlterMitarbeiter.setBerechtigung(
          Mitarbeiter.BERECHTIGUNG_BESTAND_EINGABE, value);
    if (columnIndex == 4) 
      gewaehlterMitarbeiter.setBerechtigung(
        Mitarbeiter.BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE, value);
    if (columnIndex == 5) 
      gewaehlterMitarbeiter.setBerechtigung(
        Mitarbeiter.BERECHTIGUNG_ADMIN, value);
        
    try {
      gewaehlterMitarbeiter.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Mitarbeiterberechtigungen " +
          "konnten nicht gespeichert werden!", false);
    }
    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  public int getAlignment(int rowIndex, int columnIndex) {
    if (columnIndex < 2) return AUSRICHTUNG_LINKS;
    return AUSRICHTUNG_ZENTRIERT;
  }

  public int getDefaultColumnWidth(int columnIndex) {
    if (columnIndex == 0) return 100;
    if (columnIndex == 1) return 300;
    return 100;
  }

  public boolean zeigeRahmen() {
    return true;
  }
}
