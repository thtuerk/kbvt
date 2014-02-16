package de.oberbrechen.koeb.pdf.pdfMahnungsListe;

import java.text.DecimalFormat;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Mahnung;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.einstellungen.Ausleihordnung;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell für die Internetstatistiken eines Monats
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
class MahnungTabellenModell extends TabellenModell {
  
  private Mahnung mahnung;
  private static DecimalFormat dauerFormat = new DecimalFormat("0.0");
  private static DecimalFormat kostenFormat = new DecimalFormat("0.00 EUR");
  
  public MahnungTabellenModell(Mahnung mahnung) {
    this.mahnung = mahnung;
    setSpaltenAusrichtung(3, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    setSpaltenAusrichtung(4, TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS);
    
    setBreiteProzent(1, 15);    
    setBreiteProzent(2, 55);    
    setBreiteProzent(3, 15);    
    setBreiteProzent(4, 15);    
  }

  public int getSpaltenAnzahl() {
    return 4;
  }

  public int getZeilenAnzahl() {
    try {
      return mahnung.getAnzahlAusleihen()+1;
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      return 0;
    }
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "Mediennr.";
    if (spaltenNr == 2) return "Titel";
    if (spaltenNr == 3) return "Überzugsdauer";
    if (spaltenNr == 4) return "Mahngebühr";
    return "unbekannte Spalte";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (zeilenNr == getZeilenAnzahl()) {
      if (spaltenNr == 4) {
        double mahngebuehr;
        try {
          mahngebuehr = mahnung.getMahngebuehren();
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, false);
          return "FEHLER!";
        }
        return (mahngebuehr == 0)?"-":kostenFormat.format(mahngebuehr);
      }
      return null;
    } else {        
      Ausleihe aktuelleAusleihe;
      try {
        aktuelleAusleihe = (Ausleihe) mahnung.getAusleihenListe().get(zeilenNr-1);
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, false);
        return "FEHLER!";        
      }
      Medium aktuellesMedium = aktuelleAusleihe.getMedium();
      
      if (spaltenNr == 1)
        return aktuellesMedium==null?"":aktuellesMedium.getMedienNr();
      else if (spaltenNr == 2)
        return aktuellesMedium==null?"nicht eingestelltes Medium":aktuellesMedium.getTitel();
      else if (spaltenNr == 3) {
        double ueberzogeneWochen =
          ((double) ((aktuelleAusleihe.getUeberzogeneTage()*2)/7))/2;          
        return 
          ueberzogeneWochen>0?dauerFormat.format(ueberzogeneWochen)+" W.":"-";
      }
      else if (spaltenNr == 4) {    
        double mahngebuehr = Ausleihordnung.getInstance().
          berechneMahngebuehren(aktuelleAusleihe);
        return (mahngebuehr == 0)?"-":kostenFormat.format(mahngebuehr);
      }
      return "Fehler";
    }
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