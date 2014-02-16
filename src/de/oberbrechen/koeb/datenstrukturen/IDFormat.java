package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.Datenbankzugriff;

/**
 * Diese Klasse repräsentiert eine Formatierung für Datenbankzugriff-Objekte. 
 * Die ID wird als Object-Format verwendet.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class IDFormat<T extends Datenbankzugriff> extends Format<T> {

  public String format(T o) {
    return Integer.toString(o.getId());
  }
}