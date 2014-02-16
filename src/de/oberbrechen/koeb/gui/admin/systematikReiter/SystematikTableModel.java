package de.oberbrechen.koeb.gui.admin.systematikReiter;

import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class SystematikTableModel extends AbstractListeSortierbaresTableModel<Systematik> {
  
  /**
   * Erstellt ein neues Modell ohne Daten, das nach dem Sollrückgabedatum
   * der Ausleihen sortiert ist
   */
  public SystematikTableModel() {
    initDaten(new SystematikListe());
  }

  public int getColumnCount() {
    return 3;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Systematik";
    if (columnIndex == 1) return "Beschreibung";
    if (columnIndex == 2) return "Obersystematik";
    return "nicht definierte Spalte";
  }

  public int getDefaultColumnWidth(int column) {
    switch (column) { 
      case 0: return 40;
      case 1: return 350;
      case 2: return 40;
    }
    
    return 100;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.size()) return null;
    Systematik gewaehlteSystematik = (Systematik) daten.get(rowIndex);
    if (columnIndex == 0) return gewaehlteSystematik.getName();
    if (columnIndex == 1) return gewaehlteSystematik.getBeschreibung();
    if (columnIndex == 2) {
      try {
        Systematik systematik = gewaehlteSystematik.getDirekteObersystematik();
        return systematik==null?"-":systematik.getName();    
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, false);
        return "FEHLER!";
      }
    }
    
    return "nicht definierte Spalte";
  }

  public int getFont(int rowIndex, int columnIndex) {
    try {
      Systematik systematik = (Systematik) get(rowIndex);
      if (systematik.getDirekteObersystematik() == null) {
        return SCHRIFT_FETT;
      } else {
        return SCHRIFT_STANDARD;
      }
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      return SCHRIFT_STANDARD;
    }
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    if (spalte == 0) {
      daten.setSortierung(SystematikListe.alphabetischeSortierung, false);
    }
  }
  
  public boolean istSortierbarNachSpalte(int spalte) {
    return (spalte == 0);
  }

  public void sortiereNachStandardSortierung() {
    daten.setSortierung(SystematikListe.alphabetischeSortierung);
  }

  public boolean zeigeSortierung() {
    return false;
  }

}
