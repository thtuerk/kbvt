package de.oberbrechen.koeb.gui.veranstaltungen.listenReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.TeilnehmerlisteAusgabenFactory;
import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsteilnahmeListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.email.EMail;
import de.oberbrechen.koeb.email.EMailHandler;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.veranstaltungen.Main;
import de.oberbrechen.koeb.gui.veranstaltungen.VeranstaltungenMainReiter;
import de.oberbrechen.koeb.pdf.pdfTeilnehmerListe.PdfTeilnehmerlisteAusgabenFactory;


/**
 * Diese Klasse repräsentiert den Reiter, der die Ausgabe von Listen darstellt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class ListenReiter extends JPanel implements VeranstaltungenMainReiter {

  private Main hauptFenster;
  private VeranstaltungenTableModel veranstaltungenModel;
  boolean listenModelWirdVeraendert;
  private JTable veranstaltungenTable;

  private TeilnehmerlisteAusgabenFactory teilnehmerlisteAusgabeFactory;

  ListenTableModel listenModel;
  private JTable listenTable;
  private JRadioButton nameButton;
  private JRadioButton klasseButton;
  private JRadioButton anmeldeDatumButton;
  private JRadioButton bemerkungenButton;

  private JCheckBox bemerkungenCheckBox;
  private JCheckBox beschreibungCheckBox;
  
  boolean istVeraenderbar;
  private JButton bearbeitenButton;
  private JButton verwerfenButton;
  private JButton emailButton;
  private JButton pdfButton;
  private JButton warteListeButton;
  private int warteListenAktion;
  
  private String aufWarteListeString =
    Datenbank.getInstance().getEinstellungFactory().getEinstellung(
      "de.oberbrechen.koeb.einstellungen", "aufWarteListeString").getWert("noch nicht bezahlt");
  private String aufTeilnehmerListeString =
    Datenbank.getInstance().getEinstellungFactory().getEinstellung(
        "de.oberbrechen.koeb.einstellungen", "aufTeilnehmerListeString").getWert("schon bezahlt");
  
  public ListenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    
    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();
    try {
      teilnehmerlisteAusgabeFactory = (TeilnehmerlisteAusgabenFactory) 
        einstellungFactory.getClientEinstellung(
        this.getClass().getName(), "TeilnehmerlisteAusgabe").
        getWertObject(TeilnehmerlisteAusgabenFactory.class,
        PdfTeilnehmerlisteAusgabenFactory.class);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }

    try {
      jbInit();
      benutzerGewechselt();
    }
    catch(Exception e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    setVeraenderbar(false);
  }

  private void jbInit() throws Exception {
    //Button-Panel bauen
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));

    bearbeitenButton = new JButton("Bearbeiten");
    bearbeitenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (istVeraenderbar) {
          saveChanges();
        } else {
          setVeraenderbar(true);
        }
      }
    });
    verwerfenButton = new JButton("Änderungen verwerfen");
    verwerfenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        aenderungenVerwerfen();
      }
    });
    pdfButton = new JButton("Liste erstellen");
    pdfButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            pdfErstellen();
          }
        }.start();
      }
    });

    emailButton = new JButton("eMail");
    emailButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            emailErstellen();
          }
        }.start();
      }
    });

    buttonPanel.add(bearbeitenButton, null);
    buttonPanel.add(verwerfenButton, null);
    buttonPanel.add(emailButton, null);
    buttonPanel.add(pdfButton, null);


    //Liste bauen
    warteListeButton = new JButton("-");
    warteListeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        warteListeWechseln();
      }
    });
    
    listenTable = new JTable();
    listenModel = new ListenTableModel(listenTable);
    listenModel.addTableModelListener(new javax.swing.event.TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        if (!istVeraenderbar && !listenModelWirdVeraendert) 
          setVeraenderbar(true);
      }
    });        
    ErweiterterTableCellRenderer.setzeErweitertesModell(listenModel, listenTable);
    listenTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    listenTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listenTable.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          benutzerGewechselt();
        }
    });
    JScrollPane listenScrollPane = new JScrollPane(listenTable);
    listenScrollPane.setMinimumSize(new Dimension(200, 30));

    JPanel listenPanel = new JPanel();
    listenPanel.setLayout(new BorderLayout());
    listenPanel.setBorder(BorderFactory.createEmptyBorder(15,6,15,10));
    warteListeButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5,0,0,0), 
        warteListeButton.getBorder()));    
    listenPanel.setPreferredSize(new Dimension(200, 30));
    listenPanel.add(listenScrollPane, BorderLayout.CENTER);
    listenPanel.add(warteListeButton, BorderLayout.SOUTH);

    //Veranstaltungenauswahl bauen
    veranstaltungenModel = new VeranstaltungenTableModel(null);
    veranstaltungenTable = new JTable();
    ErweiterterTableCellRenderer.setzeErweitertesModell(veranstaltungenModel, 
        veranstaltungenTable);
    veranstaltungenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    veranstaltungenTable.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          veranstaltungGewechselt();
        }
    });
    JScrollPane veranstaltungenScrollPane=new JScrollPane(veranstaltungenTable);
    veranstaltungenScrollPane.setPreferredSize(new Dimension(300, 30));

    ActionListener sortierungAuswahlListener = new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        int sortierung = getSortierung();
        listenModelWirdVeraendert = true;
        listenModel.sortiere(sortierung);
        listenModelWirdVeraendert = false;
      }
    };
    nameButton = new JRadioButton("Name");
    klasseButton = new JRadioButton("Klasse");
    anmeldeDatumButton = new JRadioButton("Anmeldedatum");
    bemerkungenButton = new JRadioButton("Bemerkungen");

    nameButton.addActionListener(sortierungAuswahlListener);
    klasseButton.addActionListener(sortierungAuswahlListener);
    anmeldeDatumButton.addActionListener(sortierungAuswahlListener);
    bemerkungenButton.addActionListener(sortierungAuswahlListener);

    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(nameButton);
    buttonGroup.add(klasseButton);
    buttonGroup.add(anmeldeDatumButton);
    buttonGroup.add(bemerkungenButton);
    nameButton.setSelected(true);

    JPanel sortierungPanel = new JPanel();
    sortierungPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5,0));
    sortierungPanel.add(nameButton, null);
    sortierungPanel.add(klasseButton, null);
    sortierungPanel.add(anmeldeDatumButton, null);
    sortierungPanel.add(bemerkungenButton, null);
    sortierungPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    
    JPanel pdfPanel = new JPanel();
    pdfPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5,0));
    bemerkungenCheckBox = new JCheckBox("Bemerkungen", true);
    beschreibungCheckBox = new JCheckBox("Beschreibung",true);
    pdfPanel.add(beschreibungCheckBox, null);
    pdfPanel.add(bemerkungenCheckBox, null);
    pdfPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

    JPanel ausgabePanel = new JPanel();
    ausgabePanel.setLayout(new BorderLayout());
    ausgabePanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));    
    ausgabePanel.add(sortierungPanel, BorderLayout.NORTH);
    ausgabePanel.add(pdfPanel, BorderLayout.SOUTH);
    
    JPanel veranstaltungenPanel = new JPanel();
    veranstaltungenPanel.setLayout(new BorderLayout());
    veranstaltungenPanel.setBorder(BorderFactory.createEmptyBorder(15,10,15,6));
    veranstaltungenPanel.add(veranstaltungenScrollPane, BorderLayout.CENTER);
    veranstaltungenPanel.add(ausgabePanel, BorderLayout.SOUTH);
    veranstaltungenPanel.setPreferredSize(new Dimension(400, 30));
    veranstaltungenPanel.setMinimumSize(new Dimension(400, 30));

    //Splitpanel
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane1.add(listenPanel, JSplitPane.RIGHT);
    jSplitPane1.add(veranstaltungenPanel, JSplitPane.LEFT);
    jSplitPane1.setResizeWeight(0.5);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(jSplitPane1, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob die aktuelle
   * Liste verändert werden darf.
   * @param veraenderbar ist Liste veraenderbar oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    hauptFenster.erlaubeAenderungen(!veraenderbar);
    veranstaltungenTable.setEnabled(!veraenderbar);
    initButtons();
  }
  
  private void initButtons() {
    int gewaehlteReihe = veranstaltungenTable.getSelectedRow();
    boolean veranstaltungGesetzt = (gewaehlteReihe >= 0);
    
    
    if (gewaehlteReihe < 0) {
      nameButton.setEnabled(false);
      anmeldeDatumButton.setEnabled(false);
      bemerkungenButton.setEnabled(false);
      klasseButton.setEnabled(false);
    } else if (gewaehlteReihe == 0) {
      nameButton.setEnabled(true);
      anmeldeDatumButton.setEnabled(false);
      bemerkungenButton.setEnabled(false);
      klasseButton.setEnabled(false);
    } else {
      nameButton.setEnabled(true);
      anmeldeDatumButton.setEnabled(true);
      bemerkungenButton.setEnabled(true);
      klasseButton.setEnabled(true);      
    }
    
    emailButton.setEnabled(!istVeraenderbar && veranstaltungGesetzt &&
        EMailHandler.getInstance().erlaubtAnzeige());
    pdfButton.setEnabled(!istVeraenderbar && veranstaltungGesetzt);
    beschreibungCheckBox.setEnabled(!istVeraenderbar && veranstaltungGesetzt);
    bemerkungenCheckBox.setEnabled(!istVeraenderbar && veranstaltungGesetzt);
    
    verwerfenButton.setEnabled(istVeraenderbar && veranstaltungGesetzt);
          
    if (!istVeraenderbar) {
      bearbeitenButton.setText("Bearbeiten");
    } else {
      bearbeitenButton.setText("Speichern");
    }
    
    bearbeitenButton.setEnabled(veranstaltungGesetzt);    
  }

  /**
   * Wird aufgerufen, wenn einen neue Veranstaltung in der Tabelle ausgewählt
   * wird.
   */
  void veranstaltungGewechselt() {
    int gewaehlteReihe = veranstaltungenTable.getSelectedRow();
    if (gewaehlteReihe == -1) return;
    if (gewaehlteReihe == 0) {
      nameButton.setSelected(true);
      VeranstaltungsteilnahmeFactory veranstaltungsteilnahmeFactory =
        Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
      BenutzerListe benutzerliste =
        veranstaltungsteilnahmeFactory.getTeilnehmerListe(
        hauptFenster.getAktuelleVeranstaltungsgruppe());
      listenModelWirdVeraendert = true;
      listenModel.setDaten(benutzerliste);
      listenModel.sortiere(getSortierung());
      listenModelWirdVeraendert = false;
    } else {
      VeranstaltungsteilnahmeFactory veranstaltungsteilnahmeFactory =
        Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
      VeranstaltungsteilnahmeListe teilnahmeListe =
        veranstaltungsteilnahmeFactory.getTeilnahmeListe(
        veranstaltungenModel.getVeranstaltung(gewaehlteReihe));        
        
      listenModelWirdVeraendert = true;        
      listenModel.setDaten(teilnahmeListe);
      listenModel.sortiere(getSortierung());
      listenModelWirdVeraendert = false;      
    }
    initButtons();
  }

  /**
   * Wird aufgerufen, wenn einen neuer Benutzer in der Tabelle ausgewählt
   * wird.
   */
  void benutzerGewechselt() {    
    int[] gewaehlteZeilen = listenTable.getSelectedRows();
    
    boolean enabled = true;
    boolean aufTeilnehmerliste = false;
    boolean aufNichtBlockierenderWarteliste = false;
    boolean aufBlockierenderWarteliste = false;
        
    Veranstaltungsteilnahme teilnahme = null; 
    if (gewaehlteZeilen.length == 1) {
      teilnahme = listenModel.getVeranstaltungsteilnahme(gewaehlteZeilen[0]);      
      Benutzer gewaehlterBenutzer = listenModel.getBenutzer(gewaehlteZeilen[0]);
      hauptFenster.setAktiverBenutzer(gewaehlterBenutzer);      
    }
    
    for (int i=0; i < gewaehlteZeilen.length && enabled; i++) {
      teilnahme = listenModel.getVeranstaltungsteilnahme(gewaehlteZeilen[i]);
      
      if (teilnahme != null) {
        int status = teilnahme.getStatus();
        if (status == Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET) {
          aufTeilnehmerliste = true;
        } else if (status == Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND) {
          aufNichtBlockierenderWarteliste = true;
        } else if (status == Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND) {
          aufBlockierenderWarteliste = true;          
        }
      } else {
        enabled = false;
      }
    }    

    //Aktion bestimmen
    if (!enabled || gewaehlteZeilen.length <= 0 || (aufTeilnehmerliste && (aufBlockierenderWarteliste || aufNichtBlockierenderWarteliste))) {
      enabled = false;
    } else {
      enabled = true;
      if (aufTeilnehmerliste) {
        warteListenAktion = Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND;
      } else {
        if (aufBlockierenderWarteliste) {
          warteListenAktion = Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET;
        } else if (aufNichtBlockierenderWarteliste) {
          warteListenAktion = Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND;          
        } else {
          //sollte eigentlich nie vorkommen können
          ErrorHandler.getInstance().handleException(new Exception("Unerwarteter Zustand"), false);
          enabled = false;
        }
      }
    }
    

    if (!enabled) {
      warteListeButton.setText("auf Warteliste setzen");
      warteListeButton.setEnabled(false);
    } else {
      warteListeButton.setEnabled(true);
      switch (warteListenAktion) {
        case Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET:
          warteListeButton.setText("auf Teilnehmerliste setzen");                
          break;
        case Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND:
          warteListeButton.setText("auf blockierende Warteliste setzen");                
          break;
        case Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND:
          warteListeButton.setText("auf Warteliste setzen");                
          break;
      }
    }    
  }

  protected void warteListeWechseln() {
    int[] gewaehlteZeilen = listenTable.getSelectedRows();
    Veranstaltungsteilnahme[] teilnahmen = 
      new Veranstaltungsteilnahme[gewaehlteZeilen.length];

    for (int i=0; i < gewaehlteZeilen.length; i++) {
      teilnahmen[i] = listenModel.getVeranstaltungsteilnahme(gewaehlteZeilen[i]);
    }        

    for (int i=0; i < gewaehlteZeilen.length; i++) {
      if (teilnahmen[i] != null) {      
        try {
          boolean istAufWarteListeVorher = teilnahmen[i].istAufWarteListe();
          teilnahmen[i].setStatus(warteListenAktion);
          boolean istAufWarteListeNachher = teilnahmen[i].istAufWarteListe();          
          
          if (istAufWarteListeVorher != istAufWarteListeNachher) {
            String alterAnhang, neuerAnhang;
            if (istAufWarteListeVorher) {
              alterAnhang = aufTeilnehmerListeString;            
              neuerAnhang = aufWarteListeString;
            } else {
              alterAnhang = aufWarteListeString;
              neuerAnhang = aufTeilnehmerListeString;
            }
            
            String bemerkungen = teilnahmen[i].getBemerkungen(); 
            if (bemerkungen != null && bemerkungen.endsWith(alterAnhang)) {
              bemerkungen = bemerkungen.substring(0, bemerkungen.length()-alterAnhang.length());
              if (bemerkungen.length() > 0)
                bemerkungen = bemerkungen.substring(0, bemerkungen.length()-2);
            } else {
              if (bemerkungen == null || bemerkungen.length() == 0) {
                bemerkungen = neuerAnhang;
              } else {
                bemerkungen = bemerkungen+"; "+neuerAnhang;
              }
            }
            teilnahmen[i].setBemerkungen(bemerkungen);
          }          
          
        } catch (Exception e) {
          ErrorHandler.getInstance().handleException(e, 
              "Fehler beim Ändern der Anmeldung!", false);
        }
      }
    }
    listenModel.fireTableRowsUpdated(0, listenModel.getRowCount());
    veranstaltungenModel.fireTableRowsUpdated(0, veranstaltungenModel.getRowCount());
    benutzerGewechselt();
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
  }

  //Doku siehe bitte Interface
  public void setVeranstaltungsgruppe(Veranstaltungsgruppe gruppe) {
    veranstaltungenModel.setVeranstaltungsgruppe(gruppe);
    if (veranstaltungenTable.getRowCount() > 0) {
      veranstaltungenTable.getSelectionModel().setSelectionInterval(0,0);
    } else {
      veranstaltungenTable.clearSelection();      
    }
    initButtons();
  }

  /**
   * Bestimmt die aktuell ausgewählte Sortierung der Teilnehmerliste
   */
  public int getSortierung() {
    if (nameButton.isSelected()) return ListenTableModel.SORTIERUNG_NAME;
    if (klasseButton.isSelected()) return ListenTableModel.SORTIERUNG_KLASSE;
    if (anmeldeDatumButton.isSelected())
      return ListenTableModel.SORTIERUNG_ANMELDENR;
    if (bemerkungenButton.isSelected()) return ListenTableModel.SORTIERUNG_BEMERKUNGEN;

    return -1;
  }

  /**
   * Gibt die Liste in eine PDF-Datei aus
   */
  private void pdfErstellen() {
    int gewaehlteReihe = veranstaltungenTable.getSelectedRow();

    if (gewaehlteReihe == -1) return;
    Ausgabe ausgabe;
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      if (gewaehlteReihe == 0) {
        Veranstaltungsgruppe gruppe = hauptFenster.getAktuelleVeranstaltungsgruppe();
        ausgabe = teilnehmerlisteAusgabeFactory.createTeilnehmerlisteVeranstaltungsgruppeAusgabe(
          gruppe, bemerkungenCheckBox.isSelected());
      } else {
        ausgabe = teilnehmerlisteAusgabeFactory.createTeilnehmerlisteVeranstaltungAusgabe(
          veranstaltungenModel.getVeranstaltung(gewaehlteReihe), getSortierung(),
          bemerkungenCheckBox.isSelected(), beschreibungCheckBox.isSelected());
      }
      ausgabe.run(hauptFenster, true);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e,
        "Die PDF-Datei konnte nicht ausgegeben werden.", false);
    }
    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }


  /**
   * Schreibt eine eMail an alle Teilnehmer
   */
  private void emailErstellen() {
    int gewaehlteReihe = veranstaltungenTable.getSelectedRow();

    if (gewaehlteReihe == -1) return;
    
    BenutzerListe benutzer;
    if (gewaehlteReihe == 0) {
      Veranstaltungsgruppe gruppe = hauptFenster.getAktuelleVeranstaltungsgruppe();
      benutzer = Datenbank.getInstance().getVeranstaltungsteilnahmeFactory().getTeilnehmerListe(gruppe);
    } else {
      Veranstaltung veranstaltung = veranstaltungenModel.getVeranstaltung(gewaehlteReihe);
      benutzer = Datenbank.getInstance().getVeranstaltungsteilnahmeFactory().getTeilnehmerListe(veranstaltung);
    }
    
    benutzer.setSortierung(BenutzerListe.VornameNachnameSortierung);
    EMail email = new EMail();
    email.addEmpfaenger(Buecherei.getInstance().getBuechereiEmail());
    
    BenutzerListe ohneeMail = new BenutzerListe();
    for (int i=0; i < benutzer.size(); i++) {
      Benutzer currentBenutzer = (Benutzer) benutzer.get(i);
      if (currentBenutzer.getEMail() != null) {
        email.addBlindkopieEmpfaenger(currentBenutzer.getEMailMitName());
      } else {
        ohneeMail.add(currentBenutzer);
      }
    }
    
    if (ohneeMail.size() > 0) {
      ohneeMail.setSortierung(BenutzerListe.NachnameVornameSortierung);
      StringBuffer buffer =new StringBuffer();
      buffer.append("Keine eMail haben erhalten:\n");
      for (int i=0; i < ohneeMail.size(); i++) {
        Benutzer currentBenutzer = (Benutzer) ohneeMail.get(i);
        buffer.append(currentBenutzer.getNameFormal());
        if (currentBenutzer.getTel() != null) {
          buffer.append(" (Tel. ");
          buffer.append(currentBenutzer.getTel());
          buffer.append(")");
        }
        buffer.append("\n");
      }

      if (gewaehlteReihe == 0) {
        email.setNachricht(buffer.toString());
      } else {
        try {
          BemerkungVeranstaltungFactory factory = Datenbank.getInstance().getBemerkungVeranstaltungFactory();
          BemerkungVeranstaltung bemerkung = factory.erstelleNeu();
          bemerkung.setVeranstaltung(veranstaltungenModel.getVeranstaltung(gewaehlteReihe));
          bemerkung.setBemerkung(buffer.toString());
          bemerkung.save();
        } catch (DatenbankzugriffException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern der Bemerkung!", false);
        }
      }
    }
    
    EMailHandler.getInstance().versende(email, true);    
  }
  
  /**
   * Speichert die gemachten Änderungen
   */
  public void saveChanges() {
    if (listenTable.isEditing()) {
      JOptionPane.showMessageDialog(hauptFenster, "Bitte beenden Sie zuerst "+
        "die aktuelle Bearbeitung!",
        "Noch in Bearbeitung!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    listenModel.save();
    this.setVeraenderbar(false);
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  public void aenderungenVerwerfen() {
    if (listenTable.isEditing()) {
      JOptionPane.showMessageDialog(hauptFenster, "Bitte beenden Sie zuerst "+
        "die aktuelle Bearbeitung!",
        "Noch in Bearbeitung!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    listenModel.reload();
    this.setVeraenderbar(false);
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}