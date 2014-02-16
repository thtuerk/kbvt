package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

class PdfTeilnehmerListeVeranstaltungsgruppeTabellenModellZusammenfassung 
  extends TabellenModell {

  private VeranstaltungenListe veranstaltungen;
  private VeranstaltungsteilnahmeFactory teilnahmeFactory;
  
  public PdfTeilnehmerListeVeranstaltungsgruppeTabellenModellZusammenfassung(
    Veranstaltungsgruppe veranstaltungsgruppe) throws DatenbankzugriffException{

    teilnahmeFactory = Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
        
    VeranstaltungFactory veranstaltungFactory =
      Datenbank.getInstance().getVeranstaltungFactory();
    veranstaltungen = veranstaltungFactory.getVeranstaltungenMitAnmeldung(
        veranstaltungsgruppe);        
    veranstaltungen.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);
        
    //Spaltenmodell bauen
    setSpaltenAusrichtung(1, TabellenModell.SPALTEN_AUSRICHTUNG_LINKS);
    setSpaltenAusrichtung(2, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);    
    setSpaltenAbstand(5);
  }

  public int getSpaltenAnzahl() {
    return 2;
  }

  public int getZeilenAnzahl() {
    return veranstaltungen.size();
  }

  public String getSpaltenName(int spaltenNr) {
    switch (spaltenNr) {
      case 1: return "Veranstaltung";
      case 2: return "Teilnehmeranzahl";
    }
    return "FEHLER!!!";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    Veranstaltung veranstaltung = (Veranstaltung) 
      veranstaltungen.get(zeilenNr - 1);

    switch (spaltenNr) {
      case 1: return veranstaltung.getTitel();
      case 2: 
        int teilnehmerAnzahl = teilnahmeFactory.getTeilnehmerAnzahl(veranstaltung);
        return teilnehmerAnzahl==0?"-":Integer.toString(teilnehmerAnzahl);
    }

    return "Fehler!";
  }
}

