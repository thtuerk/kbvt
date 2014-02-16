package de.oberbrechen.koeb.datenbankzugriff;


/**
* Eine Factory für Datenbank-Objekte. 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface DatenbankFactory {

  /**
   * Erstellt ein neues <code>Datenbank</code>-Objekt.
   * @return die neue Instanz der <code>Datenbank</code>-Klasse
   */
  public Datenbank erstelleDatenbank();
}