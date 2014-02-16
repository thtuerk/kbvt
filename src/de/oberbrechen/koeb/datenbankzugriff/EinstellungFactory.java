package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenstrukturen.EinstellungenListe;
import de.oberbrechen.koeb.datenstrukturen.Liste;

/**
 * Diese Klasse kapselt Methoden zum Zugriff auf Einstellungen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface EinstellungFactory {

  /**
   * Liefert eine unsortierte Liste aller in der Datenbank gespeicherten
   * Einstellungen
   * @return die Einstellungenliste
   */
  public EinstellungenListe getAlleEinstellungen();

  /**
   * Liefert eine Liste aller Namen von Einstellungen, die in der Datenbank
   * gespeichert sind. Im Unterschied zu getAlleEinstellungen() enthält die 
   * Liste nur die Namen als Strings, nicht komplette Einstellungen mit
   * Weet, Client, Mitarbeiter, usw. Ist eine Einstellung für mehrere Clients 
   * oder Mitarbeiter in der Datenbank eingetragen, so wird der Name nur einmal 
   * in die Liste aufgenommen. 
   * @return die Liste der Namen aller Einstellungen
   */
  public Liste<String> getAlleEinstellungenNamen();
  
  /**
   * Erstellt eine neue, noch nicht in der Datenbank
   * vorhandenes Einstellung-Objekt
   * 
   * @return das neue Einstellung-Objekt
   */
  public Einstellung erstelleNeu(Client client, Mitarbeiter mitarbeiter, String name);
  
  /**
   * Liefert die Einstellung für den
   * übergebenen Client und Mitarbeiter, deren Name
   * gleich dem übergebenen Namen ist. Soll eine Einstellung für alle Clients
   * oder alle Mitarbeiter gesucht werden, so ist <code>null</code> für das 
   * entsprechende Attribut zu verwenden. Wird die Einstellung nicht gefunden,
   * so wird zunächst nach dieser Einstellung ohne Mitarbeiter gesucht. Wird
   * auch dort nichts gefunden, wird auch der Client entfernt.
   * @param client
   * @param mitarbeiter
   * @param name
   * @return die gesuchte Einstelltung
   */
  public Einstellung getEinstellung(
    Client client, Mitarbeiter mitarbeiter, String name);


  /**
   * Liefert die Einstellung für den
   * übergebenen Client und Mitarbeiter, deren Name
   * gleich dem übergebenen Namen ist. Soll eine Einstellung für alle Clients
   * oder alle Mitarbeiter gesucht werden, so ist <code>null</code> für das 
   * entsprechende Attribut zu verwenden. Wird die Einstellung nicht gefunden,
   * so wird zunächst nach dieser Einstellung ohne Mitarbeiter gesucht. Wird
   * auch dort nichts gefunden, wird auch der Client entfernt. 
   * @param client
   * @param mitarbeiter
   * @param name
   * @param namespace 
   * @return die gesuchte Einstelltung
   */
  public Einstellung getEinstellung(Client client, Mitarbeiter mitarbeiter, 
    String namespace, String name);

  /**
   * Liefert die Einstellung für den
   * aktuellen Client und Mitarbeiter. Wird die Einstellung nicht gefunden,
   * so wird zunächst nach dieser Einstellung ohne Mitarbeiter gesucht. Wird
   * auch dort nichts gefunden, wird auch der Client entfernt. 
   * @param name
   * @param namespace 
   * @return die gesuchte Einstelltung
   */
  public Einstellung getClientMitarbeiterEinstellung(String namespace, String name);

  /**
   * Liefert die Einstellung für den
   * aktuellen Client. Wird die Einstellung nicht gefunden,
   * so wird zunächst nach dieser Einstellung für alle Clients gesucht.
   * @param name
   * @param namespace 
   * @return die gesuchte Einstelltung
   */
  public Einstellung getClientEinstellung(String namespace, String name);

  /**
   * Liefert die Einstellung für den
   * aktuellen Mitarbeiter. Wird die Einstellung nicht gefunden,
   * so wird zunächst nach dieser Einstellung für alle Mitarbeiter gesucht.     
   * @param name
   * @param namespace 
   * @return die gesuchte Einstelltung
   */
  public Einstellung getMitarbeiterEinstellung(String namespace, String name);

  /**
   * Liefert die Einstellung für alle Clients und Mitarbeiter, deren Name
   * gleich dem übergebenen Namen ist. Wird keine passende Einstellung 
   * gefunden, so wird eine neue erzeugt. 
   * @param name
   * @param namespace 
   * @return die gesuchte Einstelltung
   */
  public Einstellung getEinstellung(String namespace, String name);

  /**
   * Liefert eine Liste der Werte, die für die Einstellung mit dem
   * übergebenen Namen für verschiedene Clients oder Mitarbeiter
   * gespeichert sind.
   * @param einstellungsname
   * @return die Liste der Werte
   */
  public Liste<String> getBenutzeWerte(String einstellungsname);

  /**
   * Bestimmt, ob die übergebene Einstellung für Clients unterschiedlich
   * gesetzt werden kann. Das Ergebnis basiert nur auf in der Datenbank
   * gespeicherten Werten und ist daher nur eine Vermutung.
   * @param name
   * @return
   */
  public boolean istClientEinstellung(String name);
  
  /**
   * Bestimmt, ob die übergebene Einstellung für Mitarbeiter unterschiedlich
   * gesetzt werden kann. Das Ergebnis basiert nur auf in der Datenbank
   * gespeicherten Werten und ist daher nur eine Vermutung.
   * @param name
   * @return
   */
  public boolean istMitarbeiterEinstellung(String name);  
}