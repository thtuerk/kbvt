package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;


/**
* Diese Exception wird geworfen, wenn versucht wird, eine Veranstaltungsgruppe
* zu speichern, deren Name schon verwendet wird.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class VeranstaltungsgruppeSchonVergebenException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue VeranstaltungsgruppeSchonVergebenException mit
   * der übergebenen Fehlermeldung und der übergebenen Veranstaltungsgruppe,
   * die den Namen bereits verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktVeranstaltungsgruppe die Veranstaltungsgruppe, die den 
   *   Namen schon verwendet
   */
  public VeranstaltungsgruppeSchonVergebenException(String meldung,
    Veranstaltungsgruppe konfliktVeranstaltungsgruppe) {

    super(meldung, konfliktVeranstaltungsgruppe);
  }

  /**
   * Erstellt eine neue VeranstaltungsgruppeSchonVergebenException mit
   * einer Standardfehlermeldung und der übergebenen Veranstaltungsgruppe,
   * die den Namen bereits verwendet.
   *
   * @param konfliktVeranstaltungsgruppe die Veranstaltungsgruppe, die den 
   *   Namen schon verwendet
   */
  public VeranstaltungsgruppeSchonVergebenException(
    Veranstaltungsgruppe konfliktVeranstaltungsgruppe) {
    super("Die Veranstaltungsgruppe '"+
      konfliktVeranstaltungsgruppe.getName()+"' existiert bereits!",
      konfliktVeranstaltungsgruppe);
  }

}
