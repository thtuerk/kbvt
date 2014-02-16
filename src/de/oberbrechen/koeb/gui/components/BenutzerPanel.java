package de.oberbrechen.koeb.gui.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.standarddialoge.PasswortAendernDialog;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Benutzer-Daten angezeigt und
 * bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BenutzerPanel extends JPanel {

  protected static final long milliSekundenInJahr = 31536l * 1000000l;

  private JFrame hauptFenster;
  private Benutzer currentBenutzer;
  private Benutzer oldBenutzer;

  //Datumsformatierung
  private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
  private PasswortAendernDialog benutzerpasswortAendernDialog;

  // die Datenfelder
  private JTextField idFeld = new JTextField();
  private JTextField nachnameFeld = new JTextField();
  private JTextField adresseFeld = new JTextField();
  private JTextField anmeldedatumFeld = new JTextField();
  private SortiertComboBox<String> ortFeld = new SortiertComboBox<String>();
  private JTextField vornameFeld = new JTextField();
  private JTextField geburtsdatumFeld = new JTextField();
  private JTextField telFeld = new JTextField();
  private JTextField emailFeld = new JTextField();
  private JTextField alterFeld = new JTextField();
  private JTextField klasseFeld = new JTextField();
  private JTextField faxFeld = new JTextField();
  private JTextField benutzernameFeld = new JTextField();
  private JTextField passwortFeld = new JTextField();
  private JTextArea bemerkungenFeld = new JTextArea();
  private JButton benutzernameButton = new JButton();
  private JButton passwortAendernButton = new JButton();

  /**
   * Zeigt den übergebenen Benutzer an.
   * @param benutzer der anzuzeigende Benutzer
   */
  public void setBenutzer(Benutzer benutzer) {
    if (benutzer == null) throw new NullPointerException();

    currentBenutzer = benutzer;
    if (!benutzer.istNeu()) {
      idFeld.setText(Integer.toString(benutzer.getId()));
      oldBenutzer = benutzer;
    } else {
      idFeld.setText("");
    }

    nachnameFeld.setText(benutzer.getNachname());
    vornameFeld.setText(benutzer.getVorname());
    adresseFeld.setText(benutzer.getAdresse());
    ortFeld.setSelectedItem(benutzer.getOrt());
    klasseFeld.setText(benutzer.getKlasse());
    telFeld.setText(benutzer.getTel());
    faxFeld.setText(benutzer.getFax());
    emailFeld.setText(benutzer.getEMail());
    benutzernameFeld.setText(benutzer.getBenutzername());
    bemerkungenFeld.setText(benutzer.getBemerkungen());
    if (benutzer.istPasswortGesetzt())
      passwortFeld.setText("********");
    else
      passwortFeld.setText(null);

    anmeldedatumFeld.setText(formatDatum(benutzer.getAnmeldedatum()));
    geburtsdatumFeld.setText(formatDatum(benutzer.getGeburtsdatum()));
    checkGeburtsdatum();
  }

  /**
   * Löscht den aktuellen Benutzer nach einer Sicherheitsabfrage.
   * @return <code>true</code> gdw das Löschen stattfand war
   */
  public boolean loescheBenutzer() {
    try {
      int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll der Benutzer "+
      currentBenutzer.getName()+ " wirklich gelöscht werden?",
      "Benutzer löschen?", JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION) return false;

      currentBenutzer.loesche();
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
    nachnameFeld.setEditable(veraenderbar);
    vornameFeld.setEditable(veraenderbar);
    adresseFeld.setEditable(veraenderbar);
    ortFeld.setEditable(veraenderbar);
    ortFeld.setEnabled(veraenderbar);
    klasseFeld.setEditable(veraenderbar);
    telFeld.setEditable(veraenderbar);
    faxFeld.setEditable(veraenderbar);
    emailFeld.setEditable(veraenderbar);
    benutzernameFeld.setEditable(veraenderbar);
    passwortFeld.setEditable(false);
    bemerkungenFeld.setEditable(veraenderbar);
    benutzernameButton.setEnabled(veraenderbar);
    passwortAendernButton.setEnabled(veraenderbar);

    anmeldedatumFeld.setEditable(veraenderbar);
    geburtsdatumFeld.setEditable(veraenderbar);
    if (veraenderbar) nachnameFeld.grabFocus();
  }


  /**
   * Formatiert ein Datum als String.
   * @param datum das zu formatierende Datum
   */
  private String formatDatum(Date datum) {
    if (datum != null) {
      return(dateFormat.format(datum));
    } else {
      return null;
    }
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public BenutzerPanel(JFrame hauptFenster) {
    this.hauptFenster=hauptFenster;
    currentBenutzer = null;
    oldBenutzer = null;
    benutzerpasswortAendernDialog =
      new PasswortAendernDialog(hauptFenster);
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
    JLabel jLabel1 = new JLabel("Benutzer-ID:");
    JLabel jLabel2 = new JLabel("Nachname:");
    JLabel jLabel3 = new JLabel("Ort:");
    JLabel jLabel4 = new JLabel("Adresse:");
    JLabel jLabel5 = new JLabel("Vorname:");
    JLabel jLabel6 = new JLabel("Anmeldedatum:");
    JLabel jLabel7 = new JLabel("Geburtsdatum:");
    JLabel jLabel8 = new JLabel("Tel.:");
    JLabel jLabel9 = new JLabel("eMail:");
    JLabel jLabel10 = new JLabel("Alter:");
    JLabel jLabel11 = new JLabel("Klasse:");
    JLabel jLabel12 = new JLabel("Fax:");
    JLabel jLabel13 = new JLabel("Benutzername:");
    JLabel jLabel14 = new JLabel("Passwort:");
    JLabel jLabel15 = new JLabel("Bemerkungen:");

    this.setLayout(new GridBagLayout());
    idFeld.setEditable(false);
    alterFeld.setEditable(false);

    ortFeld.setBorder(null);
    JComponentFormatierer.setDimension(ortFeld, new Dimension(100, vornameFeld.getPreferredSize().height));
    ortFeld.setToolTipText("Wohnort des Benutzers");

    anmeldedatumFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkAnmeldedatum();
      }
    });
    geburtsdatumFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkGeburtsdatum();
      }
    });

    benutzernameButton.setText("zuweisen");
    benutzernameButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            benutzernameZuweisen();
          }
        }.start();
      }
    });
    passwortAendernButton.setText("ändern");
    passwortAendernButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            passwortAendern();
          }
        }.start();
      }
    });
    int buttonWidth = Math.max(benutzernameButton.getPreferredSize().width,
                               passwortAendernButton.getPreferredSize().width);
    int buttonHeight = benutzernameFeld.getPreferredSize().height;
    Dimension buttonDimension = new Dimension(buttonWidth, buttonHeight);
    JComponentFormatierer.setDimension(benutzernameButton, buttonDimension);
    JComponentFormatierer.setDimension(passwortAendernButton, buttonDimension);

    JScrollPane jScrollPane = new JScrollPane();
    jScrollPane.setMinimumSize(new Dimension(0, 40));
    jScrollPane.setPreferredSize(new Dimension(0, 40));
    bemerkungenFeld.setLineWrap(true);
    bemerkungenFeld.setWrapStyleWord(true);

    this.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(idFeld,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 20), 0, 0));
    this.add(jLabel6,    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(anmeldedatumFeld,     new GridBagConstraints(3, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel2,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(nachnameFeld,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 20), 0, 0));
    this.add(benutzernameFeld,    new GridBagConstraints(3, 6, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel5,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(vornameFeld,     new GridBagConstraints(3, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel4,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel8,      new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel3,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(adresseFeld,     new GridBagConstraints(1, 2, 4, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(ortFeld,     new GridBagConstraints(1, 3, 4, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel7,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(geburtsdatumFeld,    new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 20), 0, 0));
    this.add(jLabel11,    new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(klasseFeld,     new GridBagConstraints(3, 4, 2, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(telFeld,     new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 20), 0, 0));
    this.add(jLabel12,     new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(faxFeld,      new GridBagConstraints(3, 5, 2, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel9,    new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel14,    new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel13,     new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(emailFeld,    new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 20), 0, 0));
    this.add(jLabel10,    new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 1), 0, 0));
    this.add(alterFeld,    new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 20), 0, 0));
    this.add(passwortFeld,      new GridBagConstraints(3, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel15,     new GridBagConstraints(0, 8, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(benutzernameButton,          new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    this.add(passwortAendernButton,    new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    this.add(jScrollPane,        new GridBagConstraints(1, 8, 4, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane.getViewport().add(bemerkungenFeld, null);
  }

  /**
   * Läd die Orte neu aus der Datenbank
   */
  public void aktualisiere() {
    BenutzerFactory benutzerFactory =
      Datenbank.getInstance().getBenutzerFactory();
    Liste<String> liste = benutzerFactory.getAlleOrte();
    ortFeld.setDaten(liste);

    if (currentBenutzer != null) {
      try {
        currentBenutzer.reload();
        this.setBenutzer(currentBenutzer);
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
      }
    }
  }

  /**
   * Überprüft, ob der übergebene String wirklich ein gültiges
   * Datum repräsentiert. Ist dies nicht der Fall wird eine Fehlermeldung
   * angezeigt
   *
   * @param eintrag das Feld, in dem der zu testende Eintrag steht
   * @return das gepaarste Datum, falls das Anmeldedatum OK ist; null sonst
   */
  private Date checkDatum(JTextField eintrag) {
    String datumString = eintrag.getText();
    if (datumString == null || datumString.equals("")) return null;

    Date neuDatum;
    try {
      neuDatum = dateFormat.parse(datumString);
    } catch (ParseException e) {
      neuDatum = null;
      eintrag.setText(null);
      JOptionPane.showMessageDialog(hauptFenster, "Die Eingabe '"+
        datumString + "' kann\nnicht als Datum interpretiert\n"+
        "werden!\n\nDatumseingaben müssen in der\nForm 'tt.mm.jjjj' erfolgen.",
        "Ungültiges Datum!",
        JOptionPane.ERROR_MESSAGE);
    }
    eintrag.setText(formatDatum(neuDatum));
    return neuDatum;
  }

  /**
   * Überprüft, ob der Eintrag im Feld Anmeldedatum wirklich ein gültiges
   * Datum repräsentiert. Ist dies nicht der Fall wird eine Fehlermeldung
   * angezeigt
   *
   * @return das gepaarste Datum, falls das Anmeldedatum OK ist; null sonst
   */
  Date checkAnmeldedatum() {
    return checkDatum(anmeldedatumFeld);
  }

  /**
   * Überprüft, ob der Eintrag im Feld Geburtsdatum wirklich ein gültiges
   * Datum repräsentiert. Ist dies nicht der Fall wird eine Fehlermeldung
   * angezeigt. Außerdem wird die Altersanzeige aktualisiert.
   *
   * @return das gepaarste Datum, falls das Anmeldedatum OK ist; null sonst
   */
  Date checkGeburtsdatum() {
    Date neuDatum = checkDatum(geburtsdatumFeld);

    if (neuDatum == null) {
      alterFeld.setText(null);
    } else {
      long alterInMillisekunden =
        System.currentTimeMillis() - neuDatum.getTime();

      double alterInJahren = ((double) alterInMillisekunden) /
        milliSekundenInJahr;
      double alterInJahrenGerundet = ((double) Math.round(alterInJahren*10))/10;

      alterFeld.setText(Double.toString(alterInJahrenGerundet)+" Jahre");
    }

    return neuDatum;
  }

  /**
   * Liefert den aktuell angezeigten Benutzer
   */
  public Benutzer getBenutzer() {
    return currentBenutzer;
  }

  /**
   * Speichert die gemachten Änderungen am Benutzer, falls dies möglich ist. Ist
   * dies nicht möglich, weil bspw. nicht alle Daten angegeben sind, wird eine
   * entsprechende Fehlermeldung angezeigt.
   *
   * @return <code>true</code> gdw die Änderungen gespeichert werden konnten
   */
  public boolean saveChanges() {
    try {
      currentBenutzer.setNachname(nachnameFeld.getText());
      currentBenutzer.setVorname(vornameFeld.getText());
      currentBenutzer.setAdresse(adresseFeld.getText());
      currentBenutzer.setOrt((String) ortFeld.getSelectedItem());
      currentBenutzer.setTel(telFeld.getText());
      currentBenutzer.setFax(faxFeld.getText());
      currentBenutzer.setEMail(emailFeld.getText());
      currentBenutzer.setKlasse(klasseFeld.getText());
      currentBenutzer.setBenutzername(benutzernameFeld.getText());
      currentBenutzer.setBemerkungen(bemerkungenFeld.getText());
      currentBenutzer.setAnmeldedatum(checkAnmeldedatum());
      currentBenutzer.setGeburtsdatum(checkGeburtsdatum());
      currentBenutzer.save();

      if (currentBenutzer.getOrt() != null)
        ortFeld.addItem(currentBenutzer.getOrt());
      return true;
    } catch (BenutzernameSchonVergebenException e) {
      JOptionPane.showMessageDialog(hauptFenster, "Der Benutzer konnte nicht "+
        "gespeichert\nwerden, da der Benutzername '"+
          currentBenutzer.getBenutzername()+
        "'\n schon von "+e.getKonfliktBenutzer().getName()+" benutzt wird!",
        "Doppelt verwendeter Benutzername!",
        JOptionPane.ERROR_MESSAGE);
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, "Der Benutzer konnte nicht "+
        "gespeichert\nwerden, da nicht alle nötigen Daten eingegeben wurden!"+
        "\nEs muss mindestens der Vor- und Nachname des Benutzers angegeben "+
        "werden.", "Unvollständige Daten!",
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
      oldBenutzer.reload();
      setBenutzer(oldBenutzer);
      setVeraenderbar(false);
    } catch (DatenNichtGefundenException e) {
      return false;
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      return false;
    }
    return true;
  }

  /**
   * Weist dem Benutzer einen neuen, einmaligen Standardbenutzernamen zu
   */
  void benutzernameZuweisen() {
    currentBenutzer.setBenutzername(benutzernameFeld.getText());
    currentBenutzer.setNachname(nachnameFeld.getText());
    currentBenutzer.setVorname(vornameFeld.getText());
    if (currentBenutzer.istBenutzernameGesetzt()) {
      int erg = JOptionPane.showConfirmDialog(hauptFenster, "Es wird bereits "+
        "ein Benutzername verwendet. Soll dieser wirklich "+
        "überschrieben werden?", "Benutzernamen ändern",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
      if (erg != JOptionPane.YES_OPTION) return;
    }
    currentBenutzer.setStandardBenutzername();
    benutzernameFeld.setText(currentBenutzer.getBenutzername());
  }

  /**
   * Zeigt einen Dialog zum Ändern der aktuellen Benutzerpassworts an.
   */
  void passwortAendern() {
    benutzerpasswortAendernDialog.changeBenutzerPasswort(
      currentBenutzer, false);
    if (currentBenutzer.istPasswortGesetzt())
      passwortFeld.setText("********");
    else
      passwortFeld.setText(null);
  }
}