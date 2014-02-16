package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.ClientListe;

/**
 * Diese Interface repräsentiert eine Factory für Clients
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface ClientFactory extends DatenbankzugriffFactory<Client> {

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Client-Objekt
   * 
   * @return das neue Client-Objekt
   */
  public Client erstelleNeu(); 

  /**
   * Liefert den Client, auf dem die Software ausgeführt wird. 
   * Existiert dieser nicht in der Datenbank, so wird er hinzugefügt!
   * @return der benutzte Client
   */
  public Client getBenutzenClient();
  
  /**
   * Liefert eine unsortierte Liste aller Clients, die in der Datenbank
   * eingetragen sind.
   * @see ClientListe
   */
  public ClientListe getAlleClients();
  
  /**
   * Liefert eine unsortierte Liste aller Clients, 
   * deren Internetzugang gesteuert werden kann.
   * @see ClientListe
   */
  public ClientListe getAlleClientsMitInternetzugang();
}
