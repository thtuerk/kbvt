package de.oberbrechen.koeb.gui.components;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Systematik-Daten angezeigt und
 * bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class SystematikPanel extends JPanel {

  private JFrame hauptFenster;
  private Systematik currentSystematik;
  private Systematik oldSystematik;

  
  // die Datenfelder
  private JTextField nameFeld = new JTextField();
  private SortiertComboBox<Systematik> spezialisiertFeld = new SortiertComboBox<Systematik>();
  private JTextArea beschreibungFeld = new JTextArea();
  private JButton resetButton;
  
  /**
   * Zeigt die übergebenen Systematik an.
   * @param systematik die anzuzeigende Systematik
   */
  public void setSystematik(Systematik systematik) {
    currentSystematik = systematik;
    if (systematik == null) {
      nameFeld.setText(null);
      spezialisiertFeld.setSelectedItem(null);
      beschreibungFeld.setText(null);
      return;
    }
    
    if (!systematik.istNeu()) {
      oldSystematik = systematik;
    }

    nameFeld.setText(systematik.getName());
    beschreibungFeld.setText(systematik.getBeschreibung());
    try {
      spezialisiertFeld.setSelectedItem(systematik.getDirekteObersystematik());
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      setSystematik(null);
    }
  }

  /**
   * Löscht die aktuellen Systematik nach einer Sicherheitsabfrage.
   * @return <code>true</code> gdw das Löschen stattfand war
   */
  public boolean loescheSystematik() {
    try {
      int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll die "+
      "Systematik " +currentSystematik.getName()+ " wirklich gelöscht werden?",
      "Systematik löschen?", JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION) return false;

      currentSystematik.loesche();
    } catch (DatenbankInkonsistenzException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Datenbank Inkonsistenz!", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    //Eingabefelder
    nameFeld.setEditable(veraenderbar);
    spezialisiertFeld.setEnabled(veraenderbar);
    beschreibungFeld.setEditable(veraenderbar);
    resetButton.setEnabled(veraenderbar);
    
    if (veraenderbar) nameFeld.grabFocus();
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public SystematikPanel(JFrame hauptFenster) {
    this.hauptFenster=hauptFenster;
    currentSystematik = null;
    oldSystematik = null;
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
    JLabel jLabel1 = new JLabel("Systematik:");
    JLabel jLabel2 = new JLabel("Obersystematik:");
    JLabel jLabel3 = new JLabel("Beschreibung:");

    this.setLayout(new GridBagLayout());

    JScrollPane jScrollPane = new JScrollPane();
    jScrollPane.setMinimumSize(new Dimension(0, 40));
    jScrollPane.setPreferredSize(new Dimension(0, 40));
    beschreibungFeld.setLineWrap(true);
    beschreibungFeld.setWrapStyleWord(true);
    spezialisiertFeld.setEditable(false);

    resetButton = new JButton("Zurücksetzen");
    resetButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        spezialisiertFeld.setSelectedItem(null);
      }
    });
    
    this.add(jLabel1,    new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
    this.add(jLabel2,    new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
    this.add(jLabel3,    new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
    this.add(nameFeld,    new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(spezialisiertFeld,     new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(resetButton,     new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jScrollPane,        new GridBagConstraints(0, 6, 2, 1, 1.0, 1.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane.getViewport().add(beschreibungFeld, null);
  }

  public void aktualisiere() {
    SystematikListe systematiken =
      Datenbank.getInstance().getSystematikFactory().getAlleSystematiken();
    systematiken.setSortierung(SystematikListe.alphabetischeSortierung);
    spezialisiertFeld.setDaten(systematiken);
    
    if (currentSystematik != null) {
      try {
        currentSystematik.reload();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
        currentSystematik = null;
      }
      this.setSystematik(currentSystematik);            
    }
  }

  /**
   * Liefert die aktuell angezeigte Systematik
   */
  public Systematik getSystematik() {
    return currentSystematik;
  }

  /**
   * Speichert die gemachten Änderungen an der Systematik, falls dies möglich ist. Ist
   * dies nicht möglich, weil bspw. nicht alle Daten angegeben sind, wird eine
   * entsprechende Fehlermeldung angezeigt.
   *
   * @return <code>true</code> gdw die Änderungen gespeichert werden konnten
   */
  public boolean saveChanges() {
    try {
      currentSystematik.setName(nameFeld.getText());
      currentSystematik.setBeschreibung(beschreibungFeld.getText());
      currentSystematik.setDirekteObersystematik((Systematik) spezialisiertFeld.getSelectedItem());
      currentSystematik.save();
      return true;

    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, "Die Systematik konnte nicht "+
        "gespeichert\nwerden, da nicht alle nötigen Daten eingegeben wurden!"+
        "\nEs muss mindestens der Name der Systematik angegeben "+
        "werden.", "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);
    } catch (UnpassendeObersystematikException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(), 
          "Unpassende Obersystematik!",
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
      if (oldSystematik != null) oldSystematik.reload();
      setSystematik(oldSystematik);
      setVeraenderbar(false);
      return true;
    } catch (DatenNichtGefundenException e) {
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    return false;
  }

  public void addSpezialisiertListe(Systematik systematik) {
    ((SortiertMutableComboBoxModel) spezialisiertFeld.getModel()).
      addElement(systematik);
  }

  public void removeSpezialisiertListe(Systematik systematik) {
    ((SortiertMutableComboBoxModel) spezialisiertFeld.getModel()).
      removeElement(systematik);
  } 
}