package de.oberbrechen.koeb.gui.admin.konfigurierbareBuechereiAusleihordnungReiter;

import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.*;
import de.oberbrechen.koeb.gui.components.einstellungBindung.*;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzern in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class KonfigurierbareBuechereiAusleihordnungReiter extends JPanel
  implements AdminMainReiter {

  private MedientypTableModel medientypTableModel;
  private JTable medientypTable;
  private Main hauptFenster;
  private Vector<JComponentEinstellungBindung> jComponentEinstellungBindungVector;

  /**
   * Erzeugt einen BenutzerReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public KonfigurierbareBuechereiAusleihordnungReiter(Main parentFrame) {
    hauptFenster = parentFrame;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  // erzeugt die GUI
  private void jbInit() throws Exception {
    jComponentEinstellungBindungVector = new Vector<JComponentEinstellungBindung>();
    
    //Oeffnungstage-Panel
    JPanel oeffnungstagePanel = new JPanel();
    oeffnungstagePanel.setLayout(new GridLayout(7,1));
    oeffnungstagePanel.setBorder(BorderFactory.createCompoundBorder(
      new TitledBorder(BorderFactory.createEtchedBorder(new Color(248, 254, 255),
      new Color(121, 124, 136)),"Öffnungstage"),
      BorderFactory.createEmptyBorder(0,5,0,5)));

    JCheckBox tageCheckBox1 = new JCheckBox("Montag");
    JCheckBox tageCheckBox2 = new JCheckBox("Dienstag");
    JCheckBox tageCheckBox3 = new JCheckBox("Mittwoch");
    JCheckBox tageCheckBox4 = new JCheckBox("Donnerstag");
    JCheckBox tageCheckBox5 = new JCheckBox("Freitag");
    JCheckBox tageCheckBox6 = new JCheckBox("Samstag");
    JCheckBox tageCheckBox7 = new JCheckBox("Sonntag");
    oeffnungstagePanel.add(tageCheckBox1, null);
    oeffnungstagePanel.add(tageCheckBox2, null);
    oeffnungstagePanel.add(tageCheckBox3, null);
    oeffnungstagePanel.add(tageCheckBox4, null);
    oeffnungstagePanel.add(tageCheckBox5, null);
    oeffnungstagePanel.add(tageCheckBox6, null);
    oeffnungstagePanel.add(tageCheckBox7, null);

    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();

    Einstellung einstellung1  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Montag");
    Einstellung einstellung2  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Dienstag");
    Einstellung einstellung3  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Mittwoch");
    Einstellung einstellung4  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Donnerstag");
    Einstellung einstellung5  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Freitag");
    Einstellung einstellung6  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Samstag");
    Einstellung einstellung7  = einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "istGeoeffnet.Sonntag");
      
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox1, einstellung1, false));
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox2, einstellung2, false));
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox3, einstellung3, false));
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox4, einstellung4, false));
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox5, einstellung5, false));
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox6, einstellung6, false));
    jComponentEinstellungBindungVector.add(
      new JCheckBoxEinstellungBindung(tageCheckBox7, einstellung7, true));

    //Mahngebühren
    JPanel mahngebuehrPanel = new JPanel();
    mahngebuehrPanel.setLayout(new GridBagLayout());
    mahngebuehrPanel.setBorder(BorderFactory.createCompoundBorder(
      new TitledBorder(BorderFactory.createEtchedBorder(new Color(248, 254, 255),
      new Color(121, 124, 136)),"Mahngebühren"),
      BorderFactory.createEmptyBorder(0,5,0,5)));

    JLabel jLabel1 = new JLabel("Mahngebühr pro Medium pro Woche:");
    JLabel jLabel2 = new JLabel("Kulanzzeit in Tagen:");
    JLabel jLabel3 = new JLabel("EUR");
    JTextField mahngebuehrFeld = new JTextField();
    mahngebuehrFeld.setHorizontalAlignment(JTextField.RIGHT);

    Einstellung mahngebuehrEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareAusleihordnung", 
      "mahngebuehrProMediumProWocheInEuro");
    jComponentEinstellungBindungVector.add(
      new JTextFieldDoubleEinstellungBindung(hauptFenster, mahngebuehrFeld, 
      mahngebuehrEinstellung, 0.25));

    JTextField kulanzzeitFeld = new JTextField();
    kulanzzeitFeld.setHorizontalAlignment(JTextField.RIGHT);
    Einstellung kulanzZeitEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareAusleihordnung", 
      "kulanzZeitInTagen");    
    jComponentEinstellungBindungVector.add(
      new JTextFieldIntEinstellungBindung(hauptFenster, kulanzzeitFeld, 
        kulanzZeitEinstellung, 14));

    mahngebuehrPanel.add(jLabel1,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    mahngebuehrPanel.add(mahngebuehrFeld,        new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mahngebuehrPanel.add(jLabel2,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    mahngebuehrPanel.add(kulanzzeitFeld,    new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    mahngebuehrPanel.add(jLabel3,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    
    //Kontakt
    JPanel kontaktPanel = new JPanel();
    kontaktPanel.setLayout(new GridBagLayout());
    kontaktPanel.setBorder(BorderFactory.createCompoundBorder(
        new TitledBorder(BorderFactory.createEtchedBorder(new Color(248, 254, 255),
            new Color(121, 124, 136)),"Kontakt"),
            BorderFactory.createEmptyBorder(0,5,0,5)));

    JTextField nameFeld = new JTextField();
    Einstellung nameEinstellung = einstellungFactory.getEinstellung(
        "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
    "buechereiName");
    jComponentEinstellungBindungVector.add(
        new JTextFieldEinstellungBindung(hauptFenster, nameFeld, 
            nameEinstellung,"KöB Oberbrechen"));

    JTextField emailFeld = new JTextField();
    Einstellung eMailEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "buechereiEMail");
    jComponentEinstellungBindungVector.add(
       new JTextFieldEinstellungBindung(hauptFenster, emailFeld, 
            eMailEinstellung, "buecherei@koeb-oberbrechen.de"));

    JTextField adminEMailFeld = new JTextField();
    Einstellung adminEMailEinstellung = einstellungFactory.getEinstellung(
        "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
    "adminEMail");
    jComponentEinstellungBindungVector.add(
        new JTextFieldEinstellungBindung(hauptFenster, adminEMailFeld, 
            adminEMailEinstellung, "t_tuerk@gmx.de"));
    
    
    kontaktPanel.add(new JLabel("Büchereiname:"),         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    kontaktPanel.add(nameFeld,        new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    kontaktPanel.add(new JLabel("eMail:"),         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    kontaktPanel.add(emailFeld,        new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    kontaktPanel.add(new JLabel("Admin-eMail:"),         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    kontaktPanel.add(adminEMailFeld,        new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    
    
    //Internetzugang
    JPanel internetzugangPanel = new JPanel();
    internetzugangPanel.setBorder(BorderFactory.createCompoundBorder(
      new TitledBorder(BorderFactory.createEtchedBorder(new Color(248, 254, 255),
      new Color(121, 124, 136)),"Internetzugang"),
      BorderFactory.createEmptyBorder(0,5,0,5)));
    internetzugangPanel.setLayout(new GridBagLayout());

    JTextField einheitslaengeFeld = new JTextField();
    einheitslaengeFeld.setHorizontalAlignment(JTextField.RIGHT);    
    Einstellung internetlaengeEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "laengeInternetEinheitInSec");
    jComponentEinstellungBindungVector.add(
      new JTextFieldIntEinstellungBindung(hauptFenster, einheitslaengeFeld,
      internetlaengeEinstellung, 900));

    JTextField internetKostenFeld = new JTextField();
    internetKostenFeld.setHorizontalAlignment(JTextField.RIGHT);
    Einstellung internetkostenEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
       "internetKostenProEinheitInEuro");
    jComponentEinstellungBindungVector.add(
      new JTextFieldDoubleEinstellungBindung(hauptFenster, internetKostenFeld, 
      internetkostenEinstellung, 0.5));

    JTextField internetKulanzzeitFeld = new JTextField();
    internetKulanzzeitFeld.setHorizontalAlignment(JTextField.RIGHT);
    Einstellung internetKulanzzeitEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "kulanzZeitInternetZugangInSec");
    jComponentEinstellungBindungVector.add(
      new JTextFieldIntEinstellungBindung(hauptFenster, internetKulanzzeitFeld, 
      internetKulanzzeitEinstellung, 90));

    JLabel jLabel4 = new JLabel("Einheitslänge:");
    JLabel jLabel5 = new JLabel("Kosten pro Einheit:");
    JLabel jLabel6 = new JLabel("Kulanzzeit:");
    JLabel jLabel7 = new JLabel("s");
    JLabel jLabel8 = new JLabel("s");
    JLabel jLabel9 = new JLabel("EUR");
    internetzugangPanel.add(jLabel4,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    internetzugangPanel.add(jLabel5,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    internetzugangPanel.add(jLabel6,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    internetzugangPanel.add(einheitslaengeFeld,   new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    internetzugangPanel.add(internetKostenFeld,   new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    internetzugangPanel.add(internetKulanzzeitFeld,  new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    internetzugangPanel.add(jLabel7,     new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    internetzugangPanel.add(jLabel8,     new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    internetzugangPanel.add(jLabel9,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
    
    //MainPanel
    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    mainPanel.setLayout(new GridBagLayout());

    mainPanel.add(kontaktPanel,        new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(oeffnungstagePanel,         new GridBagConstraints(0, 1, 1, 2, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 10), 0, 0));
    mainPanel.add(mahngebuehrPanel,        new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    mainPanel.add(internetzugangPanel,     new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));

    
    //Tabelle
    JPanel medienTypPanel = new JPanel();
    medientypTable = new JTable();
    medientypTableModel = 
      new MedientypTableModel(hauptFenster);
    ErweiterterTableCellRenderer.setzeErweitertesModell(medientypTableModel, medientypTable);
    medientypTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    medientypTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane jScrollPane1 = new JScrollPane(medientypTable);
    jScrollPane1.setMinimumSize(new Dimension(150,150));
    jScrollPane1.setPreferredSize(new Dimension(150,150));
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));
  
    medienTypPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));    
    medienTypPanel.setLayout(new BorderLayout());
    medienTypPanel.add(jScrollPane1, BorderLayout.CENTER);
    
    
    //Alles zusammenbauen
    this.setLayout(new BorderLayout());
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    this.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(mainPanel, JSplitPane.TOP);
    jSplitPane1.add(medienTypPanel, JSplitPane.BOTTOM);
  }

  public void aktualisiere() {
    refresh();
  }

  public void refresh() {
    Iterator<JComponentEinstellungBindung> it = jComponentEinstellungBindungVector.iterator();
    while (it.hasNext()) {
      it.next().refresh();
    }

    try {
      medientypTableModel.refresh();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}