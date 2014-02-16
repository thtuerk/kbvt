package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.BemerkungenListe;

/**
 * Diese Interface repräsentiert eine Factory für Veranstaltungsbemerkungen
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface BemerkungVeranstaltungFactory extends DatenbankzugriffFactory<BemerkungVeranstaltung> {

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Veranstaltungsbemerkung-Object
   * 
   * @return das neue Objekt
   */
  public BemerkungVeranstaltung erstelleNeu(); 
  
  /**
   * Liefert eine unsortierte Liste aller Veranstaltungsbemerkungen, die in der Datenbank
   * eingetragen sind.
   * @throws DatenbankInkonsistenzException
   */
  public BemerkungenListe getAlleVeranstaltungsbemerkungen() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Veranstaltungsbemerkungen der
   * übergebenen Veranstaltung.
   * @throws DatenbankInkonsistenzException
   */
  public BemerkungenListe getAlleVeranstaltungsbemerkungen(Veranstaltung veranstaltung) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Veranstaltungsbemerkungen zu Veranstaltungen der
   * übergebenen Veranstaltungsgruppe.
   * @throws DatenbankInkonsistenzException
   */
  public BemerkungenListe getAlleVeranstaltungsgruppenbemerkungen(Veranstaltungsgruppe gruppe) throws DatenbankInkonsistenzException;

}
