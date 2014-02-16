package de.oberbrechen.koeb.pdf;


/**
 * Eine Factory für PdfFactories
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface PdfDokumentFactory {

  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public PdfDokument createPdfDokument() throws Exception;
  
}