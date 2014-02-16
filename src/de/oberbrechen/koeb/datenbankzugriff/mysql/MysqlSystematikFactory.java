package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractSystematikFactory;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlSystematikFactory extends AbstractSystematikFactory {

  Hashtable<String, Systematik> nameHashtable = new Hashtable<String, Systematik>();
    
  public Systematik erstelleNeu() {
    return new MysqlSystematik();
  }

 
  public Systematik ladeAusDatenbank(int id) throws DatenNichtGefundenException {
    return new MysqlSystematik(id);
  }


  public SystematikListe getAlleSystematiken() {
    clearCache();    
    return ladeSystematikListe("select * from systematik;");
  }

  public SystematikListe getAlleHauptSystematiken() {
    return ladeSystematikListe("select * from systematik where isNull(SpezialisiertID);");
  }  
      
  @SuppressWarnings("null")
  public Systematik get(String name) throws DatenNichtGefundenException, DatenbankInkonsistenzException {    
    Systematik erg = nameHashtable.get(name);
    if (erg != null) return erg;
    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select id from systematik where name=\""+name+"\"");
      boolean gefunden = result.next();
      if (!gefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException("Es konnte keine Systematik " +
            "mit dem Namen '"+name+"' gefunden werden");
      }
      
      try {
        erg = (Systematik) get(result.getInt("id"));
      } catch (DatenNichtGefundenException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      }
      nameHashtable.put(name, erg);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Systematik mit dem Namen '"+name+"'!", true);    
    }
    
    if (ladeAusCache(erg.getId()) == null)
      cache.put(new Integer(erg.getId()), erg);
    return erg;
  }
  
  public SystematikListe ladeSystematikListe(String sqlQuery) {
    SystematikListe liste = new SystematikListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(sqlQuery);
      while (result.next()) {
        int id = result.getInt("id");        
        Systematik systematik = (Systematik) ladeAusCache(id);
        if (systematik == null) {           
          systematik = new MysqlSystematik(result);
          cache.put(new Integer(systematik.getId()), systematik);
        }
        liste.addNoDuplicate(systematik);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Systematikliste!", true);
    }

    return liste;
  }

  
  public void loescheAlleSystematiken() {
    try {
      clearCache();
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();

      statement.execute("delete from medium_gehoert_zu_systematik");
      statement.execute("update systematik set spezialisiertID=null");
      statement.execute("delete from systematik");

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Löschen!", true);
    }
  }

  public void clearCache() {
    super.clearCache();
    nameHashtable.clear();
  }

}