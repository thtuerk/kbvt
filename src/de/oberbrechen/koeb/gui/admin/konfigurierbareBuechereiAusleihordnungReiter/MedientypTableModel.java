package de.oberbrechen.koeb.gui.admin.konfigurierbareBuechereiAusleihordnungReiter;

import java.util.Iterator;

import javax.swing.JFrame;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.datenstrukturen.MedientypListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist eine Tabellenmodell für eine Tabelle von 
* Medientyp-Einstellungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MedientypTableModel extends AbstractListeTableModel<MedientypDaten> {
  
  final JFrame hauptFenster;
  
  public MedientypTableModel(JFrame hauptFenster) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    this.hauptFenster = hauptFenster;    
    initDaten(new Liste<MedientypDaten>());
  }  
      
  public int getColumnCount() {
    return 6;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Medientyp";
    if (columnIndex == 1) return "Plural";
    if (columnIndex == 2) return "Mindestausleihdauer";
    if (columnIndex == 3) return "Mediennr.-Präfix";
    if (columnIndex == 4) return "langes Mediennr.-Format";
    if (columnIndex == 5) return "Beispiel";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return String.class;
    if (columnIndex == 1) return String.class;
    if (columnIndex == 2) return Integer.class;
    if (columnIndex == 3) return String.class;
    if (columnIndex == 4) return Boolean.class;
    if (columnIndex == 5) return String.class;
    return String.class;
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0:
      case 1:
        return 120;
      case 2:
      case 3:
        return 100;
      case 4:
        return 40;
      case 5:
        return 100;
      case 6:
        return 300;
    }
    
    return 300;
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    MedientypDaten gewaehlteDaten = (MedientypDaten) daten.get(rowIndex);
    
    if (columnIndex == 0) return gewaehlteDaten.getMedientyp();
    if (columnIndex == 1) return gewaehlteDaten.getPlural();
    if (columnIndex == 2) return new Integer(gewaehlteDaten.getMindestAusleihDauer());
    if (columnIndex == 3) return gewaehlteDaten.getMedienNrPraefix();
    if (columnIndex == 4) return new Boolean(gewaehlteDaten.isLangesMedienNrFormat());
    if (columnIndex == 5) return gewaehlteDaten.getBeispiel();
    return "nicht definierte Spalte";
  }  
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return (columnIndex < 5);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex >= 5) return;
    
    MedientypDaten gewaehlteDaten = (MedientypDaten) daten.get(rowIndex);    
    if (columnIndex == 0) gewaehlteDaten.setMedientyp((String) aValue, hauptFenster);
    if (columnIndex == 1) gewaehlteDaten.setPlural((String) aValue);
    if (columnIndex == 2) gewaehlteDaten.setMindestAusleihDauer(((Integer) aValue).intValue());
    if (columnIndex == 3) gewaehlteDaten.setMedienNrPraefix((String) aValue);
    if (columnIndex == 4) gewaehlteDaten.setLangesMedienNrFormat(((Boolean) aValue).booleanValue());

    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  public void refresh() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    MedientypFactory medientypFactory = Datenbank.getInstance().getMedientypFactory();
    MedientypListe medientypListe = medientypFactory.getAlleMedientypen();
    
    Liste<MedientypDaten> neueDaten = new Liste<MedientypDaten>();
    Iterator<Medientyp> it = medientypListe.iterator();
    while (it.hasNext()) {
      neueDaten.add(new MedientypDaten(it.next()));
    }
    
    setDaten(neueDaten);
  }

  public boolean zeigeRahmen() {
    return true;
  }

}
