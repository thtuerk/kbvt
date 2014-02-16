package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Hashtable;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;

/**
 * Dieses Klasse stellt einige grundlegende 
 * Funktionenen einer DatenbankzugriffFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractDatenbankzugriffFactory<T extends Datenbankzugriff> 
  implements DatenbankzugriffFactory<T> {

  protected Hashtable<Integer, T> cache;
  
  /**
   * Registriert die Factory bei der Datenbank
   */
  public AbstractDatenbankzugriffFactory() {
    cache = new Hashtable<Integer, T>();
    Datenbank.getInstance().registriereDatenbankzugriffFactory(this);    
  }
  
  public void fuegeInCacheEin(T datenbankzugriff) {
    Integer idWrapper = new Integer(datenbankzugriff.getId());
    cache.put(idWrapper, datenbankzugriff);
  }
    
  public T ladeAusCache(int id) {
    Integer idWrapper = new Integer(id);
    return cache.get(idWrapper);    
  }
  
  public T get(int id) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    Integer idWrapper = new Integer(id);
    T erg = cache.get(idWrapper);
    if (erg == null) {
      erg = ladeAusDatenbank(id);
      cache.put(idWrapper, erg);
    }
    return erg;
  }

  public void clearCache() {
    cache.clear();
  }
}