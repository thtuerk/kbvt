package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

public class PdfTeilnehmerUebersichtVeranstaltungsgruppeTabellenModell 
  extends TabellenModell {

  String[] klassen;
  String[][] daten;
  
  public PdfTeilnehmerUebersichtVeranstaltungsgruppeTabellenModell(
    Veranstaltungsgruppe veranstaltungsgruppe) {
    super(3, false);

    VeranstaltungsteilnahmeFactory teilnahmeFactory =
      Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
    BenutzerListe teilnehmerListe = 
      teilnahmeFactory.getTeilnehmerListe(veranstaltungsgruppe);
    teilnehmerListe.setSortierung(BenutzerListe.NachnameVornameSortierung);
    
    Liste<String> klassenListe = new Liste<String>();
    for (Benutzer benutzer : teilnehmerListe) {
      String klasse = benutzer.getKlasse();
      if (klasse != null) klassenListe.add(klasse);
    }
    klassenListe.setSortierung(Liste.StringSortierung, false);

    klassen = klassenListe.toArray(new String[klassenListe.size()]);

    //Daten Initialisieren
    VeranstaltungenListe veranstaltungenListe = 
      Datenbank.getInstance().getVeranstaltungFactory().
        getVeranstaltungenMitAnmeldung(veranstaltungsgruppe); 
      
    veranstaltungenListe.setSortierung(VeranstaltungenListe.AlphabetischeSortierung, false);
    daten = new String[veranstaltungenListe.size()][klassen.length+3];
        
    for (int vNr = 0; vNr < veranstaltungenListe.size(); vNr++) {
      Veranstaltung veranstaltung = (Veranstaltung) veranstaltungenListe.get(vNr);
      daten[vNr][0] = veranstaltung.getTitel();

      int teilnehmerAnzahl = teilnahmeFactory.getTeilnehmerAnzahl(veranstaltung);
      if (teilnehmerAnzahl == 0) {
        daten[vNr][1] = "-";
      } else {
        daten[vNr][1] = Integer.toString(teilnehmerAnzahl);
      }
      
      if (veranstaltung.getMaximaleTeilnehmerAnzahl() > 0) {
        daten[vNr][1] += " / ";
        daten[vNr][1] += Integer.toString(veranstaltung.getMaximaleTeilnehmerAnzahl());            
      }
      
      int[] klassenAnzahl = new int[klassen.length+1];      
      int[] klassenAnzahlWarteListe = new int[klassen.length+1];      
      VeranstaltungsteilnahmeListe teilnahmenListe = 
        teilnahmeFactory.getTeilnahmeListe(veranstaltung);
      teilnahmenListe.setSortierung(
          VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung);
      Iterator<Veranstaltungsteilnahme> teilnahmenIt = teilnahmenListe.iterator();
      while (teilnahmenIt.hasNext()) {
        Veranstaltungsteilnahme currentTeilnahme =
          teilnahmenIt.next();
        String currentKlasse = currentTeilnahme.getBenutzer().getKlasse();
        int pos = 0;
        boolean gefunden = false;
        while (currentKlasse != null && !gefunden && pos < klassen.length) {
          if (currentKlasse.equals(klassen[pos])) {
            gefunden = true;
            if (currentTeilnahme.istAufWarteListe()) {
              klassenAnzahlWarteListe[pos]++;
            } else {
              klassenAnzahl[pos]++;              
            }
          }
          pos++;
        }
        if (!gefunden) {
          if (currentTeilnahme.istAufWarteListe()) {
            klassenAnzahlWarteListe[klassen.length]++;
          } else {
            klassenAnzahl[klassen.length]++;
          }
        }
      }

      for (int pos = 0; pos < klassenAnzahl.length; pos++) {
        if (klassenAnzahlWarteListe[pos] == 0) {
          if (klassenAnzahl[pos] == 0) {
            daten[vNr][2+pos] = "-";
          } else {
            daten[vNr][2+pos] = Integer.toString(klassenAnzahl[pos]);
          }
        } else {
          if (klassenAnzahl[pos] == 0) {
            daten[vNr][2+pos] = "0 (+"+Integer.toString(klassenAnzahlWarteListe[pos])+")";
          } else {
            daten[vNr][2+pos] = Integer.toString(klassenAnzahl[pos])+" (+"+
              Integer.toString(klassenAnzahlWarteListe[pos])+")";
          }          
        }
      }

    }
        
    //Spaltenmodell bauen
    init(getSpaltenAnzahl());
    for (int i=2;i<=getSpaltenAnzahl(); i++) {
      setSpaltenAusrichtung(i, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);      
    }
    setZeigeSpaltenHintergrund(2, true);
  }

  public int getSpaltenAnzahl() {
    if (daten.length > 0)
      return daten[0].length;
    
    return 2;
  }

  public int getZeilenAnzahl() {
    return daten.length;
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "Veranstaltung";
    if (spaltenNr == 2) return "Anzahl";
    if (spaltenNr > 2 && spaltenNr < 3+klassen.length) {
      return klassen[spaltenNr-3];
    }
    return "-";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    return daten[zeilenNr-1][spaltenNr-1];
  }
}

