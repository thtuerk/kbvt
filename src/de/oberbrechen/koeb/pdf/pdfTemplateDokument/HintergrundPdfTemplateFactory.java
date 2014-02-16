package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

/**
 * Diese Klasse stellt eine PdfTemplateFactory dar, die mehrere
 * andere PdfTemplateFactories zu einer einzigen kombiniert.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class HintergrundPdfTemplateFactory implements PdfTemplateFactoryMitSpalten {  
  
  PdfTemplateFactory templateFactory;
  float rand;
  float hintergrund;
  float breite;
  
  public HintergrundPdfTemplateFactory(PdfTemplateFactory pdfTemplateFactory) {
    this.templateFactory = pdfTemplateFactory;
    rand = 5;
    hintergrund = 0.8f;
  }    
  
  /**
   * Setzt den Rand, der freigelassen werden soll
   * @param rand der neue Rand
   */
  public void setRand(float rand) {
    this.rand = rand;
  }
  
  /**
   * Setzt den Grauton, der als Hintergrund verwendet wird. 
   * 0 entspricht schwarz, 1 weiß.
   * @param hintergrund
   */
  public void setHintergrund(float hintergrund) {
    this.hintergrund = hintergrund;
  }
  
  public void setBreite(float breite) {
    this.breite = breite;
    templateFactory.setBreite(breite-2*rand);
  }
      
  public boolean hasNextTemplate() {   
    return templateFactory.hasNextTemplate();
  }
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge,
    float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {
    
    PdfTemplateBeschreibung result = templateFactory.getNextTemplate(
        optimaleLaenge-2*rand, maximaleLaenge-2*rand, erlaubteAbweichung, standardAbstand, 
        parentPdf);
    
    PdfTemplate template = parentPdf.createTemplate(
        breite, result.getTemplate().getHeight()+2*rand);    
    template.setGrayFill(hintergrund);
    template.rectangle(0, 0, 
        template.getWidth(), template.getHeight());
    template.fillStroke();
    template.setGrayFill(0);

    template.addTemplate(result.getTemplate(), rand, rand);
    result.template = template;
    result.seitenAusgleich = false;
    
    return result;
  }

  public void setSpaltenAnzahl(int spalten) {
    if (templateFactory instanceof PdfTemplateFactoryMitSpalten) {
      ((PdfTemplateFactoryMitSpalten) templateFactory).setSpaltenAnzahl(spalten);
    }
  }
}