package de.oberbrechen.koeb.gui.ausleihe.medienSucheReiter;

import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
 * Diese Klasse ist ein Tabellenmodell für eine Tabelle von Medien.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MedienTableModel extends AbstractListeSortierbaresTableModel<Medium> {
  
  public MedienTableModel() {
    initDaten(new MedienListe());
  }

  public int getColumnCount() {
    return 4;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Nr";
    if (columnIndex == 1) return "Titel";
    if (columnIndex == 2) return "Autor";
    if (columnIndex == 3) return "Einstellungsdatum";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    Medium gewaehltesMedium = (Medium) daten.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return gewaehltesMedium.getMedienNr();
      case 1:
        return gewaehltesMedium.getTitel();
      case 2:
        return gewaehltesMedium.getAutor();
      case 3:
        return formatDatum(gewaehltesMedium.getEinstellungsdatum());
    }
    return "nicht definierte Spalte";
  }
  
  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 85;
      case 1: return 300;
      case 2: return 170;
      case 3: return 100;
    }

    return 500;
  }    

  public int getFont(int rowIndex, int columnIndex) {
    Medium medium = (Medium) get(rowIndex);    
    boolean istNeuEingestellt = medium.istNeuEingestellt();
    boolean istNochInBestand = medium.istNochInBestand();
    
    if (!istNochInBestand && istNeuEingestellt) {
      return SCHRIFT_FETT_KURSIV;
    } else if (istNeuEingestellt) {
      return SCHRIFT_FETT;
    } else if (!istNochInBestand) {
      return SCHRIFT_KURSIV;
    } else {
      return SCHRIFT_STANDARD;      
    }
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(MedienListe.MedienNummerSortierung, umgekehrteSortierung);
        return;
      case 1: 
        daten.setSortierung(MedienListe.TitelAutorSortierung, umgekehrteSortierung);
        return;
      case 2: 
        daten.setSortierung(MedienListe.AutorTitelSortierung, umgekehrteSortierung);
        return;                
      case 3: 
        daten.setSortierung(MedienListe.EinstellungsdatumSortierung, umgekehrteSortierung);
        return;
    }
  }
  
  public boolean istSortierbarNachSpalte(int spalte) {
    return true;
  }  

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(1,false);
  }
}
