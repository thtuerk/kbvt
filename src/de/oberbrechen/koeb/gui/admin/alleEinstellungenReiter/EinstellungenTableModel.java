package de.oberbrechen.koeb.gui.admin.alleEinstellungenReiter;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.EinstellungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von Einstellungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class EinstellungenTableModel extends AbstractListeTableModel<Einstellung> {

  public EinstellungenTableModel() {
    EinstellungenListe liste = new EinstellungenListe();
    liste.setSortierung(EinstellungenListe.AlphabetischeSortierung);
    initDaten(liste);
  }
  
  public void refresh() {
    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();
    setDaten(einstellungFactory.getAlleEinstellungen());
  }
  
  public int getColumnCount() {
    return 5;
  }

  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
      case 0: return "ID";
      case 1: return "Client";
      case 2: return "Mitarbeiter";
      case 3: return "Name";
      case 4: return "Wert";
    }

    return "nicht definierte Spalte";
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 50;
      case 1: return 50;
      case 2: return 50;
      case 3: return 750;
      case 4: return 500;
    }

    return 500;
  }
  
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Einstellung gewaehlteEinstellung = (Einstellung) get(rowIndex);
    
    switch (columnIndex) {
      case 0: 
        return Integer.toString(gewaehlteEinstellung.getId());        
      case 1: 
        if (gewaehlteEinstellung.getClient() != null) {
          return gewaehlteEinstellung.getClient().getName();
        } else {
          return "-";
        } 
      case 2: 
        if (gewaehlteEinstellung.getMitarbeiter() != null) {
          return gewaehlteEinstellung.getMitarbeiter().getBenutzer().getName();
        } else {
          return "-";
        }         
      case 3:
        return gewaehlteEinstellung.getName();
      case 4: 
        return gewaehlteEinstellung.getWert();
    }
    
    return "nicht definierte Spalte";
  }
    
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return (columnIndex == 4);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex != 4) return;
    
    Einstellung gewaehlteEinstellung = (Einstellung) get(rowIndex);
    gewaehlteEinstellung.setWert(aValue.toString());
    try {
      gewaehlteEinstellung.save();      
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }    
  }

  public boolean zeigeRahmen() {
    return true;
  }
}
