package de.oberbrechen.koeb.gui.barcodescanner;

/**
 * Dieses Interface kann zusammen mit dem BarcodeReaderKeyAdapter dazu benutzt
 * werden, auf einen eingelesenen Barcode zu reagieren. Erkennt der
 * BarcodeReaderKeyAdapter einen Barcode, so wird die Methode barcodeRead
 * des passenden BarcodeReadEventHandlers aufgerufen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface BarcodeGelesenEventHandler {

  /**
   * Reagiert auf das Lesen des im Parameter übergebenen Barcodes.
   *
   * @param barcode der belesene Barcode
   */
  public void barcodeGelesen(String barcode);

  /**
   * Reagiert auf das Lesen des Beginns eines Barcodes.
   *
   * @param barcode der belesene Barcode
   */
  public void barcodeStartGelesen();
}