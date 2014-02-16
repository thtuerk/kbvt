package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import java.util.Iterator;

import com.lowagie.text.pdf.BaseFont;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BemerkungenListe;
import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.AbsatzPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.BeschriftungenPdfTemplateFactory;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.KombiniertePdfTemplateFactory;

public class VeranstaltungPdfTemplateFactory 
  extends KombiniertePdfTemplateFactory {
    
  public VeranstaltungPdfTemplateFactory(Veranstaltung veranstaltung, 
      boolean zeigeTeilnehmerAnzahl, boolean zeigeBeschreibung, 
      boolean zeigeBemerkungen) throws DatenbankInkonsistenzException {
        
    BeschriftungenPdfTemplateFactory beschriftungenPdfTemplateFactory =
      getBeschriftungenTemplate(veranstaltung, zeigeTeilnehmerAnzahl);
    this.addPdfTemplateFactory(beschriftungenPdfTemplateFactory);
    
    
    BemerkungenListe bemerkungenListe = new BemerkungenListe();
    if (zeigeBemerkungen) {
      BemerkungVeranstaltungFactory factory = 
        Datenbank.getInstance().getBemerkungVeranstaltungFactory();        
      bemerkungenListe = 
        factory.getAlleVeranstaltungsbemerkungen(veranstaltung);
      bemerkungenListe.setSortierung(BemerkungenListe.DatumSortierung);
    }

    boolean beschreibungSichtbar = zeigeBeschreibung && veranstaltung.getBeschreibung() != null;
    boolean bemerkungenSichtbar = zeigeBemerkungen && bemerkungenListe.size() > 0;

    beschriftungenPdfTemplateFactory.setAbstandFactor(bemerkungenSichtbar || beschreibungSichtbar?
        0.25f:1f);
      
    
    if (beschreibungSichtbar) {
      AbsatzPdfTemplateFactory absatzPdfTemplateFactory = 
        new AbsatzPdfTemplateFactory(veranstaltung.getBeschreibung()); 
      absatzPdfTemplateFactory.setAbstandFactor(bemerkungenSichtbar?0.5f:1f);
      this.addPdfTemplateFactory(absatzPdfTemplateFactory);
    }        
    
    if (bemerkungenSichtbar) {        
      this.addMiniUeberschrift("Bemerkungen:", null, null);        
      for (int j=0; j < bemerkungenListe.size(); j++) {
        Bemerkung bemerkung = (Bemerkung) bemerkungenListe.get(j);
        
        BaseFont schrift = bemerkung.istAktuell()?PdfDokumentEinstellungen.schriftNormal:PdfDokumentEinstellungen.schriftKursiv;
        AbsatzPdfTemplateFactory absatzPdfTemplateFactory = 
          new AbsatzPdfTemplateFactory(bemerkung.getBemerkung(), schrift);
        absatzPdfTemplateFactory.setAbstandFactor((j == bemerkungenListe.size()-1)?1f:0.25f);
        this.addPdfTemplateFactory(absatzPdfTemplateFactory);
      }
    }    
  }
  
  private BeschriftungenPdfTemplateFactory getBeschriftungenTemplate(Veranstaltung veranstaltung, boolean zeigeTeilnehmerAnzahl) {
    BeschriftungenPdfTemplateFactory beschriftungen =
      new BeschriftungenPdfTemplateFactory(2);
    beschriftungen.setSpaltenBreite(1, 0.5f);
    beschriftungen.setSpaltenBreite(2, 0.5f);
    beschriftungen.setZeigeExtraBeschriftungsspalte(1, false);
    
    TerminListe termine = veranstaltung.getTermine();
    termine.setSortierung(TerminListe.chronologischeSortierung);
    
    if (termine.size() > 0) {
      String terminLabel = termine.size() > 1?"Termine:":"Termin:";    
      Iterator<Termin> it = termine.iterator();
      Termin termin = it.next();
      beschriftungen.addEintrag(1, termin.getZeitraumFormat(Zeitraum.ZEITRAUMFORMAT_LANG_MIT_UHRZEIT), terminLabel);
      while (it.hasNext()) {
        termin = it.next();
        beschriftungen.addEintrag(1, termin.getZeitraumFormat(Zeitraum.ZEITRAUMFORMAT_LANG_MIT_UHRZEIT));
      }
    }
    
    String ansprechPartner = veranstaltung.getAnsprechpartner();
    if (ansprechPartner == null) ansprechPartner = "-";
    beschriftungen.addEintrag(2, ansprechPartner, "Ansprechpartner:");
    
    String bezugsGruppe = veranstaltung.getBezugsgruppe();
    if (bezugsGruppe == null) bezugsGruppe = "-";
    beschriftungen.addEintrag(2, bezugsGruppe, "Bezugsgruppe:");
    
    beschriftungen.addEintrag(2, veranstaltung.getKostenFormatiert(), "Kosten:");
    
    if (zeigeTeilnehmerAnzahl) {
      VeranstaltungsteilnahmeFactory teilnahmeFactory = 
        Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
      
      int teilnehmerAnzahl = teilnahmeFactory.getTeilnehmerAnzahl(veranstaltung);
      beschriftungen.addEintrag(2,Integer.toString(teilnehmerAnzahl), "Teilnehmeranzahl:");
      
      int wartelistenInt = teilnahmeFactory.getWartelistenLaenge(veranstaltung);
      if (wartelistenInt > 0) {
        beschriftungen.addEintrag(2,Integer.toString(wartelistenInt), "Auf Warteliste:");        
      }

    }    
    
    return beschriftungen;
  }
}
