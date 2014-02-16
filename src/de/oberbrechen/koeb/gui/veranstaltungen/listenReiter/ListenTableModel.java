package de.oberbrechen.koeb.gui.veranstaltungen.listenReiter;

import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsteilnahme;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsteilnahmeListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.TableRendererModel;

/**
 * Diese Klasse ist ein Tabellenmodell für eine Tabelle von Veranstaltungen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ListenTableModel extends AbstractTableModel 
  implements TableRendererModel {

  //Die zur Verfügung stehenden Sortierungen
  public static final int SORTIERUNG_NAME =
    VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung;
  public static final int SORTIERUNG_KLASSE =
    VeranstaltungsteilnahmeListe.BenutzerKlasseSortierung;
  public static final int SORTIERUNG_ANMELDENR =
    VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung;
  public static final int SORTIERUNG_BEMERKUNGEN =
    VeranstaltungsteilnahmeListe.BemerkungenSortierung;

  private BenutzerListe benutzerListe;
  private JTable table;
  private VeranstaltungsteilnahmeListe veranstaltungsteilnahmeListe;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  
  public ListenTableModel(JTable table) {
    benutzerListe = null;
    this.table = table;
    veranstaltungsteilnahmeListe = null;
  }

  /**
   * Zeigt die in der übergebenen BenutzerListe enthaltenen Daten an.
   * @param benutzerListe die BenutzerListe, deren Daten angezeigt
   *   werden sollen.
   */
  public void setDaten(BenutzerListe benutzerListe) {
    this.benutzerListe = benutzerListe;
    this.veranstaltungsteilnahmeListe = null;
    fireTableStructureChanged();
  }

  /**
   * Zeigt die in der übergebenen VeranstaltungsteilnahmeListe
   * enthaltenen Daten an.
   * @param veranstaltungsteilnahmeListe die VeranstaltungsteilnahmeListe,
   *   deren Daten angezeigt werden sollen.
   */
  public void setDaten(VeranstaltungsteilnahmeListe
    veranstaltungsteilnahmeListe) {

    this.benutzerListe = null;
    this.veranstaltungsteilnahmeListe = veranstaltungsteilnahmeListe;
    fireTableStructureChanged();
  }
    
  public void fireTableStructureChanged() {
    super.fireTableStructureChanged();
    TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < getColumnCount(); i++) 
      columnModel.getColumn(i).setPreferredWidth(getDefaultColumnWidth(i));      
  }  

  //Methoden für das TableModel, Doku siehe bitte dort
  public int getRowCount() {
    if (veranstaltungsteilnahmeListe != null)
      return veranstaltungsteilnahmeListe.size();

    if (benutzerListe != null)
      return benutzerListe.size();

    return 0;
  }

  public int getColumnCount() {
    return benutzerListe==null?4:3;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Name";
    if (columnIndex == 1) return "Klasse";    
    if (columnIndex == 2) return "Bemerkungen";
    if (columnIndex == 3) return "Anmeldedatum";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex > this.getRowCount()) return null;

    if (veranstaltungsteilnahmeListe != null) {
      Veranstaltungsteilnahme gewaehlteTeilnahme = 
         veranstaltungsteilnahmeListe.get(rowIndex);
      if (columnIndex == 0)
        return gewaehlteTeilnahme.getBenutzer().getNameFormal();
      if (columnIndex == 1) return gewaehlteTeilnahme.getBenutzer().getKlasse();
      if (columnIndex == 2) return gewaehlteTeilnahme.getBemerkungen();
      if (columnIndex == 3) return dateFormat.format(gewaehlteTeilnahme.getAnmeldeDatum());
      return "nicht definierte Spalte";
    }

    if (benutzerListe != null) {
      Benutzer gewaehlterBenutzer = benutzerListe.get(rowIndex);
      if (columnIndex == 0) return gewaehlterBenutzer.getNameFormal();
      if (columnIndex == 1) return gewaehlterBenutzer.getKlasse();
      if (columnIndex == 2) return gewaehlterBenutzer.getBemerkungen();
      return "nicht definierte Spalte";
    }

    return null;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (veranstaltungsteilnahmeListe != null) {
      Veranstaltungsteilnahme gewaehlteTeilnahme = 
        veranstaltungsteilnahmeListe.get(rowIndex);
      if (columnIndex == 1)
        gewaehlteTeilnahme.getBenutzer().setKlasse(aValue.toString());
      if (columnIndex == 2)
        gewaehlteTeilnahme.setBemerkungen(aValue.toString());
    }

    if (benutzerListe != null) {
      Benutzer gewaehlterBenutzer = benutzerListe.get(rowIndex);
      if (columnIndex == 1) 
        gewaehlterBenutzer.setKlasse(aValue.toString());
      if (columnIndex == 2)
        gewaehlterBenutzer.setBemerkungen(aValue.toString());      
    }
    fireTableCellUpdated(rowIndex, columnIndex);
  }

  /**
   * Liefert den Benutzer, der in der übergebenen Zeile angezeigt wird.
   * @param rowIndex die Zeile, deren Benutzer geliefert werden soll
   */
  public Benutzer getBenutzer(int rowIndex) {
    if (veranstaltungsteilnahmeListe != null) {
      Veranstaltungsteilnahme gewaehlteTeilnahme = 
         veranstaltungsteilnahmeListe.get(rowIndex);
      return gewaehlteTeilnahme.getBenutzer();
    }

    if (benutzerListe != null) {
      return benutzerListe.get(rowIndex);
    }

    return null;
  }

  /**
   * Liefert die Teilnahme, der in der übergebenen Zeile angezeigt wird, oder
   * null, wenn keine Teilnahme angezeigt wird.
   * @param rowIndex die Zeile, deren Teilnahme geliefert werden soll
   */
  public Veranstaltungsteilnahme getVeranstaltungsteilnahme(int rowIndex) {
    if (veranstaltungsteilnahmeListe != null) {
      return veranstaltungsteilnahmeListe.get(rowIndex);
    }

    return null;
  }
  
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return ((columnIndex == 1 || columnIndex == 2));
  }

  /**
   * Sortiert die Tabelle nach der übergebenen Sortierung. Die zur Verfügung
   * stehenden Sortierungen stehen als öffentliche Konstanten bereit.
   *
   * @param sortierung die neue Sortierung
   * @throws IllegalArgumentExeception falls die übergebene Sortierung unbekannt
   *  ist
   */
  public void sortiere(int sortierung) {
    if (veranstaltungsteilnahmeListe != null) {
      if (sortierung == SORTIERUNG_NAME) {
        veranstaltungsteilnahmeListe.setSortierung(
          VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung);
      } else if (sortierung == SORTIERUNG_KLASSE) {
        veranstaltungsteilnahmeListe.setSortierung(
          VeranstaltungsteilnahmeListe.BenutzerKlasseSortierung);
      } else if (sortierung == SORTIERUNG_ANMELDENR) {
        veranstaltungsteilnahmeListe.setSortierung(
          VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung);
      } else if (sortierung == SORTIERUNG_BEMERKUNGEN) {
        veranstaltungsteilnahmeListe.setSortierung(
          VeranstaltungsteilnahmeListe.BemerkungenSortierung);
      } else {
        throw new IllegalArgumentException("Eine Sortierung mit der Nummer "+
          sortierung + " ist unbekannt!");
      }
      fireTableDataChanged();
      return;
    }

    if (benutzerListe != null) {
      if (sortierung == SORTIERUNG_NAME) {
        benutzerListe.setSortierung(
          BenutzerListe.NachnameVornameSortierung);
      } else if (sortierung == SORTIERUNG_KLASSE) {
        benutzerListe.setSortierung(
          BenutzerListe.KlasseNachnameVornameSortierung);
      } else if (sortierung == SORTIERUNG_ANMELDENR) {
        throw new IllegalArgumentException("Eine Benutzerliste kann nicht "+
          "nach der Anmeldenummer sortiert werden!");
      } else if (sortierung == SORTIERUNG_BEMERKUNGEN) {
        throw new IllegalArgumentException("Eine Benutzerliste kann nicht "+
          "nach den Bemerkungen sortiert werden!");
      } else {
        throw new IllegalArgumentException("Eine Sortierung mit der Nummer "+
          sortierung + " ist unbekannt!");
      }
      fireTableDataChanged();
      return;
    }
  }

  public void save() {
    try {
      if (veranstaltungsteilnahmeListe != null) {
        veranstaltungsteilnahmeListe.setErhalteSortierung(false);
        for(Veranstaltungsteilnahme aktuelleTeilnahme : veranstaltungsteilnahmeListe) {
          aktuelleTeilnahme.save();
          aktuelleTeilnahme.getBenutzer().save();
        }
        veranstaltungsteilnahmeListe.setErhalteSortierung(true);
      }
  
      if (benutzerListe != null) {
        benutzerListe.setErhalteSortierung(false);
        for (Benutzer b : benutzerListe) b.save();
        benutzerListe.setErhalteSortierung(true);
      }
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Teilnehmerliste konnte "+
          "nicht gespeichert werden!", false);
    }
    fireTableDataChanged();
  }

  public void reload() {
    try {
      if (veranstaltungsteilnahmeListe != null) {
        veranstaltungsteilnahmeListe.setErhalteSortierung(false);
        for(Veranstaltungsteilnahme aktuelleTeilnahme : veranstaltungsteilnahmeListe) {
          aktuelleTeilnahme.reload();
          aktuelleTeilnahme.getBenutzer().reload();
        }
        veranstaltungsteilnahmeListe.setErhalteSortierung(true);
      }
  
      if (benutzerListe != null) {
        benutzerListe.setErhalteSortierung(false);
        for (Benutzer b : benutzerListe) {
          b.reload();
        }
        benutzerListe.setErhalteSortierung(true);
      }
      fireTableDataChanged();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Teilnehmerliste konnte "+
          "nicht neu aufgebaut werden!", false);
    }
  }

  public int getColor(int rowIndex, int columnIndex) {
    if (veranstaltungsteilnahmeListe == null) return FARBE_STANDARD;
    
    Veranstaltungsteilnahme teilnahme = 
      (Veranstaltungsteilnahme) veranstaltungsteilnahmeListe.get(rowIndex);
    if (teilnahme.istAufWarteListe()) {
      return FARBE_BLAU;
    } else {
      return FARBE_STANDARD;
    }
  }

  public int getFont(int rowIndex, int columnIndex) {
    if (veranstaltungsteilnahmeListe == null) return SCHRIFT_STANDARD;
    
    Veranstaltungsteilnahme teilnahme = 
      (Veranstaltungsteilnahme) veranstaltungsteilnahmeListe.get(rowIndex);
    if (teilnahme.istAufBlockierenderWarteListe()) {
      return SCHRIFT_FETT;
    } else if (teilnahme.istAufWarteListe()) {
      return SCHRIFT_STANDARD;
    } else {
      return SCHRIFT_STANDARD;      
    }
  }

  public int getAlignment(int rowIndex, int columnIndex) {
    return AUSRICHTUNG_LINKS;
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0: return 175;        
      case 1: return 40;        
      case 2: return 250;        
      case 3: return 150;        
    }
    return 250;
  }

  public boolean zeigeRahmen() {
    return true;
  }

  public boolean zeigeSortierung() {
    return false;
  }
}
