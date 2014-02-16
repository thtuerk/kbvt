package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;

/**
 * Diese Klasse repräsentiert eine Formatierung, die angewendet werden kann.
 * Im Gegensatz zu java.text.Format geht es hier nur um das Umwandeln von
 * Objekten in Strings, Parsen wird nicht benutzt.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class Format<T> implements Comparator<T> {

  /**
   * Liefert eine Stringrepräsentation für das übergebene Objekt
   * @param o das Objekt
   * @return die Repräsentation
   */
  public String format(T o) {
    if (o == null) return null;
    return o.toString();
  }

  public int compare(T o1, T o2) {
    return format(o1).compareToIgnoreCase(format(o2));
  }
}