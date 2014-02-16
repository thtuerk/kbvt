package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractVeranstaltungsteilnahmeFactory;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsteilnahme;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.IDFormat;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsteilnahmeListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlVeranstaltungsteilnahmeFactory 
  extends AbstractVeranstaltungsteilnahmeFactory {

  private BenutzerFactory benutzerFactory =
    MysqlDatenbank.getMysqlInstance().getBenutzerFactory();
  
  public Veranstaltungsteilnahme erstelleNeu(
      Benutzer benutzer, Veranstaltung veranstaltung) {
    return new MysqlVeranstaltungsteilnahme(benutzer, veranstaltung);
  }

  public Veranstaltungsteilnahme getVeranstaltungsteilnahme(
      Benutzer benutzer, Veranstaltung veranstaltung) throws DatenbankInkonsistenzException {

    Veranstaltungsteilnahme erg = null;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();            
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = 
        (PreparedStatement) connection.prepareStatement(
            "select id from benutzer_besucht_veranstaltung where " +
            "benutzerID = ? and veranstaltungID = ?");
      statement.setInt(1, benutzer.getId());
      statement.setInt(2, veranstaltung.getId());
      ResultSet result = (ResultSet) statement.executeQuery();      
            
      boolean gefunden = result.next();      
      if (gefunden) {
        try {
          erg = (Veranstaltungsteilnahme) get(result.getInt(1));
        } catch (DatenNichtGefundenException e) {
          //Sollte nicht auftreten
          ErrorHandler.getInstance().handleException(e, "Unerwarteter Fehler " +
              "beim Laden der Veranstaltungsteilnahme!", true);
        } catch (DatenbankInkonsistenzException e) {
          MysqlDatenbank.getMysqlInstance().endTransaktion();            
          throw e;
        }
      }
      MysqlDatenbank.getMysqlInstance().endTransaktion();            
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Suchen" +
        "der Teilnahme des Benutzers "+benutzer.getName()+" an der " +
        "Veranstaltung "+veranstaltung.getTitel()+"!", true);
    }
    return erg;
  }
  
  public int getTeilnehmerAnzahl(Veranstaltung veranstaltung) {
    return getTeilnahmenAnzahl("Status="+Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET+
        " and veranstaltungid = "+veranstaltung.getId());
  }  
  
  public int getWartelistenLaenge(Veranstaltung veranstaltung) {
    return getTeilnahmenAnzahl("(Status="+Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND+
        " or Status="+Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND+") and veranstaltungid = "+veranstaltung.getId());
  }  
  
  public int getBlockierendeWartelistenLaenge(Veranstaltung veranstaltung) {
    return getTeilnahmenAnzahl("Status="+Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND+
        " and veranstaltungid = "+veranstaltung.getId());
  }
  
  public int getTeilnahmenAnzahl(String bedingung) {
    int erg = 0;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = statement.executeQuery(
          "select count(*) from benutzer_besucht_veranstaltung " +
          "where "+bedingung);
      result.next();
      erg = result.getInt(1);
      
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Veranstaltungsteilnahmelist!", true);
    }
    
    return erg;
  }
  
  public int[] getTeilnehmerAnzahl(VeranstaltungenListe veranstaltungen) {
    int[] erg = new int[veranstaltungen.size()];
    if (veranstaltungen.isEmpty()) return erg;
    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select veranstaltungid, count(*) from benutzer_besucht_veranstaltung " +
          "where Status="+Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET+
          " and veranstaltungid IN ("+
          veranstaltungen.toKommaGetrenntenString(new IDFormat<Veranstaltung>())+
          ") group by veranstaltungid");
      while(result.next()) {
        int currentID = result.getInt(1);
        for (int i = 0; i < veranstaltungen.size(); i++) {                 
          if (((Veranstaltung) veranstaltungen.get(i)).getId() == currentID) {
            erg[i] = result.getInt(2);
            break;
          }
        }
      }
      
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Veranstaltungsteilnahmeliste für die" +
          "Veranstaltungen "+veranstaltungen.toKommaGetrenntenString()+"!", true);
    }    
    return erg;
  }
  
    
  public int getTeilnehmerAnzahl(Veranstaltungsgruppe veranstaltungsgruppe) {
    int erg = 0;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
        "select count(DISTINCT bbv.benutzerID) from "+
        "benutzer_besucht_veranstaltung as bbv left join veranstaltung as v "+
        "on bbv.veranstaltungID = v.id "+
        "where v.veranstaltungsgruppeID="+veranstaltungsgruppe.getId());
      result.next();
      erg = result.getInt(1);
      
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Bestimmen der Teilnehmeranzahl für die" +
        "Veranstaltungsgruppe "+veranstaltungsgruppe+"!", true);
    }

    return erg;        
  }

  public BenutzerListe getTeilnehmerListe(Veranstaltungsgruppe veranstaltungsgruppe) {
    BenutzerListe liste = new BenutzerListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
        "select DISTINCT bbv.benutzerid from "+
        "benutzer_besucht_veranstaltung as bbv left join veranstaltung as v "+
        "on bbv.veranstaltungID = v.id "+
        "where v.veranstaltungsgruppeID="+veranstaltungsgruppe.getId());
      while (result.next()) {
        int benutzerID = result.getInt("benutzerID");
        try {
          liste.add(benutzerFactory.get(benutzerID));
        } catch (DatenNichtGefundenException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
              "des Benutzers "+benutzerID+" für die Benutzerliste!", false);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
              "des Benutzers "+benutzerID+" für die Benutzerliste!", false);
        }
      }

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
        "TeilnehmerListe der Veranstaltungsgruppe "+
        veranstaltungsgruppe.getName()+"!", true);
    }
    return liste;
  }

  public Veranstaltungsteilnahme ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlVeranstaltungsteilnahme(id);
  }

  protected BenutzerListe getBenutzerListe(String bedingung) {
    BenutzerListe liste = new BenutzerListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select DISTINCT benutzerID from "+
          "benutzer_besucht_veranstaltung "+
          "where "+bedingung);      
      while (result.next()) {
        int benutzerID = result.getInt("benutzerID");
        try {
          liste.add(benutzerFactory.get(benutzerID));
        } catch (DatenNichtGefundenException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
              "des Benutzers "+benutzerID+" für die Benutzerliste!", false);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
              "des Benutzers "+benutzerID+" für die Benutzerliste!", false);
        }
      }      
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Bestimmen der Teilnehmerliste!", true);
    }
    return liste;        
  }

  public BenutzerListe getTeilnehmerListe(Veranstaltung veranstaltung) {
    return getBenutzerListe("veranstaltungid = "+veranstaltung.getId());
  }
    
  public int[][] getTeilnahmeArray(
    VeranstaltungenListe veranstaltungen, BenutzerListe benutzer) {

    int[][] erg = new int[veranstaltungen.size()][benutzer.size()];
    if (veranstaltungen.isEmpty() || benutzer.isEmpty()) return erg;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();            
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
            "select benutzerID, veranstaltungID, Status from " +
            "benutzer_besucht_veranstaltung where " +
            "benutzerID IN ("+benutzer.toKommaGetrenntenString(new IDFormat<Benutzer>())+
            ") and veranstaltungID IN ("+
            veranstaltungen.toKommaGetrenntenString(new IDFormat<Veranstaltung>())+
            ") order by benutzerID, veranstaltungID");
      
      for (int i = 0; i < veranstaltungen.size(); i++) {
        for (int j = 0; j < benutzer.size(); j++) {
          erg[i][j] = TEILNAHME_KEINE;
        }      
      }
      
      int benutzerPos = 0;
      int veranstaltungPos = 0;
      while (result.next()) {
        int benutzerId = result.getInt(1);
        int veranstaltungId = result.getInt(2);
        int status = result.getInt(3);
        boolean warteListe = 
          (status == Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND) ||
          (status == Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND);
        
        while (benutzerId != ((Benutzer) benutzer.get(benutzerPos)).getId()) {
          benutzerPos++;
          if (benutzerPos == benutzer.size()) benutzerPos = 0;
        }

        while (veranstaltungId != 
          ((Veranstaltung) veranstaltungen.get(veranstaltungPos)).getId()) {
          veranstaltungPos++;
          if (veranstaltungPos == veranstaltungen.size()) veranstaltungPos = 0;
        }
        
        erg[veranstaltungPos][benutzerPos] = warteListe?TEILNAHME_WARTELISTE:TEILNAHME_TEILNEHMERLISTE;
      }

      MysqlDatenbank.getMysqlInstance().endTransaktion();            
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
          "des Teilnahme-Arrays!", true);
    }
    
    return erg;           
  }
 
  protected VeranstaltungsteilnahmeListe getTeilnahmeListe(String bedingung) {
    VeranstaltungsteilnahmeListe liste = new VeranstaltungsteilnahmeListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from benutzer_besucht_veranstaltung " +
          "where "+bedingung);
      while (result.next()) {
        try {
          Veranstaltungsteilnahme teilnahme = 
            new MysqlVeranstaltungsteilnahme(result);
          
          cache.put(new Integer(teilnahme.getId()), teilnahme);
          liste.addNoDuplicate(teilnahme);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Inkonsistenz beim " +
              "Laden der Teilnehmerliste entdeckt!", false);
        }
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Veranstaltungsteilnahmeliste!", true);
    }

    return liste;
  }

  public VeranstaltungsteilnahmeListe getTeilnahmeListe(
      Veranstaltung veranstaltung) {
    return getTeilnahmeListe(
        "veranstaltungid = "+veranstaltung.getId());
  }
}