package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Dieses Klasse stellt einige grundlegende
 * Funktionen einer VeranstaltungsteilnahmeFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractVeranstaltungsteilnahmeFactory extends 
  AbstractDatenbankzugriffFactory<Veranstaltungsteilnahme> implements VeranstaltungsteilnahmeFactory {

  public boolean istAngemeldet(
    Benutzer benutzer, Veranstaltung veranstaltung) throws DatenbankInkonsistenzException {
    return getVeranstaltungsteilnahme(benutzer, veranstaltung) != null;
  }

  public int[][] getTeilnahmeArray(VeranstaltungenListe veranstaltungen,
    BenutzerListe benutzer) throws DatenbankInkonsistenzException {

    int[][] erg = new int[veranstaltungen.size()][benutzer.size()];

    for (int i = 0; i < veranstaltungen.size(); i++) {
      for (int j = 0; j < benutzer.size(); j++) {
        Veranstaltungsteilnahme teilnahme = getVeranstaltungsteilnahme(
            (Benutzer) benutzer.get(j), 
            (Veranstaltung) veranstaltungen.get(i));
        
        if (teilnahme == null) {
          erg[i][j] = TEILNAHME_KEINE;
        } else {
          erg[i][j] = teilnahme.istAufWarteListe()?TEILNAHME_WARTELISTE:TEILNAHME_TEILNEHMERLISTE;
        }
      }      
    }
    return erg;
  }
  
  public boolean istBelegt(Veranstaltung veranstaltung) {
    if (veranstaltung.getMaximaleTeilnehmerAnzahl() == 0) return false;
    
    return getTeilnehmerAnzahl(veranstaltung) >= veranstaltung.getMaximaleTeilnehmerAnzahl();
  }  

  public BenutzerListe getTeilnehmerListeInZeitraum(Zeitraum zeitraum) {
    VeranstaltungFactory veranstaltungFactory = Datenbank.getInstance().getVeranstaltungFactory();
    VeranstaltungenListe veranstaltungen = 
      veranstaltungFactory.getAlleVeranstaltungenInZeitraum(zeitraum);
    BenutzerListe teilnehmer = new BenutzerListe();
    teilnehmer.setSortierung(BenutzerListe.BenutzernrSortierung);
    
    for (int i=0; i < veranstaltungen.size(); i++) {
      Veranstaltung veranstaltung = (Veranstaltung) veranstaltungen.get(i);      
      BenutzerListe veranstaltungTeilnehmer = 
        getTeilnehmerListe(veranstaltung);      
        
      teilnehmer.addAll(veranstaltungTeilnehmer);
    }
    
    return teilnehmer;
  }
}