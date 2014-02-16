package de.oberbrechen.koeb.framework;

/**
 * Dieses Interface kapselt Methoden, die es ermöglichen sollen, auf
 * Fehler zu reagieren. Auf eine konkrete Instanz kann mittels 
 * getInstanz() bzw. setInstanz() zugegriffen werden. 
 * Bei Fehlern wird die Fehlerbehandlung dann mittels der hier definierten
 * Methoden dieses ErrorHandlers durchgeführt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public abstract class ErrorHandler {

  private static ErrorHandler instance = new StandardErrorHandler();
  
  /**
   * Liefert die eingestellte Instanz des ErrorHandlers
   * @return ErrorHandler
   */
  public static ErrorHandler getInstance() {
    return instance;
  }
  
  /**
   * Setzt die Instanz des ErrorHandlers die verwendet werden soll.
   * @param errorHandler der zu verwendende ErrorHandler
   */
  public static void setInstance(ErrorHandler errorHandler) {
    if (errorHandler == null) throw new NullPointerException();
    instance = errorHandler;
  }

  /**
   * Behandelt eine aufgetretene Exception.
   * @param beschreibung eine Beschreibung des Fehlers
   * @param e die zu behandelnde Exception
   * @param istKritisch ist der Fehler so kritisch, dass das System
   *   beendet werden muss
   */
  public void handleException(Exception e, String beschreibung, boolean istKritisch) {
    StringBuffer message = new StringBuffer();
    if (beschreibung != null) message.append(beschreibung);
    message.append("\n\n");
    message.append(e.getLocalizedMessage());
    
    handleError(message.toString(), istKritisch);
  }

  /**
   * Behandelt eine aufgetretene Exception.
   * @param e die zu behandelnde Exception
   * @param istKritisch ist der Fehler so kritisch, dass das System
   *   beendet werden muss
   */
  public void handleException(Exception e, boolean istKritisch) {
    handleException(e, "Unerwarteter Fehler!", istKritisch);
  }
  
  /**
   * Behandelt einen aufgetretenen Fehler.
   * @param beschreibung eine Beschreibung des Fehlers
   * @param istKritisch ist der Fehler so kritisch, dass das System
   *   beendet werden muss
   */
  public abstract void handleError(String beschreibung, boolean istKritisch);
}