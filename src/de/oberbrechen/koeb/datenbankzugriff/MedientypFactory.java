package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedientypListe;

/**
 * Dieses Interface repräsentiert eine Factory für Medientypen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface MedientypFactory extends DatenbankzugriffFactory<Medientyp> {

  /**
   * Liefert den meist genutzten Medientypen, d.h. den Medientypen,
   * mit den meisten in der Datenbank eingetragenen Medien.
   * @return den meist genutzten Medientyp
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  public Medientyp getMeistBenutztenMedientyp() throws DatenNichtGefundenException, DatenbankInkonsistenzException;

  /**
   * Liefert eine alphabetisch sortierte Liste aller 
   * Medientypen, die in der Datenbank
   * eingetragen sind.
   */
  public MedientypListe getAlleMedientypen();
  
  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Medientyp-Objekt
   * 
   * @return das neue Medientyp-Objekt
   */
  public Medientyp erstelleNeu(); 

  /**
   * Liefert den Medientyp mit dem übergebenen Namen aus der Datenbank oder
   * aus dem Cache.
   * @return das neue Medientyp-Objekt
   * @throws DatenNichtGefundenException
   * @throws DatenbankInkonsistenzException
   */
  public Medientyp get(String name) throws DatenbankInkonsistenzException, DatenNichtGefundenException;  
}