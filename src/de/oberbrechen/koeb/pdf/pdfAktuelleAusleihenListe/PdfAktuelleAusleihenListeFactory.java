package de.oberbrechen.koeb.pdf.pdfAktuelleAusleihenListe;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.UeberschriftPdfTemplateFactory;

/**
 * Erstellt ein PdfDokument, das eine Liste aller aktuellen Mahnungen
 * enthaelt.
 */
public class PdfAktuelleAusleihenListeFactory extends PdfTemplateDokumentFactory {

  protected BenutzerListe benutzerListe;

  public PdfAktuelleAusleihenListeFactory(BenutzerListe benutzerListe) {
    this.benutzerListe = benutzerListe;
  }
  
  
  private void erstelleAusleihenTemplates(Benutzer benutzer, 
      PdfTemplateDokument result) throws DatenbankInkonsistenzException {
    result.addUnterUeberschrift(benutzer.getNameFormal(), null, null);
        
    AktuelleAusleihenTabellenModell tabellenModell = 
      new AktuelleAusleihenTabellenModell(benutzer);      
    PdfTabelle tabelle = new PdfTabelle(tabellenModell);    
    result.addPdfTemplateFactory(tabelle);
  }



  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = 
      createLeeresPdfTemplateDokument("Aktuelle Ausleihen");
    
    result.addPdfTemplateFactory(
        new UeberschriftPdfTemplateFactory("Aktuelle Ausleihen", null, null));
    
    for (int i = 0; i < benutzerListe.size(); i++) {
      erstelleAusleihenTemplates(
          (Benutzer) benutzerListe.get(i), result);      
    }
    
    return result;
  }
}
