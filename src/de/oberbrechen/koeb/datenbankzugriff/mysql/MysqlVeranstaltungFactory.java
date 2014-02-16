package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractVeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Termin;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlVeranstaltungFactory extends AbstractVeranstaltungFactory {
    
  public Veranstaltung erstelleNeu() {
    return new MysqlVeranstaltung();
  }

  public Veranstaltung ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlVeranstaltung(id);
  }

  public VeranstaltungenListe getVeranstaltungen(
      Veranstaltungsgruppe veranstaltungsgruppe) {
    return getVeranstaltungen(veranstaltungsgruppe, false);
  }
  
  private VeranstaltungenListe getVeranstaltungen(
    Veranstaltungsgruppe veranstaltungsgruppe, 
    boolean anmeldung_erforderlich) {
    String sqlStatement = "select * from veranstaltung " +
        "where veranstaltungsgruppeid = "+veranstaltungsgruppe.getId();
      if (anmeldung_erforderlich)
        sqlStatement = sqlStatement + " and anmeldung_erforderlich > 0";
    return ladeAusVeranstaltungTabelle(sqlStatement);
  }

  private VeranstaltungenListe ladeAusVeranstaltungTabelle(
      String sqlStatement) {
    VeranstaltungenListe liste = new VeranstaltungenListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      
      ResultSet result = statement.executeQuery(sqlStatement);
      while (result.next()) {        
        try {
          int id = result.getInt("id");
          Veranstaltung veranstaltung = (Veranstaltung) ladeAusCache(id);        
          
          if (veranstaltung == null) { 
            try {
              veranstaltung = new MysqlVeranstaltung(result);
            } catch (DatenbankInkonsistenzException e) {
              MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
              MysqlDatenbank.getMysqlInstance().endTransaktion();
              throw e;
            }
            cache.put(new Integer(veranstaltung.getId()), veranstaltung);
          }
          liste.addNoDuplicate(veranstaltung);
        } catch (DatenbankzugriffException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der " +
              "Veranstaltungsliste!", false);          
        }
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Veranstaltungenliste!\n"+sqlStatement, true);
    }

    return liste;
  }
  
  public TerminListe getUeblicheUhrzeiten() {
    TerminListe termine = new TerminListe();
    try {
      //Termine laden
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select *, count(*) as a from termin group by hour(beginn), minute(beginn), hour(ende), minute(ende) order by a DESC limit 5;");
      while (result.next()) {
        Date beginn = result.getTimestamp("Beginn");
        Date ende = result.getTimestamp("Ende");
        termine.addNoDuplicate(new Termin(beginn, ende));
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement); 
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
          "üblichen Uhrzeiten!", true);
    }

    return termine;    
  }
  public VeranstaltungenListe getVeranstaltungenMitAnmeldung(
      Veranstaltungsgruppe veranstaltungsgruppe) {
    return getVeranstaltungen(veranstaltungsgruppe, true);
  }

  public VeranstaltungenListe getAlleVeranstaltungenInZeitraum(Zeitraum zeitraum) {
    StringBuffer sqlStament = new StringBuffer();
    sqlStament.append("select DISTINCT v.* from veranstaltung v, termin t " +
        "where (t.veranstaltungid = v.id) and (t.beginn >= '");
    sqlStament.append(MysqlDatenbank.sqldateFormat.format(zeitraum.getBeginn()));
    sqlStament.append("' AND t.beginn < '");
    sqlStament.append(MysqlDatenbank.sqldateFormat.format(zeitraum.getEnde()));
    sqlStament.append("')");
    return ladeAusVeranstaltungTabelle(sqlStament.toString());
  }
}
