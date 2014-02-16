package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.MitarbeiterFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.BenutzerSchonMitarbeiterException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.EindeutigerSchluesselSchonVergebenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.MitarbeiterbenutzernameSchonVergebenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlMitarbeiter extends AbstractMitarbeiter {

  public MysqlMitarbeiter(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);    
  }

  public MysqlMitarbeiter(Benutzer benutzer) {
    this.benutzer = benutzer;
  }
  
  MysqlMitarbeiter(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    this.load(this.getId());
  }
  
  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from mitarbeiter where id = " + id);
      boolean mitarbeiterGefunden = result.next();
      if (!mitarbeiterGefunden) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        throw new DatenNichtGefundenException(
            "Ein Mitarbeiter mit der id "+id+" existiert nicht!");
      }

      try {
        load(result);
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        throw e;
      }

      MysqlDatenbank.getMysqlInstance().endTransaktion();
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "des Mitarbeiters mit der ID "+id, true);
    }
  }  

  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    this.id = result.getInt("id");
    mitarbeiterBenutzername = result.getString("benutzername");
    mitarbeiterPasswortPruefziffer = result.getString("Passwort");
    berechtigungen = result.getInt("Berechtigungen");

    int benutzerId = result.getInt("BenutzerID");
    try {
      benutzer = (Benutzer) MysqlDatenbank.getMysqlInstance().
        getBenutzerFactory().get(benutzerId);
    } catch (DatenNichtGefundenException e) {
      MysqlDatenbank.getMysqlInstance().endTransaktion();
      throw new DatenbankInkonsistenzException("Der Mitarbeiter mit der "+
        "ID "+id+" verweist auf die nicht existierende "+
        "BenutzerID "+benutzerId+"!");
    } catch (DatenbankInkonsistenzException e) {
      MysqlDatenbank.getMysqlInstance().endTransaktion();
      throw e;
    }
  }
  
  public void save() throws EindeutigerSchluesselSchonVergebenException, MitarbeiterbenutzernameSchonVergebenException, DatenbankInkonsistenzException {
    if (istGespeichert()) return;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      
      //ist Benutzer schon Mitarbeiter unter anderem Namen
      try {
        MitarbeiterFactory mitarbeiterFactory =
          MysqlDatenbank.getMysqlInstance().getMitarbeiterFactory();
        int zugehoerigeMitarbeiterId =
          mitarbeiterFactory.sucheBenutzer(getBenutzer().getId());
  
        if (zugehoerigeMitarbeiterId != this.getId()) {
          MysqlDatenbank.getMysqlInstance().endTransaktion();
          throw new BenutzerSchonMitarbeiterException((Mitarbeiter)
              mitarbeiterFactory.get(zugehoerigeMitarbeiterId));       
        }
      } catch (DatenNichtGefundenException e) {
        //alles OK
      }    
      
      //ist Mitarbeiterbenutzername eindeutig?
      if (mitarbeiterBenutzername != null) {
        try {
          MitarbeiterFactory mitarbeiterFactory =
            MysqlDatenbank.getMysqlInstance().getMitarbeiterFactory();
          int zugehoerigeMitarbeiterId =
            mitarbeiterFactory.sucheMitarbeiterBenutzername(
              getMitarbeiterBenutzername());
      
          if (zugehoerigeMitarbeiterId != this.getId()) {
            MysqlDatenbank.getMysqlInstance().endTransaktion();
            throw new MitarbeiterbenutzernameSchonVergebenException
              ((Mitarbeiter) mitarbeiterFactory.get(zugehoerigeMitarbeiterId));       
          }
        } catch (DatenNichtGefundenException e) {
          //alles OK
        }    
      }

      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;
      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into mitarbeiter set BenutzerID = ?, "+
          "Benutzername = ?, Passwort = ?, Berechtigungen = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update mitarbeiter set BenutzerID = ?, "+
          "Benutzername = ?, Passwort = ?, Berechtigungen = ? "+
          "where ID="+getId());
      }
      statement.setInt(1, this.getBenutzer().getId());
      statement.setString(2, this.getMitarbeiterBenutzername());
      statement.setString(3, mitarbeiterPasswortPruefziffer);
      statement.setInt(4, berechtigungen);

      statement.execute();
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern des folgenden "+
        "Mitarbeiters:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    setChanged();
    notifyObservers();    
  }

  public void loesche() throws DatenbankInkonsistenzException {
    if (this.istNeu()) return;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();

      //Ausleihen
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(id) from ausleihe where "+
          "MitarbeiterIdRueckgabe="+this.getId());
      result.next();
      boolean ausleiheVorhanden = result.getInt(1) > 0;
      if (!ausleiheVorhanden) {
        result = (ResultSet) statement.executeQuery(
            "select count(*) from ausleihzeitraum where "+
            "MitarbeiterId="+this.getId());
        result.next();        
        ausleiheVorhanden = result.getInt(1) > 0;
      }
      
      if (ausleiheVorhanden) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Der Mitarbeiter "+
        this.getBenutzer().getName()+
        " kann nicht gelöscht werden, da noch Ausleihen dieses Mitarbeiters "+
        "existieren.");
      }

      // Internet
      result = (ResultSet) statement.executeQuery(
        "select count(id) from internet_zugang where "+
        "MitarbeiterId="+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Der Mitarbeiter "+
        this.getBenutzer().getName()+
        " kann nicht gelöscht werden, da noch Internetfreigaben dieses "+
        "Mitarbeiters existieren.");
      }

      // Mitarbeiter löschen
      statement.execute("delete from einstellung where "+
        "MitarbeiterId=\""+this.getId()+"\"");
      statement.execute("delete from mitarbeiter where "+
        "id=\""+this.getId()+"\"");

      MysqlDatenbank.getMysqlInstance().endTransaktion();
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen des folgenden "+
        "Mitarbeiters:\n\n"+this.toDebugString(), true);
    }
  }

}