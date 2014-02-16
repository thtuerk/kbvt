package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Dieses Interfacerepräsentiert einen Medientyp der Bücherei. Es stellt die
 * Verbindung zur Datenbank her und Methoden, um auf Medientypen zuzugreifen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface Medientyp extends Datenbankzugriff {

  /**
   * Liefert die Bezeichnung des Medientyps
   * @return die Bezeichnung des Medientyps
   */
  public String getName();

  /**
   * Setzt die Bezeichnung des Medientyps
   * @param name die neue Bezeichnung des Medientyps
   */
  public void setName(String name);
  
  /**
   * Liefert den Plural des Medientypnames. D.h. für den
   * Medientyp 'Buch' wird beispielsweise 'Bücher' geliefert.
   * @return den Plural des Medientypnames.
   */
  public String getPlural();

  /**
   * Setzt den Plural dieses Medientyps
   * @param plural der neue Plural
   */
  public void setPlural(String plural);  
}