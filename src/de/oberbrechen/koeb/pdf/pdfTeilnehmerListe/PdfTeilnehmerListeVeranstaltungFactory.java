package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungsteilnahmeFactory;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsteilnahmeListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelleDokumentFactory;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;

public class PdfTeilnehmerListeVeranstaltungFactory extends PdfTabelleDokumentFactory {

  boolean zeigeBemerkungen;
  boolean zeigeBeschreibung;
  Veranstaltung veranstaltung;
  
  public PdfTeilnehmerListeVeranstaltungFactory(Veranstaltung veranstaltung) {
    this.veranstaltung = veranstaltung;
    titel = veranstaltung.getTitel();
    zeigeBemerkungen = true;
    zeigeBeschreibung = true;
  }
 
  public void setZeigeBemerkungen(boolean bemerkungen) {
    this.zeigeBemerkungen = bemerkungen;
  }
  
  public void setZeigeBeschreibung(boolean beschreibung) {
    this.zeigeBeschreibung = beschreibung;
  }
  
  protected int getStandardSortierung() {
    return VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung;
  }

  public PdfDokument createPdfDokument() throws Exception {
    //Initialisierungen
    VeranstaltungsteilnahmeFactory veranstaltungsteilnahmeFactory =
      Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
    final VeranstaltungsteilnahmeListe teilnahmeListe =
      veranstaltungsteilnahmeFactory.getTeilnahmeListe(veranstaltung);
    teilnahmeListe.setSortierung(sortierung, umgekehrteSortierung);      

    //Modell bauen
    TabellenModell modell = new PdfTeilnehmerListeVeranstaltungTabellenModell(
        teilnahmeListe, sortierung);
    modell.setZeigeZeilenHintergrund(zeigeZeilenHintergrund);

    if (zeigeSortierteSpalteHintergrund) {
      switch (sortierung) {
        case VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung:
          modell.setZeigeSpaltenHintergrund(5, true);
          break;
        case VeranstaltungsteilnahmeListe.BenutzerKlasseSortierung:
          modell.setZeigeSpaltenHintergrund(4, true);
          break;
        case VeranstaltungsteilnahmeListe.BemerkungenSortierung:
          modell.setZeigeSpaltenHintergrund(6, true);
          break;
        case VeranstaltungsteilnahmeListe.BenutzerVornameNachnameSortierung:
        case VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung:
          modell.setZeigeSpaltenHintergrund(1, true);
          break;
      }
    }

    
    //Überschrift bauen
    int teilnehmerAnzahlInt = veranstaltungsteilnahmeFactory.getTeilnehmerAnzahl(
        veranstaltung);
    int wartelistenInt = veranstaltungsteilnahmeFactory.getWartelistenLaenge(
        veranstaltung);
    
    String teilnehmerAnzahl;
    if (teilnehmerAnzahlInt == 0 && wartelistenInt == 0) {
      teilnehmerAnzahl = "keine Teilnehmer";
    } else if (wartelistenInt == 0) {
      teilnehmerAnzahl = teilnehmerAnzahlInt+" Teilnehmer";
    } else {
      teilnehmerAnzahl = teilnehmerAnzahlInt+" (+"+wartelistenInt+") Teilnehmer";
    }
    
    
    
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(titel);
    result.addHauptUeberschrift(titel, null, teilnehmerAnzahl);
    
    VeranstaltungPdfTemplateFactory veranstaltungPdfTemplateFactory =
      new VeranstaltungPdfTemplateFactory(veranstaltung, false, zeigeBeschreibung, zeigeBemerkungen);
    result.addPdfTemplateFactory(veranstaltungPdfTemplateFactory);
    
    PdfTabelle tabelle = new PdfTabelle(modell);
    result.addPdfTemplateFactory(tabelle);
    
    return result;
  }
}
