package de.oberbrechen.koeb.ausgaben;

import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;

/**
* Dieses Interface stellt eine Factory da, die
* eine Ausgabe erstellt, die eine Teilnehmerliste ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface TeilnehmerlisteAusgabenFactory {

  /**
   * Erstellt eine Ausleihe, die eine
   * Teilnehmerliste für eine Veranstaltung ausgibt.
   * @param veranstaltung die Veranstaltung, deren Teilnehmerliste ausgegeben
   *   werden soll
   * @param sortierung die Sortierung der Teilnehmerliste
   * @param zeigeBemerkungen bestimmt, ob die Bemerkungen zu den Veranstaltungen
   *  angezeigt werden sollen
   * @param zeigeBeschreibung bestimmt, ob die Beschreibung der Veranstaltung
   *  angezeigt werden soll
   * @return die erzeugte Ausleihe
   */
  public Ausgabe createTeilnehmerlisteVeranstaltungAusgabe(
    Veranstaltung veranstaltung, int sortierung, boolean zeigeBemerkungen, 
    boolean zeigeBeschreibung);

  /**
   * Erstellt eine Ausleihe, die eine
   * Teilnehmerliste für eine Veranstaltungsgruppe ausgibt.
   * @param veranstaltungsgruppe die Veranstaltungsgruppe, 
   *   deren Teilnehmerliste ausgegeben werden soll
   * @param zeigeBemerkungen bestimmt, ob die Bemerkungen zu den Veranstaltungen
   *  angezeigt werden sollen
   * @param zeigeBeschreibung bestimmt, ob die Beschreibung der Veranstaltung
   *  angezeigt werden soll
   * @return die erzeugte Ausleihe
   */
  public Ausgabe createTeilnehmerlisteVeranstaltungsgruppeAusgabe(
    Veranstaltungsgruppe veranstaltungsgruppe, boolean zeigeBemerkungen);
}