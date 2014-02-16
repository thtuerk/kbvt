package de.oberbrechen.koeb.gui.ausleihe.ausleiheReiter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.ausgaben.BenutzerAusgabe;
import de.oberbrechen.koeb.ausgaben.BenutzerAusgabeFactory;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.AusleiheFactory;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.einstellungen.WidersprichtAusleihordnungException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.Main;
import de.oberbrechen.koeb.gui.components.DelayListSelectionListener;
import de.oberbrechen.koeb.gui.components.benutzerAusgabeWrapper.BenutzerAusgabeWrapper;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.standarddialoge.DatumAuswahlDialog;
import de.oberbrechen.koeb.pdf.pdfAktuelleAusleihenListe.PdfAktuelleAusleihenListeAusgabenFactory;

/**
 * Diese Klasse repräsentiert die Ausleihenliste in dem Reiter, der die
 * Ausleihe und Rückgabe ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class AusleihenTabelle extends JPanel {

  static SimpleDateFormat dateFormat =
    new SimpleDateFormat("d. MMMM yyyy");

  Main hauptFenster;
  DatumAuswahlDialog datumAuswahlDialog;
  
  JLabel mahngebuehrenLabel;
  JTable ausleihTable;
  JButton neuButton;
  JButton allesVerlaengernButton;
  JButton ausgabeButton;
  AusleiheTableModel ausleihen;

  AusleiheReiter ausleiheReiter;
  DecimalFormat waehrungsFormat;
  Benutzer aktuellerBenutzer;

  boolean auswahlWirdGeradeGeaendert;
  DelayListSelectionListener delayListSelectionListener;
  BenutzerAusgabeFactory aktuelleAusleihenListeAusgabenFactory;

  public AusleihenTabelle(AusleiheReiter ausleiheReiter, Main hauptFenster) {
    this.hauptFenster = hauptFenster;
    auswahlWirdGeradeGeaendert = false;
    waehrungsFormat = new DecimalFormat("0.00 EUR");
    datumAuswahlDialog = new DatumAuswahlDialog(hauptFenster);

    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();
    Einstellung factoryEinstellung = einstellungFactory.getClientEinstellung(
      this.getClass().getName(), "AktuelleAusleihenListeAusgabe"); 
    try {
      aktuelleAusleihenListeAusgabenFactory = 
        (BenutzerAusgabeFactory) factoryEinstellung.getWertObject( 
        BenutzerAusgabeFactory.class, 
        PdfAktuelleAusleihenListeAusgabenFactory.class);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    
    this.ausleiheReiter = ausleiheReiter;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    ausleiheReiter.ausleihenAuswahlGewechselt(new Ausleihe[0]);
  }

  private void jbInit() throws Exception {
    ausleihTable = new JTable();
    ausleihen = new AusleiheTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(ausleihen, ausleihTable);
    ausleihTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    mahngebuehrenLabel = new JLabel("Mahngebühren:");

    JLabel beschriftung = new JLabel("Mahngebühren:");
    beschriftung.setBorder(BorderFactory.createEmptyBorder(15,0,0,2));
    mahngebuehrenLabel.setBorder(BorderFactory.createEmptyBorder(15,0,0,2));
    mahngebuehrenLabel.setHorizontalAlignment(SwingConstants.RIGHT);


    neuButton = new JButton("neue Ausleihe");
    JComponentFormatierer.setDimension(neuButton, new Dimension(50, 21));
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        auswahlWirdGeradeGeaendert = true;
        ausleihTable.clearSelection();
        delayListSelectionListener.fireDelayListSelectionEvent();
        auswahlWirdGeradeGeaendert = false;
        ausleiheReiter.neueAusleiheErstellen();
      }
    });
    allesVerlaengernButton = new JButton("alle Verlängern");
    JComponentFormatierer.setDimension(
      allesVerlaengernButton, new Dimension(50, 21));
    allesVerlaengernButton.addActionListener(
      new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        verlaengereAlleAusleihen();
      }
    });
    ausgabeButton = new JButton("Ausgabe");
    JComponentFormatierer.setDimension(
      ausgabeButton, new Dimension(50, 21));
    ausgabeButton.addActionListener(
      new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            aktuelleAusleihenAusgabe();
          }
        }.start();
      }
    });

    JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
    buttonPanel.add(neuButton);
    buttonPanel.add(allesVerlaengernButton);
    buttonPanel.add(ausgabeButton);
    
    JPanel mahngebuehrenPanel = new JPanel(new BorderLayout());
    mahngebuehrenPanel.add(buttonPanel, BorderLayout.NORTH);
    mahngebuehrenPanel.add(beschriftung, BorderLayout.WEST);
    mahngebuehrenPanel.add(mahngebuehrenLabel, BorderLayout.EAST);

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setAutoscrolls(true);
    jScrollPane1.getViewport().add(ausleihTable);
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(
      Color.white,new Color(148, 145, 140)));
    JComponentFormatierer.setDimension(jScrollPane1, new Dimension(100, 50));

    this.setLayout(new BorderLayout());
    this.add(jScrollPane1, BorderLayout.CENTER);
    this.add(mahngebuehrenPanel, BorderLayout.SOUTH);

    delayListSelectionListener = new DelayListSelectionListener(250);
    delayListSelectionListener.addListSelectionListener(
        new ListSelectionListener() {                    
          public void valueChanged(ListSelectionEvent e) {
            if (auswahlWirdGeradeGeaendert) return;
            if (e.getValueIsAdjusting()) return;
            
            int[] gewaehlteReihen = ausleihTable.getSelectedRows();
            Ausleihe[] gewaehlteAusleihen = new Ausleihe[gewaehlteReihen.length];
            for (int i=0; i < gewaehlteAusleihen.length; i++) {
              gewaehlteAusleihen[i] = (Ausleihe) ausleihen.get(gewaehlteReihen[i]);
            }
            ausleiheReiter.ausleihenAuswahlGewechselt(gewaehlteAusleihen);
          }
      });
    ausleihTable.getSelectionModel().addListSelectionListener(
      delayListSelectionListener);

  }

  protected void aktuelleAusleihenAusgabe() {
    BenutzerListe ausgewaehlteBenutzer = new BenutzerListe();
    ausgewaehlteBenutzer.add(hauptFenster.getAktivenBenutzer());
    
    BenutzerAusgabe ausgabe = aktuelleAusleihenListeAusgabenFactory.createBenutzerAusgabe();
    BenutzerAusgabeWrapper wrapper = new BenutzerAusgabeWrapper(ausgabe);
    wrapper.setZeigeAuswahl(false);
    wrapper.setAuswahl(ausgewaehlteBenutzer);
    try {
      wrapper.run(hauptFenster, true);         
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Erstellen der"+        " aktuellen Ausleihen-Liste!", false);
    }
  }

  /**
   * Läd die aktuellen Ausleihen des übergebenen Benutzers und zeigt sie an.
   * @param benutzer der Benutzer, dessen Ausleihen geladen werden sollen
   */
  public void ladeAusleihenVon(Benutzer benutzer) {
    aktuellerBenutzer = benutzer;
    ausleihTable.clearSelection();
    AusleiheFactory ausleiheFactory = 
      Datenbank.getInstance().getAusleiheFactory();
    AusleihenListe liste;
    try {
      liste = ausleiheFactory.getAlleAktuellenAusleihenVon(benutzer);
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, "Ausleihen von "+
          benutzer.getName()+
          " konnten nicht geladen werden!", false);
      liste = new AusleihenListe();
    }
    ausleihen.setDaten(liste);
    if (liste.size() > 0) {
      ausleihTable.setRowSelectionInterval(0, 0);
      ausleihTable.scrollRectToVisible(ausleihTable.getCellRect(0, 0, true));
    }
    berechneMahngebuehren();
  }

  /**
   * Zeigt die Tabelle neu an, ohne die Daten neu zu laden
   */
  public void refresh() {
    ausleihen.fireTableDataChanged();
    berechneMahngebuehren();
  }

  /**
   * Fügt eine Ausleihe zu der Tabelle hinzu.
   * @param ausleihe die hinzuzufügende Ausleihe
   */
  public void add(Ausleihe ausleihe) {
    ausleihen.add(ausleihe);
  }

  /**
   * Entfernt eine Ausleihe aus der Tabelle.
   * @param ausleihe die zu entfernende Ausleihe
   */
  public void remove(Ausleihe ausleihe) {
    ausleihen.remove(ausleihe);
  }

  /**
   * Berechnet die Mahngebühren der aktuell angezeigten Ausleihen neu und
   * zeigt sie an.
   */
  private void berechneMahngebuehren() {
    AusleihenListe alleAusleihen = (AusleihenListe) ausleihen.getDaten();
    AusleihenListe relevanteAusleihen = new AusleihenListe();

    for (Ausleihe aktuelleAusleihe : alleAusleihen) {
      if (aktuelleAusleihe.heuteZurueckgegeben()) {
        relevanteAusleihen.add(aktuelleAusleihe);
      }
    }

    double mahngebuehren = Buecherei.getInstance().getAusleihordnung()
      .berechneMahngebuehren(relevanteAusleihen);

    if (mahngebuehren == 0) {
      mahngebuehrenLabel.setForeground(Color.black);
      mahngebuehrenLabel.setText("-");
    } else {
      mahngebuehrenLabel.setForeground(Color.red);
      mahngebuehrenLabel.setText(
        waehrungsFormat.format(mahngebuehren));
    }
  }

  /**
   * Erlaubt / verbietet Änderungen, d.h. das Auswählen einer anderen
   * Ausleihe oder das Anlegen einer neuen Ausleihe.
   *
   * @param erlaubt bestimmt, ob Änderungen erlaubt sind
   */
  public void erlaubeAenderungen(boolean erlaubt) {
    neuButton.setEnabled(erlaubt);
    allesVerlaengernButton.setEnabled(erlaubt);
    ausleihTable.setEnabled(erlaubt);
  }

  /**
   * Aktualisiert die AusleiheTabelle, d.h. alle Daten werden erneut aus der
   * Datenbank geladen und die Anzeige aktualisiert.
   */
  public void aktualisiere() {
    int[] gewaehlteReihen = ausleihTable.getSelectedRows(); 
    ausleihen.fireTableDataChanged();
    berechneMahngebuehren();

    Ausleihe[] gewaehlteAusleihen = new Ausleihe[gewaehlteReihen.length];
    for (int i=0; i < gewaehlteAusleihen.length; i++) {
      gewaehlteAusleihen[i] = (Ausleihe) ausleihen.get(gewaehlteReihen[i]);
    }
    markiereAusleihen(gewaehlteAusleihen);
  }

  /**
   * Markiert die übergebene Ausleihe in der Tabelle. Ist die Ausleihe nicht in
   * der Tabelle enthalten, wird keine Ausleihe markiert.
   *
   * @param ausleihe die zu selektierende Ausleihe
   */
  public void markiereAusleihen(Ausleihe[] ausleihenAuswahl) {
    auswahlWirdGeradeGeaendert = true;
    ausleihTable.clearSelection();

    for (int pos=0; pos < ausleihen.getDaten().size(); pos++) {
      Ausleihe aktuelleAusleihe = (Ausleihe) ausleihen.getDaten().get(pos);
      for (int i=0; i < ausleihenAuswahl.length; i++) {
        if (aktuelleAusleihe.equals(ausleihenAuswahl[i])) {
          ausleihTable.addRowSelectionInterval(pos, pos);
        }
      }
    }
   
    ausleihTable.scrollRectToVisible(
        ausleihTable.getCellRect(ausleihTable.getSelectedRow(), 0, true));
    ausleiheReiter.ausleihenAuswahlGewechselt(ausleihenAuswahl);
    delayListSelectionListener.fireDelayListSelectionEvent();
     
    auswahlWirdGeradeGeaendert = false;
  }

  /**
   * Markiert die erste Ausleihe in der Tabelle. Ist keine Ausleihe in
   * der Tabelle enthalten, wird keine Ausleihe markiert.
   */
  public void markiereErsteAusleihe() {
    if (ausleihen.getRowCount() > 0) {
      ausleihTable.setRowSelectionInterval(0, 0);
      ausleihTable.scrollRectToVisible(ausleihTable.getCellRect(0, 0, true));
      
      ausleiheReiter.ausleihenAuswahlGewechselt((Ausleihe) ausleihen.get(0));
    } else {
      ausleiheReiter.ausleihenAuswahlGewechselt(new Ausleihe[0]);
    }
  }

  // Doku siehe Component
  public void grabFocus() {
    ausleihTable.grabFocus();
  }
  
  /**
   * Verlängert alle Ausleihen bis zu einem festen Datum
   * @author thtuerk
   */
  private void verlaengereAlleAusleihen() {    
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, 1);
    
    Date aktuellesDatum = Buecherei.getInstance().getNaechstesOeffnungsdatum(
      calendar.getTime());

    Date gewaehltesDatum = datumAuswahlDialog.waehleDatum(
      "Bis wann sollen die Medien verlängert werden?", aktuellesDatum);
    if (gewaehltesDatum == null) return;
    
    int erg = JOptionPane.showConfirmDialog(hauptFenster, 
      "Sollen alle von " + 
      hauptFenster.getAktivenBenutzer().getName() + 
      "\nausgeliehenen Medien wirklich " +
      "\nbis mindestens zum " +      dateFormat.format(gewaehltesDatum)+      
      "\nverlängert werden?", "Wirklich verlängern?",JOptionPane.YES_NO_OPTION);
    if (erg != JOptionPane.YES_OPTION) return;

    auswahlWirdGeradeGeaendert = true;

    ausleihTable.clearSelection();
    delayListSelectionListener.fireDelayListSelectionEvent();

    hauptFenster.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Thread.yield();

    //alle Verlängern
    int ausleihenAnzahl = ausleihen.getRowCount(); 
    for (int i=0; i < ausleihenAnzahl; i++) {
      Ausleihe aktuelleAusleihe = (Ausleihe) ausleihen.get(i);
      try {
        if (!aktuelleAusleihe.istZurueckgegeben()) {
          aktuelleAusleihe.verlaengereBisMindestens(gewaehltesDatum, 
              hauptFenster.getAktuellenMitarbeiter());
          aktuelleAusleihe.save();
        }
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
      } catch (WidersprichtAusleihordnungException e) {
        JOptionPane.showMessageDialog(hauptFenster,
            "Die Ausleihe des Mediums "+aktuelleAusleihe.getMedium().getTitel()+
            "konnte nicht verlängert werden,\nda die Ausleihordnung dies nicht " +
            "erlaubt!", "Verlängern nicht möglich!",
            JOptionPane.ERROR_MESSAGE);        
      }
    }
    ausleihen.fireTableDataChanged();
    
    berechneMahngebuehren();      
    hauptFenster.setCursor(Cursor.getDefaultCursor());
    auswahlWirdGeradeGeaendert = false;

    if (ausleihen.getRowCount() > 0)
      ausleihTable.getSelectionModel().setSelectionInterval(0,0);
  }
}