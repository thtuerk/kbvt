package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.TerminListe;

/**
 * Dieses Interface repräsentiert eine Systematik.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Veranstaltung extends Datenbankzugriff {

  public static final int UNBEGRENZT = 0;
  
  /**
   * Liefert den Ansprechpartner der Veranstaltung
   * @return den Ansprechpartner der Veranstaltung
   */
  public String getAnsprechpartner();
  
  /**
   * Liefert die Beschreibung der Veranstaltung
   * @return die Beschreibung der Veranstaltung
   */
  public String getBeschreibung();

  /**
   * Liefert die Bezugsgruppe der Veranstaltung
   * @return die Bezugsgruppe der Veranstaltung
   */
  public String getBezugsgruppe();

  /**
   * Liefert die Kosten für diese Veranstaltung
   * @return die Kosten für diese Veranstaltung
   */
  public double getKosten();
  
  /**
   * Liefert die Kosten für diese Veranstaltung als String
   * @return die Kosten im Format "0.00 "+Waehrung
   */
  public String getKostenFormatiert();

  /**
   * Liefert den gekürzten Titel der Veranstaltung
   * @return den gekürzten Titel der Veranstaltung
   */
  public String getKurzTitel();

  /**
   * Liefert die maximale Teilnehmeranzahl. Eine maximale Teilnehmeranzahl
   * von 0 bedeutet, dass beliebig viele Teilnehmer erlaubt sind.
   */
  public int getMaximaleTeilnehmerAnzahl();

  /**
   * Liefert Liste aller Termine der Veranstaltung.
   * @return eine Liste der Termine der Veranstaltung
   */
  public TerminListe getTermine();
  
  /**
   * Liefert der Titel der Veranstaltung
   * @return den Titel der Veranstaltung
   */
  public String getTitel();

  /**
   * Liefert die Gruppe der Veranstaltung
   * @return die Gruppe der Veranstaltung
   */
  public Veranstaltungsgruppe getVeranstaltungsgruppe();

  /**
   * Liefert die Waehrung, in der die 
   * Kosten dieser Veranstaltung angegeben sind.
   * @return die Waehrung
   */
  public String getWaehrung();

  /**
   * Bestimmt, ob für die Veranstaltung eine Anmeldung erforderlich ist
   * @return <code>true</code> gdw für die Veranstaltung
   *   eine Anmeldung erforderlich ist
   */
  public boolean istAnmeldungErforderlich();

  /**
   * Setzt, ob für die Veranstaltung eine Anmeldung erforderlich ist
   * @param anmeldungErforderlich für die Veranstaltung
   *   eine Anmeldung erforderlich ist
   */
  public void setAnmeldungErforderlich(boolean anmeldungErforderlich);

  /**
   * Setzt den Ansprechpartner der Veranstaltung
   * @param der neue Ansprechpartner der Veranstaltung
   */
  public void setAnsprechpartner(String ansprechpartner);

  /**
   * Setzt die Beschreibung der Veranstaltung
   * @param beschreibung die neue Beschreibung der Veranstaltung
   */
  public void setBeschreibung(String beschreibung);

  /**
   * Setzt die Bezugsgruppe der Veranstaltung
   * @param bezugsgruppe die Bezugsgruppe der Veranstaltung
   */
  public void setBezugsgruppe(String bezugsgruppe);

  /**
   * Setzt die Kosten für diese Veranstaltung
   * @param kosten die neuen Kosten für diese Veranstaltung
   */
  public void setKosten(double kosten);
  
  /**
   * Setzt den gekürzten Titel der Veranstaltung
   * @return kurzTitel der neue gekürzte Titel der Veranstaltung
   */
  public void setKurzTitel(String kurzTitel);

  /**
   * Setzt die maximale Teilnehmeranzahl. Eine maximale Teilnehmeranzahl
   * von 0 bedeutet, dass beliebig viele Teilnehmer erlaubt sind.
   * @param maxTeilnehmerAnzahl die neue maximale Teilnehmeranzahl
   */
  public void setMaximaleTeilnehmerAnzahl(int maxTeilnehmerAnzahl);

  /**
   * Setzt die Termine der Veranstaltung.
   * @param termine die neuen Termine der Veranstaltung
   */
  public void setTermine(TerminListe termine);

  /**
   * Setzt den Titel der Veranstaltung
   * @param titel der neue Titel der Veranstaltung
   */
  public void setTitel(String titel);
  
  /**
   * Setzt die Gruppe der Veranstaltung
   * @param die neue Gruppe der Veranstaltung
   */
  public void setVeranstaltungsgruppe(
    Veranstaltungsgruppe veranstaltungsgruppe);

  /**
   * Setzt die Währung, in der die Kosten dieser Veranstaltung angegeben
   * sind.
   * @param waehrung die neue Währung
   */
  public void setWaehrung(String waehrung);
}