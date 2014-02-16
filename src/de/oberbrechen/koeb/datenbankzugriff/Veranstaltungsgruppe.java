package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Dieses Interface repräsentiert eine Veranstaltungsgruppe.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Veranstaltungsgruppe extends Datenbankzugriff {

  /**
   * Liefert der Namen der Veranstaltungsgruppe
   * @return den Namen der Veranstaltungsgruppe
   */
  public String getName();
  
  /**
   * Liefert die Beschreibung der Veranstaltungsgruppe
   * @return die Beschreibung der Veranstaltungsgruppe
   */
  public String getBeschreibung();
  
  /**
   * Liefert den gekürzten Namen der Veranstaltungsgruppe
   * @return den gekürzten Namen der Veranstaltungsgruppe
   */
  public String getKurzName();
  
  /**
   * Liefert das Heimatverzeichnis der Veranstaltungsgruppe
   * @return die Heimatverzeichnis der Veranstaltungsgruppe
   */
  public String getHomeDir();
        
  /**
   * Setzt die Beschreibung der Veranstaltungsgruppe
   * @param beschreibung die neue Beschreibung der Veranstaltungsgruppe
   */
  public void setBeschreibung(String beschreibung);

  /**
   * Setzt das Heimatverzeichnis der Veranstaltungsgruppe
   * @param homeDir das neue Heimatverzeichnis der Veranstaltungsgruppe
   */
  public void setHomeDir(String homeDir);

  /**
   * Setzt den gekürzten Namen der Veranstaltungsgruppe
   * @param kurzName den neue gekürzte Name der Veranstaltungsgruppe
   */
  public void setKurzName(String kurzName);

  /**
   * Setzt den Namen der Veranstaltungsgruppe
   * @return name der neue Name der Veranstaltungsgruppe
   */
  public void setName(String name);  
}