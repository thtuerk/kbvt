package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import java.util.Vector;

import com.lowagie.text.pdf.PdfContentByte;

/**
 * Diese Klasse ist eine Factory für PdfTemplates, die einfach eine
 * Liste von PdfTemplateBeschreibung nacheinander ausgibt.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfTemplateFactoryBeschreibungListe implements PdfTemplateFactory {
  
  public Vector<PdfTemplateBeschreibung> daten = new Vector<PdfTemplateBeschreibung>();
  
  /**
   * Fügt übergebene die PdfTemplateFactory hinzu. Die Factory liefert
   * die hiermit hinzugefügten Beschreibungen in der Hinzufügereihenfolge.
   */
  public void addPdfTemplateBeschreibung(PdfTemplateBeschreibung beschreibung) {
    daten.add(beschreibung);
  }
  
  public void setBreite(float breite) {}  

  public boolean hasNextTemplate() {
    return !daten.isEmpty();
  }
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge,
      float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) {
    if (daten.isEmpty()) return null;
    return daten.remove(0);
  }
}