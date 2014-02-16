package de.oberbrechen.koeb.server.medienabfragen;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
* Dieses Interface repräsentiert einen Methode, externe Daten über ein
* Medium anzufragen. Das Standardscenaria ist ein Webdienst, der es erlaubt zu einer
* gegebenen ISBN-Nr. den Titel, Autor, Kurzbeschreibung etc. zu ergängzen. Das Interface
* ist jedoch allgemeiner.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public abstract class Medienabfrage {
  private static int timeout = Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
      "de.oberbrechen.koeb.server.medienabfragen.Medienabfrage", "timeout").getWertInt(10000);      
  private static Medienabfrage instance = null;
  
  public static Medienabfrage getInstance() {
    if (instance == null) {
      try {
        instance = (Medienabfrage) Datenbank.getInstance().getEinstellungFactory().getEinstellung(
            "de.oberbrechen.koeb.server.medienabfragen.Medienabfrage", "Implementierung").getWertObject(
                Medienabfrage.class, Thalia.class);
      } catch (UnpassendeEinstellungException e) {
        ErrorHandler.getInstance().handleException(e, false);
        instance = new LeereMedienAbfrage();
      }            
    }
    return instance;
  }
  
  /**
   * Versucht die gegebene ISBN nachzuschlagen. Wenn Daten gefunden werden, werden diese
   * geliefert, ansonsten null.
   */
  public abstract MedienabfrageErgebnis isbnNachschlagen(ISBN isbn);

  
  
  /**
   * technische Ekligkeit
   */
  private class MedienabfrageErgebnisRef {
    MedienabfrageErgebnis erg = null;
  }  
  public MedienabfrageErgebnis isbnNachschlagen(final ISBN isbn, long timeout) {
    final MedienabfrageErgebnisRef ergRef = new MedienabfrageErgebnisRef();
    Thread t = new Thread(new Runnable(){
      public void run() {
        ergRef.erg = isbnNachschlagen(isbn);
      }});
    t.start();
    try {
      t.join(timeout);
    } catch (InterruptedException e) {
      t.interrupt();
      return null;
    }
    return ergRef.erg;
  }

  /**
   * Ruft isbnNachschlagen mit einem Standardtimeout auf
   */
  public MedienabfrageErgebnis isbnNachschlagenMitTimeout(ISBN isbn) {
    return isbnNachschlagen(isbn, timeout);
  }
}
