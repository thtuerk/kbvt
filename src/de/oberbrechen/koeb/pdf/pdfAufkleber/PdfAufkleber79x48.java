package de.oberbrechen.koeb.pdf.pdfAufkleber;

import com.lowagie.text.PageSize;

/**
 * Diese Klasse dient dazu, Aufkleber im Format 79 mm mal 48 mm auszugeben. 
 * Dieses Format entspricht in etwa Scheckkartengröße, abgerechnet einem
 * Rand von 3 mm an allen Seiten. Dieser Rand kommt zum Beispiel durch
 * Laminieren wieder hinzu, so dass die laminierten Aufkleber Scheckkartengröße
 * besitzen.  
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class PdfAufkleber79x48 extends AbstractPdfAufkleber {
  
  public PdfAufkleber79x48() {
    super(PageSize.A4);
  }
  
  protected PdfPosition getAufkleberPosition(int aufkleberNr) {
    int spalte = (aufkleberNr - 1) % 2 + 1;
    int zeile = (aufkleberNr - spalte) / 2;
    
    float x = 74+(spalte-1)*224;
    float y = 13+(5-zeile)*136;

    return new PdfPosition(x, y);
  }

  protected int getAufkleberAnzahlProSeite() {
    return 12;
  }

  protected PdfDimension getAufkleberDimension() {
    return new PdfDimension(224, 136);
  }   
}