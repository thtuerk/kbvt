package de.oberbrechen.koeb.gui.bestand;

import javax.swing.SwingUtilities;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.AbstractMain;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeGelesenEventHandler;
import de.oberbrechen.koeb.gui.bestand.ausgabenReiter.AusgabenReiter;
import de.oberbrechen.koeb.gui.bestand.medienReiter.MedienReiter;
import de.oberbrechen.koeb.gui.bestand.standardwertReiter.StandardwertReiter;

/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die zur
 * Verwaltung und Eingabe von Medien dient.
 *
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Main extends AbstractMain implements BarcodeGelesenEventHandler {
  private static final long serialVersionUID = 1L;

  protected Standardwerte standardwerte;
  protected MedienListe alleMedien;
  
  private EANZuordnung eanZuordnung = 
    Datenbank.getInstance().getEANZuordnung();
    
  public Main(boolean isMain, Mitarbeiter mitarbeiter) {
    super(isMain, "Bestandsverwaltung", Mitarbeiter.BERECHTIGUNG_BESTAND_EINGABE,
          "de/oberbrechen/koeb/gui/icon-bestand.png", mitarbeiter);
    addBarcodeScanner(this);
  }
  
  public MedienListe getAlleMedien() {
    try {
      if (alleMedien == null)
          alleMedien = Datenbank.getInstance().getMediumFactory().
            getAlleMedienInklusiveEntfernte();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    
    return alleMedien;      
  }
  /**
   * Liefert die Standardwerte, die zum erzeugen neuer Medien benutzt werden 
   * sollen.
   * @return die Standardwerte
   */
  public Standardwerte getStandardwerte() {
    if (standardwerte == null)
      standardwerte = new Standardwerte();
    return standardwerte;
  }
  
  public static void main(String[] args) {
    init();
    new Main(true, null);
  }

  //Doku siehe bitte Interface
  public void barcodeGelesen(final String barcode) {
    new Thread() {
      public void run() {
        EAN ean = new EAN(barcode);        
        final Object referenz = eanZuordnung.getReferenz(ean);
        
        if (referenz == null) return;
        if (referenz instanceof Medium) {
          SwingUtilities.invokeLater(
            new Thread() {
              public void run() {                
                ((BestandMainReiter) reiter.getSelectedComponent()).mediumEANGelesen(
                  (Medium) referenz);
              }
          });
        }
        if (referenz instanceof ISBN) {
          SwingUtilities.invokeLater(
            new Thread() {
              public void run() {                
                ((BestandMainReiter) reiter.getSelectedComponent()).ISBNGelesen(
                  (ISBN) referenz);
              }
          });
        }
      }
    }.start();
  }

  //Doku siehe bitte Interface
  public void barcodeStartGelesen() {
    reiter.grabFocus();
  }

  protected void initDaten() {
  }

  protected void reiterHinzufuegen() {
    reiter.add(new MedienReiter(this), "Bestand");
    reiter.add(new StandardwertReiter(this), "Standardwerte");
    reiter.add(new AusgabenReiter(this), "Ausgaben");
  }
}