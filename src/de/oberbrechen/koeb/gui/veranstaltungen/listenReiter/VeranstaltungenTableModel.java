package de.oberbrechen.koeb.gui.veranstaltungen.listenReiter;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.gui.components.jTableTools.TableRendererModel;

import javax.swing.table.AbstractTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Veranstaltungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class VeranstaltungenTableModel extends AbstractTableModel
  implements TableRendererModel {

  private Veranstaltungsgruppe veranstaltungsgruppe;
  private VeranstaltungenListe daten;
  private VeranstaltungsteilnahmeFactory teilnahmeFactory =
    Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();

  public VeranstaltungenTableModel(Veranstaltungsgruppe gruppe) {
    this.daten = new VeranstaltungenListe();
    daten.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);
    setVeranstaltungsgruppe(gruppe);
  }

  /**
   * Zeigt die zur übergebenen Veranstaltungsgruppe gehörenden
   * Veranstaltungen in der Tabelle an.
   * @param neueGruppe die Veranstaltungsgruppe, deren Veranstaltungen angezeigt
   *   werden sollen.
   */
  @SuppressWarnings("unchecked")
  public void setVeranstaltungsgruppe(Veranstaltungsgruppe neueGruppe) {
    veranstaltungsgruppe = neueGruppe;
    
    VeranstaltungFactory veranstaltungFactory =
      Datenbank.getInstance().getVeranstaltungFactory();
    
    VeranstaltungenListe neueDaten = null;    
    if (veranstaltungsgruppe != null)
      neueDaten = veranstaltungFactory.getVeranstaltungenMitAnmeldung(
      veranstaltungsgruppe);

    daten.clear();
    if (neueDaten != null) {
      daten.addAllNoDuplicate(neueDaten);
    }
    fireTableDataChanged();
  }

  //Methoden für das TableModel, Doku siehe bitte dort
  public int getRowCount() {
    if (daten == null || daten.size() == 0) return 0;
    return daten.size()+1;
  }

  public int getColumnCount() {
    return 3;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Titel";
    if (columnIndex == 1) return "Teilnehmeranzahl";
    if (columnIndex == 2) return "Kosten";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex > daten.size()) return null;

    if (rowIndex == 0) {
      if (columnIndex == 0) return "Gesamt";
      if (columnIndex == 1) return
        Integer.toString(teilnahmeFactory.getTeilnehmerAnzahl(veranstaltungsgruppe));
      if (columnIndex > 1) return "-";
    } else {
      Veranstaltung gewaehlteVeranstaltung = getVeranstaltung(rowIndex);
      if (columnIndex == 0) return gewaehlteVeranstaltung.getTitel();
      if (columnIndex == 1) {        
        int teilnehmerAnzahl = 
          teilnahmeFactory.getTeilnehmerAnzahl(gewaehlteVeranstaltung);
        int warteListenLaenge = 
          teilnahmeFactory.getWartelistenLaenge(gewaehlteVeranstaltung);
        int maxAnzahl = gewaehlteVeranstaltung.getMaximaleTeilnehmerAnzahl();
        
        String result = Integer.toString(teilnehmerAnzahl).trim();
        if (warteListenLaenge > 0) result += "+"+Integer.toString(warteListenLaenge).trim();
        if (maxAnzahl > 0) result += "/"+Integer.toString(maxAnzahl).trim();
        return result;      
      }
      if (columnIndex == 2) return gewaehlteVeranstaltung.getKostenFormatiert();
    }
    return "nicht definierte Spalte";
  }

  /**
   * Liefert die Veranstaltung, die in der übergebenen Zeile des Models
   * dargestellt wird
   */
  public Veranstaltung getVeranstaltung(int rowIndex) {
    if (rowIndex < 1 || rowIndex > daten.size()) return null;
    return (Veranstaltung) daten.get(rowIndex-1);
  }

  public int getColor(int rowIndex, int columnIndex) {
    if (rowIndex == 0) return FARBE_GRUEN;
    
    Veranstaltung veranstaltung = getVeranstaltung(rowIndex);
    boolean istBelegt = teilnahmeFactory.istBelegt(veranstaltung);
    boolean warteListeBelegt = teilnahmeFactory.getBlockierendeWartelistenLaenge(veranstaltung) > 0; 
    
    if (!istBelegt && !warteListeBelegt) {      
      return FARBE_STANDARD;
    } else if (istBelegt) {
      return FARBE_ROT;
    } else {
      return FARBE_BLAU;
    }
  }

  public int getFont(int rowIndex, int columnIndex) {
    return rowIndex==0?SCHRIFT_FETT:SCHRIFT_STANDARD;
  }

  public int getAlignment(int rowIndex, int columnIndex) {
    switch (columnIndex) {
      case 0: return AUSRICHTUNG_LINKS;        
      case 1: return AUSRICHTUNG_ZENTRIERT;        
      case 2: return AUSRICHTUNG_RECHTS;        
    }
    return AUSRICHTUNG_LINKS;
  }

  public int getDefaultColumnWidth(int columnIndex) {
    return columnIndex==0?200:50;
  }

  public boolean zeigeRahmen() {
    return false;
  }

  public boolean zeigeSortierung() {
    return false;
  }
}
