package de.oberbrechen.koeb.gui.veranstaltungen.teilnahmenReiter;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.BenutzerPanel;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.veranstaltungen.Main;
import de.oberbrechen.koeb.gui.veranstaltungen.VeranstaltungenMainReiter;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzer in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class TeilnahmenBenutzerReiter extends JPanel 
  implements VeranstaltungenMainReiter, TeilnahmenReiterInterface {

  private Main hauptFenster;
  boolean istVeraenderbar;

  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  private BenutzerPanel benutzerPanel;
  private JTable veranstaltungenTable;
  private VeranstaltungenTableModel veranstaltungenTableModel;
  boolean veranstaltungenTableModelWirdVeraendert;
  private JLabel kostenLabel = new JLabel();

  /**
   * Zeigt die übergebenen Veranstaltungsgruppe an.
   * @param veranstaltungsgruppe die anzuzeigende Veranstaltungsgruppe
   */
  public void setVeranstaltungsgruppe(
    Veranstaltungsgruppe veranstaltungsgruppe) {
    if (veranstaltungsgruppe == null) return;
    veranstaltungenTableModelWirdVeraendert = true;            
    veranstaltungenTableModel.init(hauptFenster.getAktuellerBenutzer(),
      veranstaltungsgruppe);
    setVeraenderbar(false);
    veranstaltungenTableModelWirdVeraendert = false;            
  }

  /**
   * Zeigt den übergebenen Benutzer an.
   * @param benutzer der anzuzeigende Benutzer
   */
  public void setBenutzer(Benutzer benutzer) {
    if (benutzer == null) return;
    veranstaltungenTableModelWirdVeraendert = true;        
    setVeraenderbar(!benutzer.istGespeichert());
    benutzerPanel.setBenutzer(benutzer);
    veranstaltungenTableModel.init(benutzer,
      hauptFenster.getAktuelleVeranstaltungsgruppe());
    veranstaltungenTableModelWirdVeraendert = false;        
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setBenutzer(hauptFenster.getAktuellerBenutzer());
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    //Buttons anpassen
    neuButton.setEnabled(!veraenderbar);
    hauptFenster.erlaubeAenderungen(!veraenderbar);
    if (!veraenderbar) veranstaltungenTable.clearSelection();

    if (!veraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }

    benutzerPanel.setVeraenderbar(veraenderbar);
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public TeilnahmenBenutzerReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    veranstaltungenTableModelWirdVeraendert = true;    
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    veranstaltungenTableModelWirdVeraendert = false;    
  }

  // erzeugt die GUI
  void jbInit() throws Exception {
    //Buttons bauen
    neuButton.setText("Neuen Benutzer anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setBenutzer(Datenbank.getInstance().getBenutzerFactory().erstelleNeu());
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
          loescheBenutzer();
        } else {
          aenderungenVerwerfen();
        }
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
    buttonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    buttonPanel.add(saveButton, null);
    buttonPanel.add(ladenButton, null);
    buttonPanel.add(neuButton, null);

    //Benutzer bauen
    benutzerPanel = new BenutzerPanel(hauptFenster);
    benutzerPanel.setBorder(BorderFactory.createEmptyBorder(15,10,5,10));

    //Veranstaltungenliste
    veranstaltungenTableModel =
       new VeranstaltungenTableModel(this, hauptFenster.getAktuellerBenutzer(),
       hauptFenster.getAktuelleVeranstaltungsgruppe());
    veranstaltungenTable = new JTable(veranstaltungenTableModel);
    veranstaltungenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ErweiterterTableCellRenderer.setzeErweitertesModell(veranstaltungenTableModel, veranstaltungenTable);

    veranstaltungenTableModel.addTableModelListener(new javax.swing.event.TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        if (!istVeraenderbar && !veranstaltungenTableModelWirdVeraendert) 
          setVeraenderbar(true);
      }
     });

    JPanel kostenPanel = new JPanel();
    kostenPanel.setLayout(new BorderLayout());
    kostenPanel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
    JLabel jLabel16 = new JLabel("Kosten:");
    kostenPanel.add(jLabel16, BorderLayout.WEST);
    kostenPanel.add(kostenLabel, BorderLayout.EAST);

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.getViewport().add(veranstaltungenTable, null);
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));

    JPanel veranstaltungenPanel = new JPanel();
    veranstaltungenPanel.setLayout(new BorderLayout());
    veranstaltungenPanel.setBorder(BorderFactory.createEmptyBorder(15,10,0,10));
    veranstaltungenPanel.setMinimumSize(new Dimension(100,100));
    veranstaltungenPanel.setPreferredSize(new Dimension(100,100));
    veranstaltungenPanel.add(jScrollPane1, BorderLayout.CENTER);
    veranstaltungenPanel.add(kostenPanel, BorderLayout.SOUTH);

    //alles Zusammenbauen
    this.setLayout(new BorderLayout());
    JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    jSplitPane.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane.add(benutzerPanel, JSplitPane.TOP);
    jSplitPane.add(veranstaltungenPanel, JSplitPane.BOTTOM);

    this.add(jSplitPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Läd die Orte neu aus der Datenbank
   */
  public void aktualisiere() {
    setBenutzer(hauptFenster.getAktuellerBenutzer());
    setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
    benutzerPanel.aktualisiere();
  }

  /**
   * Löscht den aktuellen Benutzer
   */
  public void loescheBenutzer() {
    Benutzer currentBenutzer = benutzerPanel.getBenutzer();
    boolean loeschenOK = benutzerPanel.loescheBenutzer();
    if (loeschenOK) hauptFenster.removeBenutzer(currentBenutzer);
  }

  /**
   * Speichert die gemachten Änderungen
   */
  public void saveChanges() {
    if (veranstaltungenTable.isEditing()) {
      JOptionPane.showMessageDialog(hauptFenster, "Bitte beenden Sie zuerst "+
        "die aktuelle Bearbeitung!",
        "Noch in Bearbeitung!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    boolean speichernOK = benutzerPanel.saveChanges();
    if (speichernOK) {
      veranstaltungenTableModel.save();
      setVeraenderbar(false);
      hauptFenster.setAktiverBenutzer(benutzerPanel.getBenutzer());
    }
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  public void aenderungenVerwerfen() {
    if (veranstaltungenTable.isEditing()) {
      JOptionPane.showMessageDialog(hauptFenster, "Bitte beenden Sie zuerst "+
        "die aktuelle Bearbeitung!",
        "Noch in Bearbeitung!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (!benutzerPanel.aenderungenVerwerfen()) {
      hauptFenster.aktualisiereBenutzer();
    } else {
      setBenutzer(benutzerPanel.getBenutzer());
      setVeraenderbar(false);
    }
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  /**
   * Setzt die Kosten der aktuell angezeigten Veranstaltungen.
   * @param kosten die anzuzeigenden Kosten
   */
  public void setzeKosten(String kosten) {
    kostenLabel.setText(kosten);
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}