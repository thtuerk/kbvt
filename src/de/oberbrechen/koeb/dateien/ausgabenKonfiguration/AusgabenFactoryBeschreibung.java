package de.oberbrechen.koeb.dateien.ausgabenKonfiguration;

import java.util.Iterator;
import java.util.Vector;

import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.ParameterException;

/**
* Dieses Klasse beschreibt eine AusgabenFactory.
* Sie dient zur Interaktion mit der Konfigurationsdatei. Mittels
* dieser Klasse kann auf die dort gespeicherten Informationen
* zugegriffen werden.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class AusgabenFactoryBeschreibung {
  
  private String klasse;
  private String ordner;
  private Vector<Parameter> parameter;
  
  public AusgabenFactoryBeschreibung() {
    klasse = null;
    ordner = null;
    parameter = new Vector<Parameter>();
  }

  /**
   * Liefert den Klassenname der AusgabenFactory
   * @return den Klassenname der AusgabenFactory
   */
  public String getKlasse() {
    return klasse;
  }

  /**
   * Setzt den Klassenname der AusgabenFactory
   * @param klasse der neue Klassenname der AusgabenFactory
   */
  public void setKlasse(String klasse) {
    this.klasse = klasse;
  }

  /**
   * Liefert den Ordnernamen der AusgabenFactory
   * @return den Ordnernamen der AusgabenFactory
   */
  public String getOrdner() {
    return ordner;
  }

  /**
   * Setzt den Ordner der AusgabenFactory
   * @param ordner der neue Ordner der AusgabenFactory
   */
  public void setOrdner(String ordner) {
    this.ordner = ordner;
  }

  /**
   * Liefert einen Vector von Parametern. Sollen
   * die Parameter geändert werden, so ist dieser
   * Vector zu modifizieren
   * 
   * @return die Parameter
   */
  public Vector<Parameter> getParameter() {
    return parameter;
  }
  
  /**
   * Versucht aufgrund der gespeicherten Informationen
   * eine AusgabenFactory zu konstruieren. Dabei können
   * Exceptions geworfen werden.
   * @return die konstruierte AusgabenFactory
   */
  public AusgabenFactory createAusgabenFactory() 
    throws AusgabenFactoryErzeugungsException, ParameterException {
    
    AusgabenFactory ausgabenFactory = createAusgabenFactory(klasse);
    for (int i=0; i < parameter.size(); i++) {
      Parameter p = parameter.get(i);
      ausgabenFactory.setParameter(p.name, p.wert);
    }
    return ausgabenFactory;
  }

  private static AusgabenFactory createAusgabenFactory(String klassenname) 
    throws AusgabenFactoryErzeugungsException {
      
    try {
      return (AusgabenFactory) Class.forName(klassenname).newInstance();
    } catch (ClassCastException e) {
      throw new AusgabenFactoryErzeugungsException("Die Klasse '"+klassenname+"' " +
        "implementiert nicht das Interface AusgabenFactory!");
    } catch (InstantiationException e) {
      throw new AusgabenFactoryErzeugungsException("Fehler beim Instanzieren " +
        "der Klasse '"+klassenname+"':\n\n"+e.getMessage());
    } catch (IllegalAccessException e) {
      throw new AusgabenFactoryErzeugungsException("Fehler beim Instanzieren " +
        "der Klasse '"+klassenname+"':\n\n"+e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new AusgabenFactoryErzeugungsException("Fehler beim Instanzieren " +
        "der Klasse '"+klassenname+"':\n\n"+e.getMessage());
    }
  }
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("AusgabenFactoryBeschreibung\n");    
    buffer.append("---------------------------\n");    
    buffer.append("Klassenname: ");
    buffer.append(klasse);    
    buffer.append("\nOrdner:      ");    
    buffer.append(ordner);    
    buffer.append("\nParameter:\n");    
    Iterator<Parameter> it = parameter.iterator();
    while (it.hasNext()) {
      Parameter param = it.next();
      buffer.append("   ");
      buffer.append(param.name);
      buffer.append(": ");
      buffer.append(param.wert);
      buffer.append("\n");
    }
    return buffer.toString();    
  }
}

class Parameter {
  String name;
  String wert;
}