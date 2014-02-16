package de.oberbrechen.koeb.gui.veranstaltungen.veranstaltungenReiter;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.DelayListSelectionListener;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.components.veranstaltungenPanel.VeranstaltungenPanel;
import de.oberbrechen.koeb.gui.veranstaltungen.Main;
import de.oberbrechen.koeb.gui.veranstaltungen.VeranstaltungenMainReiter;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Veranstaltungen in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class VeranstaltungenReiter extends JPanel implements VeranstaltungenMainReiter {
  
  Main hauptFenster;
  boolean istVeraenderbar;

  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  VeranstaltungenPanel detailsPanel;
  JTable veranstaltungenTabelle;
  private DelayListSelectionListener veranstaltungenTabelleDelayListSelectionListener;
  VeranstaltungenTableModel veranstaltungenTableModel;

  boolean veranstaltungenListeUmgekehrteSortierung;
  int veranstaltungenListeSortierung;
  
  VeranstaltungFactory veranstaltungFactory;

  //Doku siehe bitte Interface
  public void refresh() {
    setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
    initButtons();
  }

  void initButtons() {
    hauptFenster.erlaubeAenderungen(!istVeraenderbar);
    veranstaltungenTabelle.setEnabled(!istVeraenderbar);
    
    if (!istVeraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }
    
    boolean veranstaltungsgruppeAusgewaehlt =
      (hauptFenster.getAktuelleVeranstaltungsgruppe() != null);  
    boolean veranstaltungAngezeigt = (veranstaltungsgruppeAusgewaehlt &&
        detailsPanel.getVeranstaltung() != null);
    neuButton.setEnabled(veranstaltungsgruppeAusgewaehlt && !istVeraenderbar);    
    ladenButton.setEnabled(veranstaltungAngezeigt); 
    saveButton.setEnabled(veranstaltungAngezeigt); 

  }
  
  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    detailsPanel.setVeraenderbar(veraenderbar);
    initButtons();
  }

  /**
   * Erzeugt einen VeranstaltungenReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public VeranstaltungenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    veranstaltungFactory = Datenbank.getInstance().getVeranstaltungFactory();

    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      ErrorHandler.getInstance().handleException(e, true);
    }

    setVeraenderbar(false);
  }

  // erzeugt die GUI
  private void jbInit() throws Exception {
    //Buttons bauen
    neuButton.setText("Neue Veranstaltung anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neueVeranstaltungAnlegen();
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
          loescheVeranstaltung();
        } else {
          aenderungenVerwerfen();
        }
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    buttonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    buttonPanel.add(saveButton, null);
    buttonPanel.add(ladenButton, null);
    buttonPanel.add(neuButton, null);

    //Details bauen
    detailsPanel = new VeranstaltungenPanel(hauptFenster);
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));

    //Medienliste
    veranstaltungenTabelle = new JTable();
    veranstaltungenTableModel = new VeranstaltungenTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(veranstaltungenTableModel, veranstaltungenTabelle);
    veranstaltungenTabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    veranstaltungenTabelleDelayListSelectionListener = new DelayListSelectionListener(250);
    veranstaltungenTabelleDelayListSelectionListener.addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int gewaehlteReihe = veranstaltungenTabelle.getSelectedRow();
            if (gewaehlteReihe != -1 && gewaehlteReihe < veranstaltungenTableModel.getRowCount()) {
              Veranstaltung gewaehlteVeranstaltung = (Veranstaltung) 
                veranstaltungenTableModel.get(gewaehlteReihe);
              detailsPanel.setVeranstaltung(gewaehlteVeranstaltung);
              initButtons();              
            }
          }
      });
    veranstaltungenTabelle.getSelectionModel().addListSelectionListener(veranstaltungenTabelleDelayListSelectionListener);

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));

    JPanel veranstaltungenPanel = new JPanel();
    veranstaltungenPanel.setLayout(new GridBagLayout());
    veranstaltungenPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    veranstaltungenPanel.setMinimumSize(new Dimension(50,80));
    veranstaltungenPanel.setPreferredSize(new Dimension(50,80));
    veranstaltungenPanel.add(jScrollPane1,         new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(veranstaltungenTabelle, null);

    //alles Zusammenbauen
    this.setLayout(new BorderLayout());
    JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane.setResizeWeight(1);
    jSplitPane.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane.add(detailsPanel, JSplitPane.BOTTOM);
    jSplitPane.add(veranstaltungenPanel, JSplitPane.TOP);

    this.add(jSplitPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Legt eine neue Veranstaltung an und zeigt es an
   */
  void neueVeranstaltungAnlegen() {
    veranstaltungenTabelleDelayListSelectionListener.fireDelayListSelectionEvent();
    Veranstaltung neueVeranstaltung = veranstaltungFactory.erstelleNeu();
    neueVeranstaltung.setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
    detailsPanel.setVeranstaltung(neueVeranstaltung);
    setVeraenderbar(true);
  }

  public void aktualisiere() {
    detailsPanel.aktualisiere();
    refresh();
  }

  /**
   * Löscht die aktuelle Veranstaltung
   */
  public void loescheVeranstaltung() {
    Veranstaltung currentVeranstaltung = detailsPanel.getVeranstaltung();
    boolean loeschenOK = detailsPanel.loescheVeranstaltung();
    if (loeschenOK) {
      veranstaltungenTableModel.getDaten().remove(currentVeranstaltung);
      if (veranstaltungenTableModel.size() > 0) {
        veranstaltungenTabelle.setRowSelectionInterval(0, 0);
        veranstaltungenTabelle.scrollRectToVisible(veranstaltungenTabelle.getCellRect(0, 0, true));
      } else {
        detailsPanel.setVeranstaltung(null);
      }
      initButtons();
    }      
  }

  /**
   * Speichert die gemachten Änderungen
   */
  public void saveChanges() {
    boolean istNeu = detailsPanel.getVeranstaltung().istNeu();
    boolean speichernOK = detailsPanel.saveChanges();
    if (speichernOK) {
      setVeraenderbar(false);
      if (istNeu) veranstaltungenTableModel.getDaten().add(detailsPanel.getVeranstaltung());
      Veranstaltung neueVeranstaltung = detailsPanel.getVeranstaltung();
      detailsPanel.setVeranstaltung(neueVeranstaltung);
    }
    initButtons();      
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  public void aenderungenVerwerfen() {
    boolean ok = detailsPanel.aenderungenVerwerfen();
    if (ok) {
      setVeraenderbar(false);
    }
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  // Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
  }

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
    if (veranstaltungsgruppe == null) {
      veranstaltungenTableModel.getDaten().clear();
      return;
    }
    
    VeranstaltungFactory veranstaltungFactory =
      Datenbank.getInstance().getVeranstaltungFactory();
    VeranstaltungenListe liste = veranstaltungFactory.getVeranstaltungen(
      veranstaltungsgruppe);
    liste.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);
    veranstaltungenTableModel.setDaten(liste);

    if (veranstaltungenTableModel.size() > 0) {
      veranstaltungenTabelle.setRowSelectionInterval(0, 0);
      veranstaltungenTabelle.scrollRectToVisible(
          veranstaltungenTabelle.getCellRect(0, 0, true));
    } else {
      detailsPanel.setVeranstaltung(null);
    }
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}