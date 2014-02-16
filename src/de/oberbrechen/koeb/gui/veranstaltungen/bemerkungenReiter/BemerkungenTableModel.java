package de.oberbrechen.koeb.gui.veranstaltungen.bemerkungenReiter;

import java.text.SimpleDateFormat;

import de.oberbrechen.koeb.datenbankzugriff.Bemerkung;
import de.oberbrechen.koeb.datenbankzugriff.BemerkungVeranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.BemerkungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
 * Diese Klasse ist ein Tabellenmodell für eine Tabelle von Bemerkungen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BemerkungenTableModel extends AbstractListeSortierbaresTableModel<Bemerkung> {


  private static SimpleDateFormat datumsFormatierer = new
    SimpleDateFormat("dd.MM.yyyy HH:mm 'Uhr'");
  
  public BemerkungenTableModel() {
    initDaten(new BemerkungenListe());
  }
      
  public int getColumnCount() {
    return 4;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Aktuell";
    if (columnIndex == 1) return "Datum";
    if (columnIndex == 2) return "Bemerkung";
    if (columnIndex == 3) return "Veranstaltung";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return Boolean.class;
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.size()) return null;
    
    BemerkungVeranstaltung gewaehlteBemerkung = (BemerkungVeranstaltung) daten.get(rowIndex);
    if (columnIndex == 0) return new Boolean(gewaehlteBemerkung.istAktuell());
    if (columnIndex == 1) return    
      (gewaehlteBemerkung.getDatum()==null)?"-":datumsFormatierer.format(gewaehlteBemerkung.getDatum());
    if (columnIndex == 2) return
      DatenmanipulationsFunktionen.entferneNull(gewaehlteBemerkung.getBemerkung());
    if (columnIndex == 3) return
      (gewaehlteBemerkung.getVeranstaltung().getTitel());
    return "nicht definierte Spalte";
  }

  public boolean isCellEditable(int zeile, int spalte) {
    return spalte==0;
  }
  
  public void setValueAt(Object wert, int zeile, int spalte) {
    if (spalte != 0) return;
    
    try {
      Bemerkung bemerkung = (Bemerkung) get(zeile);
      bemerkung.setAktuell(((Boolean) wert).booleanValue());
      bemerkung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +
          "der Bemerkung!", false);
    }
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 1:
        daten.setSortierung(BemerkungenListe.DatumSortierung, umgekehrteSortierung);
        break;
      case 2:
        daten.setSortierung(BemerkungenListe.BemerkungSortierung, umgekehrteSortierung);        
        break;
      }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return (spalte != 3);
  }

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(1, true);
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 20;
      case 1: return 100;
      case 2: return 400;
    }
    return 50;
  }

}
