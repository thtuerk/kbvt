package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;

/**
 * Diese Klasse kapselt Daten, die aus der Auswertung 
 * einer AuswahlKonfiguration stammen, die Ausleihzeitraeume enthält
 * und gruppiert sie nach dem Taetigungsdatum.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AuswahlKonfigurationAusleihzeitraumTagesDaten 
  extends AuswahlKonfigurationDaten<Ausleihzeitraum>{
   
   Date[] tage;
   int[][] daten;
   int anzahlTage;
   
   public AuswahlKonfigurationAusleihzeitraumTagesDaten(
       AuswahlKonfigurationDaten<Ausleihzeitraum> auswahlKonfigurationDaten) {
     super(auswahlKonfigurationDaten);
     
     tage = new Date[20];
     daten = new int[20][getAusgabenAnzahl()];
     anzahlTage = 0;
     int[] aktuellerIndex = new int[getAusgabenAnzahl()];
     for (int i = 0; i < ausgabenResult.length; i++) {
       ausgabenResult[i].setSortierung(AusleihzeitraumListe.taetigungsdatumSortierung);
     }
     
     Date aktuellesDatum;
     while ((aktuellesDatum = getNextDatum(aktuellerIndex)) != null) {
       int[] tagesDaten = getTagesDaten(aktuellesDatum, aktuellerIndex);
       insertDaten(aktuellesDatum, tagesDaten);
     }

     setzeGroesse(anzahlTage);     
   }

  private void setzeGroesse(int neueGroesse) {
    if (neueGroesse < anzahlTage) return;

    Date[] neueTage = new Date[neueGroesse];
    int[][] neueDaten = new int[neueGroesse][getAusgabenAnzahl()];
    for (int i=0; i < anzahlTage; i++) {
      neueTage[i] = tage[i];
      neueDaten[i] = daten[i];
    }
    tage = neueTage;
    daten = neueDaten;
  }

  private void insertDaten(Date aktuellesDatum, int[] tagesDaten) {
    if (anzahlTage >= tage.length) 
      setzeGroesse(anzahlTage*2);

    tage[anzahlTage] = aktuellesDatum;
    daten[anzahlTage] = tagesDaten;
    anzahlTage++; 
  }

  private Date getNextDatum(int[] aktuellerIndex) {
    Date minDatum = null;
    for (int i = 0; i < getAusgabenAnzahl(); i++) {
      if (ausgabenResult[i].size() > aktuellerIndex[i]) {
        Date aktuellesDatum = ((Ausleihzeitraum) 
          (ausgabenResult[i].get(aktuellerIndex[i]))).getTaetigungsdatum();
        if (minDatum == null || aktuellesDatum.before(minDatum))
          minDatum = aktuellesDatum;
      }        
    }

    return minDatum;
  }   
   
  private int[] getTagesDaten(Date aktuellesDatum, int[] aktuellerIndex) {
    int[] result = new int[getAusgabenAnzahl()];
    
    for (int i=0; i < getAusgabenAnzahl(); i++) {
      boolean ok = true;
      while (ok && ausgabenResult[i].size() > aktuellerIndex[i]) {
        Ausleihzeitraum zeitraum = (Ausleihzeitraum) 
          ausgabenResult[i].get(aktuellerIndex[i]);
        ok = zeitraum.getTaetigungsdatum().equals(aktuellesDatum);
        if (ok) {
          aktuellerIndex[i]++;
          if (zeitraum.getAusleihe().getMedium() == null)
            result[i] += 1;
          else
            result[i] += zeitraum.getAusleihe().getMedium().getMedienAnzahl();
        }
      }
    }
    return result;
  }

  public int getTageAnzahl() {
    return anzahlTage;
  }

  public int getWert(int tagIndex, int auswahlIndex) {
    return daten[tagIndex][auswahlIndex];
  } 

  public Date getTag(int tagIndex) {
    return tage[tagIndex];
  }


  public String toDebugString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    StringBuffer buffer = new StringBuffer();

    buffer.append("SUMME     ");
    for (int i=0; i < getAusgabenAnzahl(); i++) {
      
      int sum = 0;
      for (int j=0; j < getTageAnzahl(); j++) 
        sum += getWert(j, i);
      String wert = Integer.toString(sum);
      for (int pos=0; pos < 5-wert.length(); pos++) 
        buffer.append(" ");
      buffer.append(wert);  
    }
    buffer.append("\n");

    buffer.append("----------");
    for (int j=0; j < getAusgabenAnzahl(); j++) {
      buffer.append("-----");  
    }
    buffer.append("\n");

    for (int i=0; i < getTageAnzahl(); i++) {
      buffer.append(dateFormat.format(getTag(i)));
      for (int j=0; j < getAusgabenAnzahl(); j++) {
        String wert = Integer.toString(getWert(i, j));
        for (int pos=0; pos < 5-wert.length(); pos++) 
          buffer.append(" ");
        buffer.append(wert);  
      }
      buffer.append("\n");
    }

    return buffer.toString();
  } 
}