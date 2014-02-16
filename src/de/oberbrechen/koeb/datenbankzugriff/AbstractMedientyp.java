package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Benutzer.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public abstract class AbstractMedientyp extends AbstractDatenbankzugriff 
  implements Medientyp {

  protected String oldName;
  protected String name;
  protected String plural;
  
  /**
   * Erstellt einen neuen, noch nicht gespeicherten Medientyp 
   */
  public AbstractMedientyp() {
    name = null;
    plural = null;
    oldName = null;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    setIstNichtGespeichert();
    this.name = name;
  }

  public String getPlural() {
    return plural;
  }

  public void setPlural(String plural) {
    setIstNichtGespeichert();
    this.plural = plural;
  }
  
  public String toString() {
    return this.getName();
  }

  public String toDebugString() {
    return this.getName()+" - "+this.getPlural()+" ("+getId()+")";
  }
}