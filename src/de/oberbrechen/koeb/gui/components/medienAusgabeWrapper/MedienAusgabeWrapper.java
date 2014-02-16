package de.oberbrechen.koeb.gui.components.medienAusgabeWrapper;

import java.io.File;
import java.util.Collection;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.MedienAusgabe;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse bildet einen Wrapper um eine MedienAusgabe. Bei
 * Aufruf dieser Ausgabe wird ein Dialogfeld angezeigt, über das
 * die auszugebenden Medien ausgewählt und die Ausgabe konfiguriert
 * werden kann.
 */
public class MedienAusgabeWrapper implements Ausgabe {

  AuswahlDialog dialog;
  MedienAusgabe medienAusgabe;
  AuswahlKonfiguration konfiguration;
  boolean zeigeAuswahl = true;
  Collection<? extends Medium> auswahl = null;

  /**
   * Erstellt einen neuen Wrapper für die übergebene MedienAusgabe
   * @param medienAusgabe
   */
  public MedienAusgabeWrapper(MedienAusgabe medienAusgabe) {
    this(medienAusgabe, null);
  }

  /**
   * Bestimmt, ob mehrere Listen von Medien zur Auswahl angeboten werden.
   * @param zeigeAuswahl der neue Wert
   */
  public void setZeigeAuswahl(boolean zeigeAuswahl) {
    this.zeigeAuswahl= zeigeAuswahl;
  }
  
  /**
   * Setzt die Medien, die zu Beginn ausgewählt sind.
   */
  public void setAuswahl(Collection<? extends Medium> auswahl) {
    this.auswahl = auswahl;
  }

  /**
   * Erstellt einen neuen Wrapper für die übergebene Medienausgabe
   * und benutzt als Auswahlmöglichkeiten die übergebene AuswahlKonfiguration.
   * @param medienAusgabe
   * @param konfiguration
   */
  public MedienAusgabeWrapper(MedienAusgabe medienAusgabe,
    AuswahlKonfiguration konfiguration) {
    this.medienAusgabe = medienAusgabe;
  }

  protected void initDialog(JFrame hauptFenster) throws Exception {
    if (hauptFenster == null) { 
      ErrorHandler.getInstance().handleError(
          "Diese Ausgabe benötigt eine graphische Schnittstelle!", false);
      return;
    }
    
    if (dialog == null || dialog.hauptFenster != hauptFenster)
      dialog = new AuswahlDialog(hauptFenster, medienAusgabe, konfiguration, zeigeAuswahl);
    if (auswahl != null) dialog.setAuswahl(auswahl);    
  }
  
  public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception {   
    initDialog(hauptFenster);
    dialog.setAusgabe();
    dialog.setVisible(true);
  }

  public String getName() {
    return medienAusgabe.getName();
  }

  public String getBeschreibung() {
    return medienAusgabe.getBeschreibung();
  }

  public boolean istSpeicherbar() {
    return medienAusgabe.istSpeicherbar();
  }
  
  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
    initDialog(hauptFenster);
    dialog.setSchreibeInDatei(datei);
    dialog.setVisible(true);
  }
  
  public String getStandardErweiterung() {
    return medienAusgabe.getStandardErweiterung();
  }    
  
  public boolean benoetigtGUI() {
    return true;
  }  
}

