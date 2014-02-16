package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Medium;


/**
 * Dummer-Datencontainer.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
class AuswahlObject{
    
  Medium medium;
  Ausleihzeitraum zeitraum;
  Benutzer benutzer;

  public void setDaten(Ausleihzeitraum zeitraum) {
    this.zeitraum = zeitraum;
    this.medium = zeitraum.getAusleihe().getMedium();
    this.benutzer = zeitraum.getAusleihe().getBenutzer();
  }
  
  public Benutzer getBenutzer() {
    return benutzer;
  }

  public Medium getMedium() {
    return medium;
  }

  public Ausleihzeitraum getZeitraum() {
    return zeitraum;
  }

  public void setDaten(Medium medium) {
    this.zeitraum = null;
    this.medium = medium;
    this.benutzer = null;
  }
  
  public void setDaten(Benutzer benutzer) {
    this.zeitraum = null;
    this.medium = null;
    this.benutzer = benutzer;
  }
}