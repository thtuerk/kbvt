package de.oberbrechen.koeb.gui.admin.medientypReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.MedientypSchonVergebenException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Medientypen in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MedientypReiter extends JPanel
  implements AdminMainReiter {
  
  private MedientypTableModel medientypTableModel;
  private JTable medientypTable;
  private Main hauptFenster;
  
  public MedientypReiter(Main parentFrame) {
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
    medientypTable = new JTable();
    medientypTableModel = new MedientypTableModel(hauptFenster);
    ErweiterterTableCellRenderer.setzeErweitertesModell(medientypTableModel, medientypTable);
    medientypTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    medientypTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    medientypTable.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
          removeMedientyp();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);

    JScrollPane jScrollPane1 = new JScrollPane(medientypTable);
    jScrollPane1.setMinimumSize(new Dimension(150,150));
    jScrollPane1.setPreferredSize(new Dimension(150,150));
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));
  
    //Buttonpanel
    JPanel buttonPanel = new JPanel();
    JButton neuButton = new JButton("Neuen Medientyp anlegen");
    neuButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        neuenMedientyp();
      }});
    JButton loeschenButton = new JButton("Medientyp löschen");
    loeschenButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        removeMedientyp();
      }});
    
    buttonPanel.setLayout(new GridLayout(1, 2, 10, 0));
    buttonPanel.add(neuButton);
    buttonPanel.add(loeschenButton);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));    
     
    //Alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(jScrollPane1, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    
  }

  
  protected void neuenMedientyp() {
    String erg = JOptionPane.showInputDialog(hauptFenster, 
      "Bitte den Namen des neuen Medientyps angeben:",
      "Neuen Medientyp anlegen", JOptionPane.OK_CANCEL_OPTION);
    if (erg == null) return;
    if (erg.equals("")) return;
    try {
      MedientypFactory medientypFactory =
        Datenbank.getInstance().getMedientypFactory();
      Medientyp medientyp = medientypFactory.erstelleNeu();
      medientyp.setName(erg);
      medientyp.save();
      medientypTableModel.getDaten().add(medientyp);
      medientypTableModel.fireTableDataChanged();
    } catch (MedientypSchonVergebenException e) {
      JOptionPane.showMessageDialog(hauptFenster, "Ein Medientyp mit dem Namen '"+
        erg+"' existiert bereits!",
        "Medientyp bereits vergeben!", JOptionPane.ERROR_MESSAGE);
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Medientyp konnte nicht " +
          "angelegt werden!", false);
    }
  }

  /**
   * Löscht den in der Tabelle markierte Medientyp 
   * nach einer Sicherheitsabfrage aus der Datenbank.  
   */
  protected void removeMedientyp() {
    int selectedRow = medientypTable.getSelectedRow();
    if (selectedRow != -1) {
      Medientyp medientyp = (Medientyp) medientypTableModel.get(selectedRow);
      try {
        int erg = JOptionPane.showConfirmDialog(hauptFenster, 
          "Soll der Medientyp '"+medientyp.getName()+
          "' wirklich gelöscht werden?",
          "Medientyp löschen?", JOptionPane.YES_NO_OPTION);
        if (erg != JOptionPane.YES_OPTION) return;
  
        medientyp.loesche();
        medientypTable.clearSelection();
        medientypTableModel.getDaten().remove(selectedRow);
        medientypTableModel.fireTableDataChanged();
      } catch (DatenbankInkonsistenzException e) {
        JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
          "Datenbank Inkonsistenz!", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void aktualisiere() {
    refresh();
  }

  public void refresh() {
    medientypTableModel.refresh();
    medientypTable.doLayout();
  }
  
  public JMenu getMenu() {
    return null;
  }  
  
  public void focusLost() {
  }
}