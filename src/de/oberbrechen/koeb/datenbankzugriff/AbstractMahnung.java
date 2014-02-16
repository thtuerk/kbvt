package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.einstellungen.Ausleihordnung;

/**
 * Diese Klasse stellt die grundlegende Functionalität einer Mahnung
 * zur Verfügung.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AbstractMahnung implements Mahnung {
  
  protected AusleihenListe ausleihen;
  protected AusleihenListe gemahnteAusleihen;
  protected Benutzer benutzer;

  public AbstractMahnung(Benutzer benutzer) {
    this.benutzer = benutzer;
    this.ausleihen = null;
    this.gemahnteAusleihen = null;
  }
  
  public AusleihenListe getGemahnteAusleihenListe() throws DatenbankInkonsistenzException {
    if (gemahnteAusleihen != null) return gemahnteAusleihen;
    
    this.gemahnteAusleihen = new AusleihenListe();
    
    for (Ausleihe aktuelleAusleihe : getAusleihenListe()) {
      if (aktuelleAusleihe.istUeberzogen()) {
        gemahnteAusleihen.add(aktuelleAusleihe);
      }
    }    
    return gemahnteAusleihen;
  }
  
  public AusleihenListe getAusleihenListe() throws DatenbankInkonsistenzException {
    if (this.ausleihen != null) return ausleihen;
    
    this.ausleihen = Datenbank.getInstance().getAusleiheFactory().
      getAlleNichtZurueckgegebenenAusleihenVon(benutzer);
    return ausleihen;
  }

  public int getAnzahlGemahnteAusleihen() throws DatenbankInkonsistenzException {
    return getGemahnteAusleihenListe().size();
  }

  public Benutzer getBenutzer() {
    return benutzer;
  }

  public double getMahngebuehren() throws DatenbankInkonsistenzException {
    return Ausleihordnung.getInstance().berechneMahngebuehren(
        getAusleihenListe());
  }

  public int getMaxUeberzogeneTage() throws DatenbankInkonsistenzException {
    int max = 0;

    for (Ausleihe aktuelleAusleihe : getGemahnteAusleihenListe()) {
      int aktuelleUeberziehung = aktuelleAusleihe.getUeberzogeneTage();
      if (aktuelleUeberziehung > max)
        max = aktuelleUeberziehung;
    }

    return max;
  }

  public int getAnzahlAusleihen() throws DatenbankInkonsistenzException {
    return getAusleihenListe().size();
  }

  public AusleihenListe getNichtGemahnteAusleihenListe() throws DatenbankInkonsistenzException {
    AusleihenListe liste = new AusleihenListe();
    liste.addAll(getAusleihenListe());
    liste.removeAll(getGemahnteAusleihenListe());
    return liste;
  }

  public int getAnzahlNichtGemahnteAusleihen() throws DatenbankInkonsistenzException {
    return getNichtGemahnteAusleihenListe().size();
  }
}