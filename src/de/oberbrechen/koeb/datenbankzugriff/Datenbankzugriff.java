package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;

/**
 * Dieses Interface beinhaltet Standard-Methoden für
 * Objekte die Datenbankzugriffe kapseln. Mittels dieser
 * Methoden können Daten geladen und gespeichert werden. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Datenbankzugriff {
  
  /**
   * Bestimmt, ob es sich um eine neues Datenbankzugriffobjekt 
   * handelt, d.h. um ein Objekt, das gerade neu angelegt wird 
   * und noch nicht in der Datenbank gespeichert ist.
   *
   * @return <code>true</code> gdw das Object neu ist
   */
  public abstract boolean istNeu();
  
  /**
   * Läd alle Daten erneut aus der Datenbank. Ist das
   * Objekt noch nicht gespeichert, wird keine Aktion ausgeführt.
   *
   * @throws DatenNichtGefundenException falls das Objekt inzwischen aus der
   *   Datenbank entfernt wurde
   * @throws DatenbankInkonsistenzException falls beim Laden eine
   *  Inkonsistenz entdeckt wird
   */
  public abstract void reload()
    throws DatenNichtGefundenException, DatenbankInkonsistenzException;
  
  /**
   * Überprüft, ob die aktuellen Daten schon gespeichert sind.
   * @return <code>TRUE</code> falls die Daten schon gespeichert sind<br>
   *         <code>FALSE</code> sonst
   */
  public abstract boolean istGespeichert();

  /**
   * Speichert das Objekt bzw die gemachten Änderungen in der
   * Datenbank
   */
  public abstract void save() throws DatenbankzugriffException;
      
  /**
   * Löscht das Objekt aus der Datenbank.
   * 
   * @throws DatenbankInkonsistenzException falls das Objekt nicht
   * gelöscht werden kann, weil ansonsten eine Datenbankinkonsitenz
   * entstehen würde.
   */
  public abstract void loesche() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine Textdarstellung des Objektes mit allen Informationen,
   * die vor allem zum Debuggen gedacht ist.
   *
   * @return die Textdarstellung
   */
  public abstract String toDebugString();
  
  /**
   * Liefert die ID des Objekts.
   * @return die ID des Objekts
   */
  public abstract int getId();
}