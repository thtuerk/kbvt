package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.pdf.pdfTabelle.*;

class PdfTeilnehmerListeVeranstaltungTabellenModell extends TabellenModell {
    private VeranstaltungsteilnahmeListe daten;
    private int sortierung;

    public PdfTeilnehmerListeVeranstaltungTabellenModell(
      VeranstaltungsteilnahmeListe daten, int sortierung) {
      this.daten = daten;
      this.sortierung = sortierung;
      
      //Spaltenmodell bauen
      setSpaltenAusrichtung(3, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);      
      setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);      
      setSpaltenAusrichtung(5, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);      
    }


    public int getSpaltenAnzahl() {
      return 6;
    }

    public int getZeilenAnzahl() {
      return daten.size();
    }

    public String getSpaltenName(int spaltenNr) {
      switch (spaltenNr) {
        case 1: return "Name";
        case 2: return "Tel.";
        case 3: return null;
        case 4: return "Kl.";
        case 5: return "Nr.";
        case 6: return "Bemerkungen";
      }
      return null;
    }

    public String getEintrag(int spaltenNr, int zeilenNr) {
      Veranstaltungsteilnahme teilnahme =
        (Veranstaltungsteilnahme) daten.get(zeilenNr - 1);

      switch (spaltenNr) {
        case 1:
          if (sortierung ==
              VeranstaltungsteilnahmeListe.BenutzerVornameNachnameSortierung){
            return teilnahme.getBenutzer().getName();
          } else {
            return teilnahme.getBenutzer().getNameFormal();
          }
        case 2: return teilnahme.getBenutzer().getTel();
        case 3:
          boolean eMailGesetzt = (teilnahme.getBenutzer().getEMail() != null);
          boolean faxGesetzt = (teilnahme.getBenutzer().getFax() != null);
          if (!eMailGesetzt && !faxGesetzt) return null;
          String eintrag = "(";
          if (eMailGesetzt) eintrag += "E";
          if (faxGesetzt) eintrag += "F";
          return eintrag+")";
        case 4: return teilnahme.getBenutzer().getKlasse();
        case 5: return Integer.toString(teilnahme.getAnmeldeNr());
        case 6: return teilnahme.getBemerkungen();
      }
      return null;
    }

    
  public float getZellenRandOben(int modellZeile, int seitenZeile, int spalte) {
    if (modellZeile == 1 || seitenZeile == 1) return 0;
    
    Veranstaltungsteilnahme teilnahme =
      (Veranstaltungsteilnahme) daten.get(modellZeile - 1);
    Veranstaltungsteilnahme vorherigeTeilnahme =
      (Veranstaltungsteilnahme) daten.get(modellZeile - 2);
    boolean zeigeRand = (!teilnahme.istAufWarteListe() && vorherigeTeilnahme.istAufWarteListe()) || 
      (teilnahme.istAufWarteListe() && !vorherigeTeilnahme.istAufWarteListe()); 
    
    return zeigeRand?0.5f:0;
  }

  public int getZellenSchrift(int modellZeile, int seitenZeile,
    int spalte) {

    Veranstaltungsteilnahme teilnahme =
      (Veranstaltungsteilnahme) daten.get(modellZeile - 1);
    return teilnahme.istAufWarteListe()?SCHRIFT_KURSIV:SCHRIFT_NORMAL;
  }

  public float getZellenHintergrund(int modellZeile, int seitenZeile,
    int spalte) {

    Veranstaltungsteilnahme teilnahme =
      (Veranstaltungsteilnahme) daten.get(modellZeile - 1);
    
    if (!teilnahme.istAufWarteListe()) {    
      return super.getZellenHintergrund(modellZeile, seitenZeile, spalte);
    } else {
      return 1f;      
    }
  }

}
