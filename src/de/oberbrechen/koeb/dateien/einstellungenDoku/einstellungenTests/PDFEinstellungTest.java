package de.oberbrechen.koeb.dateien.einstellungenDoku.einstellungenTests;

import javax.swing.JFrame;

import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungTest;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.pdfTestDokument.PdfTestDokumentFactory;

/**
 * Diese Klasse führt einen PDF-Test aus.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PDFEinstellungTest implements EinstellungTest {

  public void testeEinstellung(JFrame main, Einstellung einstellung) throws Exception {
    PdfDokumentEinstellungen.reset();
    new PdfTestDokumentFactory().createPdfDokument().zeige(false);
  }
}
