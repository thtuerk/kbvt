package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

/**
 * Dieses Interface repräsentiert einen Benutzer der Bücherei.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Benutzer extends Datenbankzugriff {

  /**
   * Überprüft, ob dieser Benutzer einen Benutzernamen verwendet.
   * @return <code>TRUE</code> gdw. der Benutzer einen Benutzernamen verwendet
   */
  public boolean istBenutzernameGesetzt();
  
  /**
   * Überprüft, ob der Benutzername des Benutzers schon von einem
   * anderen Benutzer verwendet wird.
   * @return <code>TRUE</code> gdw der Benutzername schon von einem anderen
   * Benutzer verwendet wird
   */
  public boolean istBenutzernameSchonVergeben();

  /**
   * Überprüft, ob der übergebene Benutzername schon von einem anderen
   * Benutzer verwendet wird.
   * @param benutzername der zu testende Benutzername
   * @return <code>TRUE</code> gdw der Benutzername schon von einem anderen
   * Benutzer verwendet wird
   */
  public boolean istBenutzernameSchonVergeben(String benutzername);

  /**
   * Liefert den vollständigen Namen des Benutzers in der Form
   * "Vorname Nachname".
   * @return Name des Benutzers
   */
  public String getName();

  /**
   * Liefert den vollständigen Namen des Benutzers in der Form
   * "Nachname, Vorname".
   * @return Name des Benutzers
   */
  public String getNameFormal();

  /**
   * Liefert den Vornamen des Benutzers
   * @return Vorname des Benutzers
   */
  public String getVorname();

  /**
   * Liefert den Nachnamen des Benutzers
   * @return Nachname des Benutzers
   */
  public String getNachname();

  /**
   * Liefert, ob der Benutzer ein Bücherei-VIP ist 
   * @return <code>true</code> gwd. der Benutzer ein Bücherei-VIP ist
   */
  public boolean istVIP();

  /**
   * Setzt, ob der Benutzer ein Bücherei-VIP ist 
   * @param vip <code>true</code> gwd. der Benutzer ein Bücherei-VIP ist
   */
  public void setVIP(boolean vip);

  /**
   * Liefert die Adresse des Benutzers
   * @return Adresse des Benutzers
   */
  public String getAdresse();

  /**
   * Liefert den Ort des Benutzers
   * @return Ort des Benutzers
   */
  public String getOrt();

  /**
   * Liefert die Klasse des Benutzers
   * @return Klasse des Benutzers
   */
  public String getKlasse();

  /**
   * Liefert die Telefonnummer des Benutzers
   * @return Telefonnummer des Benutzers
   */
  public String getTel();

  /**
   * Liefert Faxnummer zum Benutzers
   * @return Faxnummer des Benutzers
   */
  public String getFax();

  /**
   * Liefert eMail-Adresse zum Benutzers
   * @return eMail-Adresse des Benutzers
   */
  public String getEMail();

  /**
   * Liefert den Benutzernamen des Benutzers für die elektronische Anmeldung
   * @return Benutzernamen des Benutzers
   */
  public String getBenutzername();

  /**
   * Liefert die Bemerkungen zum Benutzer
   * @return Bemerkungen zum Benutzer
   */
  public String getBemerkungen();

  /**
   * Liefert das Anmeldedatum des Benutzers
   * @return Anmeldedatum des Benutzers
   */
  public Date getAnmeldedatum();

  /**
   * Liefert das Geburtsdatum des Benutzers
   * @return Geburtsdatum des Benutzer
   */
  public Date getGeburtsdatum();

  /**
   * Setzt den Vornamen des Benutzers
   * @param vorname Vorname des Benutzers
   */
  public void setVorname(String vorname);

  /**
   * Setzt den Nachnamen des Benutzers
   * @param nachname Nachname des Benutzers
   */
  public void setNachname(String nachname);

  /**
   * Setzt die Adresse des Benutzers
   * @param adresse Adresse des Benutzers
   */
  public void setAdresse(String adresse);

  /**
   * Setzt den Ort des Benutzers
   * @param ort des Benutzers
   */
  public void setOrt(String ort);

  /**
   * Setzt die Klasse des Benutzers
   * @param klasse des Benutzers
   */
  public void setKlasse(String klasse);

  /**
   * Setzt die Telefonnummer des Benutzers
   * @param telefonnummer des Benutzers
   */
  public void setTel(String tel);

  /**
   * Setzt Faxnummer zum Benutzers
   * @param fax Faxnummer des Benutzers
   */
  public void setFax(String fax);

  /**
   * Setzt eMail-Adresse des Benutzers
   * @param eMail eMail-Adresse des Benutzers
   */
  public void setEMail(String eMail);

  /**
   * Setzt den Benutzernamen des Benutzers für die elektronische Anmeldung
   * @param benutzername Benutzernamen des Benutzers
   */
  public void setBenutzername(String benutzername);

  /**
   * Setzt das Passwort des Benutzers für die elektronische Anmeldung. Beachten
   * Sie bitte, dass nicht das Passwort selbst, sondern die zugehörige
   * Prüfziffer nach dem MD5-Algorithmus
   * @param passwort Passwort des Benutzers
   */
  public void setPasswort(String passwort);

  /**
   * Überprüft, ob das übergebene Passwort dem Passwort des Benutzer entspricht
   * @param passwort das zu testende Passwort
   * @return <code>TRUE</code> gdw das Passwort gültig ist
   */
  public boolean checkPasswort(String passwort);

  /**
   * Überprüft, ob dieser Benutzer ein Passwort verwendet
   * @return <code>TRUE</code> gdw der Benutzer ein Passwort verwendet
   */
  public boolean istPasswortGesetzt();

  /**
   * Setzt die Bemerkungen zum Benutzer
   * @param Bemerkungen Bemerkungen zum Benutzer
   */
  public void setBemerkungen(String Bemerkungen);

  /**
   * Setzt das Anmeldedatum des Benutzers
   * @param anmeldedatum Anmeldedatum des Benutzers
   */
  public void setAnmeldedatum(Date anmeldedatum);

  /**
   * Setzt das Geburtsdatum des Benutzers
   * @param geburtsdatum Geburtsdatum des Benutzer
   */
  public void setGeburtsdatum(Date geburtsdatum);

  /**
   * Berechnet das Alter des Benutzers aus dem Geburtsdatum und dem aktuellen
   * Datum. Falls kein Geburtsdatum angeben ist, wird eine NullPointerException
   * geworfen.
   *
   * @return Alter in Jahren
   */
  public double getAlter();
  
  /**
   * Berechnet das Alter des Benutzers zum übergebenen Zeitpunkt. Falls kein
    * Zeitpunkt übergeben wird, wird eine NullPointerException geworfen.
   *
   * @param zeitpunkt der Zeitpunkt, zu dem das Alter berechnet werden soll
   * @return Alter in Jahren
   */
  public double getAlter(Date zeitpunkt);

  /**
   * Berechnet die Dauer, seit der der Benutzers angemeldet ist, aus dem 
   * Anmeldedatum und dem aktuellen
   * Datum. Falls kein Anmeldedatum angeben ist, wird eine NullPointerException
   * geworfen.
   *
   * @return Anmeldedauer in Tagen
   */
  public int getAnmeldeDauer();
  
  /**
   * Weist dem Benutzer einen neuen, eindeutigen Benutzernamen zu. Dieser
   * Benutzername setzt sich aus den ersten beiden Buchstaben des Vornamens,
   * sowie den ersten 6 (soweit vorhanden) Buchstaben des Nachnamens zusammen.
   * Dabei werden nur Kleinbuchstaben verwendet und Umlaute durch die
   * jeweiligen Umschreibungen ersetzt. <br>
   * Ist dieser Standardname schon vergeben, wird noch eine eindeutige Nummer
   * angehängt. Der Benutzer "Thomas Türk" erhielte beispielsweise den
   * Benutzernamen "thtuerk".
   */
  public void setStandardBenutzername();

  /**
   * Liefert die eMail-Adresse des Benutzes mit seinem Namen zusammen in der 
   * Form Vorname Nachname <eMail> falls die eMail-Addresse gesetzt ist. Ansonsten
   * wird null zurückgeliefert. 
   * @return die formatierte eMail-Adresse
   */
  public String getEMailMitName();
  
  /**
   * Setzt, ob der Benutzer noch aktiv ist, d.h. noch zu den
   * regelmäßigen Nutzern der Bücherei gehört. 
   */
  public void setAktiv(boolean istAktiv);  
  
  /**
   * Liefert, ob der Benutzer zur Zeit aktiv ist, d.h. zu den
   * regelmäßigen Nutzern der Bücherei gehört.
   * @return <code>true</true> gwd. der Benutzer aktiv ist 
   */
  public boolean istAktiv();    
}