package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.framework.HexEncoder;
import de.oberbrechen.koeb.framework.KonfigurationsDatei;

/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Einstellung. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractEinstellung extends AbstractDatenbankzugriff 
  implements Einstellung {
    
  protected String name;
  protected String wert;
  protected Client client;
  protected Mitarbeiter mitarbeiter;
  protected static boolean zeigeDebugInformationen;
    
  static {
    KonfigurationsDatei confDatei =
      KonfigurationsDatei.getStandardKonfigurationsdatei();
    zeigeDebugInformationen = false;
    
    String booleanString = confDatei.getProperty("zeigeDebug");
    if (booleanString != null && booleanString.equalsIgnoreCase("true"))
      zeigeDebugInformationen = true;
  }
    
    
  public String getName() {
    return name;
  }
  
  public String getWert() {
    return wert;
  }
   
  public Client getClient() {
    return client;
  }

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public void setWert(String string) {
    setIstNichtGespeichert();
    wert = string;
  }
  
  public String toString() {
    return toDebugString();
  }
  
  public String toDebugString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Client:      ");
    if (getClient() != null) {
      ausgabe.append(this.getClient().toDebugString());
    } else {
      ausgabe.append("-"); 
    }

    ausgabe.append("\nMitarbeiter: ");
    if (getMitarbeiter() != null) {
      ausgabe.append(getMitarbeiter().getBenutzer().getName());
    } else {
      ausgabe.append("-"); 
    }
    
    ausgabe.append("\nName:        ");
    ausgabe.append(getName());

    ausgabe.append("\nWert:        ");
    ausgabe.append(getWert());

    return ausgabe.toString();
  }
  
  public boolean getWertBoolean() throws UnpassendeEinstellungException {
    String booleanString = getWert();
    if (booleanString != null && booleanString.equalsIgnoreCase("true"))
      return true;
    if (booleanString != null && booleanString.equalsIgnoreCase("false"))
      return false;

    throw new UnpassendeEinstellungException(
      "Der Wert '" + booleanString + 
      "' kann nicht als boolscher Wert interpretiert werden!");
  }

  public double getWertDouble() throws UnpassendeEinstellungException {
    String doubleString = getWert();
    try {
      if (doubleString == null) throw new NumberFormatException();
      return Double.parseDouble(doubleString);
    } catch (NumberFormatException e) {
      throw new UnpassendeEinstellungException(
        "Der Wert '" + doubleString
          + "' kann nicht als Zahl interpretiert werden!");
    }
  }

  public float getWertFloat() throws UnpassendeEinstellungException {
    String floatString = getWert();
    try {
      if (floatString == null) throw new NumberFormatException();
      return Float.parseFloat(floatString);
    } catch (NumberFormatException e) {
      throw new UnpassendeEinstellungException(
          "Der Wert '" + floatString
          + "' kann nicht als Zahl interpretiert werden!");
    }
  }
  
  public int getWertInt() throws UnpassendeEinstellungException {
    try {      
      if (getWert() == null) throw new NumberFormatException();      
      return Integer.parseInt(getWert());
    } catch (NumberFormatException e) {
      throw new UnpassendeEinstellungException(
        "Der Wert '" + getWert() + 
        "' kann nicht als Zahl interpretiert werden!");
    }
  }

  public Object getWertObject(Class<?> typ) throws UnpassendeEinstellungException {
    String className = getWert();

    if (className == null || className.length() == 0 || 
        className.equalsIgnoreCase("null"))
      return null;   
    
    Object retVal = getInstanz(className);
    if (retVal == null || !typ.isInstance(retVal)) {
      throw new UnpassendeEinstellungException(
        "Der Wert '" + className
          + "' kann nicht als Klassenname "
          + "einer Klasse mit paraterlosem Konstruktor vom Typ "
          + typ.getName()
          + " interpretiert werden!");
    }

    return retVal;
  }

  private Object getInstanz(String className) {
    if (className == null) return null;
    
    Object retVal = null;
    try {
      retVal = Class.forName(className).newInstance();
    } catch (ClassNotFoundException cnfe) {
    } catch (InstantiationException ie) {
    } catch (IllegalAccessException iae) {
    }

    return retVal;
  }

  public void setWertBoolean(boolean wert) {
    if (wert)
      setWert("TRUE");
    else
      setWert("FALSE");
  }

  public void setWertDouble(double wert) {
    setWert(Double.toString(wert));
  }

  public void setWertFloat(float wert) {
    setWert(Float.toString(wert));
  }
  
  public void setWertInt(int wert) {
    setWert(Integer.toString(wert));
  }
  
  public boolean getWertBoolean(boolean standard) {
    try {
      return getWertBoolean();
    } catch (UnpassendeEinstellungException e) {
      parseFehlerMeldung(e, ""+standard);
      setWertBoolean(standard);
      saveCatched();
      return standard;
    }
  }

  public double getWertDouble(double standard) {
    try {
      return getWertDouble();
    } catch (UnpassendeEinstellungException e) {
      parseFehlerMeldung(e, ""+standard);
      setWertDouble(standard);
      saveCatched();
      return standard;
    }
  }

  public float getWertFloat(float standard) {
    try {
      return getWertFloat();
    } catch (UnpassendeEinstellungException e) {
      parseFehlerMeldung(e, ""+standard);
      setWertFloat(standard);
      saveCatched();
      return standard;
    }
  }
  
  public int getWertInt(int standard) {
    try {
      return getWertInt();
    } catch (UnpassendeEinstellungException e) {
      parseFehlerMeldung(e, ""+standard);
      setWertInt(standard);
      saveCatched();
      return standard;
    }
  }

  private void saveCatched() {
    try {
      save();
    } catch (DatenbankzugriffException e2) {
      ErrorHandler.getInstance().handleException(e2, "Einstellung konne " +
          "nicht gespeichert werden!", false);
    }    
  }
  
  public Object getWertObject(Class<?> typ, Class<?> standard)
    throws UnpassendeEinstellungException {        

    Object result = null;
    try {      
      result = getWertObject(typ);
    } catch (UnpassendeEinstellungException e) {
      parseFehlerMeldung(e, ""+standard.getName());
    }
    if (result != null) return result;


    try {
      setWert(standard==null?null:standard.getName());
      saveCatched();    
      return getWertObject(typ);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(
        e,
        "Probleme beim Laden der Einstellung\n"
          + toDebugString()
          + "\n\n"
          + "Auch der Standardwert '"
          + standard
          + "' kann nicht als Klassenname "
          + "einer Klasse mit paraterlosem Konstruktor interpretiert werden!",
          true);
       throw e;
    }
  }

  public String getWert(String standard) {
    String wert = getWert();
    if (wert == null) {
      parseFehlerMeldung(new UnpassendeEinstellungException(
        "Die Einstellung enthielt den Wert null!"), standard); 
      setWert(standard);
      saveCatched();
      wert = standard;
    }
    
    return wert;
  }
  
  public String getWertHex(String standard) {
    String hexWert = getWert();
    
    String wert = HexEncoder.hexDecodeString(hexWert);
    
    if (wert == null || wert.length() == 0) {
      parseFehlerMeldung(new UnpassendeEinstellungException(
      "Die Einstellung enthielt den Wert null!"), standard); 
      setWertHex(standard);
      saveCatched();
      wert = standard;
    }
    
    return wert;
  }
  
  public void setWertHex(String wert) {
    setWert(HexEncoder.hexEncodeString(wert));
  }
  
  
  /**
   * Zeigt die Standardfehlermeldung für eine UnpassendeEinstellungException
   * an.  
   * @param e die geworfene Exception
   * @param standard der Standardwert, der gesetzt werden soll
   */
  private void parseFehlerMeldung(Exception e, String standard) {    
    if (zeigeDebugInformationen) {
      ErrorHandler.getInstance().handleException(e,
          "Probleme beim Laden der Einstellung\n"
          + toDebugString()
          + "\n\n"
          + "Es wird versucht, den Standardwert '"
          + standard
          + "' zu setzen!",
          false);    
    }
  }  
}
