package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;

/**
 * Dieses Interface repräsentiert eine Systematik.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface Systematik extends Datenbankzugriff {

  /**
   * Liefert der Namen der Systematik
   * @return den Namen der Systematik
   */
  public String getName();

  /**
   * Setzt den Namen der Systematik
   * @param name der neue Name der Systematik
   */
  public void setName(String name);

  /**
   * Setzt die Beschreibung der Systematik
   * @param beschreibung die neue Beschreibung der Systematik
   */
  public void setBeschreibung(String beschreibung);
  
  /**
   * Liefert die Beschreibung der Systematik
   * @return die Beschreibung der Systematik
   */
  public String getBeschreibung();

  /**
   * Liefert eine Liste aller Untersystematiken der Systematik. Eine Systematik
   * ist dabei immer Untersystematik von sich selbst.
   * @return eine Liste aller Untersystematiken der Systematik
   */
  public SystematikListe getAlleUntersystematiken();

  /**
   * Liefert eine Liste aller Obersystematiken der Systematik.
   * @return eine Liste aller Obersystematiken der Systematik
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  public SystematikListe getAlleObersystematiken() throws DatenNichtGefundenException, DatenbankInkonsistenzException;

  /**
   * Liefert eine Liste der direkten Untersystematiken der Systematik.
   * @return eine Liste der direkten Untersystematiken der Systematik
   */
  public SystematikListe getDirekteUntersystematiken();

  /**
   * Liefert die direkte Obersystematik der Systematik.
   * @return die direkte Obersystematik der Systematik
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  public Systematik getDirekteObersystematik() throws DatenbankInkonsistenzException;

  /**
   * Setzt die direkte Obersystematik der Systematik.
   * @param systematik die neue direkte Obersystematik der Systematik
   */
  public void setDirekteObersystematik(Systematik systematik);

  /**
   * Bestimmt, ob die Systematik eine Untersystematik der übergebenen
   * Systematik ist.
   * @param systematik
   * @return true gdw wenn dies der Fall ist
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  public boolean istUntersystematikVon(Systematik systematik) throws DatenbankInkonsistenzException;  
}