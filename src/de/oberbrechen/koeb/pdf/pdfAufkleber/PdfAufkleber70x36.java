package de.oberbrechen.koeb.pdf.pdfAufkleber;

import com.lowagie.text.PageSize;

/**
 * Diese Klasse dient dazu, Aufkleber im Format 70 mm mal 36 mm auszugeben. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class PdfAufkleber70x36 extends AbstractPdfAufkleber {
  
  public PdfAufkleber70x36() {
    super(PageSize.A4);
  }
  
  protected PdfPosition getAufkleberPosition(int aufkleberNr) {
    int spalte = (aufkleberNr - 1) % 3 + 1;
    int zeile = (aufkleberNr - spalte) / 3;
    
    float x = (spalte-1)*198.3333f;
    float y = 11+(7-zeile)*102.5f;

    return new PdfPosition(x, y);
  }

  protected int getAufkleberAnzahlProSeite() {
    return 24;
  }

  protected PdfDimension getAufkleberDimension() {
    return new PdfDimension(198.3333f, 102.5f);
  }
}