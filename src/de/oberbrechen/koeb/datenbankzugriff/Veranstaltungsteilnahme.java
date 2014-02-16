package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

/**
 * Dieses Interface repräsentiert eine Veranstaltungsteilnahme der Bücherei. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Veranstaltungsteilnahme extends Datenbankzugriff {

  public static final int ANMELDESTATUS_NICHT_ANGEMELDET = -1;
  public static final int ANMELDESTATUS_ANGEMELDET = 0;
  public static final int ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND = 1;
  public static final int ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND = 2;  

  /**
   * Liefert den Benutzer der an der Veranstaltung teilnimmt
   * @return den Benutzer der an der Veranstaltung teilnimmt
   */
  public Benutzer getBenutzer();

  /**
   * Liefert die Veranstaltung, an der der Benutzer teilnimmt
   * @return die Veranstaltung, an der der Benutzer teilnimmt
   */
  public Veranstaltung getVeranstaltung();

  /**
   * Liefert die Anmeldenummer des Benutzers für die Veranstaltung
   * @return die Anmeldenummer des Benutzers für die Veranstaltung
   */
  public int getAnmeldeNr();

  /**
   * Liefert die Anmeldenummer des Benutzers für die Veranstaltung
   * @return die Anmeldenummer des Benutzers für die Veranstaltung
   */
  public Date getAnmeldeDatum();

  /**
   * Setzt das Anmeldedatum des Benutzers für die Veranstaltung
   * @param datum das neue Anmeldedatum
   */
  public void setAnmeldeDatum(Date datum);

  /**
   * Liefert die Bemerkungen zur Teilnahme des Benutzers an der Veranstaltung
   * @return die Bemerkungen zur Teilnahme des Benutzers an der Veranstaltung
   */
  public String getBemerkungen();

  /**
   * Bestimmt den Status der Teilnahme. 
   * @see getStatus()
   */
  public void setStatus(int status);
  
  /**
   * Liefert den Status der Teilnahme. Die Teilnahme kann eine echte Anmeldung 
   * oder einen Eintrag auf der Warteliste beschreiben. Bei den 
   * Wartelisteneinträgen wird zusätzlich zwischen blockierenden und nicht 
   * blockierenden Einträgen unterschieden. Blockierende Wartelisteneinträge
   * erfordern, dass alle weiteren Anmeldungen ebenfalls auf der Warteliste
   * eingetragen werden. Für die einzelnen Statusmöglichkeiten existieren
   * öffentliche Konstanten dieses Interfaces.
   * @see setStatus()
   */
  public int getStatus();
  
  /**
   * Liefert, ob die Teilnahme eine echte Anmeldung oder nur ein
   * Eintrag auf der Warteliste ist.
   */
  public boolean istAufWarteListe();

  /**
   * Liefert, ob die Teilnahme auf der blockierenden Warteliste ist.
   */
  public boolean istAufBlockierenderWarteListe();

  /**
   * Setzt die Bemerkungen zur Teilnahme des Benutzers an der Veranstaltung
   * @param bemerkungen die neuem Bemerkungen
   */
  public void setBemerkungen(String bemerkungen);
}