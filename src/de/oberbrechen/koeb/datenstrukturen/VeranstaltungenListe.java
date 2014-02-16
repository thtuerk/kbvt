package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Veranstaltungen.
* Jede Veranstaltung wird nur einmal in die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Veranstaltungen statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class VeranstaltungenListe extends Liste<Veranstaltung> {

  /**
   * Veranstaltungen werden alphabetisch nach ihrem Titel sortiert
   */
  public static final int AlphabetischeSortierung = 2;


  protected Comparator<Veranstaltung> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }

  public static Comparator<Veranstaltung> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Veranstaltung> sort;

    switch (sortierung) {
      //alphabetische Sortierung
      case AlphabetischeSortierung : {
        sort = new Comparator<Veranstaltung>() {
            public int compare(Veranstaltung a, Veranstaltung b) {
              return nullCompare(a.getTitel(),
                                 b.getTitel());
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
