package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractBemerkungVeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.BemerkungVeranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BemerkungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlBemerkungVeranstaltungFactory extends AbstractBemerkungVeranstaltungFactory {

  public BemerkungVeranstaltung erstelleNeu() {
    return new MysqlBemerkungVeranstaltung();
  }


  protected BemerkungenListe ladeAusTabelle(
      String sqlStatement) throws DatenbankInkonsistenzException {
    BemerkungenListe liste = new BemerkungenListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(sqlStatement);
      while (result.next()) {
        BemerkungVeranstaltung bemerkung = 
          new MysqlBemerkungVeranstaltung(result);

        cache.put(new Integer(bemerkung.getId()), bemerkung);
        liste.addNoDuplicate(bemerkung);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Bemerkungenliste!", true);
    }

    return liste;
  }

  public BemerkungenListe getAlleVeranstaltungsbemerkungen() throws DatenbankInkonsistenzException {
    return ladeAusTabelle("select * from bemerkung_veranstaltung");
  }

  public BemerkungenListe getAlleVeranstaltungsbemerkungen(Veranstaltung veranstaltung) throws DatenbankInkonsistenzException {
    return ladeAusTabelle("select * from bemerkung_veranstaltung "+ 
        "where veranstaltungID = "+veranstaltung.getId());
  }
  
  public BemerkungenListe getAlleVeranstaltungsgruppenbemerkungen(Veranstaltungsgruppe gruppe) throws DatenbankInkonsistenzException {
    return ladeAusTabelle("select bemerkung_veranstaltung.* from bemerkung_veranstaltung left join " +
        "veranstaltung on bemerkung_veranstaltung.veranstaltungID = veranstaltung.ID where " +
        "veranstaltung.VeranstaltungsgruppeID = "+gruppe.getId());
  }

  public BemerkungVeranstaltung ladeAusDatenbank(int id)
      throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlBemerkungVeranstaltung(id);
  }

}