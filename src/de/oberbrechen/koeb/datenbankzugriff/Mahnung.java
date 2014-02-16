package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;

/**
 * Diese Klasse repräsentiert eine Mahnung, d.h. eine Liste von Ausleihen eines
 * Benutzers zu einem bestimmten Zeitpunkt. Es handelt sich damit um kein
 * eigentliches Datenbankzugriff-Objekt sondern um eine Zusammenfassung
 * mehrerer solcher.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface Mahnung {
  
  /**
   * Liefert der Anzahl der Ausleihen, die in dieser Mahnung enthalten sind
   * @return die Anzahl der Ausleihen, die in dieser Mahnung enthalten sind
   * @throws DatenbankInkonsistenzException
   */
  public int getAnzahlAusleihen() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine aller Liste der Ausleihen, die in dieser Mahnung enthalten sind
   * @return eine aller Liste der Ausleihen, die in dieser Mahnung enthalten sind
   * @throws DatenbankInkonsistenzException
   */
  public AusleihenListe getAusleihenListe() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine Liste der überzogenen Ausleihen, die in dieser Mahnung enthalten sind
   * @return eine Liste der überzogenen Ausleihen, die in dieser Mahnung enthalten sind
   * @throws DatenbankInkonsistenzException
   */
  public AusleihenListe getGemahnteAusleihenListe() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine Liste der nicht überzogenen Ausleihen, die in dieser Mahnung enthalten sind
   * @return eine Liste der nicht überzogenen Ausleihen, die in dieser Mahnung enthalten sind
   * @throws DatenbankInkonsistenzException
   */
  public AusleihenListe getNichtGemahnteAusleihenListe() throws DatenbankInkonsistenzException;

  /**
   * Liefert der Anzahl der Ausleihen, die gemahnt werden
   * @return die Anzahl der Ausleihen, die gemahnt werden
   * @throws DatenbankInkonsistenzException
   */
  public int getAnzahlGemahnteAusleihen() throws DatenbankInkonsistenzException;

  /**
   * Liefert der Anzahl der Ausleihen, die nicht gemahnt werden
   * @return die Anzahl der Ausleihen, die nicht gemahnt werden
   * @throws DatenbankInkonsistenzException
   */
  public int getAnzahlNichtGemahnteAusleihen() throws DatenbankInkonsistenzException;

  /**
   * Liefert den Benutzer, dessen Ausleihen angemahnt werden
   * @return den Benutzer, dessen Ausleihen angemahnt werden
   */
  public Benutzer getBenutzer();
  
  /**
   * Liefert die Mahngebühren für die überzogenen Ausleihen, zu dem Zeitpunkt,
   * an dem die Mahnung erstellt wurde. Dieser Zeitpunkt kann mittels
   * getZeitpunkt() abgefragt werden.
   * für den Zeitpunkt nicht
   *
   * @return die Mahngebühren
   * @throws DatenbankInkonsistenzException
   */
  public double getMahngebuehren() throws DatenbankInkonsistenzException;

  /**
   * Liefert die längste Überziehung eines Mediums in Tagen
   * @return die längste Überziehung eines Mediums in Tagen
   * @throws DatenbankInkonsistenzException
   */
  public int getMaxUeberzogeneTage() throws DatenbankInkonsistenzException;
}