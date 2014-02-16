package de.oberbrechen.koeb.gui.components;

import java.awt.*;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Veranstaltunsgruppen-Daten angezeigt und
 * bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class VeranstaltungsgruppePanel extends JPanel {

  private JFrame hauptFenster;
  private Veranstaltungsgruppe currentGruppe;
  private Veranstaltungsgruppe oldGruppe;

  // die Datenfelder
  private JTextField nameFeld = new JTextField();
  private JTextField kurzNameFeld = new JTextField();
  private JTextField homeDirFeld = new JTextField();
  private JTextArea beschreibungFeld = new JTextArea();

  /**
   * Zeigt die übergebene Veranstaltungsgruppe an.
   * @param veranstaltungsgruppe die anzuzeigende Gruppe
   */
  public void setVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
    currentGruppe = veranstaltungsgruppe;
    if (veranstaltungsgruppe!= null && !veranstaltungsgruppe.istNeu()) {
      oldGruppe = veranstaltungsgruppe;
    }

    if (currentGruppe != null) {
      nameFeld.setText(currentGruppe.getName());
      kurzNameFeld.setText(currentGruppe.getKurzName());
      homeDirFeld.setText(currentGruppe.getHomeDir());
      beschreibungFeld.setText(currentGruppe.getBeschreibung());
    } else {
      nameFeld.setText(null);
      kurzNameFeld.setText(null);
      homeDirFeld.setText(null);
      beschreibungFeld.setText(null);      
    }
  }

  /**
   * Löscht die aktuelle Veranstaltungsgruppe nach einer Sicherheitsabfrage.
   * @return <code>true</code> gdw das Löschen stattfand war
   */
  public boolean loescheVeranstaltungsgruppe() {
    try {
      int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll die " +
        "Veranstaltungsgruppe '"+currentGruppe.getName()+ "' wirklich gelöscht werden?",
      "Veranstaltungsgruppe löschen?", JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION) return false;

      currentGruppe.loesche();
    } catch (DatenbankInkonsistenzException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Datenbank Inkonsistenz!", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob die aktuell angezeigte
   * Veranstaltungsgruppe verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Veraa gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    //Eingabefelder
    nameFeld.setEditable(veraenderbar);
    kurzNameFeld.setEditable(veraenderbar);
    homeDirFeld.setEditable(veraenderbar);
    beschreibungFeld.setEditable(veraenderbar);
    if (veraenderbar) nameFeld.grabFocus();
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public VeranstaltungsgruppePanel(JFrame hauptFenster) {
    this.hauptFenster=hauptFenster;
    currentGruppe = null;
    oldGruppe = null;
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  // erzeugt die GUI
  void jbInit() throws Exception {
    JLabel jLabel1 = new JLabel("Name:");
    JLabel jLabel2 = new JLabel("Kurzname:");
    JLabel jLabel3 = new JLabel("Home-Dir:");
    JLabel jLabel4 = new JLabel("Beschreibung:");

    this.setLayout(new GridBagLayout());

    JScrollPane jScrollPane = new JScrollPane();
    jScrollPane.setMinimumSize(new Dimension(0, 40));
    jScrollPane.setPreferredSize(new Dimension(0, 40));
    beschreibungFeld.setLineWrap(true);
    beschreibungFeld.setWrapStyleWord(true);

    this.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel2,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel4,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    this.add(jLabel3,        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(nameFeld,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(kurzNameFeld,      new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(homeDirFeld,      new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jScrollPane,          new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane.getViewport().add(beschreibungFeld, null);
  }

  public void aktualisiere() {
    if (currentGruppe != null) {
      try {
        currentGruppe.reload();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
        currentGruppe = null;
      }
      this.setVeranstaltungsgruppe(currentGruppe);
    }
  }

  /**
   * Liefert die aktuell angezeigte Veranstaltungsgruppe
   */
  public Veranstaltungsgruppe getVeranstaltungsgruppe() {
    return currentGruppe;
  }

  /**
   * Speichert die gemachten Änderungen an der aktuellen Veranstaltungsgruppe,
   * falls dies möglich ist. Ist
   * dies nicht möglich, weil bspw. nicht alle Daten angegeben sind, wird eine
   * entsprechende Fehlermeldung angezeigt.
   *
   * @return <code>true</code> gdw die Änderungen gespeichert werden konnten
   */
  public boolean saveChanges() {
    try {
      currentGruppe.setName(nameFeld.getText());
      currentGruppe.setKurzName(kurzNameFeld.getText());
      currentGruppe.setHomeDir(homeDirFeld.getText());
      currentGruppe.setBeschreibung(beschreibungFeld.getText());
      currentGruppe.save();

      return true;
    } catch (VeranstaltungsgruppeSchonVergebenException e) {
      JOptionPane.showMessageDialog(hauptFenster, "Die Veranstaltungsgruppe konnte nicht "+
        "gespeichert\nwerden, da der Name '"+
          currentGruppe.getName()+
        "'\n schon benutzt wird!",
        "Doppelt verwendeter Veranstaltungsgruppenname!",
        JOptionPane.ERROR_MESSAGE);
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    return false;
  }

  /**
   * Verwirft die aktuellen Änderungen. Dies kann eventuell nicht möglich sein.
   *
   * @return <code>true</code> gdw die Aenderungen rueckgaengig
   *   gemacht werden konnten.
   */
  public boolean aenderungenVerwerfen() {
    try {
      if (oldGruppe != null) oldGruppe.reload();
      setVeranstaltungsgruppe(oldGruppe);
      setVeraenderbar(false);
      return true;
    } catch (DatenNichtGefundenException e) {
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    return false;
  }
}