package de.oberbrechen.koeb.email;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse repräsentiert eine Versandmethode für eMails. 
 * Sie kann z.B. verwendet werden, um extrene Programme mit dem 
 * Versand zu beauftragen, oder um direkt einen SMTP Server anzusprechen. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class EMailHandler {
  
  protected static EMailHandler instanz = null;

  /**
   * Löscht die gecachte Instanz
   */
  public static void clearInstance() {
    instanz = null;
  }
    
  /**
   * Liefert eine Instanz, des konfigurierten EMailsHandlers.
   */
  public static EMailHandler getInstance() {
    if (instanz != null) return instanz;
    
    try {
      instanz = (EMailHandler) Datenbank.getInstance().getEinstellungFactory().
        getClientEinstellung(
          "de.oberbrechen.koeb.email", 
          "EMailHandler").getWertObject(EMailHandler.class, ExternerEMailHandler.class);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }

    return instanz;
  }
  
  /**
   * Versendet die übergebene eMail. Je nach Implementierung kann die eMail 
   * vorher zur Verarbeitung angezeigt oder direkt versendet werden. Der
   * Parameter gibt lediglich einen Wusch an. Ob dieser erfüllt werden kann
   * verraten die Funktionen erlaubtAnzeige() und erlaubtDirektversand().
   * @param email
   * @param anzeige
   * @throws VersandException
   */
  public void versende(EMail email, boolean anzeige) {    
    try {
      if (!erlaubtAnzeige() && !erlaubtDirektversand())
        throw new VersandException("Weder direkter noch indirekter Versand möglich!");
        
      boolean bearbeiten = erlaubtAnzeige() && (anzeige || !erlaubtDirektversand());

      versendeIntern(email, bearbeiten);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Beim Versenden der eMail " +
          "trat folgender unerwarteter Fehler auf:", false);
    }
  }
  
  /**
   * Das eigentliche Versenden der eMail
   * @param email
   * @param bearbeiten
   * @throws Exception
   */
  protected abstract void versendeIntern(EMail email, boolean bearbeiten) throws Exception;

  /**
   * Liefert, ob der EMailHandler in der Lage ist, eMails zur Bearbeitung 
   * anzuzeigen.
   * @return <code>true</code>, gdw. wenn der eMail-Handler eMails zur 
   *  Bearbeitung anzeigen kann
   */
  public abstract boolean erlaubtAnzeige();

  /**
   * Liefert, ob der EMailHandler in der Lage ist, eMails ohne Anzeige direkt
   * zu versenden
   * @return <code>true</code>, gdw. wenn der eMail-Handler eMails ohne
   *  Anzeige direkt versenden kann.
   */
  public abstract boolean erlaubtDirektversand();
}
