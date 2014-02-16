package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractVeranstaltungsgruppeFactory;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsgruppenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlVeranstaltungsgruppeFactory 
  extends AbstractVeranstaltungsgruppeFactory {

  public VeranstaltungsgruppenListe getAlleVeranstaltungsgruppen() {
    clearCache();
    VeranstaltungsgruppenListe liste = new VeranstaltungsgruppenListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from veranstaltungsgruppe;");
      while (result.next()) {

        MysqlVeranstaltungsgruppe veranstaltungsgruppe = 
          new MysqlVeranstaltungsgruppe(result);

        cache.put(new Integer(veranstaltungsgruppe.getId()), 
          veranstaltungsgruppe);
        liste.addNoDuplicate(veranstaltungsgruppe);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Veranstaltungsgruppenliste!", true);
    }

    return liste;
  }

  public Veranstaltungsgruppe erstelleNeu() {
    return new MysqlVeranstaltungsgruppe();
  }

  public Veranstaltungsgruppe ladeAusDatenbank(int id) throws DatenNichtGefundenException {
    return new MysqlVeranstaltungsgruppe(id);
  }
}