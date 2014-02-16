package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsgruppenListe;

/**
 * Dieses Interface repräsentiert eine Factory für Veranstaltungsgruppen..
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface VeranstaltungsgruppeFactory extends DatenbankzugriffFactory<Veranstaltungsgruppe> {

  /**
   * Liefert eine Liste aller
   * Veranstaltungsgruppen, die in der Datenbank eingetragen sind.
   *
   * @return eine Liste aller Veranstaltungsgruppen
   */
  public VeranstaltungsgruppenListe getAlleVeranstaltungsgruppen();
  
  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Veranstaltungsgruppe-Objekt
   * 
   * @return das neue Veranstaltungsgruppe-Objekt
   */
  public Veranstaltungsgruppe erstelleNeu();   
}