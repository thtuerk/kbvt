package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;

/**
 * Der Standard-Seitenkopf für jede Seite.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class UeberschriftPdfTemplateFactory implements PdfTemplateFactory {

  final BaseFont schrift = PdfDokumentEinstellungen.schriftFett;   
  
  float schriftGroesse;
  String textLinks;
  String textMitte;
  String textRechts;
  boolean hasNextTemplate;
  float breite;
  
  float abstandFactor;
  
  /**
   * Erstellt eine neue UeberschriftPdfTemplateFactory, die den
   * die übergebenen Texte jeweils links, zentriert oder rechts in einer
   * in der gewählten Schriftgroesse ausgibt.
   */
  public UeberschriftPdfTemplateFactory(String links, String mitte, String rechts,
    float schriftGroesse) {
    this(links, mitte, rechts, schriftGroesse, 1);
  }
  
  /**
   * Erstellt eine neue UeberschriftPdfTemplateFactory, die den
   * die übergebenen Texte jeweils links, zentriert oder rechts in einer
   * in der gewählten Schriftgroesse ausgibt.
   */
  public UeberschriftPdfTemplateFactory(String links, String mitte, String rechts,
      float schriftGroesse, float abstandFactor) {

    textLinks = links;
    textMitte = mitte;
    textRechts = rechts;

    this.schriftGroesse = schriftGroesse;
    hasNextTemplate = true;
    this.abstandFactor = abstandFactor;
  }

  /**
   * Setzt den Abstand, der nach der Überschrift frei bleiben soll,
   * als Faktor vom Standardabstand
   * @param abstandFactor
   */
  public void setAbstandFactor(float abstandFactor) {
    this.abstandFactor = abstandFactor;
  }
  
  /**
   * Erstellt eine neue UeberschriftPdfTemplateFactory, die den
   * die übergebenen Texte jeweils links, zentriert oder rechts in einer
   * in der Standard-Schriftgroesse ausgibt.
   */
  public UeberschriftPdfTemplateFactory(String links, String mitte, String rechts) {
    this(links, mitte, rechts, PdfDokumentEinstellungen.getInstance().getSchriftgroesseUeberschriftNormal());
  }

  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, 
      float maximaleLaenge, float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {    
    hasNextTemplate = false;
    float hoehe = 5*schriftGroesse/4; 
    PdfTemplate template = parentPdf.createTemplate(
      breite, hoehe);

    //Text ausgeben
    float textBreite = -40;
    float textLinksBreite = 0;
    if (textLinks != null) {
      textLinksBreite = schrift.getWidthPoint(textLinks, schriftGroesse);
      textBreite += 40+textLinksBreite;
    }

    float textMitteBreite = 0;
    if (textMitte != null) {
      textMitteBreite = schrift.getWidthPoint(textMitte, schriftGroesse);
      textBreite += 40+textMitteBreite;
    }

    float textRechtsBreite = 0;
    if (textRechts != null) {
      textRechtsBreite = schrift.getWidthPoint(textRechts, schriftGroesse);
      textBreite += 40+textRechtsBreite;
    }

    float textScalierung = 1;
    if (textBreite > breite)
      textScalierung =  breite / textBreite;

    template.beginText();
    template.setFontAndSize(schrift, schriftGroesse);
    template.setHorizontalScaling(textScalierung*100);


    if (textLinks != null) {
      template.setTextMatrix(0, hoehe/4);
      template.showText(textLinks);
    }

    if (textMitte != null) {
      template.setTextMatrix((breite / 2) - ((textMitteBreite * textScalierung) / 2), hoehe/4);
      template.showText(textMitte);
    }

    
    if (textRechts != null) {
      template.setTextMatrix(breite - textRechtsBreite*textScalierung, hoehe/4);
      template.showText(textRechts);
    }

    template.endText();
    
    PdfTemplateBeschreibung result = new PdfTemplateBeschreibung(template, 
        standardAbstand*abstandFactor);
    result.verbieteSeitenUmbruch(true);
    return result;
  }

  public void setBreite(float breite) {
    this.breite = breite;
  }

  public boolean hasNextTemplate() {
    return hasNextTemplate;
  }
}