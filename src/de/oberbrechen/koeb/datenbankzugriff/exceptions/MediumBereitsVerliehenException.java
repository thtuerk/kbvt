package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.*;

/**
* Diese Exception wird geworfen, wenn versucht wird, eine Ausleihe zu speichern,
* deren Medium in dem auszuleihenden Zeitraum bereits ausgeliehen ist.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class MediumBereitsVerliehenException extends
  DatenbankzugriffException {

  private Ausleihe konfliktAusleihe;
  private Ausleihe verursachendeAusleihe;

  /**
   * Erstellt eine neue MediumBereitsVerliehenException
   * @param meldung die anzuzeigende Meldung
   * @param konfliktAusleihe die Ausleihe, durch die das Medium in dem
   *   auszuleihenden Zeitraum bereits ausgeliehen ist
   * @param verursachendeAusleihe die Ausleihe, deren Medium in
   *   dem auszuleihenden Zeitraum bereits ausgeliehen ist
   */
  public MediumBereitsVerliehenException(String meldung,
    Ausleihe konfliktAusleihe, Ausleihe verursachendeAusleihe) {
    super(meldung);
    this.konfliktAusleihe = konfliktAusleihe;
    this.verursachendeAusleihe = verursachendeAusleihe;
  }

  /**
   * Erstellt eine neue MediumBereitsVerliehenException
   * @param konfliktAusleihe die Ausleihe, durch die das Medium in dem
   *   auszuleihenden Zeitraum bereits ausgeliehen ist
   * @param verursachendeAusleihe die Ausleihe, deren Medium in
   *   dem auszuleihenden Zeitraum bereits ausgeliehen ist
   */
  public MediumBereitsVerliehenException(Ausleihe konfliktAusleihe,
    Ausleihe verursachendeAusleihe) {
    this(standardFehlerMeldung(konfliktAusleihe, verursachendeAusleihe),
         konfliktAusleihe, verursachendeAusleihe);
  }

  /**
   * Liefert die Standardfehlermeldung für die übergebenen Ausleihen
   * @return die Standardfehlermeldung
   */
  private static String standardFehlerMeldung(Ausleihe konfliktAusleihe,
    Ausleihe verursachendeAusleihe) {
    String meldung;
    if (verursachendeAusleihe.istNeu())
      meldung = "Die neue Ausleihe";
    else
      meldung = "Die Ausleihe Nr. "+ verursachendeAusleihe.getId();

    meldung += " konnte nicht gespeichert werden, da das Medium '"+
               konfliktAusleihe.getMedium().getTitel()+"' ("+
               konfliktAusleihe.getMedium().getMedienNr()+") im "+
               "auszuleihenden Zeitraum schon von "+
               "Ausleihe Nr. "+konfliktAusleihe.getId()+
               " verliehen wird.";
    return meldung;
  }

  /**
   * Liefert die Ausleihe, die das Medium im auszuleihenden Zeitraum bereits
   *   ausleiht.
   * @return die Ausleihe, die das Medium im auszuleihenden Zeitraum bereits
   *   ausleiht
   */
  public Ausleihe getKonfliktAusleihe() {
    return konfliktAusleihe;
  }

  /**
   * Liefert die Ausleihe, die das Medium neu ausleihen möchte.
   * @return die Ausleihe, die das Medium neu ausleihen möchte
   */
  public Ausleihe getVerursachendeAusleihe() {
    return verursachendeAusleihe;
  }
}
