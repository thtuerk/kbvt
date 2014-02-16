package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.*;


/**
 * Dieses Interface repräsentiert eine Factory für Benutzer.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface BenutzerFactory extends DatenbankzugriffFactory<Benutzer> {

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Benutzer-Objekt
   * 
   * @return das neue Benutzer-Objekt
   */
  public Benutzer erstelleNeu(); 

  /**
   * Liefert den Standardwohnwort für Benutzer. Dieser wird automatisch
   * gesetzt, wenn ein neuer Benutzer erstellt wird.
   * @return den Standardwohnwort für Benutzer
   */
  public String getStandardBenutzerwohnort();

  /**
   * Liefert eine unsortierte Liste aller aktiven Benutzer, die in der Datenbank
   * eingetragen sind.
   *
   * @see BenutzerListe
   */
  public BenutzerListe getAlleAktivenBenutzer();
  
  /**
   * Liefert eine unsortierte Liste aller passiven, d.h. nicht aktiven Benutzer, die in der Datenbank
   * eingetragen sind.
   *
   * @see BenutzerListe
   */
  public BenutzerListe getAllePassivenBenutzer();

  /**
   * Liefert eine unsortierte Liste aller Benutzer, die in der Datenbank
   * eingetragen sind.
   *
   * @see BenutzerListe
   */
  public BenutzerListe getAlleBenutzer();

  /**
   * Liefert eine alphabetisch sortierte Liste aller Orte, in denen Benutzer
   * wohnen. In der Liste sind die Orte als String-Objekte abgelegt.
   *
   * @return eine alphabetisch sortierte Liste aller Orte, in denen Benutzer
   * wohnen
   */
  public Liste<String> getAlleOrte();

  /**
   * Bestimmt die Benutzernr, die zu dem übergebenen Benutzernamen gehört.
   * @param benutzername der Benutzername
   * @throws DatenNichtGefundenException falls kein
   *   Mitarbeiter mit diesem Mitarbeiter-Benutzernamen existiert
   *
   * @return die zugehörige Benutzernr
   */
  public int sucheBenutzername(String benutzername) 
    throws DatenNichtGefundenException;

  /**
   * Liefert die aktiven Benutzer im übergebenen Zeitraum, d.h. die Benutzer,
   * die etwas ausgeliehen haben oder an einer Veranstaltung teilnahmen.
   * @param zeitraum
   * @throws DatenbankInkonsistenzException
   */
  public BenutzerListe getAktiveBenutzerInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert die Benutzer, die im übergebenen Zeitraum ein Medium ausgeliehen
   * haben.
   * @param zeitraum
   * @throws DatenbankInkonsistenzException
   */
  public BenutzerListe getAktiveLeserInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException;  
}