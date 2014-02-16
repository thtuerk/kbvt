package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import com.lowagie.text.pdf.PdfContentByte;

/**
 * Dieses Interface repräsentiert eine Factory für PdfTemplates. Diese
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface PdfTemplateFactory {
  
  /**
   * Setzt die Breite der zu erstellenden Templates
   * @param breite
   */
  public void setBreite(float breite);
  
  /**
   * Bestimmt, ob die Factory noch Templates enthält
   */
  public boolean hasNextTemplate();
  
  /**
   * Liefert das nächste Template der Factory. Nach Möglichkeit sollte das
   * Template höchstens die Länge optimale Länge besitzen. Muß das 
   * Template zwingend länger sein, so muss zumindest die maximale Länge 
   * eingehalten werden. 
   *  
   * @param optimaleLaenge die optimale Länge, die das Template besitzen sollte
   * @param maximaleLaenge die maximale Länge, die das Template besitzen darf
   * @param erlaubteAbweichung die Länge, die das Template die optimale/maximale 
   *   Länge notfalls überschreiten darf und trotzdem noch auf der Seite 
   *   dargestellt werden kann
   * @param standardAbstand der Standardabstand nach dem Template
   * @throws Exception
   */
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge,
      float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception;      
}