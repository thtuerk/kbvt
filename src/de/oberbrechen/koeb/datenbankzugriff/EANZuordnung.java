package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.EAN;

/**
 * Dieses Interface stellt Methoden zur Verfügung, um Benutzer und Medien ihren
 * EAN 13-Nummern zuzuordnen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface EANZuordnung {

  /**
   * Liefert das Objekt, auf das die EAN-Nr verweist. Dies kann ein
   * Benutzer, ein Medium oder null sein.
   *
   * @param ean die EAN
   * @return Objekt, auf das die EAN-Nr verweist
   * @throws DatenNichtGefundenException
   * @throws DatenbankInkonsistenzException
   */
  public Object getReferenz(EAN ean);

  /**
   * Liefert zu dem übergebenen Benutzer seine Standard-EAN.
   * @param benutzer der Benutzer, dessen EAN bestimmt werden soll
   * @return die EAN des Benutzers
   */
  public EAN getBenutzerEAN(Benutzer benutzer);

  /**
   * Liefert zu dem übergebenen Medium seine Standard-EAN.
   * @param medium das Medium, dessen EAN bestimmt werden soll
   * @return die EAN des Mediums
   */
  public EAN getMediumEAN(Medium medium);  
}