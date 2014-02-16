package de.oberbrechen.koeb.gui.bestand.standardwertReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.bestand.BestandMainReiter;
import de.oberbrechen.koeb.gui.bestand.Main;
import de.oberbrechen.koeb.gui.bestand.Standardwerte;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.einstellungBindung.JCheckBoxEinstellungBindung;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.systematikListenAuswahlPanel.SystematikListenAuswahlPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Medien-Daten 
 * angezeigt und bearbeitet werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class StandardwertReiter extends JPanel implements BestandMainReiter {

  private Main hauptFenster;
  
  //Datumsformatierung
  private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  // die Datenfelder
  private JCheckBox mediennrFeld;
  private JTextField nrFeld;
  private JTextField titelFeld;
  private JTextField autorFeld;
  private SortiertComboBox<Medientyp> medientypFeld;
  private JTextField eingestelltSeitFeld;
  private JTextField medienAnzahlFeld;
  private JTextArea beschreibungFeld;
  private SystematikListenAuswahlPanel listenAuswahlPanel;

  private JCheckBoxEinstellungBindung mediennrFeldEinstellungBindung;
  
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

  public StandardwertReiter(Main hauptFenster) {
    this.hauptFenster=hauptFenster;
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
    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    listenAuswahlPanel = new SystematikListenAuswahlPanel();
    listenAuswahlPanel.setMinimumSize(new Dimension(200, 50));
    
    //Alle wichtigen Objeke initialisieren
    mediennrFeld = new JCheckBox();
    nrFeld = new JTextField();
    titelFeld = new JTextField();
    autorFeld = new JTextField();
    medientypFeld = new SortiertComboBox<Medientyp>();
    eingestelltSeitFeld = new JTextField();
    medienAnzahlFeld = new JTextField();
    beschreibungFeld = new JTextArea();
    
    Einstellung mediennrEinstellung = Datenbank.getInstance().getEinstellungFactory().getMitarbeiterEinstellung(
        this.getClass().getName(), "medienNrVorschlagen");
    mediennrFeldEinstellungBindung =
      new JCheckBoxEinstellungBindung(mediennrFeld, mediennrEinstellung, false);
    
    //Buttons
    JButton zurueckNrButton = new JButton("Zurücksetzen");
    zurueckNrButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nrFeld.setText("1");
      }
    });
        
    JButton zurueckMedienanzahlButton = new JButton("Zurücksetzen");
    zurueckMedienanzahlButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        medienAnzahlFeld.setText("1");
      }
    });
    
    JButton zurueckMedientypButton = new JButton("Zurücksetzen");
    zurueckMedientypButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          medientypFeld.setSelectedItem(
            Datenbank.getInstance().getMedientypFactory().getMeistBenutztenMedientyp());
        } catch (Exception e2) {
          ErrorHandler.getInstance().handleException(e2, true);          
        }
      }
    });
    
    JButton zurueckEinstellungsdatumButton = new JButton("Zurücksetzen");
    zurueckEinstellungsdatumButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        eingestelltSeitFeld.setText(dateFormat.format(new Date()));
      }
    });
    
    JButton zurueckAllesButton = new JButton("Alles zurücksetzen");
    zurueckAllesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setStandardwerte(new Standardwerte());
      }
    });
    
    Dimension buttonDimension = 
      new Dimension(zurueckNrButton.getPreferredSize().width, 
                    nrFeld.getPreferredSize().height);
    JComponentFormatierer.setDimension(zurueckNrButton, buttonDimension); 
    JComponentFormatierer.setDimension(zurueckMedientypButton, buttonDimension); 
    JComponentFormatierer.setDimension(zurueckMedienanzahlButton, buttonDimension); 
    JComponentFormatierer.setDimension(zurueckEinstellungsdatumButton, buttonDimension); 
    JComponentFormatierer.setDimension(medientypFeld, 
        new Dimension(medientypFeld.getPreferredSize().width, 
            nrFeld.getPreferredSize().height));
    
    
    //Labels
    JLabel jLabel1 = new JLabel("Medien-Nr. vorschlagen:");
    JLabel jLabel2 = new JLabel("Titel:");
    JLabel jLabel3 = new JLabel("Nr:");
    JLabel jLabel4 = new JLabel("Autor:");
    JLabel jLabel6 = new JLabel("Medientyp:");
    JLabel jLabel7 = new JLabel("eingestellt seit:");
    JLabel jLabel8 = new JLabel("Medienanzahl:");
    JLabel jLabel15 = new JLabel("Beschreibung:");

    //Format-Checks
    eingestelltSeitFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkDatum(eingestelltSeitFeld);
      }
    });
    medienAnzahlFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkMedienAnzahl();
      }
    });
    nrFeld.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        checkNummer();
      }
    });
    
    //Formatierungen       
    this.setLayout(new GridBagLayout());
    mediennrFeld.setOpaque(true);
    MedientypFactory medientypFactory = 
      Datenbank.getInstance().getMedientypFactory();
    medientypFeld.setDaten(medientypFactory.getAlleMedientypen());

    beschreibungFeld.setLineWrap(true);
    beschreibungFeld.setWrapStyleWord(true);
    JScrollPane beschreibungScrollPane = new JScrollPane();
    beschreibungScrollPane.setMinimumSize(new Dimension(0, 40));
    beschreibungScrollPane.setPreferredSize(new Dimension(0, 40));
    beschreibungScrollPane.getViewport().add(beschreibungFeld, null);
    
    
    this.add(jLabel6,              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(medientypFeld,              new GridBagConstraints(1, 0, 1, 1, 2.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(zurueckMedientypButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    this.add(jLabel3,              new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(nrFeld,              new GridBagConstraints(1, 1, 1, 1, 2.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(zurueckNrButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    
    this.add(jLabel1,           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
    this.add(mediennrFeld,                   new GridBagConstraints(1, 2, 1, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel2,           new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(titelFeld,              new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel4,           new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(autorFeld,              new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel8,              new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel7,            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(eingestelltSeitFeld,                new GridBagConstraints(1, 5, 1, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(zurueckEinstellungsdatumButton, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    this.add(medienAnzahlFeld,                new GridBagConstraints(1, 6, 1, 1, 2.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(zurueckMedienanzahlButton, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
    this.add(jLabel15,               new GridBagConstraints(0, 7, 1, 1, 0.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(beschreibungScrollPane,                new GridBagConstraints(1, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    this.add(listenAuswahlPanel,           new GridBagConstraints(0, 8, 3, 1, 0.0, 15.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(15, 0, 0, 0), 0, 0));

    this.add(zurueckAllesButton, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 0, 0, 0), 0, 0));    
  }

  public void aktualisiere() {
    setStandardwerte(hauptFenster.getStandardwerte());    
  }
  
  private void setStandardwerte(Standardwerte standardwerte) {
    medientypFeld.setSelectedItem(standardwerte.medientyp);
    medienAnzahlFeld.setText(Integer.toString(standardwerte.medienanzahl));
    nrFeld.setText(Integer.toString(standardwerte.nr));
    mediennrFeldEinstellungBindung.refresh();
    autorFeld.setText(standardwerte.autor);
    titelFeld.setText(standardwerte.titel);
    eingestelltSeitFeld.setText(dateFormat.format(standardwerte.einstellungsDatum));
    beschreibungFeld.setText(standardwerte.beschreibung);
    listenAuswahlPanel.setAuswahl(standardwerte.systematiken);        
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
      neuDatum = new Date();
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

  void checkNummer() {
    String anzahlString = nrFeld.getText();
    
    boolean anzahlOK = true;
    if (anzahlString == null || anzahlString.equals("")) anzahlOK = false;
    
    if (anzahlOK) {
      int anzahl = 1;
      try {
        anzahl = Integer.parseInt(anzahlString);
        if (anzahl < 0) anzahlOK = false;
      } catch (NumberFormatException e) {
        anzahlOK = false;
      }
    }

    if (!anzahlOK) {
      nrFeld.setText("1");
      JOptionPane.showMessageDialog(hauptFenster, "Die Eingabe '"+
          anzahlString + "' kann\nnicht als positive Zahl interpretiert\n"+
          "werden!",
          "Ungültige Nummer!",
          JOptionPane.ERROR_MESSAGE);
    }
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
    Standardwerte standardwerte = hauptFenster.getStandardwerte();
    
    standardwerte.medientyp = (Medientyp) medientypFeld.getSelectedItem();
    standardwerte.medienanzahl = Integer.parseInt(medienAnzahlFeld.getText());
    standardwerte.mediennrVorschlagen = mediennrFeld.isSelected();
    standardwerte.nr = Integer.parseInt(nrFeld.getText());
    standardwerte.autor = autorFeld.getText();
    standardwerte.titel = titelFeld.getText();
    try {
      standardwerte.einstellungsDatum = dateFormat.parse(eingestelltSeitFeld.getText());
    } catch (Exception e) {
      //Sollte nicht auftreten, da vorher gecheckt
      standardwerte.einstellungsDatum = new Date();
    }
    standardwerte.beschreibung = beschreibungFeld.getText();
    standardwerte.systematiken.clear();
    standardwerte.systematiken.addAllNoDuplicate(listenAuswahlPanel.getAuswahl());
  }
}