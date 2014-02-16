package de.oberbrechen.koeb.gui.components;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.standarddialoge.PasswortAendernDialog;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Mitarbeiter-Daten angezeigt und
 * bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MitarbeiterPanel extends JPanel {

  PasswortAendernDialog passwortDialog;
  private boolean istVeraenderbar;
  JFrame hauptFenster;
  BenutzerPanel benutzerPanel;

  Mitarbeiter currentMitarbeiter;
  Mitarbeiter oldMitarbeiter;

  SortiertComboBox<Benutzer> benutzerComboBox;
  JTextField benutzerFeld;

  JTextField benutzernameFeld;

  JCheckBox bestandCheckBox;
  JCheckBox veranstaltungCheckBox;
  JCheckBox adminCheckBox;
  JCheckBox aktivCheckBox;
  
  JButton neuBenutzerButton;
  JButton passwortButton;
  JButton bearbeitenButton;

  BenutzerFactory benutzerFactory =
    Datenbank.getInstance().getBenutzerFactory();
  MitarbeiterFactory mitarbeiterFactory =
    Datenbank.getInstance().getMitarbeiterFactory();

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public MitarbeiterPanel(JFrame parentFrame) {
    hauptFenster = parentFrame;
    passwortDialog = new PasswortAendernDialog(parentFrame);

    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // erzeugt die GUI
  private void jbInit() throws Exception {
    //Benutzerpanel
    benutzerPanel = new BenutzerPanel(hauptFenster);
    benutzerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

    //DetailsPanel
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new GridBagLayout());

    JLabel benutzerLabel = new JLabel("Benutzer:");
    JLabel benutzernameLabel = new JLabel("Benutzername:");
    benutzernameFeld = new JTextField();
    Format<Benutzer> benutzerFormat = new Format<Benutzer>() {
      public String format(Benutzer ben) {
        if (ben == null) return null;
        return ben.getNameFormal();
      }
    };
    benutzerFeld = new JTextField();
    benutzerComboBox = new SortiertComboBox<Benutzer>(benutzerFormat);
    benutzerComboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        Benutzer gewaehlterBenutzer =
          (Benutzer) benutzerComboBox.getSelectedItem();
        benutzerPanel.setBenutzer(gewaehlterBenutzer);
        benutzerFeld.setText(gewaehlterBenutzer.getNameFormal());
      }
    });
    JComponentFormatierer.setDimension(
      benutzerComboBox,
      new Dimension(100, benutzerFeld.getPreferredSize().height));

    neuBenutzerButton = new JButton("Neuen Benutzer anlegen");
    neuBenutzerButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neuenBenutzerAnlegen();
      }
    });
    passwortButton = new JButton("Passwort ändern");
    passwortButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          passwortDialog.changeMitarbeiterPasswort(currentMitarbeiter, false);
          currentMitarbeiter.save();
        } catch (DatenbankzugriffException e1) {
          ErrorHandler.getInstance().handleException(e1, "Passwort konnte nicht " +
              "geändert werden!", false);
        }
      }
    });
    bearbeitenButton = new JButton("Bearbeiten");
    bearbeitenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        benutzerBearbeiten();
      }
    });

    benutzernameFeld = new JTextField();

    JComponentFormatierer.setDimension(passwortButton, new Dimension(30, 18));
    JComponentFormatierer.setDimension(bearbeitenButton, new Dimension(30, 18));
    JComponentFormatierer.setDimension(
      neuBenutzerButton,
      new Dimension(30, 18));

    detailsPanel.add(
      benutzerLabel,
      new GridBagConstraints(
        0,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 0, 5),
        0,
        0));
    detailsPanel.add(
      benutzerFeld,
      new GridBagConstraints(
        1,
        0,
        1,
        1,
        1.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0),
        0,
        0));
    detailsPanel.add(
      benutzerComboBox,
      new GridBagConstraints(
        1,
        0,
        1,
        1,
        1.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0),
        0,
        0));
    detailsPanel.add(
      benutzernameLabel,
      new GridBagConstraints(
        0,
        1,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 0, 3),
        0,
        0));
    detailsPanel.add(
      benutzernameFeld,
      new GridBagConstraints(
        1,
        1,
        1,
        1,
        1.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(3, 0, 0, 0),
        0,
        0));
    detailsPanel.add(
      neuBenutzerButton,
      new GridBagConstraints(
        0,
        4,
        2,
        1,
        0.0,
        0.0,
        GridBagConstraints.SOUTHEAST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0),
        0,
        0));
    detailsPanel.add(
      passwortButton,
      new GridBagConstraints(
        0,
        3,
        2,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(3, 0, 10, 0),
        0,
        0));
    detailsPanel.add(
      bearbeitenButton,
      new GridBagConstraints(
        0,
        5,
        2,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(3, 0, 0, 0),
        0,
        0));
    
    //BerechtigungPanel
    JPanel berechtigungPanel = new JPanel();
    berechtigungPanel.setBorder(
      BorderFactory.createCompoundBorder(
        new TitledBorder(
          new EtchedBorder(
            EtchedBorder.RAISED,
            new Color(248, 254, 255),
            new Color(121, 124, 136)),
          "Berechtigung"),
        BorderFactory.createEmptyBorder(0, 5, 0, 5)));
    berechtigungPanel.setLayout(new GridLayout(2, 2));

    bestandCheckBox = new JCheckBox("Bestand");
    veranstaltungCheckBox = new JCheckBox("Veranstaltungen");
    adminCheckBox = new JCheckBox("Administration");
    aktivCheckBox = new JCheckBox("Aktiv");
    
    berechtigungPanel.add(aktivCheckBox, null);
    berechtigungPanel.add(bestandCheckBox, null);
    berechtigungPanel.add(veranstaltungCheckBox, null);
    berechtigungPanel.add(adminCheckBox, null);

    //Mitarbeiterpanel
    JPanel mitarbeiterPanel = new JPanel();
    mitarbeiterPanel.setLayout(new GridBagLayout());

    mitarbeiterPanel.add(detailsPanel,
      new GridBagConstraints(0,  0, 1,  1, 1.0, 0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 10),
        0, 0));
    mitarbeiterPanel.add(
      berechtigungPanel,
      new GridBagConstraints(1,  0, 1,  1, 0.0,  0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0),
        0, 0));

    //alles zusammenbauen
    this.setLayout(new GridBagLayout());
    this.add(
      mitarbeiterPanel,
      new GridBagConstraints(
        0, 0,  1, 1,
        0.0, 1.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0),
        0, 0));
    this.add(
      benutzerPanel,
      new GridBagConstraints(
        0, 1, 1, 1,  1.0, 1.0,
        GridBagConstraints.SOUTHEAST,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0),
        0, 0));
  }

  /**
   * Initialisiert die Benutzerliste, die im Feld benutzerFeld dargestellt wird.
   *
   */
  private void initBenutzerListe() {
    BenutzerListe alleBenutzer = benutzerFactory.getAlleAktivenBenutzer();
    MitarbeiterListe alleMitarbeiter = mitarbeiterFactory.getAlleMitarbeiter();
    alleBenutzer.removeAll(alleMitarbeiter.convertToBenutzerListe());
            
    alleBenutzer.setSortierung(BenutzerListe.NachnameVornameSortierung);
    benutzerComboBox.setDaten(alleBenutzer);
    benutzerComboBox.setSelectedIndex(0);
    Benutzer gewaehlterBenutzer = (Benutzer) benutzerComboBox.getSelectedItem();
    benutzerPanel.setBenutzer(gewaehlterBenutzer);
    benutzerFeld.setText(gewaehlterBenutzer.getNameFormal());
  }

  /**
   * Zeigt den übergebenen Mitarbeiter an.
   * @param mitareiter der anzuzeigende Mitarbeiter
   *        <code>null</code> legt einen neuen Mitarbeiter an
   */
  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    currentMitarbeiter = mitarbeiter;
    if (currentMitarbeiter == null) {
      aktivCheckBox.setSelected(true);
      adminCheckBox.setSelected(false);
      veranstaltungCheckBox.setSelected(false);
      bestandCheckBox.setSelected(false);
      benutzernameFeld.setText("");
      initBenutzerListe();
      benutzerComboBox.setSelectedIndex(0);
    } else {
      benutzerPanel.setBenutzer(mitarbeiter.getBenutzer());
      aktivCheckBox.setSelected(mitarbeiter.istAktiv());
      adminCheckBox.setSelected(
        mitarbeiter.besitztBerechtigung(Mitarbeiter.BERECHTIGUNG_ADMIN));
      veranstaltungCheckBox.setSelected(
        mitarbeiter.besitztBerechtigung(
          Mitarbeiter.BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE));
      bestandCheckBox.setSelected(
        mitarbeiter.besitztBerechtigung(
          Mitarbeiter.BERECHTIGUNG_BESTAND_EINGABE));
      benutzernameFeld.setText(mitarbeiter.getMitarbeiterBenutzername());
      benutzerFeld.setText(mitarbeiter.getBenutzer().getNameFormal());
    }
    setVeraenderbar(istVeraenderbar);
  }

  void neuenBenutzerAnlegen() {
    Benutzer neuerBenutzer = benutzerFactory.erstelleNeu();
    benutzerPanel.setBenutzer(neuerBenutzer);
    benutzerFeld.setText("");
    benutzerBearbeiten();
  }

  void benutzerBearbeiten() {
    bearbeitenButton.setEnabled(false);
    neuBenutzerButton.setEnabled(false);
    benutzerPanel.setVeraenderbar(true);
    benutzerComboBox.setVisible(false);
    benutzerFeld.setVisible(true);
  }

  /**
   * Löscht den aktuellen Mitarbeiter nach einer Sicherheitsabfrage.
   * @return <code>true</code> gdw das Löschen stattfand war
   */
  public boolean loescheMitarbeiter() {
    try {
      int erg =
        JOptionPane.showConfirmDialog(
          hauptFenster,
          "Soll der Mitarbeiter "
            + currentMitarbeiter.getBenutzer().getName()
            + " wirklich gelöscht werden?",
          "Mitarbeiter löschen?",
          JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION)
        return false;

      currentMitarbeiter.loesche();
    } catch (DatenbankInkonsistenzException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        e.getMessage(),
        "Datenbank Inkonsistenz!",
        JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Mitarbeiter verändert werden darf. Dies ist wichtig, da abhängig davon 
   * einige Buttons verändert werden müssen.
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;

    //Eingabefelder
    benutzerFeld.setEditable(false);
    benutzerFeld.setVisible(!(veraenderbar && currentMitarbeiter == null));
    benutzerComboBox.setVisible(veraenderbar && currentMitarbeiter == null);
    benutzernameFeld.setEditable(veraenderbar);

    bestandCheckBox.setEnabled(veraenderbar);
    veranstaltungCheckBox.setEnabled(veraenderbar);
    adminCheckBox.setEnabled(veraenderbar);
    aktivCheckBox.setEnabled(veraenderbar);
    
    neuBenutzerButton.setEnabled(veraenderbar && currentMitarbeiter == null);
    passwortButton.setEnabled(!veraenderbar);
    bearbeitenButton.setEnabled(veraenderbar && currentMitarbeiter == null);
    benutzerPanel.setVeraenderbar(veraenderbar && currentMitarbeiter != null);

    if (veraenderbar)
      benutzerFeld.grabFocus();
  }

  public void aktualisiere() {
    //    Liste liste = Benutzer.getAlleOrte();
    //    ortFeld.setDaten(liste);
    //
    //    if (currentBenutzer != null) {
    //      currentBenutzer.reload();
    //      this.setBenutzer(currentBenutzer);
    //    }
  }

  /**
   * Liefert den aktuell angezeigten Mitarbeiter
   */
  public Mitarbeiter getMitarbeiter() {
    return currentMitarbeiter;
  }

  /**
   * Speichert die gemachten Änderungen am Mitarbeiter, falls dies möglich ist.
   * Ist dies nicht möglich, weil bspw. nicht alle Daten angegeben sind, 
   * wird eine entsprechende Fehlermeldung angezeigt.
   *
   * @return <code>true</code> gdw die Änderungen gespeichert werden konnten
   */
  public boolean saveChanges() {
    try {
      Benutzer aktuellerBenutzer = benutzerPanel.getBenutzer();
      Mitarbeiter neuerMitarbeiter;
      if (currentMitarbeiter == null) {
        neuerMitarbeiter = mitarbeiterFactory.erstelleNeu(aktuellerBenutzer);
      } else {
        neuerMitarbeiter = currentMitarbeiter;
      }

      neuerMitarbeiter.setAktiv(
          aktivCheckBox.isSelected());
      neuerMitarbeiter.setBerechtigung(
        Mitarbeiter.BERECHTIGUNG_ADMIN,
        adminCheckBox.isSelected());
      neuerMitarbeiter.setBerechtigung(
        Mitarbeiter.BERECHTIGUNG_BESTAND_EINGABE,
        bestandCheckBox.isSelected());
      neuerMitarbeiter.setBerechtigung(
        Mitarbeiter.BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE,
        veranstaltungCheckBox.isSelected());
      neuerMitarbeiter.setMitarbeiterBenutzername(benutzernameFeld.getText());

      benutzerPanel.saveChanges();
      neuerMitarbeiter.save();
      setMitarbeiter(neuerMitarbeiter);
      return true;
    } catch (MitarbeiterbenutzernameSchonVergebenException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Der Mitarbeiter konnte nicht "
          + "gespeichert\nwerden, da der Mitarbeiterbenutzername '"
          + currentMitarbeiter.getMitarbeiterBenutzername()
          + "'\n schon von "
          + e.getKonfliktMitarbeiter().getBenutzer().getName()
          + " benutzt wird!",
        "Doppelt verwendeter Mitarbeiterbenutzername!",
        JOptionPane.ERROR_MESSAGE);
    } catch (BenutzernameSchonVergebenException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Der Mitarbeiter konnte nicht "
          + "gespeichert\nwerden, da der Benutzername '"
          + currentMitarbeiter.getBenutzer().getBenutzername()
          + "'\n schon von "
          + e.getKonfliktBenutzer().getName()
          + " benutzt wird!",
        "Doppelt verwendeter Benutzername!",
        JOptionPane.ERROR_MESSAGE);
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Der Mitarbeiter konnte nicht gespeichert\n"
          + "werden, da nicht alle nötigen Daten eingegeben wurden!\nEs "
          + "muss mindestens der Vor- und Nachname des Mitarbeiters angegeben "
          + "werden.",
        "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    return false;
  }

}