package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.*;

public class PdfTeilnehmerListeVeranstaltungsgruppeFactory 
  extends PdfTemplateDokumentFactory {

  boolean zeigeZusammenfassung;
  boolean zeigeBemerkungen;
  Veranstaltungsgruppe veranstaltungsgruppe;
  
  public PdfTeilnehmerListeVeranstaltungsgruppeFactory(
      Veranstaltungsgruppe veranstaltungsgruppe) {
    
    zeigeBemerkungen = true;
    zeigeZusammenfassung = true;
    this.veranstaltungsgruppe = veranstaltungsgruppe;
  }

  
  public void setZeigeZusammenfassung(boolean zeigeZusammenfassung) {
    this.zeigeZusammenfassung = zeigeZusammenfassung;
  }
  
  public void setZeigeBemerkungen(boolean zeigeBemerkungen) {
    this.zeigeBemerkungen = zeigeBemerkungen;
  }
  
  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(veranstaltungsgruppe.getName());
    
    String teilnehmerAnzahl;
    int teilnehmerAnzahlInt = Datenbank.getInstance().
    getVeranstaltungsteilnahmeFactory().getTeilnehmerAnzahl(
        veranstaltungsgruppe);
    teilnehmerAnzahl = teilnehmerAnzahlInt == 0?
      "keine Teilnehmer":teilnehmerAnzahlInt+" Teilnehmer";
      
    result.addHauptUeberschrift(
        veranstaltungsgruppe.getName(), null, teilnehmerAnzahl);
    result.addAbstandRelativ(-0.5f);
    
    //Zusammenfassung
    if (zeigeZusammenfassung) {
      TabellenModell zusammenfassungModell =
        new PdfTeilnehmerListeVeranstaltungsgruppeTabellenModellZusammenfassung(
            veranstaltungsgruppe);
      zusammenfassungModell.setZeigeZeilenHintergrund(false);
      PdfTabelle tabelle = new PdfTabelle(zusammenfassungModell);
      tabelle.setSchriftGroesse(PdfDokumentEinstellungen.getInstance().getSchriftgroesseKlein());
      tabelle.setZeigeSpaltenKopf(false);
      
      HintergrundPdfTemplateFactory hintergrundPdfTemplateFactory = 
        new HintergrundPdfTemplateFactory(tabelle);
      SpaltenPdfTemplateFactory spaltenPdfTemplateFactory =
        new SpaltenPdfTemplateFactory(hintergrundPdfTemplateFactory, 2);
      result.addPdfTemplateFactory(spaltenPdfTemplateFactory);  
    }
     
    //eigentliche Daten
    TabellenModell modell=new PdfTeilnehmerListeVeranstaltungsgruppeTabellenModell(
        veranstaltungsgruppe, zeigeBemerkungen);
    PdfTabelle tabelle = new PdfTabelle(modell);
    tabelle.setAbstandFactor(2);
    result.addPdfTemplateFactory(tabelle);  
    
    
    //Bemerkungen
    if (zeigeBemerkungen) {
      VeranstaltungsteilnahmeFactory veranstaltungsteilnahmeFactory =
        Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
      BenutzerListe teilnehmerMitBemerkungenListe =
        veranstaltungsteilnahmeFactory.getTeilnehmerListe(
            veranstaltungsgruppe);
      teilnehmerMitBemerkungenListe.setSortierung(BenutzerListe.NachnameVornameSortierung);
     
      Iterator<Benutzer> it = teilnehmerMitBemerkungenListe.iterator();
      while (it.hasNext()) {
        Benutzer benutzer = it.next();
        if (benutzer.getBemerkungen() == null || 
            benutzer.getBemerkungen().length() == 0)
          it.remove();
      }
      
      if (teilnehmerMitBemerkungenListe.size() > 0) {
        result.addMittlereUeberschrift("Bemerkungen", null, null);

        KombiniertePdfTemplateFactory kombiniertePdfTemplateFactory = 
          new KombiniertePdfTemplateFactory();
        it = teilnehmerMitBemerkungenListe.iterator();
        while (it.hasNext()) {
          Benutzer benutzer = (Benutzer) it.next();
          kombiniertePdfTemplateFactory.addMiniUeberschrift(benutzer.getNameFormal(), null, null);
          
          AbsatzPdfTemplateFactory bemerkungen = new AbsatzPdfTemplateFactory(benutzer.getBemerkungen());
          bemerkungen.setAbstandFactor(0.75f);
          kombiniertePdfTemplateFactory.addPdfTemplateFactory(bemerkungen);
        }
        
        result.addPdfTemplateFactory(new SpaltenPdfTemplateFactory(kombiniertePdfTemplateFactory, 2));
      }
    }
    
    return result;
  }
}
