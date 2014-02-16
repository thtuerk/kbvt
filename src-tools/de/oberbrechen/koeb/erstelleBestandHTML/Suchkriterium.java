package de.oberbrechen.koeb.erstelleBestandHTML;

import java.sql.SQLException;

import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.mysql.MysqlDatenbank;
import de.oberbrechen.koeb.datenstrukturen.IDFormat;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;


/**
 * Diese Klasse repr�sentiert eine Liste der XML-Datei
 * 
 * @author Thomas T�rk (t_tuerk@gmx.de)
 */
public class Suchkriterium {
    
  Medientyp medientyp = null;
  SystematikListe systematiken = new SystematikListe();
  Integer einstellungsDatum = null;
  
  public void setMedientyp(Medientyp typ) {
    this.medientyp = typ;
  }
  
  public void setMedientyp(String typ) {
    MedientypFactory medientypFactory =
      Datenbank.getInstance().getMedientypFactory();
    
    try {
      setMedientyp(medientypFactory.get(typ));
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
  }
  
  public void setEinstellungsdauer(int tage) {
    einstellungsDatum = new Integer(tage);
  }

  public void setEinstellungsdauer(String tage) {
    setEinstellungsdauer(Integer.parseInt(tage));
  }
  
  public void addSystematik(String systematik) {    
    SystematikFactory systematikFactory =
      Datenbank.getInstance().getSystematikFactory();

    try {
      addSystematik(systematikFactory.get(systematik));
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }    
  }
  
  public void addSystematik(Systematik systematik) {
    systematiken.add(systematik);
  }

  
  private String getWherePart() {
    StringBuffer where = new StringBuffer();
    where.append("isNull(aus_Bestand_entfernt)"); //$NON-NLS-1$
    if (medientyp != null) {
      where.append(" AND m.medientypId = "); //$NON-NLS-1$
      where.append(medientyp.getId());
    }

    if (systematiken.size() > 0) {
      SystematikListe unterSystematiken = new SystematikListe();
      for (int i=0; i < systematiken.size(); i++) {
        Systematik systematik = (Systematik) systematiken.get(i);
        unterSystematiken.addAll(systematik.getAlleUntersystematiken());
      }
        
      where.append(" AND ms.systematikId IN ("); //$NON-NLS-1$
      where.append(unterSystematiken.toKommaGetrenntenString(new IDFormat()));
      where.append(")"); //$NON-NLS-1$
    }
    if (einstellungsDatum != null) {
      where.append(" AND (to_days(now()) - to_days(m.eingestellt_seit) <= "); //$NON-NLS-1$
      where.append(einstellungsDatum);
      where.append(")"); //$NON-NLS-1$
    }

    return where.toString();
  }
  
  public String getAnfrage() {
    StringBuffer erg = new StringBuffer();
    erg.append("select m.id, m.autor, m.titel, !isNull(a.id) from medium m " + //$NON-NLS-1$
        "left join ausleihe a on isNull(a.rueckgabeDatum) and a.mediumid=m.id "); //$NON-NLS-1$
    if (systematiken.size() > 0) {
      erg.append("left join medium_gehoert_zu_systematik ms on ms.mediumid=m.id "); //$NON-NLS-1$
    }
    erg.append("where "); //$NON-NLS-1$
    erg.append(getWherePart());
    erg.append(" group by m.id order by m.titel, m.autor"); //$NON-NLS-1$

    return erg.toString();
  }
  
  public int getTrefferAnzahl() {    
    StringBuffer erg = new StringBuffer();
    erg.append("select count(m.id) from medium m "); //$NON-NLS-1$
    if (systematiken.size() > 0) {
      erg.append("left join medium_gehoert_zu_systematik ms on ms.mediumid=m.id "); //$NON-NLS-1$
    }
    erg.append("where "); //$NON-NLS-1$
    erg.append(getWherePart());
        
    try {
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(erg.toString());
      result.next();
      return result.getInt(1);
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
