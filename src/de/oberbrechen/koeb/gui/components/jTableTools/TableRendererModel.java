package de.oberbrechen.koeb.gui.components.jTableTools;

import javax.swing.SwingConstants;


/**
 * Ein Modell der Daten für einen Table-Renderer
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface TableRendererModel {

  //0 wird für eine Spezialaufgabe verwendet,
  //ansonstens sind alle Konstanten erlaubt. In ErweiterterTableRenderer
  //ist jedoch die entsprechende Farbe zu erzeugen
  public static final int FARBE_STANDARD = 1;
  public static final int FARBE_ROT = 2;
  public static final int FARBE_GRUEN = 3;
  public static final int FARBE_BLAU = 4;  
  public static final int FARBE_LILA = 5;  
  
  public static final int SCHRIFT_STANDARD = 0;
  public static final int SCHRIFT_FETT = 1;
  public static final int SCHRIFT_KURSIV = 2;
  public static final int SCHRIFT_FETT_KURSIV = 3;
  
  public static final int AUSRICHTUNG_LINKS = SwingConstants.LEFT;
  public static final int AUSRICHTUNG_ZENTRIERT = SwingConstants.CENTER;
  public static final int AUSRICHTUNG_RECHTS = SwingConstants.RIGHT;
 
  
  /**
   * Liefert die Farbe, die die Zelle in der übergebenen
   * Zeile und Spalte haben soll.
   */
  public int getColor(int rowIndex, int columnIndex);
  
  /**
   * Liefert die Schrift, die die Zelle in der übergebenen
   * Zeile und Spalte haben soll.
   */
  public int getFont(int rowIndex, int columnIndex);  

  /**
   * Liefert die Ausrichtung, die die Zelle in der übergebenen
   * Zeile und Spalte haben soll.
   */
  public int getAlignment(int rowIndex, int columnIndex);
  
  /**
   * Liefert die Standard-Spaltenbreite der übergebenen Spalte.
   */
  public int getDefaultColumnWidth(int columnIndex);
  
  /**
   * Bestimmt, ob einen Rahmen um die aktuelle Zelle gezeichnet werden soll
   */
  public boolean zeigeRahmen();
  
  /**
   * Bestimmt, ob die Spalte, nach der sortiert ist, gekennzeichnet werden soll. 
   */
  public boolean zeigeSortierung();    
}