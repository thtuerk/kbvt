package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import java.util.Vector;

import com.lowagie.text.pdf.PdfContentByte;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;

/**
 * Diese Klasse stellt eine PdfTemplateFactory dar, die mehrere
 * andere PdfTemplateFactories zu einer einzigen kombiniert.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class KombiniertePdfTemplateFactory implements PdfTemplateFactory {  
  Vector<PdfTemplateFactory> daten;
  
  public KombiniertePdfTemplateFactory() {
    daten = new Vector<PdfTemplateFactory>();
  }
  
  public void addPdfTemplateFactory(PdfTemplateFactory templateFactory) {
    if (templateFactory == null) {
      ErrorHandler.getInstance().handleException(new NullPointerException(), false);
      return;
    }
    
    daten.add(templateFactory);
  }
  
  public void setBreite(float breite) {
    for (int i=0; i < daten.size(); i++) {
      daten.get(i).setBreite(breite);
    }
  }
  
  public boolean hasNextTemplate() {   
    while (!daten.isEmpty()) {
      boolean hasNextTemplate =
        daten.get(0).hasNextTemplate();
      
      if (!hasNextTemplate) {
        daten.remove(0);
      } else {
        return true;
      }
    } 
    
    return false;
  }
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge,
    float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {
    
    //Entfernt evtl. "leere" Factories
    if (!hasNextTemplate()) return null;
    
    return daten.get(0).getNextTemplate(
        optimaleLaenge, maximaleLaenge, erlaubteAbweichung, standardAbstand, parentPdf);
  }  
  
  /**
   * Fügt eine Hauptüberschrift, also eine Überschrift für das gesamte Dokument
   * ein.
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addHauptUeberschrift(String links, String mitte, String rechts) {
    addUeberschrift(links, mitte, rechts, 
        PdfDokumentEinstellungen.getInstance().getSchriftgroesseUeberschriftNormal(),
        PdfDokumentEinstellungen.getInstance().getAbstandUeberschriftNormal());
  }
  
  /**
   * Fügt eine Unterüberschrift, also eine Überschrift für einen Unterabschnitt
   * ein.
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addUnterUeberschrift(String links, String mitte, String rechts) {
    addUeberschrift(links, mitte, rechts, 
        PdfDokumentEinstellungen.getInstance().getSchriftgroesseUeberschriftSehrKlein(),
        PdfDokumentEinstellungen.getInstance().getAbstandUeberschriftSehrKlein());
  }
  
  /**
   * Fügt eine Miniüberschrift, also eine sehr kleine Überschrift ein.
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addMiniUeberschrift(String links, String mitte, String rechts) {
    addUeberschrift(links, mitte, rechts, 
        PdfDokumentEinstellungen.getInstance().getSchriftgroesseUeberschriftMini(),
        PdfDokumentEinstellungen.getInstance().getAbstandUeberschriftMini());
  }

  /**
   * Fügt eine mittlere Überschrift, also eine Überschrift für einen 
   * Abschnitt der selbst mehrere Unterabschnitte enthält.
   * 
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addMittlereUeberschrift(String links, String mitte, String rechts) {
    addUeberschrift(links, mitte, rechts, 
        PdfDokumentEinstellungen.getInstance().getSchriftgroesseUeberschriftKlein(),
        PdfDokumentEinstellungen.getInstance().getAbstandUeberschriftKlein());
  }  
  
  /**
   * Fügt eine Überschrift in der übergebenen Schriftgröße, die den
   * die übergebenen Texte jeweils links, zentriert oder rechts enthält, hinzu.
   * Danach wird abstandFactor*standardAbstand Abstand gelassen.
   */  
  public void addUeberschrift(String links, String mitte, String rechts, 
      float schriftGroesse, float abstandFactor) {
    addPdfTemplateFactory(new UeberschriftPdfTemplateFactory(links, mitte, rechts, 
        schriftGroesse, abstandFactor));
  }  
}