package de.oberbrechen.koeb.pdf.pdfTestDokument;

import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.AbsatzPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

/**
 * Erstellt ein PdfDokument, das für Tests gedacht ist.
 */
public class PdfTestDokumentFactory extends PdfTemplateDokumentFactory {

  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = 
      createLeeresPdfTemplateDokument("TestDokument");
    
    result.addHauptUeberschrift("Überschrift 1", null, null);
    result.addMittlereUeberschrift("Überschrift 2", null, null);
    result.addUnterUeberschrift("Überschrift 3", null, null);
    result.addMiniUeberschrift("Überschrift 4", null, null);
    result.addAbstandRelativ(2);
    
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < 100; i++) {
      buffer.append("Test-Absatz ");
    }
    result.addPdfTemplateFactory(new AbsatzPdfTemplateFactory(buffer.toString()));
    
    result.addSeitenUmbruch();
    result.addUnterUeberschrift("Alle Einstellungen", null, null);
    PdfTabelle tabelle = new PdfTabelle(new EinstellungenTabellenModell());
    result.addPdfTemplateFactory(tabelle);
    
    return result;
  }

}
