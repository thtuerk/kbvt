package de.oberbrechen.koeb.pdf.pdfInternetStatistik;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.InternetfreigabenListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell für die Internetstatistiken eines Monats
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class InternetStatistikJahrTabellenModell extends TabellenModell {
  
  Vector<String> namen;
  Vector<String> kosten;
  Vector<String> dauer;
  Vector<String> anzahl;

  public InternetStatistikJahrTabellenModell(int jahr, boolean versteckeKostenloseFreigaben) {
    DecimalFormat zahlenFormat = new DecimalFormat("00");
    DecimalFormat waehrungsFormat = new DecimalFormat("0.00 EUR");
    SimpleDateFormat monatDateFormat = new SimpleDateFormat("MMMM yyyy");

    namen = new Vector<String>();
    kosten = new Vector<String>();
    dauer = new Vector<String>();
    anzahl = new Vector<String>();
    
    int gesamtAnzahl = 0;
    double gesamtKosten = 0;
    int gesamtDauer = 0;
    
    for (int i=1; i < 13; i++) {    
      InternetfreigabeFactory internetfreigabeFactory =
        Datenbank.getInstance().getInternetfreigabeFactory();
      InternetfreigabenListe daten = 
        internetfreigabeFactory.getAlleInternetFreigabenInMonat(i, jahr);
      if (daten.size() > 0) {
        //Summen bestimmen
        double kostenSumme = 0;
        int dauerSumme = 0;
        for (Internetfreigabe aktuelleFreigabe : daten) {
          double kosten = Buecherei.getInstance().berechneInternetzugangsKosten(
              aktuelleFreigabe.getDauer());
          if (!versteckeKostenloseFreigaben || kosten > 0) {
            kostenSumme += kosten;
            dauerSumme += aktuelleFreigabe.getDauer();
          }
        }
        
        //Gesamtsummen
        gesamtAnzahl += daten.size();
        gesamtDauer += dauerSumme;
        gesamtKosten += kostenSumme;
        
        //Werte in Vectoren einfügen
        Calendar monatKalender = Calendar.getInstance();
        monatKalender.set(jahr, i-1, 1);
        namen.add(monatDateFormat.format(monatKalender.getTime()));
        anzahl.add(Integer.toString(daten.size()));
        kosten.add(waehrungsFormat.format(kostenSumme));
        
        int restDauerInSekunden = dauerSumme;

        int anzahlStunden = restDauerInSekunden / 3600;
        restDauerInSekunden = restDauerInSekunden % 3600;
        int anzahlMinuten = restDauerInSekunden / 60;
        restDauerInSekunden = restDauerInSekunden % 60;

        dauer.add(zahlenFormat.format(anzahlStunden)+":"+
               zahlenFormat.format(anzahlMinuten)+":"+
               zahlenFormat.format(restDauerInSekunden));
      }
    }

    namen.add("Summe");
    anzahl.add(Integer.toString(gesamtAnzahl));
    kosten.add(waehrungsFormat.format(gesamtKosten));
    
    int restDauerInSekunden = gesamtDauer;

    int anzahlStunden = restDauerInSekunden / 3600;
    restDauerInSekunden = restDauerInSekunden % 3600;
    int anzahlMinuten = restDauerInSekunden / 60;
    restDauerInSekunden = restDauerInSekunden % 60;

    dauer.add(zahlenFormat.format(anzahlStunden)+":"+
           zahlenFormat.format(anzahlMinuten)+":"+
           zahlenFormat.format(restDauerInSekunden));    


    setSpaltenAusrichtung(2, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(3, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setBreiteProzent(1, 25);    
    setBreiteProzent(2, 35);    
    setBreiteProzent(3, 27.5f);    
    setBreiteProzent(4, 12.5f);        
  }

  public int getSpaltenAnzahl() {
    return 4;
  }

  public int getZeilenAnzahl() {
    return namen.size();
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "";
    if (spaltenNr == 2) return "Anzahl";
    if (spaltenNr == 3) return "Dauer";
    if (spaltenNr == 4) return "Kosten";
    return "unbekannte Spalte";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (spaltenNr == 1) return namen.get(zeilenNr-1).toString();
    if (spaltenNr == 2) return anzahl.get(zeilenNr-1).toString();
    if (spaltenNr == 3) return dauer.get(zeilenNr-1).toString();
    if (spaltenNr == 4) return kosten.get(zeilenNr-1).toString();
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