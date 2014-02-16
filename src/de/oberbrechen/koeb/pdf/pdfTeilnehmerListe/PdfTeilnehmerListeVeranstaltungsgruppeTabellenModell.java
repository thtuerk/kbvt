package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.pdfTabelle.*;

import java.util.Date;
import java.text.DecimalFormat;

class PdfTeilnehmerListeVeranstaltungsgruppeTabellenModell 
  extends TabellenModell {

  private Benutzer[] daten;
  private Veranstaltung[] veranstaltungen;
  private int[][] teilnahmenArray;
  private boolean zeigeBemerkungen;
  private DecimalFormat alterFormat = new DecimalFormat("0.0");

  public PdfTeilnehmerListeVeranstaltungsgruppeTabellenModell(
    Veranstaltungsgruppe veranstaltungsgruppe, boolean zeigeBemerkungen) throws DatenbankzugriffException{
    super(10, false);
    this.zeigeBemerkungen = zeigeBemerkungen;

    VeranstaltungsteilnahmeFactory veranstaltungsteilnahmeFactory =
      Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
    BenutzerListe teilnehmerListe =
      veranstaltungsteilnahmeFactory.getTeilnehmerListe(
      veranstaltungsgruppe);
    teilnehmerListe.setSortierung(BenutzerListe.NachnameVornameSortierung);
    this.daten = new Benutzer[teilnehmerListe.size()];
    teilnehmerListe.toArray(this.daten);

    VeranstaltungFactory veranstaltungFactory =
      Datenbank.getInstance().getVeranstaltungFactory();
    VeranstaltungenListe veranstaltungenListe =
      veranstaltungFactory.getVeranstaltungenMitAnmeldung(veranstaltungsgruppe);
    veranstaltungenListe.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);
    this.veranstaltungen = new Veranstaltung[veranstaltungenListe.size()];
    veranstaltungenListe.toArray(this.veranstaltungen);
    
    teilnahmenArray = Datenbank.getInstance().
      getVeranstaltungsteilnahmeFactory().
      getTeilnahmeArray(veranstaltungenListe, teilnehmerListe);
    

    //Spaltenmodell bauen
    init(getSpaltenAnzahl());
    setSpaltenAusrichtung(3, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    for (int i=5; i <= getSpaltenAnzahl(); i++) {
      setSpaltenAusrichtung(i,
      TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL);
      setFesteBreite(i, PdfDokumentEinstellungen.getInstance().getSchriftgroesseNormal());
      setZeigeSpaltenHintergrund(i, (i - 5) % 2 == 0);
    }
    setSpaltenAbstand(0);
    setSpaltenAbstand(1, 5);
    setSpaltenAbstand(3, 5);

    if (getSpaltenAnzahl() > 4) {
      setSpaltenAbstand(4, 10);
      setSpaltenAbstandHintergrund(4, 1);
    }
  }

  public int getSpaltenAnzahl() {
    return 4+veranstaltungen.length;
  }

  public int getZeilenAnzahl() {
    return daten.length;
  }

  public String getSpaltenName(int spaltenNr) {
    switch (spaltenNr) {
      case 1: return "Name";
      case 2: return "Tel.";
      case 3: return null;
      case 4: return "Alter";
    }
    return ((Veranstaltung) veranstaltungen[spaltenNr-5]).getTitel();
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    Benutzer benutzer = (Benutzer) daten[zeilenNr - 1];

    switch (spaltenNr) {
      case 1: return benutzer.getNameFormal();
      case 2: return benutzer.getTel();
      case 3:
        boolean eMailGesetzt = (benutzer.getEMail() != null);
        boolean faxGesetzt = (benutzer.getEMail() != null);
        if (!eMailGesetzt && !faxGesetzt) return null;
        String eintrag = "(";
        if (eMailGesetzt) eintrag += "E";
        if (eMailGesetzt) eintrag += "F";
        return eintrag+")";
      case 4:
        if (benutzer.getGeburtsdatum() != null) {
          double alter = benutzer.getAlter(new Date());
          return alterFormat.format(alter)+" J.";
        } else {
          return null;
        }
    }

    switch (teilnahmenArray[spaltenNr-5][zeilenNr-1]) {
      case VeranstaltungsteilnahmeFactory.TEILNAHME_KEINE:
        return null;
      case VeranstaltungsteilnahmeFactory.TEILNAHME_TEILNEHMERLISTE:
        return "X";
      case VeranstaltungsteilnahmeFactory.TEILNAHME_WARTELISTE:
        return "?";
    }

    return "Fehler!";
  }

  public int getZellenSchrift(int modellZeile, int seitenZeile, int spalte) {
    if (!zeigeBemerkungen) return TabellenModell.SCHRIFT_NORMAL;
    
    Benutzer benutzer = (Benutzer) daten[modellZeile - 1];
    if (benutzer.getBemerkungen() == null ||
        benutzer.getBemerkungen().length() == 0) {
      return TabellenModell.SCHRIFT_NORMAL;
    } else {     
      return TabellenModell.SCHRIFT_FETT;
    }
  }

}

