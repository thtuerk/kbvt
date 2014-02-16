package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractEinstellung;
import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlEinstellung extends AbstractEinstellung {

  public MysqlEinstellung(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  MysqlEinstellung(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlEinstellung(Client client, Mitarbeiter mitarbeiter, String name) {
    this.client = client;
    this.mitarbeiter = mitarbeiter;
    this.name = name;
    this.wert = null;
  }
  
  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException {
    if (istGespeichert()) return;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
            "insert into einstellung " +
            "set ClientID = ?, MitarbeiterID = ?," +
        "Name = ?, Wert = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
            "update einstellung "+
            "set ClientID = ?, MitarbeiterID = ?," +
            "Name = ?, Wert = ? "+
            "where id="+this.getId());
      }
      
      MysqlDatenbank.setIdInPreparedStatement(1, getClient(), statement);
      MysqlDatenbank.setIdInPreparedStatement(2, getMitarbeiter(), statement);
      statement.setString(3, this.getName());
      statement.setString(4, this.getWert());
      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +
          "der folgenden Einstellung:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
    
  protected void save(boolean alleClients, boolean alleMitarbeiter) throws UnvollstaendigeDatenException {      
    if (this.getName() == null || this.getName().trim().equals("")) {
      throw new UnvollstaendigeDatenException("Jede Einstellung muss einen "+
        "Namen besitzen!");
    }
    
    if (!alleClients && !alleMitarbeiter) save();

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      
      Client client = null;
      Mitarbeiter mitarbeiter = null;
      String updateStatementString = "update einstellung set " +
          "wert = '"+getWert()+"' where name='"+getName()+"'";
      String selectStatementString = "select id from einstellung where name='"+getName()+"'";
      
      if (alleClients && !alleMitarbeiter) {
        mitarbeiter = getMitarbeiter();
        selectStatementString +=" and isNull(clientID)";
        if (mitarbeiter == null) {
          updateStatementString += " and isNull(mitarbeiterID)";
          selectStatementString += " and isNull(mitarbeiterID)";
        } else {
          updateStatementString += " and mitarbeiterID="+mitarbeiter.getId();          
          selectStatementString += " and mitarbeiterID="+mitarbeiter.getId();          
        }
      } else if (!alleClients && alleMitarbeiter) {
        client = getClient();
        selectStatementString +=" and isNull(mitarbeiterID)";
        if (client == null) {
          updateStatementString += " and isNull(clientID)";
          selectStatementString += " and isNull(clientID)";
        } else {
          updateStatementString += " and clientID="+client.getId();          
          selectStatementString += " and clientID="+client.getId();          
        }
      } else if (alleClients && alleMitarbeiter) {        
        selectStatementString +=" and isNull(mitarbeiterID) and isNull(clientId)";
      }
      Statement updateStatement = MysqlDatenbank.getMysqlInstance().getStatement();
      updateStatement.execute(updateStatementString);
      MysqlDatenbank.getMysqlInstance().releaseStatement(updateStatement);

      Statement selectStatement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = selectStatement.executeQuery(selectStatementString);
      int allgemeinID = 0;
      if (result.next()) allgemeinID = result.getInt(1);
      MysqlDatenbank.getMysqlInstance().releaseStatement(selectStatement);
      
      
      PreparedStatement statement = null;      
      if (allgemeinID == 0) {
        statement = (PreparedStatement) connection.prepareStatement(
            "insert into einstellung " +
            "set ClientID = ?, MitarbeiterID = ?," +
        "Name = ?, Wert = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
            "update einstellung "+
            "set ClientID = ?, MitarbeiterID = ?," +
            "Name = ?, Wert = ? "+
            "where id="+allgemeinID);
      }      
      
      MysqlDatenbank.setIdInPreparedStatement(1, client, statement);
      MysqlDatenbank.setIdInPreparedStatement(2, mitarbeiter, statement);
      statement.setString(3, this.getName());
      statement.setString(4, this.getWert());
      statement.execute();
            
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "der Einstellungen!", true);
    }

    this.setChanged();
    this.notifyObservers();
  }

  public void loesche() {
    if (this.istNeu()) return;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      
      statement.execute("delete from einstellung where id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen der "+
        "folgenden Einstellung: "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    name = result.getString("name");
    wert = result.getString("wert");
    
    if (result.getString("clientID") != null) {
      try {
        client = (Client) MysqlDatenbank.getMysqlInstance().
          getClientFactory().get(result.getInt("clientID"));
      } catch (DatenNichtGefundenException e) {
        throw new DatenbankInkonsistenzException("Die Einstellung "+
            id+" verweist auf einen nicht eingetragenen Client!");
      }
    } else {
      client = null;
    }
    if (result.getString("mitarbeiterID") != null) {
      try {
        mitarbeiter = (Mitarbeiter) MysqlDatenbank.getMysqlInstance().
          getMitarbeiterFactory().get(result.getInt("mitarbeiterID"));
      } catch (DatenNichtGefundenException e) {
        throw new DatenbankInkonsistenzException("Die Einstellung "+
            id+" verweist auf einen nicht eingetragenen Mitarbeiter!");
      }        
    } else {
      mitarbeiter = null;
    }
  }

  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from einstellung where id = " + id);
      boolean gefunden = result.next();
      if (!gefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
            "Eine Einstellung mit der ID "+id+" existiert nicht!");
      }
  
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
        "der Einstellung mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
    
  }

  public void saveAlleClients() throws UnvollstaendigeDatenException {
    save(true, false);
  }

  public void saveAlleMitarbeiter() throws UnvollstaendigeDatenException {
    save(false, true);
  }

  public void saveAlleClientsUndMitarbeiter() throws UnvollstaendigeDatenException {
    save(true, true);
  }
}