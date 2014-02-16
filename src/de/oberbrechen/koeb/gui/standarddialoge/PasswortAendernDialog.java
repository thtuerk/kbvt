package de.oberbrechen.koeb.gui.standarddialoge;

import de.oberbrechen.koeb.datenbankzugriff.*;
import javax.swing.*;
import java.awt.*;

/**
 * Diese Klasse zeigt einen Dialog an, der die Änderung des Passwortes eines
 * Benutzers oder eines Mitarbeiters ermöglicht.
 */
public class PasswortAendernDialog {

  private JFrame main;
  private JPanel ausgabe;
  private JTextField altesPasswortFeld;
  private JTextField neuesPasswortFeld;
  private JTextField bestaetigtesPasswort;
  private JLabel altesPasswortLabel;

  /**
   * Erzeugt eine neue Instanz, die den Dialog als Chield des übergebenen
   * Frames anzeigt.
   *
   * @param mainWindow das Hauptfenster
   */
  public PasswortAendernDialog(JFrame mainWindow) {
    main = mainWindow;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Initialisiert den Dialog, d.h. setzt alle Einträge zurück und bestimmt in
   * Abhängigkeit des Parameters checkOldPasswort, ob das Feld für die Eingabe
   * des alten Passworts angezeigt werden soll.
   * @param checkOldPasswort bestimmt, ob das Feld für die Eingabe
   *   des alten Passworts angezeigt werden soll
   */
  private void init(boolean checkOldPasswort) {
    SwingUtilities.updateComponentTreeUI(ausgabe);
    bestaetigtesPasswort.setText(null);
    neuesPasswortFeld.setText(null);
    altesPasswortFeld.setText(null);
    neuesPasswortFeld.grabFocus();
    altesPasswortFeld.setVisible(checkOldPasswort);
    altesPasswortLabel.setVisible(checkOldPasswort);

    if (checkOldPasswort) {
      altesPasswortFeld.grabFocus();
    } else {
      neuesPasswortFeld.grabFocus();
    }
  }

  /**
   * Ändert das Passwort des übergebenen Benutzers
   * @param benutzer der Benutzer, dessen Passwort geändert werden soll.
   * @param checkOldPasswort bestimmt, ob vor dem Ändern das
   *   bisherige Passwort abgefragt werden soll
   */
  public void changeBenutzerPasswort(Benutzer benutzer,
    boolean checkOldPasswort) {

    init(checkOldPasswort);

    int erg = JOptionPane.showConfirmDialog(main, ausgabe,"Passwort für "+
      benutzer.getName()+" ändern", JOptionPane.OK_CANCEL_OPTION);
    if (erg != JOptionPane.OK_OPTION) return;

    if (checkOldPasswort &&
      !benutzer.checkPasswort(altesPasswortFeld.getText())) {

      JOptionPane.showMessageDialog(main, "Das Passwort kann nicht geändert "+
        "werden, da das alte Passwort falsch eingegeben wurde.",
        "Altes Passwort ungültig!", JOptionPane.ERROR_MESSAGE);

      return;
    }

    if (!neuesPasswortFeld.getText().equals(bestaetigtesPasswort.getText())) {
      JOptionPane.showMessageDialog(main, "Das Passwort wurde nicht geändert, "+
        "da sich das bestätigte Passwort unterschied.",
        "Bestätiges Passwort verschieden!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (neuesPasswortFeld.getText() == null ||
        neuesPasswortFeld.getText().equals("")) {
      erg = JOptionPane.showConfirmDialog(main, "Soll das Passwort "+
        "deaktiviert werden?", "Passwort wirklich deaktivieren",
        JOptionPane.YES_NO_OPTION);

      if (erg == JOptionPane.YES_OPTION) benutzer.setPasswort(null);
      return;
    }

    benutzer.setPasswort(neuesPasswortFeld.getText());
  }

  /**
   * Ändert das Passwort des übergebenen Mitarbeiters
   * @param mitarbeiter der Mitarbeiter, dessen Passwort geändert werden soll.
   * @param ueberpruefeAltesPasswort bestimmt, ob vor dem Ändern das
   *   bisherige Passwort abgefragt werden soll
   */
  public void changeMitarbeiterPasswort(Mitarbeiter mitarbeiter,
    boolean checkOldPasswort) {

    init(checkOldPasswort);

    int erg = JOptionPane.showConfirmDialog(main, ausgabe,"Mitarbeiterpasswort "+
      "von "+mitarbeiter.getBenutzer().getName()+" ändern", 
      JOptionPane.OK_CANCEL_OPTION);

    if (erg != JOptionPane.OK_OPTION) return;

    if (checkOldPasswort &&
      !mitarbeiter.checkMitarbeiterPasswort(altesPasswortFeld.getText())) {

      JOptionPane.showMessageDialog(main, "Das Passwort kann nicht geändert "+
        "werden, da das alte Passwort falsch eingegeben wurde.",
        "Altes Passwort ungültig!", JOptionPane.ERROR_MESSAGE);

      return;
    }

    if (!neuesPasswortFeld.getText().equals(bestaetigtesPasswort.getText())) {
      JOptionPane.showMessageDialog(main, "Das Passwort wurde nicht geändert, "+
        "da sich das bestätigte Passwort unterschied.",
        "Bestätiges Passwort verschieden!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (neuesPasswortFeld.getText() == null ||
        neuesPasswortFeld.getText().equals("")) {
      erg = JOptionPane.showConfirmDialog(main, "Soll das Passwort "+
        "deaktiviert werden?", "Passwort wirklich deaktivieren",
        JOptionPane.YES_NO_OPTION);

      if (erg != JOptionPane.YES_OPTION) return;
    }

    mitarbeiter.setMitarbeiterPasswort(neuesPasswortFeld.getText());
  }

  private void jbInit() throws Exception {
    altesPasswortLabel = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();

    altesPasswortFeld = new JPasswordField();
    neuesPasswortFeld = new JPasswordField();
    bestaetigtesPasswort = new JPasswordField();
    ausgabe = new JPanel();
    ausgabe.setLayout(new GridBagLayout());

    altesPasswortLabel.setText("Bisheriges Passwort:");
    jLabel2.setText("Neues Passwort:");
    jLabel3.setText("Bestätigtes Passwort:");

    altesPasswortFeld.setPreferredSize(new Dimension(150, 21));

    neuesPasswortFeld.setPreferredSize(new Dimension(150, 21));
    bestaetigtesPasswort.setPreferredSize(new Dimension(150, 21));
    ausgabe.add(altesPasswortLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    ausgabe.add(altesPasswortFeld,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));
    ausgabe.add(jLabel2,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    ausgabe.add(neuesPasswortFeld,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));
    ausgabe.add(jLabel3,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    ausgabe.add(bestaetigtesPasswort,    new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));
  }

}