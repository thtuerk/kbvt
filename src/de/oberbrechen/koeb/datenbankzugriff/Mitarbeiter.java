package de.oberbrechen.koeb.datenbankzugriff;

/**
 * Dieses Interface repräsentiert einen Benutzer der Bücherei.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface Mitarbeiter extends Datenbankzugriff {

  public static final int BERECHTIGUNG_STANDARD = 0;
  public static final int BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE = 1;
  public static final int BERECHTIGUNG_BESTAND_EINGABE = 2;
  public static final int BERECHTIGUNG_ADMIN = 4;
  static final int EHEMALIGER_MITARBEITER = 8;
  
  /**
   * Setzt den Mitarbeiterbenutzernamen des Benutzers für die elektronische
   * Anmeldung
   * @param benutzername Mitarbeiterbenutzername des Benutzers
   */
  public void setMitarbeiterBenutzername(String benutzername);

  /**
   * Setzt das Mitarbeiter-Passwort des Mitarbeiters für die elektronische
   * Anmeldung. Beachten
   * Sie bitte, dass nicht das Passwort selbst, sondern die zugehörige
   * Prüfziffer nach dem MD5-Algorithmus gespeichert wird.
   * @param Passwort Passwort des Benutzers
   * @see #checkMitarbeiterPasswort
   */
  public void setMitarbeiterPasswort(String passwort);

  /**
   * Liefert den Mitarbeiterbenutzernamen des Mitarbeiters für die
   * elektronische Anmeldung
   * @return Mitarbeiterbenutzername des Benutzers
   */
  public String getMitarbeiterBenutzername();

  /**
   * Überprüft, ob das übergebene Passwort dem Mitarbeiter-Passwort des
   * Mitarbeiters entspricht
   * @param passwort das zu testende Passwort
   * @return <code>TRUE</code> gdw das Passwort gültig ist
   */
  public boolean checkMitarbeiterPasswort(String passwort);

  /**
   * Überprüft, ob dieser Mitarbeiter ein Mitarbeiter-Passwort verwendet
   * @return <code>TRUE</code> gdw der Mitarbeiter ein Mitarbeiter-Passwort
   *   verwendet
   */
  public boolean istMitarbeiterPasswortGesetzt();

  /**
   * Bestimmt, ob der Benutzer die übergebene Berechtigung besitzt. Die zur
   * Verfügung stehenden Berechtigungen sind über statische Konstanten dieser
   * Klasse festgelegt.
   *
   * @param berechtigung die Berechtigung, von der überprüft werden soll, ob
   *   dieser Benutzer sie besitzt
   * @return <code>TRUE</code> gdw. der Benutzer die übergebene Berechtigung
   *   besitzt
   */
  public boolean besitztBerechtigung(int berechtigung);

  /**
   * Liefert den Benutzer, dem der Mitarbeiter entspricht. Über den Benutzer 
   * können Name, Anschrift, Telefonnr usw. des Benutzer abgefragt werden.
   * @return den Benutzer, dem der Mitarbeiter entspricht
   */
  public Benutzer getBenutzer();
  
  /**
   * Setzt oder entfernt die übergebene Berechtigung.
   * @param berechtigung die zu ändernde Berechtigung
   * @param wert <code>true</code>, wenn die Berechtigung erteilt werden soll<br>
   * <code>false</code>, wenn die Berechtigung entzogen werden sol
   */
  public void setBerechtigung(int berechtigung, boolean wert);
  
  /**
   * Setzt, ob der Mitarbeiter ein ehemaliger Mitarbeiter oder noch
   * aktiv ist.
   * @param aktiv gibt an ob der Mitarbeiter noch aktiv ist
   */
  public void setAktiv(boolean aktiv);
  
  /**
   * Bestimmt, ob der Mitarbeiter ein ehemaliger Mitarbeiter oder noch
   * aktiv ist.
   * @return ob der Mitarbeiter noch aktiv ist
   */
  public boolean istAktiv();
  
}