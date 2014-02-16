package de.oberbrechen.koeb.gui.ausleihe.benutzerReiter;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.gui.components.BenutzerPanel;
import de.oberbrechen.koeb.gui.ausleihe.*;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzer in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class BenutzerReiter extends JPanel implements AusleiheMainReiter {

  private Main hauptFenster;
  private BenutzerPanel benutzerPanel;
  boolean istVeraenderbar;

  // die verwendeten Buttons;
  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  BenutzerFactory benutzerFactory;
  
  /**
   * Zeigt den übergebenen Benutzer an.
   * @param benutzer der anzuzeigende Benutzer
   */
  public void setBenutzer(Benutzer benutzer) {
    if (benutzer == null) return;
    setVeraenderbar(!benutzer.istGespeichert());
    benutzerPanel.setBenutzer(benutzer);
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setBenutzer(hauptFenster.getAktivenBenutzer());
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

    benutzerPanel.setVeraenderbar(veraenderbar);
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public BenutzerReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    benutzerFactory = Datenbank.getInstance().getBenutzerFactory();
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
    JPanel ButtonPanel = new JPanel();
    benutzerPanel = new BenutzerPanel(hauptFenster);

    this.setLayout(new BorderLayout());
    neuButton.setText("Neuen Benutzer anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setBenutzer(benutzerFactory.erstelleNeu());
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
    ButtonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    ButtonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    benutzerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    this.add(ButtonPanel, BorderLayout.SOUTH);
    ButtonPanel.add(saveButton, null);
    ButtonPanel.add(ladenButton, null);
    ButtonPanel.add(neuButton, null);
    this.add(benutzerPanel, BorderLayout.CENTER);
  }

  /**
   * Läd die Orte neu aus der Datenbank
   */
  public void aktualisiere() {
    benutzerPanel.aktualisiere();
    setVeraenderbar(istVeraenderbar);
  }

  /**
   * Löscht den aktuellen Benutzer
   */
  void loescheBenutzer() {
    Benutzer currentBenutzer = benutzerPanel.getBenutzer();
    boolean loeschenOK = benutzerPanel.loescheBenutzer();
    if (loeschenOK) hauptFenster.removeBenutzer(currentBenutzer);
  }

  /**
   * Speichert die gemachten Änderungen
   */
  void saveChanges() {
    boolean speichernOK = benutzerPanel.saveChanges();
    if (speichernOK) {
      setVeraenderbar(false);
      hauptFenster.setAktivenBenutzer(benutzerPanel.getBenutzer());
    }
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  void aenderungenVerwerfen() {
    if (!benutzerPanel.aenderungenVerwerfen()) {
      hauptFenster.aktualisiereBenutzer();
    } else {
      setVeraenderbar(false);
    }
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