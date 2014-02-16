package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.einstellungen.*;
import de.oberbrechen.koeb.einstellungen.Ausleihordnung;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.einstellungen.WidersprichtAusleihordnungException;

/**
 * Diese Klasse stellt die grundlegenden Methoden
 * einer Ausleihe bereit. Der datenbankspezifischen 
 * Code muss jedoch noch ergänzt werden.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractAusleihe extends AbstractDatenbankzugriff
  implements Ausleihe {

  protected Date rueckgabedatum;
  protected String bemerkungen;
  protected Benutzer benutzer;
  protected Medium medium;
  protected Mitarbeiter mitarbeiterRueckgabe;
  
  protected AusleihzeitraumListe ausleihzeitraumListe;

  /**
   * Erstellt eine neue, bisher noch nicht in der Datenbank vorhandenen
   * Ausleihe. Alle Attribute der Ausleihe außer dem Anmeldedatum werden mit
   * null initialisiert, das Anmeldedatum mit dem aktuellen Systemdatum.
   * Beim ersten Speichern wird automatisch eine passende ID
   * zugewiesen.
   */
  public AbstractAusleihe() {
    bemerkungen = null;
    benutzer = null;
    medium = null;
    rueckgabedatum = null;
    mitarbeiterRueckgabe = null;
    ausleihzeitraumListe = new AusleihzeitraumListe();
    ausleihzeitraumListe.setSortierung(
        AusleihzeitraumListe.taetigungsdatumSortierung);
    ausleihzeitraumListe.add(new Ausleihzeitraum(null, this, 
            new Date(), null));
  }

  public String toDebugString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Ausleihe ").append(id);
    ausgabe.append("\n-------------------------------");
    ausgabe.append("\nBenutzer            : ").append(getBenutzer());
    ausgabe.append("\nMedium              : ").append(getMedium());
    ausgabe.append("\nRückgabedatum       : ").append(getRueckgabedatum());
    ausgabe.append("\nMitarbeiter Rückgabe: ").append(mitarbeiterRueckgabe);
    ausgabe.append("\nBemerkungen         : ").append(bemerkungen);
    ausgabe.append("\n");
    ausgabe.append("Ausleihzeiträume:\n");
    Iterator<Ausleihzeitraum> it = getAusleihzeitraeume().iterator();
    while (it.hasNext()) {
      ausgabe.append(" - ");
      ausgabe.append(it.next().
          getZeitraumFormat(Zeitraum.ZEITRAUMFORMAT_LANG_MIT_UHRZEIT));
      ausgabe.append("\n");
    }
    return ausgabe.toString();
  }
  
  public String toString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Ausleihe (").append(id).append("): ");
    ausgabe.append(getBenutzer().getName()).append(", ");
    ausgabe.append(getMedium());
    return ausgabe.toString();
  }

  
  public int getAnzahlAusleihzeitraeume() {
    return ausleihzeitraumListe.size();
  }

  public int getAnzahlVerlaengerungen() {
    return getAnzahlAusleihzeitraeume()-1;
  }

  public Date getAusleihdatum() {
    return getErstenAusleihzeitraum().getBeginn();
  }

  public AusleihzeitraumListe getAusleihzeitraeume() {
    AusleihzeitraumListe result = new AusleihzeitraumListe();
    result.addAll(ausleihzeitraumListe);
    return result;
  }

  public String getBemerkungen() {
    return bemerkungen;
  }

  public Benutzer getBenutzer() {
    return benutzer;
  }

  public Ausleihzeitraum getErstenAusleihzeitraum() {
    return (Ausleihzeitraum) ausleihzeitraumListe.get(0);
  }

  public Ausleihzeitraum getLetztenAusleihzeitraum() {
    return (Ausleihzeitraum) ausleihzeitraumListe.get(
        ausleihzeitraumListe.size()-1);
  }

  public Medium getMedium() {
    return medium;
  }

  public Mitarbeiter getMitarbeiterAusleihe() {
    return getErstenAusleihzeitraum().getMitarbeiter();
  }

  public Mitarbeiter getMitarbeiterRueckgabe() {
    return mitarbeiterRueckgabe;
  }

  public Date getRueckgabedatum() {
    return rueckgabedatum;
  }

  public Date getSollRueckgabedatum() {
    return getLetztenAusleihzeitraum().getEnde();
  }

  public int getUeberzogeneTage() {
    Date referenz;
    Date rueckgabedatum = getRueckgabedatum();
    Date now = new Date();
    referenz = (rueckgabedatum != null &&
                rueckgabedatum.before(now))?rueckgabedatum:now;
    
    int erg = DatenmanipulationsFunktionen.getDifferenzTage(
        getSollRueckgabedatum(), referenz);
    if (erg < 0) erg = 0;

    return erg;
  }

  public boolean heuteGetaetigt() {
    return DatenmanipulationsFunktionen.entsprichtHeutigemDatum(
        getErstenAusleihzeitraum().getTaetigungsdatum());
  }

  public boolean heuteZurueckgegeben() {
    return DatenmanipulationsFunktionen.entsprichtHeutigemDatum(
        getRueckgabedatum());
  }

  public boolean istAktuell() {
    return (!istZurueckgegeben() || heuteZurueckgegeben());
  }

  public boolean istAktuellVerlaengert() {
    return getAnzahlVerlaengerungen() > 0 && 
      DatenmanipulationsFunktionen.entsprichtHeutigemDatum(
        getLetztenAusleihzeitraum().getTaetigungsdatum());
  }

  public boolean istUeberzogen() {
    return getUeberzogeneTage() > 0;
  }

  public boolean istZurueckgegeben() {
    return getRueckgabedatum() != null;
  }

  public void setAusleihdatum(Date ausleihdatum) {
    setIstNichtGespeichert();
    getErstenAusleihzeitraum().setBeginn(ausleihdatum);
  }

  public void setMitarbeiterAusleihe(Mitarbeiter mitarbeiterAusleihe) {
    setIstNichtGespeichert();
    getErstenAusleihzeitraum().setMitarbeiter(mitarbeiterAusleihe);
  }

  public void setBemerkungen(String bemerkungen) {
    setIstNichtGespeichert();
    this.bemerkungen = DatenmanipulationsFunktionen.formatString(bemerkungen);
  }

  public void setBenutzer(Benutzer benutzer) {
    setIstNichtGespeichert();
    this.benutzer = benutzer;
  }

  public void setMedium(Medium medium) {
    setIstNichtGespeichert();
    this.medium = medium;
  }

  public void setMitarbeiterRueckgabe(Mitarbeiter mitarbeiterRueckgabe) {
    setIstNichtGespeichert();
    this.mitarbeiterRueckgabe = mitarbeiterRueckgabe;
  }

  public void setRueckgabedatum(Date rueckgabedatum) {
    setIstNichtGespeichert();
    this.rueckgabedatum = rueckgabedatum;
  }

  public void setSollRueckgabedatum(Date sollrueckgabedatum) {
    setIstNichtGespeichert();
    getLetztenAusleihzeitraum().setEnde(sollrueckgabedatum);
  }

  public void verlaengere(Mitarbeiter mitarbeiter) throws DatenbankInkonsistenzException, WidersprichtAusleihordnungException {
    if (this.istZurueckgegeben())
      throw new DatenbankInkonsistenzException("Die Ausleihe "+
      this.getId() +" konnte nicht verlängert werden, da das "+
      "zugehörige Medium bereits zurückgegeben wurde.");

    Ausleihordnung ausleihordnung = Buecherei.getInstance().getAusleihordnung();
    VerlaengerungsInformationen infos = 
      ausleihordnung.getVerlaengerungsInformationen(this, new Date());
    
    //teste, ob Verlängern erlaubt ist
    if (!infos.istErlaubt()) {
      String fehlerMeldung = "Die Ausleihe "+this.getId()+" konnte "+
        "nicht verlängert werden, da die Ausleihordnung dies nicht zuließ.";

      if (infos.getBemerkungen() != null) fehlerMeldung += " Die Begründung "+
        "der Ausleihordnung lautet: "+infos.getBemerkungen();
      throw new WidersprichtAusleihordnungException(fehlerMeldung);
    }
    
    //eigentliche Verlängerung
    Ausleihzeitraum neuerZeitraum = 
      new Ausleihzeitraum(mitarbeiter, this, infos.getVon(), infos.getBis());
    ausleihzeitraumListe.add(neuerZeitraum);
    setIstNichtGespeichert();
  }

  public void verlaengereBisMindestens(Date datum, Mitarbeiter mitarbeiter) throws DatenbankInkonsistenzException, WidersprichtAusleihordnungException {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(datum);
    calendar.add(Calendar.DATE, -1);
    
    java.util.Date endDatum = calendar.getTime();
    
    while (endDatum.after(this.getSollRueckgabedatum())) {
      this.verlaengere(mitarbeiter);
    }
  }
  
  public void verlaengernRueckgaengig() throws DatenbankInkonsistenzException {
    if (ausleihzeitraumListe.size() < 2) throw
      new DatenbankInkonsistenzException("Das Verlängern kann bei Ausleihe "+
      this.getId()+" nicht rückgängig gemacht werden, da diese "+
      "Ausleihe nicht verlängert wurde.");

    ausleihzeitraumListe.removeLast();
    setIstNichtGespeichert();
  }

  public void zurueckgeben(Mitarbeiter mitarbeiter) {      
    this.setRueckgabedatum(new java.util.Date());
    this.setMitarbeiterRueckgabe(mitarbeiter);
  }

  public Date getTaetigungsdatum() {
    return getErstenAusleihzeitraum().getTaetigungsdatum();
  }

  public void setTaetigungsdatum(Date datum) {
    getErstenAusleihzeitraum().setTaetigungsdatum(datum);
    setIstNichtGespeichert();
  }

  public Ausleihzeitraum getNaechstenAusleihzeitraum(Ausleihzeitraum ausleihzeitraum) {    
    for (int i = 0; i < ausleihzeitraumListe.size()-2; i++) {
      if (ausleihzeitraumListe.get(i).equals(ausleihzeitraum))
        return (Ausleihzeitraum) ausleihzeitraumListe.get(i+1);
    }
    return null;
  }
}