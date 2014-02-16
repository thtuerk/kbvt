package de.oberbrechen.koeb.pdf.pdfAufkleber;

import com.lowagie.text.PageSize;

/**
 * Diese Klasse dient dazu, Aufkleber im Format 35 mm mal 36 mm auszugeben.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class PdfAufkleber35x36 extends AbstractPdfAufkleber {
  
  public PdfAufkleber35x36() {
    super(PageSize.A4);
  }
  
  protected PdfPosition getAufkleberPosition(int aufkleberNr) {
    int spalte = (aufkleberNr - 1) % 6 + 1;
    int zeile = (aufkleberNr - spalte) / 6;
    
    float x = (spalte-1)*99f;
    float y = 11+(7-zeile)*102.5f;

    return new PdfPosition(x, y);
  }

  protected int getAufkleberAnzahlProSeite() {
    return 48;
  }

  protected PdfDimension getAufkleberDimension() {
    return new PdfDimension(99f, 102.5f);
  }
}