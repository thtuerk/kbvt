package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;
import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.Termin;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Terminen.
* Jeder Termin wird nur einmal in die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Termine statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class TerminListe extends Liste<Termin> {

  /**
   * Termine werden wie Zeitraeume sortiert
   */
  public static final int chronologischeSortierung = ZeitraumListe.chronologischeSortierung;
  public static final int uhrzeitSortierung = ZeitraumListe.uhrzeitSortierung;

  protected Comparator<Termin> getComparatorFuerSortierung(int sortierung) {
    final Comparator<Zeitraum> comp = 
      ZeitraumListe.getStaticComparatorFuerSortierung(sortierung);
    
    return new Comparator<Termin>(){
      public int compare(Termin t1, Termin t2) {
        return comp.compare(t1, t2); 
      }};
  }
  
  
  /**
   * Legt eine Tiefenkopie der Liste, d.h. eine Liste mit den jeweiligen 
   * Kopien der Termine an.
   * @return
   */
  @SuppressWarnings("unchecked")
  public TerminListe deepCopy() {
    TerminListe copy = new TerminListe();
    
    Iterator<Termin> it = this.iterator();
    while (it.hasNext()) {
      copy.add((Termin) it.next().clone());
    }

    copy.setSortierung(this.comparator());
    return copy;
  }
}
