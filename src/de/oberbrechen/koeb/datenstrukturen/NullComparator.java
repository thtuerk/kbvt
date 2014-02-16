package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;


/**
* Dieser Comparator ist ein Wrapper um andere Comparatoren, der Null-Werte behandelt.
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class NullComparator<T> implements Comparator<T> {  

  private Comparator<T> comp;
  
  public NullComparator(Comparator<T> comp) {
    this.comp = comp;
  }
  
  public int compare(T o1, T o2) {
    if ((o1 == null) && (o2 == null)) return 0;
    if (o1 == null) return -1;
    if (o2 == null) return 1;
    return comp.compare(o1,o2);
  }
}