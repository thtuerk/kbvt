package de.oberbrechen.koeb.gui.components.benutzerAusgabeWrapper;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oberbrechen.koeb.ausgaben.BenutzerAusgabe;
import de.oberbrechen.koeb.ausgaben.KonfigurierbareAusgabe;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.benutzerListenAuswahlPanel.BenutzerListenAuswahlPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

class AuswahlDialog extends JDialog {
    
  BenutzerListenAuswahlPanel auswahlPanel;
  BenutzerAusgabe benutzerAusgabe;
  JFrame hauptFenster;
  File file;
  
  public AuswahlDialog(JFrame hauptFenster, BenutzerAusgabe benutzerAusgabe) {
    this(hauptFenster, benutzerAusgabe, null, true);
  }
  
  public AuswahlDialog(JFrame hauptFenster, BenutzerAusgabe benutzerAusgabe,
    AuswahlKonfiguration konfiguration, boolean zeigeAuswahl) {
    super(hauptFenster, "Benutzerauswahl", false);      
    this.hauptFenster = hauptFenster;
    this.benutzerAusgabe = benutzerAusgabe;
    try {
      jbInit(zeigeAuswahl);
      if (zeigeAuswahl) initAuswahlFeld(konfiguration);
      
      BenutzerFactory benutzerFactory = 
        Datenbank.getInstance().getBenutzerFactory();
      auswahlPanel.setDaten(benutzerFactory.getAlleAktivenBenutzer());         

      Point hauptFensterLocation = hauptFenster.getLocation();
      setLocation(hauptFensterLocation.x + ((hauptFenster.getWidth() - this.getWidth())/2), 
                  hauptFensterLocation.y + ((hauptFenster.getHeight() - this.getHeight())/2));
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Sorgt dafür, dass beim Beenden des Dialogs die Ausgabe in die übergebene Datei geschrieben wird
   * @param datei
   */
  public void setSchreibeInDatei(File datei) {
    this.file = datei;
  }
  
  /**
   * Sorgt dafür, dass beim Beenden des Dialogs die Ausgabe ausgeführt wird
   */
  public void setAusgabe() {
    this.file = null;
  }

  public void setAuswahl(Collection<? extends Benutzer> auswahl) {
    auswahlPanel.setAuswahl(auswahl);
  }

  void jbInit(boolean zeigeAuswahl) throws Exception {
    auswahlPanel =  new BenutzerListenAuswahlPanel(false, zeigeAuswahl, 
        BenutzerListenAuswahlPanel.AUSWAHLTYP_AKTIVE_BENUTZER);
    
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Abbrechen");
    JButton konfigButton = new JButton("Konfigurieren");
    JComponentFormatierer.setDimension(okButton, cancelButton.getPreferredSize());
    
    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent arg0) {
        new Thread() {
          public void run() {
            dispose();
            try { 
              benutzerAusgabe.setDaten(auswahlPanel.getAuswahl());          
              if (file == null || !benutzerAusgabe.istSpeicherbar()) {
                benutzerAusgabe.run(hauptFenster, true);
              } else {
                benutzerAusgabe.schreibeInDatei(hauptFenster, true, file);                
              }
            } catch (Exception e) {
              ErrorHandler.getInstance().handleException(e, "Fehler beim Ausführen der Benutzerausgabe!", false);
            }
          }
        }.start();
      }        
    });
    
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent arg0) {
        dispose();
      }        
    });

    if (benutzerAusgabe instanceof KonfigurierbareAusgabe) {      
      konfigButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
          new Thread() {
            public void run() {
              ((KonfigurierbareAusgabe) benutzerAusgabe).konfiguriere(hauptFenster);
            }
          }.start();
        }        
      });
    } else {
      konfigButton.setVisible(false);
    }
    
    JPanel pane = new JPanel();
    pane.setLayout(new GridBagLayout());
    pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    
    pane.add(new JLabel("Bitte wählen Sie die auszugebenden Benutzer aus:"), new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));      
    pane.add(auswahlPanel, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 20, 0), 0, 0));      
    
    pane.add(okButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));      
    pane.add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));      
    pane.add(konfigButton, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHEAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
    
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(pane, BorderLayout.CENTER);
    this.pack();
  }
  
  private void initAuswahlFeld(AuswahlKonfiguration konfiguration) {
    AuswahlKonfiguration konfig = konfiguration;
    if (konfig == null) {
      String configFile = 
        Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
            this.getClass().getName(), "BenutzerlistenConfigfile").
            getWert("einstellungen/Ausgaben-Benutzerlisten.conf");              
      try {      
        konfig = new AuswahlKonfiguration(configFile);              
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, 
            "Fehler beim Parsen der Datei '"+configFile+"'!", false);
      }    
    }    
    auswahlPanel.initAuswahlFeld(konfig);    
  }
}

  