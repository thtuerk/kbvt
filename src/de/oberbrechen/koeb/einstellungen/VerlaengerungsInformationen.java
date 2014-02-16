package de.oberbrechen.koeb.einstellungen;

import java.util.Date;

/**
 * Diese Klasse ist ein Datencontainer, der Informationen zu einer möglichen
 * Verlängerung kapselt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class VerlaengerungsInformationen {
  Date von, bis;
  boolean erlaubt;
  String bemerkungen;

  public VerlaengerungsInformationen(boolean erlaubt, String bemerkungen,
                                     Date von, Date bis) {
    this.erlaubt = erlaubt;
    this.bemerkungen = bemerkungen;
    this.von = von;
    this.bis = bis;
  }
  
  /**
   * Liefert, ob eine Verlängerung erlaubt ist.
   * @return ob eine Verlängerung erlaubt ist.
   */
  public boolean istErlaubt() {
    return erlaubt;
  }

  /**
   * Liefert Bemerkungen zur Verlängerung. Vor allem wird evtl. eine
   * Begründung geliefert, warum Verlängern nicht möglich ist.
   * @return Bemerkungen
   */
  public String getBemerkungen() {
    return bemerkungen;
  }

  /**
   * Liefert den Zeitpunkt, bis zu dem verlängert werden soll.
   * @return den Zeitpunkt, bis zu dem verlängert werden soll.
   */
  public Date getBis() {
    return bis;
  }


  /**
   * Liefert den Zeitpunkt, ab dem verlängert werden soll.
   * @return den Zeitpunkt, ab dem verlängert werden soll.
   */
  public Date getVon() {
    return von;
  }

}