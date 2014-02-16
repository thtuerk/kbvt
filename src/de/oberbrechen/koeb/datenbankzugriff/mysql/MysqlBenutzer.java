package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractBenutzer;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.BenutzernameSchonVergebenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlBenutzer extends AbstractBenutzer {

  public void reload() throws DatenNichtGefundenException {
    if (this.istNeu()) return;
    this.load(this.getId());
  }

  public void save() throws UnvollstaendigeDatenException, 
    BenutzernameSchonVergebenException {
    if (istGespeichert()) return;
    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
  
      if (this.getVorname() == null || this.getNachname() == null) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();        
        throw new UnvollstaendigeDatenException("Vorname und Nachname jedes "+
          "Benutzers müssen eingegeben sein.");
      }
  
      
      //Benutzername schon vergeben?
      if (benutzername != null) {
        try {
          int zugehoerigeId =
            MysqlDatenbank.getMysqlInstance().getBenutzerFactory().
            sucheBenutzername(benutzername);
          if (zugehoerigeId != id) {
            MysqlDatenbank.getMysqlInstance().endTransaktion();        
            throw new BenutzernameSchonVergebenException(
              new MysqlBenutzer(zugehoerigeId));
          }
        } catch (DatenNichtGefundenException e) {
          //dies sollte der Normalfall sein. Also ist alles OK
        }
      }

      Connection connection = MysqlDatenbank.getMysqlInstance().
        getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into benutzer set Nachname = ?, "+
          "Vorname = ?, Ort = ?, Adresse = ?, Tel = ?, Fax = ?, eMail = ?, "+
          "Benutzername = ?, Passwort = ?, Bemerkungen = ?, Geburtsdatum = ?, "+
          "Anmeldedatum = ?, klasse = ?, VIP=?, aktiv=?");        
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update benutzer set Nachname = ?, Vorname = ?, "+
          "Ort = ?, Adresse = ?, Tel = ?, Fax = ?, eMail = ?, Benutzername = ?, "+
          "Passwort = ?, Bemerkungen = ?, Geburtsdatum = ?, Anmeldedatum = ?,"+
          "klasse = ?, VIP=?, aktiv=? "+
          "where id="+id);
      }
      statement.setString(1, this.getNachname());
      statement.setString(2, this.getVorname());
      statement.setString(3, this.getOrt());
      statement.setString(4, this.getAdresse());
      statement.setString(5, this.getTel());
      statement.setString(6, this.getFax());
      statement.setString(7, this.getEMail());
      statement.setString(8, this.getBenutzername());
      statement.setString(9, passwort);
      statement.setString(10, this.getBemerkungen());
      statement.setDate(11, DatenmanipulationsFunktionen.utilDate2sqlDate(
        this.getGeburtsdatum()));
      statement.setDate(12, DatenmanipulationsFunktionen.utilDate2sqlDate(
        this.getAnmeldedatum()));
      statement.setString(13, this.getKlasse());
      statement.setBoolean(14, this.istVIP());
      statement.setBoolean(15, this.istAktiv());
      
      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Speichern des folgenden "+
        "Benutzers:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public MysqlBenutzer(int id) throws DatenNichtGefundenException {
    load(id);
  }

  public MysqlBenutzer() {
  }

  public void loesche() throws DatenbankInkonsistenzException {
    if (this.istNeu()) return;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();

      //Ausleihen
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from ausleihe where "+
          "BenutzerID="+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Der Benutzer "+this.getName()+
        " kann nicht gelöscht werden, da noch Ausleihen dieses Benutzers "+
        "existieren.");
      }
      
      // Veranstaltungen
      result = (ResultSet) statement.executeQuery(
        "select count(*) from benutzer_besucht_veranstaltung where "+
        "benutzerID="+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Der Benutzer "+this.getName()+
        " kann nicht gelöscht werden, da noch Veranstaltungsteilnahmen dieses "+
        "Benutzers existieren.");
      }

      // Internet
      result = (ResultSet) statement.executeQuery(
        "select count(*) from internet_zugang where "+
        "Benutzerid="+this.getId());
      result.next();
      if (result.getInt(1) > 0) {      
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Der Benutzer "+this.getName()+
        " kann nicht gelöscht werden, da noch Internetfreigaben dieses "+
        "Benutzers existieren.");
      }
      
      // Mitarbeiter
      result = (ResultSet) statement.executeQuery(
        "select count(*) from mitarbeiter where "+
        "benutzerID="+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Der Benutzer "+this.getName()+
        " kann nicht gelöscht werden, da er noch als Mitarbeiter eingetragen "+
        "ist.");
      }

      // Benutzer löschen
      statement.execute("delete from benutzer where "+
        "id=\""+this.getId()+"\"");

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen " +
        "des folgenden Benutzers:\n\n"+this.toDebugString(), true);
    }
  }
  
  MysqlBenutzer(ResultSet result) throws SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  void load(ResultSet result) throws SQLException {
    id = result.getInt("id");
    vorname = result.getString("vorname");
    nachname = result.getString("nachname");
    adresse = result.getString("adresse");
    ort = result.getString("ort");
    klasse = result.getString("klasse");
    tel = result.getString("tel");
    fax = result.getString("fax");
    eMail = result.getString("eMail");
    benutzername = result.getString("benutzername");
    passwort = result.getString("Passwort");
    bemerkungen = result.getString("bemerkungen");
    geburtsdatum = result.getDate("geburtsdatum");
    anmeldedatum = result.getDate("anmeldedatum");
    vip = result.getBoolean("VIP");
    aktiv = result.getBoolean("aktiv");
  }
  
  void load(int id) throws DatenNichtGefundenException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from benutzer where id = " + id);
      boolean benutzerGefunden = result.next();
      if (!benutzerGefunden) throw new DatenNichtGefundenException(
        "Ein Benutzer mit der ID "+id+" existiert nicht!");
  
      load(result);
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "des Benutzers mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }  
}