package de.oberbrechen.koeb.gui.admin.clientReiter;

import javax.swing.JOptionPane;

import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.ClientFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.ClientListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von Clients.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class ClientTableModel extends AbstractListeTableModel<Client> {

  private Main hauptFenster;
  
  public ClientTableModel(Main hauptFenster) {
    this.hauptFenster = hauptFenster;
    initDaten(new ClientListe());
    daten.setSortierung(ClientListe.NameSortierung);
  }
  
  public void refresh() {
    ClientFactory clientFactory =
      Datenbank.getInstance().getClientFactory();
    setDaten(clientFactory.getAlleClients());
  }  

  public int getColumnCount() {
    return 4;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "ID";
    if (columnIndex == 1) return "Internetzugang";
    if (columnIndex == 2) return "Name";
    if (columnIndex == 3) return "IP";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return Integer.class;
    if (columnIndex == 1) return Boolean.class;
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Client gewaehlterClient = (Client) daten.get(rowIndex);
    
    if (columnIndex == 0) {
      return new Integer(gewaehlterClient.getId());
    } else if (columnIndex == 1) {
      return new Boolean(gewaehlterClient.getBesitztInternetzugang());
    } else if (columnIndex == 2) {
      return gewaehlterClient.getName();
    } else if (columnIndex == 3) {
      return gewaehlterClient.getIP();
    }
    
    return "nicht definierte Spalte";
  }
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex > 0;
  }

  @SuppressWarnings("null")
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Object oldValue = null;
    Client client = (Client) get(rowIndex);
    try {      
      if (columnIndex == 1) {
        oldValue = new Boolean(client.getBesitztInternetzugang());
        client.setBesitztInternetzugang(((Boolean) aValue).booleanValue());         
        client.save();
      } else if (columnIndex == 2) {
        oldValue = client.getName();
        client.setName(aValue.toString());         
        client.save();
      } else if (columnIndex == 3) {
        oldValue = client.getIP();
        client.setIP(aValue.toString());
        client.save();          
      } else {
        throw new IndexOutOfBoundsException();
      }
      this.fireTableDataChanged();
      return;
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);      
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }

    if (columnIndex == 1) {
      client.setBesitztInternetzugang(((Boolean) oldValue).booleanValue());         
    } else if (columnIndex == 2) {
      client.setName((String) oldValue);         
    } else if (columnIndex == 3) {
      client.setIP((String) oldValue);
    }
    this.fireTableDataChanged();
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 50;
      case 1: return 50;
      case 2: return 250;
      case 3: return 250;
    }
    
    return 500;
  }

  public boolean zeigeRahmen() {
    return true;
  }
}
