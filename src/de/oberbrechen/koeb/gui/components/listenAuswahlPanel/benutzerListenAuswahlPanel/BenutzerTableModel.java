package de.oberbrechen.koeb.gui.components.listenAuswahlPanel.benutzerListenAuswahlPanel;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
 * Diese Klasse ist ein Tabellenmodell für eine Tabelle von Systematiken.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BenutzerTableModel extends AbstractListeSortierbaresTableModel<Benutzer> {

  public BenutzerTableModel(Liste<Benutzer> daten) {
    initDaten(daten);
  }

  public int getColumnCount() {
    return 4;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Nr";
    if (columnIndex == 1) return "Name";
    if (columnIndex == 2) return "Anmeldedatum";
    if (columnIndex == 3) return "Adresse";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return Integer.class;
    return String.class;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    Benutzer gewaehlterBenutzer = (Benutzer) daten.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return new Integer(gewaehlterBenutzer.getId());
      case 1:
        return gewaehlterBenutzer.getNameFormal();
      case 2:
        return formatDatum(gewaehlterBenutzer.getAnmeldedatum());
      case 3:
        return gewaehlterBenutzer.getAdresse();
    }
    return "nicht definierte Spalte";
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 50;
      case 1: return 250;
      case 2: return 100;
      case 3: return 250;
    }
    return 50;
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(BenutzerListe.BenutzernrSortierung, umgekehrteSortierung);
        break;
      case 1:
        daten.setSortierung(BenutzerListe.NachnameVornameSortierung, umgekehrteSortierung);
        break;
      case 2:
        daten.setSortierung(BenutzerListe.AnmeldedatumSortierung, umgekehrteSortierung);
        break;
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return (spalte != 3);
  }
  
  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(1, false);
  }
}
