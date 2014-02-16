package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMahnungFactory;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.framework.ErrorHandler;


public class MysqlMahnungFactory extends AbstractMahnungFactory {

  public BenutzerListe getAlleBenutzerMitMahnung() {
    BenutzerFactory benutzerFactory = Datenbank.getInstance().getBenutzerFactory();
    
    BenutzerListe liste = new BenutzerListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
        "select DISTINCT b.id from ausleihzeitraum z, ausleihe a, benutzer b " +
        "where z.ausleiheID = a.id and a.benutzerId = b.id and " +
        "isNull(a.rueckgabedatum) group by a.id " +
        "having to_days(max(Ende)) < to_days(now());");
      while (result.next()) {
        try {
          int id = result.getInt("id");
          liste.addNoDuplicate(benutzerFactory.get(id));
        } catch (DatenNichtGefundenException e) {
          ErrorHandler.getInstance().handleException(e, false);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, false);
        }
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Benutzerliste!", true);
    }

    return liste;
  }
}
