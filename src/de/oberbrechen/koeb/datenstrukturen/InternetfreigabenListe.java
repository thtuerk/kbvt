package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;

import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Internetfreigaben. Eine 
* Internetfreigabe
* ist eindeutig über ihre Nummer identifiziert und wird nur einmal in
* die Liste aufgenommen. 
* 
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class InternetfreigabenListe extends Liste<Internetfreigabe> {

  /**
   * InternetFreigaben werden zuerst nach dem Datum, dann nach dem Client
   * und schließlich nach dem Benutzer sortiert.
   */
  public static final int DatumSortierung = 2;
  

  protected Comparator<Internetfreigabe> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
  
  public static Comparator<Internetfreigabe> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Internetfreigabe> sort;

    switch (sortierung) {
      case DatumSortierung : {
        sort = new Comparator<Internetfreigabe>() {
          public int compare(Internetfreigabe internetfreigabeA, Internetfreigabe internetfreigabeB) {
            int erg = internetfreigabeA.getStartZeitpunkt().compareTo(
                        internetfreigabeB.getStartZeitpunkt());
            if (erg != 0) return erg;
            erg = nullCompare(internetfreigabeA.getClient().getName(),
                              internetfreigabeB.getClient().getName());
            if (erg != 0) return erg;
            erg = nullCompare(internetfreigabeA.getBenutzer().getNameFormal(),
                              internetfreigabeB.getBenutzer().getNameFormal());
            if (erg != 0) return erg;
                           
            return internetfreigabeA.getId()-internetfreigabeB.getId();
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
