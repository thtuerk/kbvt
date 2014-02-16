package de.oberbrechen.koeb.gui.admin.medientypReiter;

import javax.swing.JOptionPane;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.MedientypSchonVergebenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.MedientypListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von Medientypen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MedientypTableModel extends AbstractListeTableModel<Medientyp> {

  private Main hauptFenster;
  
  public MedientypTableModel(Main hauptFenster) {
    this.hauptFenster = hauptFenster;
    initDaten(new MedientypListe());
  }
  
  public void refresh() {
    MedientypFactory medientypFactory =
      Datenbank.getInstance().getMedientypFactory();
    MedientypListe neueDaten = medientypFactory.getAlleMedientypen();
    neueDaten.setSortierung(MedientypListe.StringSortierung);
    initDaten(neueDaten);
  }
      
  public int getColumnCount() {
    return 3;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "ID";
    if (columnIndex == 1) return "Name";
    if (columnIndex == 2) return "Plural";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return Integer.class;
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Medientyp gewaehlterMedientyp = (Medientyp) daten.get(rowIndex);
    
    if (columnIndex == 0) {
      return new Integer(gewaehlterMedientyp.getId());
    }
    if (columnIndex == 1) {
      return gewaehlterMedientyp.getName();
    }
    if (columnIndex == 2) {
      return gewaehlterMedientyp.getPlural();
    }
    
    return "nicht definierte Spalte";
  }
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex > 0;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    String oldValue = null;
    Medientyp medientyp = (Medientyp) get(rowIndex);
    try {      
      if (columnIndex == 1) {
        oldValue = medientyp.getName();
        medientyp.setName(aValue.toString());         
        medientyp.save();
      } else if (columnIndex == 2) {
        oldValue = medientyp.getPlural();
        medientyp.setPlural(aValue.toString());
        medientyp.save();          
      } else {
        throw new IndexOutOfBoundsException();
      }
      this.fireTableDataChanged();
      return;
    } catch (MedientypSchonVergebenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Medientyp schon vergeben!",
        JOptionPane.ERROR_MESSAGE);      
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);      
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Medientyp " +
          "konnte nicht gespeichert werden!", false);
    }

    if (columnIndex == 1) {
      medientyp.setName(oldValue);         
    } else if (columnIndex == 2) {
      medientyp.setPlural(oldValue);
    }
    this.fireTableDataChanged();
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 50;
      case 1: return 250;
      case 2: return 250;
    }
    return 500;
  }

  public boolean zeigeRahmen() {
    return true;
  }
}
