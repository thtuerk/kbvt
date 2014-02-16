package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMedientyp;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlMedientyp extends AbstractMedientyp {

  public MysqlMedientyp(int id) throws DatenNichtGefundenException {
    load(id);
  }

  MysqlMedientyp(ResultSet result) throws SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlMedientyp() {
  }
  
  public void reload() throws DatenNichtGefundenException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() {
    if (istGespeichert()) return;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into medientyp set Name = ?, Plural = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update medientyp set Name = ?, Plural = ? where id="+this.getId());
      }
      statement.setString(1, DatenmanipulationsFunktionen.formatString(
        this.getName()));
      statement.setString(2, DatenmanipulationsFunktionen.formatString(
        this.getPlural()));

      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "des folgenden Medientyps:\n\n"+this.toDebugString(), true);
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
      
      //Medien
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from medium where "+
          "medientypID = "+this.getId());
      result.next();
      if (result.getInt(1) > 0)
        throw new DatenbankInkonsistenzException("Der Medientyp '"+this.getName()+          "' ("+this.getId()+") kann nicht gelöscht werden, da noch Medien dieses " +
          "Typs existieren.");

      //löschen
      statement.execute("delete from medientyp where "+
        "id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen des "+
        "Medientyps "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException {
    id = result.getInt("id");
    name = result.getString("name");
    plural = result.getString("plural");
  }

  void load(int id) throws DatenNichtGefundenException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from medientyp where id = " + id);
      boolean typGefunden = result.next();
      if (!typGefunden) throw new DatenNichtGefundenException(
        "Ein Medientyp mit der ID "+id+" existiert nicht!");
  
      load(result);
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "des Medientyps mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
    
  }  
}