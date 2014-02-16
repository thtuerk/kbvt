package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractMediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlMediumFactory extends AbstractMediumFactory {

  private static SimpleDateFormat sqldateFormat =
    new SimpleDateFormat("yyyy-MM-dd");
  
  protected MedienListe alleMedienListe = null;
  protected MedienListe alleMedienNrListe = null;
  protected MedienListe alleMedienTitelListe = null;
  
  public Medium erstelleNeu() {
    return new MysqlMedium();
  }

  public Medium ladeAusDatenbank(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    return new MysqlMedium(id);
  }

  public synchronized MedienListe getAlleMedien() throws DatenbankInkonsistenzException {
    if (alleMedienListe != null) {
      MedienListe result = new MedienListe();
      result.addAllNoDuplicate(alleMedienListe);
      return result;
    }
    
    clearCache();
    MedienListe liste = getMedienListe(null, null, new Boolean(false));
    alleMedienListe = new MedienListe();
    alleMedienListe.addAllNoDuplicate(liste);
    return liste;
  }

  public MedienListe getAlleMedienInklusiveEntfernte() throws DatenbankInkonsistenzException {
    return getMedienListe(null, null, null);
  }
  
  public MedienListe getMedienListe(Medientyp typ, Systematik systematik, Boolean ausBestandEntfernt) throws DatenbankInkonsistenzException {
    //SQLQuery bauen
    String wherePart;
    if (ausBestandEntfernt != null) {
      if (ausBestandEntfernt.booleanValue()) {
        wherePart = "!isNull(aus_bestand_entfernt)";
      } else {
        wherePart = "isNull(aus_bestand_entfernt)";
      }
    } else {
      wherePart = "1";      
    }

    if (typ != null) 
      wherePart = wherePart + " AND MedientypID="+typ.getId();
      
    StringBuffer sqlQuery = new StringBuffer();
    if (systematik != null) {
      SystematikListe unterSystematiken = systematik.getAlleUntersystematiken();
      sqlQuery.append("select m.* from medium_gehoert_zu_systematik as ms "+
        "left join medium as m on ms.medium = m.nr where systematik in (");
      for (int i=0; i<unterSystematiken.size()-1; i++) {
        sqlQuery.append("\"");
        sqlQuery.append(((Systematik) unterSystematiken.get(i)).getName());
        sqlQuery.append("\",");
      }
      sqlQuery.append("\"");
      sqlQuery.append(((Systematik) unterSystematiken.get(unterSystematiken.size()-1)).getName());
      sqlQuery.append("\") AND ");
      sqlQuery.append(wherePart);
    } else {
      sqlQuery.append("select * from medium where ");      
      sqlQuery.append(wherePart);
    }
    
    return ladeAusMedienTabelle(sqlQuery.toString());
  }

  public MedienListe getAlleMedienMediennrSortierung() throws DatenbankInkonsistenzException {
    if (alleMedienNrListe != null) return alleMedienNrListe;

    if (alleMedienListe != null) {
      alleMedienNrListe = new MedienListe();
      alleMedienNrListe.addAllNoDuplicate(alleMedienListe);
    } else {
      alleMedienNrListe = getAlleMedien();      
    }

    alleMedienNrListe.setSortierung(MedienListe.MedienNummerSortierung);    
    return alleMedienNrListe;
  }

  public MedienListe getAlleMedienTitelSortierung() throws DatenbankInkonsistenzException {
    if (alleMedienTitelListe != null) return alleMedienTitelListe;

    if (alleMedienListe != null) {
      alleMedienTitelListe = new MedienListe();
      alleMedienTitelListe.addAllNoDuplicate(alleMedienListe);
    } else {
      alleMedienTitelListe = getAlleMedien();     
    }

    alleMedienTitelListe.setSortierung(MedienListe.TitelAutorSortierung);    
    return alleMedienTitelListe;
  }

  public void clearCache() {
    super.clearCache();
    alleMedienListe = null;
    alleMedienNrListe = null;
    alleMedienTitelListe = null;
  }

  public Liste<String> getAlleAutoren() {
    Liste<String> liste = new Liste<String>();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select distinct autor from medium where isNull(aus_Bestand_Entfernt);");
      while (result.next()) {
        String autor = result.getString("autor");
        autor = DatenmanipulationsFunktionen.formatString(autor);
        if (autor != null) liste.addNoDuplicate(autor);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Autorenliste!", true);
    }

    return liste;
  }

  private MedienListe ladeAusMedienTabelle(String sqlQuery) throws DatenbankInkonsistenzException {
    MedienListe liste = new MedienListe();    
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(sqlQuery);
      while (result.next()) {
        int id = result.getInt("id");
        Medium medium = ladeAusCache(id);
        
        if (medium == null) { 
          try {
            medium = new MysqlMedium(result);
          } catch (DatenbankInkonsistenzException e) {
            MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
            MysqlDatenbank.getMysqlInstance().endTransaktion();
            throw e;
          }
          cache.put(new Integer(medium.getId()), medium);
        }
        
        liste.addNoDuplicate(medium);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Laden der Medienliste!\n"+sqlQuery, true);
    }
    return liste;
  }
  
  public MedienListe sucheMedien(String stichwort, String autor, String titel, Medientyp medientyp) throws DatenbankInkonsistenzException {
    //SQLQuery bauen
    StringBuffer wherePart = new StringBuffer("isNull(aus_bestand_entfernt)");
    if (medientyp != null) 
      wherePart.append(" AND (MedientypID="+medientyp.getId()+")");
    stichwort = DatenmanipulationsFunktionen.formatString(stichwort);
    if (stichwort != null) {
      wherePart.append(" AND (");
      wherePart.append("(autor LIKE \"%"+stichwort+"%\") OR");
      wherePart.append("(titel LIKE \"%"+stichwort+"%\") OR");
      wherePart.append("(beschreibung LIKE \"%"+stichwort+"%\")");
      wherePart.append(")");
    }
    autor = DatenmanipulationsFunktionen.formatString(autor);
    if (autor != null) {
      wherePart.append(" AND (autor LIKE \""+autor+"%\")");
    }
    titel = DatenmanipulationsFunktionen.formatString(titel);
    if (titel != null) {
      wherePart.append(" AND (titel LIKE \""+titel+"%\")");
    }
    
    //laden
    return ladeAusMedienTabelle("select * from medium where "+
        wherePart.toString());
  }

  public MedienListe getAlleMedien(Date date) throws DatenbankInkonsistenzException {
    StringBuffer sqlQuery = new StringBuffer();
    sqlQuery.append("select * from medium where ");
    sqlQuery.append("(isNull(eingestellt_seit) OR eingestellt_seit < '");
    sqlQuery.append(sqldateFormat.format(date));
    sqlQuery.append("') and (isNull(aus_Bestand_entfernt) OR ");
    sqlQuery.append("(aus_Bestand_entfernt >= '");
    sqlQuery.append(sqldateFormat.format(date));
    sqlQuery.append("'))");
    
    return ladeAusMedienTabelle(sqlQuery.toString());
  }

  public MedienListe getEingestellteMedienInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException {
    StringBuffer sqlQuery = new StringBuffer();
    sqlQuery.append("select * from medium where ");
    sqlQuery.append("eingestellt_seit >= '");
    sqlQuery.append(sqldateFormat.format(zeitraum.getBeginn()));
    sqlQuery.append("' and eingestellt_seit < '");
    sqlQuery.append(sqldateFormat.format(zeitraum.getEnde()));
    sqlQuery.append("'");
    
    return ladeAusMedienTabelle(sqlQuery.toString());
  }

  public MedienListe getEntfernteMedienInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException {
    StringBuffer sqlQuery = new StringBuffer();
    sqlQuery.append("select * from medium where ");
    sqlQuery.append("aus_Bestand_entfernt >= '");
    sqlQuery.append(sqldateFormat.format(zeitraum.getBeginn()));
    sqlQuery.append("' and aus_Bestand_entfernt < '");
    sqlQuery.append(sqldateFormat.format(zeitraum.getEnde()));
    sqlQuery.append("'");

    return ladeAusMedienTabelle(sqlQuery.toString());
  }
  
  public String getNaechsteMedienNr(String pattern) {
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(
          "select max(substring(nr, "+(pattern.length()+1)+")+1) from medium where nr like \""+
          pattern.toString()+"%\"");
      result.next();
      int nr=result.getInt(1);
      if (nr == 0) nr=1;
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
      
      return pattern+nr;      
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Bestimmen der Mediennr!", true);
      return null;
    }    
  }
}