package de.oberbrechen.koeb.datenbankzugriff;

/**
* Dieses Interface repräsentiert einen Client, für den z.B. die Internetfreigabe
* getätigt werden kann.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface Client extends Datenbankzugriff {
    
  /**
   * Liefert die IP des Clients.
   * Achtung: Es wird kein Konsitenzcheck durchgeführt. Es kann
   * also nicht garantiert werden, dass das Ergebnis eine
   * gültige IP-Adresse ist.
   * @return IP des Clients
   */
  public String getIP();

  /**
   * Liefert den Namen des Clients.
   * @return den Namen des Clients
   */
  public String getName();

  /**
   * Liefert, ob der Client einen Interzugang besitzt, der gesteuert werden soll.
   * @return ob der Client einen Interzugang besitzt, der gesteuert werden soll.
   */
  public boolean getBesitztInternetzugang();

  /**
   * Setzt, ob der Client einen Interzugang besitzt, der gesteuert werden soll.
   */
  public void setBesitztInternetzugang(boolean besitztInternetzugang);

  /**
   * Setzt die IP des Clients.
   * @param ip die neue IP
   */
  public void setIP(String ip);

  /**
   * Setzt den Namen des Clients.
   */
  public void setName(String name);
}
