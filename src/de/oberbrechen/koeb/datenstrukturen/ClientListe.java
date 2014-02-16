package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Clients. Ein Client
* ist eindeutig über seine Nr identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Clients statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class ClientListe extends Liste<Client> {

  /**
   * Clients werden alphabetisch nach ihrem Namen sortiert
   */
  public static final int NameSortierung = 2;


  protected Comparator<Client> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
  
  public static Comparator<Client> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Client> sort;

    switch (sortierung) {
      case NameSortierung : {
        sort = new Comparator<Client>() {
          public int compare(Client clientA, Client clientB) {
            int erg = nullCompare(clientA.getName(), clientB.getName());
            if (erg == 0) erg = clientA.getId()-clientB.getId();
            return erg;
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
