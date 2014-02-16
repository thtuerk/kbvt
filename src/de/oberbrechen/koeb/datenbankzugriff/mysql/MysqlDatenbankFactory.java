package de.oberbrechen.koeb.datenbankzugriff.mysql;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.DatenbankFactory;
import de.oberbrechen.koeb.framework.KonfigurationsDatei;


/**
* Eine Factory für MysqlDatenbank-Objekte. 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MysqlDatenbankFactory implements DatenbankFactory {

  public Datenbank erstelleDatenbank() {
    KonfigurationsDatei confDatei =
      KonfigurationsDatei.getStandardKonfigurationsdatei();

    String host          = confDatei.getProperty("MySQL-Host");
    String port          = confDatei.getProperty("MySQL-Port");
    String datenbankname = confDatei.getProperty("MySQL-Datenbankname");
    String user          = confDatei.getProperty("MySQL-User");
    String passwort      = confDatei.getProperty("MySQL-Passwort");
    return new MysqlDatenbank(host, port, datenbankname, user, passwort);        
  }
}