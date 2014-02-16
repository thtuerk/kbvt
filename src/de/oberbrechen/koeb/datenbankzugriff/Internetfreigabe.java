package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;

/**
* Dieses Interface repräsentiert eine Internetfreigabe der Bücherei.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface Internetfreigabe extends Datenbankzugriff {

  /**
   * Liefert die Dauer in Sekunden die der letzte Internetzugang für den
   * Rechner freigeschaltet war. Ist der Zugang noch freigeschaltet, wird die
   * Dauer bis zum aktuellen Zeitpunkt geliefert. War der Rechner noch nie
   * freigeschaltet wird 0 geliefert.
   *
   * @return die Dauer in Sekunden die der letzte Internetzugang für den
   *   Rechner freigeschaltet war
   */
  public int getDauer();
      
  /**
   * Liefert den Zeitpunkt, an dem der Internetzugang freigeschaltet wurde
   * @return den Zeitpunkt, an dem der Internetzugang freigeschaltet wurde
   */
  public Date getStartZeitpunkt();

  /**
   * Liefert den Zeitpunkt, bis zu dem der Internetzugang freigeschaltet wurde
   * @return den Zeitpunkt, bis zu dem Internetzugang freigeschaltet wurde
   */
  public Date getEndZeitpunkt();

  /**
   * Liefert den Client dieser Internetfreigabe.
   * @return den Client dieser Internetfreigabe
   */
  public Client getClient();

  /**
   * Bestimmt, ob die Freigabe aktuell ist. Eine Freigabe ist aktuell, wenn
   * sie nicht vor mehr als 3 Stunden beendet wurde.
   *
   * @return <code>true</code> gdw. die Freigabe aktuell ist
   */
  public boolean istAktuell();

  /**
   * Liefert den Benutzer der Internetfreigabe, d.h. den Benutzer, für den der
   * Internetzugang freigeschaltet war bzw. ist.
   * @return den Benutzer der Internetfreigabe
   */
  public Benutzer getBenutzer();

  /**
   * Liefert den Mitarbeiter der Internetfreigabe, d.h. den Mitarbeiter, der
   * die Freigabe tätigte.
   * @return den Benutzer der Internetfreigabe
   */
  public Mitarbeiter getMitarbeiter();

  /**
   * Bestimmt, ob die Freigabe noch aktiv ist, d.h. ob der Internetzugang
   * zur Zeit noch freigeschaltet ist.
   * @return <code>true</code> gdw. der Internetzugang freigeschaltet ist.
   */
  public boolean istFreigegeben();
  
  /**
   * Sperrt den Internetzugang für den Client, den die aktuelle Freigabe 
   * freigibt und aktualisiert die Freigabe.
   * @throws DatenbankInkonsistenzException
   * @throws DatenNichtGefundenException
   */
  public void sperren() throws DatenNichtGefundenException, DatenbankInkonsistenzException;  
}