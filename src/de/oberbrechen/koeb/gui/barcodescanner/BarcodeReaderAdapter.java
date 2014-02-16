package de.oberbrechen.koeb.gui.barcodescanner;

/**
 * Diese Klasse stellt das Interface für einen allegemeinen BarcodeReaderAdapter
 * dar. Eigentlich muss nur der BarcodeGelesenEventHandler, der aufgerufen werden
 * soll, angegeben werden. Da die bisherigen Implementierungen in die
 * Tastatureingaben eingeschleiften Signale auswerten, werden Implementierungen
 * Implementierungen, die zusätzlich das Interface KeyAdapter erfüllen,
 * vom Framework automatisch als Keylistener der notwendigen Elemente eingetragen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface BarcodeReaderAdapter {

  public void setEventHandler(BarcodeGelesenEventHandler eventHandler);
}