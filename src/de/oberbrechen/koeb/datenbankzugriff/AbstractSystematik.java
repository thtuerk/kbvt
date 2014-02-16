package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;


/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Benutzer.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractSystematik extends AbstractDatenbankzugriff 
  implements Systematik {

  protected String name, beschreibung;
  protected Systematik direkteObersystematik;
  protected SystematikListe direkteUntersystematiken;
  protected SystematikListe alleUntersystematiken;

  public AbstractSystematik() {
    name = null;
    beschreibung = null;
    direkteUntersystematiken = null;
    alleUntersystematiken = null;
    direkteObersystematik = null;
  }
  
  public String getName() {
    return name;
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public void setName(String name) {
    setIstNichtGespeichert();
    this.name = DatenmanipulationsFunktionen.formatString(name);
  }

  public void setBeschreibung(String beschreibung) {
    setIstNichtGespeichert();
    this.beschreibung = DatenmanipulationsFunktionen.formatString(beschreibung);
  }

  public String toString() {
    return name;
  }

  public String toDebugString() {
    return this.getName() + " ("+this.getId()+")\n"+this.getBeschreibung();
  }

  protected abstract SystematikListe ladeDirekteUntersystematiken();

  protected SystematikListe ladeAlleUntersystematiken() {
    SystematikListe liste = new SystematikListe();
    
    liste.add(this);       
    for (Systematik aktuelleSystematik : getDirekteUntersystematiken()) {
      liste.addAll(aktuelleSystematik.getAlleUntersystematiken());
    }
    
    return liste;
  } 
  
  public SystematikListe getDirekteUntersystematiken() {
    if (direkteUntersystematiken == null) 
      direkteUntersystematiken = ladeDirekteUntersystematiken();
    SystematikListe result = new SystematikListe();
    result.addAllNoDuplicate(direkteUntersystematiken);
    return result;
  }
  
  public SystematikListe getAlleUntersystematiken() {
    if (alleUntersystematiken == null) 
      alleUntersystematiken = ladeAlleUntersystematiken();
    SystematikListe result = new SystematikListe();
    result.addAllNoDuplicate(alleUntersystematiken);
    return result;
  }

  /**
   * Fügt alle Obersystematiken der Systematik zur übergebenen liste hinzu
   * @param liste die zu verändernde Liste
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  protected void addAlleObersystematiken(SystematikListe liste) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    if (getDirekteObersystematik() != null) {
      liste.add(getDirekteObersystematik());
      ((AbstractSystematik) getDirekteObersystematik()).addAlleObersystematiken(liste);    
    }
  }
  
  public SystematikListe getAlleObersystematiken() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    SystematikListe liste = new SystematikListe();    
    addAlleObersystematiken(liste);
    return liste;
  }
    
  public Systematik getDirekteObersystematik() throws DatenbankInkonsistenzException {
    return direkteObersystematik;
  }

  public void setDirekteObersystematik(Systematik systematik) {
    setIstNichtGespeichert();
    direkteObersystematik = systematik;
  }
}