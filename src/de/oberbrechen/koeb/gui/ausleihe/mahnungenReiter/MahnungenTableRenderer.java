package de.oberbrechen.koeb.gui.ausleihe.mahnungenReiter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Diese Klasse ist ein TableCellRenderer für eine Liste von
 * Ausleihen. Er ist eng mit dem AusleiheTableModel verbunden und darf
 * nur zusammen mit diesem eingesetzt werden, d.h. jede Tabelle, die diesen
 * Renderer benutzt muss ein AusleiheTableModel benutzen.
 */
public class MahnungenTableRenderer implements TableCellRenderer {

  private DefaultTableCellRenderer renderer;

  private static final Color standardForeground = new Color(0,0,0);
  private static final Color standardBackground = new Color(0,0,0,30);

  public MahnungenTableRenderer() {    
    renderer = new DefaultTableCellRenderer();
    renderer.setOpaque(true);
    renderer.setFont(renderer.getFont().deriveFont(Font.PLAIN));
  }

  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column) {

    if (value == null) {
      renderer.setText("-");
    } else {
      renderer.setText(value.toString());
    }

    renderer.setForeground(standardForeground);
    renderer.setBackground(standardBackground);

    if (!isSelected) renderer.setBackground(Color.white);

    int realColumn = table.convertColumnIndexToModel(column);
    if (realColumn < 3) {
      renderer.setHorizontalAlignment(JLabel.LEFT);
    } else {
      renderer.setHorizontalAlignment(JLabel.RIGHT);
    }
    return renderer;
  }


}