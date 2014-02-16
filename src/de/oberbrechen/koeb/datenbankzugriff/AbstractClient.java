package de.oberbrechen.koeb.datenbankzugriff;

/**
 * Diese Klasse stellt die grundlegenden Methoden
 * eines Clients bereit. Der datenbankspezifischen 
 * Code muss jedoch noch ergänzt werden.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractClient 
  extends AbstractDatenbankzugriff implements Client {

  protected String name;
  protected String ip;
  protected boolean besitztInternetzugang;
  
  public AbstractClient() {
    name = null;
    ip = null;
    besitztInternetzugang = true;
  }
  
  public String getIP() {
    return ip;
  }

  public String getName() {
    return name;
  }

  public boolean getBesitztInternetzugang() {
    return besitztInternetzugang;
  }

  public void setIP(String ip) {
    setIstNichtGespeichert();
    this.ip = DatenmanipulationsFunktionen.formatString(ip);
  }

  public void setName(String name) {
    setIstNichtGespeichert();
    this.name = DatenmanipulationsFunktionen.formatString(name);
  }

  public void setBesitztInternetzugang(boolean besitztInternetzugang) {
    setIstNichtGespeichert();
    this.besitztInternetzugang = besitztInternetzugang;
  }

  public String toDebugString() {
    return name+" ("+ip+"/"+id+")";
  }

  public String toString() {
    return toDebugString();
  }
}
