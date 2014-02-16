package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMedium;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.SystematikFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.EANSchonVergebenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.EindeutigerSchluesselSchonVergebenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.MedienNrSchonVergebenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.EANListe;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlMedium extends AbstractMedium {

  boolean systematikenGeladen = false;
  boolean eansGeladen = false;

  public MysqlMedium(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    load(id);
  }

  MysqlMedium(ResultSet result) throws DatenbankInkonsistenzException, SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlMedium() {
  }
  
  public void reload() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException, EindeutigerSchluesselSchonVergebenException {
    if (istGespeichert()) return;
    loadEANs();
    loadSystematiken();
    
    if (this.getMedienNr() == null || this.getTitel() == null ||
        this.getMedientyp() == null)
      throw new UnvollstaendigeDatenException("Nummer, Titel und Typ jedes "+
        "Mediums müssen eingegeben sein.");

    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      
      Statement normalStatement = 
        MysqlDatenbank.getMysqlInstance().getStatement();

      //Mediennr schon vergeben?
      ResultSet result = (ResultSet) normalStatement.executeQuery(
          "select id from medium where Nr = \""+this.getMedienNr()+"\"");
      if (result.next() && 
          (this.istNeu() || this.getId() != result.getInt("id"))) {
        
        MediumFactory mediumFactory = 
          MysqlDatenbank.getMysqlInstance().getMediumFactory();
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        Medium konfliktMedium;
        try {
          konfliktMedium = (Medium) mediumFactory.get(result.getInt("id"));
        } catch (DatenbankzugriffException e) {
          konfliktMedium = null;
          ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
              "des Konfliktmediums!", false);
        }
        throw new MedienNrSchonVergebenException(konfliktMedium);          
      }
      
      //EAN schon benutzt
      for (EAN ean : getEANs()) {
        result = (ResultSet) normalStatement.executeQuery(
            "select mediumid from medium_ean where ean = \""+ean+"\"");
        if (result.next() && 
            (this.istNeu() || this.getId() != result.getInt("mediumid"))) {
        
          MediumFactory mediumFactory = 
            MysqlDatenbank.getMysqlInstance().getMediumFactory();
          MysqlDatenbank.getMysqlInstance().endTransaktion();
          Medium konfliktMedium;
          try {
            konfliktMedium = (Medium) mediumFactory.get(
              result.getInt("mediumid"));
          } catch (DatenbankzugriffException e) {
            konfliktMedium = null;
            ErrorHandler.getInstance().handleException(e, "Fehler beim Laden " +
                "des Konfliktmediums!", false);
          }
          throw new EANSchonVergebenException("Die EAN '"+ean+
            "' wird schon von dem Medium "+konfliktMedium+" " +
            "verwendet.", konfliktMedium);    
        }
        
      }
      
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into medium "+
          "set Nr = ?, Titel = ?, "+
          "Autor = ?, Beschreibung = ?, MedientypID = ?, " +
          "Medienanzahl = ?, eingestellt_seit = ?, "+
          "aus_Bestand_entfernt = ?, ISBN = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update medium "+
          "set Nr = ?, Titel = ?, "+
          "Autor = ?, Beschreibung = ?, MedientypID = ?, " +
          "Medienanzahl = ?, eingestellt_seit = ?, "+
          "aus_Bestand_entfernt = ?, ISBN = ? "+
          "where id="+this.getId());
      }

      statement.setString(1, this.getMedienNr());
      statement.setString(2, this.getTitel());
      statement.setString(3, this.getAutor());
      statement.setString(4, this.getBeschreibung());
      statement.setInt(5, this.getMedientyp().getId());
      statement.setInt(6, this.getMedienAnzahl());
      statement.setDate(7, DatenmanipulationsFunktionen.utilDate2sqlDate(
          this.getEinstellungsdatum()));
      statement.setDate(8, DatenmanipulationsFunktionen.utilDate2sqlDate(
          this.getEntfernungsdatum()));
      statement.setString(9, isbn != null?isbn.getISBN():null);
      
      statement.execute();
      
      if (this.istNeu()) {
        result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      
      //Systematiken einfügen
      normalStatement.execute("delete from medium_gehoert_zu_systematik " +
          "where mediumid="+this.getId());
      PreparedStatement insertSystematikStatement = 
        (PreparedStatement) connection.prepareStatement(
            "insert into medium_gehoert_zu_systematik set "+
            "mediumid = ?, systematikID = ?");
      insertSystematikStatement.setInt(1, this.getId());
      for (Systematik sys : getSystematiken()) {
        insertSystematikStatement.setInt(2, (sys.getId()));
        insertSystematikStatement.addBatch();
      }
      insertSystematikStatement.executeBatch();
      
      //EANs einfügen
      normalStatement.execute("delete from medium_ean " +
        "where mediumid="+this.getId());
      PreparedStatement insertEANsStatement = 
        (PreparedStatement) connection.prepareStatement(
            "insert into medium_ean set "+
            "mediumid = ?, ean = ?");
      insertEANsStatement.setInt(1, this.getId());
      for (EAN ean : getEANs()) {
        insertEANsStatement.setString(2, ean.getEAN());
        insertEANsStatement.addBatch();
      }
      insertEANsStatement.executeBatch();
      
      MysqlDatenbank.getMysqlInstance().releaseStatement(normalStatement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "des folgenden Mediums:\n\n"+this.toDebugString(), true);
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
      
      //Medien
      ResultSet result = (ResultSet) statement.executeQuery(
          "select count(*) from ausleihe where "+
          "mediumID = "+this.getId());
      result.next();
      if (result.getInt(1) > 0)
        throw new DatenbankInkonsistenzException("Das Medium '"+this.getTitel()+          "' ("+this.getMedienNr()+") kann nicht gelöscht werden, da noch " +
          "Medien dieses Typs existieren.");

      //löschen
      statement.execute("delete from medium_gehoert_zu_systematik where "+
        "mediumid = "+this.getId());

      statement.execute("delete from medium_ean where "+
        "mediumid = "+this.getId());

      statement.execute("delete from medium where "+
        "id = "+this.getId());

      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen des "+
        "Medientyps "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException, DatenbankInkonsistenzException {
    id = result.getInt("id");
    nr = result.getString("nr");
    titel = result.getString("titel");
    autor = result.getString("autor");
    beschreibung = result.getString("beschreibung");
    medienAnzahl = result.getInt("Medienanzahl");
    aus_Bestand_entfernt = result.getDate("aus_Bestand_entfernt");
    eingestellt_seit = result.getDate("eingestellt_seit");
    try {
      String isbnString = result.getString("isbn");
      isbnString = DatenmanipulationsFunktionen.formatString(isbnString);
      isbn = isbnString == null?null:new ISBN(isbnString);
    } catch (IllegalArgumentException exception) {
      throw new DatenbankInkonsistenzException("Das Medium "+id+" besitzt eine "+
          "ungültige ISBN-Nummer!");
    }
    
    try {
      MedientypFactory medientypFactory = 
        MysqlDatenbank.getMysqlInstance().getMedientypFactory();
      medientyp = (Medientyp) medientypFactory.get(result.getInt("MedientypID"));
    } catch (DatenNichtGefundenException e) {
      throw new DatenbankInkonsistenzException("Das Medium "+id+" besitzt einen "+
        "ungültigen Medientyp!");
    }
  }

  void loadSystematiken() {
    
    if (systematikenGeladen) return;
    try {
      systematikenGeladen = true;
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      SystematikFactory systematikFactory = 
        MysqlDatenbank.getMysqlInstance().getSystematikFactory();
      systematiken = new SystematikListe();
      ResultSet systematikenResult = (ResultSet) statement.executeQuery(
          "select systematikid from medium_gehoert_zu_systematik "+ 
          "where mediumID = " + id);
      while (systematikenResult.next()) {
        int systematikID = systematikenResult.getInt(1);
        try {
          systematiken.add(systematikFactory.get(systematikID));
        } catch (DatenbankzugriffException e) {
          ErrorHandler.getInstance().handleException(e, "Das Medium "+getId()+
              " gehört zur nicht existenten Systematik "+systematikID+"!", false);
        }
      }
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
        "Systematiken!", true);
    }
  }

  void loadEANs() {
    if (eansGeladen) return;
    try {
      eansGeladen = true;
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      eanListe = new EANListe();
      ResultSet eanResult = (ResultSet) statement.executeQuery(
          "select ean from medium_ean where mediumID = " + id);
      while (eanResult.next()) {
        eanListe.add(new EAN(eanResult.getString(1)));
      }
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der "+
        "Systematiken!", true);
    }
  }
  
  void load(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from medium where id = " + id);
      boolean mediumGefunden = result.next();
      if (!mediumGefunden) throw new DatenNichtGefundenException(
        "Ein Medium mit der ID "+id+" existiert nicht!");
  
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
        "des Mediums mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
    
  }   

  public EANListe getEANs() {
    loadEANs();
    return super.getEANs();
  }

  public SystematikListe getSystematiken() {
    loadSystematiken();
    return super.getSystematiken();
  }

  public void setEANs(EANListe eanListe) {
    eansGeladen = true;
    super.setEANs(eanListe);
  }

  public void setSystematiken(SystematikListe systematiken) {
    systematikenGeladen = true;
    super.setSystematiken(systematiken);
  }
}