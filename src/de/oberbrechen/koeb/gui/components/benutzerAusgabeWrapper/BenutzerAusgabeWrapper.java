package de.oberbrechen.koeb.gui.components.benutzerAusgabeWrapper;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.BenutzerAusgabe;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse bildet einen Wrapper um eine BenutzerAusgabe. Bei
 * Aufruf dieser Ausgabe wird ein Dialogfeld angezeigt, über das
 * die auszugebenden Benutzer ausgewählt und die Ausgabe konfiguriert
 * werden kann.
 */
public class BenutzerAusgabeWrapper implements Ausgabe {

  AuswahlDialog dialog;
  BenutzerAusgabe benutzerAusgabe;
  AuswahlKonfiguration konfiguration;
  boolean zeigeAuswahl = true;
  Vector<Benutzer> auswahl = null;
  
  /**
   * Erstellt einen neuen Wrapper für die übergebene BenutzerAusgabe
   * @param benutzerAusgabe
   */
  public BenutzerAusgabeWrapper(BenutzerAusgabe benutzerAusgabe) {
    this(benutzerAusgabe, null);
  }

  /**
   * Bestimmt, ob mehrere Listen von Benutzern zur Auswahl angeboten werden.
   * @param zeigeAuswahl der neue Wert
   */
  public void setZeigeAuswahl(boolean zeigeAuswahl) {
    this.zeigeAuswahl= zeigeAuswahl;
  }
  
  /**
   * Setzt die Benutzer, die zu Beginn ausgewählt sind.
   */
  public void setAuswahl(Collection<? extends Benutzer> auswahl) {
    this.auswahl = new Vector<Benutzer>(auswahl);
  }
  
  /**
   * Erstellt einen neuen Wrapper für die übergebene BenutzerAusgabe
   * und benutzt als Auswahlmöglichkeiten die übergebene AuswahlKonfiguration.
   * @param medienAusgabe
   * @param konfiguration
   */
  public BenutzerAusgabeWrapper(BenutzerAusgabe benutzerAusgabe,
    AuswahlKonfiguration konfiguration) {
    this.benutzerAusgabe = benutzerAusgabe;
  }
  
  protected void initDialog(JFrame hauptFenster) {
    if (hauptFenster == null) { 
      ErrorHandler.getInstance().handleError(
          "Diese Ausgabe benötigt eine graphische Schnittstelle!", false);
      return;
    }
    
    if (dialog == null || dialog.hauptFenster != hauptFenster)
      dialog = new AuswahlDialog(hauptFenster, benutzerAusgabe, konfiguration, zeigeAuswahl);

    if (auswahl != null) dialog.setAuswahl(auswahl);    
  }
  
  public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception {   
    initDialog(hauptFenster);
    dialog.setAusgabe();
    dialog.setVisible(true);
  }

  public String getName() {
    return benutzerAusgabe.getName();
  }

  public String getBeschreibung() {
    return benutzerAusgabe.getBeschreibung();
  }

  public boolean istSpeicherbar() {
    return benutzerAusgabe.istSpeicherbar();
  }

  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
    initDialog(hauptFenster);
    dialog.setSchreibeInDatei(datei);
    dialog.setVisible(true);
  }
  
  public String getStandardErweiterung() {
    return "pdf";
  }

  public boolean benoetigtGUI() {
    return true;
  }  
}

