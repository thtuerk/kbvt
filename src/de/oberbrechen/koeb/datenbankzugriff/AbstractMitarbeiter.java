package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Benutzer.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public abstract class AbstractMitarbeiter extends AbstractDatenbankzugriff
  implements Mitarbeiter {

  protected String mitarbeiterBenutzername, mitarbeiterPasswortPruefziffer;
  protected int berechtigungen;
  protected Benutzer benutzer;

  public AbstractMitarbeiter() {
    benutzer = null;
    mitarbeiterBenutzername = null;
    mitarbeiterPasswortPruefziffer = null;
    berechtigungen = 0;  
  }
  
  public void setMitarbeiterBenutzername(String benutzername) {
    setIstNichtGespeichert();
    this.mitarbeiterBenutzername = 
      DatenmanipulationsFunktionen.formatString(benutzername);
  }

  public void setMitarbeiterPasswort(String passwort) {
    setIstNichtGespeichert();
    this.mitarbeiterPasswortPruefziffer = AbstractBenutzer.berechnePruefziffer(passwort);
  }

  public String getMitarbeiterBenutzername() {
    return mitarbeiterBenutzername;
  }

  public boolean checkMitarbeiterPasswort(String passwort) {
    if (mitarbeiterPasswortPruefziffer == null)
      return (DatenmanipulationsFunktionen.formatString(passwort) == null);
    return mitarbeiterPasswortPruefziffer.equals(
      AbstractBenutzer.berechnePruefziffer(passwort));
  }

  public boolean istMitarbeiterPasswortGesetzt () {
    return (mitarbeiterPasswortPruefziffer != null);
  }

  public String toDebugString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Mitarbeiter ").append(this.getId()).append("\n");
    ausgabe.append(getBenutzer().toDebugString());

    if (this.getMitarbeiterBenutzername() != null ||
      this.istMitarbeiterPasswortGesetzt()) {

      ausgabe.append("\nMitarbeiter-Benutzername: ");
      if (this.getMitarbeiterBenutzername() != null)
        ausgabe.append(getMitarbeiterBenutzername());

      ausgabe.append("\nMitarbeiter-Passwort    : ");
      if (this.istMitarbeiterPasswortGesetzt()) ausgabe.append("********\n");

    }
    return ausgabe.toString();
  }

  public boolean besitztBerechtigung(int berechtigung) {
    if (berechtigung == BERECHTIGUNG_STANDARD) return true;
    return (this.berechtigungen & berechtigung) != 0;
  }

  public void setBerechtigung(int berechtigung, boolean wert) {
    setIstNichtGespeichert();
    if (berechtigung == BERECHTIGUNG_STANDARD) return;
    if (wert) {
      berechtigungen = berechtigung | berechtigungen;
    } else {
      berechtigungen = berechtigungen & (~berechtigung);
    }
  }
  
  public Benutzer getBenutzer() {
    return benutzer;
  }  
  
  public String toString() {
    return this.getBenutzer().getName()+" ("+getId()+")";
  }
  
  public void setAktiv(boolean aktiv) {
    setBerechtigung(Mitarbeiter.EHEMALIGER_MITARBEITER, !aktiv);
  }

  public boolean istAktiv() {
    return !besitztBerechtigung(Mitarbeiter.EHEMALIGER_MITARBEITER);
  }    
}