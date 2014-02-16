package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Observable;

/**
 * Diese Klasse ist eine unvollständige
 * Standard-Implementierung des
 * Interfaces Datenbankzugriff.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractDatenbankzugriff extends Observable 
  implements Datenbankzugriff {
  
  protected int id;
  protected boolean istGespeichert;
  
  public AbstractDatenbankzugriff() {
    id = 0;
    istGespeichert = false;
  }
  
  /**
   * Interne Methode, die nur von Subklassen verwendet werden sollte.
   * Mit Hilfe dieser Methode kann die Information, dass
   * das Datenbankzugriff-Objekt nicht gespeichert ist, gesetzt werden.
   * Dies sollte immer geschehen, wenn Attribute geändert werden.
   */
  protected void setIstNichtGespeichert() {
    istGespeichert = false;
  }
  
  /**
   * Interne Methode, die nur von Subklassen verwendet werden sollte.
   * Mit Hilfe dieser Methode kann die Information, dass
   * das Datenbankzugriff-Objekt gespeichert ist, gesetzt werden.
   */
  protected void setIstGespeichert() {
    istGespeichert = true;
  }
    
  public boolean istNeu() {
    return (id == 0);
  }
    
  public boolean istGespeichert() {
    return istGespeichert;
  }

  public String toDebugString() {
    return toString();
  }
  
  public int getId() {
    return id;
  }
  
  public boolean equals(Object object) {
    if (object == null) return false;
    if (object.getClass() != this.getClass()) return false;

    return (this.getId() == ((Datenbankzugriff) object).getId());
  }    
  
  public int hashCode() {
    return getId();
  }
  
  public String toString() {
    return this.getClass()+": "+getId();
  } 
  
  public int compareTo(Object o) {
    return this.toString().compareTo(o.toString());
  }  
}