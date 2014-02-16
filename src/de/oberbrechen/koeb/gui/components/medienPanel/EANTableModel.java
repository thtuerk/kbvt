package de.oberbrechen.koeb.gui.components.medienPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.EANListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von EANs.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class EANTableModel extends AbstractListeTableModel<EAN> {

  private JFrame hauptFenster;
  private boolean showNewEAN;
  
  /**
   * Erstellt ein neues Modell ohne Daten
   */
  public EANTableModel(JFrame hauptFenster) {
    this.hauptFenster = hauptFenster;
    this.showNewEAN = false;
    daten = new EANListe();
    daten.setSortierung(EANListe.StringSortierung);
  }

  public int getRowCount() {
    if (showNewEAN) return daten.size()+1;
    return daten.size();
  }

  public int getColumnCount() {
    return 1;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "EAN";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex == daten.size()) return "...";
    
    EAN gewaehlteEAN = (EAN) daten.get(rowIndex);
    if (columnIndex == 0) return gewaehlteEAN.getEAN();

    return "nicht definierte Spalte";
  }
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  public void setValueAt(Object eanString, int rowIndex, int columnIndex) {
    EAN neueEAN = checkEAN((String) eanString);
    
    if (rowIndex < daten.size()) daten.remove(rowIndex);
    if (neueEAN != null) daten.add(neueEAN);
    
    fireTableDataChanged();
  }

  protected EAN checkEAN(String ean) {
    if (ean == null || ean.equals("")) return null;
    
    EAN neuEAN;
    try {
      neuEAN = new EAN(ean);
      return neuEAN;     
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(hauptFenster, "Die Eingabe '"+
          ean + "' kann\nnicht als EAN interpretiert\n"+
          "werden!",
          "Ungültige EAN!",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }    
  }
  
  /**
   * Setzt, ob ... angezeigt wird
   * @author thtuerk
   */
  public void showNewItem(boolean show) {
    this.showNewEAN = show;
    fireTableDataChanged();
  }
}
