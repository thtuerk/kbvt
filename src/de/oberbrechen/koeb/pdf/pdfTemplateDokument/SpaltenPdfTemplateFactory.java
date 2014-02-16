package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import java.util.Vector;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

/**
 * Diese Klasse stellt eine PdfTemplateFactory dar, die eine
 * andere PdfTemplateFactories in mehreren Spalten darstellt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class SpaltenPdfTemplateFactory implements PdfTemplateFactory {  
  
  PdfTemplateFactory templateFactory;
  int spaltenAnzahl;
  float spaltenAbstand;
  float zeilenAbstand;
  float breite;
  boolean initialisiert;
  
  public SpaltenPdfTemplateFactory(PdfTemplateFactory pdfTemplateFactory, 
      int spaltenAnzahl) {
    
    this.templateFactory = pdfTemplateFactory;
    this.spaltenAnzahl = spaltenAnzahl;
    
    if (templateFactory instanceof PdfTemplateFactoryMitSpalten) {
      ((PdfTemplateFactoryMitSpalten) templateFactory).setSpaltenAnzahl(spaltenAnzahl);
    }
    spaltenAbstand = -1;
    zeilenAbstand = -1;
    initialisiert = false;
  }    
  
  /**
   * Setzt den Abstand zwischen Spalten
   */
  public void setSpaltenAbstand(float spaltenAbstand) {
    this.spaltenAbstand = spaltenAbstand;
  }
  
  /**
   * Setzt den Abstand zwischen Zeilen
   */
  public void setZeilenAbstand(float zeilenAbstand) {
    this.zeilenAbstand = zeilenAbstand;
  }
  
  public void setBreite(float breite) {
    this.breite = breite;
  }
      
  public boolean hasNextTemplate() {   
    return templateFactory.hasNextTemplate();
  }
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge,
    float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {
    
    if (!initialisiert) {
      zeilenAbstand = zeilenAbstand > 0?zeilenAbstand:standardAbstand;
      spaltenAbstand = spaltenAbstand > 0?spaltenAbstand:standardAbstand;
      
      templateFactory.setBreite(
          (breite-(spaltenAnzahl-1)*spaltenAbstand)/spaltenAnzahl);
      initialisiert = true;
    }
    
    
    Vector<PdfTemplateBeschreibung> daten = new Vector<PdfTemplateBeschreibung>();
    
    int i = 0;
    float maxLaenge = 0;
    float currentLaenge = 0;
    while (i < spaltenAnzahl && templateFactory.hasNextTemplate()) {
      PdfTemplateBeschreibung beschreibung = 
        templateFactory.getNextTemplate(
            optimaleLaenge, maximaleLaenge, erlaubteAbweichung, standardAbstand, 
            parentPdf);
      currentLaenge += beschreibung.getHeight(); 
      daten.add(beschreibung);
      
      if (!beschreibung.verbieteSeitenUmbruch) {
        if (currentLaenge > maxLaenge) maxLaenge = currentLaenge;        
        currentLaenge = 0;
        i++;
      } else {
        currentLaenge += beschreibung.abstand;
      }
    }

    PdfTemplate template = parentPdf.createTemplate(
        breite, maxLaenge);
        
    float spaltenBreiteMitAbstand =
      (breite+spaltenAbstand)/spaltenAnzahl;
    
    int spalte = 0;
    float y = maxLaenge;
    for (i=0; i < daten.size(); i++) {
      PdfTemplateBeschreibung currentBeschreibung =
        daten.get(i);
      y -= currentBeschreibung.getHeight();
      template.addTemplate(
          currentBeschreibung.getTemplate(),
          spalte*spaltenBreiteMitAbstand,
          y);
      
      if (currentBeschreibung.verbieteSeitenUmbruch) {
        y -= currentBeschreibung.abstand;
      } else {
        y = maxLaenge;
        spalte++;
      }
    }
    
    PdfTemplateBeschreibung result = new PdfTemplateBeschreibung(
        template, zeilenAbstand);
 
    return result;
  }
}