package de.oberbrechen.koeb.einstellungen;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;

/**
* Diese Klasse repräsentiert eine Ausleihordnung einer Bücherei.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public abstract class Ausleihordnung {

  /**
   * Liefert eine Instanz der aktuell konfigurierten Ausleihordnung.
   * @return eine Instanz der aktuell konfigurierten Ausleihordnung
   */
  public static Ausleihordnung getInstance() {
    return Buecherei.getInstance().getAusleihordnung();
  }

  /**
   * Berechnet die Mahngebühren für die übergebene Liste von Ausleihen
   * zum aktuellen Zeitpunkt
   * @param liste die AusleihenListe, für die die Mahngebühren berechnet werden
   *   sollen
   */
  public double berechneMahngebuehren(AusleihenListe liste) {
    double summe = 0;

    for (Ausleihe ausleihe : liste) {
      summe += berechneMahngebuehren(ausleihe);
    }

    return summe;
  }
  
  /**
   * Berechnet die Mahngebühren für die uebergebene Ausleihe zum
   * aktuellen Zeitpunkt
   * @param ausleihe die Ausleihe, für die die Mahngebühren berechnet werden
   *   sollen
   */
  public double berechneMahngebuehren(Ausleihe ausleihe) {
    AusleihenListe liste = new AusleihenListe();
    liste.add(ausleihe);
    return berechneMahngebuehren(liste);
  }  
  
  /**
   * Liefert Informationen, ob und wie die übergebene Ausleihe am 
   * übergebenen Datum verlängert werden kann.
   * @param datum das Datum, an dem die Verlängerung getätigt werden soll   
   * @param ausleihe die zu verlaengernde Ausleihe
   */
  public VerlaengerungsInformationen getVerlaengerungsInformationen(
      Ausleihe ausleihe, Date datum) {
    Date von = ausleihe.getSollRueckgabedatum();
    if (von != null && datum.after(von)) von = datum;

    if (von == null)
      return new VerlaengerungsInformationen(false, "Noch nicht ausgeliehen", null, null);
      
    Date bis = getAusleihenBisDatum(ausleihe.getMedium(), von);
    return new VerlaengerungsInformationen(true, null, von, bis);    
  }

  /**
   * Bestimmt das Sollrückgabedatum für das übergebene Medium vorausgesetzt,
   * dass es am übergebenen Datum ausgeliehen wird.
   *
   * @param medium das Medium, das ausgeliehen werden soll
   * @param datum das Datum, ab dem das Medium ausgeliehen werden soll,
   *   null wird als das heutige Datum interpretiert
   * @return das Sollrückgabedatum
   */
  public abstract Date getAusleihenBisDatum(Medium medium, Date datum);
}