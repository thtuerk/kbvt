package de.oberbrechen.koeb.gui.ausleihe.ausleihenInfoReiter;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
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
    return 3;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Medien-Nr";
    if (columnIndex == 1) return "Titel";
    if (columnIndex == 2) return "Ausleihzeitraum";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Ausleihe gewaehlteAusleihe = (Ausleihe) daten.get(rowIndex);
    if (columnIndex == 0) { 
      Medium medium = gewaehlteAusleihe.getMedium();
      return medium==null?"-":medium.getMedienNr();
    }
    if (columnIndex == 1) {
      Medium medium = gewaehlteAusleihe.getMedium();
      return medium==null?"nicht eingestelltes Medium":medium.getTitel();
    }      
    if (columnIndex == 2) {
      return Zeitraum.getZeitraumFormat(
          gewaehlteAusleihe.getAusleihdatum(), 
          gewaehlteAusleihe.getRueckgabedatum(),
          Zeitraum.ZEITRAUMFORMAT_KURZ);
    }
    return "nicht definierte Spalte";
  }

  public int getFont(int rowIndex, int columnIndex) {
    if (!((Ausleihe) get(rowIndex)).istZurueckgegeben()) {
      return SCHRIFT_FETT;
    } else {
      return SCHRIFT_STANDARD;      
    }
  }

  public int getDefaultColumnWidth(int columnIndex) {
    if (columnIndex == 1) return 120;
    return 60;
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(AusleihenListe.MedienNrSortierung, umgekehrteSortierung);
        return;
      case 1:
        daten.setSortierung(AusleihenListe.MedienTitelSortierung, umgekehrteSortierung);
        return;
      case 2:
        daten.setSortierung(AusleihenListe.AusleihdatumSortierung, umgekehrteSortierung);
        return;
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return true;
  }
  
  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(1, false);
  }
}
