package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Veranstaltungsgruppe.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractVeranstaltungsgruppe 
  extends AbstractDatenbankzugriff implements Veranstaltungsgruppe {

  protected String name, kurzName, beschreibung, homeDir;
  
  public AbstractVeranstaltungsgruppe() {
    name = null;
    beschreibung = null;
    kurzName = null;
    homeDir = null;
  }

  public String getName() {
    return name;
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public String getKurzName() {
    return kurzName;
  }

  public String getHomeDir() {
    return homeDir;
  }

  public String toString() {
    return name;
  }

  public String toDebugString() {
    return this.toString();
  }

  public void setBeschreibung(String beschreibung) {
    setIstNichtGespeichert();
    this.beschreibung = DatenmanipulationsFunktionen.formatString(beschreibung);
  }

  public void setHomeDir(String homeDir) {
    setIstNichtGespeichert();
    this.homeDir = DatenmanipulationsFunktionen.formatString(homeDir);
  }

  public void setKurzName(String kurzName) {
    setIstNichtGespeichert();
    this.kurzName = DatenmanipulationsFunktionen.formatString(kurzName);
  }

  public void setName(String name) {
    setIstNichtGespeichert();
    this.name = DatenmanipulationsFunktionen.formatString(name);
  }  
}