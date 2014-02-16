package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractSystematik;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeObersystematikException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlSystematik extends AbstractSystematik {

  boolean direkteObersystematikGeladen = false;
  int direkteObersystematikID = 0;
  
  public MysqlSystematik(int id) throws DatenNichtGefundenException {
    load(id);
  }

  MysqlSystematik(ResultSet result) throws SQLException {
    load(result);
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
  }
  
  public MysqlSystematik() {
  }
  
  public void reload() throws DatenNichtGefundenException {
    if (this.istNeu()) return;
    load(getId());
  }

  public void save() throws UnvollstaendigeDatenException, DatenbankInkonsistenzException, UnpassendeObersystematikException {
    if (istGespeichert()) return;

    if (this.getName() == null || this.getName().trim().equals("")) {
      throw new UnvollstaendigeDatenException("Der Name jeder "+
        "Systematik muss eingegeben sein.");
    }

    //unpassende Obersystematik ?
    if (getDirekteObersystematik() != null) {
      
      boolean unpassendeObersystematik=false; 
      
      Systematik aktuelleSystematik = getDirekteObersystematik();
      while (!unpassendeObersystematik && aktuelleSystematik != null) {
        if (aktuelleSystematik.equals(this)) unpassendeObersystematik = true;
        aktuelleSystematik = aktuelleSystematik.getDirekteObersystematik();        
      }

      if (unpassendeObersystematik) {
        throw new UnpassendeObersystematikException("Die Systematik "+ getDirekteObersystematik().getName()+
          " kann nicht als Obersystematik für "+this.getName()+" gesetzt werden, da " +
          getDirekteObersystematik().getName()+" eine Untersystematik von "+this.getName()+
          " ist!");
      }
    }
    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Connection connection = MysqlDatenbank.getMysqlInstance().getConnection();
      PreparedStatement statement = null;

      if (this.istNeu()) {
        statement = (PreparedStatement) connection.prepareStatement(
          "insert into systematik set Name = ?, beschreibung = ?, SpezialisiertID = ?");
      } else {
        statement = (PreparedStatement) connection.prepareStatement(
          "update systematik set Name = ?, beschreibung = ?, SpezialisiertID = ? where id="+this.getId());
      }
      statement.setString(1, this.getName());
      statement.setString(2, this.getBeschreibung());
      if (direkteObersystematikID > 0) {
        statement.setInt(3, direkteObersystematikID);
      } else {
        statement.setObject(3, null);
      }
      statement.execute();
      
      if (this.istNeu()) {
        ResultSet result = (ResultSet) statement.getGeneratedKeys();
        result.next();
        id = result.getInt(1);
      }
      
      MysqlDatenbank.getMysqlInstance().endTransaktion();      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "der folgenden Systematik:\n\n"+this.toDebugString(), true);
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
          "select count(*) from medium_gehoert_zu_systematik where "+
          "SystematikID = "+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Die Systematik "+this.getName()+          "("+this.getId()+") kann nicht gelöscht werden, da noch Medien " +
          "dieser Systematik existieren.");
      }

      //Untersystematiken
      result = (ResultSet) statement.executeQuery(
          "select count(*) from systematik where "+
          "SpezialisiertID = "+this.getId());
      result.next();
      if (result.getInt(1) > 0) {
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenbankInkonsistenzException("Die Systematik "+this.getName()+
            "("+this.getId()+") kann nicht gelöscht werden, da noch Untersystematiken " +
        "dieser Systematik existieren.");
      }

      //Löschen
      statement.execute("delete from systematik where ID = "+this.getId());
        
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);      
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Löschen der "+
        "Systematik "+this.toDebugString(), true);
    }
  }
  
  void load(ResultSet result) throws SQLException {
    id = result.getInt("id");
    name = result.getString("name");
    beschreibung = result.getString("beschreibung");

    direkteObersystematik = null;
    direkteObersystematikGeladen = false;
    direkteObersystematikID = result.getInt("SpezialisiertID");
    
  }

  void load(int id) throws DatenNichtGefundenException {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select * from systematik where id = " + id);
      boolean gefunden = result.next();
      if (!gefunden) {
        MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
        MysqlDatenbank.getMysqlInstance().endTransaktion();
        throw new DatenNichtGefundenException(
          "Eine Systematik mit der ID "+id+" existiert nicht!");
      }
      load(result);
  
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden "+
        "der Systematik mit der ID "+id+"!", true);
    }
    setIstGespeichert();
    this.setChanged();
    this.notifyObservers();
    
  }

  protected SystematikListe ladeDirekteUntersystematiken() {
    MysqlSystematikFactory systematikFactory = (MysqlSystematikFactory) 
      MysqlDatenbank.getMysqlInstance().getSystematikFactory();
    return systematikFactory.ladeSystematikListe("select * from " +
        "systematik where spezialisiertID="+this.getId());
  }

  public Systematik getDirekteObersystematik() throws DatenbankInkonsistenzException {
    if (!direkteObersystematikGeladen) {
      if (direkteObersystematikID > 0) {
        try {
          direkteObersystematik = (Systematik) MysqlDatenbank.getMysqlInstance().
            getSystematikFactory().get(direkteObersystematikID);
        } catch (DatenNichtGefundenException e) {
          throw new DatenbankInkonsistenzException("Die Systematik "+id+
              " verweist auf eine nicht existente Obersystematik!");
        }
      } else {
        direkteObersystematik = null;
      }
      direkteObersystematikGeladen = true;
    }
    
    return direkteObersystematik;
  }

  public void setDirekteObersystematik(Systematik systematik) {
    super.setDirekteObersystematik(systematik);
    
    direkteObersystematikGeladen = true;
    direkteObersystematikID = (systematik == null)?0:systematik.getId();
  }

  public boolean istUntersystematikVon(Systematik systematik) throws DatenbankInkonsistenzException {
    Systematik aktuelleSystematik = this;
    while (aktuelleSystematik != null) {
      if (aktuelleSystematik.equals(systematik)) return true;
      aktuelleSystematik = aktuelleSystematik.getDirekteObersystematik();
    }
    
    return false;
  }  
}