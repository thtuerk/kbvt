package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import com.lowagie.text.Rectangle;

import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.PdfDokumentFactory;


/**
 * Eine Factory für PdfFactories
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class PdfTemplateDokumentFactory implements PdfDokumentFactory {

  Rectangle pageSize;
  boolean querFormat;
  boolean debug;
  
  public PdfTemplateDokumentFactory() {
    pageSize = PdfDokumentEinstellungen.getInstance().getPageSize();
    querFormat = false;
    debug = false;
  }
  
  /**
   * Erstellt ein leeres PdfTemplateDokument mit allen Einstellungen
   * @param der Titel des Dokuments
   * @return ein leeres PdfTemplateDokument mit allen Einstellungen
   */
  protected PdfTemplateDokument createLeeresPdfTemplateDokument(String titel) {
    PdfTemplateDokument templateDokument = 
      new PdfTemplateDokument(pageSize, titel, querFormat);
    templateDokument.setDebug(debug);
    return templateDokument;
  }
  
  /**
   * Setzt die zu verwendende Seitengröße
   * @param pageSize
   */
  public void setPageSize(Rectangle pageSize) {
    this.pageSize = pageSize;    
  }
  
  /**
   * Setzt, ob Querformat verwendet werden soll.
   * @param querFormat
   */
  public void setQuerformat(boolean querFormat) {
    this.querFormat = querFormat;
  }
  
  /**
   * Schaltet den Debug-Modus an oder aus. Im Debug-Modus werden
   * Rahmen um alle Templates gezeichnet, um diese klar trennen zu können.
   * 
   * @param debug
   * @return
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }  
}