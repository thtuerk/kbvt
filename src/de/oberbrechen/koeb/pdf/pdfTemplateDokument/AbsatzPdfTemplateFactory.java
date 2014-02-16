package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import java.util.StringTokenizer;
import java.util.Vector;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;

public class AbsatzPdfTemplateFactory implements PdfTemplateFactory {
      
  private float breite = 0;
  private float schriftGroesse;
  private float zeilenAbstandFaktor;
  private String text;
  private BaseFont schrift;
  private float skalierung;
  
  private float abstandFactor;
  
  private String[] zeilen;
  private int currentZeile;
  private static int minZeilenAnzahlBeiTrennung = 3;
  
  public AbsatzPdfTemplateFactory(String text, BaseFont schrift) {
    this.text = text.trim();
    this.schrift = schrift;
    this.schriftGroesse = PdfDokumentEinstellungen.getInstance().getSchriftgroesseNormal();
    this.zeilenAbstandFaktor = PdfDokumentEinstellungen.getInstance().getAbsatzZeilenAbstandFaktor();
    abstandFactor = 1;
    skalierung = 1;
    currentZeile = -1;
    zeilen = null;
  }
  
  public AbsatzPdfTemplateFactory(String text) {
    this (text, PdfDokumentEinstellungen.schriftNormal);
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
   * Setzt die horizontale Skalierung des Textes
   */
  public void setSkalierung(float skalierung) {
    this.skalierung = skalierung;
  }
    
  /**
   * Setzt die zu verwendende Schrift
   * @param die schrift
   */
  public void setSchrift(BaseFont schrift) {
    this.schrift = schrift;
  }
    
  /**
   * Setzt die zu verwendende Schriftgröße
   * @param schriftGroesse
   */
  public void setSchriftGroesse(float schriftGroesse) {
    this.schriftGroesse = schriftGroesse;
  }
  
  /**
   * Setzt den Abstand zwischen zwei Zeilen
   * @param faktor
   */
  public void setZeilenAbstandFaktor(float faktor) {
    this.zeilenAbstandFaktor = faktor;
  }
    
  public void setBreite(float breite) {
    this.breite = breite;
  }

  public boolean hasNextTemplate() {
    return (text != null && text.length() > 0 &&
        (zeilen == null || (currentZeile < zeilen.length)));
  }
  
  private Vector<String> zerlegeInZeilenIntern(String text) {
    Vector<String> strings = new Vector<String>();

    StringTokenizer tokenizer = new StringTokenizer(text, " ");
    String currentString = new String();
    while (tokenizer.hasMoreTokens()) {
      String currentToken = tokenizer.nextToken();
      float currentWidth = skalierung*schrift.
        getWidthPoint(currentString+ " "+currentToken, schriftGroesse);
      if (currentWidth > breite) {
        strings.add(currentString);
        currentString = "";
      }
      if (currentString.length() > 0) 
        currentString += " ";      
      currentString+=currentToken;
    }
    strings.add(currentString);
    
    return strings;    
  }
  
  private String[] zerlegeInZeilen(String text) {
    Vector<String> strings = new Vector<String>();
    
    int pos = 0;
    int lastPos = -1;
    pos = text.indexOf("\n", 0);
    while (pos > 0) {
      if (lastPos+1 == pos) {
        strings.add(new String());
      } else {
        strings.addAll(zerlegeInZeilenIntern(text.substring(lastPos+1, pos)));
      }
      lastPos = pos;
      pos = text.indexOf("\n", lastPos+1);      
    }
    strings.addAll(zerlegeInZeilenIntern(text.substring(lastPos+1)));
    
    return strings.toArray(new String[strings.size()]);    
  }  
    
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge, float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {
    if (zeilen == null) {
      zeilen = zerlegeInZeilen(text);
      currentZeile = 0;
    }

    int zeilenAnzahlInTemplate = (int) (optimaleLaenge/(schriftGroesse*zeilenAbstandFaktor));
    if (zeilenAnzahlInTemplate < minZeilenAnzahlBeiTrennung || 
        zeilen.length - currentZeile - zeilenAnzahlInTemplate < minZeilenAnzahlBeiTrennung) {
      zeilenAnzahlInTemplate = (int) (maximaleLaenge/(schriftGroesse*zeilenAbstandFaktor));
    }    

    if (currentZeile + zeilenAnzahlInTemplate > zeilen.length)
      zeilenAnzahlInTemplate = zeilen.length - currentZeile;
    
        
    float hoehe = zeilenAnzahlInTemplate*zeilenAbstandFaktor*schriftGroesse;
    PdfTemplate template = parentPdf.createTemplate(breite, hoehe);
    
    float y = hoehe -schriftGroesse;
    template.beginText();
    template.setHorizontalScaling(skalierung*100);
    template.setFontAndSize(schrift, schriftGroesse);
    for (int i=0; i < zeilenAnzahlInTemplate; i++) {
      template.setTextMatrix(0, y);
      template.showText(zeilen[currentZeile]);
      y-=zeilenAbstandFaktor*schriftGroesse;
      currentZeile++;
    }
    
    template.endText();    
    return new PdfTemplateBeschreibung(template, standardAbstand*abstandFactor);
  }
}
