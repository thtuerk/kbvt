package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.InternetfreigabenListe;

/**
 * Dieses Klasse stellt einige grundlegende 
 * Funktionen einer InternetfreigabeFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractInternetfreigabeFactory 
  extends AbstractDatenbankzugriffFactory<Internetfreigabe>
  implements InternetfreigabeFactory {

  private Hashtable<Client, Internetfreigabe> clientCache = new Hashtable<Client, Internetfreigabe>();
  private Hashtable<Client, Long> clientTimeCache = new Hashtable<Client, Long>();
  
  /**
   * Liefert eine unsortierte Liste aller Internetfreigaben, die im übergebenen
   * Monat getätigt wurden.
   * 
   * @param monat die Nr des Monats von 1 bis 12
   * @param jahr das Jahr
   */
  public InternetfreigabenListe 
    getAlleInternetFreigabenInMonat(int monat, int jahr) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(jahr, monat-1, 1);
    java.util.Date von = calendar.getTime();
    
    calendar.set(jahr, monat, 1);
    calendar.add(Calendar.DATE, -1);
    java.util.Date bis = calendar.getTime();    
    
    return getAlleInternetfreigabenInZeitraum(von, bis);
  }  

  /**
   * Das eigentliche Laden der aktuellen Freigabe aus der Datenbank
   * @throws DatenbankInkonsistenzException
   */
  protected abstract Internetfreigabe ladeAktuelleInternetfreigabe(Client client) throws DatenbankInkonsistenzException;   

  public Internetfreigabe getAktuelleInternetfreigabe(Client client) throws DatenbankInkonsistenzException {   
    //Ergebnisse 5 sek cachen    
    Long ladeZeitpunkt = clientTimeCache.get(client);
    if (ladeZeitpunkt == null || 
        System.currentTimeMillis()-ladeZeitpunkt.longValue() > 15000) {
      Internetfreigabe freigabe = ladeAktuelleInternetfreigabe(client);
      ladeZeitpunkt = new Long(System.currentTimeMillis());
      entferneAusClientCache(client);
      if (freigabe != null) {
        clientCache.put(client, freigabe);
        clientTimeCache.put(client, ladeZeitpunkt);
      }
      return freigabe;
    } else {
      return clientCache.get(client);
    }
  }
  
  protected void entferneAusClientCache(Client client) {
    clientCache.remove(client);
    clientTimeCache.remove(client);
  }  
  
  public boolean istInternetzugangFreigegeben(Client client) throws DatenbankInkonsistenzException {
    Internetfreigabe aktuelleFreigabe = 
      getAktuelleInternetfreigabe(client);
    if (aktuelleFreigabe == null) return false;
    
    return aktuelleFreigabe.istFreigegeben();
  }

  public Internetfreigabe freigeben(
      Benutzer benutzer, Client client, Mitarbeiter mitarbeiter) throws DatenbankzugriffException {
    return freigeben(benutzer, client, mitarbeiter, new Date());
  }

  public Internetfreigabe freigeben(Benutzer benutzer, Client client) throws DatenbankzugriffException {
    return freigeben(benutzer, client, Datenbank.getInstance().
        getMitarbeiterFactory().getAktuellenMitarbeiter(), new Date());
  }
}