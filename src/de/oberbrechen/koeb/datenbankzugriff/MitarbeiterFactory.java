package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;

/**
 * Dieses Interface repräsentiert eine Factory für Benutzer.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface MitarbeiterFactory extends DatenbankzugriffFactory<Mitarbeiter> {

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Mitarbeiter-Objekt
   * 
   * @param benutzer der Benutzer, der zu einem Mitarbeiter gemacht werden soll
   * @return das neue Mitarbeiter-Objekt
   */
  public Mitarbeiter erstelleNeu(Benutzer benutzer); 

  /**
   * Liefert eine Liste aller Mitarbeiter, die in der Datenbank
   * eingetragen sind
   */
  public MitarbeiterListe getAlleMitarbeiter();

  /**
   * Liefert eine Liste aller Mitarbeiter, die die übergebene Berechtigung
   * besitzen 
   * @param berechtigung die nötige Berechtigung
   */
  public MitarbeiterListe getAlleMitarbeiterMitBerechtigung(int berechtigung);
  
  /**
   * Liefert eine Liste aller Mitarbeiter, die die übergebene Berechtigung
   * besitzen und noch aktiv sind
   * @param berechtigung die nötige Berechtigung
   */
  public MitarbeiterListe getAktiveMitarbeiterMitBerechtigung(int berechtigung);
  
  /**
   * Liefert den aktuellen Mitarbeiter
   * @return den aktuellen Mitarbeiter
   */
  public Mitarbeiter getAktuellenMitarbeiter();
  
  /**
   * Setzt den aktuellen Mitarbeiter
   * @param mitarbeiter der neue aktuelle Mitarbeiter
   */
  public void setAktuellenMitarbeiter(Mitarbeiter mitarbeiter);

  /**
   * Bestimmt die Mitarbeiterid, die zu der übergebenen
   * Benutzerid gehört.
   *
   * @param benutzerid die Benutzerid
   * @throws DatenNichtGefundenException falls kein
   *   Mitarbeiter mit dieser Benutzerid existiert
   * @return die zugehörige Mitarbeiterid
   */
  public int sucheBenutzer(int id) throws DatenNichtGefundenException;

  
  /**
   * Läd den Mitarbeiter, der zu der übergebenen
   * Benutzerid gehört.
   *
   * @param benutzerid die Benutzerid
   * @throws DatenNichtGefundenException falls kein
   *   Mitarbeiter mit dieser Benutzerid existiert
   * @return die zugehörigen Mitarbeiter
   */
  public Mitarbeiter getMitarbeiterZuBenutzer(int benutzerId) throws DatenNichtGefundenException, DatenbankInkonsistenzException;
  
  
  /**
   * Bestimmt die Mitarbeiternr, die zu dem übergebenen
   * Mitarbeiter-Benutzernamen gehört.
   *
   * @param benutzername der Mitarbeiter-Benutzername
   * @throws DatenNichtGefundenException falls kein
   *   Mitarbeiter mit diesem Mitarbeiter-Benutzernamen existiert
   * @return die zugehörige Mitarbeiter
   */
  public int sucheMitarbeiterBenutzername(String benutzername) 
    throws DatenNichtGefundenException;
  
  /**
   * Erstellt einen neuen Mitarbeiter Namens Neuer Mitarbeiter, der alle
   * Berechtigungen besitzt.
   */
  public Mitarbeiter erstelleStandardMitarbeiter();  
}