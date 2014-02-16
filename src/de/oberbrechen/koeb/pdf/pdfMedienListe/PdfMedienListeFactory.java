package de.oberbrechen.koeb.pdf.pdfMedienListe;

import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelleDokumentFactory;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;

public class PdfMedienListeFactory extends PdfTabelleDokumentFactory {

  MedienListe daten;
  
  protected int getStandardSortierung() {
    return MedienListe.TitelAutorSortierung;
  }
  
  public PdfMedienListeFactory(MedienListe daten) {
    this.daten = daten;
    setTitel("Medienliste");
  }
  
  
  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(titel);
    String anzahlString;
    switch (daten.size()) {
      case 0: anzahlString = "keine Medien"; break;
      case 1: anzahlString = "1 Medium"; break;
      default: anzahlString = daten.size()+" Medien"; break;
    }
    result.addHauptUeberschrift(titel, null, anzahlString);    
    
    //Modell bauen
    TabellenModell modell = new PdfMedienListeTabellenModell(
        daten, sortierung, umgekehrteSortierung);

    if (zeigeSortierteSpalteHintergrund) {
      switch (sortierung) {
      case MedienListe.AutorTitelSortierung:
        modell.setZeigeSpaltenHintergrund(3, true);
        break;
      case MedienListe.TitelAutorSortierung:
        modell.setZeigeSpaltenHintergrund(2, true);
        break;
      case MedienListe.MedienNummerSortierung:
        modell.setZeigeSpaltenHintergrund(1, true);
        break;
      }
    }
    modell.setZeigeZeilenHintergrund(zeigeZeilenHintergrund);    

    //alles zusammenbauen
    PdfTabelle tabelle = new PdfTabelle(modell);
    result.addPdfTemplateFactory(tabelle);    
    
    return result;
  }
}