package de.oberbrechen.koeb.pdf;

import com.lowagie.text.pdf.*;

import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.UeberschriftPdfTemplateFactory;

/**
 * Der Standard-Seitenkopf für jede Seite.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class StandardSeitenKopfErsteSeite implements SeitenKopfFuss{

  SeitenKopfFuss normalerSeitenKopf;
  PdfTemplateFactory ueberschriftPdfTemplateFactory;
  PdfTemplate template;
  float schriftGroesse;
  
  /**
   * Erstellt einen neuen Standard-Seitenkopf für die erste Seite einer
   * pdf-Tabelle, die nach der Überschrift den übergebenen SeitenKopf anzeigt.
   */
  public StandardSeitenKopfErsteSeite(String links, String mitte, String rechts,
    SeitenKopfFuss normalerSeitenKopf) {

    ueberschriftPdfTemplateFactory =
      new UeberschriftPdfTemplateFactory(links, mitte, rechts);        
    this.normalerSeitenKopf = normalerSeitenKopf;
  }

  //Doku siehe bitte Interface
  public float getHoehe(int seitenNr) throws Exception {
    if (normalerSeitenKopf == null) return schriftGroesse+10;
    return 10+schriftGroesse+normalerSeitenKopf.getHoehe(seitenNr);
  }

  //Doku siehe bitte Interface
  public PdfTemplate getSeitenKopfFuss(
    ErweitertesPdfDokument pdfDokument, 
    PdfContentByte pdf, int seitenNr) throws Exception {
    
    if (template != null) return template;
    
    float hoehe = this.getHoehe(seitenNr); 
    PdfTemplate template = pdf.createTemplate(
      pdfDokument.getSeitenBreite(), hoehe);
    
    PdfTemplate ueberschriftTemplate =
      ueberschriftPdfTemplateFactory.getNextTemplate(0, 0, 0, 0, pdf).getTemplate();
    template.addTemplate(ueberschriftTemplate, 0, hoehe-
        ueberschriftTemplate.getHeight());    
    
    if (normalerSeitenKopf != null) {
      PdfTemplate normalerSeitenKopfTemplate =
        normalerSeitenKopf.getSeitenKopfFuss(pdfDokument, pdf, seitenNr);
      template.addTemplate(normalerSeitenKopfTemplate, 0, hoehe-10-
        normalerSeitenKopfTemplate.getHeight()-ueberschriftTemplate.getHeight());
    }

    return template;
  }

  //Doku siehe bitte Interface
  public void finalisiere(int gesamtseitenAnzahl) {
    //nichts zu tun!
  }
}