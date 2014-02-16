package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractBenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlBenutzerFactory extends AbstractBenutzerFactory {

  private String standardBenutzerwohnort = null;

  public Benutzer erstelleNeu() {
    return new MysqlBenutzer();
  }

  public String getStandardBenutzerwohnort() {
    if (standardBenutzerwohnort != null) return standardBenutzerwohnort;
    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) as anzahl, ort from benutzer group by ort order" +
          " by anzahl DESC limit 1");
      boolean ortGefunden = result.next();
      if (!ortGefunden) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);        
        return null;
      }
      String erg = result.getString("ort");

      MysqlDatenbank.getMysqlInstance().endTransaktion();
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      standardBenutzerwohnort = erg;
      return erg;
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Bestimmen des Standardwohnorts!", true);
    }
    return null;
  }

  protected BenutzerListe ladeBenutzerliste(String sqlStatement) {
    BenutzerListe liste = new BenutzerListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(sqlStatement);
      while (result.next()) {

        int benutzerId = result.getInt("id");
        Benutzer neuBenutzer = (Benutzer) ladeAusCache(benutzerId) ;
        if (neuBenutzer == null) {
          neuBenutzer = new MysqlBenutzer(result);
          cache.put(new Integer(neuBenutzer.getId()), neuBenutzer);
        }

        liste.addNoDuplicate(neuBenutzer);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Benutzerliste!", true);
    }

    return liste;
  }

  
  public BenutzerListe getAlleAktivenBenutzer() {
    clearCache();
    return ladeBenutzerliste("select * from benutzer where aktiv=1;");
  }

  public Liste<String> getAlleOrte() {
    Liste<String> liste = new Liste<String>();

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select distinct ort from benutzer;");
      while (result.next()) {
        String ort = result.getString("ort");
        if (ort != null) liste.add(ort);
      }

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
        "Wohnortsliste der Benutzer!", true);
    }

    liste.setSortierung(Liste.StringSortierung, false);
    return liste;
  }

  public int sucheBenutzername(String benutzername) 
    throws DatenNichtGefundenException {
    int erg = 0;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
        "select id from benutzer where "+
        "Benutzername=\""+benutzername+"\"");

      boolean benutzerGefunden = result.next();
      if (!benutzerGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
          "Ein Benutzer mit dem Benutzernamen '"+benutzername+"' existiert "+
          "nicht.");
      }
      
      erg = result.getInt(1);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Suchen des " +        "Benutzers mit dem Benutzernamen '"+benutzername+"'!", true);
    }

    return erg;
  }

  public Benutzer ladeAusDatenbank(int id) throws DatenNichtGefundenException {
    return new MysqlBenutzer(id);
  }

  public BenutzerListe getAlleBenutzer() {
    clearCache();
    return ladeBenutzerliste("select * from benutzer;");
  }

  public BenutzerListe getAllePassivenBenutzer() {
    clearCache();
    return ladeBenutzerliste("select * from benutzer where aktiv=0;");
  }
}