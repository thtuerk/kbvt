package de.oberbrechen.koeb.datenbankzugriff;

import java.text.DecimalFormat;

import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Veranstaltung.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractVeranstaltung extends AbstractDatenbankzugriff
  implements Veranstaltung {

  private static DecimalFormat waehrungsFormat = new DecimalFormat("0.00");

  protected String titel, kurzTitel, ansprechpartner, beschreibung,  bezugsgruppe;
  protected Veranstaltungsgruppe veranstaltungsgruppe;
  protected int maxTeilnehmerAnzahl;
  protected String waehrung;
  protected TerminListe termine;
  protected boolean anmeldungErforderlich;
  protected double kosten;

  public AbstractVeranstaltung() {
    titel = null;
    kurzTitel = null;
    ansprechpartner = null;
    beschreibung = null;
    bezugsgruppe = null;
    veranstaltungsgruppe = null;
    maxTeilnehmerAnzahl = 0;
    termine = new TerminListe();
    anmeldungErforderlich = true;
    kosten = 0;
    waehrung = "EUR";
  }


  public String getAnsprechpartner() {
    return ansprechpartner;
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public String getBezugsgruppe() {
    return bezugsgruppe;
  }

  public double getKosten() {
    return kosten;
  }

  public String getKostenFormatiert() {
    if (getKosten() == 0) {
      return "-";
    } else {
      return waehrungsFormat.format(getKosten())+" "+getWaehrung();
    }
  }

  public String getKurzTitel() {
    return kurzTitel;
  }

  public int getMaximaleTeilnehmerAnzahl() {
    return maxTeilnehmerAnzahl;
  }

  public TerminListe getTermine() {
    return termine;
  }

  public String getTitel() {
    return titel;
  }

  public Veranstaltungsgruppe getVeranstaltungsgruppe() {
    return veranstaltungsgruppe;
  }

  public String getWaehrung() {
    return waehrung;
  }

  public boolean istAnmeldungErforderlich() {
    return anmeldungErforderlich;
  }

  public void setAnmeldungErforderlich(boolean anmeldungErforderlich) {
    setIstNichtGespeichert();
    this.anmeldungErforderlich = anmeldungErforderlich;
  }

  public void setAnsprechpartner(String ansprechpartner) {
    setIstNichtGespeichert();
    this.ansprechpartner = 
      DatenmanipulationsFunktionen.formatString(ansprechpartner);
  }

  public void setBeschreibung(String beschreibung) {
    setIstNichtGespeichert();
    this.beschreibung = 
      DatenmanipulationsFunktionen.formatString(beschreibung);
  }

  public void setBezugsgruppe(String bezugsgruppe) {
    setIstNichtGespeichert();
    this.bezugsgruppe = 
      DatenmanipulationsFunktionen.formatString(bezugsgruppe);
  }

  public void setKosten(double kosten) {
    setIstNichtGespeichert();
    this.kosten = kosten;
  }

  public void setKurzTitel(String kurzTitel) {
    setIstNichtGespeichert();
    this.kurzTitel = 
      DatenmanipulationsFunktionen.formatString(kurzTitel);
  }

  public void setMaximaleTeilnehmerAnzahl(int maxTeilnehmerAnzahl) {
    setIstNichtGespeichert();
    this.maxTeilnehmerAnzahl = maxTeilnehmerAnzahl;
  }

  public void setTermine(TerminListe termine) {
    if (termine == null) throw new NullPointerException();
    setIstNichtGespeichert();

    this.termine.clear();
    this.termine.addAll(termine);
  }

  public void setTitel(String titel) {
    setIstNichtGespeichert();
    this.titel = DatenmanipulationsFunktionen.formatString(titel);
  }

  public void setVeranstaltungsgruppe(Veranstaltungsgruppe veranstaltungsgruppe) {
    setIstNichtGespeichert();
    this.veranstaltungsgruppe = veranstaltungsgruppe;
  }

  public void setWaehrung(String waehrung) {
    setIstNichtGespeichert();
    this.waehrung = DatenmanipulationsFunktionen.formatString(waehrung);
  }

  public String toDebugString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Veranstaltung ").append(getId()).append("\n");
    ausgabe.append("-------------------------------\n");
    ausgabe.append("Titel                 : ").append(titel).append("\n");
    ausgabe.append("Kurz-Titel            : ").append(kurzTitel).append("\n");
    ausgabe.append("Ansprechpartner       : ").append(ansprechpartner).append("\n");
    ausgabe.append("Bezugsgruppe          : ").append(bezugsgruppe).append("\n");
    ausgabe.append("Veranstaltungsgruppe  : ").append(veranstaltungsgruppe.getName()).append("\n");
    ausgabe.append("Kosten                : ").append(getKostenFormatiert()).append("\n");
    ausgabe.append("Anmeldung erforderlich: ").append(istAnmeldungErforderlich()).append("\n");
    ausgabe.append("Beschreibung          : ").append(getBeschreibung()).append("\n");
    ausgabe.append("\nTermine:\n");
    for (Termin termin : termine) {
      ausgabe.append("   - "+termin.getZeitraumFormat(
          Zeitraum.ZEITRAUMFORMAT_KURZ_MIT_UHRZEIT)+"\n");    }

    return ausgabe.toString();
  }

  public String toString() {
    return titel+" ("+getId()+")";
  }
}