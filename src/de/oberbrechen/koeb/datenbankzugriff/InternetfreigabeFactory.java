package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.InternetfreigabenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Dieses Interface repräsentiert eine Factory für Benutzer.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface InternetfreigabeFactory extends DatenbankzugriffFactory<Internetfreigabe> {
    
  /**
   * Erstellt eine neue Internetfreigabe, die den übergebenen Client
   * für den übergebenen Benutzer ab dem angegebenen Zeitpunkt freigibt und
   * vom übergebenen Mitarbeiter getätigt wird.
   *
   * @param benutzer der Benutzer, für den der Internetzugang freigeschaltet
   *   werden soll
   * @param Client der Client, an dem der Internetzugang freigeschaltet werden
   *   soll
   * @param mitarbeiter der Mitarbeiter, der die Freigabe tätigt
   * @throws DatenbankzugriffException falls beim Eintragen der Freigabe in die
   *    Datenbank ein Fehler auftritt
   */
  public Internetfreigabe freigeben(Benutzer benutzer, Client client,
    Mitarbeiter mitarbeiter, Date beginn) throws DatenbankzugriffException;
    
  /**
   * Erstellt eine neue Internetfreigabe, die den übergebenen Client
   * für den übergebenen Benutzer ab dem aktuellen Zeitpunkt freigibt und
   * vom übergebenen Mitarbeiter getätigt wird.
   *
   * @param benutzer der Benutzer, für den der Internetzugang freigeschaltet
   *   werden soll
   * @param Client der Client, an dem der Internetzugang freigeschaltet werden
   *   soll
   * @param mitarbeiter der Mitarbeiter, der die Freigabe tätigt
   * @throws DatenbankzugriffException
   */
  public Internetfreigabe freigeben(Benutzer benutzer, Client client,
    Mitarbeiter mitarbeiter) throws DatenbankzugriffException;


  /**
   * Erstellt eine neue Internetfreigabe, die den übergebenen Client
   * für den übergebenen Benutzer ab dem aktuellen Zeitpunkt freigibt und
   * vom aktuellen Mitarbeiter getätigt wird.
   *
   * @param benutzer der Benutzer, für den der Internetzugang freigeschaltet
   *   werden soll
   * @param Client der Client, an dem der Internetzugang freigeschaltet werden
   *   soll
   * @throws DatenbankzugriffException
   */
  public Internetfreigabe freigeben(Benutzer benutzer, Client client) throws DatenbankzugriffException;
  
  /**
   * Bestimmt, ob der Internetzugang für den übergebenen Client zu Zeit
   * freigegeben ist.
   * @param client
   * @return <code>TRUE</code> gdw. der Internetzugang für den übergebenen
   *  Client zur Zeit freigegeben ist
   * @throws DatenbankInkonsistenzException
   */
  public boolean istInternetzugangFreigegeben(Client client) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert die zeitlich letzte Freigabe für den übergebenen Client. Wurde
   * der Client noch nie freigegeben wird <code>null</code> geliefert.
   * @param client der Client, dessen letzte Freigabe geliefert werden soll
   * @return die letzte Freigabe
   * @throws DatenbankInkonsistenzException
   */
  public Internetfreigabe getAktuelleInternetfreigabe(Client client) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert eine unsortierte Liste aller Internetfreigaben, die im übergebenen
   * Monat getätigt wurden.
   * 
   * @param monat die Nr des Monats von 1 bis 12
   * @param jahr das Jahr
   */
  public InternetfreigabenListe 
    getAlleInternetFreigabenInMonat(int monat, int jahr);

  /**
   * Liefert eine unsortierte Liste aller Internetfreigaben, 
   * die zwischen den beiden übergebenen
   * Zeitpunkten getätigt wurden.
   * 
   * @param von der Startzeitpunkt
   * @param bis der Entzeitpunkt
   */
  public InternetfreigabenListe getAlleInternetfreigabenInZeitraum(
    Date von, Date bis);

  /**
   * Sperrt den Internetzugang für den übergebenen Client
   * @param client
   */
  public void sperren(Client client);

  /**
   * Liefert den Zeitraum, in dem Internetfreigaben
   * getätigt wurden.
   */
  public Zeitraum getInternetfreigabenZeitraum();
}