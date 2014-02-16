package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractEinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.EinstellungenListe;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlEinstellungFactory extends AbstractEinstellungFactory {

  public EinstellungenListe getAlleEinstellungen() {
    clearCache();
    EinstellungenListe liste = new EinstellungenListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from einstellung;");
      while (result.next()) {
        try {
          Einstellung einstellung = new MysqlEinstellung(result);
    
          cache.put(new Integer(einstellung.getId()), einstellung);
          liste.addNoDuplicate(einstellung);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Inkonsistenz beim " +
              "Laden der Einstellungsliste entdeckt!", false);
        }
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Einstellungenliste!", true);
    }
  
    return liste;
  }
  
  public Liste<String> getAlleEinstellungenNamen() {
    Liste<String> liste = new Liste<String>();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
        "select name from einstellung group by name order by name;");
      while (result.next()) {
        String name = result.getString(1);
        liste.addNoDuplicate(name);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Einstellungsnamenliste!", true);
    }
    
    return liste;
  }
  
  public Liste<String> getBenutzeWerte(String einstellungsname) {
    Liste<String> liste = new Liste<String>();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
      "select wert from einstellung where name='"+einstellungsname+"'");
      while (result.next()) {
        String wert = result.getString(1);
        if (wert != null) liste.addNoDuplicate(wert);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Einstellungsnamenliste!", true);
    }
    
    return liste;
  }
  
  
  protected Einstellung getEinstellungIntern(
      Client client, Mitarbeiter mitarbeiter, String name) {
    Einstellung erg = null;
    try {
      StringBuffer sqlStatement = new StringBuffer();
      sqlStatement.append("select id from einstellung where ");
      if (client == null) {
        sqlStatement.append("isNull(clientID) and ");        
      } else {
        sqlStatement.append("clientID = "+client.getId()+" and ");        
      }
      if (mitarbeiter == null) {
        sqlStatement.append("isNull(mitarbeiterID) and ");        
      } else {
        sqlStatement.append("mitarbeiterID = "+mitarbeiter.getId()+" and ");        
      }
      sqlStatement.append("name=\""+name+"\"");        
      

      MysqlDatenbank.getMysqlInstance().beginTransaktion();      
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = 
        (ResultSet) statement.executeQuery(sqlStatement.toString());
      if (result.next()) {
        int einstellungID = result.getInt(1);
        try {
          erg = new MysqlEinstellung(einstellungID);
        } catch (DatenbankInkonsistenzException e) {
          MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
          MysqlDatenbank.getMysqlInstance().endTransaktion();
          ErrorHandler.getInstance().handleException(e, "Inkonsistenz beim "+
              "Laden der Einstellung "+einstellungID+"!", false);
          return null;
        } catch (DatenNichtGefundenException e) {
          ErrorHandler.getInstance().handleException(e, true);
        }
      } else {
        //erg = null; gilt sowieso
      }

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Einstellung!", true);
    }
  
    return erg;
  }
  
  public Einstellung ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlEinstellung(id);
  }

  public Einstellung erstelleNeu(Client client, Mitarbeiter mitarbeiter, String name) {
    return new MysqlEinstellung(client, mitarbeiter, name);
  }

  public boolean istClientEinstellung(String name) {
    int anzahl = 0;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
      "select count(*) from einstellung where name = '"+name+"' and !isNull(clientID)");
      result.next();
      anzahl = result.getInt(1);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    
    return anzahl > 0;
  }

  public boolean istMitarbeiterEinstellung(String name) {
    int anzahl = 0;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from einstellung where name = '"+name+"' and !isNull(mitarbeiterID)");
      result.next();
      anzahl = result.getInt(1);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    
    return anzahl > 0;
  }

}