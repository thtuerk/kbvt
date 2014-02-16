package de.oberbrechen.koeb.pdf.pdfTabelle;

/**
* Diese Exception wird geworfen, wenn eine PdfTabelle in ein Template
* geschrieben werden soll, das dafür zu kurz ist.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class TemplateZuKurzException extends Exception {

  public TemplateZuKurzException() {
    super("Die PDFTabelle konnte nicht geschrieben werden, da das Template zu kurz ist!");
  }

}
