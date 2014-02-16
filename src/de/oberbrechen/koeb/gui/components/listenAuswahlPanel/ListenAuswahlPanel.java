package de.oberbrechen.koeb.gui.components.listenAuswahlPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Einträge aus einer Liste ausgwählt
 * werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class ListenAuswahlPanel<T> extends JPanel {

  public static final int AUSWAHLTYP_STANDARD = 0;
  
  @SuppressWarnings("unused")
  private boolean istVeraenderbar;

  // die Datenfelder
  protected AbstractListeSortierbaresTableModel<T> auswahlModell;
  protected JTable auswahlTabelle;

  protected AbstractListeSortierbaresTableModel<T> datenModell;
  protected JTable datenTabelle;

  private JButton removeButton;
  private JButton insertButton;
  private JButton clearButton;
  private JButton invertButton;
  protected JComboBox auswahlComboBox;
  private boolean horizontaleAusrichtung;
  private boolean zeigeAuswahl;
  private boolean selektionWirdGeradeVeraendert = false;
  
  /**
   * Setzt die übergebene Liste als aktuelle Auswahl
   * @param collection die neue Auswahl
   */
  public void setAuswahl(Collection<? extends T> collection) {
    auswahlModell.setDaten(collection);
  }
  
  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    //Eingabefelder
    this.istVeraenderbar = veraenderbar;
    
    removeButton.setEnabled(veraenderbar);
    insertButton.setEnabled(veraenderbar);
    clearButton.setEnabled(veraenderbar);
    auswahlTabelle.setEnabled(veraenderbar);
    datenTabelle.setEnabled(veraenderbar);
    
    if (!veraenderbar) {
      auswahlTabelle.clearSelection();
      datenTabelle.clearSelection();
    }
  }

  /**
   * Erzeugt ein MedienPanel, das im übergebenen Frame angezeigt wird und
   * den Standardauswahltyp besitzt.
   * @param parentFrame Frame, zu dem das Panel gehört
   * @param horizontaleAusrichtung bestimmt, ob die Tabellen übereinander
   *   oder nebeneinander dargestellt werden sollen. Ein Wert von False
   *   sorgt für eine Ausrichtung der Tabellen übereinander.
   * @param bestimmt, ob die Auswahl angezeigt werden soll
   */
  public ListenAuswahlPanel(boolean horizontaleAusrichtung, boolean zeigeAuswahl) {
    this(horizontaleAusrichtung, zeigeAuswahl, AUSWAHLTYP_STANDARD);
  }
    /**
     * Erzeugt ein MedienPanel, das im übergebenen Frame angezeigt wird. 
     * Über den Auswahltyp kann festgelegt werden, welche Daten zur
     * Auswahl angeboten werden.
     * @param parentFrame Frame, zu dem das Panel gehört
     * @param horizontaleAusrichtung bestimmt, ob die Tabellen übereinander
     *   oder nebeneinander dargestellt werden sollen. Ein Wert von False
     *   sorgt für eine Ausrichtung der Tabellen übereinander.
     * @param bestimmt, ob die Auswahl angezeigt werden soll
     */
    public ListenAuswahlPanel(boolean horizontaleAusrichtung, boolean zeigeAuswahl,
        int auswahlTyp) {
    
    this.zeigeAuswahl = zeigeAuswahl;
    this.horizontaleAusrichtung = horizontaleAusrichtung;
    try {
      jbInit(auswahlTyp);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    setVeraenderbar(true);
  }
  
  /**
   * Erzeugt ein neues AuswahlTableModel, das alle Daten enthält, aus denen
   * ausgewählt werden kann
   * @param table die Tabelle, für die das Datenmodell erzeugt werden soll
   * @return ein neues leeres AuswahlTableModel
   * @throws DatenbankInkonsistenzException
   */
  protected abstract AbstractListeSortierbaresTableModel<T> createDatenAuswahlTableModel(JTable table, int auswahlTyp) throws DatenbankInkonsistenzException;

  /**
   * Erzeugt ein neues leeres AuswahlTableModel für die übergebene Tabelle.
   * @param table die Tabelle, für die das Datenmodell erzeugt werden soll
   * @return ein neues leeres AuswahlTableModel
   */
  protected abstract AbstractListeSortierbaresTableModel<T> createEmptyAuswahlTableModel(JTable table, int auswahlTyp);

  // erzeugt die GUI
  void jbInit(int auswahlTyp) throws Exception {

    //Alle wichtigen Objeke initialisieren
    auswahlComboBox = new JComboBox();
    initAuswahlComboBox();
    
    auswahlTabelle = new JTable();
    auswahlTabelle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    auswahlModell = createEmptyAuswahlTableModel(auswahlTabelle, auswahlTyp);
    ErweiterterTableCellRenderer.setzeErweitertesModell(auswahlModell, auswahlTabelle);  
    
    auswahlTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
          removeDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
    auswahlTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         removeDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK), JComponent.WHEN_FOCUSED);
    auswahlTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         removeDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);

    datenTabelle = new JTable();
    datenModell = createDatenAuswahlTableModel(datenTabelle, auswahlTyp);
    datenTabelle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    ErweiterterTableCellRenderer.setzeErweitertesModell(datenModell, datenTabelle);
    
    datenTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
        switchDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK), JComponent.WHEN_FOCUSED);
    datenTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         switchDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
    datenTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         insertDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), JComponent.WHEN_FOCUSED);
    datenTabelle.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         removeDatenDaten();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);

    datenTabelle.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent arg0) {
            if (!selektionWirdGeradeVeraendert) {
              auswahlComboBox.setSelectedItem(null);
              selektionWirdGeradeVeraendert = true;
              auswahlTabelle.clearSelection();
              selektionWirdGeradeVeraendert = false;
            }
          }});
    auswahlTabelle.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent arg0) {
            if (!selektionWirdGeradeVeraendert) {
              selektionWirdGeradeVeraendert = true;
              datenTabelle.clearSelection();
              selektionWirdGeradeVeraendert = false;
            }            
          }});
    auswahlComboBox.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        new Thread() {
          public void run() {
            Object selektionObject = auswahlComboBox.getSelectedItem();
            if (selektionObject == null || !(selektionObject instanceof TabellenSelektion)) return;
            final TabellenSelektion selektion = (TabellenSelektion) selektionObject;

            selektionWirdGeradeVeraendert = true;
            try {
              SwingUtilities.invokeAndWait(
                  new Runnable() {
                    public void run() {
                      auswahlTabelle.clearSelection();
                      selektion.selektiere(datenModell.getDaten(), datenTabelle.getSelectionModel());
                    }
                  }
              );
            } catch (Exception e) {
              ErrorHandler.getInstance().handleException(e, false);
            }
            selektionWirdGeradeVeraendert = false;
          }
        }.start();
      }
    });
    
    clearButton = new JButton();
    removeButton = new JButton();
    insertButton = new JButton();
    insertButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        insertDaten();
      }
    });    
    
    invertButton = new JButton("Invertieren");    
    invertButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        invertAuswahl();
      }
    });    
    datenTabelle.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
          insertDaten();
        }
      }

      public void mousePressed(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}      
    });
    auswahlTabelle.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) { 
          removeDaten();
        }
      }

      public void mousePressed(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}      
    });
    removeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         removeDaten();
         removeDatenDaten();
      }
    });    
    
    clearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearAuswahl();
      }
    });    
    
    
    //SystematikPanel
    this.setLayout(new GridBagLayout());

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setMinimumSize(new Dimension(0, 40));
    jScrollPane1.setPreferredSize(new Dimension(0, 40));
    jScrollPane1.getViewport().add(auswahlTabelle, null);

    JScrollPane jScrollPane2 = new JScrollPane();
    jScrollPane2.setMinimumSize(new Dimension(0, 40));
    jScrollPane2.setPreferredSize(new Dimension(0, 40));
    jScrollPane2.getViewport().add(datenTabelle, null);


    //Auswahl
    JComponentFormatierer.setDimension(auswahlComboBox, new JTextField().getPreferredSize());
    JComponentFormatierer.setDimension(invertButton, new Dimension(invertButton.getPreferredSize().width, auswahlComboBox.getPreferredSize().height));    
    
    JPanel auswahlPanel = new JPanel();
    auswahlPanel.setLayout(new GridBagLayout());
    auswahlPanel.add(new JLabel("Auswahl:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    auswahlPanel.add(auswahlComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    auswahlPanel.add(invertButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    
    
    if (horizontaleAusrichtung) {
      removeButton.setText("-->");
      clearButton.setText("==>");
      insertButton.setText("<--");
      JComponentFormatierer.setDimension(insertButton, clearButton.getPreferredSize());
      JComponentFormatierer.setDimension(removeButton, clearButton.getPreferredSize());
      JComponentFormatierer.setDimension(clearButton, clearButton.getPreferredSize());      
      
      if (zeigeAuswahl) {
        this.add(auswahlPanel,new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 5, 0), 0, 0));
      }
      this.add(jScrollPane1,         new GridBagConstraints(0, 0, 1, 4, 1.0, 1.0
              ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
      this.add(jScrollPane2,          new GridBagConstraints(2, 1, 1, 3, 1.0, 1.0
              ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
      this.add(removeButton,     new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0
          ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 0, 0), 0, 0));
      this.add(clearButton,     new GridBagConstraints(1, 2, 1, 1, 0.0, 1.0
              ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 0, 0));
      this.add(insertButton,   new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0
              ,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));
    } else {
      removeButton.setText("entfernen");
      insertButton.setText("hinzufügen");
      clearButton.setText("zurücksetzen");
      
      JComponentFormatierer.setDimension(insertButton, clearButton.getPreferredSize());
      JComponentFormatierer.setDimension(removeButton, clearButton.getPreferredSize());
      JComponentFormatierer.setDimension(clearButton, clearButton.getPreferredSize());
      
      if (zeigeAuswahl) {
        this.add(auswahlPanel,new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
      }

      this.add(jScrollPane2,         new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0
          ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      this.add(jScrollPane1,          new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0
          ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      this.add(insertButton,   new GridBagConstraints(0, 2, 1, 1, 2.0, 0.0
          ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 5), 0, 0));
      this.add(removeButton,     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
          ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 10, 0), 0, 0));      
      this.add(clearButton,     new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0
          ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));      
    }
    this.setPreferredSize(new Dimension(600, 200));
    
  }

  protected void initAuswahlComboBox() {
    auswahlComboBox.removeAllItems();
    auswahlComboBox.addItem("");
    auswahlComboBox.addItem(new TabellenSelektion(){

      protected boolean istInSelektion(Object o) {
        return true;
      }
    
      public String toString() {
        return "Gesamt";
      }
    });
  }
  
  void removeDatenDaten() {
    int[] gewaehlteZeilen = datenTabelle.getSelectedRows();
    Vector<T> gewaehlteDaten = new Vector<T>();
    for (int i=0; i < gewaehlteZeilen.length; i++) {
      gewaehlteDaten.add(datenModell.get(gewaehlteZeilen[i]));
    }
    auswahlModell.removeDaten(gewaehlteDaten);
  }

  void switchDaten() {
    int[] gewaehlteZeilen = datenTabelle.getSelectedRows();
    for (int i=0; i < gewaehlteZeilen.length; i++) {
      auswahlModell.switchDaten(datenModell.get(gewaehlteZeilen[i]));
    }
  }

  void insertDaten() {
    int[] gewaehlteZeilen = datenTabelle.getSelectedRows();
    Vector<T> gewaehlteDaten = new Vector<T>();
    for (int i=0; i < gewaehlteZeilen.length; i++) {
      gewaehlteDaten.add(datenModell.get(
        gewaehlteZeilen[i]));
    }
    auswahlModell.addDaten(gewaehlteDaten);
  }
  
  void invertAuswahl() {
    ListSelectionModel selectionModel = datenTabelle.getSelectionModel();
    int[] selectedRows = datenTabelle.getSelectedRows();
    selectionModel.setValueIsAdjusting(false);
    selectionModel.clearSelection();
    int lastIndex = -1;
    for (int i=0; i < selectedRows.length; i++) {      
      int currentIndex = selectedRows[i];
      if (lastIndex+1 <= currentIndex-1)
        selectionModel.addSelectionInterval(lastIndex+1, currentIndex-1);      
      lastIndex = currentIndex;
    }
    if (lastIndex+1 <= datenTabelle.getRowCount()-1)
      selectionModel.addSelectionInterval(lastIndex+1, datenTabelle.getRowCount()-1);        
    selectionModel.setValueIsAdjusting(true);    
  }
  
  void removeDaten() {
    int[] gewaehlteZeilen = auswahlTabelle.getSelectedRows();
    Vector<T> gewaehlteDaten = new Vector<T>();
    for (int i=0; i < gewaehlteZeilen.length; i++) {
      gewaehlteDaten.add(auswahlModell.get(
        gewaehlteZeilen[i]));
    }
    auswahlModell.removeDaten(gewaehlteDaten);
  }
  
  /**
   * Liefert die aktuelle Auswahl
   * @return die aktuelle Auswahl
   */
  public Liste<T> getAuswahl() {
    return auswahlModell.getDaten();
  }
  
  /**
   * Sortiert die Auswahl und die Daten nach der übergebenenen Spalte
   * @param spalte
   * @param umgekehrteSortierung
   */
  public void sortiereNachSpalte(int spalte, boolean umgekehrteSortierung) {
    datenTabelle.clearSelection();
    auswahlTabelle.clearSelection();
    datenModell.sortiereNachSpalte(spalte, umgekehrteSortierung);
    auswahlModell.sortiereNachSpalte(spalte, umgekehrteSortierung);
  }
  
  /**
   * Setzt die Daten, aus denen ausgewählt werden kann.
   * @param daten die Daten
   */
  public void setDaten(Collection<? extends T> daten) {
    datenModell.setDaten(daten);
  }
  
  /**
   * Entfernt alle Einträge aus der Auswahl.
   */
  public void clearAuswahl() {
    auswahlModell.clear();
  }
}