package de.oberbrechen.koeb.gui.components.jTableTools;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Diese Klasse ist ein erweiterter TableCellRenderer, der über
 * ein Modell die einfache Festlegung der Farbe, Schrift und Ausrichtung
 * der darzustellenden Daten erlaubt.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class TableHeaderCellRenderer implements TableCellRenderer {
  
  static protected TableCellRenderer renderer;
    
  protected static Font schriftNormal;
  protected static Font schriftKursiv;
  
  static {   
    //Schriften
    Font bisherigeFont = new DefaultTableCellRenderer().getFont();
    schriftNormal = bisherigeFont.deriveFont(Font.PLAIN);
    schriftKursiv = bisherigeFont.deriveFont(Font.ITALIC);
    
    //CellRenderer
    renderer = new JTableHeader().getDefaultRenderer();
  }
    
  protected SortierbaresTableModel model;
      
  public TableHeaderCellRenderer(SortierbaresTableModel model) {
    this.model = model;    
  }      

  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int columnView) {

    int column = table.convertColumnIndexToModel(columnView);   
    if (model.istSortierbarNachSpalte(column)) {
      ((Component) renderer).setFont(schriftKursiv);
    } else {
      ((Component) renderer).setFont(schriftNormal);      
    }
    
    return (Component) renderer;
  }
}