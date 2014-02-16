package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Diese Klasse implementiert einige Funktionen des AusleiheFactory-Interfaces
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractAusleiheFactory 
  extends AbstractDatenbankzugriffFactory<Ausleihe> implements AusleiheFactory {
    
  public int getAnzahlGetaetigteAusleihzeitraeumeInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException {
    return getGetaetigteAusleihzeitraeumeInZeitraum(zeitraum).size();
  }
  
  public AusleihenListe getAlleAktuellenAusleihenVon(Benutzer benutzer) throws DatenbankInkonsistenzException {
    return getAlleAktuellenAusleihenVon(benutzer, new Date());
  }

  public AusleihzeitraumListe getAlleAusleihzeitraeume() {
    AusleihzeitraumListe liste = new AusleihzeitraumListe();

    for (Ausleihe ausleihe : getAlleAusleihen()) {
      liste.addAllNoDuplicate(ausleihe.getAusleihzeitraeume());
    }
    
    return liste;
  }
}