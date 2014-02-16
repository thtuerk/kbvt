package de.oberbrechen.koeb.gui.admin.mitarbeiterReiter;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.*;

import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.MitarbeiterPanel;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzer in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MitarbeiterReiter extends JPanel implements AdminMainReiter {

  Main hauptFenster;
  MitarbeiterPanel mitarbeiterPanel;
  boolean istVeraenderbar;
  MitarbeiterTableModel mitarbeiterTableModel;
  JTable mitarbeiterTabelle;
  
  // die verwendeten Buttons;
  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  void neuenMitarbeiterAnlegen() {
    setVeraenderbar(true);
    mitarbeiterTabelle.clearSelection();
    mitarbeiterPanel.setMitarbeiter(null);
  }

  //Doku siehe bitte Interface
  public void refresh() {
    if (mitarbeiterTabelle.getSelectedRow() == -1) {
      mitarbeiterTabelle.getSelectionModel().setSelectionInterval(0, 0);
    }
      
    if (!istVeraenderbar) {
      Mitarbeiter aktuellerMitarbeiter = mitarbeiterPanel.getMitarbeiter();
      if (aktuellerMitarbeiter != null) {
        try {
          aktuellerMitarbeiter.reload();
        } catch (DatenbankzugriffException e) {
          ErrorHandler.getInstance().handleException(e, false);
          aktuellerMitarbeiter = null;
        } 
        mitarbeiterPanel.setMitarbeiter(aktuellerMitarbeiter);
      }
    }
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    hauptFenster.erlaubeAenderungen(!veraenderbar);
    mitarbeiterTabelle.setEnabled(!veraenderbar);
    
    //Buttons anpassen
    neuButton.setEnabled(!veraenderbar);

    if (!veraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }

    mitarbeiterPanel.setVeraenderbar(veraenderbar);
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public MitarbeiterReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  // erzeugt die GUI
  void jbInit() throws Exception {
    //Mitarbeiterpanel
    mitarbeiterPanel = new MitarbeiterPanel(hauptFenster);
    mitarbeiterPanel.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));

    //Tabelle
    mitarbeiterTableModel = new MitarbeiterTableModel();
    mitarbeiterTabelle = new JTable();
    ErweiterterTableCellRenderer.setzeErweitertesModell(mitarbeiterTableModel, mitarbeiterTabelle);
    mitarbeiterTabelle.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          int gewaehlteReihe = mitarbeiterTabelle.getSelectedRow();
          if (gewaehlteReihe != -1) {
            Mitarbeiter gewaehlterMitarbeiter = (Mitarbeiter)
              mitarbeiterTableModel.get(gewaehlteReihe);
            mitarbeiterPanel.setMitarbeiter(gewaehlterMitarbeiter);
          }
        }
      }
    );
    mitarbeiterTableModel.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        int gewaehlteReihe = mitarbeiterTabelle.getSelectedRow();
        if (gewaehlteReihe != -1) {
          Mitarbeiter gewaehlterMitarbeiter = (Mitarbeiter) 
            mitarbeiterTableModel.get(gewaehlteReihe);
          mitarbeiterPanel.setMitarbeiter(gewaehlterMitarbeiter);
        }
      }
    });
    JScrollPane jScrollPane1 = new JScrollPane(mitarbeiterTabelle);
    jScrollPane1.setMinimumSize(new Dimension(100,40));
    jScrollPane1.setPreferredSize(new Dimension(100,40));
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));
    JPanel mitarbeiterTablePanel = new JPanel();
    mitarbeiterTablePanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));    
    mitarbeiterTablePanel.setLayout(new BorderLayout());
    mitarbeiterTablePanel.add(jScrollPane1, BorderLayout.CENTER);
    
    //Button-Panel
    neuButton.setText("Neuen Mitarbeiter anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neuenMitarbeiterAnlegen();
      }
    });
    saveButton.setText("Speichern");
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (istVeraenderbar) {
          saveChanges();
        } else {
          setVeraenderbar(true);
        }
      }
    });
    ladenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!istVeraenderbar) {
          loescheMitarbeiter();
        } else {
          aenderungenVerwerfen();
        }
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    buttonPanel.add(saveButton, null);
    buttonPanel.add(ladenButton, null);
    buttonPanel.add(neuButton, null);

    //Alles Zusammenbauen
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.add(mitarbeiterTablePanel, JSplitPane.TOP);
    jSplitPane1.add(mitarbeiterPanel, JSplitPane.BOTTOM);
    jSplitPane1.setResizeWeight(1.0);
    this.add(jSplitPane1, BorderLayout.CENTER);
    
    this.setLayout(new BorderLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    this.add(jSplitPane1, BorderLayout.CENTER);
  }

  /**
   * Läd die Orte neu aus der Datenbank
   */
  public void aktualisiere() {
    mitarbeiterPanel.aktualisiere();
    setVeraenderbar(istVeraenderbar);
  }

  /**
   * Löscht den aktuellen Mitarbeiter
   */
  void loescheMitarbeiter() {
    boolean loeschenOK = mitarbeiterPanel.loescheMitarbeiter();
    if (loeschenOK) {
      mitarbeiterTableModel.refresh();
      mitarbeiterTabelle.getSelectionModel().setSelectionInterval(0, 0);
    } 
  }

  /**
   * Speichert die gemachten Änderungen
   */
  void saveChanges() {
    boolean speichernOK = mitarbeiterPanel.saveChanges();
    if (speichernOK) {
      setVeraenderbar(false);
      mitarbeiterTableModel.add(mitarbeiterPanel.getMitarbeiter());
    }
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  void aenderungenVerwerfen() {
    Mitarbeiter currentMitarbeiter = mitarbeiterPanel.getMitarbeiter();
    
    if (currentMitarbeiter == null) {
      mitarbeiterTabelle.getSelectionModel().setSelectionInterval(0,0);
    } else {
      try {
        currentMitarbeiter.reload();
        mitarbeiterPanel.setMitarbeiter(currentMitarbeiter);        
      } catch (DatenNichtGefundenException e) {
        mitarbeiterTableModel.refresh();
        mitarbeiterTabelle.getSelectionModel().setSelectionInterval(0,0);
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, false);
        mitarbeiterTableModel.refresh();
        mitarbeiterTabelle.getSelectionModel().setSelectionInterval(0,0);        
      }
    }    
         
    setVeraenderbar(false);
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}