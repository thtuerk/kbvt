package de.oberbrechen.koeb.gui.bestand.medienReiter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.ausgaben.MedienAusgabe;
import de.oberbrechen.koeb.ausgaben.MedienAusgabeFactory;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AusgabeAuswahl;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.bestand.BestandMainReiter;
import de.oberbrechen.koeb.gui.bestand.Main;
import de.oberbrechen.koeb.gui.components.DelayListSelectionListener;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.components.medienPanel.MedienPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.pdf.pdfMedienListe.PdfMedienlisteMedienAusgabeFactory;
import de.oberbrechen.koeb.server.medienabfragen.Medienabfrage;
import de.oberbrechen.koeb.server.medienabfragen.MedienabfrageErgebnis;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzern in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MedienReiter extends JPanel implements BestandMainReiter {

  private static final long serialVersionUID = 1L;

  interface MedienListeWrapper {
    public MedienListe getMedienliste() throws DatenbankInkonsistenzException;
    public String getAusgabeBeschreibung();
  }
  
  private MedienAusgabe medienlisteAusgabe;
  private MedientypFactory medientypFactory;

  private MediumFactory mediumFactory; 
  Main hauptFenster;
  boolean istVeraenderbar;

  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  MedienPanel detailsPanel;
  JTable medienTable;
  private DelayListSelectionListener medienTabelleDelayListSelectionListener;
  MedienTableModel medienTableModel;
  private SortiertComboBox<MedienListeWrapper> auswahlFeld;

  private JButton pdfButton;   
  
  //Doku siehe bitte Interface
  public void refresh() {
  }

  void initButtons() {
    neuButton.setEnabled(!istVeraenderbar);
    hauptFenster.erlaubeAenderungen(!istVeraenderbar);
    medienTable.setEnabled(!istVeraenderbar);
    auswahlFeld.setEnabled(!istVeraenderbar);    
    pdfButton.setEnabled(!istVeraenderbar && medienlisteAusgabe != null);
    
    if (!istVeraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }
    
    boolean mediumAngezeigt = (detailsPanel.getMedium() != null);
    ladenButton.setEnabled(mediumAngezeigt); 
    saveButton.setEnabled(mediumAngezeigt); 

  }
  
  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    detailsPanel.setVeraenderbar(veraenderbar);
    initButtons();
  }

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public MedienReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    mediumFactory = Datenbank.getInstance().getMediumFactory();
    medientypFactory = Datenbank.getInstance().getMedientypFactory();
    
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();

    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
            
    try {
      medienlisteAusgabe = ((MedienAusgabeFactory) 
      einstellungFactory.getClientEinstellung(
          this.getClass().getName(), "MedienlisteAusgabe").
          getWertObject(MedienAusgabeFactory.class,
              PdfMedienlisteMedienAusgabeFactory.class)).createMedienAusgabe();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der MedienlisteAusgabe!", false);
      medienlisteAusgabe = null;
    }
    
    setVeraenderbar(false);
    initAuswahlFeld();
    auswahlFeld.setSelectedIndex(0);
  }

  private void initAuswahlFeld() {        
    String configFile = 
      Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
          this.getClass().getName(), "MedienlistenConfigfile").
          getWert("einstellungen/Bestand-Medienlisten.conf");      
    
    boolean ok = false;
    try {
      AuswahlKonfiguration konfiguration = new AuswahlKonfiguration(configFile);      
            
      for (int i=0; i < konfiguration.getAusgabeAnzahl(); i++) {
        final AusgabeAuswahl ausgabe = konfiguration.getAusgabe(i);
        if (!ausgabe.istMediumAuswahl()) {
          ErrorHandler.getInstance().handleError("Ausgabe '"+ausgabe.getTitel()+
              "' ist keine Medium-Auswahl!", false);                 
        } else {
          ok = true;
          auswahlFeld.addItem(new MedienListeWrapper() {
            public MedienListe getMedienliste() {
              return ausgabe.bewerte(hauptFenster.getAlleMedien());
            }

            public String getAusgabeBeschreibung() {
              return ausgabe.getTitel();
            }        
            
            public String toString() {
              return ausgabe.getTitel();
            }
          });
        }
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Parsen der Datei '"+configFile+"'!", false);
    }
    
    if (!ok) {
      auswahlFeld.addItem(new MedienListeWrapper() {
        public MedienListe getMedienliste() throws DatenbankInkonsistenzException {
          return mediumFactory.getMedienListe(null, null, false);
        }
        
        public String toString() {
          return "alle Medien";
        }
    
        public String getAusgabeBeschreibung() {
          return "Gesamtliste";
        }
      });
  
      auswahlFeld.addItem(new MedienListeWrapper() {
        public MedienListe getMedienliste() throws DatenbankInkonsistenzException {
          return mediumFactory.getMedienListe(null, null, true);
        }
        
        public String toString() {
          return "entfernte Medien";
        }
  
        public String getAusgabeBeschreibung() {
          return this.toString();
        }
      });
  
      auswahlFeld.addItem(new MedienListeWrapper() {
        public MedienListe getMedienliste() throws DatenbankInkonsistenzException {
          MedienListe auswahlListe = 
            mediumFactory.getMedienListe(null, null, null);
  
          return auswahlListe;
        }
        
        public String toString() {
          return "alle Medien inklusive entfernte Medien";
        }
        
        public String getAusgabeBeschreibung() {
          return "Gesamtliste inklusive entfernte Medien";
        }
      });

      for (final Medientyp medientyp : medientypFactory.getAlleMedientypen()) {
        auswahlFeld.addItem(new MedienListeWrapper() {
          public MedienListe getMedienliste() throws DatenbankInkonsistenzException {
            return mediumFactory.getMedienListe(medientyp, null, false);
          }
          
          public String toString() {
            return "Medientyp - "+medientyp.getName();
          }          
          
          public String getAusgabeBeschreibung() {
            return medientyp.getPlural();
          }        
        });
      }
    }
  }

  void neueMedienAuswahl(MedienListeWrapper auswahl) {
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    MedienListe auswahlListe;
    try {
      auswahlListe = auswahl.getMedienliste();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      auswahlListe = new MedienListe();
    }
    
    
    medienTableModel.setDaten(auswahlListe);
    medienTable.clearSelection();

    if (auswahlListe.size() > 0) {
      medienTable.addRowSelectionInterval(0,0);
      medienTable.scrollRectToVisible(medienTable.getCellRect(0, 0, true));
      medienTabelleDelayListSelectionListener.fireDelayListSelectionEvent();
    } else {
      detailsPanel.setMedium(null);
      initButtons();      
    }
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  // erzeugt die GUI
  private void jbInit() throws Exception {
    //Auswahl
    pdfButton = new JButton("Ausgabe");
    pdfButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pdfAusgabe();
      }
    });
    JLabel auswahlLabel = new JLabel("Auswahl:");
    auswahlFeld = new SortiertComboBox<MedienListeWrapper>(false);
    JComponentFormatierer.setDimension(auswahlFeld, new JTextField().getPreferredSize());
    JComponentFormatierer.setDimension(pdfButton, new Dimension(pdfButton.getPreferredSize().width, auswahlFeld.getPreferredSize().height));
    auswahlFeld.addDelayItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        neueMedienAuswahl((MedienListeWrapper) e.getItem());
      }
    });

    //Buttons bauen
    neuButton.setText("Neues Medium anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        neuesMediumAnlegen(null);
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
          loescheMedium();
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
    detailsPanel = new MedienPanel(hauptFenster);
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));

    //Medienliste
    medienTable = new JTable();
    medienTableModel = new MedienTableModel(medienTable, hauptFenster);
    ErweiterterTableCellRenderer.setzeErweitertesModell(medienTableModel, medienTable);
    
    medienTabelleDelayListSelectionListener = 
      new DelayListSelectionListener(250);
    medienTabelleDelayListSelectionListener.addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int gewaehlteReihe = medienTable.getSelectedRow();
            if (gewaehlteReihe != -1) {
              Medium gewaehltesMedium = (Medium) medienTableModel.get(gewaehlteReihe);
              detailsPanel.setMedium(gewaehltesMedium);
              initButtons();              
            }
          }
      });
    medienTable.getSelectionModel().addListSelectionListener(medienTabelleDelayListSelectionListener);
    
    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));

    JPanel medienPanel = new JPanel();
    medienPanel.setLayout(new GridBagLayout());
    medienPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    medienPanel.setMinimumSize(new Dimension(50,80));
    medienPanel.setPreferredSize(new Dimension(50,80));
    medienPanel.add(auswahlLabel,           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 10), 0, 0));
    medienPanel.add(jScrollPane1,         new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
    medienPanel.add(auswahlFeld,      new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    medienPanel.add(pdfButton,       new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(medienTable, null);

    //alles Zusammenbauen
    this.setLayout(new BorderLayout());
    JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane.add(detailsPanel, JSplitPane.BOTTOM);
    jSplitPane.add(medienPanel, JSplitPane.TOP);
    jSplitPane.setResizeWeight(1);

    this.add(jSplitPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  void pdfAusgabe() {
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    MedienListeWrapper auswahl = auswahlFeld.getSelectedItemTyped(); 
    
    MedienListe medienListe;
    try {
      medienListe = auswahl.getMedienliste();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      medienListe = new MedienListe();
    }
    
    String sortierungName = medienTableModel.getSortierungSpalteName();
    
    try {
      medienlisteAusgabe.setDaten(medienListe);
      medienlisteAusgabe.setTitel(auswahl.getAusgabeBeschreibung()+" - "+sortierungName);
      medienlisteAusgabe.setSortierung(medienTableModel.getSortierung(), medienTableModel.istSortierungUmgekehrt());     
      medienlisteAusgabe.run(hauptFenster, true);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Erstellen der PDF-Datei!", false);
    }
    
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Legt ein neues Medium an und zeigt es an
   */
  void neuesMediumAnlegen(ISBN isbn) {
    medienTabelleDelayListSelectionListener.fireDelayListSelectionEvent();
    Medium neuesMedium = mediumFactory.erstelleNeu();
    hauptFenster.getStandardwerte().initialisiereMedium(neuesMedium);
    if (isbn != null) {
       neuesMedium.setISBN(isbn);
       MedienabfrageErgebnis erg = Medienabfrage.getInstance().isbnNachschlagenMitTimeout(isbn);
       if (erg != null && erg.enthaeltDaten()) {
         neuesMedium.setTitel(erg.getTitel());
         neuesMedium.setAutor(erg.getAutor());
         if (erg.getBeschreibung() != null) neuesMedium.setBeschreibung(erg.getBeschreibung());      
       }       
    }
    detailsPanel.setMedium(neuesMedium);
    setVeraenderbar(true);
  }

  /**
   * Läd die Orte neu aus der Datenbank
   */
  public void aktualisiere() {
  }

  /**
   * Löscht das aktuelle Medium
   */
  public void loescheMedium() {
    Medium currentMedium = detailsPanel.getMedium();
    boolean loeschenOK = detailsPanel.loescheMedium();
    if (loeschenOK) medienTableModel.remove(currentMedium);
    if (medienTableModel.size() > 0) {
      medienTable.setRowSelectionInterval(0, 0);
      medienTable.scrollRectToVisible(medienTable.getCellRect(0, 0, true));
    } else {
      detailsPanel.setMedium(null);
    }
    initButtons();      
  }

  /**
   * Speichert die gemachten Änderungen
   */
  public void saveChanges() {
    boolean istNeu = detailsPanel.getMedium().istNeu();
    boolean speichernOK = detailsPanel.saveChanges();
    if (speichernOK) {
      setVeraenderbar(false);
      if (istNeu) medienTableModel.add(detailsPanel.getMedium());
    }
    Medium neuesMedium = detailsPanel.getMedium();
    detailsPanel.setMedium(neuesMedium);
    initButtons();      
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  public void aenderungenVerwerfen() {
    boolean ok = detailsPanel.aenderungenVerwerfen();
    if (ok) {
      setVeraenderbar(false);
    }
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
    if (!istVeraenderbar) {
      medienTableModel.select(medium);
    }
  }
  
  public void ISBNGelesen(ISBN isbn) {
    if (!istVeraenderbar) {
      neuesMediumAnlegen(isbn);
    }
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}