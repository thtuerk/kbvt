package de.oberbrechen.koeb.pdf.pdfMedienListe;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.AbsatzPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.BeschriftungenPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfListeDokumentFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;

public class PdfMedienListeDetailliertFactory extends PdfListeDokumentFactory {

  MedienListe daten;
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d. MMMM yyyy");
  
  protected int getStandardSortierung() {
    return MedienListe.TitelAutorSortierung;
  }
  
  public PdfMedienListeDetailliertFactory(MedienListe daten) {
    this.daten = daten;
    setTitel("Medienliste");
  }
  
  private String formatDatum(Date datum) {
    if (datum == null) return "-";
    return simpleDateFormat.format(datum);
  }
  
  private String entferneNull(String string) {
    if (string == null || string.length() == 0) return "-";
    return string;
  }

  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(titel);
    String anzahlString;
    switch (daten.size()) {
      case 0: anzahlString = "keine Medien"; break;
      case 1: anzahlString = "ein Medium"; break;
      default: anzahlString = daten.size()+" Medien"; break;
    }
    result.addHauptUeberschrift(titel, null, anzahlString);    
    
    MedienListe medienListe = new MedienListe();
    medienListe.addAllNoDuplicate(daten);
    medienListe.setSortierung(sortierung, umgekehrteSortierung);
    
    for (int i=0; i < medienListe.size(); i++) {
      Medium currentMedium = (Medium) medienListe.get(i);
      
      result.addUnterUeberschrift(currentMedium.getTitel(), null, null);
      
      BeschriftungenPdfTemplateFactory beschriftungenFactory =
        new BeschriftungenPdfTemplateFactory(2);
      beschriftungenFactory.setSpaltenBreite(1, 0.5f);
      beschriftungenFactory.setSpaltenBreite(2, 0.5f);
        
      beschriftungenFactory.addEintrag(1, currentMedium.getMedientyp().getName(), "Medientyp.:");
      beschriftungenFactory.addEintrag(1, currentMedium.getMedienNr(), "Mediennr.:");
      beschriftungenFactory.addEintrag(1, entferneNull(currentMedium.getAutor()), "Autor:");
      beschriftungenFactory.addEintrag(1, currentMedium.getISBN()==null?"-":currentMedium.getISBN().toString(), "ISBN:");

      beschriftungenFactory.addEintrag(2, formatDatum(currentMedium.getEinstellungsdatum()), "eingestellt seit:");
      beschriftungenFactory.addEintrag(2, formatDatum(currentMedium.getEntfernungsdatum()), "entfernt seit:");
      beschriftungenFactory.addEintrag(2, entferneNull(currentMedium.getSystematikenString()), "Systematiken:");
            
      result.addPdfTemplateFactory(beschriftungenFactory);
      
      if (currentMedium.getBeschreibung() != null) {
        result.verbieteSeitenUmbruch();
        beschriftungenFactory.setAbstandFactor(0.25f);
        AbsatzPdfTemplateFactory absatzPdfTemplateFactory = 
          new AbsatzPdfTemplateFactory(currentMedium.getBeschreibung()); 
        result.addPdfTemplateFactory(absatzPdfTemplateFactory);
      }
    }

    return result;
  }
}