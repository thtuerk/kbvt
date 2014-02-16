package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractVeranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Termin;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungsgruppeFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlVeranstaltung extends AbstractVeranstaltung {
  
  //Speichert, ob die Termine schon geladen wurden
  private boolean termineGeladen = false;

  public MysqlVeranstaltung() {
  }
  
  public MysqlVeranstaltung(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException {
    if (istGespeichert()) return;
    loadTermine();

    if (this.getTitel() == null || this.getTitel().trim().equals("") || 
        this.getVeranstaltungsgruppe() == null) {
      throw new UnvollstaendigeDatenException("Titel und Veranstaltungsgruppe jeder "+
        "Veranstaltung müssen eingegeben sein.");
    }

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into veranstaltung "+
          "SET titel = ?, kurzTitel = ?, "+
          "ansprechpartner = ?, beschreibung = ?, kosten = ?, " +
          "bezugsgruppe = ?, veranstaltungsgruppeID = ?, waehrung = ?, "+
          "anmeldung_erforderlich = ?, maximaleTeilnehmeranzahl = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update veranstaltung "+
          "SET titel = ?, kurzTitel = ?, "+
          "ansprechpartner = ?, beschreibung = ?, kosten = ?, " +
          "bezugsgruppe = ?, veranstaltungsgruppeID = ?, waehrung = ?, "+
          "anmeldung_erforderlich = ?, maximaleTeilnehmeranzahl = ? "+
          "where id="+getId());
      }

      statement.setString(1, this.getTitel());
      statement.setString(2, this.getKurzTitel());
      statement.setString(3, this.getAnsprechpartner());
      statement.setString(4, this.getBeschreibung());
      statement.setDouble(5, this.getKosten());
      statement.setString(6, this.getBezugsgruppe());
      statement.setInt(7, this.getVeranstaltungsgruppe().getId());
      statement.setString(8, this.getWaehrung());
      statement.setBoolean(9, this.istAnmeldungErforderlich());
      statement.setInt(10, this.getMaximaleTeilnehmerAnzahl());

      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }

      //Termine
      Statement termineLoeschStatement = MysqlDatenbank.getMysqlInstance().
        getStatement();
      termineLoeschStatement.execute("delete from termin " +
        "where veranstaltungID = "+getId());
      MysqlDatenbank.getMysqlInstance().releaseStatement(
        termineLoeschStatement);

      PreparedStatement insertStatement = (PreparedStatement) 
        connection.prepareStatement(
        "insert into termin SET veranstaltungID=?, Beginn=?, Ende=?");
      insertStatement.setInt(1, getId());
       
      for (Termin termin : termine) {
        insertStatement.setTimestamp(2, 
          DatenmanipulationsFunktionen.utilDate2sqlTimestamp(termin.getBeginn()));
        insertStatement.setTimestamp(3, 
          DatenmanipulationsFunktionen.utilDate2sqlTimestamp(termin.getEnde()));
        insertStatement.addBatch();
      }
      insertStatement.executeBatch();

      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "des folgenden Clients:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
   }

  public void loesche() throws DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      
      //Teilnahmen
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from benutzer_besucht_veranstaltung where "+
          "VeranstaltungID = "+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();        
        throw new DatenbankInkonsistenzException("Die Veranstaltung "+
          this.getTitel()+" ("+this.getId()+")\nkann nicht gelöscht werden, " +
          "da noch\nTeilnahmen an dieser Veranstaltung existieren.");
      }

      //Löschen
      statement.execute("delete from veranstaltung where "+
        "id = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen der "+
        "Veranstaltung "+this.toDebugString(), true);
    }
  }
  
  MysqlVeranstaltung(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    titel = result.getString("titel");
    kurzTitel = result.getString("kurzTitel");
    ansprechpartner = result.getString("ansprechpartner");
    bezugsgruppe = result.getString("bezugsgruppe");
    kosten = result.getDouble("kosten");
    beschreibung = result.getString("beschreibung");
    anmeldungErforderlich = result.getBoolean("anmeldung_erforderlich");
    waehrung = result.getString("waehrung");
    maxTeilnehmerAnzahl = result.getInt("maximaleTeilnehmerAnzahl");

    //Veranstaltungsgruppe laden
    int veranstaltungsGruppeID = result.getInt("veranstaltungsgruppeID");
    try {
      VeranstaltungsgruppeFactory veranstaltungsgruppeFactory =
        Datenbank.getInstance().getVeranstaltungsgruppeFactory();
      veranstaltungsgruppe = (Veranstaltungsgruppe) 
      veranstaltungsgruppeFactory.get(veranstaltungsGruppeID);
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Veranstaltung mit der "+
          "ID "+id+" verweist auf die unbekannte "+
          "Veranstaltungsgruppe-ID "+veranstaltungsGruppeID+"!");
    }
  }
  
  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = statement.executeQuery(
          "select * from veranstaltung where id = \"" + id + "\"");
      boolean veranstaltungGefunden = result.next();
      if (!veranstaltungGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
            "Die Veranstaltung mit der ID "+id+" existiert nicht!");
      }

      try {
        load(result);
      } catch (DatenbankInkonsistenzException e) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw e;
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
        "Veranstaltung mit der Nummer "+id+"!", true);
    }    

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public TerminListe getTermine() {
    loadTermine();
    return super.getTermine();
  }

  private void loadTermine() {
    if (termineGeladen) return;
    try {
      termineGeladen = true;
      //Termine laden
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      termine = new TerminListe();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from termin where veranstaltungID="+id);
      while (result.next()) {
        Date beginn = result.getTimestamp("Beginn");
        Date ende = result.getTimestamp("Ende");
        termine.addNoDuplicate(new Termin(beginn, ende));
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);    
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
          "Termine!", true);
    }
  }

}
