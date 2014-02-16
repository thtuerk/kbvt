package de.oberbrechen.koeb.gui.admin.benutzerReiter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzern in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class BenutzerReiter extends JPanel
  implements AdminMainReiter {
  
  private BenutzerTableModel benutzerTableModel;
  private JTable vipTable;
  private Main hauptFenster;
  
  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public BenutzerReiter(Main parentFrame) {
    hauptFenster = parentFrame;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  // erzeugt die GUI
  private void jbInit() throws Exception {
    //Medienliste
    vipTable = new JTable();
    benutzerTableModel = new BenutzerTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(benutzerTableModel, vipTable);
    vipTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    vipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    JScrollPane jScrollPane1 = new JScrollPane(vipTable);
    jScrollPane1.setMinimumSize(new Dimension(150,150));
    jScrollPane1.setPreferredSize(new Dimension(150,150));
    jScrollPane1.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(0, 0, 10, 0),
        BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    
  
    JButton aktivButton = new JButton("nicht aktive Benutzer markieren");
    aktivButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        passiveBenutzerMarkieren();
      }
    });
    
    
    this.setLayout(new BorderLayout());
    this.add(jScrollPane1, BorderLayout.CENTER);
    this.add(aktivButton, BorderLayout.SOUTH);
  }

  

  private void passiveBenutzerMarkieren() {
    hauptFenster.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    hauptFenster.setEnabled(false);
    
    new Thread(new Runnable() {
      public void run() {
        benutzerTableModel.passiveBenutzerMarkieren();
      }
    }).run();
    
    hauptFenster.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    hauptFenster.setEnabled(true);    
  }
  
  
  public void aktualisiere() {
    refresh();
  }

  public void refresh() {
    benutzerTableModel.refresh();
    vipTable.doLayout();
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}