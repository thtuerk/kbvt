package de.oberbrechen.koeb.gui.veranstaltungen.veranstaltungsgruppenReiter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.VeranstaltungsgruppePanel;
import de.oberbrechen.koeb.gui.veranstaltungen.Main;
import de.oberbrechen.koeb.gui.veranstaltungen.VeranstaltungenMainReiter;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Veranstaltungsgruppen in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class VeranstaltungsgruppenReiter extends JPanel implements VeranstaltungenMainReiter {

  private Main hauptFenster;
  private VeranstaltungsgruppePanel veranstaltungsgruppePanel;
  boolean istVeraenderbar;
  Veranstaltungsgruppe aktuelleVeranstaltungsgruppe = null;
  
  // die verwendeten Buttons;
  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
    aktuelleVeranstaltungsgruppe = veranstaltungsgruppe;
    
    setVeraenderbar(veranstaltungsgruppe!= null && 
        !veranstaltungsgruppe.istGespeichert());
    veranstaltungsgruppePanel.setVeranstaltungsgruppe(veranstaltungsgruppe);
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;

    //Buttons anpassen
    neuButton.setEnabled(!veraenderbar);
    hauptFenster.erlaubeAenderungen(!veraenderbar);

    if (!veraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }

    ladenButton.setEnabled(aktuelleVeranstaltungsgruppe != null);
    saveButton.setEnabled(aktuelleVeranstaltungsgruppe != null);
    
    veranstaltungsgruppePanel.setVeraenderbar(veraenderbar);
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public VeranstaltungsgruppenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
  }

  // erzeugt die GUI
  void jbInit() throws Exception {
    JPanel ButtonPanel = new JPanel();
    veranstaltungsgruppePanel = new VeranstaltungsgruppePanel(hauptFenster);

    this.setLayout(new BorderLayout());
    neuButton.setText("Neue Gruppe anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        VeranstaltungsgruppeFactory veranstaltungsgruppeFactory =
          Datenbank.getInstance().getVeranstaltungsgruppeFactory();
        setVeranstaltungsgruppe(veranstaltungsgruppeFactory.erstelleNeu());
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
          loescheVeranstaltungsgruppe();
        } else {
          aenderungenVerwerfen();
        }
      }
    });
    ButtonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    ButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    veranstaltungsgruppePanel.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));

    this.add(ButtonPanel, BorderLayout.SOUTH);
    ButtonPanel.add(saveButton, null);
    ButtonPanel.add(ladenButton, null);
    ButtonPanel.add(neuButton, null);
    this.add(veranstaltungsgruppePanel, BorderLayout.CENTER);
  }

  public void aktualisiere() {
    veranstaltungsgruppePanel.aktualisiere();
    setVeraenderbar(istVeraenderbar);
  }

  /**
   * Löscht die aktuelle Veranstaltungsgruppe
   */
  void loescheVeranstaltungsgruppe() {
    Veranstaltungsgruppe currentVeranstaltungsgruppe = veranstaltungsgruppePanel.getVeranstaltungsgruppe();
    boolean loeschenOK = veranstaltungsgruppePanel.loescheVeranstaltungsgruppe();
    if (loeschenOK) hauptFenster.removeVeranstaltungsgruppe(currentVeranstaltungsgruppe);
  }

  /**
   * Speichert die gemachten Änderungen
   */
  void saveChanges() {
    boolean speichernOK = veranstaltungsgruppePanel.saveChanges();
    if (speichernOK) {
      setVeraenderbar(false);
      hauptFenster.setAktiveVeranstaltungsgruppe(
        veranstaltungsgruppePanel.getVeranstaltungsgruppe());
    }
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  void aenderungenVerwerfen() {
    veranstaltungsgruppePanel.aenderungenVerwerfen();
    hauptFenster.aktualisiereVeranstaltungsgruppe();
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  // Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }  
}