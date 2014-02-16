package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractInternetfreigabeFactory;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Internetfreigabe;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.InternetfreigabenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlInternetfreigabeFactory 
  extends AbstractInternetfreigabeFactory {


  protected Internetfreigabe ladeAktuelleInternetfreigabe(Client client) throws DatenbankInkonsistenzException {
    if (client == null || client.istNeu()) {
      throw new IllegalArgumentException("Es dürfen nur gespeicherte Clients "+
      "übergeben werden!");
    }
    Internetfreigabe erg = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from internet_zugang where clientID="+client.getId()+
          " order by von DESC limit 1");
      if (result.next()) {
        try {
          erg = new MysqlInternetfreigabe(result);
        } catch (DatenbankInkonsistenzException e) {
          MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
          MysqlDatenbank.getMysqlInstance().endTransaktion();
          throw e;
        }
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der aktuellen Internetfreigabe!", true);
    }
  
    return erg;
  }

  public InternetfreigabenListe getAlleInternetfreigabenInZeitraum(
      Date von, Date bis) {
    InternetfreigabenListe liste = new InternetfreigabenListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
            
      PreparedStatement statement = (PreparedStatement) 
        connection.prepareStatement(
          "select * from internet_zugang where von >= ? and von <= ?;");
      statement.setDate(1, DatenmanipulationsFunktionen.utilDate2sqlDate(von));
      statement.setDate(2, DatenmanipulationsFunktionen.utilDate2sqlDate(bis));

      ResultSet result = (ResultSet) statement.executeQuery();      
      while (result.next()) {
        try {
          Internetfreigabe freigabe = new MysqlInternetfreigabe(result);
          liste.addNoDuplicate(freigabe);        
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Inkonsistenz beim " +
              "Laden der Internetfreigabeliste entdeckt!", false);
        }        
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Internetfreigaben im Zeitraum!", true);
    }

    return liste;
  }

  public void sperren(Client client) {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
            
      PreparedStatement statement = (PreparedStatement) 
        connection.prepareStatement(
          "update internet_zugang set bis = ? where isNull(bis) and clientID = ?;");
      statement.setTimestamp(1, DatenmanipulationsFunktionen.utilDate2sqlTimestamp(new Date()));
      statement.setInt(2, client.getId());
  
      statement.execute();
      
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Sperren "+
        "der Internetfreigabe für den Client "+client+"!", true);
    }
    entferneAusClientCache(client);
  }

  public Internetfreigabe ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlInternetfreigabe(id);
  }

  public Internetfreigabe freigeben(Benutzer benutzer, Client client,
                                    Mitarbeiter mitarbeiter, Date beginn) throws DatenbankzugriffException {
    Internetfreigabe result = 
      new MysqlInternetfreigabe(benutzer, client, mitarbeiter, beginn);
    result.save();
    entferneAusClientCache(client);    
    return result;
  }

  public Zeitraum getInternetfreigabenZeitraum() {
    Date beginn = null;
    Date ende = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement(); 
      
      ResultSet result = (ResultSet) statement.executeQuery("select min(von), max(von) from internet_zugang");
      result.next();
      beginn = result.getDate(1);
      ende = result.getDate(2);
      
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Internetzugänge!", true);
    }

    if (beginn==null) beginn=new Date();
    if (ende==null) ende=new Date();    
    return new Zeitraum(beginn, ende);
  }
}