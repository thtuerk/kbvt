package de.oberbrechen.koeb.gui.bestand.ausgabenReiter;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import de.oberbrechen.koeb.ausgaben.KonfigurierbareAusgabe;
import de.oberbrechen.koeb.ausgaben.MedienAusgabe;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.dateien.medienAusgabenKonfiguration.MedienAusgabeFactoryBeschreibung;
import de.oberbrechen.koeb.dateien.medienAusgabenKonfiguration.MedienAusgabeKonfigurationXMLDatei;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.bestand.BestandMainReiter;
import de.oberbrechen.koeb.gui.bestand.Main;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.mediumListenAuswahlPanel.MediumListenAuswahlPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Medien-Daten 
 * ausgegeben werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AusgabenReiter extends JPanel implements BestandMainReiter {

  protected Main hauptFenster;
  protected MediumListenAuswahlPanel listenAuswahlPanel;
  protected SortiertComboBox<MedienAusgabe> ausgabeComboBox;
  
  public AusgabenReiter(Main hauptFenster) {
    this.hauptFenster=hauptFenster;
    try {
      jbInit();
      initAuswahlFeld();    
      initAusgabeFeld();
      aktualisiere();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  private void initAusgabeFeld() {
    MedienAusgabeKonfigurationXMLDatei konfigFile = 
      MedienAusgabeKonfigurationXMLDatei.getInstance();
    
    MedienAusgabeFactoryBeschreibung[] daten = konfigFile.getDaten();
    for (int i=0; i < daten.length; i++) {
      try {
        ausgabeComboBox.addItem(daten[i].createMedienAusgabeFactory().createMedienAusgabe());
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Fehler beim Initialisieren der Ausgaben!", false);
      }
    }    
  }

  // erzeugt die GUI
  void jbInit() throws Exception {    
    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    listenAuswahlPanel = new MediumListenAuswahlPanel(false, true);
    listenAuswahlPanel.setMinimumSize(new Dimension(200, 50));

    ausgabeComboBox = new SortiertComboBox<MedienAusgabe>(false, new Format<MedienAusgabe>() {    
      public String format(MedienAusgabe o) {
        if (o == null) return null;
        return o.getName();
      }    
    });
    JButton ausfuehrenButton = new JButton("Ausführen");
    ausfuehrenButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        MedienListe daten = new MedienListe();
        daten.addAllNoDuplicate(listenAuswahlPanel.getAuswahl());
        try {
          MedienAusgabe ausgabe = ((MedienAusgabe) 
          ausgabeComboBox.getSelectedItem());
          ausgabe.setDaten(daten);
          ausgabe.run(hauptFenster, true);
        } catch (Exception e) {
          ErrorHandler.getInstance().handleException(e, 
              "Fehler beim Ausführen der Medienausgabe!", false);
        }
      }});
    final JButton konfigurierenButton = new JButton("Konfigurieren");
    konfigurierenButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        try {
          KonfigurierbareAusgabe ausgabe = ((KonfigurierbareAusgabe) 
          ausgabeComboBox.getSelectedItem());
          
          ausgabe.konfiguriere(hauptFenster);
        } catch (Exception e) {
          ErrorHandler.getInstance().handleException(e, 
              "Fehler beim Konfigurieren der Medienausgabe!", false);
        }
      }});
    

    ausgabeComboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        MedienAusgabe ausgabe = ((MedienAusgabe) 
        ausgabeComboBox.getSelectedItem());
        konfigurierenButton.setEnabled(ausgabe instanceof KonfigurierbareAusgabe);
      }      
    });
    JComponentFormatierer.setDimension(ausgabeComboBox, new JTextField().getPreferredSize());
    JComponentFormatierer.setDimension(ausfuehrenButton, new Dimension(ausfuehrenButton.getPreferredSize().width, ausgabeComboBox.getPreferredSize().height));    
    JComponentFormatierer.setDimension(konfigurierenButton, new Dimension(konfigurierenButton.getPreferredSize().width, ausgabeComboBox.getPreferredSize().height));    
    
    //Formatierungen       
    this.setLayout(new GridBagLayout());    
    
    this.add(listenAuswahlPanel, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 20, 0), 0, 0));
    this.add(new JLabel("Ausgabe:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    this.add(ausgabeComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
    this.add(konfigurierenButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0, 0, 5), 0, 0));
    this.add(ausfuehrenButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }
  
  private void initAuswahlFeld() {
    String configFile = 
      Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
          this.getClass().getName(), "MedienlistenConfigfile").
          getWert("einstellungen/Ausgaben-Medienlisten.conf");      
    
    AuswahlKonfiguration konfiguration = null;
    try {
      konfiguration = new AuswahlKonfiguration(configFile);        
      listenAuswahlPanel.initAuswahlFeld(konfiguration);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Parsen der Datei '"+configFile+"'!", false);
    }    
  }

  public void aktualisiere() {    
    listenAuswahlPanel.setDaten(hauptFenster.getAlleMedien());
  }
    
  public void mediumEANGelesen(Medium medium) {
  }

  public void ISBNGelesen(ISBN isbn) {
  }

  public void refresh() {
    aktualisiere();
  }

  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }

}