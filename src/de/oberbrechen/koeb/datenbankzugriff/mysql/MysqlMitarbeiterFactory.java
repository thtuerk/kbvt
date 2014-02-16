package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMitarbeiterFactory;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlMitarbeiterFactory extends AbstractMitarbeiterFactory {

  public Mitarbeiter erstelleNeu(Benutzer benutzer) {
    return new MysqlMitarbeiter(benutzer);
  }

  public MitarbeiterListe getAlleMitarbeiter() {
    cache.clear();
    MitarbeiterListe liste = new MitarbeiterListe();

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from mitarbeiter;");
          
      while (result.next()) {
        try {
          Mitarbeiter neuMitarbeiter = new MysqlMitarbeiter(result);
          cache.put(new Integer(neuMitarbeiter.getId()),
            neuMitarbeiter);
          liste.add(neuMitarbeiter);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Datenbankinkonsistenz " +
              "beim Laden der Mitarbeiterliste entdeckt!", false);
        }
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e,
        "Fehler beim Laden der Mitarbeiterliste!", true);
    }

    return liste;
  }

  public int sucheBenutzer(int id) throws DatenNichtGefundenException {
    int erg = 0;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select ID from mitarbeiter where "+
          "benutzerID=\""+id+"\"");

      boolean mitarbeiterGefunden = result.next();
      if (!mitarbeiterGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        
        throw new DatenNichtGefundenException(
        "Ein Mitarbeiter mit der BenutzerID '"+id+"' existiert "+
        "nicht.");
      }
      
      erg = result.getInt(1);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Suchen des Mitarbeiters mit"+
        " der Benutzernummer '"+id+"'!", true);
    }

    return erg;
  }

  public int sucheMitarbeiterBenutzername(String benutzername) throws DatenNichtGefundenException {
    int erg = 0;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select ID from mitarbeiter where "+
          "Benutzername=\""+benutzername+"\"");

      boolean mitarbeiterGefunden = result.next();
      if (!mitarbeiterGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();        
        throw new DatenNichtGefundenException(
          "Ein Mitarbeiter mit dem Benutzernamen '"+benutzername+"' existiert "+
          "nicht.");
      }

      erg = result.getInt(1);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Suchen des " +        "Mitarbeiters mit dem Benutzernamen '"+benutzername+"'!", true);
    }
    return erg;
  }

  public Mitarbeiter ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlMitarbeiter(id);
  }

}