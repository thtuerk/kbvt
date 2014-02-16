package de.oberbrechen.koeb.pdf.pdfAusleihStatistik;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfigurationAusleihzeitraumTagesDaten;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfigurationDaten;
import de.oberbrechen.koeb.datenbankzugriff.AusleiheFactory;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse erstellt eine StatistikTabellenModell, das Ausleihstatistiken
 * für ein Jahr oder einen Monat repräsentiert.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AusleihStatistikTabellenModell extends TabellenModell {

  private static SimpleDateFormat dateFormat = new SimpleDateFormat("EE, d. MMM. yyyy");
  private static SimpleDateFormat monatDateFormat = new SimpleDateFormat("MMMM");  
  
  Vector<int[]> daten;
  Vector<String> zeilenTitel;
  AuswahlKonfigurationAusleihzeitraumTagesDaten statistik;
  
  /**
   * Erstellt ein neues StatistikTabellenModell für
   * das übergebene Jahr. Die nötigen Daten werden im
   * Gegensatz zum Entsprechenden Konstruktor vorher aus der Datenbank
   * geladen und bewertet.
   * @throws DatenbankInkonsistenzException
   */
  public static AusleihStatistikTabellenModell createStatistikTabellenModell(
      AuswahlKonfiguration statistik, 
      int jahr) throws DatenbankInkonsistenzException {
    if (statistik == null)
      throw new NullPointerException("Es muss eine Statistik übergeben werden!");
    
    AusleiheFactory ausleiheFactory =
      Datenbank.getInstance().getAusleiheFactory();
    AusleihzeitraumListe liste = ausleiheFactory.getGetaetigteAusleihzeitraeumeInZeitraum(
        new Zeitraum(jahr));
    AuswahlKonfigurationDaten<Ausleihzeitraum> daten = statistik.bewerte(liste);    
    daten.ueberpruefeChecks();    
    AuswahlKonfigurationAusleihzeitraumTagesDaten tagesDaten =
      new AuswahlKonfigurationAusleihzeitraumTagesDaten(daten);        
    
    return new AusleihStatistikTabellenModell(tagesDaten, jahr);
  }
  
  /**
   * Erstellt ein neues StatistikTabellenModell für den übergebenen
   * Monat und das übergebene Jahr. Die nötigen Daten werden im
   * Gegensatz zum Entsprechenden Konstruktor vorher aus der Datenbank
   * geladen und bewertet.
   * @param statistik
   * @param monat
   * @param jahr
   * @return
   * @throws DatenbankInkonsistenzException
   */
  public static AusleihStatistikTabellenModell createStatistikTabellenModell(
      AuswahlKonfiguration statistik, 
      int monat, int jahr) throws DatenbankInkonsistenzException {
    
    AusleiheFactory ausleiheFactory =
      Datenbank.getInstance().getAusleiheFactory();
    AusleihzeitraumListe liste = ausleiheFactory.getGetaetigteAusleihzeitraeumeInZeitraum(
        new Zeitraum(monat, jahr));
    AuswahlKonfigurationDaten<Ausleihzeitraum> daten = statistik.bewerte(liste);    
    AuswahlKonfigurationAusleihzeitraumTagesDaten tagesDaten =
      new AuswahlKonfigurationAusleihzeitraumTagesDaten(daten);
    
    daten.ueberpruefeChecks();        
    return new AusleihStatistikTabellenModell(tagesDaten, monat, jahr);
  }

  /**
   * Erstellt ein TabellenModell für die übergebene Statistik und das übergebene
   * Jahr. Es werden für jeden Monat alle Daten aufsummiert sowie eine 
   * Gesamtsumme ausgegeben. Das übergebene Dokument wird genötigt, um die
   * zur Verfügung stehende Seitenbreite zu bestimmen und die Spalten 
   * dementsprechend zu initialisieren. Soll das Spaltenmodell für Ausgaben
   * in mehreren Dokumenten mit unterschiedlicher Breite benutzt werden, so
   * kann diese Initialisierung mittels initSpalten wiederholt werden.
   * @param statistik
   * @param jahr
   * @param dokument
   */
  public AusleihStatistikTabellenModell(
      AuswahlKonfigurationAusleihzeitraumTagesDaten statistik, 
      int jahr) {
    super(statistik.getAusgabenAnzahl()+1, true);
    
    Calendar calendar = Calendar.getInstance();
    int[] summe = new int[statistik.getAusgabenAnzahl()];
    int[][] monatDaten = new int[12][statistik.getAusgabenAnzahl()];    
    for (int i = 0; i < statistik.getTageAnzahl(); i++) {
      calendar.setTime(statistik.getTag(i));
      if (calendar.get(Calendar.YEAR) == jahr) {
        int monat = calendar.get(Calendar.MONTH);
        for (int j=0; j < statistik.getAusgabenAnzahl(); j++) {
          monatDaten[monat][j] += statistik.getWert(i, j);
          summe[j] += statistik.getWert(i, j);            
        }
      }
    }
    
    zeilenTitel = new Vector<String>();
    daten = new Vector<int[]>();
    int startMonat=1;
    int endMonat=12;
    if (!arrayLeer(summe)) {
      while (arrayLeer(monatDaten[startMonat-1]))
        startMonat++;
      while (arrayLeer(monatDaten[endMonat-1]))
        endMonat--;
    }
    
    for (int i=startMonat-1; i < endMonat; i++) {      
      zeilenTitel.add(getMonatName(i+1));
      daten.add(monatDaten[i]);
    }    
    zeilenTitel.add("Summe");
    daten.add(summe);

    this.statistik = statistik;            
    initSpalten();
  }
  
  /**
   * Bestimmt, ob alle Werte des Arrays 0 sind.
   * @return
   */
  private boolean arrayLeer(int[] array) {
    for (int i=0; i < array.length; i++) 
      if (array[i] != 0) return false;
      
    return true;
  }
  
  /**
   * Erstellt ein TabellenModell für die übergebene Statistik und den übergebenen 
   * Monat und das übergebene Jahr. Das übergebene Dokument wird genötigt, um die
   * zur Verfügung stehende Seitenbreite zu bestimmen und die Spalten 
   * dementsprechend zu initialisieren. Soll das Spaltenmodell für Ausgaben
   * in mehreren Dokumenten mit unterschiedlicher Breite benutzt werden, so
   * kann diese Initialisierung mittels initSpalten wiederholt werden.
   * @param statistik
   * @param monat
   * @param jahr
   * @param dokument
   */
  public AusleihStatistikTabellenModell(
      AuswahlKonfigurationAusleihzeitraumTagesDaten statistik, 
      int monat, int jahr) {
    super(statistik.getAusgabenAnzahl()+1, true);
    
    Calendar calendar = Calendar.getInstance();
    zeilenTitel = new Vector<String>();
    daten = new Vector<int[]>();
    int[] summe = new int[statistik.getAusgabenAnzahl()];
    for (int i = 0; i < statistik.getTageAnzahl(); i++) {
      calendar.setTime(statistik.getTag(i));
      if (calendar.get(Calendar.MONTH) == monat-1 && calendar.get(Calendar.YEAR) == jahr) {
        int[] aktuelleDaten = new int[statistik.getAusgabenAnzahl()];
        for (int j=0; j < statistik.getAusgabenAnzahl(); j++) {
          aktuelleDaten[j] = statistik.getWert(i, j);
          summe[j] += statistik.getWert(i, j);            
        }
        zeilenTitel.add(dateFormat.format(statistik.getTag(i)));          
        daten.add(aktuelleDaten);
      }
    }
    zeilenTitel.add("Summe");
    daten.add(summe);

    this.statistik = statistik;        
    
    initSpalten();
  }
  
  private String getMonatName(int monat) {
    Calendar monatKalender = Calendar.getInstance();
    monatKalender.set(2000, monat-1, 1);
    return monatDateFormat.format(monatKalender.getTime());    
  }
    
  private void initSpalten() {    
    setBreiteProzent(1, 20);
    float andereSpaltenBreiten = 80f / (statistik.getAusgabenAnzahl());
        
    for (int i = 2; i < statistik.getAusgabenAnzahl() +2; i++) {
      setSpaltenAusrichtung(i, TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL);        
      setBreiteProzent(i, andereSpaltenBreiten);        
      
      if (statistik.getAusgabe(i-2).getLevel() > 0)
        setZeigeSpaltenHintergrund(i, true);        
    }
    setSpaltenAbstand(1);
    setSpaltenAbstand(1, 5);      
    setSpaltenAbstandHintergrund(1, 1);      
  }
  
  public int getSpaltenAnzahl() {
    return statistik.getAusgabenAnzahl()+1;
  }
  
  public int getZeilenAnzahl() {
    return daten.size();
  }
  
  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "";
    return statistik.getAusgabe(spaltenNr-2).getTitel();
  }
  
  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (spaltenNr == 1) {
      return zeilenTitel.get(zeilenNr-1).toString();
    }
    int anzahl = daten.get(zeilenNr-1)[spaltenNr-2];
    return (anzahl == 0)?"-":Integer.toString(anzahl);
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