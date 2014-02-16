package de.oberbrechen.koeb.framework;

/**
 * Dieses Implementierung des ErrorHandlers gibt Meldungen 
 * auf System.err aus.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class StandardErrorHandler extends ErrorHandler {

  public void handleException(Exception e, String beschreibung, boolean istKritisch) {
    System.err.println("Folgender Fehler trat auf:\n");
    if (beschreibung != null) System.err.println(beschreibung);
    System.err.println();
    System.err.println(e.getLocalizedMessage());
    System.err.println();
    e.printStackTrace(System.err);
    if (istKritisch) System.exit(1);
  }

  public void handleError(String beschreibung, boolean istKritisch) {
    System.err.println("Folgender Fehler trat auf:\n");
    if (beschreibung != null) System.err.println(beschreibung);
    System.err.println();
    if (istKritisch) System.exit(1);
  }
}