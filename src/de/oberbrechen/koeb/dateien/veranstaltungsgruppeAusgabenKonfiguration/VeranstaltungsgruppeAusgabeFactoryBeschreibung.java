package de.oberbrechen.koeb.dateien.veranstaltungsgruppeAusgabenKonfiguration;

import java.util.Vector;

import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabeFactory;

/**
* Dieses Klasse beschreibt eine VeranstaltungsgruppeAusgabenFactory.
* Sie dient zur Interaktion mit der Konfigurationsdatei. Mittels
* dieser Klasse kann auf die dort gespeicherten Informationen
* zugegriffen werden und bearbeitet werden.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class VeranstaltungsgruppeAusgabeFactoryBeschreibung {
  
  private String name;
  private String klasse;
  private Vector<Parameter> parameter;
  
  public VeranstaltungsgruppeAusgabeFactoryBeschreibung() {
    klasse = null;
    parameter = new Vector<Parameter>();
  }


  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
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
  public VeranstaltungsgruppeAusgabeFactory createVeranstaltungsgruppeAusgabeFactory() 
    throws VeranstaltungsgruppeAusgabeFactoryErzeugungsException, ParameterException {
      
    VeranstaltungsgruppeAusgabeFactory ausgabenFactory = createVeranstaltungsgruppeAusgabeFactory(klasse);
    for (Parameter p :  parameter) {
      ausgabenFactory.setParameter(p.name, p.wert);
    }
    return ausgabenFactory;
  }

  private static VeranstaltungsgruppeAusgabeFactory createVeranstaltungsgruppeAusgabeFactory(String klassenname) 
    throws VeranstaltungsgruppeAusgabeFactoryErzeugungsException {
      
    try {
      return (VeranstaltungsgruppeAusgabeFactory) Class.forName(klassenname).newInstance();
    } catch (ClassCastException e) {
      throw new VeranstaltungsgruppeAusgabeFactoryErzeugungsException("Die Klasse '"+klassenname+"' " +
        "implementiert nicht das Interface VeranstaltungsgruppeAusgabeFactory!");
    } catch (InstantiationException e) {
      throw new VeranstaltungsgruppeAusgabeFactoryErzeugungsException("Fehler beim Instanzieren " +
        "der Klasse '"+klassenname+"':\n\n"+e.getMessage());
    } catch (IllegalAccessException e) {
      throw new VeranstaltungsgruppeAusgabeFactoryErzeugungsException("Fehler beim Instanzieren " +
        "der Klasse '"+klassenname+"':\n\n"+e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new VeranstaltungsgruppeAusgabeFactoryErzeugungsException("Fehler beim Instanzieren " +
        "der Klasse '"+klassenname+"':\n\n"+e.getMessage());
    }
  }
  
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("AusgabenFactoryBeschreibung\n");    
    buffer.append("---------------------------\n");    
    buffer.append("Klassenname: ");
    buffer.append(klasse);    
    buffer.append("\nParameter:\n");    
    for (Parameter param : parameter) {
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