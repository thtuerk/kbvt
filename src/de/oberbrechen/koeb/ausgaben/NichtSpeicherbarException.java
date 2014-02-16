package de.oberbrechen.koeb.ausgaben;

/**
* Diese Exception wird geworfen, wenn versucht wird, eine Ausgabe zu speichern, die dies nicht unterstützt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class NichtSpeicherbarException extends Exception {

  public NichtSpeicherbarException() {
    super("Die Ausgabe unterstützt das Speichern in eine Datei nicht!");
  }

}
