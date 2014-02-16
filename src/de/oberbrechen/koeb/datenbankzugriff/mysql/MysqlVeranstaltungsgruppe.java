package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractVeranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlVeranstaltungsgruppe extends AbstractVeranstaltungsgruppe {

  public MysqlVeranstaltungsgruppe(int id) throws DatenNichtGefundenException {
    load(id);
  }

  MysqlVeranstaltungsgruppe(ResultSet result) throws SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlVeranstaltungsgruppe() {
  }
  
  public void reload() throws DatenNichtGefundenException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException {
    if (istGespeichert()) return;

    if (this.getName() == null)
      throw new UnvollstaendigeDatenException("Der Name jeder Veranstaltungsgruppe" +
        "muss eingegeben sein!");

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into veranstaltungsgruppe set name = ?, kurzName = ?, "+
          "beschreibung = ?, homeDir = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update veranstaltungsgruppe set name = ?, kurzName = ?, "+
          "beschreibung = ?, homeDir = ? where id="+this.getId());
      }

      statement.setString(1, this.getName());
      statement.setString(2, this.getKurzName());
      statement.setString(3, this.getBeschreibung());
      statement.setString(4, this.getHomeDir());
      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +
        "der folgenden Veranstaltungsgruppe:\n\n"+this.toDebugString(), true);
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

      //Veranstaltungen testen
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from veranstaltung where "+
          "veranstaltungsgruppeID="+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Die Veranstaltungsgruppe "+
          this.getName()+" ("+this.getId()+") " +
          " kann nicht gelöscht werden, da noch Veranstaltungen dieser Gruppe "+
          "existieren.");
      }
      
      //Veranstaltung löschen
      statement.execute("delete from veranstaltungsgruppe where "+
        "ID=\""+this.getId()+"\"");

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen der folgenden "+
        "Veranstaltungsgruppe:\n\n"+this.toDebugString(), true);
    }
   }
  
  void load(ResultSet result) throws SQLException {
    id = result.getInt("id");
    name = result.getString("name");
    kurzName = result.getString("kurzName");
    homeDir = result.getString("homeDir");
    beschreibung = result.getString("Beschreibung");
  }

  void load(int id) throws DatenNichtGefundenException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();

      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from veranstaltungsgruppe where id = " + id);
      boolean gruppeGefunden = result.next();
      if (!gruppeGefunden) throw new DatenNichtGefundenException(
        "Eine Veranstaltungsgruppe mit der ID "+id+" existiert nicht!");
  
      load(result);
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "der Veranstaltungsgruppe mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();    
  }
}