package de.oberbrechen.koeb.gui.admin.dokumentierteEinstellungenReiter;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungBeschreibung;
import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungTest;
import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.ClientListe;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die für
 * Auswahl von Ausgaben gedacht ist.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class DokumentierteEinstellungenReiter extends JPanel implements AdminMainReiter {
  private Main hauptFenster;
  private TreeModel model;
  private EinstellungBeschreibung aktuelleEinstellungBeschreibung;
  private boolean wertWirdGeradeGeaendert;
  
  private JTextArea beschreibung;
  private JTextField nameFeld;
  private SortiertComboBox<String> wertFeld;
  private SortiertComboBox<Mitarbeiter> mitarbeiterFeld;
  private SortiertComboBox<Client> clientFeld;
  private JButton testButton;
  private JButton standardWertButton;
  private JButton clientSetzenButton;
  private JButton mitarbeiterSetzenButton;
  private JButton clientMitarbeiterSetzenButton;
  
  private JTextField mitarbeiterFestFeld;
  private JTextField clientFestFeld;
  
  public DokumentierteEinstellungenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    try {
      jbInit();
      setNeueEinstellung(null);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    //Panel bauen
    JPanel allgemeinPanel = new JPanel();
    allgemeinPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
    allgemeinPanel.setLayout(new GridBagLayout());
    beschreibung = new JTextArea();
    beschreibung.setEditable(false);
    beschreibung.setLineWrap(true);
    beschreibung.setWrapStyleWord(true);
    standardWertButton = new JButton("Standardwert setzen");
    standardWertButton.setEnabled(false);
    standardWertButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        wertFeld.setSelectedItem(aktuelleEinstellungBeschreibung.getStandardWert());
      }
    });
    
    
    testButton = new JButton("Einstellung testen");
    testButton.setEnabled(false);
    testButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            EinstellungTest test = aktuelleEinstellungBeschreibung.getTest();
            if (test != null)
              try {
                test.testeEinstellung(hauptFenster, getAktuelleEinstellung());
              } catch (Exception e) {
                ErrorHandler.getInstance().handleException(e, "Der Test " +
                    "schlug fehl:", false);
              }
          }
        }.start();
      }
    });
    nameFeld = new JTextField();
    nameFeld.setEditable(false);   

    JScrollPane jScrollPaneBeschreibung = new JScrollPane(beschreibung);
    JComponentFormatierer.setDimension(jScrollPaneBeschreibung, new Dimension(200, 10));
    clientFeld = new SortiertComboBox<Client>(new Format<Client>() {
      public String format(Client o) {
        return o.getName();
      }      
    });
    mitarbeiterFeld = new SortiertComboBox<Mitarbeiter>(new Format<Mitarbeiter>() {
      public String format(Mitarbeiter o) {
        return o.getBenutzer().getNameFormal();
      }      
    });
    mitarbeiterFeld.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        updateWert();
      }});
    clientFeld.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        updateWert();
      }});
    
    wertFeld = new SortiertComboBox<String>();    
    JComponentFormatierer.setDimension(wertFeld, wertFeld.getPreferredSize());
    wertFeld.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        if (!wertWirdGeradeGeaendert && 
            arg0.getStateChange() == ItemEvent.SELECTED) saveEinstellung();
      }});
    
    mitarbeiterSetzenButton = new JButton("für alle Mitarbeiter setzen");
    mitarbeiterSetzenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          Einstellung einstellung = getAktuelleEinstellung();
          einstellung.setWert((String) wertFeld.getSelectedItem());
          einstellung.saveAlleMitarbeiter();
        } catch (UnvollstaendigeDatenException e) {
          //Sollte nie auftreten
          ErrorHandler.getInstance().handleException(e, false);
        }
      }});
    clientSetzenButton = new JButton("für alle Clients setzen");
    clientSetzenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          Einstellung einstellung = getAktuelleEinstellung();
          einstellung.setWert((String) wertFeld.getSelectedItem());
          einstellung.saveAlleClients();
        } catch (UnvollstaendigeDatenException e) {
          //Sollte nie auftreten
          ErrorHandler.getInstance().handleException(e, false);
        }
      }});
    clientMitarbeiterSetzenButton = new JButton("für alle Mitarbeiter und Clients setzen");
    clientMitarbeiterSetzenButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          Einstellung einstellung = getAktuelleEinstellung();
          einstellung.setWert((String) wertFeld.getSelectedItem());
          einstellung.saveAlleClientsUndMitarbeiter();
        } catch (UnvollstaendigeDatenException e) {
          //Sollte nie auftreten
          ErrorHandler.getInstance().handleException(e, false);
        }
      }});
    
    this.setMinimumSize(new Dimension(174, 168));
    this.setLayout(new BorderLayout());
    allgemeinPanel.add(new JLabel("Name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(10, 0, 5, 0), 0, 0));
    allgemeinPanel.add(nameFeld, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    allgemeinPanel.add(new JLabel("Beschreibung:"),  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    allgemeinPanel.add(jScrollPaneBeschreibung,       new GridBagConstraints(1, 1, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    allgemeinPanel.add(new JLabel("Client:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    allgemeinPanel.add(clientFeld, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 10), 0, 0));
    allgemeinPanel.add(new JLabel("Mitarbeiter:"), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
    allgemeinPanel.add(mitarbeiterFeld, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    allgemeinPanel.add(new JLabel("Wert:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    allgemeinPanel.add(wertFeld, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));     

    
    allgemeinPanel.add(testButton,         new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
    allgemeinPanel.add(standardWertButton, new GridBagConstraints(2, 4, 2, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    
    allgemeinPanel.add(clientSetzenButton, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 5, 5), 0, 0));
    allgemeinPanel.add(mitarbeiterSetzenButton, new GridBagConstraints(2, 5, 2, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 5, 0), 0, 0));
    allgemeinPanel.add(clientMitarbeiterSetzenButton, new GridBagConstraints(0, 6, 4, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    
    
    //Baum bauen
    model = new EinstellungenTreeModel();
    JTree einstellungenTree = new JTree(model);

    einstellungenTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent event) {
        final TreePath tp = event.getNewLeadSelectionPath();
        if (tp != null && ((TreeNode) tp.getLastPathComponent()).isLeaf()) {
          setNeueEinstellung(((DefaultMutableTreeNode) tp.getLastPathComponent()));
        } else {
          setNeueEinstellung(null);
        }
      }
    }
    );
    
    mitarbeiterFestFeld = new JTextField();
    mitarbeiterFestFeld.setEditable(false);
    
    clientFestFeld = new JTextField();
    clientFestFeld.setEditable(false);
    
    JPanel einstellungenPanel = new JPanel();
    einstellungenPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    einstellungenPanel.setLayout(new GridBagLayout());
    einstellungenPanel.add(new JLabel("Client:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
    einstellungenPanel.add(clientFestFeld, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 10), 0, 0));
    einstellungenPanel.add(new JLabel("Mitarbeiter:"), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
    einstellungenPanel.add(mitarbeiterFestFeld, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    JScrollPane jScrollPane = new JScrollPane(einstellungenTree);
    JComponentFormatierer.setDimension(jScrollPane, new Dimension(200, 150));
    einstellungenPanel.add(jScrollPane, new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    
    //alles zusammenbauen
    JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    jSplitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jSplitPane.add(einstellungenPanel, JSplitPane.TOP);
    jSplitPane.add(allgemeinPanel, JSplitPane.BOTTOM);
    add(jSplitPane, BorderLayout.CENTER);
  }

  protected void saveEinstellung() {
    if (aktuelleEinstellungBeschreibung == null) return;

    wertWirdGeradeGeaendert = true;   
    String wert = (String) wertFeld.getSelectedItem();
    String fehler = aktuelleEinstellungBeschreibung.checkWert(wert);
    if (fehler != null)
      JOptionPane.showMessageDialog(hauptFenster,
          fehler,
          "Ungültiger Wert",
          JOptionPane.ERROR_MESSAGE);                   
      
    Einstellung aktuelleEinstellung = getAktuelleEinstellung();
    aktuelleEinstellung.setWert(wert);
    try {
      aktuelleEinstellung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }    

    wertWirdGeradeGeaendert = false;   
  }

  Einstellung getAktuelleEinstellung() {
    if (aktuelleEinstellungBeschreibung == null) return null;
    
    Client client = null;
    if (aktuelleEinstellungBeschreibung.istClientEinstellung())
      client = (Client) clientFeld.getSelectedItem();

    Mitarbeiter mitarbeiter = null;
    if (aktuelleEinstellungBeschreibung.istMitarbeiterEinstellung())
      mitarbeiter = (Mitarbeiter) mitarbeiterFeld.getSelectedItem();
       
    return Datenbank.getInstance().getEinstellungFactory().getEinstellung(
        client, mitarbeiter, aktuelleEinstellungBeschreibung.getName());
  }
    
  void updateWert() {
    Einstellung einstellung = getAktuelleEinstellung();
    String wert = null;
    
    if (einstellung != null)
      wert = einstellung.getWert(aktuelleEinstellungBeschreibung.getStandardWert());
    wertFeld.setSelectedItem(wert);
  }
    
  void setNeueEinstellung(DefaultMutableTreeNode node) {
    wertWirdGeradeGeaendert = true;
    wertFeld.removeAllItems();
    
    if (node == null) {
      nameFeld.setText("");
      wertFeld.setSelectedItem("");
      wertFeld.setEnabled(false);
      wertFeld.setEditable(false);
      aktuelleEinstellungBeschreibung = null;
      beschreibung.setText("");
      testButton.setEnabled(false);
      standardWertButton.setEnabled(false);
      clientFeld.setEnabled(false);
      mitarbeiterFeld.setEnabled(false);
      clientSetzenButton.setEnabled(false);
      mitarbeiterSetzenButton.setEnabled(false);
      clientMitarbeiterSetzenButton.setEnabled(false);
    } else {
      aktuelleEinstellungBeschreibung = (EinstellungBeschreibung) node.getUserObject();
      
      nameFeld.setText(aktuelleEinstellungBeschreibung.getName());
      beschreibung.setText(aktuelleEinstellungBeschreibung.getBeschreibung());
      testButton.setEnabled(aktuelleEinstellungBeschreibung.getTest() != null);
            
      wertFeld.setEnabled(true);
      wertFeld.setEditable(aktuelleEinstellungBeschreibung.istEditable());
      wertFeld.addAll(aktuelleEinstellungBeschreibung.getWerteAuswahl());
      updateWert();
      
      String standardWert = aktuelleEinstellungBeschreibung.getStandardWert(); 
      standardWertButton.setEnabled(standardWert != null && standardWert.trim().length() > 0);
      
      
      clientFeld.setEnabled(aktuelleEinstellungBeschreibung.istClientEinstellung());
      mitarbeiterFeld.setEnabled(aktuelleEinstellungBeschreibung.istMitarbeiterEinstellung());
      clientSetzenButton.setEnabled(aktuelleEinstellungBeschreibung.istClientEinstellung());
      mitarbeiterSetzenButton.setEnabled(aktuelleEinstellungBeschreibung.istMitarbeiterEinstellung());
      clientMitarbeiterSetzenButton.setEnabled(aktuelleEinstellungBeschreibung.istClientEinstellung() && aktuelleEinstellungBeschreibung.istMitarbeiterEinstellung());      
    }
    wertWirdGeradeGeaendert = false;
  }

  public void aktualisiere() {    
  }

  public void refresh() {
    ClientListe clients =  Datenbank.getInstance().
      getClientFactory().getAlleClients();
    clients.setSortierung(ClientListe.NameSortierung);
    clientFeld.setDaten(clients);    
    Client benutzterClient = Datenbank.getInstance().getClientFactory(). getBenutzenClient();
    clientFeld.setSelectedItem(benutzterClient);
    clientFestFeld.setText(benutzterClient.getName());
    
    MitarbeiterListe mitarbeiter = Datenbank.getInstance().
      getMitarbeiterFactory().getAlleMitarbeiter();
    mitarbeiter.setSortierung(MitarbeiterListe.NachnameVornameSortierung);
    mitarbeiterFeld.setDaten(mitarbeiter);        
    Mitarbeiter aktuellerMitarbeiter = Datenbank.getInstance().getMitarbeiterFactory().getAktuellenMitarbeiter(); 
    mitarbeiterFeld.setSelectedItem(aktuellerMitarbeiter);
    if (aktuellerMitarbeiter != null) {
      mitarbeiterFestFeld.setText(aktuellerMitarbeiter.getBenutzer().getName()+" ("+aktuellerMitarbeiter.getId()+")");
    }
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}