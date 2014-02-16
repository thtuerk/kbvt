package de.oberbrechen.koeb.pdf.pdfJahresStatistik;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import de.oberbrechen.koeb.datenbankzugriff.AusleiheFactory;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell für die Benutzer und Veranstaltungsstatistiken eines Jahres
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AktiveLeserStatistikJahrTabellenModell extends TabellenModell {
  
  class BenutzerInfos {       
    Benutzer benutzer;
    AusleihenListe ausleihen = new AusleihenListe();
    AusleihzeitraumListe verlaengerungen = new AusleihzeitraumListe();
    MedienListe medien = new MedienListe();
    int medienAnzahlUnbekannt = 0;
    
    public BenutzerInfos(Benutzer benutzer) {
      this.benutzer = benutzer;     
    }
    
    public void addAusleihzeitraum(Ausleihzeitraum ausleihzeitraum) {
      ausleihen.add(ausleihzeitraum.getAusleihe());
      verlaengerungen.add(ausleihzeitraum);
      Medium medium = ausleihzeitraum.getAusleihe().getMedium();
      if (medium != null) { 
        medien.add(medium);
      } else {
        medienAnzahlUnbekannt++;
      }
    }
  }
  
  BenutzerInfos[] daten;
  int maxRows;
  
  public AktiveLeserStatistikJahrTabellenModell(int jahr) throws DatenbankInkonsistenzException {    
    this(jahr, 15);
  }
  
  public AktiveLeserStatistikJahrTabellenModell(int jahr, int maxRows) throws DatenbankInkonsistenzException {    
    Zeitraum zeitraum = new Zeitraum(jahr);
    this.maxRows = maxRows; 
    AusleiheFactory ausleiheFactory = Datenbank.getInstance().getAusleiheFactory();
    
    AusleihzeitraumListe ausleihzeitraumListe = 
      ausleiheFactory.getGetaetigteAusleihzeitraeumeInZeitraum(zeitraum);
    Hashtable<Benutzer, BenutzerInfos> infosHash = new Hashtable<Benutzer, BenutzerInfos>();
    
    int benutzerCount = 0;
    for (int i=0; i < ausleihzeitraumListe.size(); i++) {
      Ausleihzeitraum ausleihzeitraum = 
        (Ausleihzeitraum) ausleihzeitraumListe.get(i);
      
      BenutzerInfos infos = infosHash.get(ausleihzeitraum.getAusleihe().getBenutzer());
      if (infos == null) {
        infos = new BenutzerInfos(ausleihzeitraum.getAusleihe().getBenutzer());
        infosHash.put(infos.benutzer, infos);
        benutzerCount++;
      }

      infos.addAusleihzeitraum(ausleihzeitraum);
    }
    
    daten = new BenutzerInfos[benutzerCount];
    benutzerCount = 0;
    Enumeration<BenutzerInfos> enumeration = infosHash.elements();
    while (enumeration.hasMoreElements()) {
      daten[benutzerCount] = enumeration.nextElement();
      benutzerCount++;
    }
    
    Arrays.sort(daten, new Comparator<BenutzerInfos>() {

      Collator collator = Collator.getInstance();
      
      public int compare(BenutzerInfos aInfos, BenutzerInfos bInfos) {
        if (aInfos == null || bInfos == null)
          throw new NullPointerException();
        
        int erg = (bInfos.verlaengerungen.size() - aInfos.verlaengerungen.size());
        if (erg != 0) return erg;
        erg = (bInfos.ausleihen.size() - aInfos.ausleihen.size());
        if (erg != 0) return erg;
        erg = collator.compare(aInfos.benutzer.getNameFormal(), bInfos.benutzer.getNameFormal()); 
        if (erg != 0) return erg;
        
        return aInfos.benutzer.getId() - bInfos.benutzer.getId();
      }});
    
    setBreiteProzent(1, 40);
    setBreiteProzent(2, 34);
    setBreiteProzent(3, 13);
    setBreiteProzent(4, 13);
    setSpaltenAusrichtung(2, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(3, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
  }

  public int getSpaltenAnzahl() {
    return 4;
  }

  public int getZeilenAnzahl() {
    return daten.length < maxRows?daten.length:maxRows;
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "Benutzer"; 
    if (spaltenNr == 2) return "Ausleihen & Verlängerungen"; 
    if (spaltenNr == 3) return "Ausleihen"; 
    if (spaltenNr == 4) return "Medien"; 
    return "nicht definierte Spalte"; 
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    BenutzerInfos infos = daten[zeilenNr-1];
    if (spaltenNr == 1) return infos.benutzer.getNameFormal();
    if (spaltenNr == 2) {
      int erg = infos.verlaengerungen.size();      
      return erg == 0?"-":Integer.toString(erg); 
    }
    if (spaltenNr == 3) {
      int erg = infos.ausleihen.size();      
      return erg == 0?"-":Integer.toString(erg); 
    }
    if (spaltenNr == 4) {
      int erg = infos.medien.size()+infos.medienAnzahlUnbekannt;      
      return erg == 0?"-":Integer.toString(erg); 
    }
    return "!!! FEHLER !!!"; 
  } 
}
