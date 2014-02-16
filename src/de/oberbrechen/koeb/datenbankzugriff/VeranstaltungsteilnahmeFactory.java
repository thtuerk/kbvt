package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsteilnahmeListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;


/**
 * Dieses Interface repräsentiert eine Factory für Veranstaltungsteilnahmen..
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface VeranstaltungsteilnahmeFactory extends DatenbankzugriffFactory<Veranstaltungsteilnahme>{

  public static final int TEILNAHME_KEINE = 0;
  public static final int TEILNAHME_WARTELISTE = 1;
  public static final int TEILNAHME_TEILNEHMERLISTE = 2;
  
  /**
   * Erstellt ein neues Veranstaltungsteilnahme-Objekt für die übergebene 
   * Veranstaltung und den übergebenen Benutzer.
   */
  public Veranstaltungsteilnahme erstelleNeu(Benutzer benutzer, 
    Veranstaltung veranstaltung);
    
  /**
   * Bestimmt, ob der übergebene Benutzer für der übergebene Veranstaltung
   * angemeldet ist.
   *
   * @param benutzer der zu überprüfende Benutzer
   * @param veranstaltung die zu überprüfende Veranstaltung
   * @return <code>true</code> gdw der übergebene Benutzer für der übergebene Veranstaltung
   * angemeldet ist
   * @throws DatenbankInkonsistenzException
   */
  public boolean istAngemeldet(Benutzer benutzer, Veranstaltung veranstaltung) throws DatenbankInkonsistenzException;


  /**
   * Bestimmt, ob die übergebene Veranstaltung belegt ist, d.h. ob es eine
   * maximale Teilnehmeranzahl für diese Veranstaltung gibt und bereits so
   * viele Teilnehmer angemeldet sind.
   */
  public boolean istBelegt(Veranstaltung veranstaltung);
  
  /**
   * Liefert ein Array, in dem eingetragen ist, ob die übergebenen Benutzer
   * an den übergebenen Veranstaltungen teilnehmen. Der erste Array-Index
   * gibt die Veranstaltung, der zweite den Benutzer an. Die Werte in diesem
   * Array entsprechen den öffentlichen Konstanten in diesem Interface 
   *
   * @param veranstaltungen die zu prüfenden Veranstaltungen
   * @param benutzer die zu prüfenden Benutzer
   * @return das Array
   * @throws DatenbankInkonsistenzException
   */
  public int[][] getTeilnahmeArray(VeranstaltungenListe veranstaltungen,
    BenutzerListe benutzer) throws DatenbankInkonsistenzException;
    
  /**
   * Sucht die Veranstaltungsteilnahme, die den übergebenen Benutzers für die
   * übergebene Veranstaltung anmeldet.
   *
   * @param benutzer der zu überprüfende Benutzer
   * @param veranstaltung die zu überprüfende Veranstaltung
   * @return die Veranstaltungsteilnahme, die den übergebenen Benutzers für die
   * übergebene Veranstaltung anmeldet, oder <code>null</code> falls keine
   * solche existiert
   * @throws DatenbankInkonsistenzException
   */
  public Veranstaltungsteilnahme getVeranstaltungsteilnahme(
    Benutzer benutzer, Veranstaltung veranstaltung) throws DatenbankInkonsistenzException;    
    
  /**
   * Liefert eine Liste der Teilnahmen an der Veranstaltung. Wartelisteneintraege
   * werden ausgegeben.
   * @param veranstaltung die Veranstaltung
   * @return eine Liste aller Teilnahmen an der Veranstaltung
   */
  public VeranstaltungsteilnahmeListe getTeilnahmeListe(
    Veranstaltung veranstaltung);    
    
  /**
   * Bestimmt die Anzahl der Teilnehmer, die sich für eine Veranstaltung
   * bisher angemeldet haben. Die Anmeldeliste wird nicht mitgezählt.
   * @param veranstaltung die Veranstaltung, deren Teilnehmeranzahl bestimmt 
   *  werden soll
   * @return die Anzahl der Teilnehmer, die sich für diese Veranstaltung
   *   bisher angemeldet haben
   */
  public int getTeilnehmerAnzahl(Veranstaltung veranstaltung);
  
  /**
   * Bestimmt die Anzahl der Teilnehmer, die auf der Warteliste für
   * die übergebene Veranstaltung stehen
   * @param veranstaltung die Veranstaltung, deren Wartelistenlänge bestimmt 
   *  werden soll
   * @return die Länge der Warteliste
   */
  public int getWartelistenLaenge(Veranstaltung veranstaltung);

  /**
   * Bestimmt die Anzahl der Teilnehmer, die auf der blockierenden Warteliste für
   * die übergebene Veranstaltung stehen
   * @param veranstaltung die Veranstaltung, deren Wartelistenlänge bestimmt 
   *  werden soll
   * @return die Länge der Warteliste
   */
  public int getBlockierendeWartelistenLaenge(Veranstaltung veranstaltung);

  /**
   * Bestimmt die Anzahl der Teilnehmer, die sich für Veranstaltungen dieser
   * Veranstaltungsgruppe bisher angemeldet haben. Auch Benutzer auf 
   * Wartelisten werden berücksichtigt. Nimmt ein Benutzer an
   * mehreren Veranstaltungen der Gruppe teil, wird er nur einfach gezählt.
   * @param veranstaltungsgruppe die Veranstaltungsgruppe
   * @return die Anzahl der Teilnehmer, die sich für Veranstaltungen dieser
   *   Veranstaltungsgruppe bisher angemeldet haben
   */
  public int getTeilnehmerAnzahl(Veranstaltungsgruppe veranstaltungsgruppe);

  /**
   * Bestimmt die Anzahl der Teilnehmer, die sich die Veranstaltungen in
   * der übergebenen Liste bisher angemeldet haben.
   * @param veranstaltungen die Veranstaltungen, deren Teilnehmeranzahl bestimmt 
   *  werden soll
   * @return die Anzahl der Teilnehmer, die sich für diese Veranstaltungen
   *   bisher angemeldet haben, in der gleichen Reihenfolge wie die 
   *   Veranstaltungen in der Liste
   */
  public int[] getTeilnehmerAnzahl(VeranstaltungenListe veranstaltungen);

  /**
   * Liefert eine Liste aller Benutzer die sich für Veranstaltungen
   * der Veranstaltungsgruppe bisher angemeldet haben. Auch Teilnehmer
   * auf Wartelisten werden berücksichtigt. Nimmt ein Benutzer an
   * mehreren Veranstaltungen der Gruppe teil, wird er nur einmal aufgenommen.
   * @param veranstaltungsgruppe die Veranstaltungsgruppe
   * @return eine Liste aller Teilnehmer an der Veranstaltungsgruppe
   */
  public BenutzerListe getTeilnehmerListe(
      Veranstaltungsgruppe veranstaltungsgruppe);
  
  /**
   * Liefert eine Liste aller Benutzer die sich für die Veranstaltung
   * angemeldet haben. Benutzer auf der Warteliste werden aufgelistet.
   * @param veranstaltung die Veranstaltung
   * @return eine Liste aller Teilnehmer an der Veranstaltung
   */
  public BenutzerListe getTeilnehmerListe(Veranstaltung veranstaltung);
  
  /**
   * Liefert eine Liste aller Benutzer die für eine Veranstaltung angemeldet sind,
   * von der mindestens ein Termin im übergebenen Zeitraum liegt.
   * @param zeitraum der Zeitraum
   * @return eine Liste aller Teilnehmer
   */
  public BenutzerListe getTeilnehmerListeInZeitraum(Zeitraum zeitraum);  
}