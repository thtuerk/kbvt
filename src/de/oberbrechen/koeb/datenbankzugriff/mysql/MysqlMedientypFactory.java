package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedientypListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlMedientypFactory extends AbstractMedientypFactory {

  public Medientyp get(String name) throws DatenbankInkonsistenzException, DatenNichtGefundenException {
    Medientyp erg = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select id from medientyp where name=\""+name+"\"");
      boolean gefunden = result.next();
      if (!gefunden) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();       
        throw new DatenNichtGefundenException("Es konnte kein Medientyp " +
            "mit dem Namen '"+name+"' gefunden werden");
      }

      try {
        erg = (Medientyp) get(result.getInt("id"));
      } catch (DatenNichtGefundenException e) {
        //Sollte nie auftreten
        MysqlDatenbank.getMysqlInstance().endTransaktion();       
        throw e;
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();       
        throw e;
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden des Medientyps mit dem Namen '"+name+"'!", true);    
    }
    return erg;
  }
  
  public Medientyp getMeistBenutztenMedientyp() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
        "select count(*) as anzahl, medientypID from medium where isNull(aus_Bestand_entfernt) " +
        "group by medientypID order by anzahl DESC limit 1");
      boolean medientypGefunden = result.next();
      if (!medientypGefunden) { 
        result = (ResultSet) statement.executeQuery(
          "select id as medientypID from medientyp limit 1");
        medientypGefunden = result.next();
      }
      if (!medientypGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException("Es konnte kein Medientyp gefunden werden!");                         
      }
      int id = result.getInt("medientypID");
      
      try {
        Medientyp erg = (Medientyp) get(id);
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        return erg;
      } catch (DatenNichtGefundenException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      }
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
      "Fehler beim Laden des meist benutzten Medientys!", true);
    }
    return null;
  }

  public MedientypListe getAlleMedientypen() {
    clearCache();
    MedientypListe liste = new MedientypListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from medientyp;");
      while (result.next()) {
        Medientyp medientyp = new MysqlMedientyp(result);

        cache.put(new Integer(medientyp.getId()), medientyp);
        liste.addNoDuplicate(medientyp);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Medientypliste!", true);
    }

    return liste;
  }

  public Medientyp erstelleNeu() {
    return new MysqlMedientyp();
  }

  public Medientyp ladeAusDatenbank(int id) throws DatenNichtGefundenException {
    return new MysqlMedientyp(id);
  }

}