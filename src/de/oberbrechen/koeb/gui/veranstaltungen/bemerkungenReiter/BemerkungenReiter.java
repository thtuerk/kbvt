package de.oberbrechen.koeb.gui.veranstaltungen.bemerkungenReiter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.datenbankzugriff.Bemerkung;
import de.oberbrechen.koeb.datenbankzugriff.BemerkungVeranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.BemerkungVeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.BemerkungenListe;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.DelayListSelectionListener;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.veranstaltungen.Main;
import de.oberbrechen.koeb.gui.veranstaltungen.VeranstaltungenMainReiter;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Veranstaltungsbemerkungen in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class BemerkungenReiter extends JPanel implements VeranstaltungenMainReiter {

  public static SimpleDateFormat terminFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

  
  private BemerkungVeranstaltungFactory bemerkungFactory;
  private VeranstaltungFactory veranstaltungFactory;
  private boolean veranstaltungWirdGeradeVeraendert=true;

  Main hauptFenster;
  boolean istVeraenderbar;

  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  private JTextField datumFeld;
  private JTextArea bemerkungFeld;
  private JButton datumButton;

  
  JTable bemerkungTable;
  private DelayListSelectionListener bemerkungenTabelleDelayListSelectionListener;
  BemerkungenTableModel bemerkungenTableModel;
  private SortiertComboBox<Veranstaltung> veranstaltungFeld;
  private Veranstaltungsgruppe veranstaltungsgruppe;
  private Bemerkung aktuelleBemerkung = null;
  private Veranstaltung aktuelleVeranstaltung = null;

  //Doku siehe bitte Interface
  public void refresh() {    
    setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
  }

  void initButtons() {
    neuButton.setEnabled(!istVeraenderbar && veranstaltungFeld.getSelectedItem() != null);
    hauptFenster.erlaubeAenderungen(!istVeraenderbar);
    bemerkungTable.setEnabled(!istVeraenderbar);
    veranstaltungFeld.setEnabled(!istVeraenderbar);    
    
    if (!istVeraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }
    
    boolean bemerkungAngezeigt = (aktuelleBemerkung != null);
    ladenButton.setEnabled(bemerkungAngezeigt); 
    saveButton.setEnabled(bemerkungAngezeigt); 

  }
  
  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob die aktuell angezeigte
   * Bemerkung verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    
    datumFeld.setEditable(veraenderbar);
    bemerkungFeld.setEditable(veraenderbar);
    datumButton.setEnabled(veraenderbar);
    initButtons();
  }

  public BemerkungenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    bemerkungFactory = Datenbank.getInstance().getBemerkungVeranstaltungFactory();
    veranstaltungFactory = Datenbank.getInstance().getVeranstaltungFactory();

    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      ErrorHandler.getInstance().handleException(e, true);
    }                
    setVeraenderbar(false);
  }

  private void setVeranstaltung (Veranstaltung veranstaltung) {
    aktuelleVeranstaltung = veranstaltung;
    
    BemerkungenListe liste = null;
    if (veranstaltung != null) {
      try {
        liste = bemerkungFactory.getAlleVeranstaltungsbemerkungen(veranstaltung);
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, false);
      }
    } else {
      if (veranstaltungsgruppe != null) {
        try {
          liste = bemerkungFactory.getAlleVeranstaltungsgruppenbemerkungen(veranstaltungsgruppe);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, false);
        }
      }
    }
    
    if (liste == null) liste = new BemerkungenListe();        
    bemerkungenTableModel.setDaten(liste);
    initButtons();
  }
  
  private Date getDatum() {
    Date neuesDatum = null;
    String datumString = datumFeld.getText();
    try {
      if (datumString != null && !datumString.trim().equals("")) {
        neuesDatum = terminFormat.parse(datumString);
      }      
    } catch (ParseException e) {
      JOptionPane.showMessageDialog(
        hauptFenster,
        "Ein Termin muss im Format 'tt.mm.jjjj hh:mm' eingegeben werden.",
        "Ungültiger Termin!",
        JOptionPane.ERROR_MESSAGE);
    }    
    return neuesDatum;
  }
  
  private void updateDatum() {
    Date neuesDatum = getDatum();
    String datumString = neuesDatum != null?terminFormat.format(neuesDatum):null;
    datumFeld.setText(datumString);
  }

  // erzeugt die GUI
  private void jbInit() throws Exception {
    //Auswahl
    JLabel veranstaltungLabel = new JLabel("Veranstaltung:");
    veranstaltungFeld = new SortiertComboBox<Veranstaltung>(false, new Format<Veranstaltung>() {
      public String format(Veranstaltung o) {
        if (o == null) return "-";
        return o.getTitel();
      }      
    });
    veranstaltungFeld.setErgaenzeNull(true);
    JComponentFormatierer.setDimension(veranstaltungFeld, new JTextField().getPreferredSize());
    veranstaltungFeld.addDelayItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (!veranstaltungWirdGeradeVeraendert) {
          Veranstaltung veranstaltung = null;
          if (e.getStateChange() == ItemEvent.SELECTED) veranstaltung = (Veranstaltung) e.getItem();
          setVeranstaltung(veranstaltung);
        }
      }
    });

    //Buttons bauen
    neuButton.setText("Neue Bemerkung");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neueBemerkungAnlegen();
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
          loescheBemerkung();
        } else {
          aenderungenVerwerfen();
        }
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    buttonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    buttonPanel.add(saveButton, null);
    buttonPanel.add(ladenButton, null);
    buttonPanel.add(neuButton, null);

    //Details bauen
    JPanel detailsPanel = new JPanel();
    datumFeld = new JTextField();
    datumFeld.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {}

      public void focusLost(FocusEvent e) {
        updateDatum();
      }
    });


    datumButton = new JButton("Setzen");
    datumButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        datumFeld.setText(terminFormat.format(new Date()));
      }      
    });
    JComponentFormatierer.setDimension(datumButton, new Dimension(
        datumButton.getPreferredSize().width, datumFeld.getPreferredSize().height));
    
    bemerkungFeld = new JTextArea();
    JScrollPane jScrollPane2 = new JScrollPane();
    jScrollPane2.setMinimumSize(new Dimension(0, 40));
    jScrollPane2.setPreferredSize(new Dimension(0, 40));
    bemerkungFeld.setLineWrap(true);
    bemerkungFeld.setWrapStyleWord(true);
    jScrollPane2.getViewport().add(bemerkungFeld, null);
    
    
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
    detailsPanel.setLayout(new GridBagLayout());
    detailsPanel.setMinimumSize(new Dimension(50,80));
    detailsPanel.setPreferredSize(new Dimension(50,80));
    detailsPanel.add(new JLabel("Datum:"),           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    detailsPanel.add(new JLabel("Bemerkung:"),           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    detailsPanel.add(datumFeld,         new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    detailsPanel.add(datumButton,         new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    detailsPanel.add(jScrollPane2,         new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));
    
    
    //Bemerkungenliste
    bemerkungTable = new JTable();
    bemerkungenTableModel = new BemerkungenTableModel();
    bemerkungTable.setModel(bemerkungenTableModel);
    ErweiterterTableCellRenderer.setzeErweitertesModell(bemerkungenTableModel, bemerkungTable);
    bemerkungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    bemerkungenTabelleDelayListSelectionListener = 
      new DelayListSelectionListener(250);
    bemerkungenTabelleDelayListSelectionListener.addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            if (!veranstaltungWirdGeradeVeraendert) {
              int gewaehlteReihe = bemerkungTable.getSelectedRow();
              if (gewaehlteReihe != -1) {
                Bemerkung gewaehlteBemerkung = (Bemerkung)
                  bemerkungenTableModel.get(gewaehlteReihe);
                setBemerkung(gewaehlteBemerkung);
              } else {
                setBemerkung(null);                
              }
              initButtons();              
            }
          }
      });
    bemerkungTable.getSelectionModel().addListSelectionListener(
        bemerkungenTabelleDelayListSelectionListener);

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));

    JPanel medienPanel = new JPanel();
    medienPanel.setLayout(new GridBagLayout());
    medienPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    medienPanel.setMinimumSize(new Dimension(50,80));
    medienPanel.setPreferredSize(new Dimension(50,80));
    medienPanel.add(veranstaltungLabel,           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 10), 0, 0));
    medienPanel.add(jScrollPane1,         new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
    medienPanel.add(veranstaltungFeld,      new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(bemerkungTable, null);

    //alles Zusammenbauen
    this.setLayout(new BorderLayout());
    JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane.add(detailsPanel, JSplitPane.BOTTOM);
    jSplitPane.add(medienPanel, JSplitPane.TOP);
    jSplitPane.setResizeWeight(0.5);

    this.add(jSplitPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Legt ein neues Medium an und zeigt es an
   */
  void neueBemerkungAnlegen() {
    bemerkungenTabelleDelayListSelectionListener.fireDelayListSelectionEvent();
    BemerkungVeranstaltung neueBemerkung = bemerkungFactory.erstelleNeu();
    neueBemerkung.setVeranstaltung((Veranstaltung) veranstaltungFeld.getSelectedItem());
    setBemerkung(neueBemerkung);
    setVeraenderbar(true);
  }

  public void aktualisiere() {
  }

  private void setBemerkung(Bemerkung bemerkung) {
    aktuelleBemerkung = bemerkung;
    
    if (bemerkung == null) {
      bemerkungFeld.setText(null);
      datumFeld.setText(null);
    } else {
      bemerkungFeld.setText(bemerkung.getBemerkung());
      if (bemerkung.getDatum() != null) {
        datumFeld.setText(terminFormat.format(bemerkung.getDatum()));
      } else {
        datumFeld.setText(null);
      }
    }
  }

  /**
   * Löscht die aktuelle Bemerkung
   */
  public void loescheBemerkung() {
    try {
      int erg = JOptionPane.showConfirmDialog(hauptFenster, "Soll die Bemerkung "+
        "wirklich gelöscht werden?",
        "Medium löschen?", JOptionPane.YES_NO_OPTION);
      if (erg != JOptionPane.YES_OPTION) return;

      aktuelleBemerkung.loesche();
      bemerkungenTableModel.remove(aktuelleBemerkung);
      
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e,
        "Beim Löschen der Bemerkung wurde eine Datenbank-Inkonsistenz bemerkt.", 
        false);
    }
    
    if (bemerkungenTableModel.size() > 0) {
      bemerkungTable.setRowSelectionInterval(0, 0);
      bemerkungTable.scrollRectToVisible(bemerkungTable.getCellRect(0, 0, true));
    } else {
      setBemerkung(null);
    }
    initButtons();      
  }

  /**
   * Speichert die gemachten Änderungen
   */
  public void saveChanges() {    
    if (aktuelleBemerkung == null) return;
    
    try {
      boolean istNeu = aktuelleBemerkung.istNeu();
      aktuelleBemerkung.setBemerkung(bemerkungFeld.getText());            
      aktuelleBemerkung.setDatum(getDatum());
      aktuelleBemerkung.save();
      setVeraenderbar(false);
      if (istNeu) bemerkungenTableModel.add(aktuelleBemerkung);
      setBemerkung(aktuelleBemerkung);
      initButtons();
    } catch (UnvollstaendigeDatenException e) {
      JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
          "Bemerkungen ohne Text können nicht gespeichert werden!",
          JOptionPane.ERROR_MESSAGE);      
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }    
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  public void aenderungenVerwerfen() {
    if (aktuelleBemerkung != null) {
      try {
        aktuelleBemerkung.reload();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
        aktuelleBemerkung = null;
      }
    }
    
    setBemerkung(aktuelleBemerkung);
    setVeraenderbar(false);
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
//    veranstaltungWirdGeradeVeraendert=true;
    this.veranstaltungsgruppe = veranstaltungsgruppe;
    
    VeranstaltungenListe liste = null;
    
    if (veranstaltungsgruppe != null) {
      liste = veranstaltungFactory.getVeranstaltungen(veranstaltungsgruppe);
    } else {
      liste = new VeranstaltungenListe();
    }
    
    liste.setSortierung(VeranstaltungenListe.AlphabetischeSortierung); 
    veranstaltungFeld.setDaten(liste);
    veranstaltungWirdGeradeVeraendert=false;
    
    if (aktuelleVeranstaltung != null && liste.contains(aktuelleVeranstaltung)) {
      veranstaltungFeld.setSelectedItem(aktuelleVeranstaltung);
    } else if (liste.size() > 0) {
      veranstaltungFeld.setSelectedItem(null);
      setVeranstaltung(null);      
    }
  }

  public void setBenutzer(Benutzer benutzer) {
  }
}