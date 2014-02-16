package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Systematiken.
* Jede Systematik wird nur einmal in die Liste aufgenommen.
* Es ist zu
* beachten, dass überall nur Systematiken statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class SystematikListe extends Liste<Systematik> {

  /**
   * Systematiken werden alphabetisch nach ihrem Namen sortiert
   */
  public static final int alphabetischeSortierung = 2;

  

  protected Comparator<Systematik> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }

  public static Comparator<Systematik> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Systematik> sort;

    switch (sortierung) {
      //alphabetische Sortierung
      case alphabetischeSortierung : {
        sort = new Comparator<Systematik>() {
            public int compare(Systematik a, Systematik b) {
              return nullCompare(a.getName(), b.getName());
            }};
        break;
      }

      default:
        throw new IllegalArgumentException("Eine Sortierung mit der Nummer "+
          sortierung + " ist unbekannt!");
    }

    return sort;
  }
}
