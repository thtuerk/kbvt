package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Veranstaltungsgruppen.
* Jede Veranstaltungsgruppe wird nur einmal in die Liste aufgenommen.
* Es ist zu
* beachten, dass überall nur Veranstaltungsgruppen statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class VeranstaltungsgruppenListe extends Liste<Veranstaltungsgruppe> {

  /**
   * Veranstaltungsgruppen werden alphabetisch nach ihrem Namen sortiert
   */
  public static final int alphabetischeSortierung = 2;

  protected Comparator<Veranstaltungsgruppe> getComparatorFuerSortierung(int sortierung) {
    final Comparator<Veranstaltungsgruppe> sort;

    switch (sortierung) {
      //alphabetische Sortierung
      case alphabetischeSortierung : {
        sort = new Comparator<Veranstaltungsgruppe>() {
            public int compare(Veranstaltungsgruppe a, Veranstaltungsgruppe b) {
              return nullCompare(a.getName(),
                                 b.getName());
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
