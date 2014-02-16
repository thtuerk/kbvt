package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.*;

import de.oberbrechen.koeb.datenbankzugriff.AbstractClient;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlClient extends AbstractClient {

  public MysqlClient(int id) throws DatenNichtGefundenException {
    load(id);
  }

  MysqlClient(ResultSet result) throws SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlClient() {
  }
  
  public void reload() throws DatenNichtGefundenException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException {
    if (istGespeichert()) return;

    if (this.getName() == null || this.getName().trim().equals("")) {
      throw new UnvollstaendigeDatenException("Der Name jedes "+
        "Clients muss eingegeben sein.");
    }

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into client set Name = ?, IP = ?, besitztInternetzugang = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update client set Name = ?, IP = ?, besitztInternetzugang = ? where id="+this.getId());
      }
      statement.setString(1, this.getName());
      statement.setString(2, DatenmanipulationsFunktionen.formatString(
        this.getIP()));
      statement.setBoolean(3, this.getBesitztInternetzugang());

      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "des folgenden Clients:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public void loesche() throws DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      
      //Internet_zugang
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from internet_zugang where "+
          "ClientID = "+this.getId());
      result.next();
      if (result.getInt(1) > 0)
        throw new DatenbankInkonsistenzException("Der Client "+this.getName()+          "("+this.getId()+") kann nicht gelöscht werden, da noch Freigaben " +
          "dieses Clients existieren.");

      //Client löschen
      statement.execute("delete from client where "+
        "id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen des "+
        "Clients "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException {
    id = result.getInt("id");
    name = result.getString("name");
    ip = result.getString("IP");
    besitztInternetzugang = result.getBoolean("besitztInternetzugang");
  }

  void load(int id) throws DatenNichtGefundenException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from client where id = " + id);
      boolean clientGefunden = result.next();
      if (!clientGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
          "Ein Client mit der ID "+id+" existiert nicht!");
      }
      
      load(result);
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "des Clients mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
    
  }  
}