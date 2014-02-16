package de.oberbrechen.koeb.pdf.pdfBenutzerListe;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.AbsatzPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.BeschriftungenPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfListeDokumentFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;

public class PdfBenutzerListeFactory extends PdfListeDokumentFactory {

  BenutzerListe daten;
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d. MMMM yyyy");
  
  protected int getStandardSortierung() {
    return BenutzerListe.NachnameVornameSortierung;
  }
  
  public PdfBenutzerListeFactory(BenutzerListe daten) {
    this.daten = daten;
    setTitel("Benutzerliste");
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
      case 0: anzahlString = "keine Benutzer"; break;
      case 1: anzahlString = "ein Benutzer"; break;
      default: anzahlString = daten.size()+" Benutzer"; break;
    }
    result.addHauptUeberschrift(titel, null, anzahlString);    
    
    BenutzerListe benutzerListe = new BenutzerListe();
    benutzerListe.addAllNoDuplicate(daten);
    benutzerListe.setSortierung(sortierung, umgekehrteSortierung);
    
    for (int i=0; i < benutzerListe.size(); i++) {
      Benutzer currentBenutzer = (Benutzer) benutzerListe.get(i);
      
      result.addUnterUeberschrift(currentBenutzer.getNameFormal(), null, null);
      
      BeschriftungenPdfTemplateFactory beschriftungenFactory =
        new BeschriftungenPdfTemplateFactory(2);
      beschriftungenFactory.setSpaltenBreite(1, 0.5f);
      beschriftungenFactory.setSpaltenBreite(2, 0.5f);
        
      beschriftungenFactory.addEintrag(1, entferneNull(currentBenutzer.getAdresse()), "Adresse:");
      beschriftungenFactory.addEintrag(1, entferneNull(currentBenutzer.getOrt()), "Ort:");
      beschriftungenFactory.addEintrag(1, formatDatum(currentBenutzer.getGeburtsdatum()), "Geburtstag:");
      beschriftungenFactory.addEintrag(2, entferneNull(currentBenutzer.getTel()), "Tel.:");
      beschriftungenFactory.addEintrag(2, entferneNull(currentBenutzer.getFax()), "Fax:");
      beschriftungenFactory.addEintrag(2, entferneNull(currentBenutzer.getEMail()), "eMail:");
            
      result.addPdfTemplateFactory(beschriftungenFactory);
      
      if (currentBenutzer.getBemerkungen() != null) {
        beschriftungenFactory.setAbstandFactor(0.25f);
        AbsatzPdfTemplateFactory absatzPdfTemplateFactory = 
          new AbsatzPdfTemplateFactory(currentBenutzer.getBemerkungen()); 
        result.addPdfTemplateFactory(absatzPdfTemplateFactory);
      }
    }

    return result;
  }
}