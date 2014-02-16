package de.oberbrechen.koeb.gui.bestand.medienReiter;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;

import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.gui.bestand.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von Medien.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MedienTableModel extends AbstractListeSortierbaresTableModel<Medium> 
  implements Observer {

  private Main hauptFenster;
  private int sortierung;
  private boolean umgekehrteSortierung;
  private JTable tabelle;
  
  public MedienTableModel(JTable tabelle, Main hauptFenster) {
    this.hauptFenster = hauptFenster;
    this.tabelle = tabelle;
    MedienListe daten = new MedienListe();
    daten.addAll(hauptFenster.getAlleMedien());
    initDaten(daten);
  }
      
  public int getColumnCount() {
    return 4;
  }
  
  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Mediennr.";
    if (columnIndex == 1) return "Titel";
    if (columnIndex == 2) return "Autor";
    if (columnIndex == 3) return "ISBN";
    return "nicht definierte Spalte";
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.size()) return null;
    Medium gewaehltesMedium = (Medium) daten.get(rowIndex);
    if (columnIndex == 0) return gewaehltesMedium.getMedienNr();
    if (columnIndex == 1) return 
    DatenmanipulationsFunktionen.entferneNull(gewaehltesMedium.getTitel());
    if (columnIndex == 2) return
    DatenmanipulationsFunktionen.entferneNull(gewaehltesMedium.getAutor());
    if (columnIndex == 3) {
      ISBN isbn = gewaehltesMedium.getISBN();
      if (isbn == null) return "";
      return isbn.getISBN();
    }
    return "nicht definierte Spalte";
  }

  public void update(Observable o, Object arg) {
    this.fireTableDataChanged();
  }

  public void remove(Medium medium) {
    super.remove(medium);
    hauptFenster.getAlleMedien().remove(medium);
  }

  public void add(Medium medium) {
    super.add(medium);
    hauptFenster.getAlleMedien().add(medium);
  }
  
  
  public void select(Medium medium) {
    int row = daten.indexOf(medium);
    if (row == -1) return;
    tabelle.getSelectionModel().setSelectionInterval(row, row);
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0:
        sortierung = MedienListe.MedienNummerSortierung;
        this.umgekehrteSortierung = umgekehrteSortierung;
        break;
      case 1:
        sortierung = MedienListe.TitelAutorSortierung;
        this.umgekehrteSortierung = umgekehrteSortierung;
        break;
      case 2:
        sortierung = MedienListe.AutorTitelSortierung;
        this.umgekehrteSortierung = umgekehrteSortierung;
        break;
      case 3:
        sortierung = MedienListe.ISBNSortierung;
        this.umgekehrteSortierung = umgekehrteSortierung;
        break;
      default:
        return;
    }
    
    daten.setSortierung(sortierung, umgekehrteSortierung);    
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return true;
  }

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(1, false);
  }

  public boolean istSortierungUmgekehrt() {
    return umgekehrteSortierung;
  }

  public int getSortierung() {
    return sortierung;
  }
}
