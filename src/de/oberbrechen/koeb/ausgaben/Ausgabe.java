package de.oberbrechen.koeb.ausgaben;

import java.io.File;

import javax.swing.JFrame;

/**
* Dieses Interface repräsentiert eine Ausgabe, d.h. eine Statistik, Teilnehmerliste,
* Mahnung oder eine andere Ausgabe des Systems.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public interface Ausgabe {

  /**
   * Liefert den Namen der Ausgabe
   * @return den Namen der Ausgabe
   */
  public String getName();
  

  /**
   * Liefert eine Kurzbeschreibung der Ausgabe
   * @return eine Kurzbeschreibung der Ausgabe
   */
  public String getBeschreibung();

  /**
   * Führt die eigentliche Ausgabe aus. Was hier tatsächlich
   * passiert, kann sich stark unterscheiden. Es kann eine
   * Berechnung durchgeführt und das Ergebniss ohne Interaktion
   * mit dem Benutzer in eine Datei geschrieben werden. Es ist
   * aber genauso möglich vorher Eingaben vom Benutzer zu fordern,
   * oder das Ergebnis auf dem Bildschirm dazustellen. Dazu dient
   * der übergebene JFrame. Er kann als Parent für eventuell
   * von der Ausleihe generierten Dialogen dienen. 
   * Steht keine graphische Schnittstelle
   * zur Verfügung, wird als hauptFenster der Wert <code>null</code>
   * übergeben. Eine Ausleihe darf beliebige Exceptions werfen. Das
   * aufrufende Programm ist dafür verantwortlich, diese
   * sinnvoll zu verarbeiten.  
   * 
   * @param hauptFenster ein JFrame, der für graphische Aus- und Eingaben
   *   benutzt werden kann, <code>null</code>, falls keine graphische
   *   Schnittstelle verfügbar ist
   * @param zeigeDialog bestimmt, ob graphische Aus- und Eingaben
   *   benutzt werden sollen. Diese Option gibt lediglich den Benutzerwunsch an. Sie ist nicht zwingend, d.\,h. 
   *    Ausgaben dürfen trotz diese Option Dialoge anzeigen oder es nicht tun
   */
  public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception;
  
  /**
   * Liefert, ob das Ergebnis der Ausgabe in einer Datei gespeichert werden kann.
   * @return <code>true</code> gdw. das Ergebnis der Ausgabe in einer Datei gespeichert werden kann
   */
  public boolean istSpeicherbar();
  
  /**
   * Liefert, ob die Ausgabe dringend eine GUI für z.B. eine Konfigurationsdialog benötigt.
   */
  public boolean benoetigtGUI();

  /**
   * Speichert die Ausgabe in der übergebenen Datei 
   */
  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception;
  
  /**
   * Liefert die Standarderweiterung für das Speichern in eine Datei 
   */
  public String getStandardErweiterung();
  
}