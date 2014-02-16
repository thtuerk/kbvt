package de.oberbrechen.koeb.gui.admin.clientReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.ClientFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Clients in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class ClientReiter extends JPanel
  implements AdminMainReiter {
  
  private ClientTableModel clientTableModel;
  private JTable clientTable;
  private Main hauptFenster;
  
  public ClientReiter(Main parentFrame) {
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
    clientTable = new JTable();
    clientTableModel = new ClientTableModel(hauptFenster);
    ErweiterterTableCellRenderer.setzeErweitertesModell(clientTableModel, clientTable);
    clientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    clientTable.registerKeyboardAction( new ActionListener() {
       public void actionPerformed(ActionEvent e) {
        removeClient();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);

    JScrollPane jScrollPane1 = new JScrollPane(clientTable);
    jScrollPane1.setMinimumSize(new Dimension(150,150));
    jScrollPane1.setPreferredSize(new Dimension(150,150));
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));
  
    //Buttonpanel
    JPanel buttonPanel = new JPanel();
    JButton neuButton = new JButton("Neuen Client anlegen");
    neuButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        neuerClient();
      }});
    JButton loeschenButton = new JButton("Client löschen");
    loeschenButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        removeClient();
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

  
  protected void neuerClient() {
    try {
      String erg = JOptionPane.showInputDialog(hauptFenster, 
        "Bitte den Namen des neuen Clients angeben:",
        "Neuen Client anlegen", JOptionPane.OK_CANCEL_OPTION);
      if (erg == null) return;
      if (erg.equals("")) return;
  
      ClientFactory clientFactory = 
        Datenbank.getInstance().getClientFactory();
      Client client = clientFactory.erstelleNeu();
      client.setName(erg);
      client.save();
      clientTableModel.getDaten().add(client);
      clientTableModel.fireTableDataChanged();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Neuer Client konnte nicht "+
          "gespeichert werden!", false);
    }
  }

  /**
   * Löscht den in der Tabelle markierte Client 
   * nach einer Sicherheitsabfrage aus der Datenbank.  
   */
  protected void removeClient() {
    int selectedRow = clientTable.getSelectedRow();
    if (selectedRow != -1) {
      Client client = (Client) clientTableModel.get(selectedRow);
      try {
        int erg = JOptionPane.showConfirmDialog(hauptFenster, 
          "Soll der Client '"+client.toDebugString()+
          "' wirklich gelöscht werden?",
          "Client löschen?", JOptionPane.YES_NO_OPTION);
        if (erg != JOptionPane.YES_OPTION) return;
  
        client.loesche();
        clientTable.clearSelection();
        clientTableModel.getDaten().remove(selectedRow);
        clientTableModel.fireTableDataChanged();
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
    clientTableModel.refresh();
    clientTable.doLayout();
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}