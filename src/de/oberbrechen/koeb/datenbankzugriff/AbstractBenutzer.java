package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.framework.ErrorHandler;

import java.util.*;
import java.security.*;


/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Benutzer.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractBenutzer extends AbstractDatenbankzugriff 
  implements Benutzer {

  protected static final long milliSekundenInTag = 864l * 100000l;
  protected static final long milliSekundenInJahr = 31536l * 1000000l;

  // Die Attribute der Benutzer
  protected String vorname, nachname, adresse, ort, klasse;
  protected String tel, fax, eMail, benutzername, passwort, bemerkungen;
  protected Date geburtsdatum, anmeldedatum;
  protected boolean vip, aktiv;
  
  /**
   * Berechnet zum übergebenen Passwort die Prüfziffer nach dem MD5
   * Algorithmus
   *
   * @param passwort das Passwort, dessen Prüfziffer berechnet werden soll
   * @return die Prüfziffer
   */
  protected static String berechnePruefziffer(String passwort) {
    if (passwort == null) return null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] passwortMD5 = md.digest(passwort.getBytes());
      StringBuffer buffer = new StringBuffer();
      for (int i=0; i < passwortMD5.length; i++) {
        int value = (passwortMD5[i] & 0x7F) + (passwortMD5[i] < 0 ? 128 : 0);
        String codeValue = Integer.toHexString(value);
        if (codeValue.length() == 1) buffer.append("0");
        buffer.append(codeValue);
      }
      return buffer.toString();
    } catch (NoSuchAlgorithmException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Berechnen der Prüfziffer für das Passwort!", true); 
    }
    return null;
  }

  public AbstractBenutzer() {
    vorname = null;
    nachname = null;
    adresse = null;
    ort = Datenbank.getInstance().getBenutzerFactory().
      getStandardBenutzerwohnort();
    klasse = null;
    tel = null;
    fax = null;
    eMail = null;
    benutzername = null;
    passwort = null;
    bemerkungen = null;
    geburtsdatum = null;
    anmeldedatum = new Date(System.currentTimeMillis());
    vip = false;
    aktiv = true;
  }

  public boolean istBenutzernameGesetzt() {
    return (this.getBenutzername() != null);
  }

  public boolean istBenutzernameSchonVergeben() {
    return istBenutzernameSchonVergeben(this.getBenutzername());
  }
  
  public boolean istBenutzernameSchonVergeben(String benutzername) {
    if (benutzername == null) return false;

    try {
      int zugehoerigeBenutzernr =
        Datenbank.getInstance().getBenutzerFactory().
          sucheBenutzername(benutzername);
      return (zugehoerigeBenutzernr != this.getId());
    } catch (DatenNichtGefundenException e) {
      return false;
    }
  }  

  public String getName() {
    return vorname+" "+nachname;
  }

  public String getNameFormal() {
    return nachname+", "+vorname;
  }

  public String getVorname() {
    return vorname;
  }

  public String getNachname() {
    return nachname;
  }

  public String getAdresse() {
    return adresse;
  }

  public String getOrt() {
    return ort;
  }

  public String getKlasse() {
    return klasse;
  }

  public String getTel() {
    return tel;
  }

  public String getFax() {
    return fax;
  }

  public String getEMail() {
    return eMail;
  }
  
  public String getEMailMitName() {
    if (getEMail() == null) return null;
    
    return getName()+" <"+getEMail()+">";
  }

  public String getBenutzername() {
    return benutzername;
  }

  public String getBemerkungen() {
    return bemerkungen;
  }

  public Date getAnmeldedatum() {
    return anmeldedatum;
  }

  public Date getGeburtsdatum() {
    return geburtsdatum;
  }

  public void setVorname(String vorname) {
    setIstNichtGespeichert();
    this.vorname = DatenmanipulationsFunktionen.formatString(vorname);
  }

  public void setNachname(String nachname) {
    setIstNichtGespeichert();
    this.nachname = DatenmanipulationsFunktionen.formatString(nachname);
  }

  public void setAdresse(String adresse) {
    setIstNichtGespeichert();
    this.adresse = DatenmanipulationsFunktionen.formatString(adresse);
  }

  public void setOrt(String ort) {
    setIstNichtGespeichert();
    this.ort = DatenmanipulationsFunktionen.formatString(ort);
  }

  public void setKlasse(String klasse) {
    setIstNichtGespeichert();
    this.klasse = DatenmanipulationsFunktionen.formatString(klasse);
  }

  public void setTel(String tel) {
    setIstNichtGespeichert();
    this.tel = DatenmanipulationsFunktionen.formatString(tel);
  }

  public void setFax(String fax) {
    setIstNichtGespeichert();
    this.fax = DatenmanipulationsFunktionen.formatString(fax);
  }

  public void setEMail(String eMail) {
    setIstNichtGespeichert();
    this.eMail = DatenmanipulationsFunktionen.formatString(eMail);
  }

  public void setBenutzername(String benutzername) {
    setIstNichtGespeichert();
    this.benutzername = DatenmanipulationsFunktionen.formatString(benutzername);
  }

  public void setPasswort(String passwort) {
    setIstNichtGespeichert();
    this.passwort = berechnePruefziffer(passwort);
  }

  public boolean checkPasswort(String passwort) {
    if (this.passwort == null)
      return (DatenmanipulationsFunktionen.formatString(passwort) == null);
    return this.passwort.equals(berechnePruefziffer(passwort));
  }

  public boolean istPasswortGesetzt () {
    return (passwort != null);
  }

  public void setBemerkungen(String bemerkungen) {
    setIstNichtGespeichert();
    this.bemerkungen = DatenmanipulationsFunktionen.formatString(bemerkungen);
  }

  public void setAnmeldedatum(Date anmeldedatum) {
    setIstNichtGespeichert();
    this.anmeldedatum = anmeldedatum;
  }

  public void setGeburtsdatum(Date geburtsdatum) {
    setIstNichtGespeichert();
    this.geburtsdatum = geburtsdatum;
  }

  public boolean istVIP() {
    return vip;
  }

  public void setVIP(boolean vip) {
    this.vip = vip;
    setIstNichtGespeichert();
  }
  
  public boolean istAktiv() {
    return aktiv;
  }

  public void setAktiv(boolean aktiv) {
    this.aktiv = aktiv;
    setIstNichtGespeichert();
  }
  
  public double getAlter() {
    return getAlter(new Date());
  }

  public double getAlter(Date zeitpunkt) {
    if (geburtsdatum == null || zeitpunkt == null) 
      throw new NullPointerException();
        
    long alterInMillisekunden = zeitpunkt.getTime() -
      geburtsdatum.getTime();
        
    return (((double) alterInMillisekunden) / milliSekundenInJahr);
  }
  
  public int getAnmeldeDauer() {
    if (anmeldedatum == null) 
      throw new NullPointerException();
    
    long anmeldeDatumInMillisekunden = System.currentTimeMillis()
      - anmeldedatum.getTime();
    return (int) (anmeldeDatumInMillisekunden / milliSekundenInTag);    
  }
  

  public String toDebugString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Benutzer ").append(getId()).append("\n");
    ausgabe.append("-------------------------------\n");
    ausgabe.append(this.getName()).append("\n");
    if (adresse != null)     ausgabe.append(adresse).append("\n");
    if (ort != null)          ausgabe.append(ort).append("\n");
    if (tel != null)          ausgabe.append("Tel.  : ").append(tel).append("\n");
    if (fax != null)          ausgabe.append("Fax   : ").append(fax).append("\n");
    if (eMail != null)        ausgabe.append("eMail : ").append(eMail).append("\n");
    if (klasse != null)       ausgabe.append("Klasse: ").append(klasse).append("\n");
    if (geburtsdatum != null) ausgabe.append("Geburtsdatum: ").append(DatenmanipulationsFunktionen.formatDate(geburtsdatum)).append("\n");
    if (anmeldedatum != null) ausgabe.append("Anmeldedatum: ").append(DatenmanipulationsFunktionen.formatDate(anmeldedatum)).append("\n");
    ausgabe.append("VIP:          ").append(istVIP()).append("\n");
    ausgabe.append("aktiv:          ").append(istAktiv()).append("\n");
    
    if (benutzername != null || passwort != null) {
      ausgabe.append("\nBenutzername: ");
      if (benutzername != null) ausgabe.append(benutzername);
      ausgabe.append("\nPasswort    : ");
      if (passwort != null) ausgabe.append("********\n");
    }

    return ausgabe.toString();
  }

  public void setStandardBenutzername() {
    if (vorname == null || nachname == null) return;

    String neuerBenutzername =
        vorname.substring(0, Math.min(2, vorname.length())) +
        nachname.substring(0, Math.min(6, nachname.length()));
    neuerBenutzername = neuerBenutzername.toLowerCase();

    // Umlaute entfernen
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < neuerBenutzername.length(); i++) {
      char currentChar = neuerBenutzername.charAt(i);
      switch (currentChar) {
        case 'ä':
          buffer.append("ae"); break;
        case 'ö':
          buffer.append("oe"); break;
        case 'ü':
          buffer.append("ue"); break;
        case 'ß':
          buffer.append("ss"); break;
        default:
          buffer.append(currentChar);
      }
    }
    neuerBenutzername = buffer.toString();

    //Eindeutigkeit sicherstellen
    int Nr = 1;
    String neuerBenutzernameNr = neuerBenutzername;
    while (istBenutzernameSchonVergeben(neuerBenutzernameNr)) {        
      Nr++;
      neuerBenutzernameNr = neuerBenutzername+Nr;
    }

    this.setBenutzername(neuerBenutzernameNr);
  }

  public String toString() {
    return this.getName();
  }  
}