package de.oberbrechen.koeb.pdf.pdfJahresStatistik;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfigurationDaten;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell für die Bestandstatistiken eines Jahres
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BestandStatistikJahrTabellenModell extends TabellenModell {
  
  Vector<String> namen;
  Vector<int[]> daten;
  Vector<String> spalten;

  public BestandStatistikJahrTabellenModell(int jahr, 
      AuswahlKonfiguration konfiguration) throws DatenbankInkonsistenzException {
    
    Zeitraum zeitraum = new Zeitraum(jahr);
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    
    if (cal.getTime().before(zeitraum.getEnde()) && cal.getTime().after(zeitraum.getBeginn())) {
      cal.add(Calendar.DATE, 1);
      zeitraum.setEnde(cal.getTime());
    }
        
    MediumFactory mediumFactory = Datenbank.getInstance().getMediumFactory();
    
    MedienListe beginnMedien = mediumFactory.getAlleMedien(zeitraum.getBeginn());
    AuswahlKonfigurationDaten<Medium> beginnDaten = konfiguration.bewerte(beginnMedien);
    beginnDaten.ueberpruefeChecks();
    
    MedienListe neuMedien = mediumFactory.getEingestellteMedienInZeitraum(zeitraum); 
    AuswahlKonfigurationDaten<Medium> neuDaten = konfiguration.bewerte(neuMedien);
    neuDaten.ueberpruefeChecks();  
    
    MedienListe entferntMedien = mediumFactory.getEntfernteMedienInZeitraum(zeitraum); 
    AuswahlKonfigurationDaten<Medium> entferntDaten = konfiguration.bewerte(entferntMedien);
    entferntDaten.ueberpruefeChecks();
    
    MedienListe endeMedien = mediumFactory.getAlleMedien(zeitraum.getEnde()); 
    AuswahlKonfigurationDaten<Medium> endeDaten = konfiguration.bewerte(endeMedien);
    endeDaten.ueberpruefeChecks();
    
    namen = new Vector<String>();
    daten = new Vector<int[]>();
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); 
    spalten = new Vector<String>();
    spalten.add(dateFormat.format(zeitraum.getBeginn()));
    spalten.add("entfernt"); 
    spalten.add("Neueinstellungen"); 
    spalten.add(dateFormat.format(zeitraum.getEnde()));
    
    
    for (int i=0; i < konfiguration.getAusgabeAnzahl(); i++) {
      namen.add(konfiguration.getAusgabe(i).getTitel());
      int[] neueDaten = new int[4];
      neueDaten[0] = beginnDaten.getAusgabeTreffer(i).size();
      neueDaten[1] = entferntDaten.getAusgabeTreffer(i).size();
      neueDaten[2] = neuDaten.getAusgabeTreffer(i).size();
      neueDaten[3] = endeDaten.getAusgabeTreffer(i).size();
      
      if (neueDaten[0]-neueDaten[1]+neueDaten[2] != neueDaten[3]) {
        ErrorHandler.getInstance().handleError("Falsche Berechnung der Daten für $0!", false);
      }
        
      daten.add(neueDaten);
    }
       
    setSpaltenAusrichtung(2, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(3, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(5, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setBreiteProzent(1, 40f);    
    setBreiteProzent(2, 15f);    
    setBreiteProzent(3, 15f);    
    setBreiteProzent(4, 15f);        
    setBreiteProzent(5, 15f);        
  }

  public int getSpaltenAnzahl() {
    return 5;
  }

  public int getZeilenAnzahl() {
    return namen.size();
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return ""; 
    if (spaltenNr > 1 && spaltenNr < 6) {
      return spalten.get(spaltenNr-2).toString();
    }
    return "nicht definierte Spalte"; 
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (spaltenNr == 1) return namen.get(zeilenNr-1).toString();
    if (spaltenNr > 1 && spaltenNr < 6) {
      int wert = daten.get(zeilenNr-1)[spaltenNr-2];      
      return wert == 0?"-":Integer.toString(wert); 
    }
    return "!!! FEHLER !!!"; 
  }
}