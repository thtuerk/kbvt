package de.oberbrechen.koeb.gui.barcodescanner;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EANZuordnung;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse stellt einen KeyAdapter da, der dazu dient,
 * die Eingabe auf mögliche Barcodes zu überprüfen. Dazu wird
 * nach einer bestimmten Anfangszeichenfolge gesucht. Wird dieser
 * erkannt, wird ein entsprechendes Event ausgelöst. Dann wird nach
 * einer Endzeichenfolge gesucht. Wird diese gefunden, so werden
 * die dazwischenliegenden Zeichen als gelesener Barcode ausgegeben
 * und die Suche neu begonnen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class DebugBarcodeReaderKeyAdapter extends KeyAdapter implements BarcodeReaderAdapter {

  private BarcodeGelesenEventHandler eventHandler;
  private MedienListe alleMedien;
  private BenutzerListe alleBenutzer;
  private Random random;
  private EANZuordnung eanZuordnung;
  private String[] isbns = {"9780552134620", "9781893115576", "9783897212251",
      "978389667248", "978389716506", "978389716517", "978389736323", "978389897019"};
  
  public DebugBarcodeReaderKeyAdapter() {
    random = new Random();
    eanZuordnung = Datenbank.getInstance().getEANZuordnung();
    
    try {
      alleBenutzer = Datenbank.getInstance().getBenutzerFactory().getAlleAktivenBenutzer();
      alleMedien = Datenbank.getInstance().getMediumFactory().getAlleMedien();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }    
  }

  public void keyReleased(KeyEvent e) {
    //CTRL-M
    if ((e.getKeyCode() == 77) && 
         ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
      Medium medium = alleMedien.get(
          random.nextInt(alleMedien.size())); 
      eventHandler.barcodeGelesen(eanZuordnung.getMediumEAN(medium).getEAN());
    }

    //CTRL-B
    if ((e.getKeyCode() == 66) && 
        ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
      Benutzer benutzer = (Benutzer) alleBenutzer.get(
          random.nextInt(alleBenutzer.size())); 
      eventHandler.barcodeGelesen(eanZuordnung.getBenutzerEAN(benutzer).getEAN());
    }

    //CTRL-I
    if ((e.getKeyCode() == 73) && 
        ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
      String isbn = isbns[random.nextInt(isbns.length)]; 
      eventHandler.barcodeGelesen(isbn);
    }
  }
  
  public void setEventHandler(BarcodeGelesenEventHandler eventHandler) {
    this.eventHandler = eventHandler;
  }  
}