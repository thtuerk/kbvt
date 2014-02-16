package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;

/**
 * Dieses Interface stellt eine Factory für allgemeine
 * Datenbankzugriffe da. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface DatenbankzugriffFactory<T extends Datenbankzugriff> {
  
  /**
   * Läd das Datenbankzugriff-Objekt mit der übergebenen
   * ID aus der Datenbank.
   * @param id die ID des zu ladenden Objekts
   * @throws DatenNichtGefundenException falls ein Objekt mit der
   *  übergebenen id nicht in der Datenbank existiert
   * @return das geladene Datenbankzugriff-Objekt
   * @throws DatenbankInkonsistenzException
   */
  public T ladeAusDatenbank(int id)
    throws DatenNichtGefundenException, DatenbankInkonsistenzException;

  /**
   * Versucht das Datenbankzugriff-Objekt mit der übergebenen
   * ID aus dem Cache zu holen.
   * @param id die ID des zu ladenden Objekts
   * @throws DatenNichtGefundenException falls ein Objekt mit der
   *  übergebenen id nicht in der Datenbank existiert
   * @return das geladene Datenbankzugriff-Objekt oder null falls es nicht
   *   im Cache gefunden wurde
   */
  public T ladeAusCache(int id);
    
  /**
   * Liefert das zur übergebenen ID passende Datenbankzugriff-Objekt, das
   * entweder aus dem Cache geholt oder neu erzeugt wird. 
   *
   * @param id die ID des zu ladenden Objekts
   * @throws DatenNichtGefundenException falls ein Objekt mit der
   *  übergebenen id nicht in der Datenbank existiert
   * @return das geladene Datenbankzugriff-Objekt
   * @throws DatenbankInkonsistenzException
   */
  public T get(int id) 
    throws DatenNichtGefundenException, DatenbankInkonsistenzException;

  /**
   * Fügt das übergebene Datenbankzugriff-Objekt in den Cache ein.
   * @param datenbankzugriff das einzufügende Objekt
   */
  public void fuegeInCacheEin(T datenbankzugriff);

  /**
   * Löscht alle im Cache enthaltenen Daten
   */
  public void clearCache();
}