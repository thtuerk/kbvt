package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractAusleiheFactory;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlAusleiheFactory extends AbstractAusleiheFactory {

  public Ausleihe erstelleNeu() {
    return new MysqlAusleihe();
  }

  private AusleihenListe ladeAusAusleiheTabelle(String sqlQuery) throws DatenbankInkonsistenzException {
    AusleihenListe liste = new AusleihenListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(sqlQuery);
      try { 
        while (result.next()) {
          Ausleihe neueAusleihe = new MysqlAusleihe(result);
          liste.addNoDuplicate(neueAusleihe);
        }
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Ausleihenliste!", true);
    }
    
    return liste;
  }

  @SuppressWarnings("null") //laden liefert nie NULL
  public AusleihenListe getAlleAusleihen() {
    AusleihenListe liste = null;
    try {
      liste = ladeAusAusleiheTabelle("select * from ausleihe");
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Ausleihenliste!", true);
    }
    clearCache();
    for (Ausleihe neueAusleihe : liste) {
      cache.put(new Integer(neueAusleihe.getId()), neueAusleihe);
    }
    return liste;
  }

  public AusleihenListe getAlleAusleihenVon(Medium medium) throws DatenbankInkonsistenzException {
    return ladeAusAusleiheTabelle(
        "select * from ausleihe where mediumID = "+medium.getId());
  }

  public Ausleihe ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlAusleihe(id);
  }

  public AusleihzeitraumListe getGetaetigteAusleihzeitraeumeInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException {
    AusleihzeitraumListe liste = new AusleihzeitraumListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      try {
        PreparedStatement statement = 
          (PreparedStatement) MysqlDatenbank.getMysqlInstance().getConnection().
            prepareStatement("select DISTINCT a.* " +
               "from ausleihzeitraum z left join ausleihe a on " +
               "z.ausleiheID = a.id " +
               "where z.taetigungsdatum >= ? AND z.taetigungsdatum < ?");
        statement.setDate(1, DatenmanipulationsFunktionen.utilDate2sqlDate(
                zeitraum.getBeginn()));
        statement.setDate(2, DatenmanipulationsFunktionen.utilDate2sqlDate(
                zeitraum.getEnde()));
        ResultSet result = (ResultSet) statement.executeQuery();

        while (result.next()) {
          Ausleihe neueAusleihe = new MysqlAusleihe(result);
          
          for (Ausleihzeitraum ausleihzeitraum : neueAusleihe.getAusleihzeitraeume()) {
            if (!ausleihzeitraum.getTaetigungsdatum().before(zeitraum.getBeginn()) &&
                zeitraum.getEnde().after(ausleihzeitraum.getTaetigungsdatum())) {              
              liste.addNoDuplicate(ausleihzeitraum);
            }
          }
        }
      } catch (Exception e) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Ausleihzeitraumliste!", true);
    }

    return liste;
  }
  
  public int getAnzahlGetaetigteAusleihzeitraeumeInZeitraum(Zeitraum zeitraum) {
    int erg = 0;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      PreparedStatement statement = 
        (PreparedStatement) MysqlDatenbank.getMysqlInstance().getConnection().
          prepareStatement("select count(DISTINCT a.id) " +
             "from ausleihzeitraum z left join ausleihe a on " +
             "z.ausleiheID = a.id " +
             "where z.taetigungsdatum >= ? AND z.taetigungsdatum <= ?");
      statement.setDate(1, DatenmanipulationsFunktionen.utilDate2sqlDate(
              zeitraum.getBeginn()));
      statement.setDate(2, DatenmanipulationsFunktionen.utilDate2sqlDate(
              zeitraum.getEnde()));
      ResultSet result = (ResultSet) statement.executeQuery();
      result.next();
      erg = result.getInt(1);
      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Ausleihzeitraumliste!", true);
    }

    return erg;
  }  

  public AusleihenListe getAlleAktuellenAusleihenVon(Benutzer benutzer, Date datum) throws DatenbankInkonsistenzException {
    return ladeAusAusleiheTabelle(
      "select * from ausleihe where benutzerID = "+benutzer.getId() +
      " AND (isNull(rueckgabeDatum) OR rueckgabeDatum >= '"+
      MysqlDatenbank.sqldateFormat.format(datum)+"');");
  }

  public AusleihenListe getAlleAusleihenVon(Benutzer benutzer) throws DatenbankInkonsistenzException {
    return ladeAusAusleiheTabelle(
      "select * from ausleihe where benutzerID = "+benutzer.getId());
  }
  
  public AusleihenListe getAlleNichtZurueckgegebenenAusleihenVon(
      Benutzer benutzer) throws DatenbankInkonsistenzException {
    return ladeAusAusleiheTabelle(
        "select * from ausleihe where benutzerID = "+benutzer.getId() +
        " AND isNull(rueckgabeDatum);");
  }
  
  public AusleihenListe getAlleNichtZurueckgegebenenAusleihenVon(
      Benutzer benutzer, Date datum) throws DatenbankInkonsistenzException {
    return ladeAusAusleiheTabelle(
        "select * from ausleihe where benutzerID = "+benutzer.getId() +
        " AND (isNull(rueckgabeDatum) OR rueckgabeDatum > '"+
        MysqlDatenbank.sqldateFormat.format(datum)+"');");
  }
  
  public Zeitraum getAusleihenZeitraum() {
    Date beginn = null;
    Date ende = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement(); 
      
      ResultSet result = (ResultSet) statement.executeQuery("select min(taetigungsdatum), max(taetigungsdatum) from ausleihzeitraum");
      result.next();
      beginn = result.getDate(1);
      ende = result.getDate(2);
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Ausleihzeitraumliste!", true);
    }
    if (beginn==null) beginn=new Date();
    if (ende==null) ende=new Date();
    
    return new Zeitraum(beginn, ende);
  }  
}