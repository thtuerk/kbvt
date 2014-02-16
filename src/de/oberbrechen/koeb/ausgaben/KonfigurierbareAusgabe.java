package de.oberbrechen.koeb.ausgaben;

import javax.swing.JFrame;

/**
* Dieses Interface repräsentiert eine Ausgabe, die konfiguriert werden kann.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface KonfigurierbareAusgabe extends Ausgabe {
 
  /**
   * Konfiguriert die Ausgabe mittels einer graphischen Schnittstelle.
   * @param main das Fenster, das der Schnittstelle als Hauptfenster dient 
   */
  public void konfiguriere(JFrame main);  
}