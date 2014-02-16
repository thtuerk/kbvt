package de.oberbrechen.koeb.gui.ausleihe.internetFreigabeReiter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.*;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse repräsentiert den Reiter, der die Internetfreigabe
 * ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class InternetFreigabeReiter extends JPanel implements AusleiheMainReiter {

  private Main hauptFenster;

  private InternetfreigabeFactory internetfreigabeFactory;
  private Client aktuellerClient;
  private JButton freigabeButton;
  private JButton sperrenButton;

  public InternetFreigabeReiter(Main parentFrame) {
    internetfreigabeFactory = 
      Datenbank.getInstance().getInternetfreigabeFactory();
    hauptFenster = parentFrame;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    //Buttons bauen
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    buttonPanel.setLayout(new GridLayout(1, 2, 15, 5));
    freigabeButton = new JButton("Internetzugang freischalten");
    sperrenButton = new JButton("Internetzugang sperren");

    freigabeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        freigeben();
      }
    });
    sperrenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sperren();
      }
    });
    buttonPanel.add(freigabeButton, null);
    buttonPanel.add(sperrenButton, null);

    //Tabelle bauen
    final JTable freigabeTable = new JTable();
    final InternetFreigabeTableModel model = new InternetFreigabeTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(model, freigabeTable);
    
    freigabeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    freigabeTable.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        
        int gewaehlteZeile = freigabeTable.getSelectedRow();
        if (gewaehlteZeile != -1) {           
          Client gewaehlterRechner = (Client)
              model.get(gewaehlteZeile);
          setRechner(gewaehlterRechner);
        } else {
          setRechner(null);
        }
      }
    });
    freigabeTable.setRowSelectionInterval(0, 0);
    freigabeTable.scrollRectToVisible(freigabeTable.getCellRect(0, 0, true));
    

    new Timer(500, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int markierung = freigabeTable.getSelectedRow();
        model.fireTableDataChanged();
        freigabeTable.setRowSelectionInterval(markierung, markierung);
      }
    }).start();

    JScrollPane freigabeScrollPane = new JScrollPane();
    JComponentFormatierer.setDimension(
      freigabeScrollPane, new Dimension(0, 40));
    freigabeScrollPane.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(10,10,10,10),
      BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
    freigabeScrollPane.getViewport().add(freigabeTable, null);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    this.add(freigabeScrollPane, BorderLayout.CENTER);
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  //Doku siehe bitte Interface
  public void refresh() {
  }

  /**
   * Setzt den übergebenen Rechner als aktuell bearbeitete, d.h. die
   * Buttons sollen die Internetfreigabe dieses Rechners sperren bzw. freigeben.
   */
  public void setRechner(Client client) {
    if (client == null) return;
    aktuellerClient = client;
    freigabeButton.setText(client.getName()+" freigeben");
    sperrenButton.setText(client.getName()+" sperren");

    try {
      freigabeButton.setEnabled(
        !internetfreigabeFactory.istInternetzugangFreigegeben(client));
      sperrenButton.setEnabled(
          internetfreigabeFactory.istInternetzugangFreigegeben(client));
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      sperrenButton.setEnabled(false);
      freigabeButton.setEnabled(false);      
    }
  }

  /**
   * Gibt den Internetzugang für die aktuelleInternetfreigabe frei.
   */
  void freigeben() {
    Benutzer aktuellerBenutzer = hauptFenster.getAktivenBenutzer();
    Mitarbeiter aktuellerMitarbeiter = hauptFenster.getAktuellenMitarbeiter();
    try {
      internetfreigabeFactory.freigeben(aktuellerBenutzer, 
        aktuellerClient, aktuellerMitarbeiter);
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }

  /**
   * Sperrt den Internetzugang für die aktuelleInternetfreigabe.
   */
  void sperren() {
    internetfreigabeFactory.sperren(aktuellerClient);
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}