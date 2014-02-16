package de.oberbrechen.koeb.gui.components.veranstaltungenPanel;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.standarddialoge.DatumAuswahlDialog;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Veranstaltungen
 * angezeigt und bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class VeranstaltungenPanel extends JPanel {

  private boolean terminWirdGeradeGecheckt;
  private boolean terminWirdGeradeGeupdated;
  public static DecimalFormat waehrungsFormat = new DecimalFormat("0.00");
  public static SimpleDateFormat terminFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
  public static SimpleDateFormat terminFormatLocker = new SimpleDateFormat("d.M.y H:m");
  public static SimpleDateFormat terminFormatDatum = new SimpleDateFormat("d.M.y");
  public static SimpleDateFormat terminFormatDatumLocker = new SimpleDateFormat("d.M.y");

  private DatumAuswahlDialog datumAuswahlDialog;
  private JButton endeAendernButton;
  private JButton beginnAendernButton;
  private JButton zeitSetzenButton;
  private JButton terminHinzufuegenButton;
  private JButton terminLoeschenButton;
  private JTable terminTabelle;
  private TerminTableModel terminModell;
  private boolean istVeraenderbar;
  private JFrame hauptFenster;
  private Veranstaltung aktuelleVeranstaltung;
  private Veranstaltung oldVeranstaltung;
  private Termin aktuellerTermin;
  private Date letzterTerminBeginn;
  private int aktuellerTerminIndex;
  private boolean keinCheck;
  
  // die Datenfelder
  private JTextField titelFeld;
  private JTextField kurzTitelFeld;
  private JTextField ansprechpartnerFeld;
  private JTextArea beschreibungFeld;
  private JTextField kostenFeld;
  private JTextField bezugsgruppeFeld;
  private JTextField waehrungFeld;
  private SortiertComboBox<Veranstaltungsgruppe> veranstaltungsgruppeFeld;
  private JTextField maximaleTeilnehmeranzahlFeld;
  private JCheckBox anmeldungErforderlichBox;
  private JTextField beginnFeld;
  private JTextField endeFeld;
  private SortiertComboBox<Termin> zeitFeld;

  /**
   * Zeigt die übergebene Veranstaltung an.
   * @param veranstaltung die anzuzeigende Veranstaltung
   */
  public void setVeranstaltung(Veranstaltung veranstaltung) {
    terminWirdGeradeGecheckt = true;

    aktuelleVeranstaltung = veranstaltung;
    if (veranstaltung == null) {
      keinCheck = true;
      titelFeld.setText(null);
      kurzTitelFeld.setText(null);
      ansprechpartnerFeld.setText(null);
      beschreibungFeld.setText(null);
      kostenFeld.setText(null);
      maximaleTeilnehmeranzahlFeld.setText(null);
      anmeldungErforderlichBox.setSelected(false);
      bezugsgruppeFeld.setText(null);
      waehrungFeld.setText(null);
      veranstaltungsgruppeFeld.setSelectedItem(null);
      terminModell.setDaten(new TerminListe());
    } else {
      keinCheck = false;
      if (!veranstaltung.istNeu()) {
        oldVeranstaltung = veranstaltung;
      }
      titelFeld.setText(veranstaltung.getTitel());
      kurzTitelFeld.setText(veranstaltung.getKurzTitel());
      ansprechpartnerFeld.setText(veranstaltung.getAnsprechpartner());
      beschreibungFeld.setText(veranstaltung.getBeschreibung());
      kostenFeld.setText(waehrungsFormat.format(veranstaltung.getKosten()));
      maximaleTeilnehmeranzahlFeld.setText(
        Integer.toString(veranstaltung.getMaximaleTeilnehmerAnzahl()));
      anmeldungErforderlichBox.setSelected(
        veranstaltung.istAnmeldungErforderlich());
      bezugsgruppeFeld.setText(veranstaltung.getBezugsgruppe());
      waehrungFeld.setText(veranstaltung.getWaehrung());
      veranstaltungsgruppeFeld.setSelectedItem(
        veranstaltung.getVeranstaltungsgruppe());
      terminModell.setDaten(aktuelleVeranstaltung.getTermine().deepCopy());
    }
    terminWirdGeradeGecheckt = false;
  }
  
  /**
   * Liest die Änderungen am aktuellen Termin
   */
  private void updateTermin() {
    if (aktuellerTermin == null || terminWirdGeradeGeupdated) return;
    
    terminWirdGeradeGeupdated = true;
    boolean setzeZeit = false;
    try {
      Date beginn = null;
      Date ende = null;    

      String beginnString = beginnFeld.getText();
      if (beginnString != null && !beginnString.trim().equals("")) {
        try {
          beginn = terminFormatLocker.parse(beginnString);
        } catch (ParseException e) {
           beginn = terminFormatDatum.parse(beginnString);
           setzeZeit = true;
        }        
      }
      
      String endeString = endeFeld.getText();
      if (endeString != null && !endeString.trim().equals("")) {
        ende = terminFormat.parse(endeString);
      }

      aktuellerTermin.setBeginn(beginn);
      aktuellerTermin.setEnde(ende);
    } catch (ParseException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Ein Termin muss im Format 'tt.mm.jjjj hh:mm' eingegeben werden.",
        "Ungültiger Termin!",
        JOptionPane.ERROR_MESSAGE);
    }
    
    setTermin(aktuellerTermin);
    terminModell.fireTableRowsUpdated(aktuellerTerminIndex, 
      aktuellerTerminIndex);
    terminWirdGeradeGeupdated = false;
    if (setzeZeit) setzeZeit();
  }
  
  
  /**
   * Löscht die aktuelle Veranstaltung nach einer Sicherheitsabfrage.
   * @return <code>true</code> gdw das Löschen erfolgreich war
   */
  public boolean loescheVeranstaltung() {
    try {
      int erg =
        JOptionPane.showConfirmDialog(
          hauptFenster,
          "Soll die Veranstaltung "
            + aktuelleVeranstaltung.getTitel()
            + " wirklich gelöscht werden?",
          "Veranstaltung löschen?",
          JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION)
        return false;

      aktuelleVeranstaltung.loesche();
    } catch (DatenbankInkonsistenzException e) {
      JOptionPane.showMessageDialog(hauptFenster, 
          e.getLocalizedMessage(),
          "Löschen nicht möglich!",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob die aktuell angezeigte
   * Veranstaltung verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param veraenderbar ist die Veranstaltung veraenderbar oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    //Eingabefelder
    this.istVeraenderbar = veraenderbar;
    titelFeld.setEditable(veraenderbar);
    kurzTitelFeld.setEditable(veraenderbar);
    ansprechpartnerFeld.setEditable(veraenderbar);
    beschreibungFeld.setEditable(veraenderbar);
    kostenFeld.setEditable(veraenderbar);
    maximaleTeilnehmeranzahlFeld.setEditable(veraenderbar);
    anmeldungErforderlichBox.setEnabled(veraenderbar);
    bezugsgruppeFeld.setEditable(veraenderbar);
    waehrungFeld.setEditable(veraenderbar);
    veranstaltungsgruppeFeld.setEnabled(veraenderbar);
    terminTabelle.setEnabled(veraenderbar);
    setTerminVeraenderbar(veraenderbar);

    if (!veraenderbar) {
      terminTabelle.clearSelection();
    } else {    
      if (terminTabelle.getSelectedRow() == -1 && 
          terminModell.getRowCount() > 0)
        terminTabelle.getSelectionModel().setSelectionInterval(0,0);      
      titelFeld.grabFocus();
    }
  }

  private void setTerminVeraenderbar(boolean veraenderbar) {
    boolean editierbar = veraenderbar && aktuellerTermin != null;
    endeFeld.setEditable(editierbar);
    beginnFeld.setEditable(editierbar);
    beginnAendernButton.setEnabled(editierbar);
    endeAendernButton.setEnabled(editierbar);
    zeitFeld.setEnabled(editierbar);
    zeitSetzenButton.setEnabled(editierbar);
    
    terminHinzufuegenButton.setEnabled(veraenderbar);
    terminLoeschenButton.setEnabled(editierbar);    
  }
  
  private void setTermin(Termin termin) {        
    aktuellerTermin = termin;

    if (termin == null || (termin.getBeginn() == null)) {
      beginnFeld.setText(null);      
    } else {
      beginnFeld.setText(terminFormat.format(termin.getBeginn()));      
    }
    
    if (termin == null || (termin.getEnde() == null)) {
      endeFeld.setText(null);      
    } else {
      endeFeld.setText(terminFormat.format(termin.getEnde()));      
    }
    if (termin != null && termin.getBeginn() != null) 
      letzterTerminBeginn = termin.getBeginn();
    
    setTerminVeraenderbar(istVeraenderbar);
  }

  /**
   * Erzeugt ein VeranstaltungenPanel, das im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem das Panel gehört
   */
  public VeranstaltungenPanel(JFrame hauptFenster) {
    this.hauptFenster = hauptFenster;
    aktuelleVeranstaltung = null;
    oldVeranstaltung = null;
    terminWirdGeradeGecheckt = false;
    terminWirdGeradeGeupdated = false;
    keinCheck = true;
    datumAuswahlDialog = new DatumAuswahlDialog(hauptFenster);
    try {
      jbInit();
      aktualisiere();      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  // erzeugt die GUI
  void jbInit() throws Exception {

    //Alle wichtigen Objeke initialisieren
    titelFeld = new JTextField();
    titelFeld.setMinimumSize(
      new Dimension(80, titelFeld.getPreferredSize().height));
    kurzTitelFeld = new JTextField();
    ansprechpartnerFeld = new JTextField();
    beschreibungFeld = new JTextArea();
    beschreibungFeld.setWrapStyleWord(true);
    beschreibungFeld.setLineWrap(true);
    kostenFeld = new JTextField();
    maximaleTeilnehmeranzahlFeld = new JTextField();
    anmeldungErforderlichBox = new JCheckBox();
    JComponentFormatierer.setDimension(
      anmeldungErforderlichBox,
      new Dimension(30, titelFeld.getPreferredSize().height));
    bezugsgruppeFeld = new JTextField();
    waehrungFeld = new JTextField();
    JComponentFormatierer.setDimension(
      waehrungFeld,
      new Dimension(40, waehrungFeld.getPreferredSize().height));
    veranstaltungsgruppeFeld = new SortiertComboBox<Veranstaltungsgruppe>();
    JComponentFormatierer.setDimension(
      veranstaltungsgruppeFeld,
      new Dimension(30, waehrungFeld.getPreferredSize().height));
    maximaleTeilnehmeranzahlFeld.setHorizontalAlignment(SwingConstants.RIGHT);
    kostenFeld.setHorizontalAlignment(SwingConstants.RIGHT);

    maximaleTeilnehmeranzahlFeld
      .addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {         
        checkMaxTeilnehmerAnzahl();
      }
    });
    kostenFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkKosten();
      }
    });

    terminTabelle = new JTable();
    terminModell = new TerminTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(terminModell, terminTabelle);
    terminTabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    terminTabelle.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {        
        checkAktuellenTermin();
        
        aktuellerTerminIndex = terminTabelle.getSelectedRow();        
        if (aktuellerTerminIndex == -1) {
          setTermin(null);
        } else {
          setTermin(terminModell.get(aktuellerTerminIndex));
        }
      }
    });

    //Labels
    JLabel jLabel1 = new JLabel("Titel:");
    JLabel jLabel2 = new JLabel("Veranstaltungsgruppe:");
    JLabel jLabel3 = new JLabel("Bezugsgruppe:");
    JLabel jLabel4 = new JLabel("Kosten:");
    JLabel jLabel7 = new JLabel("Anmeldung erforderlich:");
    JLabel jLabel8 = new JLabel("max. Teilnehmeranzahl:");
    JLabel jLabel11 = new JLabel("Kurz-Titel:");
    JLabel jLabel12 = new JLabel("Ansprechpartner:");
    JLabel jLabel15 = new JLabel("Beschreibung:");
    JLabel jLabel16 = new JLabel("Beginn:");
    JLabel jLabel17 = new JLabel("Ende:");

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setMinimumSize(new Dimension(0, 40));
    jScrollPane1.setPreferredSize(new Dimension(0, 40));

    //TerminPanel
    JPanel terminPanel = new JPanel();
    terminPanel.setLayout(new GridBagLayout());
    beginnFeld = new JTextField();
    endeFeld = new JTextField();
    zeitFeld = new SortiertComboBox<Termin>(new Format<Termin>() {
      public String format(Termin o) {
        if (o == null) return null;
        return o.getZeitraumFormat(Zeitraum.ZEITRAUMFORMAT_UHRZEIT);
      }    
    });
    
    JComponentFormatierer.setDimension(
        zeitFeld,
        new Dimension(30, waehrungFeld.getPreferredSize().height));

    
    FocusListener terminFocusListener = new FocusListener() {
      public void focusGained(FocusEvent e) {}

      public void focusLost(FocusEvent e) {
        updateTermin();
      }
    };
    beginnFeld.addFocusListener(terminFocusListener);
    endeFeld.addFocusListener(terminFocusListener); 
    beginnAendernButton = new JButton("Kalender");
    beginnAendernButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {        
        new Thread() {
          public void run() {
            setzeTerminBeginn();
          }
        }.start();
      }
    });
    endeAendernButton = new JButton("Kalender");
    endeAendernButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            setzeTerminEnde();
          }
        }.start();
      }
    });
    zeitSetzenButton = new JButton("Setzen");
    zeitSetzenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            setzeZeit();
          }
        }.start();
      }
    });

    
    JComponentFormatierer.setDimension(beginnAendernButton, new Dimension(
      beginnAendernButton.getPreferredSize().width, beginnFeld.getPreferredSize().height));
    JComponentFormatierer.setDimension(endeAendernButton, new Dimension(
      endeAendernButton.getPreferredSize().width, beginnFeld.getPreferredSize().height));
    JComponentFormatierer.setDimension(zeitSetzenButton, new Dimension(
        zeitSetzenButton.getPreferredSize().width, beginnFeld.getPreferredSize().height));
    jScrollPane1.getViewport().add(beschreibungFeld, null);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2, 15, 5));
    terminLoeschenButton = new JButton("Löschen");
    terminLoeschenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            terminLoeschen();
          }
        }.start();
      }
    });
    terminHinzufuegenButton = new JButton("Hinzufügen");
    terminHinzufuegenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            neuenTerminAnlegen();
          }
        }.start();
      }
    });
    buttonPanel.add(terminHinzufuegenButton);
    buttonPanel.add(terminLoeschenButton);
   
    JScrollPane jScrollPane2 = new JScrollPane();
    jScrollPane2.setMinimumSize(new Dimension(0, 40));
    jScrollPane2.setPreferredSize(new Dimension(0, 40));
    jScrollPane2.getViewport().add(terminTabelle, null);

    this.setLayout(new GridBagLayout());
    this.add(
      jLabel1,
      new GridBagConstraints(
        0,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 5),
        0,
        0));
    this.add(
      titelFeld,
      new GridBagConstraints(
        1,
        0,
        2,
        1,
        1.0,
        0.0,
        GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      jLabel2,
      new GridBagConstraints(
        3,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 20, 5, 5),
        0,
        0));
    this.add(
      jLabel3,
      new GridBagConstraints(
        0,
        1,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 5),
        0,
        0));
    this.add(
      jLabel8,
      new GridBagConstraints(
        0,
        3,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 5),
        0,
        0));
    this.add(
      maximaleTeilnehmeranzahlFeld,
      new GridBagConstraints(
        1,
        3,
        2,
        1,
        1.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      jLabel7,
      new GridBagConstraints(
        0,
        2,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 5),
        0,
        0));
    this.add(
      anmeldungErforderlichBox,
      new GridBagConstraints(
        1,
        2,
        2,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTH,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      bezugsgruppeFeld,
      new GridBagConstraints(
        1,
        1,
        2,
        1,
        1.0,
        0.0,
        GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      veranstaltungsgruppeFeld,
      new GridBagConstraints(
        4,
        0,
        2,
        1,
        1.0,
        0.0,
        GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      jScrollPane1,
      new GridBagConstraints(
        1,
        4,
        5,
        1,
        1.0,
        1.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 20, 0),
        0,
        0));
    this.add(
      jLabel4,
      new GridBagConstraints(
        3,
        2,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 20, 5, 5),
        0,
        0));
    this.add(
      kostenFeld,
      new GridBagConstraints(
        4,
        2,
        1,
        1,
        1.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      waehrungFeld,
      new GridBagConstraints(
        5,
        2,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 5, 5, 0),
        0,
        0));
    this.add(
      jLabel11,
      new GridBagConstraints(
        3,
        3,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 20, 5, 5),
        0,
        0));
    this.add(
      jLabel15,
      new GridBagConstraints(
        0,
        4,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 5),
        0,
        0));
    this.add(
      ansprechpartnerFeld,
      new GridBagConstraints(
        4,
        1,
        2,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTH,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      jLabel12,
      new GridBagConstraints(
        3,
        1,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.NONE,
        new Insets(0, 20, 5, 5),
        0,
        0));
    this.add(
      kurzTitelFeld,
      new GridBagConstraints(
        4,
        3,
        2,
        1,
        1.0,
        0.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      jScrollPane2,
      new GridBagConstraints(
        0,
        5,
        3,
        1,
        1.0,
        1.0,
        GridBagConstraints.NORTHWEST,
        GridBagConstraints.BOTH,
        new Insets(0, 0, 5, 0),
        0,
        0));
    this.add(
      terminPanel,
      new GridBagConstraints(
        3,
        5,
        3,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        new Insets(0, 20, 0, 0),
        0,
        0));
    terminPanel.add(
      jLabel16,
      new GridBagConstraints(
        0,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 5),
        0,
        0));
    terminPanel.add(
      jLabel17,
      new GridBagConstraints(
        0,
        1,
        2,
        1,
        0.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(0, 0, 5, 0),
        0,
        0));
    terminPanel.add(
      beginnFeld,
      new GridBagConstraints(
        1,
        0,
        1,
        1,
        1.0,
        1.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    terminPanel.add(
      endeFeld,
      new GridBagConstraints(
        1,
        1,
        1,
        1,
        1.0,
        1.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 5, 0),
        0,
        0));
    terminPanel.add(
      beginnAendernButton,
      new GridBagConstraints(
        2,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 5, 5, 0),
        0,
        0));
    terminPanel.add(
      endeAendernButton,
      new GridBagConstraints(
        2,
        1,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 5, 5, 0),
        0,
        0));

    terminPanel.add(
    new JLabel("Zeit"),
    new GridBagConstraints(
      0,
      2,
      2,
      1,
      0.0,
      0.0,
      GridBagConstraints.WEST,
      GridBagConstraints.NONE,
      new Insets(0, 0, 5, 0),
      0,
      0));
  terminPanel.add(
    zeitFeld,
    new GridBagConstraints(
      1,
      2,
      1,
      1,
      1.0,
      1.0,
      GridBagConstraints.CENTER,
      GridBagConstraints.HORIZONTAL,
      new Insets(0, 0, 5, 0),
      0,
      0));
  terminPanel.add(
      zeitSetzenButton,
      new GridBagConstraints(
        2,
        2,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 5, 5, 0),
        0,
        0));

    
    terminPanel.add(
      buttonPanel,
      new GridBagConstraints(
        0,
        3,
        3,
        1,
        0.0,
        0.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,
        new Insets(10, 0, 5, 0),
        0,
        0));
  }

  protected boolean checkAktuellenTermin() {
    if (terminWirdGeradeGecheckt) return true;
    terminWirdGeradeGecheckt = true;

    boolean erg = true;
    try {
      if (aktuellerTermin != null && aktuellerTermin.getBeginn() != null) {
        aktuellerTermin.check();
        letzterTerminBeginn = aktuellerTermin.getBeginn();    
      }
    } catch (ZeitraumInkonsistenzException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        e.getMessage(),
        "Ungültiger Termin!",
        JOptionPane.ERROR_MESSAGE);
      terminTabelle.getSelectionModel().setSelectionInterval(
        aktuellerTerminIndex, aktuellerTerminIndex);
      erg = false;
    }
    
    terminWirdGeradeGecheckt = false;    
    return erg;
  }

  public void aktualisiere() {
    if (aktuelleVeranstaltung != null) {
      try {
        aktuelleVeranstaltung.reload();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
        aktuelleVeranstaltung = null;
      }
      this.setVeranstaltung(aktuelleVeranstaltung);
    }
    VeranstaltungsgruppeFactory veranstaltungsgruppeFactory =
      Datenbank.getInstance().getVeranstaltungsgruppeFactory();
    VeranstaltungsgruppenListe liste =
      veranstaltungsgruppeFactory.getAlleVeranstaltungsgruppen();
    liste.setSortierung(
      VeranstaltungsgruppenListe.alphabetischeSortierung,
      true);    
    veranstaltungsgruppeFeld.setDaten(liste);
    
    VeranstaltungFactory veranstaltungFactory =
      Datenbank.getInstance().getVeranstaltungFactory();
    TerminListe terminliste =
      veranstaltungFactory.getUeblicheUhrzeiten();
    terminliste.setSortierung(ZeitraumListe.uhrzeitSortierung);
    terminliste.setZeigeDoppelteEintraege(false);
    zeitFeld.setDaten(terminliste);
  }

  void checkMaxTeilnehmerAnzahl() {
    if (keinCheck) return;
    
    String maximaleTeilnehmerAnzahlString =
      maximaleTeilnehmeranzahlFeld.getText();    
    
    boolean anzahlOK = true;
    if (maximaleTeilnehmerAnzahlString == null
      || maximaleTeilnehmerAnzahlString.equals(""))
      anzahlOK = false;

    int anzahl = 0;
    if (anzahlOK) {
      try {
        anzahl = Integer.parseInt(maximaleTeilnehmerAnzahlString);
        if (anzahl < 0)
          anzahlOK = false;
      } catch (NumberFormatException e) {
        anzahlOK = false;
      }
    }

    if (!anzahlOK)
      anzahl = 0;
    maximaleTeilnehmeranzahlFeld.setText(Integer.toString(anzahl));
    if (!anzahlOK) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Die Eingabe '"
          + maximaleTeilnehmerAnzahlString
          + "' kann\nnicht als positive Zahl interpretiert\n"
          + "werden!",
        "Ungültige maximale Teilnehmeranzahl!",
        JOptionPane.ERROR_MESSAGE);
    }
  }

  void checkKosten() {
    if (keinCheck) return;
    String kostenString = kostenFeld.getText();

    boolean kostenOK = true;
    if (kostenString == null || kostenString.equals(""))
      kostenOK = false;

    double kosten = 0;
    if (kostenOK) {
      try {
        kosten = waehrungsFormat.parse(kostenString).doubleValue();
        if (kosten < 0)
          kostenOK = false;
      } catch (ParseException e) {
        kostenOK = false;
      }
    }

    if (!kostenOK)
      kosten = 0;
    kostenFeld.setText(waehrungsFormat.format(kosten));
    if (!kostenOK) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Die Eingabe '"
          + kostenString
          + "' kann\nnicht als Betrag interpretiert\n"
          + "werden!",
        "Ungültige Kosten!",
        JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Liefert die aktuell angezeigte Veranstaltung
   */
  public Veranstaltung getVeranstaltung() {
    return aktuelleVeranstaltung;
  }

  /**
   * Speichert die gemachten Änderungen an der Veranstaltung, falls dies möglich ist. Ist
   * dies nicht möglich, weil bspw. nicht alle Daten angegeben sind, wird eine
   * entsprechende Fehlermeldung angezeigt.
   *
   * @return <code>true</code> gdw die Änderungen gespeichert werden konnten
   */
  public boolean saveChanges() {
    if (!checkAktuellenTermin()) return false;
    
    if (aktuelleVeranstaltung == null)
      throw new NullPointerException();

    try {
      terminModell.saeubere();
    } catch (ZeitraumInkonsistenzException e) {
      JOptionPane.showMessageDialog(
          hauptFenster,
          e.getMessage(),
          "Ungültiger Termin!",
          JOptionPane.ERROR_MESSAGE);
      terminModell.fireTableDataChanged();
      return false;
    }
    
    try {
      aktuelleVeranstaltung.setTitel(titelFeld.getText());
      aktuelleVeranstaltung.setKurzTitel(kurzTitelFeld.getText());
      aktuelleVeranstaltung.setAnsprechpartner(ansprechpartnerFeld.getText());
      aktuelleVeranstaltung.setBezugsgruppe(bezugsgruppeFeld.getText());
      aktuelleVeranstaltung.setBeschreibung(beschreibungFeld.getText());
      aktuelleVeranstaltung.setMaximaleTeilnehmerAnzahl(
        Integer.parseInt(maximaleTeilnehmeranzahlFeld.getText()));
      aktuelleVeranstaltung.setKosten(
        waehrungsFormat.parse(kostenFeld.getText()).doubleValue());
      aktuelleVeranstaltung.setAnmeldungErforderlich(
        anmeldungErforderlichBox.isSelected());
      aktuelleVeranstaltung.setWaehrung(waehrungFeld.getText());
      aktuelleVeranstaltung.setVeranstaltungsgruppe(
        (Veranstaltungsgruppe) veranstaltungsgruppeFeld.getSelectedItem());
      aktuelleVeranstaltung.setTermine((TerminListe) terminModell.getDaten());

      aktuelleVeranstaltung.save();
      zeitFeld.addAll(aktuelleVeranstaltung.getTermine());
      return true;
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        e.getMessage(),
        "Unvollständige Daten!",
        JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
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
      if (oldVeranstaltung != null)
        oldVeranstaltung.reload();
      setVeranstaltung(oldVeranstaltung);
      setVeraenderbar(false);
      return true;
    } catch (DatenNichtGefundenException e) {
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    return false;
  }

  void neuenTerminAnlegen() {
    if (!checkAktuellenTermin()) return;
    
    Termin neuerTermin = new Termin(null, null);
    terminModell.add(neuerTermin);
    int index = terminModell.indexOf(neuerTermin);
    terminTabelle.getSelectionModel().setSelectionInterval(index,index);
  }

  void terminLoeschen() {
    terminWirdGeradeGecheckt = true;

    int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll der Termin '"+
      aktuellerTermin +
      "' wirklich gelöscht werden?",
      "Termin löschen?", JOptionPane.YES_NO_OPTION);
    if (erg == JOptionPane.YES_OPTION) {        
      terminModell.remove(aktuellerTermin);
      terminModell.fireTableDataChanged();
      if (terminModell.getRowCount() > 0)
        terminTabelle.getSelectionModel().setSelectionInterval(0,0);
    }
    
    terminWirdGeradeGecheckt = false;
  }

  private void setzeTerminBeginn() {
    Date ausgangsDatum = aktuellerTermin.getBeginn();
    if (ausgangsDatum == null) ausgangsDatum = aktuellerTermin.getEnde();
    if (ausgangsDatum == null) ausgangsDatum = letzterTerminBeginn;
    Date gewaehltesDatum = datumAuswahlDialog.waehleDatum(null, 
      ausgangsDatum, true);
    if (gewaehltesDatum == null) return;

    
    Termin uhrzeit = (Termin) zeitFeld.getSelectedItem();
    if (aktuellerTermin.getEnde() == null &&
        aktuellerTermin.getBeginn() == null &&
        uhrzeit != null && uhrzeit.getEnde() != null) {
      Calendar calendarUhrzeit = Calendar.getInstance();
      calendarUhrzeit.setTime(uhrzeit.getBeginn());
      
      Calendar calendarResult = Calendar.getInstance();
      calendarResult.setTime(gewaehltesDatum);
      
      if (calendarResult.get(Calendar.HOUR_OF_DAY) == 
          calendarUhrzeit.get(Calendar.HOUR_OF_DAY) &&
          calendarResult.get(Calendar.MINUTE) ==  
          calendarUhrzeit.get(Calendar.MINUTE)) {
        calendarResult.add(Calendar.MILLISECOND, 
            (int) (uhrzeit.getEnde().getTime()-uhrzeit.getBeginn().getTime()));
        aktuellerTermin.setEnde(calendarResult.getTime());
        endeFeld.setText(terminFormat.format(aktuellerTermin.getEnde()));
      }
    }
                                 
    beginnFeld.setText(terminFormat.format(gewaehltesDatum));
    aktuellerTermin.setBeginn(gewaehltesDatum);
    terminModell.fireTableRowsUpdated(
      aktuellerTerminIndex, aktuellerTerminIndex);
  }

  private void setzeTerminEnde() {
    Date ausgangsDatum = aktuellerTermin.getEnde();
    if (ausgangsDatum == null) ausgangsDatum = aktuellerTermin.getBeginn(); 
    Date gewaehltesDatum = datumAuswahlDialog.waehleDatum(null, 
      ausgangsDatum, true);
    if (gewaehltesDatum == null) return;

    endeFeld.setText(terminFormat.format(gewaehltesDatum));
    aktuellerTermin.setEnde(gewaehltesDatum);
    terminModell.fireTableRowsUpdated(
      aktuellerTerminIndex, aktuellerTerminIndex);
  }

  private void setzeZeit() {
    Termin uhrzeit = (Termin) zeitFeld.getSelectedItem();
    if (uhrzeit == null || aktuellerTermin.getBeginn() == null) return;
    
    Calendar calendarUhrzeitBeginn = Calendar.getInstance();
    calendarUhrzeitBeginn.setTime(uhrzeit.getBeginn());
    
    Calendar calendarResult = Calendar.getInstance();
    calendarResult.setTime(aktuellerTermin.getBeginn());
    calendarResult.set(Calendar.HOUR_OF_DAY, 
        calendarUhrzeitBeginn.get(Calendar.HOUR_OF_DAY));
    calendarResult.set(Calendar.MINUTE, 
        calendarUhrzeitBeginn.get(Calendar.MINUTE));

    aktuellerTermin.setBeginn(calendarResult.getTime());
    beginnFeld.setText(terminFormat.format(aktuellerTermin.getBeginn()));
    if (uhrzeit.getEnde() == null) {
      aktuellerTermin.setEnde(null);
      endeFeld.setText(null);
    } else {        
      calendarResult.add(Calendar.MILLISECOND, 
          (int) (uhrzeit.getEnde().getTime()-uhrzeit.getBeginn().getTime()));
      aktuellerTermin.setEnde(calendarResult.getTime());
      endeFeld.setText(terminFormat.format(aktuellerTermin.getEnde()));
    }  
    terminModell.fireTableRowsUpdated(
      aktuellerTerminIndex, aktuellerTerminIndex);
  }
}