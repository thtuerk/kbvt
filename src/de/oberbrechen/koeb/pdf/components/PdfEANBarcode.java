package de.oberbrechen.koeb.pdf.components;

import java.util.BitSet;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.oberbrechen.koeb.datenstrukturen.EAN;

/**
 * Diese Klasse kann ein EAN-Barcode als ein PdfTemplate darstellen. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfEANBarcode {

  /**
   * Erstellt aus dem übergebenen EAN-Barcode ein PdfTemplate der
   * übergebenen Höhe und Breite
   */
  public static PdfTemplate getTemplate(EAN ean, PdfContentByte pdf, float breite, float hoehe) {
    PdfTemplate template = pdf.createTemplate(breite, hoehe);
    
    BitSet bitset = ean.getBitSet();
    float balkenbreite = breite/bitset.length();
    
    int aktuelleBreite = 0;
    for (int i=0; i <= bitset.length(); i++) {
      if (bitset.get(i)) {
        aktuelleBreite++;
      } else {
        if (aktuelleBreite > 0) {
          template.rectangle((i-aktuelleBreite)*balkenbreite, 0, aktuelleBreite*balkenbreite, hoehe);
        }
        aktuelleBreite = 0;
      }
    }
    template.fill();    
    
    return template;
  }
}