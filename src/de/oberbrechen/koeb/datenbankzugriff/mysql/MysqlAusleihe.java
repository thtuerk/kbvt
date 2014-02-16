package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import com.mysql.jdbc.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlAusleihe extends AbstractAusleihe {
  
  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    this.load(this.getId());
  }
  
  public void save() throws UnvollstaendigeDatenException, 
    MediumBereitsVerliehenException {
    if (istGespeichert) return;
        
    try {
      //Daten vollständig?
      if (getBenutzer() == null)
        throw new UnvollstaendigeDatenException("Der Benutzer "+
          "der Ausleihe ist nicht angegeben.");
      if (getAusleihdatum() == null)
        throw new UnvollstaendigeDatenException("Das Datum, an dem das Medium "+
          "ausgeliehen wurde, ist nicht angegeben.");
      if (getRueckgabedatum() != null && getMitarbeiterRueckgabe() == null)
        throw new UnvollstaendigeDatenException("Der Mitarbeiter, der das " +
          "Medium zurücknahm, ist nicht angegeben.");
      if (getMitarbeiterAusleihe() == null)
        throw new UnvollstaendigeDatenException("Der Mitarbeiter, der das " +
          "Medium herausgab, ist nicht angegeben.");
      if (getSollRueckgabedatum() == null)
        setSollRueckgabedatum(Buecherei.getInstance()
          .getAusleihordnung().getAusleihenBisDatum(medium, new Date()));

      
      //Teste ob Medium bereits verliehen ist
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      
      if (getMedium() != null) {
        PreparedStatement statement = 
          (PreparedStatement) connection.prepareStatement(
            "select ausleiheID, rueckgabeDatum " +
            "from ausleihzeitraum z left join ausleihe a " +
            "on a.id = z.ausleiheid where mediumID = ? AND " +
            "ausleiheID != ? " +
            "group by ausleiheid having " +
            "(isNull(rueckgabeDatum) OR rueckgabeDatum > ?) AND " +
            "(isNull(?) OR min(beginn) < ?)");
            
        statement.setInt(1, getMedium().getId());     
        statement.setInt(2, this.getId());     
        statement.setDate(3, DatenmanipulationsFunktionen.utilDate2sqlDate(
                this.getAusleihdatum()));     
        statement.setDate(4, DatenmanipulationsFunktionen.utilDate2sqlDate(
                this.getRueckgabedatum()));     
        statement.setDate(5, DatenmanipulationsFunktionen.utilDate2sqlDate(
                this.getRueckgabedatum()));  

        ResultSet result = (ResultSet) statement.executeQuery();
        boolean ausleiheGefunden = result.next();
        if (ausleiheGefunden) {
          MysqlDatenbank.getMysqlInstance().endTransaktion();          
          Ausleihe konfliktAusleihe = null;
          try {
            konfliktAusleihe = new MysqlAusleihe(result.getInt("ausleiheID"));
          } catch (Exception e) {
            //sollte nicht auftreten
            ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
                "der in Konflikt stehenden Ausleihe!", true);
          }
          throw new MediumBereitsVerliehenException(konfliktAusleihe, this);
        }          
      }
      
      
      //Eigentliches Speichern
      
      PreparedStatement statement = null;
      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into ausleihe set "+
          "MediumID = ?, BenutzerID = ?, RueckgabeDatum = ?, " +
          "MitarbeiterIDRueckgabe = ?, Bemerkungen = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update ausleihe set " +
          "MediumID = ?, BenutzerID = ?, RueckgabeDatum = ?," +
          "MitarbeiterIDRueckgabe = ?, Bemerkungen = ? "+
          "where id="+getId());
      }
      
      if (getMedium() == null) {
        statement.setObject(1, null);
      } else {
        statement.setInt(1, getMedium().getId());
      }
      statement.setInt(2, getBenutzer().getId());
      statement.setDate(3, 
          DatenmanipulationsFunktionen.utilDate2sqlDate(getRueckgabedatum()));
      statement.setDate(3, 
          DatenmanipulationsFunktionen.utilDate2sqlDate(getRueckgabedatum()));
      if (getMitarbeiterRueckgabe() == null) {
        statement.setObject(4, null);
      } else {
        statement.setInt(4, getMitarbeiterRueckgabe().getId());
      }      
      statement.setString(5, getBemerkungen());      
      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      
      //Zeiträume einfügen
      statement.execute("delete from ausleihzeitraum where "+
        "ausleiheId="+this.getId());
      
      statement = (PreparedStatement) connection.prepareStatement(
          "insert into ausleihzeitraum set "+
          "ausleiheID = ?, MitarbeiterID = ?, Beginn = ?, Ende = ?," +
          "Taetigungsdatum = ?");
      statement.setInt(1, getId());
      Iterator<Ausleihzeitraum> zeitraumIt = getAusleihzeitraeume().iterator();
      while (zeitraumIt.hasNext()) {
        Ausleihzeitraum zeitraum = (Ausleihzeitraum) zeitraumIt.next();
        statement.setInt(2, zeitraum.getMitarbeiter().getId());
        statement.setDate(3, DatenmanipulationsFunktionen.utilDate2sqlDate(
                zeitraum.getBeginn()));
        statement.setDate(4, DatenmanipulationsFunktionen.utilDate2sqlDate(
                zeitraum.getEnde()));
        statement.setDate(5, DatenmanipulationsFunktionen.utilDate2sqlDate(
                zeitraum.getTaetigungsdatum()));
        statement.addBatch();
      }
      statement.executeBatch();
      
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Speichern des folgenden "+
        "Benutzers:\n\n"+this.toDebugString(), true);
    }

    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }

  public MysqlAusleihe(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  public MysqlAusleihe() {
  }

  public void loesche() {
    if (this.istNeu()) return;

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();

      statement.execute("delete from ausleihzeitraum where "+
        "ausleiheId="+this.getId());
      statement.execute("delete from ausleihe where id="+this.getId());

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen " +
        "der folgenden Ausleihe:\n\n"+this.toDebugString(), true);
    }
  }

  
  MysqlAusleihe(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    bemerkungen = result.getString("bemerkungen");
    rueckgabedatum = result.getDate("rueckgabedatum");
    
    try {
      mitarbeiterRueckgabe = null;
      int mitarbeiterID = result.getInt("MitarbeiterIDRueckgabe");
      if (mitarbeiterID != 0) {
        MitarbeiterFactory mitarbeiterFactory = 
          MysqlDatenbank.getMysqlInstance().getMitarbeiterFactory();
        mitarbeiterRueckgabe = (Mitarbeiter) 
          mitarbeiterFactory.get(mitarbeiterID);
      }
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Ausleihe "+id+" verweist " +
          "auf einen nicht existenten Mitarbeiter!");
    }
    
    try {
      BenutzerFactory benutzerFactory = 
        MysqlDatenbank.getMysqlInstance().getBenutzerFactory();
      benutzer = (Benutzer) 
        benutzerFactory.get(result.getInt("BenutzerID"));
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Ausleihe "+id+" verweist " +
          "auf einen nicht existenten Benutzer!");
    }

    try {
      medium = null;
      int mediumID = result.getInt("MediumID");
      if (mediumID != 0) {
        MediumFactory mediumFactory = 
          MysqlDatenbank.getMysqlInstance().getMediumFactory();
        medium = (Medium) 
          mediumFactory.get(mediumID);
      }
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Die Ausleihe "+id+" verweist " +
          "auf ein nicht existentes Medium!");
    }
    
    //Ausleihzeiträume laden
    ausleihzeitraumListe = new AusleihzeitraumListe(); 
    ausleihzeitraumListe.setSortierung(AusleihzeitraumListe.taetigungsdatumSortierung);
    MysqlDatenbank.getMysqlInstance().beginTransaktion();
    Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
    ResultSet zeitRaumResult = (ResultSet) statement.executeQuery(
        "select * from ausleihzeitraum where ausleiheid = " + id);
      
    try {
      while (zeitRaumResult.next()) {
        ausleihzeitraumListe.add(loadAusleihzeitraum(zeitRaumResult, this));
      }
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (DatenNichtGefundenException e) {
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
      throw new DatenbankInkonsistenzException("Probleme beim Laden der "+
        "Ausleihzeiträume der Ausleihe "+id+"!");      
    }
    
  }
  
  /**
   * Läd einen Ausleihzeitraum aus dem übergebenen ResultSet. Dazu wird
   * die zugehörige Ausleihe geladen. 
   * @param result
   * @return den gelesenen Ausleihzeitraum
   * @throws DatenNichtGefundenException
   * @throws SQLException
   * @throws DatenbankInkonsistenzException
   */
  protected static Ausleihzeitraum loadAusleihzeitraum(ResultSet result) 
    throws DatenNichtGefundenException, DatenbankInkonsistenzException, SQLException {
    return loadAusleihzeitraum(result, null);
  }
      
  /**
   * Läd einen Ausleihzeitraum aus dem übergebenen ResultSet. Stimmt die ID der
   * ebenfalls übergebenen Ausleihe mit der AusleihID des Zeitraums überein,
   * so wird diese verwendet, ansonsten wird die Ausleihe neu erstellt. 
   * @param result
   * @param ausleihe
   * @return den gelesenen Ausleihzeitraum
   * @throws DatenbankInkonsistenzException
   */
  protected static Ausleihzeitraum loadAusleihzeitraum(ResultSet result,
        Ausleihe moeglicheAusleihe) throws DatenNichtGefundenException, SQLException, DatenbankInkonsistenzException {
    
    int ausleiheID = result.getInt("AusleiheID");
    int mitarbeiterID = result.getInt("MitarbeiterID");
    Mitarbeiter mitarbeiter = null;
    try {
      MitarbeiterFactory mitarbeiterFactory = 
        MysqlDatenbank.getMysqlInstance().getMitarbeiterFactory();
      mitarbeiter = (Mitarbeiter) mitarbeiterFactory.get(mitarbeiterID);
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Ein Zeitraum der Ausleihe "+
          ausleiheID+" verweist auf einen nicht eingetragenen Mitarbeiter!");
    }
    
    Date beginn = result.getDate("Beginn");
    Date ende = result.getDate("Ende");
    Date taetigungsDatum = result.getDate("Taetigungsdatum");
  
    Ausleihe ausleihe = null;
    if (moeglicheAusleihe != null && moeglicheAusleihe.getId() == ausleiheID) {
      ausleihe = moeglicheAusleihe;
    } else {
      ausleihe = (Ausleihe) 
        MysqlDatenbank.getMysqlInstance().getAusleiheFactory().get(ausleiheID);
    }
    
    return new Ausleihzeitraum(mitarbeiter,ausleihe, beginn, ende, taetigungsDatum);
  }

  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from ausleihe where id = " + id);
      boolean ausleiheGefunden = result.next();
      if (!ausleiheGefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
            "Eine Ausleihe mit der ID "+id+" existiert nicht!");
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
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "der Ausleihe mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }  
}
