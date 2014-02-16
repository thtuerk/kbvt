package de.oberbrechen.koeb.pdf.pdfJahresStatistik;

import java.util.Date;
import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Termin;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungsteilnahmeFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell für die Benutzer und Veranstaltungsstatistiken eines Jahres
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BenutzerUndVeranstaltungenStatistikJahrTabellenModell extends TabellenModell {
  
  String[] namen;
  String[] datenString;
  int[] daten;

  public BenutzerUndVeranstaltungenStatistikJahrTabellenModell(int jahr) throws DatenbankInkonsistenzException {
    
    //Initialisierung
    Zeitraum zeitraum = new Zeitraum(jahr);
    BenutzerFactory benutzerFactory = Datenbank.getInstance().getBenutzerFactory();
    VeranstaltungFactory veranstaltungFactory = Datenbank.getInstance().getVeranstaltungFactory();
    VeranstaltungsteilnahmeFactory teilnahmeFactory = Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
    
  
    //Hole alle Daten
    BenutzerListe alleBenutzer = benutzerFactory.getAlleAktivenBenutzer(); 
    VeranstaltungenListe veranstaltungen = 
      veranstaltungFactory.getAlleVeranstaltungenInZeitraum(zeitraum);
    BenutzerListe teilnehmer = new BenutzerListe();
    teilnehmer.setSortierung(BenutzerListe.BenutzernrSortierung);
    int teilnehmerAnzahlTermine = 0;
    int teilnehmerAnzahlTermineJung = 0;
    int termineAnzahl = 0;
    
    for (int i=0; i < veranstaltungen.size(); i++) {
      Veranstaltung veranstaltung = (Veranstaltung) veranstaltungen.get(i);      
      BenutzerListe veranstaltungTeilnehmer = 
        teilnahmeFactory.getTeilnehmerListe(veranstaltung);      
      int veranstaltungTeilnehmerAnzahl = 
        veranstaltungTeilnehmer.size();
      int veranstaltungTeilnehmerAnzahlJung =
        getJungeBenutzerListe(veranstaltungTeilnehmer, zeitraum.getEnde()).size();
        
      Iterator<Termin> it = veranstaltung.getTermine().iterator();
      while (it.hasNext()) {
        Termin termin = it.next();
        if (zeitraum.liegtIn(termin.getBeginn())) {
          termineAnzahl++;
          teilnehmerAnzahlTermine += veranstaltungTeilnehmerAnzahl;
          teilnehmerAnzahlTermineJung += veranstaltungTeilnehmerAnzahlJung;
        }
        if (termineAnzahl > 0) {
          teilnehmer.addAll(veranstaltungTeilnehmer);
        }
      }
    }        
    
    BenutzerListe leser = benutzerFactory.getAktiveLeserInZeitraum(zeitraum);
    leser.setSortierung(BenutzerListe.BenutzernrSortierung);
    
    BenutzerListe aktiveBenutzer = new BenutzerListe();
    aktiveBenutzer.addAll(leser);
    aktiveBenutzer.addAll(teilnehmer);
        
    BenutzerListe doppeltAktiveBenutzer = new BenutzerListe();
    doppeltAktiveBenutzer.addAll(leser);
    doppeltAktiveBenutzer.retainAll(teilnehmer);
    

    BenutzerListe neueLeser = new BenutzerListe();
    neueLeser.addAll(leser);
    
    
    //Ausgabe zusammenbauen 
    int zeilenAnzahl=16;
    namen = new String[zeilenAnzahl];
    daten = new int[zeilenAnzahl];

    final String jungString = " - bis einschließlich 12 Jahren"; 
    
    namen[0] = "Benutzer"; 
    namen[1] = namen[0]+jungString;
    daten[0] = alleBenutzer.size();
    daten[1] = getJungeBenutzerListe(alleBenutzer, zeitraum.getEnde()).size();

    namen[2] = "Benutzer mit Ausleihen"; 
    namen[3] = namen[2]+jungString;
    daten[2] = leser.size();    
    daten[3] = getJungeBenutzerListe(leser, zeitraum.getEnde()).size();
        
    namen[4] = "Neue Benutzer mit Ausleihen"; 
    namen[5] = namen[4]+jungString;
    daten[4] = getNeueBenutzerListe(leser, zeitraum).size();    
    daten[5] = getJungeBenutzerListe(getNeueBenutzerListe(leser, zeitraum), zeitraum.getEnde()).size();

    namen[6] = "Teilnehmer an Veranstaltungen"; 
    namen[7] = namen[6]+jungString;
    daten[6] = teilnehmer.size();    
    daten[7] = getJungeBenutzerListe(teilnehmer, zeitraum.getEnde()).size();
    
    namen[8] = "aktive Benutzer"; 
    namen[9] = namen[8]+jungString;
    daten[8] = aktiveBenutzer.size();
    daten[9] = getJungeBenutzerListe(aktiveBenutzer, zeitraum.getEnde()).size();
    
    namen[10] = "doppelt aktive Benutzer"; 
    namen[11] = namen[10]+jungString;
    daten[10] = doppeltAktiveBenutzer.size();
    daten[11] = getJungeBenutzerListe(doppeltAktiveBenutzer, zeitraum.getEnde()).size();
    

    namen[12] = "Veranstaltungen"; 
    daten[12] = veranstaltungen.size();
    
    namen[13] = "Termine"; 
    daten[13] = termineAnzahl;
        
    namen[14] = "Teilnahmen an Terminen"; 
    namen[15] = namen[14]+jungString;
    daten[14] = teilnehmerAnzahlTermine;    
    daten[15] = teilnehmerAnzahlTermineJung;
    
    setSpaltenAusrichtung(2, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
  }

  private BenutzerListe getJungeBenutzerListe(BenutzerListe liste, Date zeitpunkt) {
    BenutzerListe benutzerListe = new BenutzerListe();
    
    for (int i=0; i < liste.size(); i++) {
      Benutzer benutzer = (Benutzer) liste.get(i);
      if (benutzer.getGeburtsdatum() != null && 
          benutzer.getAlter(zeitpunkt) < 13) benutzerListe.add(benutzer);
    }
    
    return benutzerListe;
  }
  
  private BenutzerListe getNeueBenutzerListe(BenutzerListe liste, Zeitraum zeitraum) {
    BenutzerListe benutzerListe = new BenutzerListe();
    
    for (int i=0; i < liste.size(); i++) {
      Benutzer benutzer = (Benutzer) liste.get(i);
      if (zeitraum.liegtIn(benutzer.getAnmeldedatum())) {
        benutzerListe.add(benutzer);
      }
    }
    
    return benutzerListe;
  }

  public int getSpaltenAnzahl() {
    return 2;
  }

  public int getZeilenAnzahl() {
    return namen.length;
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr < 3) return ""; 
    return "nicht definierte Spalte"; 
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (spaltenNr == 1) return namen[zeilenNr-1];
    if (spaltenNr == 2) return (daten[zeilenNr-1] == 0?"-":Integer.toString(daten[zeilenNr-1])); 
    return "!!! FEHLER !!!"; 
  } 
}
