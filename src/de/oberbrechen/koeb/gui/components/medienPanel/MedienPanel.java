package de.oberbrechen.koeb.gui.components.medienPanel;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.EANListe;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.ListenAuswahlPanel;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.systematikListenAuswahlPanel.SystematikListenAuswahlPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.server.medienabfragen.Medienabfrage;
import de.oberbrechen.koeb.server.medienabfragen.MedienabfrageErgebnis;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Medien-Daten 
 * angezeigt und bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MedienPanel extends JPanel {

  private boolean istVeraenderbar;
  private String altesAusBestandEntferntDatum;
  private JFrame hauptFenster;
  private Medium currentMedium;
  private Medium oldMedium;

  //Datumsformatierung
  private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  // die Datenfelder
  private JTextField nrFeld;
  private JTextField titelFeld;
  private JTextField autorFeld;
  private SortiertComboBox<Medientyp> medientypFeld;
  private JTextField eingestelltSeitFeld;
  private JTextField medienAnzahlFeld;
  private JTextField ausBestandEntferntFeld;
  private JTextField isbnFeld;
  private JTextArea beschreibungFeld;
  private JButton ausBestandEntfernenButton;
  private JButton medienNrButton;
  private JButton isbnNachschlagenButton;
  private JTable eanTable;
  private EANTableModel eanTableModel;
  private ListenAuswahlPanel<Systematik> listenAuswahlPanel;
  
  /**
   * Zeigt das übergebene Medium an.
   * @param medium das anzuzeigende Medium
   */
  public void setMedium(Medium medium) {
    currentMedium = medium;
    if (medium == null) {
      nrFeld.setText(null);
      medientypFeld.setSelectedItem(null);
      titelFeld.setText(null);
      autorFeld.setText(null);
      eingestelltSeitFeld.setText(null);
      ausBestandEntferntFeld.setText(null);
      medienAnzahlFeld.setText(null);
      isbnFeld.setText(null);
      beschreibungFeld.setText(null);
      eanTableModel.setDaten(null);

      listenAuswahlPanel.setAuswahl(new SystematikListe());
      altesAusBestandEntferntDatum = null;
      return;
    }


    if (!medium.istNeu()) {
      oldMedium = medium;
    }

    nrFeld.setText(medium.getMedienNr());
    medientypFeld.setSelectedItem(medium.getMedientyp());
    titelFeld.setText(medium.getTitel());
    autorFeld.setText(medium.getAutor());

    if (medium.getEinstellungsdatum() != null) {
      eingestelltSeitFeld.setText(dateFormat.format(medium.getEinstellungsdatum()));
    } else {
      eingestelltSeitFeld.setText("");
    }

    if (medium.getEntfernungsdatum() != null) {
      ausBestandEntferntFeld.setText(dateFormat.format(medium.getEntfernungsdatum()));
    } else {
      ausBestandEntferntFeld.setText("");
    }

    medienAnzahlFeld.setText(Integer.toString(medium.getMedienAnzahl()));
    isbnFeld.setText(null);
    if (medium.getISBN() != null) isbnFeld.setText(medium.getISBN().getISBN());
    beschreibungFeld.setText(medium.getBeschreibung());

    listenAuswahlPanel.setAuswahl(medium.getSystematiken());
    eanTableModel.setDaten(medium.getEANs());
    altesAusBestandEntferntDatum = null;
    initButtons();
  }
  
  /**
   * Löscht das aktuelle Medium nach einer Sicherheitsabfrage.
   * @return <code>true</code> gdw das Löschen erfolgreich war
   */
  public boolean loescheMedium() {
    try {
      int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll das Medium "+
        currentMedium.getTitel() + " ("+currentMedium.getMedienNr()+") " +
        "wirklich gelöscht werden?\nEs ist meist sinnvoller, das Medium nur\n" +
        "aus dem Bestand zu entfernen!",
        "Medium löschen?", JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION) return false;

      currentMedium.loesche();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e,
        "Beim Löschen des Mediums wurde eine Datenbank-Inkonsistenz bemerkt.", 
        false);
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
    this.istVeraenderbar = veraenderbar;
    nrFeld.setEditable(veraenderbar);
    autorFeld.setEditable(veraenderbar);
    titelFeld.setEditable(veraenderbar);
    medienAnzahlFeld.setEditable(veraenderbar);
    medientypFeld.setEnabled(veraenderbar);
    eingestelltSeitFeld.setEditable(veraenderbar);
    ausBestandEntferntFeld.setEditable(veraenderbar);
    isbnFeld.setEditable(veraenderbar);
    beschreibungFeld.setEditable(veraenderbar);
    
    listenAuswahlPanel.setVeraenderbar(veraenderbar);
    eanTable.setEnabled(veraenderbar);
    eanTableModel.showNewItem(veraenderbar);
    
    if (veraenderbar) {
      nrFeld.grabFocus();      
    }
    
    initButtons();
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
   * Erzeugt ein MedienPanel, das im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem das Panel gehört
   */
  public MedienPanel(JFrame hauptFenster) {
    this.hauptFenster=hauptFenster;
    currentMedium = null;
    oldMedium = null;
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void initButtons() {
    if (ausBestandEntferntFeld.getText() == null || ausBestandEntferntFeld.getText().equals("")) {
      ausBestandEntfernenButton.setText("Entfernen");
    } else {
      ausBestandEntfernenButton.setText("Einstellen");
    }
    ausBestandEntfernenButton.setEnabled(istVeraenderbar);
        
    isbnNachschlagenButton.setEnabled(istVeraenderbar);      
        
    if (istVeraenderbar && 
      eingestelltSeitFeld.getText() != null && 
      !eingestelltSeitFeld.getText().equals("") && 
      medientypFeld.getSelectedItem() != null &&
      (nrFeld.getText() == null || nrFeld.getText().equals(""))) { 
        medienNrButton.setEnabled(true);
    } else {
      medienNrButton.setEnabled(false);
    }    
  }
  
  /**
   * Entfernt das aktuelle Medium aus dem Bestand. Dazu wird
   * das aktuelle Datum im Feld aus_Bestand_entfernt eingetragen.
   */
  void entferneAusBestand() {
    if (altesAusBestandEntferntDatum == null) {
      ausBestandEntferntFeld.setText(dateFormat.format(new Date()));
    } else {
      ausBestandEntferntFeld.setText(altesAusBestandEntferntDatum);  
    }
    initButtons();
  }

  /**
   * Stellt das aktuelle Medium wieder ein.
   */
  void stelleWiederInBestandEin() {
    if (ausBestandEntferntFeld.getText() == null || ausBestandEntferntFeld.getText().equals("")) {
      altesAusBestandEntferntDatum = null;
    } else {
      altesAusBestandEntferntDatum = ausBestandEntferntFeld.getText();
    }
    ausBestandEntferntFeld.setText(null);
    initButtons();
  }

  // erzeugt die GUI
  void jbInit() throws Exception {
    listenAuswahlPanel = new SystematikListenAuswahlPanel();
    listenAuswahlPanel.setMinimumSize(new Dimension(200, 50));
    
    //Alle wichtigen Objeke initialisieren
    nrFeld = new JTextField();
    titelFeld = new JTextField();
    autorFeld = new JTextField();
    medientypFeld = new SortiertComboBox<Medientyp>();
    eingestelltSeitFeld = new JTextField();
    medienAnzahlFeld = new JTextField();
    ausBestandEntferntFeld = new JTextField();
    isbnFeld = new JTextField();
    beschreibungFeld = new JTextArea();

    ausBestandEntfernenButton = new JButton("Entfernen");
    medienNrButton = new JButton("Vorschlagen");
    isbnNachschlagenButton = new JButton("Nachschlagen");
    
    //EANTablelle
    eanTableModel = new EANTableModel(hauptFenster);
    eanTable = new JTable();
    eanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ErweiterterTableCellRenderer.setzeErweitertesModell(eanTableModel, eanTable);
    eanTable.registerKeyboardAction( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeEAN();
      }}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
    
    JScrollPane eanScrollPane = new JScrollPane();
    eanScrollPane.setMinimumSize(new Dimension(100, 40));
    eanScrollPane.setPreferredSize(new Dimension(100, 40));
    eanScrollPane.getViewport().add(eanTable, null);
    
    //Labels
    JLabel jLabel1 = new JLabel("Medien-Nr:");
    JLabel jLabel2 = new JLabel("Titel:");
    JLabel jLabel4 = new JLabel("Autor:");
    JLabel jLabel6 = new JLabel("Medientyp:");
    JLabel jLabel7 = new JLabel("eingestellt seit:");
    JLabel jLabel8 = new JLabel("Medienanzahl:");
    JLabel jLabel11 = new JLabel("aus Bestand entfernt:");
    JLabel jLabel12 = new JLabel("ISBN:");
    JLabel jLabel15 = new JLabel("Beschreibung:");

    //Format-Checks
    eingestelltSeitFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkDatum(eingestelltSeitFeld);
        initButtons();
      }
    });
    ausBestandEntferntFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkDatum(ausBestandEntferntFeld);
        initButtons();
      }
    });
    medienAnzahlFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkMedienAnzahl();
      }
    });
    isbnFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkISBN();
      }
    });
    nrFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        initButtons();
      }
    });
    medientypFeld.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        initButtons();
      }
    });
    
    
    //Buttons
    ausBestandEntfernenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ausBestandEntferntFeld.getText() == null || ausBestandEntferntFeld.getText().equals("")) {
          entferneAusBestand();
        } else {
          stelleWiederInBestandEin();
        }
      }
    });
    medienNrButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            medienNrZuweisen();
          }
        }.start();
      }
    });        
    isbnNachschlagenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isbnNachschlagen();
      }
    });        
    
    //Formatierungen       
    nrFeld.setOpaque(true);
    MedientypFactory medientypFactory = 
      Datenbank.getInstance().getMedientypFactory();
    medientypFeld.setDaten(medientypFactory.getAlleMedientypen());

    int buttonWidth = Math.max(isbnNachschlagenButton.getPreferredSize().width,
      Math.max(medienNrButton.getPreferredSize().width,
      ausBestandEntfernenButton.getPreferredSize().width));
    int buttonHeight = isbnFeld.getPreferredSize().height;
    Dimension buttonDimension = new Dimension(buttonWidth, buttonHeight);
    JComponentFormatierer.setDimension(isbnNachschlagenButton, buttonDimension);
    JComponentFormatierer.setDimension(medienNrButton, buttonDimension);
    JComponentFormatierer.setDimension(ausBestandEntfernenButton, buttonDimension);
    JComponentFormatierer.setDimension(medientypFeld, buttonDimension);

    beschreibungFeld.setLineWrap(true);
    beschreibungFeld.setWrapStyleWord(true);
    JScrollPane beschreibungScrollPane = new JScrollPane();
    beschreibungScrollPane.setMinimumSize(new Dimension(0, 40));
    beschreibungScrollPane.setPreferredSize(new Dimension(0, 40));
    beschreibungScrollPane.getViewport().add(beschreibungFeld, null);

    JSplitPane systematikEANSplitPane = new JSplitPane();
    systematikEANSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    systematikEANSplitPane.setBorder(BorderFactory.createEmptyBorder());
    listenAuswahlPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,10));
    eanScrollPane.setBorder(BorderFactory.createCompoundBorder(
       BorderFactory.createEmptyBorder(10,10,0,0), eanScrollPane.getBorder()));
    systematikEANSplitPane.add(listenAuswahlPanel, JSplitPane.LEFT);
    systematikEANSplitPane.add(eanScrollPane, JSplitPane.RIGHT);
    systematikEANSplitPane.setResizeWeight(1);
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());
    mainPanel.add(jLabel1,           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(nrFeld,                   new GridBagConstraints(1, 0, 1, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(jLabel6,              new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 20, 5, 10), 0, 0));
    mainPanel.add(medientypFeld,              new GridBagConstraints(4, 0, 2, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(jLabel2,           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(titelFeld,              new GridBagConstraints(1, 1, 5, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(isbnFeld,                new GridBagConstraints(4, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(isbnNachschlagenButton,          new GridBagConstraints(5, 4, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    mainPanel.add(jLabel4,           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(jLabel8,              new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(autorFeld,              new GridBagConstraints(1, 2, 5, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(jLabel7,            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    mainPanel.add(eingestelltSeitFeld,                new GridBagConstraints(1, 3, 2, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(jLabel11,                 new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 20, 5, 10), 0, 0));
    mainPanel.add(ausBestandEntferntFeld,               new GridBagConstraints(4, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(medienAnzahlFeld,                new GridBagConstraints(1, 4, 2, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(jLabel12,                new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 20, 5, 0), 0, 0));
    mainPanel.add(jLabel15,               new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(beschreibungScrollPane,                new GridBagConstraints(1, 5, 5, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    mainPanel.add(ausBestandEntfernenButton,          new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    mainPanel.add(medienNrButton,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));

    JSplitPane mainSplitPane = new JSplitPane();
    mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    mainSplitPane.setBorder(BorderFactory.createEmptyBorder());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    systematikEANSplitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
/*
    eanScrollPane.setBorder(BorderFactory.createCompoundBorder(
       BorderFactory.createEmptyBorder(0,10,0,0), eanScrollPane.getBorder()));*/

    mainSplitPane.add(mainPanel, JSplitPane.TOP);
    mainSplitPane.add(systematikEANSplitPane, JSplitPane.BOTTOM);
    mainSplitPane.setResizeWeight(0.8);
    
    //alles Zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(mainSplitPane, BorderLayout.CENTER);

  }

  /**
   * Weist dem Medium eine neue Mediennr zu.
   */
  void medienNrZuweisen() {
    try {
      nrFeld.setText(Buecherei.getInstance().getStandardMedienNr(
        (Medientyp) medientypFeld.getSelectedItem(), 
        dateFormat.parse(eingestelltSeitFeld.getText())));
    } catch (ParseException e) {
      //darf nie auftreten, da beim Ändern der Inhalts von eingestelltSeitFeld
      //Format gecheckt wird
      e.printStackTrace();
      System.exit(1);
    }
    initButtons();
  }

  /**
   * Läd das Medium neu aus der Datenbank
   */
  public void aktualisiere() {
    if (currentMedium != null) {
      try {
        currentMedium.reload();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, "Medium konnte nicht "+
            "neu geladen werden!", false);
        currentMedium = null;
      }
      this.setMedium(currentMedium);
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
  Date checkDatum(JTextField eintrag) {
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


  void checkMedienAnzahl() {
    String anzahlString = medienAnzahlFeld.getText();
    
    boolean anzahlOK = true;
    if (anzahlString == null || anzahlString.equals("")) anzahlOK = false;
    
    if (anzahlOK) {
      int anzahl = 1;
      try {
        anzahl = Integer.parseInt(anzahlString);
        if (anzahl <= 0) anzahlOK = false;
      } catch (NumberFormatException e) {
        anzahlOK = false;
      }
    }

    if (!anzahlOK) {
      medienAnzahlFeld.setText("1");
      JOptionPane.showMessageDialog(hauptFenster, "Die Eingabe '"+
        anzahlString + "' kann\nnicht als positive Zahl interpretiert\n"+
        "werden!",
        "Ungültige Medienanzahl!",
        JOptionPane.ERROR_MESSAGE);
    }
  }

  void checkISBN() {
    String isbn = isbnFeld.getText();
    if (isbn == null || isbn.equals("")) return;
    
    String neuISBN = ISBN.checkISBN(isbn);
    if (neuISBN == null) {
      isbnFeld.setText(null);
      JOptionPane.showMessageDialog(hauptFenster, "Die Eingabe '"+
        isbn + "' kann\nnicht als ISBN interpretiert\n"+
        "werden!",
        "Ungültige ISBN!",
        JOptionPane.ERROR_MESSAGE);
    } else {
      isbnFeld.setText(neuISBN);      
    }
    initButtons();
  }

  /**
   * Liefert das aktuell angezeigte Medium
   */
  public Medium getMedium() {
    return currentMedium;
  }

  /**
   * Speichert die gemachten Änderungen am Benutzer, falls dies möglich ist. Ist
   * dies nicht möglich, weil bspw. nicht alle Daten angegeben sind, wird eine
   * entsprechende Fehlermeldung angezeigt.
   *
   * @return <code>true</code> gdw die Änderungen gespeichert werden konnten
   */
  public boolean saveChanges() {
    if (currentMedium == null) throw new NullPointerException();
    
    try {
      currentMedium.setMediennr(nrFeld.getText());
      currentMedium.setAutor(autorFeld.getText());
      currentMedium.setTitel(titelFeld.getText());
      currentMedium.setMedientyp((Medientyp) medientypFeld.getSelectedItem());
      currentMedium.setBeschreibung(beschreibungFeld.getText());
      currentMedium.setMedienAnzahl(Integer.parseInt(medienAnzahlFeld.getText()));
      currentMedium.setEANs((EANListe) eanTableModel.getDaten());
      
      if (isbnFeld.getText() != null && !isbnFeld.getText().equals("")) {
        ISBN isbn = new ISBN(isbnFeld.getText());
        currentMedium.setISBN(isbn);
      } else {
        currentMedium.setISBN(null);
      }
            
      if (eingestelltSeitFeld.getText() != null && !eingestelltSeitFeld.getText().equals("")) {
        currentMedium.setEinstellungsdatum(dateFormat.parse(eingestelltSeitFeld.getText()));
      } else {
        currentMedium.setEinstellungsdatum(null);        
      }
      
      if (ausBestandEntferntFeld.getText() != null && !ausBestandEntferntFeld.getText().equals("")) {
        currentMedium.setEntfernungsdatum(dateFormat.parse(ausBestandEntferntFeld.getText()));
      } else {
        currentMedium.setEntfernungsdatum(null);
      }
      
      currentMedium.setSystematiken(
        (SystematikListe) listenAuswahlPanel.getAuswahl());      
      currentMedium.save();
      return true;
    } catch (MedienNrSchonVergebenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Ungültige Mediennr.!",
        JOptionPane.ERROR_MESSAGE);
    } catch (EANSchonVergebenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
        "Ungültige EAN!",
        JOptionPane.ERROR_MESSAGE);
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(), 
        "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);
    } catch (ParseException e) {
      ErrorHandler.getInstance().handleException(e, false);
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
      if (oldMedium != null) {
        try {
          oldMedium.reload();
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Medium konnte nicht "+
              "erneut geladen werden!", false);
          oldMedium = null;
        }
      }
      setMedium(oldMedium);
      setVeraenderbar(false);
    } catch (DatenNichtGefundenException e) {
      return false;
    }
    return true;
  }
  
  /**
   * Löscht die in der Tabelle markierte EAN 
   */
  protected void removeEAN() {
    int selectedRow = eanTable.getSelectedRow();
    if (selectedRow != -1) { 
      eanTableModel.remove(selectedRow);  
      initButtons();
    }
  }
  
  protected void isbnNachschlagen() {  
    if (isbnFeld.getText() == null || isbnFeld.getText().equals("")) return;
    
    String isbnString = isbnFeld.getText();
    ISBN isbn = new ISBN(isbnString);   
    MedienabfrageErgebnis erg = Medienabfrage.getInstance().isbnNachschlagenMitTimeout(isbn);
    if (erg != null && erg.enthaeltDaten()) {
      titelFeld.setText(erg.getTitel());
      autorFeld.setText(erg.getAutor());
      if (erg.getBeschreibung() != null) beschreibungFeld.setText(erg.getBeschreibung());      
    } else {
      JOptionPane.showMessageDialog(hauptFenster, "Das Nachschlagen der ISBN-Nr. '"+
          isbn.getISBN() + "' schlug\nfehl! Keine Daten gefunden!",
          "Keine Daten gefunden!",
          JOptionPane.WARNING_MESSAGE);      
    }
  }
}