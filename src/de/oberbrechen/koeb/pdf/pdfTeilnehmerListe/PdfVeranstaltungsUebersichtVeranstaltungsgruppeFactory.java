package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.components.KalenderPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.*;

public class PdfVeranstaltungsUebersichtVeranstaltungsgruppeFactory
  extends PdfTemplateDokumentFactory {
  
  boolean zeigeBeschreibung;
  boolean zeigeBemerkungen;
  Veranstaltungsgruppe gruppe;
  
  public PdfVeranstaltungsUebersichtVeranstaltungsgruppeFactory(
      Veranstaltungsgruppe veranstaltungsgruppe) {
    
    zeigeBemerkungen = true;
    zeigeBeschreibung = true;
    this.gruppe = veranstaltungsgruppe;
  }

  
  public void setZeigeBeschreibung(boolean zeigeBeschreibung) {
    this.zeigeBeschreibung = zeigeBeschreibung;
  }
  
  public void setZeigeBemerkungen(boolean zeigeBemerkungen) {
    this.zeigeBemerkungen = zeigeBemerkungen;
  }
  
  
  private PdfTemplateFactory createKalenderTemplateFactory(VeranstaltungenListe veranstaltungen) {
    KalenderPdfTemplateFactory kalender = new KalenderPdfTemplateFactory();
    
    //Daten in Kalender eintragen
    for (int i=0; i < veranstaltungen.size(); i++) {
      TerminListe termine = ((Veranstaltung) veranstaltungen.get(i)).getTermine();    
      for (int j=0; j < termine.size(); j++) {
        Termin termin = (Termin) termine.get(j);
        Date datum = termin.getBeginn();        
        kalender.addEintrag(datum, Integer.toString(i+1));
      }      
    }

    return new SpaltenPdfTemplateFactory(kalender,3);
  }


  /* (non-Javadoc)
   * @see de.oberbrechen.koeb.pdf.PdfDokumentFactory#createPdfDokument()
   */
  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(
        "Veranstaltungsübersicht "+gruppe.getName());    
    result.addHauptUeberschrift("Veranstaltungsübersicht "+gruppe.getName(), null, null);
    
    VeranstaltungenListe veranstaltungen = 
      Datenbank.getInstance().getVeranstaltungFactory().getVeranstaltungen(gruppe);
    veranstaltungen.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);

    //Kalender
    result.addPdfTemplateFactory(createKalenderTemplateFactory(veranstaltungen));
    
    for (int i=0; i < veranstaltungen.size(); i++) {
      Veranstaltung currentVeranstaltung = (Veranstaltung) veranstaltungen.get(i);
      String titel = (i+1)+") "+currentVeranstaltung.getTitel();
      result.addUnterUeberschrift(titel, null, null);
      
      VeranstaltungPdfTemplateFactory veranstaltungPdfTemplateFactory =
        new VeranstaltungPdfTemplateFactory(currentVeranstaltung, 
            currentVeranstaltung.istAnmeldungErforderlich(), zeigeBeschreibung, 
            zeigeBemerkungen);
      result.addPdfTemplateFactory(veranstaltungPdfTemplateFactory);      
    }
    
    return result;
  }  
}
