package de.oberbrechen.koeb.ausgaben;

import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;

/**
* Dieses Interface repräsentiert eine Ausgabe für Veranstaltungsgruppe.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public interface VeranstaltungsgruppeAusgabe extends Ausgabe{

  /**
   * Setzt die von der Ausgabe anzuzeigende Veranstaltungsgruppe.
   */
  public void setVeranstaltungsgruppe(Veranstaltungsgruppe daten);      
}