package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse repräsentiert die Teilnahme eines Benutzers an einer
 * Veranstaltung der Bücherei.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractVeranstaltungsteilnahme 
  extends AbstractDatenbankzugriff implements Veranstaltungsteilnahme {

  protected Benutzer benutzer;
  protected Veranstaltung veranstaltung;
  protected String bemerkungen;

  protected int anmeldenr;
  protected Date anmeldeDatum;
  protected int status;
  
  public AbstractVeranstaltungsteilnahme(Benutzer benutzer,
    Veranstaltung veranstaltung) {    
    if (benutzer == null || veranstaltung == null)
      throw new IllegalArgumentException();
    
    this.benutzer = benutzer;
    this.veranstaltung = veranstaltung;
    this.bemerkungen = null;
    this.anmeldenr = 0;
    this.status = ANMELDESTATUS_ANGEMELDET;
    this.anmeldeDatum = null;
  }

  public AbstractVeranstaltungsteilnahme() {
    this.benutzer = null;
    this.veranstaltung = null;
    this.bemerkungen = null;
    this.anmeldenr = 0;
    this.status = ANMELDESTATUS_ANGEMELDET;
    this.anmeldeDatum = null;
  }

  public Benutzer getBenutzer() {
    return benutzer;
  }

  public Veranstaltung getVeranstaltung() {
    return veranstaltung;
  }

  public int getAnmeldeNr() {
    if (this.istNeu()) return 0;
    
    if (anmeldenr > 0) return anmeldenr;
    
    anmeldenr = bestimmeAnmeldeNr();
    return anmeldenr;
  }
  
  /**
   * Bestimmt die AnmeldeNr der Teilname aus dem Anmeldedatum 
   */  
  protected abstract int bestimmeAnmeldeNr();

  public String getBemerkungen() {
    return bemerkungen;
  }

  public void setBemerkungen(String bemerkungen) {
    setIstNichtGespeichert();
    this.bemerkungen = DatenmanipulationsFunktionen.formatString(bemerkungen);
  }

  public String toString() {
    return benutzer.getName()+" nimmt an der Veranstaltung "+
      veranstaltung.getTitel()+" teil!";
  }

  public String toDebugString() {
    return this.toString();
  }
  
  public Date getAnmeldeDatum() {
    return anmeldeDatum;
  }

  public void setAnmeldeDatum(Date datum) {
    setIstNichtGespeichert();
    anmeldeDatum = datum;
  }
  
  public boolean istAufWarteListe() {
    return (status == ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND) ||
    (status == ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND);
  }

  public int getStatus() {
    return status;
  }

  public boolean istAufBlockierenderWarteListe() {
    return status == ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND;
  }

  public void setStatus(int status) {
    setIstNichtGespeichert();
    if ((status != ANMELDESTATUS_ANGEMELDET) &&
        (status != ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND) &&
        (status != ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND)) {
      ErrorHandler.getInstance().handleException(new IllegalArgumentException(), false);
    } else {
      this.status = status;
    }
  }

}