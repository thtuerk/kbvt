package de.oberbrechen.koeb.gui.ausleihe.ausleiheReiter;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
 * Diese Klasse ist ein Tabellenmodell für eine Tabelle von Ausleihen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ZeitraumTableModel extends AbstractListeTableModel<Ausleihzeitraum> {

  /**
   * Erstellt ein neues Modell ohne Daten, das nach dem Sollrückgabedatum
   * der Ausleihen sortiert ist
   */
  public ZeitraumTableModel() {
    daten = new AusleihzeitraumListe();
    daten.setSortierung(AusleihzeitraumListe.beginnSortierung);
  }

  public int getColumnCount() {
    return 1;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Ausleihzeitraum gewaehlterZeitraum = 
      (Ausleihzeitraum) daten.get(rowIndex);
    if (columnIndex == 0)
      return gewaehlterZeitraum.getZeitraumFormat(
          Zeitraum.ZEITRAUMFORMAT_MITTEL);
    return "nicht definierte Spalte";
  }
  
  public String getColumnName(int col) {
    if (col == 0) return "Zeitraum";
    return "unbekannte Spalte";
  }

  public int getColor(int rowIndex, int columnIndex) {
    Ausleihzeitraum zeitraum = (Ausleihzeitraum) get(rowIndex);
    
    if (zeitraum.istUeberzogen()) {
      return FARBE_ROT;
    } else {
      return FARBE_STANDARD;      
    }
  }
}
