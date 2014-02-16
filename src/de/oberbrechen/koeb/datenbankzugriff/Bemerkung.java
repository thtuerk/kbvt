package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

/**
 * Dieses Interface repräsentiert eine Bemerkung. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Bemerkung extends Datenbankzugriff {


  /**
   * Liefert den Text der Bemerkung
   * @return den Text der Bemerkung
   */
  public String getBemerkung();

  /**
   * Setzt den Text der Bemerkung
   * @param bemerkung die neue Bemerkung
   */
  public void setBemerkung(String bemerkung);
  
  /**
   * Setzt, ob die Bemerkung aktuell ist
   */
  public void setAktuell(boolean aktuell);

  /**
   * Liefert, ob die Bemerkung aktuell ist
   */
  public boolean istAktuell();

  /**
   * Setzt das Datum
   */
  public void setDatum(Date datum);

  /**
   * Liefert das Datum
   */
  public Date getDatum();  
}