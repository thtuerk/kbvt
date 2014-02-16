package de.oberbrechen.koeb.gui.standarddialoge;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.MitarbeiterFactory;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;

/**
 * Diese Klasse zeigt einen Dialog an, der die Auswahl eines Mitarbeiters
 * ermöglicht. Der gewählte Mitarbeiter muss sich über Eingabe seines
 * Passwortes autentifizieren.
 */
public class MitarbeiterAuswahlDialog {

  private JFrame main;
  private JPanel ausgabe;
  private JPasswordField passwortFeld;
  private SortiertComboBox<Mitarbeiter> mitarbeiterFeld;

  /**
   * Erzeugt eine neue Instanz, die den Dialog als Chield des übergebenen
   * Frames anzeigt.
   *
   * @param mainWindow das Hauptfenster
   */
  public MitarbeiterAuswahlDialog(JFrame mainWindow) {
    main = mainWindow;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Zeigt ein Dialogfeld zur Auswahl eines Mitarbeiters an, der die übergebene
   * Berechtigung besitzt. Die Berechtigungen stehen als
   * öffentliche Konstanten der Klasse Mitarbeiter zu Verfügung.
   * @param berechtigung die Berechtigung, die die Mitarbeiter besitzen muss
   * @return den gewählten Mitarbeiter oder <code>null</code>, falls kein
   *   Mitarbeiter gewählt wurde
   */
  public Mitarbeiter waehleMitarbeiter(int berechtigung) {
    ladeMitarbeiter(berechtigung);
    passwortFeld.setText(null);
    mitarbeiterFeld.grabFocus();
    SwingUtilities.updateComponentTreeUI(ausgabe);

    Mitarbeiter gewaehlterMitarbeiter;
    boolean mitarbeiterGewaehlt = false;
    do {
      int erg = JOptionPane.showConfirmDialog(main, ausgabe,"Mitarbeiter-Auswahl",
          JOptionPane.OK_CANCEL_OPTION);

      if (erg != JOptionPane.OK_OPTION) return null;
      gewaehlterMitarbeiter = (Mitarbeiter) mitarbeiterFeld.getSelectedItem();

      mitarbeiterGewaehlt = true;
      if (!gewaehlterMitarbeiter.checkMitarbeiterPasswort(
          new String(passwortFeld.getPassword()))) {

        mitarbeiterGewaehlt = false;
        JOptionPane.showMessageDialog(main, "Das angegebene Passwort ist für "+
          "den Mitarbeiter "+gewaehlterMitarbeiter.getBenutzer().getName()+
          " ungültig!", "Passwort ungültig!", JOptionPane.ERROR_MESSAGE);

      }
    } while (!mitarbeiterGewaehlt);

    return gewaehlterMitarbeiter;
  }


  /**
   * Läd alle Benutzer, die die übergebene Berechtigung besitzen aus der
   * Datenbank und setzt sie im mitarbeiterFeld.
   *
   * @param berechtigung die Berechtigung, die die Mitarbeiter besitzen sollen
   */
  private void ladeMitarbeiter(int berechtigung) {
    MitarbeiterFactory mitarbeiterFactory = 
        Datenbank.getInstance().getMitarbeiterFactory();
    MitarbeiterListe ausgewaehlteMitarbeiter = 
        mitarbeiterFactory.getAktiveMitarbeiterMitBerechtigung(berechtigung);
    ausgewaehlteMitarbeiter.setSortierung(
        BenutzerListe.NachnameVornameSortierung);
    
    //neuen Mitarbeiter anlegen, falls keiner existiert
    if (ausgewaehlteMitarbeiter.isEmpty()) {
      ausgewaehlteMitarbeiter.add(mitarbeiterFactory.erstelleStandardMitarbeiter());      
    }
        
    mitarbeiterFeld.setDaten(ausgewaehlteMitarbeiter);
  }

  private void jbInit() throws Exception {
    ausgabe = new JPanel();

    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    passwortFeld = new JPasswordField();
    Format<Mitarbeiter> benutzerFormat = new Format<Mitarbeiter>() {
      public String format (Mitarbeiter mit) {
        if (mit == null) return null;
        return mit.getBenutzer().getNameFormal();
      }
    };
    mitarbeiterFeld = new SortiertComboBox<Mitarbeiter>(benutzerFormat);

    jLabel1.setText("Mitarbeiter:");
    ausgabe.setLayout(new GridBagLayout());
    jLabel2.setText("Passwort:");
    ausgabe.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    ausgabe.add(mitarbeiterFeld,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    ausgabe.add(jLabel2,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    ausgabe.add(passwortFeld,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
  }
}