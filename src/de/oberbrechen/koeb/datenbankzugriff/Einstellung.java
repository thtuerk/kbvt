package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnvollstaendigeDatenException;

/**
 * Dieses Interface repräsentiert eine Einstellung, die in der Datenbank
 * abgelegt ist.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Einstellung extends Datenbankzugriff {  

  /**
   * Liefert den Namen der Einstellung
   * @return den Namen der Einstellung
   */
  public String getName();
  
  /**
   * Liefert den Client, den die Einstellung betrift. Falls die Einstellung alle
   * Clients betrifft, wird <code>null</code> geliefert.
   * @return den Client, den die Einstellung betrift
   */
  public Client getClient();

  /**
   * Liefert den Mitarbeiter, den die Einstellung betrift. 
   * Falls die Einstellung alle Mitarbeiter betrifft, wird 
   * <code>null</code> geliefert.
   * @return den Mitarbeiter, den die Einstellung betrift
   */
  public Mitarbeiter getMitarbeiter();

  /**
   * Liefert den Wert der Einstellung als String
   * @return den Wert der Einstellung als String
   */
  public String getWert();
   
  /**
   * Liefert den Wert der Einstellung als String.
   * Ist der Wert null, so wird der Standardwert geliefert und 
   * direkt in der Einstellung gespeichert.
   * @param standard der Standardwert
   * @return den Wert der Einstellung als String
   */
  public String getWert(String standard);

  /**
   * Liefert den Wert der Einstellung als Integer
   * @return den Wert der Einstellung als Integer
   */
  public int getWertInt() throws UnpassendeEinstellungException;

  /**
   * Liefert den Wert der Einstellung als Integer. 
   * Kann der Wert nicht als Integer
   * interpretiert werden, wird der Standardwert geliefert und direkt in der
   * Einstellung gespeichert.
   * @param standard der Standardwert
   * @return den Wert der Einstellung als Integer
   */
  public int getWertInt(int standard);

  /**
   * Liefert den Wert der Einstellung als Double
   * @return den Wert der Einstellung als Double
   */
  public double getWertDouble() throws UnpassendeEinstellungException;

  /**
   * Liefert den Wert der Einstellung als Double. 
   * Kann der Wert nicht als Double
   * interpretiert werden, wird der Standardwert geliefert und direkt in der
   * Einstellung gespeichert.
   * @param standard der Standardwert
   * @return den Wert der Einstellung als Double
   */
  public double getWertDouble(double standard);

  /**
   * Liefert den Wert der Einstellung als Float
   * @return den Wert der Einstellung als Float
   */
  public float getWertFloat() throws UnpassendeEinstellungException;

  
  /**
   * Liefert den Wert der Einstellung als Float. 
   * Kann der Wert nicht als Float
   * interpretiert werden, wird der Standardwert geliefert und direkt in der
   * Einstellung gespeichert.
   * @param standard der Standardwert
   * @return den Wert der Einstellung als Float
   */
  public float getWertFloat(float standard);

  
  /**
   * Liefert den Wert der Einstellung als Boolean
   * @return den Wert der Einstellung als Boolean
   */
  public boolean getWertBoolean() throws UnpassendeEinstellungException;

  /**
   * Liefert den Wert der Einstellung als Boolean
   * Kann der Wert nicht als Boolean
   * interpretiert werden, wird der Standardwert geliefert und direkt in der
   * Einstellung gespeichert.
   * @param standard der Standardwert
   * @return den Wert der Einstellung als Boolean
   */
  public boolean getWertBoolean(boolean standard);

  /**
   * Der Wert der Einstellung wird als Klassenname interpretiert und 
   * versucht mit einem parameterlosen Konstruktor 
   * eine Instanz zu erzeugen. Diese wird zurückgeliefert. Dies ist insbesondere
   * dafür gedacht, Factories zu erzeugen, mit denen dann viele der eingentlich
   * benötigten Objekte gebaut werden können.
   * Kann kein Objekt des übergebenen Typs erzeugt werden, wird versucht ein
   * Objekt des Standardtyps, falls dieser nicht null ist, zu erzeugen. Scheitert 
   * auch dies, wird eine UnpassendeEinstellungException geworfen. Ist 
   * der Standardtyp null, wird null geliefert.  
   * @param standard der Standardtyp
   * @param typ der Typ, d.h. die Klasse oder das Interface des Objektes, das 
   *  zurückgeliefert werden soll     
   * @throws UnpassendeEinstellungException
   */
  public Object getWertObject(Class<?> typ, Class<?> standard) throws UnpassendeEinstellungException;
  
  /**
   * Liefert den Wert der Einstellung, die als HEX-Repräsentation eines
   * Strings betrachtet wird
   * @param standard der Standardwert
   * @return den Wert der Einstellung
   */
  public String getWertHex(String standard);      

  /**
   * Setzt den Wert der Einstellung
   * @param wert der neue Wert der Einstellung
   */
  public void setWert(String wert);

  /**
   * Setzt den Wert der Einstellung
   * @param wert der neue Wert der Einstellung
   */
  public void setWertInt(int wert);
  
  /**
   * Setzt den Wert der Einstellung
   * @param wert der neue Wert der Einstellung
   */
  public void setWertDouble(double wert);

  /**
   * Setzt den Wert der Einstellung
   * @param wert der neue Wert der Einstellung
   */
  public void setWertFloat(float wert);

  /**
   * Setzt den Wert der Einstellung
   * @param wert der neue Wert der Einstellung
   */
  public void setWertBoolean(boolean wert);

  /**
   * Setzt den Wert der Einstellung auf die HEX-Repräsentation des
   * übergebenen Strings.
   * @param wert der neue Wert der Einstellung
   */
  public void setWertHex(String wert);      
  
  /**
   * Speichert die Einstellung für alle Clients.
   */
  public void saveAlleClients() throws UnvollstaendigeDatenException;

  /**
   * Speichert die Einstellung für alle Mitarbeiter.
   */
  public void saveAlleMitarbeiter() throws UnvollstaendigeDatenException;
  
  /**
   * Speichert die Einstellung für alle Clients und Mitarbeiter.
   */
  public void saveAlleClientsUndMitarbeiter() throws UnvollstaendigeDatenException;
}
