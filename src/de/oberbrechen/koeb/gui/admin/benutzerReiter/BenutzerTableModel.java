package de.oberbrechen.koeb.gui.admin.benutzerReiter;

import java.util.Calendar;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;
import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManagerListenDaten;

/**
 * Diese Klasse ist eine Tabellenmodell für eine Tabelle 
 * von Benutzer für das Setzen des VIP-Attributes.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class BenutzerTableModel extends AbstractListeTableModel<Benutzer>
  implements ListenKeySelectionManagerListenDaten {

  public void refresh() {
    BenutzerFactory benutzerFactory =
      Datenbank.getInstance().getBenutzerFactory();
    daten = benutzerFactory.getAlleBenutzer();
    daten.setSortierung(BenutzerListe.NachnameVornameSortierung);
    fireTableDataChanged();
  }
  
  public int getColumnCount() {
    return 5;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "aktiv";
    if (columnIndex == 1) return "VIP";
    if (columnIndex == 2) return "Name";
    if (columnIndex == 3) return "Adresse";
    if (columnIndex == 4) return "Ort";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0 || columnIndex == 1) return Boolean.class;
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Benutzer gewaehlterBenutzer = (Benutzer) get(rowIndex);
    
    switch (columnIndex) { 
      case 0:
        return new Boolean(gewaehlterBenutzer.istAktiv());
      case 1:
        return new Boolean(gewaehlterBenutzer.istVIP());
      case 2: 
        return gewaehlterBenutzer.getNameFormal();
      case 3:
        return gewaehlterBenutzer.getAdresse();
      case 4:
        return gewaehlterBenutzer.getOrt();
    }

    return "nicht definierte Spalte";
  }
  
  public int getDefaultColumnWidth(int column) {
    switch (column) { 
      case 0: return 30;
      case 1: return 30;
      case 2: return 200;
      case 3: return 200;
      case 4: return 250;
    }
    
    return 100;
  }
      
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return (columnIndex == 0) || (columnIndex == 1);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex == 0) {      
      try {
        Benutzer gewaehlterBenutzer = (Benutzer) get(rowIndex);
        gewaehlterBenutzer.setAktiv(((Boolean) aValue).booleanValue());
        gewaehlterBenutzer.save();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, "Aktiv-Status konnte " +
            "nicht gespeichert werden!", false);
      }
    }
    
    if (columnIndex == 1) {      
      try {
        Benutzer gewaehlterBenutzer = (Benutzer) get(rowIndex);
        gewaehlterBenutzer.setVIP(((Boolean) aValue).booleanValue());
        gewaehlterBenutzer.save();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, "VIP-Status konnte " +
            "nicht gespeichert werden!", false);
      }
    }    
  }
  
  public String getKeySelectionValue(int row) {
    return getValueAt(row, 2).toString();
  }

  public int size() {
    return getRowCount();
  }

  public boolean zeigeRahmen() {
    return true;
  }

  /**
   * Markiert alle Benutzer, die in den letzten 18 Monaten nichts
   * ausgeliehen haben oder an einer Veranstaltung teilgenommen
   * haben als passiv.
   */
  public void passiveBenutzerMarkieren() {    
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -18);
    Zeitraum zeitraum = new Zeitraum(calendar.getTime(), new Date());
    
    try {
      final BenutzerListe benutzerListe = 
        Datenbank.getInstance().getBenutzerFactory().getAktiveBenutzerInZeitraum(zeitraum);
      benutzerListe.setSortierung(BenutzerListe.BenutzernrSortierung);
      
      Runnable saveRunnable = new Runnable() {
        public void run() {
          for (int i=0; i < daten.size(); i++) {
            try {
              Benutzer aktuellerBenutzer = (Benutzer) daten.get(i);
              aktuellerBenutzer.setAktiv(benutzerListe.contains(aktuellerBenutzer));
              aktuellerBenutzer.save();
            } catch (DatenbankzugriffException e) {
              ErrorHandler.getInstance().handleException(e, false);
            }
          }
        }
      };
    
      Datenbank.getInstance().executeInTransaktion(saveRunnable);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    
    this.fireTableDataChanged();
  }
}
