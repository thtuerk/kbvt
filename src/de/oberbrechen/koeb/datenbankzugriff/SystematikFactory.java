package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;

/**
 * Dieses Interface repräsentiert eine Factory für Systematiken..
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface SystematikFactory extends DatenbankzugriffFactory<Systematik> {

  /**
   * Löscht alle Systematiken aus der Datenbank. Ist ein Medium einer
   * Systematik zugeordnet, wird dabei diese Zuordnung entfernt.
   */
  public void loescheAlleSystematiken();
  
  /**
   * Liefert eine unsortierte Liste aller Systematiken, die in der Datenbank
   * eingetragen sind.
   *
   * @return die Liste der Systematiken
   */
  public SystematikListe getAlleSystematiken();

  /**
   * Liefert eine unsortierte Liste aller Systematiken, die keine
   * Obersystematiken besitzen, die also Hauptsystematiken bilden.
   *
   * @return die Liste der Systematiken
   */
  public SystematikListe getAlleHauptSystematiken();

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Systematik-Objekt
   * 
   * @return das neue Systematik-Objekt
   */
  public Systematik erstelleNeu(); 

  /**
   * Liefert die Systematik mit dem übergebenen Namen aus der Datenbank oder
   * aus dem Cache.
   * @return das neue Systematik-Objekt
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  public Systematik get(String name) throws DatenNichtGefundenException, DatenbankInkonsistenzException; 
}