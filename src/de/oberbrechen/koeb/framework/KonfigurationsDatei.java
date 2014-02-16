package de.oberbrechen.koeb.framework;

import java.util.Properties;
import java.util.Enumeration;
import java.io.*;

/**
 * Diese Klasse repräsentiert eine Konfigurationsdatei.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class KonfigurationsDatei {

  private Properties properties;
  private File file;

  private static File standardDatei = new java.io.File("einstellungen.conf");
  private static KonfigurationsDatei instance;

  /**
   * Setzt die Konfigurationsdatei
   * @param datei die neuen Standard-Konfigurationsdatei
   */
  public static void setStandardDatei(File datei) {
    standardDatei = datei;
    instance = null;
  }

  /**
   * Setzt die Konfigurationsdatei
   * @param dateiname der Name der neuen Standard-Konfigurationsdatei
   */
  public static void setStandardDatei(String dateiName) {
    setStandardDatei(new java.io.File(dateiName));
  }

  /**
   * Liefert die Standardkonfigurationsdatei, die vorher mittels
   * setStandardDatei(File datei) eingestellt wurde.
   *
   * @return die Standardkonfigurationsdatei
   */
  public static KonfigurationsDatei getStandardKonfigurationsdatei() {
    if (standardDatei == null)
      throw new NullPointerException("Die Standardkonfigurationsdatei "+
        "wurde nicht initialisiert! Vor Verwendung von "+
        "getStandardKonfigurationsdatei() muss diese mittels "+
        "setStandandardDateiname(String dateiname) gesetzt werden!");
    if (instance == null) instance = new KonfigurationsDatei(standardDatei);

    return instance;
  }

  /**
   * Erstellt ein neues KonfigurationsDatei-Objekt, das seine Informationen
   * in der übergebenen Datei speichert. Die zur Zeit in dieser Datei
   * gespeicherten Informationen werden geladen.
   *
   * @param datei die Datei, in der die Informationen gespeichert werden
   */
  public KonfigurationsDatei(File datei) {
    this.file = datei;
    properties = new Properties();
    load();
  }

  /**
   * Erstellt ein neues KonfigurationsDatei-Objekt, das seine Informationen
   * in der übergebenen Datei speichert. Die zur Zeit in dieser Datei
   * gespeicherten Informationen werden geladen.
   *
   * @param dateiName die Datei, in der die Informationen gespeichert werden
   */
  public KonfigurationsDatei(String dateiName) {
    this(new java.io.File(dateiName));
  }


  /**
   * Läd alle Informationen aus der Datei.
   */
  private void load() {
    try {
      FileInputStream input = new FileInputStream(file);
      properties.load(input);
    } catch (FileNotFoundException e) {
      // nichts zu tun ...
    } catch (IOException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Konfigurationsdatei "+file, true);
    }
  }

  /**
   * Speichert alle Informationen in die Datei   */
  private void save() {
    try {
      FileOutputStream output = new FileOutputStream(file);
      properties.store(output, null);
    } catch (IOException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +        "der Konfigurationsdatei "+file, true);
    }
  }

  /**
   * Liefert die Namen aller gesetzen Properties.
   * @return die Namen aller gesetzen Properties.
   */
  public Enumeration<?> propertyNames() {
    return properties.propertyNames();
  }

  /**
   * Setzt die übergebene Einstellung auf den angegebenen Wert und speichert
   * die Änderungen direkt in der Datei.
   *
   * @param einstellung der Name der zu setzenden Einstellung
   * @param wert der zu setzende Wert
   */
  public void setProperty(String einstellung, String wert) {
    properties.setProperty(einstellung, wert);
    this.save();
  }

  /**
   * Liefert den Wert der übergebenen Einstellung. Ist die Einstellung nicht in
   * der Konfigurationsdatei, so wird diese Einstellung mit '-' initalisiert.
   *
   * @param einstellung der Name der Einstellung
   * @return den Wert der Einstellung
   */
  public String getProperty(String einstellung) {
    return getProperty(einstellung, "-");
  }


  /**
   * Liefert den Wert der übergebenen Einstellung. Ist die Einstellung nicht in
   * der Konfigurationsdatei, so wird diese Einstellung dem übergebenen
   * Default-Wert initalisiert.
   *
   * @param einstellung der Name der Einstellung
   * @param standard der Default-Wert
   * @return den Wert der Einstellung
   */
  public String getProperty(String einstellung, String standard) {
    String ret = properties.getProperty(einstellung);

    if (ret == null) {
      ErrorHandler.getInstance().handleError("Fehler beim Laden der " +        "Einstellung '" + einstellung +"'! Der Standardwert '"+standard+"' "+        "wird gesetzt!", false);
      this.setProperty(einstellung, standard);
      return standard;
    }

    return ret;
  }
}
