package de.oberbrechen.koeb.pdf.pdfAktuelleAusleihenListe;

import java.text.SimpleDateFormat;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell aktuellen Ausleihen eines Benutzers
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
class AktuelleAusleihenTabellenModell extends TabellenModell {
  
  private AusleihenListe aktuelleAusleihen;
  private SimpleDateFormat dateFormat=new SimpleDateFormat("EE, d. MMM. yyyy");
  
  public AktuelleAusleihenTabellenModell(Benutzer benutzer) throws DatenbankInkonsistenzException {
    aktuelleAusleihen = Datenbank.getInstance().getAusleiheFactory().
      getAlleNichtZurueckgegebenenAusleihenVon(benutzer);
    aktuelleAusleihen.setSortierung(AusleihenListe.MedienTitelSortierung, 
      false);
    
    setSpaltenAbstand(3);
    setBreiteProzent(1, 12.5f);    
    setBreiteProzent(2, 42.5f);    
    setBreiteProzent(3, 25);    
    setBreiteProzent(4, 20);    
  }

  public int getSpaltenAnzahl() {
    return 4;
  }

  public int getZeilenAnzahl() {
    if (aktuelleAusleihen.size() > 0) return aktuelleAusleihen.size();
    return 1;
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "Mediennr.";
    if (spaltenNr == 2) return "Titel";
    if (spaltenNr == 3) return "Autor";
    if (spaltenNr == 4) return "Rückgabedatum";
    return "unbekannte Spalte";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    if (aktuelleAusleihen.size() == 0) return "-";
    
    Ausleihe aktuelleAusleihe = 
      (Ausleihe) aktuelleAusleihen.get(zeilenNr-1);
    Medium medium = aktuelleAusleihe.getMedium();
    
    if (spaltenNr == 1)
      return medium==null?"-":medium.getMedienNr();
    else if (spaltenNr == 2)
      return medium==null?"nicht eingestelltes Medium":medium.getTitel();
    else if (spaltenNr == 3)
      return medium==null?"":medium.getAutor();
    else if (spaltenNr == 4)
      return dateFormat.format(aktuelleAusleihe.getSollRueckgabedatum());    

    return "Fehler";
  }
    
  public int getZellenSchrift(int modellZeile, int seitenZeile, int spalte) {
    if (aktuelleAusleihen.size() == 0) return SCHRIFT_NORMAL;
    
    Ausleihe aktuelleAusleihe = 
      (Ausleihe) aktuelleAusleihen.get(modellZeile-1);
    return aktuelleAusleihe.istUeberzogen()?SCHRIFT_FETT:SCHRIFT_NORMAL;
  }

}