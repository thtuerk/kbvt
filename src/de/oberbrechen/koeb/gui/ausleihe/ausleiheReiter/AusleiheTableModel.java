package de.oberbrechen.koeb.gui.ausleihe.ausleiheReiter;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Ausleihen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class AusleiheTableModel extends AbstractListeSortierbaresTableModel<Ausleihe> {

  /**
   * Erstellt ein neues Modell ohne Daten, das nach dem Sollrückgabedatum
   * der Ausleihen sortiert ist
   */
  public AusleiheTableModel() {
    initDaten(new AusleihenListe());
  }

  /**
   * Erstellt ein neues Modell mit den übergebenen Daten in der dort angegebenen
   * Sortierung
   *
   * @param daten die enthaltenen Ausleihen
   */
  public AusleiheTableModel(AusleihenListe daten) {
    initDaten(daten);
  }

  public int getColumnCount() {
    return 2;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Titel";
    if (columnIndex == 1) return "zurückzugeben am";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Ausleihe gewaehlteAusleihe = (Ausleihe) daten.get(rowIndex);
    
    switch (columnIndex) {
      case 0:
        Medium medium = gewaehlteAusleihe.getMedium();
        return medium==null?"-":medium.getTitel();
      case 1:
        return formatDatumLang(gewaehlteAusleihe.getSollRueckgabedatum());
    }
    
    return "nicht definierte Spalte";    
  }

  public int getColor(int rowIndex, int columnIndex) {
    Ausleihe ausleihe = (Ausleihe) get(rowIndex);
    if (ausleihe.istUeberzogen()) {
      return FARBE_ROT;
    } else {
      return FARBE_STANDARD;      
    }
  }

  public int getFont(int rowIndex, int columnIndex) {
    Ausleihe ausleihe = (Ausleihe) get(rowIndex);
    
    boolean fett = ausleihe.heuteGetaetigt() || ausleihe.istAktuellVerlaengert();
    boolean kursiv = ausleihe.istZurueckgegeben();
    
    return getSchrift(fett, kursiv);
  }

  public int getDefaultColumnWidth(int columnIndex) {
    if (columnIndex == 0) return 400;
    if (columnIndex == 1) return 200;
    return 400;
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(AusleihenListe.MedienTitelSortierung,umgekehrteSortierung);
        return;
      case 1:
        daten.setSortierung(AusleihenListe.SollrueckgabeDatumSortierung, umgekehrteSortierung);
        return;
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return true;
  }

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(0, false);
  }

  public boolean zeigeSortierung() {
    return false;
  }

}
