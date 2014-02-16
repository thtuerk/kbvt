package de.oberbrechen.koeb.einstellungen;

import java.text.Collator;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
* Diese Klasse repräsentiert die Einstellungen, die abhängig von der
* jeweiligen Bücherei sind. Dies sind die Ausleihordnung,
* Standardwohnort der Benutzer u.ä.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public abstract class Buecherei {

  private static Buecherei instance;
  protected Comparator<String> medienNrComparator = null;
  
  static {
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();
      
    Einstellung buechereiEinstellung = einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.Buecherei","instanz");
      
    BuechereiFactory buechereiFactory = null;
    try {
      buechereiFactory = (BuechereiFactory)
        buechereiEinstellung.getWertObject(BuechereiFactory.class, 
        KonfigurierbareBuechereiFactory.class);
      instance = buechereiFactory.createBuecherei();
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
  }
    
  
  /**
   * Liefert eine Instanz der aktuell konfigurierten Buecherei.
   * @return eine Instanz der aktuell konfigurierten Buecherei
   */
  public static Buecherei getInstance() {
    return instance;
  }

  /**
   * Liefert den Namen der Bücherei
   * @return den Namen der Bücherei
   */
  public abstract String getBuechereiName();
  
  /**
   * Liefert die Ausleihordnung der Buecherei.
   * @return die Ausleihordnung der Buecherei
   */
  public abstract Ausleihordnung getAusleihordnung();

  /**
   * Bestimmt, ob die Bücherei am übergebenen Datum geöffnet hat
   * @param datum das Datum, das überprüft werden soll
   * @return <code>true</code> gdw. die Bücherei am übergebenen Datum geöffnet
   *   hat
   */
  public abstract boolean istGeoeffnet(Calendar datum);

  /**
   * Bestimmt, ob die Bücherei am übergebenen Datum geöffnet hat
   * @param datum das Datum, das überprüft werden soll
   * @return <code>true</code> gdw. die Bücherei am übergebenen Datum geöffnet
   *   hat
   */
  public boolean istGeoeffnet(Date datum) {
    Calendar neuerCalendar = Calendar.getInstance();
    neuerCalendar.setTime(datum);
    return istGeoeffnet(neuerCalendar);
  }
  
  /**
   * Liefert das erste Datum nach dem übergebenen Datum, an dem
   * die Bücherei geöffnet hat.
   * @param beginn das Datum, ab dem gesucht wird
   * @return das erste Öffnungsdatum nach dem übergebenen Datum
   */
  public Date getNaechstesOeffnungsdatum(Date beginn) {
    Calendar neuerCalendar = Calendar.getInstance();
    neuerCalendar.setTime(beginn);
    
    while (!istGeoeffnet(neuerCalendar)) {
      neuerCalendar.add(Calendar.DATE, 1);
    }
    
    return neuerCalendar.getTime();    
  }

  /**
   * Liefert das nächste Datum, an dem
   * die Bücherei geöffnet hat.
   * @return das erste Öffnungsdatum nach dem übergebenen Datum
   */
  public Date getNaechstesOeffnungsdatum() {
    return getNaechstesOeffnungsdatum(new Date());
  }

  /**
   * Berechnet die Kosten für einen Internetzugang der übergebenen Dauer.
   * @param dauer die Dauer des Internetzugangs in Sekunden
   * @return die Kosten für diesen Internetzugang
   */
  public abstract double berechneInternetzugangsKosten(int dauer);

  /**
   * Liefert in Abhängigkeit vom Einstellungsdatum und vom Medientyp
   * eines Mediums einen Vorschlag für eine Mediennr.
   * @param medientyp der Medientyp des Mediums
   * @param einstellungsDatum das Einstellungsdatum des Mediums
   * @return die vorgeschlagene Mediennr.
   */
  public abstract String getStandardMedienNr(Medientyp medientyp, Date einstellungsDatum);
  
  /**
   * Liefert einen Comparator, der in der Lage ist, zwei Medien anhand ihrer
   * Mediennummer miteinander zu vergleichen
   * @return den Comparator
   */
  public Comparator<String> getMedienNrComparator() {
    if (medienNrComparator == null) 
      medienNrComparator = erstelleMedienNrComparator();
    
    return medienNrComparator;
  }

  /**
   * Erstellt einen neuen Comparator, der in der Lage ist, zwei Medien anhand ihrer 
   * Mediennummer miteinander zu vergleichen. Dieser wird zwischengespeichert 
   * und kann mittels getMedienNrComparator abgefragt werden. 
   * @return den Comparator
   */   
  protected Comparator<String> erstelleMedienNrComparator() {
    final Collator c = Collator.getInstance();
    return (new Comparator<String>() {
      public int compare(String o1, String o2) {
        return c.compare(o1, o2);
      }
    });
  }
  
  
  /**
   * Liefert die eMail-Adresse des System-Administrators
   * @return die eMail-Adresse des System-Administrators
   */
  public abstract String getAdminEmail();

  /**
   * Liefert die eMail-Adresse der Bücherei
   * @return die eMail-Adresse der Bücherei
   */
  public abstract String getBuechereiEmail();
}