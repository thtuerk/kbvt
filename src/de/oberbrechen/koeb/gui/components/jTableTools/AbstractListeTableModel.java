package de.oberbrechen.koeb.gui.components.jTableTools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import de.oberbrechen.koeb.datenstrukturen.Liste;

/**
 * Diese Klasse ist eine abstrakte Oberklasse für
 * Tabellenmodelle, die ihre Daten als Liste abspeicheren.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractListeTableModel<T> extends AbstractTableModel
  implements TableRendererModel {

  private static SimpleDateFormat datumsformatKurz = 
    new SimpleDateFormat("dd.MM.yyyy");
  private static SimpleDateFormat datumsformatLang =
    new SimpleDateFormat("EE, dd. MMM yyyy");
  private static DecimalFormat kostenFormat = 
    new DecimalFormat("0.00 EUR");
  
  
  protected static String formatDatum(Date date) {
    return date==null?"-":datumsformatKurz.format(date);    
  }
  
  protected static String formatDatumLang(Date date) {
    return date==null?"-":datumsformatLang.format(date);    
  }
  
  protected static String formatKosten(double kosten) {
    return kosten==0?"-":kostenFormat.format(kosten);    
  }
  
  
  protected Liste<T> daten;
  
  /**
   * Initialisiert das Modell mit neuen Daten. Diese Methode sollte 
   * zu Beginn aufgerufen werden.
   */
  @SuppressWarnings("unchecked")
  public void initDaten(Liste<? extends T> daten) {
    this.daten = (Liste<T>) daten;
    fireTableDataChanged();
  }
      
  public void clear() {
    daten.clear();
    fireTableDataChanged();
  }
    
  //neue Methoden
  public void setDaten(Collection<? extends T> neueDaten) {
    daten.clear();
    daten.addAllNoDuplicate(neueDaten);
    fireTableDataChanged();
  }

  public void addDaten(Collection<? extends T> neueDaten) {
    daten.addAll(neueDaten);
    fireTableDataChanged();
  }

  public void removeDaten(Collection<T> alteDaten) {
    daten.removeAll(alteDaten);
    fireTableDataChanged();
  }

  //Methoden für das TableModel, Doku siehe bitte dort
  public int getRowCount() {
    if (daten == null) return 0;
    return daten.size();
  }

  public Liste<T> getDaten() {
    return daten;
  }

  /**
   * Ist das übergebene Object in den Daten enthälten, wird es entfernt, 
   * ansonsten hinzugenommen.
   */
  public void switchDaten(T object) {
    if (daten.contains(object)) {
      daten.remove(object);
    } else {
      daten.add(object);
    }
    fireTableDataChanged();
  }
  
  /**
   * Fügt das Object zum Modell hinzu.
   * @param object die hinzuzufügende Ausleihe
   */
  public void add(T object) {
    if (object == null) return;
    daten.add(object);
    fireTableDataChanged();
  }

  /**
   * Entfernt das Object aus dem Modell.
   * @param ausleihe die zu entfernende Ausleihe
   */
  public void remove(T object) {
    if (object == null) return;
    daten.remove(object);
    fireTableDataChanged();
  }
    
  /**
   * Löscht die angegebene Zeile aus dem Modell
   */
  public void remove(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < daten.size()) {
      daten.remove(rowIndex);
      fireTableRowsDeleted(rowIndex, rowIndex);
    }
  }  
  
  /**
   * Liefert das Object in der übergebenen Zeile des Modell 
   */
  public T get(int rowIndex) {
    return daten.get(rowIndex);
  }
    
  public int getAlignment(int rowIndex, int columnIndex) {
    if (this.getColumnClass(columnIndex) == Boolean.class) {
      return AUSRICHTUNG_ZENTRIERT;
    } else {
      return AUSRICHTUNG_LINKS;
    }
  }

  public int getColor(int rowIndex, int columnIndex) {
    return FARBE_STANDARD;
  }

  public int getDefaultColumnWidth(int columnIndex) {
    return 50;
  }

  public int getFont(int rowIndex, int columnIndex) {
    return SCHRIFT_STANDARD;
  }

  public boolean zeigeRahmen() {
    return false;
  }
  
  public boolean zeigeSortierung() {
   return true; 
  }
  
  /**
   * Liefert die Zeile, in der das übergebene Object steht oder
   * -1, falls das Object nicht in der Liste ist.
   */
  public int indexOf(Object o) {
    return daten.indexOf(o);
  }  
  
  /**
   * Liefert den Code für die gewünschte Schrift.
   * @param fett
   * @param kursiv
   */
  public static int getSchrift(boolean fett, boolean kursiv) {
    if (fett && kursiv) {
      return SCHRIFT_FETT_KURSIV;
    } else if (fett) {
      return SCHRIFT_FETT;
    } else if (kursiv) {
      return SCHRIFT_KURSIV;
    } else {
      return SCHRIFT_STANDARD;
    }
  }
}
