package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Dieses Interface repräsentiert eine Factory für Ausleihen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface AusleiheFactory extends DatenbankzugriffFactory<Ausleihe> {
  
  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Ausleihe-Objekt
   * 
   * @return das neue Ausleihe-Objekt
   */
  public abstract Ausleihe erstelleNeu(); 

  /**
   * Liefert eine unsortierte Liste aller Ausleihen.
   *
   * @see AusleihenListe
   */
  public AusleihenListe getAlleAusleihen();

  /**
   * Liefert eine unsortierte Liste aller Ausleihzeiträume.
   *
   * @see AusleihenListe
   */
  public AusleihzeitraumListe getAlleAusleihzeitraeume();

  /**
   * Liefert eine unsortierte Liste aller Ausleihzeiträume, die im übergebenen
   * Zeitraum getätigt wurden.
   * @throws DatenbankInkonsistenzException
   */
  public AusleihzeitraumListe getGetaetigteAusleihzeitraeumeInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException;

  /**
   * Liefert die Anzahl aller Ausleihzeiträume, die im übergebenen
   * Monat für Ausleihen getätigt wurden.
   * @throws DatenbankInkonsistenzException
   */
  public int getAnzahlGetaetigteAusleihzeitraeumeInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller aktuellen Ausleihen des übergebenen
   * Benutzers.
   *
   * @param benutzer der Benutzer, dessen ausleihen geliefert werden sollen
   * @throws DatenbankInkonsistenzException
   * @see AusleihenListe
   */
  public AusleihenListe getAlleAktuellenAusleihenVon(Benutzer benutzer) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller zum übergebenen Zeitpunkt aktuellen
   * Ausleihen des übergebenen Benutzers, d.h. aller Ausleihen, 
   * die zu dem angegebenen Zeitpunkt zwar ausgeliehen aber noch
   * nicht zurückgegeben waren. Ausleihen, die am uebergebenen Datum
   * zurueckgegeben wurden, werden als ebenfalls zurueckgeliefert.
   *
   * @param benutzer der Benutzer, dessen ausleihen geliefert werden sollen
   * @param datum das Datum an dem die Ausleihen zwar getätigt, aber noch
   *   nicht zurückgegeben waren
   * @throws DatenbankInkonsistenzException
   * @see AusleihenListe
   */
  public AusleihenListe getAlleAktuellenAusleihenVon(
    Benutzer benutzer, Date datum) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller zum übergebenen Zeitpunkt nicht 
   * zurückgegebenen Ausleihen des übergebenen Benutzers, d.h. aller Ausleihen, 
   * die zu dem angegebenen Zeitpunkt zwar ausgeliehen aber noch
   * nicht zurückgegeben waren. Ausleihen, die am uebergebenen Datum
   * zurueckgegeben wurden, werden als ebenfalls zurueckgeliefert.
   *
   * @param benutzer der Benutzer, dessen ausleihen geliefert werden sollen
   * @param datum das Datum an dem die Ausleihen zwar getätigt, aber noch
   *   nicht zurückgegeben waren
   * @throws DatenbankInkonsistenzException
   * @see AusleihenListe
   */
  public AusleihenListe getAlleNichtZurueckgegebenenAusleihenVon(
      Benutzer benutzer, Date datum) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert eine unsortierte Liste aller zum aktuellen Zeitpunkt nicht 
   * zurückgegebenen Ausleihen des übergebenen Benutzers, d.h. aller Ausleihen, 
   * die zu dem angegebenen Zeitpunkt zwar ausgeliehen aber noch
   * nicht zurückgegeben waren. Ausleihen, die am uebergebenen Datum
   * zurueckgegeben wurden, werden als ebenfalls zurueckgeliefert.
   *
   * @param benutzer der Benutzer, dessen ausleihen geliefert werden sollen
   * @param datum das Datum an dem die Ausleihen zwar getätigt, aber noch
   *   nicht zurückgegeben waren
   * @throws DatenbankInkonsistenzException
   * @see AusleihenListe
   */
  public AusleihenListe getAlleNichtZurueckgegebenenAusleihenVon(
      Benutzer benutzer) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Ausleihen des Benutzers.
   *
   * @param benutzer der Benutzer, dessen Ausleihen geliefert werden sollen
   * @throws DatenbankInkonsistenzException
   * @see AusleihenListe
   */
  public AusleihenListe getAlleAusleihenVon(Benutzer benutzer) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Ausleihen des übergebenen
   * Mediums.
   *
   * @param medium das Medium, dessen Ausleihen geliefert werden sollen
   * @throws DatenbankInkonsistenzException
   * @see AusleihenListe
   */
  public AusleihenListe getAlleAusleihenVon(Medium medium) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert den Zeitraum, in dem Ausleihen 
   * getätigt wurden, d.h. der Beginn liegt beim Tätigungsdatum der ersten Ausleihe in der Datenbank,
   * das Ende beim Tätigungsdatum der letzten Ausleihe
   * Sollten noch keine Ausleihen getaetigt worden sein, wird der Beginn und das Ende auf
   * das aktuelle Datum gesetzt
   */
  public Zeitraum getAusleihenZeitraum();
  
  
}