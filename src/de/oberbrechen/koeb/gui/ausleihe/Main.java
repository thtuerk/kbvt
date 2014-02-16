package de.oberbrechen.koeb.gui.ausleihe;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.gui.AbstractMain;
import de.oberbrechen.koeb.gui.ausleihe.ausleiheReiter.AusleiheReiter;
import de.oberbrechen.koeb.gui.ausleihe.ausleihenInfoReiter.AusleihenInfoReiter;
import de.oberbrechen.koeb.gui.ausleihe.benutzerReiter.BenutzerReiter;
import de.oberbrechen.koeb.gui.ausleihe.internetFreigabeReiter.InternetFreigabeReiter;
import de.oberbrechen.koeb.gui.ausleihe.mahnungenReiter.MahnungenReiter;
import de.oberbrechen.koeb.gui.ausleihe.medienInfoReiter.MedienInfoReiter;
import de.oberbrechen.koeb.gui.ausleihe.medienSucheReiter.MedienSucheReiter;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeGelesenEventHandler;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die für
 * die täglich auftredenden Aufgaben, also vor allem für die Ausleihe gedacht
 * ist.
 *
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Main extends AbstractMain implements BarcodeGelesenEventHandler {
  private JTextField mitarbeiterFeld;
  private SortiertComboBox<Benutzer> benutzerFeld;

  private AusleiheReiter ausleiheReiter;
  private MedienInfoReiter medienInfoReiter;
  private EANZuordnung eanZuordnung;

  public Main(boolean isMain, Mitarbeiter currentMitarbeiter) {
    super(isMain,
      "Ausleihe",
      Mitarbeiter.BERECHTIGUNG_STANDARD,
      "de/oberbrechen/koeb/gui/icon-ausleihe.png", currentMitarbeiter);
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
  }

  /**
   * Aktiviert den übergebenen Benutzer, ist der Benutzer nicht in der
   * bisherigen Benutzerliste, so wird er ergänzt, falls er gespeichert ist.
   * @param benutzer der zu aktivierende Benutzer
   */
  public void setAktivenBenutzer(Benutzer benutzer) {
    benutzerFeld.setSelectedItem(benutzer);
    benutzerFeld.fireDelayItemListenerEvent();
  }

  public static void main(String[] args) {
    init();
    new Main(true, null);
  }

  protected JPanel getAllgemeinPanel() throws Exception {
    //allgemeinPanel bauen
    JPanel allgemeinPanel = new JPanel();
    allgemeinPanel.setLayout(new GridBagLayout());

    JLabel mitarbeiterLabel = new JLabel("Mitarbeiter:");
    JLabel benutzerLabel = new JLabel("Benutzer:");
    int labelBreite =
      Math.max(
        benutzerLabel.getPreferredSize().width,
        mitarbeiterLabel.getPreferredSize().width);
    int labelHoehe =
      Math.max(
        benutzerLabel.getPreferredSize().height,
        mitarbeiterLabel.getPreferredSize().height);
    Dimension labelDimension = new Dimension(labelBreite, labelHoehe);

    JComponentFormatierer.setDimension(benutzerLabel, labelDimension);
    JComponentFormatierer.setDimension(mitarbeiterLabel, labelDimension);

    mitarbeiterFeld = new JTextField();
    mitarbeiterFeld.setToolTipText("Der aktuell angemeldete Mitarbeiter!");
    benutzerFeld = new SortiertComboBox<Benutzer>(new Format<Benutzer>() {
      public String format(Benutzer ben) {
        if (ben == null) return null;
        return ben.getNameFormal();
      }
    });
    benutzerFeld.setToolTipText("Der aktuelle Benutzer!");
    int eingabeHoehe = mitarbeiterFeld.getPreferredSize().height;
    Dimension eingabeDimension = new Dimension(100, eingabeHoehe);
    JComponentFormatierer.setDimension(mitarbeiterFeld, eingabeDimension);
    JComponentFormatierer.setDimension(benutzerFeld, eingabeDimension);
    mitarbeiterFeld.setEditable(false);
    benutzerFeld.addDelayItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        aktualisiereBenutzer();
      }
    });

    allgemeinPanel.add(
      benutzerLabel,
      new GridBagConstraints(
        0,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(0, 2, 0, 10),
        0,
        0));
    allgemeinPanel.add(
      benutzerFeld,
      new GridBagConstraints(
        1,
        0,
        1,
        1,
        1.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0),
        0,
        0));
    allgemeinPanel.add(
      mitarbeiterLabel,
      new GridBagConstraints(
        2,
        0,
        1,
        1,
        0.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.NONE,
        new Insets(0, 20, 0, 10),
        0,
        0));
    allgemeinPanel.add(
      mitarbeiterFeld,
      new GridBagConstraints(
        3,
        0,
        1,
        1,
        1.0,
        0.0,
        GridBagConstraints.WEST,
        GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0),
        0,
        0));

    return allgemeinPanel;
  }

  /**
   * Wird aufgerufen, wenn ein neuer Benutzer ausgewählt wurde
   */
  public void aktualisiereBenutzer() {
    ((AusleiheMainReiter) reiter.getSelectedComponent()).setBenutzer(
      this.getAktivenBenutzer());
  }

  //Doku siehe bitte Interface
  public void barcodeGelesen(final String barcode) {
    new Thread() {
      public void run() {
        EAN neuerBarcode = new EAN(barcode);

        final Object referenz = eanZuordnung.getReferenz(neuerBarcode); 
        if (referenz == null) return;

        SwingUtilities.invokeLater(
          new Thread() {
            public void run() {
              
              if (referenz instanceof Benutzer) {
                if (!erlaubeAenderungen)
                  return;
                setAktivenBenutzer((Benutzer) referenz);
                return;
              }
      
              if (referenz instanceof Medium) {
                ((AusleiheMainReiter) reiter.getSelectedComponent()).mediumEANGelesen(
                  (Medium) referenz);
                return;
              }
            }
        });
      }
    }.start();
  }

  //Doku siehe bitte Interface
  public void barcodeStartGelesen() {
    mitarbeiterFeld.grabFocus();
  }

  /**
   * Liefert den aktuellen Benutzer
   * @return den aktuellen Benutzer
   */
  public Benutzer getAktivenBenutzer() {
    return (Benutzer) benutzerFeld.getSelectedItem();
  }

  /**
   * Zeigt die übergebene Ausleihe an. D.h. es wird der zur Ausleihe
   * gehörende Benutzer gesetzt und der Ausleihe-Reiter aktiviert. Dort wird,
   * falls möglich, die übergebene Ausleihe ausgewählt.
   *
   * @param ausleihe die anzuzeigende Ausleihe
   */
  public void zeigeAusleihe(Ausleihe ausleihe) {
    this.setAktivenBenutzer(ausleihe.getBenutzer());
    zeigeAusleiheReiter();
    ausleiheReiter.zeigeAusleihe(ausleihe);
  }
  
  /**
   * Zeigt das übergebene Medium im Detailsreiter an.
   */
  public void zeigeMedium(Medium medium) {
    medienInfoReiter.setMedium(medium);
    reiter.setSelectedComponent(medienInfoReiter);
  }
  

  /**
   * Zeigt den AusleiheReiter an
   */
  public void zeigeAusleiheReiter() {
    reiter.setSelectedComponent(ausleiheReiter);
  }

  protected void initDaten() {
    BenutzerListe alleBenutzerListe = Datenbank.getInstance().
      getBenutzerFactory().getAlleAktivenBenutzer();
    alleBenutzerListe.setSortierung(
      BenutzerListe.NachnameVornameSortierung);
    benutzerFeld.setDaten(alleBenutzerListe);
  }

  protected void reiterHinzufuegen() {
    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();

    ausleiheReiter = new AusleiheReiter(this);
    reiter.add(ausleiheReiter, "Ausleihe / Rückgabe");
    reiter.add(new BenutzerReiter(this), "Benutzerdaten");

    if (einstellungFactory.getClientEinstellung(
        this.getClass().getName(),
        "ZeigeReiter.Medien-Informationen").getWertBoolean(true)) {
      medienInfoReiter = new MedienInfoReiter(this);
      reiter.add(medienInfoReiter, "Medien-Informationen");
      reiter.add(new MedienSucheReiter(this), "Medien-Suche");
    }
    if (einstellungFactory.getClientEinstellung(
        this.getClass().getName(),
        "ZeigeReiter.bisherige_Ausleihen").getWertBoolean(true))
      reiter.add(new AusleihenInfoReiter(this), "bisherige Ausleihen");
    if (einstellungFactory.getClientEinstellung(
        this.getClass().getName(),
        "ZeigeReiter.Internetzugang").getWertBoolean(true))
      reiter.add(new InternetFreigabeReiter(this), "Internetzugang");
    if (einstellungFactory.getClientEinstellung(
        this.getClass().getName(),
        "ZeigeReiter.Mahnungen").getWertBoolean(true))
      reiter.add(new MahnungenReiter(this), "Mahnungen");
  }

  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    super.setMitarbeiter(mitarbeiter);
    if (mitarbeiterFeld != null)
      mitarbeiterFeld.setText(mitarbeiter.getBenutzer().getName());
  }

  protected void addMenue(JMenuBar menue) {
    menue.add(getDateiMenue(false));
    menue.add(getBenutzerMenue());
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
}