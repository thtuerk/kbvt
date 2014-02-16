package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;


/**
 * Dieses Interface repräsentiert eine Factory für Veranstaltungen..
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface VeranstaltungFactory extends DatenbankzugriffFactory<Veranstaltung> {

  /**
   * Diese Methode liefert eine Liste mit Terminen, die häufig benutze 
   * Ührzeiten für Termine darstellen.
   * @return die Liste
   */
  public TerminListe getUeblicheUhrzeiten();

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Veranstaltung-Objekt
   * 
   * @return das neue Veranstaltung-Objekt
   */
  public Veranstaltung erstelleNeu(); 

  /**
   * Liefert eine unsortierte Liste aller
   * Veranstaltungen der Gruppe, die in der Datenbank eingetragen sind.
   * @param veranstaltungsgruppe die Veranstaltungsgruppe, deren 
   *   Veranstaltungen geladen werden sollen
   *
   * @return eine Liste aller Veranstaltungen der Gruppe
   */
  public VeranstaltungenListe getVeranstaltungen(
    Veranstaltungsgruppe veranstaltungsgruppe);

  /**
   * Liefert unsortierte Liste aller
   * Veranstaltungen der Gruppe, für die eine Anmeldung erforderlich ist.
   * @param veranstaltungsgruppe die Veranstaltungsgruppe, deren 
   *   Veranstaltungen geladen werden sollen
   *
   * @return eine unsortierte Liste der Veranstaltungen der Gruppe
   */
  public VeranstaltungenListe getVeranstaltungenMitAnmeldung(
    Veranstaltungsgruppe veranstaltungsgruppe);

  /**
   * Liefert eine unsortierte Liste aller Veranstaltungen, von denen
   * mindestens ein Termin im übergebenen
   * Zeitraum liegt 
   * @param zeitraum der Zeitraum
   * @return die unsortierte Liste
   */
  public VeranstaltungenListe getAlleVeranstaltungenInZeitraum(Zeitraum zeitraum);
}