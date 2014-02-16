package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractInternetfreigabe;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.ClientFactory;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.MitarbeiterFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlInternetfreigabe extends AbstractInternetfreigabe {

  public MysqlInternetfreigabe(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  MysqlInternetfreigabe(Benutzer benutzer, Client client, 
                        Mitarbeiter mitarbeiter, Date beginn) {
    this.client = client;
    this.benutzer = benutzer;
    this.mitarbeiter = mitarbeiter;
    this.von = beginn;
  }

  MysqlInternetfreigabe(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException {
    if (istGespeichert()) return;

    if (this.getClient() == null || this.getMitarbeiter() == null ||
        this.getBenutzer() == null || this.getStartZeitpunkt() == null) {
      throw new UnvollstaendigeDatenException("Client, Benutzer, Mitarbeiter" +
        "und Beginn jeder Internetfreigabe muss eingegeben sein.");
    }

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into internet_zugang " +
          "set ClientID = ?, MitarbeiterID = ?, BenutzerID = ?," +
          "Von = ?, Bis = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update internet_zugang "+
          "set ClientID = ?, MitarbeiterID = ?, BenutzerID = ?," +
          "Von = ?, Bis = ? "+
          "where id="+this.getId());
      }
      statement.setInt(1, this.getClient().getId());
      statement.setInt(2, this.getMitarbeiter().getId());
      statement.setInt(3, this.getBenutzer().getId());
      statement.setTimestamp(4, 
          DatenmanipulationsFunktionen.utilDate2sqlTimestamp(this.getStartZeitpunkt()));
      statement.setTimestamp(5, 
          DatenmanipulationsFunktionen.utilDate2sqlTimestamp(this.getEndZeitpunkt()));

      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "des folgenden Internetzugangs:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public void loesche() {
    if (this.istNeu()) return;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      
      statement.execute("delete from internet_zugang where "+
        "id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen der "+
        "Internetfreigabe "+this.toDebugString(), true);
    }
  }
  

  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    von = result.getTimestamp("Von");
    bis = result.getTimestamp("Bis");
    
    try {
      ClientFactory clientFactory = 
        MysqlDatenbank.getMysqlInstance().getClientFactory();
      client = (Client) clientFactory.get(result.getInt("ClientID"));
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Internetfreigabe "+ id+
          " verweist auf einen nicht eingetragenen Client!");
    }
    
    try {
      MitarbeiterFactory mitarbeiterFactory = 
        MysqlDatenbank.getMysqlInstance().getMitarbeiterFactory();
      mitarbeiter = (Mitarbeiter) mitarbeiterFactory.get(result.getInt("MitarbeiterID"));
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Internetfreigabe "+ id+
          " verweist auf einen nicht eingetragenen Mitarbeiter!");
    }

    try {
      BenutzerFactory benutzerFactory = 
        MysqlDatenbank.getMysqlInstance().getBenutzerFactory();
      benutzer = (Benutzer) benutzerFactory.get(result.getInt("BenutzerID"));
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Internetfreigabe "+ id+
          " verweist auf einen nicht eingetragenen Benutzer!");
    }      
  }

  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from internet_zugang where id = " + id);
      boolean gefunden = result.next();
      if (!gefunden) throw new DatenNichtGefundenException(
        "Eine Internetfreigabe mit der ID "+id+" existiert nicht!");
  
      try {
        load(result);
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      }
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "der Internetfreigabe mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();    
  }  
}