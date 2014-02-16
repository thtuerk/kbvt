package de.oberbrechen.koeb.pdf.pdfTabelle;

/**
* Diese Exception wird geworfen, wenn eine PdfTabelle geschrieben werden soll,
* deren unveränderliche Spalten zusammen breiter als die verfügbare Seitenbreite
* ist.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class SpaltenZuBreitException extends Exception {

  public SpaltenZuBreitException() {
    super("Die PDFTabelle konnte nicht geschrieben werden, da die nicht "+
          "skalierbaren Spalten zusammen breiter als die verfügbare " +
          "Seitenbreite sind!");
  }
}
