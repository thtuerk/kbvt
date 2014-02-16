package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.*;

import de.oberbrechen.koeb.datenbankzugriff.AbstractBemerkungVeranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlBemerkungVeranstaltung extends AbstractBemerkungVeranstaltung {

  public MysqlBemerkungVeranstaltung(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  MysqlBemerkungVeranstaltung(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlBemerkungVeranstaltung() {
  }
  
  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException {
    if (istGespeichert()) return;

    if (this.getBemerkung() == null || this.getBemerkung().trim().equals("")) {
      throw new UnvollstaendigeDatenException("Der Text jeder "+
        "Bemerkung muss eingegeben sein.");
    }

    if (this.getVeranstaltung() == null) {
      throw new UnvollstaendigeDatenException("Die Veranstaltung jeder "+
        "Veranstaltungsbemerkung muss eingegeben sein.");
    }
    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into bemerkung_veranstaltung set Bemerkung = ?, " +
          "Datum = ?, aktuell = ?, VeranstaltungID = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update bemerkung_veranstaltung set Bemerkung = ?, " +
          "Datum = ?, aktuell = ?, VeranstaltungID = ? where id="+this.getId());
      }
      statement.setString(1, DatenmanipulationsFunktionen.formatString(this.getBemerkung()));
      statement.setTimestamp(2, DatenmanipulationsFunktionen.utilDate2sqlTimestamp(
          this.getDatum()));
      statement.setBoolean(3, this.istAktuell());
      statement.setInt(4, getVeranstaltung().getId());      
      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "der folgenden Veranstaltungsbemerkung:\n\n"+this.toDebugString(), true);
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
      
      //Client löschen
      statement.execute("delete from bemerkung_veranstaltung where "+
        "id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen der "+
        "Veranstaltungsbemerkung "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    bemerkung = result.getString("bemerkung");
    istAktuell = result.getBoolean("aktuell");
    datum = result.getTimestamp("datum");
    
    try{
      int veranstaltungID = result.getInt("VeranstaltungID");
      veranstaltung = (Veranstaltung) MysqlDatenbank.getMysqlInstance().
        getVeranstaltungFactory().get(veranstaltungID);
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException(
          "Die Veranstaltungsbemerkung "+id+" verweist auf "+
          "eine nicht eingetragene Veranstaltung!");
    }    
  }

  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from bemerkung_veranstaltung where id = " + id);
      boolean gefunden = result.next();
      if (!gefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
          "Eine Veranstaltungsbemerkung mit der ID "+id+" existiert nicht!");
      }
      
      load(result);
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "der Veranstaltungsbemerkung mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
    
  }  
}