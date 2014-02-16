package de.oberbrechen.koeb.pdf.pdfInternetStatistik;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Internetfreigabe;
import de.oberbrechen.koeb.datenstrukturen.InternetfreigabenListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell für die Internetstatistiken eines Monats
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class InternetStatistikMonatTabellenModell extends TabellenModell {
  
  private SimpleDateFormat dateFormat = new SimpleDateFormat("EE, d. MMM. yyyy HH:mm:ss");
  private DecimalFormat zahlenFormat = new DecimalFormat("00");
  private DecimalFormat waehrungsFormat = new DecimalFormat("0.00 EUR");

  InternetfreigabenListe daten;
  double kostenSumme;
  int dauerSumme;

  public InternetStatistikMonatTabellenModell(int monat, int jahr,
      boolean versteckeKostenloseFreigaben) {
    InternetfreigabeFactory internetfreigabeFactory =
      Datenbank.getInstance().getInternetfreigabeFactory();
    daten = internetfreigabeFactory.getAlleInternetFreigabenInMonat(monat, jahr);
    kostenSumme = 0;
    dauerSumme = 0;
    Iterator<Internetfreigabe> it = daten.iterator();
    while (it.hasNext()) {
      Internetfreigabe aktuelleFreigabe = it.next();
      double kosten = Buecherei.getInstance().berechneInternetzugangsKosten(
        aktuelleFreigabe.getDauer());
      if (!versteckeKostenloseFreigaben || kosten > 0) {
        kostenSumme += kosten;
        dauerSumme += aktuelleFreigabe.getDauer();
      } else {
        it.remove();
      }
    }
    
    setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(5, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setBreiteProzent(1, 25);    
    setBreiteProzent(2, 25);    
    setBreiteProzent(3, 25);    
    setBreiteProzent(4, 12.5f);    
    setBreiteProzent(5, 12.5f);    
  }

  public int getSpaltenAnzahl() {
    return 5;
  }

  public int getZeilenAnzahl() {
    return daten.size()+1;
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "";
    if (spaltenNr == 2) return "Client";
    if (spaltenNr == 3) return "Benutzer";
    if (spaltenNr == 4) return "Dauer";
    if (spaltenNr == 5) return "Kosten";
    return "unbekannte Spalte";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (zeilenNr <= daten.size()) {
      Internetfreigabe aktuelleFreigabe = (Internetfreigabe) daten.get(zeilenNr-1);
      
      if (spaltenNr == 1) return dateFormat.format(aktuelleFreigabe.getStartZeitpunkt());
      if (spaltenNr == 2) return aktuelleFreigabe.getClient().getName();
      if (spaltenNr == 3) return aktuelleFreigabe.getBenutzer().getNameFormal();
      if (spaltenNr == 4) {
        int restDauerInSekunden = aktuelleFreigabe.getDauer();

        int anzahlStunden = restDauerInSekunden / 3600;
        restDauerInSekunden = restDauerInSekunden % 3600;
        int anzahlMinuten = restDauerInSekunden / 60;
        restDauerInSekunden = restDauerInSekunden % 60;

        return zahlenFormat.format(anzahlStunden)+":"+
               zahlenFormat.format(anzahlMinuten)+":"+
               zahlenFormat.format(restDauerInSekunden);
      } 
      if (spaltenNr == 5) return waehrungsFormat.format(Buecherei.getInstance().berechneInternetzugangsKosten(aktuelleFreigabe.getDauer()));
    } else {
      if (spaltenNr == 4) {
        int restDauerInSekunden = dauerSumme;

        int anzahlStunden = restDauerInSekunden / 3600;
        restDauerInSekunden = restDauerInSekunden % 3600;
        int anzahlMinuten = restDauerInSekunden / 60;
        restDauerInSekunden = restDauerInSekunden % 60;

        return zahlenFormat.format(anzahlStunden)+":"+
               zahlenFormat.format(anzahlMinuten)+":"+
               zahlenFormat.format(restDauerInSekunden);
      } 
      if (spaltenNr == 5) return waehrungsFormat.format(kostenSumme);      
      return null;
    }
    return "Fehler";
  }
  
  public boolean getZeigeZeilenHintergrund(int modellZeile, int seitenZeile) {
    if (modellZeile == getZeilenAnzahl()) return false;
    return super.getZeigeZeilenHintergrund(modellZeile, seitenZeile);
  }

  public float getZellenRandOben(int modellZeile,int seitenZeile,int spalte) {
    if (modellZeile == 1 || modellZeile == getZeilenAnzahl()) return 1;
    return 0;   
  }  
}