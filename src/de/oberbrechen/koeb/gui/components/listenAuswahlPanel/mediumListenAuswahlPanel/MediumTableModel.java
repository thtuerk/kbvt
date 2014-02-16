package de.oberbrechen.koeb.gui.components.listenAuswahlPanel.mediumListenAuswahlPanel;

import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Medien.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MediumTableModel extends AbstractListeSortierbaresTableModel<Medium> {

  public MediumTableModel(Liste<Medium> daten) {
    initDaten(daten);
  }
  
  public int getColumnCount() {
    return 5;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Nr";
    if (columnIndex == 1) return "Titel";
    if (columnIndex == 2) return "Autor";
    if (columnIndex == 3) return "Einstellungsdatum";
    if (columnIndex == 4) return "Systematiken";
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
      case 4:
        return gewaehltesMedium.getSystematikenString();
    }
    return "nicht definierte Spalte";
  }
  
  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 100;
      case 1: return 300;
      case 2: return 200;
      case 3: return 100;
      case 4: return 150;
    }
    return 50;
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

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(3, true);
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(MedienListe.MedienNummerSortierung, umgekehrteSortierung);
        break;
      case 1: 
        daten.setSortierung(MedienListe.TitelAutorSortierung, umgekehrteSortierung);
        break;
      case 2: 
        daten.setSortierung(MedienListe.AutorTitelSortierung, umgekehrteSortierung);
        break;                
      case 3: 
        daten.setSortierung(MedienListe.EinstellungsdatumSortierung, umgekehrteSortierung);
        break;
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return (spalte != 4);
  }
}

