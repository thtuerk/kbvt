package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MitarbeiterListe;
import de.oberbrechen.koeb.framework.ErrorHandler;


/**
 * Dieses Klasse stellt einige grundlegende
 * Funktionen einer MitarbeiterFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public abstract class AbstractMitarbeiterFactory extends 
  AbstractDatenbankzugriffFactory<Mitarbeiter> implements MitarbeiterFactory {
  
  Mitarbeiter aktuellerMitarbeiter = null;

  public MitarbeiterListe getAlleMitarbeiterMitBerechtigung(int berechtigung) {
    MitarbeiterListe alleMitarbeiter = getAlleMitarbeiter();
    MitarbeiterListe ausgewaehlteMitarbeiter = new MitarbeiterListe();

    for (Mitarbeiter currentMitarbeiter : alleMitarbeiter) {
      if (currentMitarbeiter.besitztBerechtigung(berechtigung)) {
        ausgewaehlteMitarbeiter.add(currentMitarbeiter);
      }
    }
    return ausgewaehlteMitarbeiter;
  }

  public Mitarbeiter getAktuellenMitarbeiter() {
    return aktuellerMitarbeiter;
  }

  public void setAktuellenMitarbeiter(Mitarbeiter mitarbeiter) {
    aktuellerMitarbeiter = mitarbeiter;
  }

  public MitarbeiterListe getAktiveMitarbeiterMitBerechtigung(int berechtigung) {
    MitarbeiterListe alleMitarbeiter = getAlleMitarbeiterMitBerechtigung(berechtigung);
    MitarbeiterListe ausgewaehlteMitarbeiter = new MitarbeiterListe();

    for (Mitarbeiter currentMitarbeiter : alleMitarbeiter) {
      if (currentMitarbeiter.istAktiv()) {
        ausgewaehlteMitarbeiter.add(currentMitarbeiter);
      }
    }
    return ausgewaehlteMitarbeiter;
  }
  
  public Mitarbeiter erstelleStandardMitarbeiter() {
    try {
      BenutzerFactory benutzerFactory =
          Datenbank.getInstance().getBenutzerFactory();
      Benutzer neuerBenutzer = benutzerFactory.erstelleNeu();
      neuerBenutzer.setVorname("Neuer");
      neuerBenutzer.setNachname("Mitarbeiter");
      neuerBenutzer.save();
    
      //angelegten Benutzer zum Mitarbeiter machen
      Mitarbeiter neuerMitarbeiter = erstelleNeu(neuerBenutzer);
      neuerMitarbeiter.setBerechtigung(Mitarbeiter.BERECHTIGUNG_BESTAND_EINGABE, true);
      neuerMitarbeiter.setBerechtigung(Mitarbeiter.BERECHTIGUNG_STANDARD, true);
      neuerMitarbeiter.setBerechtigung(Mitarbeiter.BERECHTIGUNG_VERANSTALTUNGSTEILNAHME_EINGABE, true);
      neuerMitarbeiter.setBerechtigung(Mitarbeiter.BERECHTIGUNG_ADMIN, true);
      neuerMitarbeiter.save();
      return neuerMitarbeiter;
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Standard-Mitarbeiter " +
          "konnte nicht erstellt werden!", true);
    }
    
    //Wird nie erreicht!
    return null;
  }    
  
  
  public Mitarbeiter getMitarbeiterZuBenutzer(int benutzerId)
    throws DatenNichtGefundenException, DatenbankInkonsistenzException {

    int mitarbeiterId = sucheBenutzer(benutzerId);
    return (Mitarbeiter) get(mitarbeiterId);
  }
}