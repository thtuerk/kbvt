package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;


/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Bemerkung.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractBemerkung extends AbstractDatenbankzugriff 
  implements Bemerkung {

  protected String bemerkung = "";
  protected boolean istAktuell = true;
  protected Date datum = new Date();  

  public String getBemerkung() {
    return bemerkung;
  }

  public Date getDatum() {
    return datum;
  }

  public boolean istAktuell() {
    return istAktuell;
  }
  
  public void setAktuell(boolean aktuell) {
    setIstNichtGespeichert();
    this.istAktuell = aktuell;
  }

  public void setBemerkung(String bemerkung) {
    setIstNichtGespeichert();
    this.bemerkung = DatenmanipulationsFunktionen.formatString(bemerkung);
  }

  public void setDatum(Date datum) {
    setIstNichtGespeichert();
    this.datum = datum;
  }
  
  public String toString() {
    return bemerkung;
  }
  
  protected String toDebugString(String klassenname) {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append(klassenname).append(getId()).append("\n");
    ausgabe.append("-------------------------------\n");
    ausgabe.append(this.getDatum()).append("\n");
    ausgabe.append(this.getBemerkung()).append("\n");
    ausgabe.append("aktuell: "+this.istAktuell());
    
    return ausgabe.toString();
  }  
}