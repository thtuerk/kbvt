package de.oberbrechen.koeb.datenbankzugriff.mysql.datenbankupdate;

import java.sql.SQLException;

import com.mysql.jdbc.*;

import de.oberbrechen.koeb.datenbankzugriff.mysql.MysqlDatenbank;
import de.oberbrechen.koeb.framework.ErrorHandler;


/**
 * Diese Klasse dient dazu die Datenbank von Version 1 des 
 * Datenbankschemas in Version 2 zu überführen.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Datenbankupdate_2_nach_3 {
 
  MysqlDatenbank mysqlDatenbank;
  Connection connection;
  
  public Datenbankupdate_2_nach_3() {
    mysqlDatenbank = MysqlDatenbank.getMysqlInstance();
    connection = mysqlDatenbank.getConnection();
  }
  
  /**
   * Konvertiert die Datenbank.
   * @param von die vorhandene Version
   * @param nach die herzustellende Version
   */
  public void convertDatenbank() {
    try {
      System.out.println("Konvertiere Datenbank von Version 2 nach Version 3");

      mysqlDatenbank.beginTransaktion();

      createVeranstaltungsbemerkungen();
      convertBenutzerBesuchtVeranstaltung();
      convertBenutzer();
      convertClient();
      
      mysqlDatenbank.endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim " +        "Konvertieren der Datenbank!", true);
    }
  }

  /**
   * @throws SQLException
   * 
   */
  private void createVeranstaltungsbemerkungen() throws SQLException {
    System.out.println("   - Bemerkungen-Veranstaltungen");
    Statement statement = mysqlDatenbank.getStatement();    
    statement.execute("CREATE TABLE bemerkung_veranstaltung ("+
        "ID int(11) NOT NULL auto_increment," +
        "Bemerkung text NOT NULL,"+
        "Datum datetime default NULL,"+
        "VeranstaltungID int(11) NOT NULL default '0',"+        
        "aktuell tinyint(1) NOT NULL default '0',"+        
        "PRIMARY KEY (ID)"+
        ") TYPE=InnoDB;");
    statement.execute("create index veranstaltungIndex on " +
        "bemerkung_veranstaltung (VeranstaltungID)");
    statement.execute("alter table bemerkung_veranstaltung add foreign key " +
        "veranstaltungID (VeranstaltungID) references veranstaltung(id) " +
        "on delete cascade on update restrict");          
  }
  
  private void convertBenutzerBesuchtVeranstaltung() throws SQLException {
    System.out.println("   - benutzer_besucht_veranstaltung");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table benutzer_besucht_veranstaltung add anmeldeDatum datetime not null");
    statement.execute("alter table benutzer_besucht_veranstaltung add Status int(4) not null default 0");
    statement.execute("update benutzer_besucht_veranstaltung set anmeldeDatum = DATE_ADD(DATE_ADD('1980-1-1', INTERVAL anmeldenr MINUTE), INTERVAL veranstaltungID DAY)");
    statement.execute("alter table benutzer_besucht_veranstaltung drop anmeldenr");    
  }   
  
  private void convertClient() throws SQLException {
    System.out.println("   - client");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table client add besitztInternetzugang int(1) not null default 1");
  }   

  /**
   * Konvertiert die Benutzertabelle.
   */
  private void convertBenutzer() throws SQLException {
    System.out.println("   - Benutzer");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table benutzer add aktiv bool default 1 not null");
  }    
}