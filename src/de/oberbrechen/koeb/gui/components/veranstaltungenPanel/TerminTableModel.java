package de.oberbrechen.koeb.gui.components.veranstaltungenPanel;

import de.oberbrechen.koeb.datenbankzugriff.Termin;
import de.oberbrechen.koeb.datenstrukturen.TerminListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;
import de.oberbrechen.koeb.datenstrukturen.ZeitraumInkonsistenzException;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Terminen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class TerminTableModel extends AbstractListeTableModel<Termin> {

  /**
   * Erstellt ein neues Modell
   */
  public TerminTableModel() {
    daten = new TerminListe();
    daten.setSortierung(TerminListe.chronologischeSortierung);
  }
   
  public int getColumnCount() {
    return 1;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Termine";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    Termin gewaehlterTermin = (Termin) daten.get(rowIndex);
    switch (columnIndex) {
      case 0: return gewaehlterTermin.getZeitraumFormat(
          Zeitraum.ZEITRAUMFORMAT_LANG_MIT_UHRZEIT);     
    }
    return "ungültige Spalte";
  }


  /**
   * Entfernt leere Termine 
   * @throws ZeitraumInkonsistenzException
   */
  public void saeubere() throws ZeitraumInkonsistenzException {
    TerminListe loeschliste = new TerminListe();
    
    for (int i=0; i < daten.size(); i++) {
      Termin aktuellerTermin = (Termin) daten.get(i);
      if (aktuellerTermin.getBeginn() == null) {
        loeschliste.add(aktuellerTermin);
      }
    }
    daten.removeAll(loeschliste);
    
    for (int i=0; i < daten.size(); i++) {
      Termin aktuellerTermin = (Termin) daten.get(i);
      aktuellerTermin.check();
    }
    
    fireTableDataChanged();    
  }
}
