package de.oberbrechen.koeb.gui.veranstaltungen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.*;

import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabe;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabeFactory;
import de.oberbrechen.koeb.dateien.veranstaltungsgruppeAusgabenKonfiguration.VeranstaltungsgruppeAusgabeFactoryBeschreibung;
import de.oberbrechen.koeb.dateien.veranstaltungsgruppeAusgabenKonfiguration.VeranstaltungsgruppeAusgabeKonfigurationXMLDatei;
import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.AbstractMain;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeGelesenEventHandler;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.veranstaltungen.bemerkungenReiter.BemerkungenReiter;
import de.oberbrechen.koeb.gui.veranstaltungen.benutzerReiter.BenutzerReiter;
import de.oberbrechen.koeb.gui.veranstaltungen.listenReiter.ListenReiter;
import de.oberbrechen.koeb.gui.veranstaltungen.teilnahmenReiter.TeilnahmenBenutzerReiter;
import de.oberbrechen.koeb.gui.veranstaltungen.teilnahmenReiter.TeilnahmenReiter;
import de.oberbrechen.koeb.gui.veranstaltungen.veranstaltungenReiter.VeranstaltungenReiter;
import de.oberbrechen.koeb.gui.veranstaltungen.veranstaltungsgruppenReiter.VeranstaltungsgruppenReiter;


/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die für
 * die das Eintragen der Veranstaltungsteilnahmen in die Datenbank gedacht ist.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Main extends AbstractMain implements BarcodeGelesenEventHandler {

  private SortiertComboBox<Benutzer> benutzerFeld;
  private SortiertComboBox<Veranstaltungsgruppe> veranstaltungsgruppeFeld;
  private EANZuordnung eanZuordnung;

  public Main(boolean isMain, Mitarbeiter mitarbeiter) {
    super(isMain, "Veranstaltungen", 
      Mitarbeiter.BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE,
      "de/oberbrechen/koeb/gui/icon-veranstaltungen.png", mitarbeiter);
    eanZuordnung = Datenbank.getInstance().getEANZuordnung();
    addBarcodeScanner(this);
  }

  /**
   * Entfernt einen Benutzer aus der Liste
   * @param benutzer der zu entfernende Benutzer
   */
  public void removeBenutzer(Benutzer benutzer) {
    benutzerFeld.removeItem(benutzer);
    benutzerFeld.setSelectedIndex(0);
  }

  public void erlaubeAenderungen(boolean erlaubt) {
    super.erlaubeAenderungen(erlaubt);
    benutzerFeld.setEnabled(erlaubt);
    veranstaltungsgruppeFeld.setEnabled(erlaubt);
  }

  /**
   * Aktiviert den übergebenen Benutzer, ist der Benutzer nicht in der
   * bisherigen Benutzerliste, so wird er ergänzt, falls er gespeichert ist.
   * @param benutzer der zu aktivierende Benutzer
   */
  public void setAktiverBenutzer(Benutzer benutzer) {
    benutzerFeld.setSelectedItem(benutzer);
    benutzerFeld.fireDelayItemListenerEvent();
  }

  public static void main(String[] args) {
    init();
    new Main(true, null);
  }

  protected JPanel getAllgemeinPanel() throws Exception {
    JPanel allgemeinPanel = new JPanel();
    allgemeinPanel.setLayout(new GridBagLayout());

    JLabel benutzerLabel = new JLabel("Benutzer:");
    JLabel veranstaltungsgruppeLabel = new JLabel("Veranstaltunsgruppe:");

    benutzerFeld = new SortiertComboBox<Benutzer>(new Format<Benutzer>() {
      public String format(Benutzer ben) {
        if (ben == null) return null;
        return ben.getNameFormal();
      }
    });
    benutzerFeld.setToolTipText("Der aktuelle Benutzer!");

    veranstaltungsgruppeFeld = new SortiertComboBox<Veranstaltungsgruppe>(
      new Format<Veranstaltungsgruppe>() {
      public String format (Veranstaltungsgruppe gruppe) {
        if (gruppe == null) return null;
        return gruppe.getName();
      }
    });
    veranstaltungsgruppeFeld.addDelayItemListener(
      new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        aktualisiereVeranstaltungsgruppe();
      }
    });
    veranstaltungsgruppeFeld.setToolTipText(
      "Die aktuelle Veranstaltungsgruppe!");

    int eingabeHoehe = new JTextField().getPreferredSize().height;
    Dimension eingabeDimension = new Dimension(100, eingabeHoehe);
    JComponentFormatierer.setDimension(benutzerFeld, eingabeDimension);
    JComponentFormatierer.setDimension(
      veranstaltungsgruppeFeld, eingabeDimension);
    benutzerFeld.addDelayItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        aktualisiereBenutzer();
      }
    });

    allgemeinPanel.add(benutzerLabel,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 10), 0, 0));
    allgemeinPanel.add(benutzerFeld,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(veranstaltungsgruppeLabel,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
    allgemeinPanel.add(veranstaltungsgruppeFeld,   new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

    return allgemeinPanel;
  }

  /**
   * Wird aufgerufen, wenn ein neuer Benutzer ausgewählt wurde
   */
  public void aktualisiereBenutzer() {
    ((VeranstaltungenMainReiter) reiter.getSelectedComponent()).
      setBenutzer(this.getAktuellerBenutzer());
  }

  /**
   * Wird aufgerufen, wenn eine neue Veranstaltungsgruppe ausgewählt wurde
   */
  public void aktualisiereVeranstaltungsgruppe() {
    ((VeranstaltungenMainReiter) reiter.getSelectedComponent()).
      setVeranstaltungsgruppe(this.getAktuelleVeranstaltungsgruppe());
  }

  //Doku siehe bitte Interface
  public void barcodeGelesen(final String barcode) {
    new Thread() {
      public void run() {
        EAN neuerBarcode = new EAN(barcode);
        Object referenz = eanZuordnung.getReferenz(neuerBarcode);
        
        if (referenz == null) return;

        if (referenz instanceof Benutzer) {
          if (!erlaubeAenderungen) return;
          setAktiverBenutzer((Benutzer) referenz);
          return;
        }
      }
    }.start();
  }

  //Doku siehe bitte Interface
  public void barcodeStartGelesen() {
    benutzerFeld.grabFocus();
  }

  /**
   * Liefert die aktuelle Veranstaltungsgruppe
   * @return die aktuelle Veranstaltungsgruppe
   */
  public Veranstaltungsgruppe getAktuelleVeranstaltungsgruppe() {
    return (Veranstaltungsgruppe) veranstaltungsgruppeFeld.getSelectedItem();
  }

  /**
   * Liefert den aktuellen Benutzer
   * @return den aktuellen Benutzer
   */
  public Benutzer getAktuellerBenutzer() {
    return (Benutzer) benutzerFeld.getSelectedItem();
  }


  protected void initDaten() {
    BenutzerFactory benutzerFactory = 
      Datenbank.getInstance().getBenutzerFactory();
    BenutzerListe alleBenutzerListe = benutzerFactory.getAlleAktivenBenutzer();
    alleBenutzerListe.setSortierung(BenutzerListe.NachnameVornameSortierung);    
    benutzerFeld.setDaten(alleBenutzerListe);

    VeranstaltungsgruppeFactory veranstaltungsgruppeFactory =
      Datenbank.getInstance().getVeranstaltungsgruppeFactory();
    VeranstaltungsgruppenListe liste =
      veranstaltungsgruppeFactory.getAlleVeranstaltungsgruppen();
    liste.setSortierung(
        VeranstaltungsgruppenListe.alphabetischeSortierung, true);
    veranstaltungsgruppeFeld.setDaten(liste);

    aktualisiereBenutzer();
    aktualisiereVeranstaltungsgruppe();
  }

  protected void reiterHinzufuegen() {
    Datenbank datenbank = Datenbank.getInstance();
    
    if (datenbank.getEinstellungFactory().getClientEinstellung(
      this.getClass().getName(), "zeigeBenutzerReiterEinzeln").getWertBoolean(
            true)) {
      reiter.add(new BenutzerReiter(this), "Benutzer");
      reiter.add(new TeilnahmenReiter(this), "Teilnahmen");
    } else {
      reiter.add(new TeilnahmenBenutzerReiter(this), "Teilnahmen/Benutzer");
    }
     
    reiter.add(new ListenReiter(this), "Listen");
    reiter.add(new BemerkungenReiter(this), "Bemerkungen");
    reiter.add(new VeranstaltungenReiter(this), "Veranstaltungen");
    reiter.add(new VeranstaltungsgruppenReiter(this), "Veranstaltungsgruppen");
  }

  /**
   * Entfernt die übergebene Veranstaltungsgruppe aus der Liste
   * @param veranstaltungsgruppe die zu entfernende Veranstaltungsgruppe
   */
  public void removeVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
    veranstaltungsgruppeFeld.removeItem(veranstaltungsgruppe);
    if (veranstaltungsgruppeFeld.getItemCount() > 0)
      veranstaltungsgruppeFeld.setSelectedIndex(0);
  }

  /**
   * Aktiviert die übergebene Veranstaltungsgruppe. Ist die Gruppe nicht in der
   * bisherigen Veranstaltungsgruppenliste, so wird sie ergänzt, falls sie 
   * gespeichert ist.
   * @param veranstaltungsgruppe die zu aktivierende Gruppe
   */
  public void setAktiveVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
    veranstaltungsgruppeFeld.setSelectedItem(veranstaltungsgruppe);
    veranstaltungsgruppeFeld.fireDelayItemListenerEvent();
  }
  
  /**
   * Fügt das Menü hinzu
   */
  protected void addMenue(JMenuBar menue) {
    menue.add(getDateiMenue(false));
    menue.add(getBenutzerMenue());
    menue.add(getAusgabenMenue());
    addReiterMenues(menue);
    menue.add(getInfoMenue());
  }
    
  protected JMenu getBenutzerMenue() {
    // Menü bauen
    JMenu benutzerMenue = new JMenu("Benutzer");
    
    JMenuItem benutzerAktivierenButton = new JMenuItem("Benutzer aktivieren");
    benutzerAktivierenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            BenutzerListe benutzerListe = benutzerAktivieren();
            if (benutzerListe != null && benutzerListe.size() > 0) {
              benutzerFeld.addAll(benutzerListe);
              benutzerFeld.setSelectedItem(benutzerListe.get(0));
            }
          }
        }.start();
      }
    });

    JMenuItem benutzerLadenButton = new JMenuItem("Benutzerliste neu einlesen");
    benutzerLadenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            setEnabled(false);

            BenutzerFactory benutzerFactory = 
              Datenbank.getInstance().getBenutzerFactory();
            final BenutzerListe alleBenutzerListe = benutzerFactory.getAlleAktivenBenutzer();
            alleBenutzerListe.setSortierung(BenutzerListe.NachnameVornameSortierung);    
            
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {                
                benutzerFeld.setDaten(alleBenutzerListe);
                aktualisiereBenutzer();
              }
            });  

            setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        }.start();
      }
    });
    
    benutzerMenue.add(benutzerAktivierenButton);
    benutzerMenue.add(benutzerLadenButton);
    
    return benutzerMenue;
  }
  
  protected JMenu getAusgabenMenue() {    
    // Menü bauen
    JMenu ausgabenMenue = new JMenu("Ausgaben");

    VeranstaltungsgruppeAusgabeKonfigurationXMLDatei konfigFile = 
      VeranstaltungsgruppeAusgabeKonfigurationXMLDatei.getInstance();
    
    final Main hauptFenster = this;
    VeranstaltungsgruppeAusgabeFactoryBeschreibung[] daten = konfigFile.getDaten();
    for (int i=0; i < daten.length; i++) {
      try {
        VeranstaltungsgruppeAusgabeFactory factory = 
          daten[i].createVeranstaltungsgruppeAusgabeFactory();
        final VeranstaltungsgruppeAusgabe ausgabe = factory.createVeranstaltungsgruppeAusgabe();
        JMenuItem ausgabeButton = new JMenuItem(daten[i].getName());
        
        ausgabeButton.addActionListener(new java.awt.event.ActionListener() {
          
          public void actionPerformed(ActionEvent e) {
            new Thread() {
              public void run() {
                try {
                  ausgabe.setVeranstaltungsgruppe(getAktuelleVeranstaltungsgruppe());
                  ausgabe.run(hauptFenster, true);
                } catch (Exception e) {
                  ErrorHandler.getInstance().handleException(e, "Fehler beim Ausführen der Ausgabe!", false);
                }
              }
            }.start();
          }
        });
        ausgabenMenue.add(ausgabeButton);
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Fehler beim Initialisieren der Ausgaben!", false);
      }
    }    
    
    return ausgabenMenue;
  }  
}