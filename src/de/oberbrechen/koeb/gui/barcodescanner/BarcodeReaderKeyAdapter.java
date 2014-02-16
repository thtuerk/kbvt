package de.oberbrechen.koeb.gui.barcodescanner;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
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

public class BarcodeReaderKeyAdapter extends KeyAdapter 
  implements BarcodeReaderAdapter {
  
  private StringBuffer buffer;
  private String start;
  private String ende;
  private BarcodeGelesenEventHandler eventHandler;
  private boolean praefixSchonGelesen;

  
  public static void setStandardStart(String start) {
    try {
      Einstellung einstellung =  
      Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
        BarcodeReaderKeyAdapter.class.getName(), "start");
      einstellung.setWert(start);
      einstellung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern des " +
          "Standardwertes für die Startzeichenfolge des Barcodescanners!", false);
    }    
  }
  
  public static void setStandardEnde(String ende) {
    try {
      Einstellung einstellung =  
        Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
            BarcodeReaderKeyAdapter.class.getName(), "ende");
      einstellung.setWert(ende);
      einstellung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern des " +
          "Standardwertes für die Endzeichenfolge des Barcodescanners!", false);
    }    
  }
  
  public BarcodeReaderKeyAdapter() {
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory(); 
    
    start = einstellungFactory.getClientEinstellung(
        this.getClass().getName(), "start").getWertHex("\5");    
    
    ende = einstellungFactory.getClientEinstellung(
        this.getClass().getName(), "ende").getWertHex("\12");    
    
    buffer = new StringBuffer();
    praefixSchonGelesen = false;
  }

  public void keyReleased(KeyEvent e) {
    buffer.append(e.getKeyChar());
    if (!praefixSchonGelesen) {
      while (buffer.length() > start.length()) buffer.deleteCharAt(0);
      if (buffer.toString().equals(start)) {
        praefixSchonGelesen = true;
        buffer = new StringBuffer();
        eventHandler.barcodeStartGelesen();
      }
    } else {
      if (buffer.toString().endsWith(ende)) {
        int i = 0;
        while (i < buffer.length()) {
          if (!(Character.isLetter(buffer.charAt(i)) ||
                Character.isDigit(buffer.charAt(i)))) {
            buffer.deleteCharAt(i);
          } else {
            i++;
          }
        }

        String ausgabe = buffer.substring(0, buffer.length()-ende.length()+1);
        eventHandler.barcodeGelesen(ausgabe);

        buffer.delete(0, buffer.length());
        praefixSchonGelesen = false;
      }
    }
  }

  public void setEventHandler(BarcodeGelesenEventHandler eventHandler) {
    this.eventHandler = eventHandler;
  }
}