package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractEANZuordnung;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlEANZuordnung extends AbstractEANZuordnung {

  protected Medium sucheMedium(EAN ean) {
    Medium erg = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
        
      //Internet_zugang
      ResultSet result = (ResultSet) statement.executeQuery(
          "select mediumID from medium_ean where EAN = \""+ean+"\"");
      if (result.next()) {
        try {
          erg = (Medium) mediumFactory.get(result.getInt(1));
        } catch (DatenNichtGefundenException e) {
          MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
          MysqlDatenbank.getMysqlInstance().endTransaktion();
          ErrorHandler.getInstance().handleException(e, false);
          return null;
        } catch (DatenbankInkonsistenzException e) {
          MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
          MysqlDatenbank.getMysqlInstance().endTransaktion();
          ErrorHandler.getInstance().handleException(e, false);
          return null;
        }
      }
          
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Suchen eines Mediums für eine EAN!", true);
    }
    return erg;
  }

  protected Medium sucheMedium(ISBN isbn) {
    Medium erg = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
        
      //Internet_zugang
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from medium where isbn = \""+isbn.getISBN()+"\"");
      if (result.next()) {
        try {
          erg = new MysqlMedium(result);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, false);
        }
      }
      if (result.next()) erg = null;
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Suchen eines Mediums für eine EAN!", true);
    }
    return erg;
  }
}