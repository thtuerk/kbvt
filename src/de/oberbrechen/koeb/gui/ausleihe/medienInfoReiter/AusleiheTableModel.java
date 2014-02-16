package de.oberbrechen.koeb.gui.ausleihe.medienInfoReiter;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Ausleihen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class AusleiheTableModel extends AbstractListeSortierbaresTableModel<Ausleihe> {

  private static final long serialVersionUID = 1L;

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
    if (columnIndex == 0) return "Benutzer";
    if (columnIndex == 1) return "Ausleihzeitraum";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Ausleihe gewaehlteAusleihe = (Ausleihe) daten.get(rowIndex);
    if (columnIndex == 0) return gewaehlteAusleihe.getBenutzer().getName();
    if (columnIndex == 1) {
      return Zeitraum.getZeitraumFormat(
          gewaehlteAusleihe.getAusleihdatum(), 
          gewaehlteAusleihe.getRueckgabedatum(),
          Zeitraum.ZEITRAUMFORMAT_KURZ);
    }
    return "nicht definierte Spalte";
  }
  
  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 300;
      case 1: return 300;
    }

    return 500;
  }    

  public int getFont(int rowIndex, int columnIndex) {
    Ausleihe ausleihe = (Ausleihe) daten.get(rowIndex);    
    
    if (!ausleihe.istZurueckgegeben()) {
      return SCHRIFT_FETT;
    } else {
      return SCHRIFT_STANDARD;      
    }
  }

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(1, false);    
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(AusleihenListe.BenutzerVornameSortierung, umgekehrteSortierung);
        return;
      case 1:        
        daten.setSortierung(AusleihenListe.AusleihdatumSortierung, umgekehrteSortierung);        
        return;
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return true;
  }
  
  public boolean zeigeSortierung() {
    return false;
  } 
}
