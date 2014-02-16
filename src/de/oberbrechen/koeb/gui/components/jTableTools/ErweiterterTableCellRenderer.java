package de.oberbrechen.koeb.gui.components.jTableTools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.*;

import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManager;
import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManagerListenDaten;

/**
 * Diese Klasse ist ein erweiterter TableCellRenderer, der über
 * ein Modell die einfache Festlegung der Farbe, Schrift und Ausrichtung
 * der darzustellenden Daten erlaubt.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ErweiterterTableCellRenderer implements TableCellRenderer {
  
  static protected JCheckBox rendererCheckBox;
  static protected DefaultTableCellRenderer rendererLabel;
    
  static protected TableRendererModel standardRendererModel =
    new TableRendererModel() {

      public int getColor(int rowIndex, int columnIndex) {
        return FARBE_STANDARD;
      }
      
      public int getFont(int rowIndex, int columnIndex) {
        return SCHRIFT_STANDARD;
      }

      public int getAlignment(int rowIndex, int columnIndex) {
        return AUSRICHTUNG_LINKS;
      }

      public int getDefaultColumnWidth(int columnIndex) {
        return 50;
      }

      public boolean zeigeRahmen() {
        return true;
      }

      public boolean zeigeSortierung() {
        return true;
      }
  };
  
  protected static Color[] foregroundColor;
  protected static Color[] backgroundColor;
  protected static Color[] backgroundOrderedColor;
  protected static Color[] backgroundOrderedSelectedColor;
  protected static Border[] border;
    
  protected static Font[] font;
  
  static {   
    //Farben    
    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
    final int farbenAnzahl = 6;
    
    foregroundColor = new Color[farbenAnzahl];
    backgroundColor = new Color[farbenAnzahl];
    backgroundOrderedColor = new Color[farbenAnzahl];
    backgroundOrderedSelectedColor = new Color[farbenAnzahl];
    border = new Border[farbenAnzahl];
        
    backgroundColor[0] = defaults.getColor("Table.background");
    
    foregroundColor[TableRendererModel.FARBE_STANDARD] = new Color(0,0,0);
    backgroundColor[TableRendererModel.FARBE_STANDARD] = new Color(0,0,0,30);
    backgroundOrderedColor[TableRendererModel.FARBE_STANDARD] = new Color(0,0,0,50);
    backgroundOrderedSelectedColor[TableRendererModel.FARBE_STANDARD] = new Color(0,0,0,70);
    border[TableRendererModel.FARBE_STANDARD] = new LineBorder(new Color(0,0,0,150), 1);
    
    foregroundColor[TableRendererModel.FARBE_ROT] = new Color(255,0,0);
    backgroundColor[TableRendererModel.FARBE_ROT] = new Color(255,0,0,30);
    backgroundOrderedColor[TableRendererModel.FARBE_ROT] = new Color(255,0,0,50);
    backgroundOrderedSelectedColor[TableRendererModel.FARBE_ROT] = new Color(255,0,0,70);
    border[TableRendererModel.FARBE_ROT] = new LineBorder(new Color(255,0,0,150), 1);
    
    foregroundColor[TableRendererModel.FARBE_BLAU] = new Color(0, 0,255);
    backgroundColor[TableRendererModel.FARBE_BLAU] = new Color(0, 0,255,30);
    backgroundOrderedColor[TableRendererModel.FARBE_BLAU] = new Color(0,0,255,50);
    backgroundOrderedSelectedColor[TableRendererModel.FARBE_BLAU] = new Color(0,0,255,70);
    border[TableRendererModel.FARBE_BLAU] = new LineBorder(new Color(0, 0,255,150), 1);
    
    foregroundColor[TableRendererModel.FARBE_GRUEN] = new Color(0,128,0);
    backgroundColor[TableRendererModel.FARBE_GRUEN] = new Color(0,128,0,30);
    backgroundOrderedColor[TableRendererModel.FARBE_GRUEN] = new Color(0,128,0,50);
    backgroundOrderedSelectedColor[TableRendererModel.FARBE_GRUEN] = new Color(0,128,0,70);
    border[TableRendererModel.FARBE_GRUEN] = new LineBorder(new Color(0,128,0,150), 1);
    
    foregroundColor[TableRendererModel.FARBE_LILA] = new Color(145,0, 145);
    backgroundColor[TableRendererModel.FARBE_LILA] = new Color(145,0,145,30);
    backgroundOrderedColor[TableRendererModel.FARBE_LILA] = new Color(145,0,145,50);
    backgroundOrderedSelectedColor[TableRendererModel.FARBE_LILA] = new Color(145,0,145,70);
    border[TableRendererModel.FARBE_LILA] = new LineBorder(new Color(145,0,145,150), 1);    
    
    //Schriften
    font = new Font[4];
    Font bisherigeFont = new DefaultTableCellRenderer().getFont();
    font[TableRendererModel.SCHRIFT_STANDARD] = bisherigeFont.deriveFont(Font.PLAIN);
    font[TableRendererModel.SCHRIFT_FETT] = bisherigeFont.deriveFont(Font.BOLD);
    font[TableRendererModel.SCHRIFT_KURSIV] = bisherigeFont.deriveFont(Font.ITALIC);
    font[TableRendererModel.SCHRIFT_FETT_KURSIV] = bisherigeFont.deriveFont(Font.ITALIC+Font.BOLD);
    
    //CellRenderer
    rendererLabel = new DefaultTableCellRenderer();
    rendererLabel.setOpaque(true);
    
    rendererCheckBox = new JCheckBox();
    rendererCheckBox.setBorderPaintedFlat(true);
    rendererCheckBox.setBorderPainted(true);    
  }
    
  public static void setzeErweitertesModell(final TableModel model, final JTable table) {
    table.setModel(model);
    
    if (model instanceof TableRendererModel) {
      ErweiterterTableCellRenderer renderer = new ErweiterterTableCellRenderer(
          (TableRendererModel) model);
      table.setDefaultRenderer(Object.class, renderer);            
      table.setDefaultRenderer(String.class, renderer);            
      table.setDefaultRenderer(Boolean.class, renderer);            
      table.setDefaultRenderer(Integer.class, renderer);            
      
      TableColumnModel columnModel = table.getColumnModel();
      for (int i = 0; i < model.getColumnCount(); i++) 
        columnModel.getColumn(i).setPreferredWidth(
            ((TableRendererModel) model).getDefaultColumnWidth(i));      
    }

    if (model instanceof KeyListener)
      table.addKeyListener((KeyListener) model);
    
    if (model instanceof ListenKeySelectionManagerListenDaten) {
      KeyListener keyListener = new KeyListener() {        
        ListenKeySelectionManager keySelectionManager =
          new ListenKeySelectionManager((ListenKeySelectionManagerListenDaten) model);
        
        public void keyPressed(KeyEvent e) {
          char aKey = e.getKeyChar();
          int zuSelektierendeZeile = keySelectionManager.selectionForKey(aKey);
          if (zuSelektierendeZeile == -1) return;
          
          table.setRowSelectionInterval(zuSelektierendeZeile, zuSelektierendeZeile);
          table.scrollRectToVisible(table.getCellRect(zuSelektierendeZeile, 0, true));
        }

        public void keyReleased(KeyEvent arg0) {}
        public void keyTyped(KeyEvent arg0) {}
      };
      table.addKeyListener(keyListener);      
    }
    
    
    if (model instanceof SortierbaresTableModel) {
     // table.getTableHeader().setDefaultRenderer(new TableHeaderCellRenderer(
     //     (SortierbaresTableModel) model));
      table.getTableHeader().addMouseListener(new MouseAdapter() {        
        int sortierteSpalte = -1;
        boolean umgekehrteSortierung = false;
        
        public void mouseClicked(final MouseEvent e) {
          int spalteView = table.getColumnModel().getColumnIndexAtX(e.getX());
          int spalte = table.convertColumnIndexToModel(spalteView);

          if (((SortierbaresTableModel) model).istSortierbarNachSpalte(spalte)) { 
            umgekehrteSortierung = sortierteSpalte == spalte?!umgekehrteSortierung:false;          
            sortierteSpalte = spalte;
            
            ((SortierbaresTableModel) model).sortiereNachSpalte(spalte, umgekehrteSortierung);
          }
        }
      });              
    }
  }
  
  protected TableRendererModel model;
  protected boolean zeigeSortierteSpalte;
  
  public ErweiterterTableCellRenderer() {
    this(null);
  }
    
  public ErweiterterTableCellRenderer(TableRendererModel model) {
    this.model = model;    
    if (this.model == null) {
      this.model = standardRendererModel;
      zeigeSortierteSpalte = false;
    } else {
      zeigeSortierteSpalte = (model instanceof SortierbaresTableModel) &&
        model.zeigeSortierung();
    }
  }      

  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int columnView) {

    int column = table.convertColumnIndexToModel(columnView);   
    
    //Werte bestimmen
    int farbe = model.getColor(row,column);
    int ausrichtung = model.getAlignment(row, column);   
    boolean zeigeRahmen = model.zeigeRahmen();   
    
    Component renderer;
    if (value instanceof Boolean) {
      renderer = rendererCheckBox;
      rendererCheckBox.setSelected(((Boolean) value).booleanValue());
      rendererCheckBox.setHorizontalAlignment(ausrichtung);      
      rendererCheckBox.setBorder(zeigeRahmen&&hasFocus?border[farbe]:null);
    } else {
      renderer = rendererLabel;      
      rendererLabel.setHorizontalAlignment(ausrichtung);
      rendererLabel.setFont(font[model.getFont(row, column)]);
      rendererLabel.setBorder(zeigeRahmen&&hasFocus?border[farbe]:null);      
      rendererLabel.setText(value == null?"":value.toString());
    }
    
    renderer.setForeground(foregroundColor[farbe]);    

    boolean spalteSortiert = zeigeSortierteSpalte && (column == ((SortierbaresTableModel) model).getSortierteSpalte());
    if (isSelected) {
      if (spalteSortiert) {
        renderer.setBackground(backgroundOrderedSelectedColor[farbe]);
      } else {
        renderer.setBackground(backgroundColor[farbe]);
      }
    } else {
      if (spalteSortiert) {
        renderer.setBackground(backgroundOrderedColor[farbe]);
      } else {
        renderer.setBackground(backgroundColor[0]);
      }      
    }
    return renderer;
  }
}