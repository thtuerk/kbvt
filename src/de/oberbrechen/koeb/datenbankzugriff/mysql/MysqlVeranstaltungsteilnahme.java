package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractVeranstaltungsteilnahme;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.BenutzerBereitsAngemeldetException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlVeranstaltungsteilnahme 
  extends AbstractVeranstaltungsteilnahme {
  
  public MysqlVeranstaltungsteilnahme(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  MysqlVeranstaltungsteilnahme(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlVeranstaltungsteilnahme(
      Benutzer benutzer, Veranstaltung veranstaltung) {
    super(benutzer, veranstaltung);    
  }
  
  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException, BenutzerBereitsAngemeldetException, DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (istGespeichert()) return;

    if (this.getVeranstaltung() == null || this.getBenutzer() == null) {
      throw new UnvollstaendigeDatenException("Für jede Teilnahme muss eine "+
        "Veranstaltung und ein Benutzer eingegeben sein!");
    }

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();            
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = 
        (PreparedStatement) connection.prepareStatement(
            "select id from benutzer_besucht_veranstaltung where " +
            "benutzerID = ? and veranstaltungID = ? and id != ?");
      statement.setInt(1, getBenutzer().getId());
      statement.setInt(2, getVeranstaltung().getId());
      statement.setInt(3, getId());
      ResultSet result = (ResultSet) statement.executeQuery();      
      if (result.next()) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();            
        throw new BenutzerBereitsAngemeldetException(
          new MysqlVeranstaltungsteilnahme(result.getInt(1)));
      }        
      
      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into benutzer_besucht_veranstaltung " +
          "set BenutzerID = ?, VeranstaltungID = ?, AnmeldeDatum = ?, "+
          "Bemerkungen = ?, Status = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update benutzer_besucht_veranstaltung " +
          "set BenutzerID = ?, VeranstaltungID = ?, AnmeldeDatum = ?, "+
          "Bemerkungen = ?, Status = ? where id="+this.getId());
      }
      if (this.anmeldeDatum == null) anmeldeDatum = new Date();
      
      statement.setInt(1, this.getBenutzer().getId());
      statement.setInt(2, this.getVeranstaltung().getId());
      statement.setTimestamp(3, DatenmanipulationsFunktionen.utilDate2sqlTimestamp(this.getAnmeldeDatum()));
      statement.setString(4, this.getBemerkungen());
      statement.setInt(5, this.getStatus());
      
      statement.execute();
      
      if (this.istNeu()) {
        result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "der folgenden Freigabe:\n\n"+this.toDebugString(), true);
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
      
      statement.execute("delete from benutzer_besucht_veranstaltung where "+
        "id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen des "+
        "Teilnahme "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    
    try {
      int benutzerID = result.getInt("benutzerID");
      benutzer = (Benutzer) MysqlDatenbank.getMysqlInstance().
        getBenutzerFactory().get(benutzerID);
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException(
          "Die Veranstaltungsteilnahme "+id+" verweist auf "+
          "einen nicht eingetragenen Benutzer!");
    }
    
    try{
      int veranstaltungID = result.getInt("VeranstaltungID");
      veranstaltung = (Veranstaltung) MysqlDatenbank.getMysqlInstance().
        getVeranstaltungFactory().get(veranstaltungID);
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException(
          "Die Veranstaltungsteilnahme "+id+" verweist auf "+
          "eine nicht eingetragene Veranstaltung!");
    }

    anmeldenr = 0;
    anmeldeDatum = result.getTimestamp("Anmeldedatum");
    status = result.getInt("Status");
    bemerkungen = result.getString("Bemerkungen");
  }

  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from benutzer_besucht_veranstaltung where id = " + id);
      boolean gefunden = result.next();
      if (!gefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
          "Eine Teilnahme mit der ID "+id+" existiert nicht!");
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
        "der Teilnahme mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();    
  }

  protected int bestimmeAnmeldeNr() {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = (PreparedStatement) connection.prepareStatement(
          "select count(*) from benutzer_besucht_veranstaltung where " +
          "veranstaltungID = ? and anmeldeDatum < ?");
      
      statement.setInt(1, getVeranstaltung().getId());
      statement.setTimestamp(2, DatenmanipulationsFunktionen.utilDate2sqlTimestamp(getAnmeldeDatum()));
      ResultSet result = (ResultSet) statement.executeQuery();
      int erg;

      boolean gefunden = result.next();
      if (!gefunden) {
        erg = 1;
      } else {
        erg = result.getInt(1)+1;
      }
      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
      return erg;
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Bestimmen "+
          "der Anmeldenr der Teilnahme mit der ID "+id+"!", true);
    }
    return 0;
  }
}