package de.oberbrechen.koeb.gui.veranstaltungen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.gui.MainReiter;

/**
 * Dieses Interface repräsentiert einen beliebigen Reiter, der
 * in Main eingebunden werden kann.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface VeranstaltungenMainReiter extends MainReiter {

  /**
   * Teilt dem Reiter mit, dass sich der aktuelle Benutzer geändert hat.
   * @param benutzer der neue Benutzer
   */
  public void setBenutzer(Benutzer benutzer);

  /**
   * Teilt dem Reiter mit, dass sich der aktuelle Veranstaltungsgruppe
   * geändert hat.
   * @param veranstaltungsgruppe die neue Veranstaltungsgruppe
   */
  public void setVeranstaltungsgruppe(Veranstaltungsgruppe
    veranstaltungsgruppe);
}