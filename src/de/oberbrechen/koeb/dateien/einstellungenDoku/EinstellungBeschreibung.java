package de.oberbrechen.koeb.dateien.einstellungenDoku;

import java.io.File;
import java.util.Collection;

import de.oberbrechen.koeb.dateien.auswahlKonfiguration.ParseException;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenstrukturen.Liste;

/**
* Dieses Klasse beschreibt eine Einstellung.
* Sie dient zur Interaktion mit der Dokudatei. Mittels
* dieser Klasse kann auf die dort gespeicherten Informationen
* zugegriffen werden. Es handelt sich jedoch nur um einen 
* dummen Datencontainer
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class EinstellungBeschreibung {  
  
  public static final int TYP_INT = 1;   
  public static final int TYP_DOUBLE = 2;   
  public static final int TYP_STRING = 3;   
  public static final int TYP_CLASS = 4;   
  public static final int TYP_FILE = 5;   
  public static final int TYP_DIR = 6;   
  public static final int TYP_BOOLEAN = 7;   
  
  private String gruppe;
  private String name;
  private int typ;
  private Class<?> klasse;
  private boolean mitarbeiter; 
  private boolean client; 
  private String beschreibung;
  private String standardWert;
  private Liste<String> werte;
  private EinstellungTest test;

  public EinstellungBeschreibung() {
    werte = new Liste<String>();
    mitarbeiter = false;
    client = false;    
  }
  
  public void setTyp(String typString) {
    if (typString.equalsIgnoreCase("BOOLEAN")) {
      typ = TYP_BOOLEAN;
    } else if (typString.equalsIgnoreCase("INTEGER")) { 
      typ = TYP_INT;
    } else if (typString.equalsIgnoreCase("DOUBLE")) { 
      typ = TYP_DOUBLE;
    } else if (typString.equalsIgnoreCase("STRING")) {
      typ = TYP_STRING;
    } else if (typString.equalsIgnoreCase("FILE")) {
      typ = TYP_FILE;
    } else if (typString.equalsIgnoreCase("DIR")) {
      typ = TYP_DIR;
    } else if (typString.length() > 6 && typString.substring(0, 6).equalsIgnoreCase("CLASS:")) {
      typ = TYP_CLASS;
      String klassenString = null;
      try {
        klassenString = typString.substring(6, typString.length());
        klasse = Class.forName(klassenString);
      } catch (ClassNotFoundException e) {
        throw new ParseException(e, "Klasse nicht gefunden: "+klassenString);
      }
    } else {
      throw new ParseException("Einstellungstyp '"+typString+"' unbekannt!");
    }
  }
  
  private String getTyp() {
    switch (typ) {
      case TYP_BOOLEAN:
        return "BOOLEAN";
      case TYP_INT:
        return "INTEGER";
      case TYP_DOUBLE:
        return "DOUBLE";
      case TYP_STRING:
        return "STRING";
      case TYP_CLASS:
        return "CLASS:"+klasse.getName();        
      case TYP_FILE:
        return "FILE";        
      case TYP_DIR:
        return "DIR";        
    }
    
    return "UNBEKANNTER TYP!";
  }
 
  
  public String toString() {
    return name;
  }
  
  
  public String toDebugString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(name).append("\n");
    buffer.append("TYP:          ").append(getTyp()).append("\n");
    buffer.append("Gruppe:       ").append(gruppe).append("\n");
    buffer.append("Client:       ").append(client).append("\n");
    buffer.append("Mitarbeiter:  ").append(mitarbeiter).append("\n");
    buffer.append("Test:         ").append(test == null?"-":test.getClass().getName()).append("\n");
    buffer.append("Standardwert: ").append(standardWert).append("\n");
    buffer.append("Werte:        ").append(werte.toKommaGetrenntenString()).append("\n\n");   
    buffer.append(beschreibung).append("\n");   
    
    return buffer.toString();
  }

  public static EinstellungBeschreibung createUndokumentierteEinstellung(String name) {
    EinstellungBeschreibung result = new EinstellungBeschreibung();
    result.name = name;
    result.beschreibung = "Undokumentiert!";
    result.test = null;
    result.werte = new Liste<String>();
    result.standardWert = null;
    result.typ = TYP_STRING;
    
    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();
    result.client = einstellungFactory.istClientEinstellung(name);
    result.mitarbeiter = einstellungFactory.istMitarbeiterEinstellung(name);
    
    return result;
  }
  
  public String getBeschreibung() {
    return beschreibung;
  }

  public void setBeschreibung(String beschreibung) {
    this.beschreibung = beschreibung;
  }

  public boolean istClientEinstellung() {
    return client;
  }

  public void setClient(boolean client) {
    this.client = client;
  }

  public String getGruppe() {
    return gruppe;
  }

  public void setGruppe(String gruppe) {
    this.gruppe = gruppe;
  }

  public Class<?> getKlasse() {
    return klasse;
  }

  public void setKlasse(Class<?> klasse) {
    this.klasse = klasse;
  }

  public boolean istMitarbeiterEinstellung() {
    return mitarbeiter;
  }

  public void setMitarbeiter(boolean mitarbeiter) {
    this.mitarbeiter = mitarbeiter;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStandardWert() {
    return standardWert;
  }

  public void setStandardWert(String standardWert) {
    this.standardWert = standardWert;
  }

  public EinstellungTest getTest() {
    return test;
  }

  public void setTest(EinstellungTest test) {
    this.test = test;
  }

  public Liste<String> getWerte() {
    return werte;
  }

  public void setWerte(Collection<String> werte) {
    this.werte.clear();
    this.werte.addAll(werte);
  }

  public void addWert(String wert) {
    this.werte.add(wert);
  }
  
  public void setTyp(int typ) {
    this.typ = typ;
  }


  
  public boolean istEditable() {
    return typ != TYP_BOOLEAN;
  }

  /**
   * Liefert eine Liste sinnvoller Werte für die
   * Einstellung
   * @return
   */
  public Liste<String> getWerteAuswahl() {
    Liste<String> liste = new Liste<String>();
    liste.setZeigeDoppelteEintraege(false);
    liste.setSortierung(Liste.StringSortierung);
    
    if (typ == TYP_BOOLEAN) {
      liste.add("TRUE");
      liste.add("FALSE");
    } else {
      if (standardWert != null && 
          standardWert.trim().length() > 0) 
        liste.add(standardWert);
      
      liste.addAll(werte);
      liste.addAll(Datenbank.getInstance().getEinstellungFactory().getBenutzeWerte(name));
    }
    
    return liste;
  }

  public String checkWert(String wert) {    
    switch (typ) {
      case TYP_BOOLEAN:
        if (wert != null && 
          (wert.equalsIgnoreCase("true") ||
           wert.equalsIgnoreCase("false")))
          return null;

        return "Der Wert '" + wert + 
          "' kann nicht als boolscher Wert interpretiert werden!";
      case TYP_INT:
        if (wert != null) {
          try {
            Integer.parseInt(wert);
            return null;
          } catch (NumberFormatException e) {            
          }
        }
        
        return "Der Wert '" + wert + 
          "' kann nicht als Zahl interpretiert werden!";           
      case TYP_DOUBLE:
        if (wert != null) {
          try {
            Double.parseDouble(wert);
            return null;
          } catch (NumberFormatException e) {            
          }
        }
        
        return "Der Wert '" + wert + 
          "' kann nicht als Zahl interpretiert werden!";           
      case TYP_STRING:
        return null;
      case TYP_CLASS:
        if (wert != null && wert.length() > 0 && !wert.equalsIgnoreCase("null")) {
          try {
            Class<?> klasseWert = Class.forName(wert);
            if (klasse.isAssignableFrom(klasseWert)) return null;
          } catch (ClassNotFoundException e) {
          }        
          return "Der Wert '" + wert + 
          "' kann nicht als Klasse vom Typ '"+klasse.getName()+"' " +
          "interpretiert werden!";                                     
        }
        return null;        
      case TYP_FILE:
        if (wert != null) {
          File file = new File(wert);
          if (file.exists() && file.isFile())
            return null;
        }
        
        return "Die Datei '"+wert+"' existiert nicht!";        
      case TYP_DIR:
        if (wert != null) {
          File file = new File(wert);
          if (file.exists() && file.isDirectory())
            return null;
        }
        
        return "Das Verzeichnis '"+wert+"' existiert nicht!";        
    }
    
    return null;
  }

}
