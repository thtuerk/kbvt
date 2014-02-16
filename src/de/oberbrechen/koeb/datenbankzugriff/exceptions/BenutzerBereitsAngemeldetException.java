package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.*;

/**
* Diese Exception wird geworfen, wenn versucht wird, einen Benutzer für eine
* Veranstaltung anzumelden, für die er bereits angemeldet ist.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class BenutzerBereitsAngemeldetException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue BenutzerBereitsAngemeldetException mit
   * der übergebenen Fehlermeldung und der übergebenenen
   * Veranstaltungsteilnahme, die den Benutzer bereits für die Veranstaltung
   * anmeldet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktTeilnahme die Veranstaltungsteilnahme, die den Benutzer
   *   bereits für die Veranstaltung anmeldet
   */
  public BenutzerBereitsAngemeldetException(String meldung,
    Veranstaltungsteilnahme konfliktTeilnahme) {

    super(meldung, konfliktTeilnahme);
  }

  /**
   * Erstellt eine neue BenutzerBereitsAngemeldetException mit
   * der Standardfehlermeldung und der übergebenenen
   * Veranstaltungsteilnahme, die den Benutzer bereits für die Veranstaltung
   * anmeldet.
   *
   * @param konfliktTeilnahme die Veranstaltungsteilnahme, die den Benutzer
   *   bereits für die Veranstaltung anmeldet
   */
  public BenutzerBereitsAngemeldetException(Veranstaltungsteilnahme
    konfliktTeilnahme) {

    super("Der Benutzer "+ konfliktTeilnahme.getBenutzer().getName()+
      " kann nicht für die Veranstaltung "+
      konfliktTeilnahme.getVeranstaltung().getTitel()+" angemeldet werden, "+
      "da die Teilnahme "+konfliktTeilnahme.getId()+" dieses Benutzer "+
      "bereits für diese Veranstaltung anmeldet!", konfliktTeilnahme);
  }

  /**
   * Liefert die Teilnahme, die in Konflikt mit der Anmeldung steht
   * @return die Teilnahme, die in Konflikt mit der Anmeldung steht
   */
  public Veranstaltungsteilnahme getKonfliktTeilnahme() {
    return (Veranstaltungsteilnahme) super.getKonfliktDatensatz();
  }

}
