package de.oberbrechen.koeb.gui.admin.alleEinstellungenReiter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzern in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class AlleEinstellungenReiter extends JPanel
  implements AdminMainReiter {
  
  private EinstellungenTableModel einstellungenTableModel;
  private JTable einstellungenTable;
  private Main hauptFenster;
  
  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public AlleEinstellungenReiter(Main parentFrame) {
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
    einstellungenTable = new JTable();
    einstellungenTableModel = 
      new EinstellungenTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(einstellungenTableModel, einstellungenTable);
    einstellungenTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    einstellungenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    einstellungenTable.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
        removeEinstellung();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);

    JScrollPane jScrollPane1 = new JScrollPane(einstellungenTable);
    jScrollPane1.setMinimumSize(new Dimension(150,150));
    jScrollPane1.setPreferredSize(new Dimension(150,150));
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));
    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    
  
    this.setLayout(new BorderLayout());
    this.add(jScrollPane1, BorderLayout.CENTER);
  }

  
  /**
   * Löscht die in der Tabelle markierte Einstellung 
   * nach einer Sicherheitsabfrage aus der Datenbank.  
   */
  protected void removeEinstellung() {
    int selectedRow = einstellungenTable.getSelectedRow();
    if (selectedRow != -1) {
      Einstellung einstellung = (Einstellung) einstellungenTableModel.get(selectedRow);
      try {
        int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll die Einstellung\n\n"+
          einstellung.toDebugString() +
          "\n\nwirklich gelöscht werden?",
          "Einstellung löschen?", JOptionPane.YES_NO_OPTION);
        if (erg != JOptionPane.YES_OPTION) return;
  
        einstellung.loesche();
        einstellungenTable.clearSelection();
        einstellungenTableModel.getDaten().remove(selectedRow);
        einstellungenTableModel.fireTableDataChanged();
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, "Beim Löschen des Mediums wurde eine" +
          "Datenbank-Inkonsistenz bemerkt.", false);
      }
    }
  }

  public void aktualisiere() {
    refresh();
  }

  public void refresh() {
    einstellungenTableModel.refresh();
    einstellungenTable.doLayout();
  }
  
  public JMenu getMenu() {
    return null;
  }

  public void focusLost() {
  }
}