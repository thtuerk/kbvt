package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Bemerkungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class BemerkungenListe extends Liste<Bemerkung> {

  /**
   * Clients werden alphabetisch nach ihrem Inhalt sortiert
   */
  public static final int BemerkungSortierung = 2;

  /**
   * Bemerkungen werden nach ihrem Datum sortiert
   */
  public static final int DatumSortierung = 3;

  protected Comparator<Bemerkung> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
  
  static Comparator<Bemerkung> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Bemerkung> sort;

    switch (sortierung) {
      case BemerkungSortierung : {
        sort = new Comparator<Bemerkung>() {
          public int compare(Bemerkung bemA, Bemerkung bemB) {
            int erg;
  
            erg = nullCompare(bemA.getBemerkung(), bemB.getBemerkung());
            if (erg == 0) erg = bemA.getId()-bemB.getId();
            return erg;
          }};
        break;
      }
  
      case DatumSortierung : {
        sort = new Comparator<Bemerkung>() {
          public int compare(Bemerkung bemA, Bemerkung bemB) {
            int erg = 0;
  
            if (bemA.getDatum() == null && bemB.getDatum() != null) return 1;
            if (bemA.getDatum() != null && bemB.getDatum() == null) return -1;
            if (bemA.getDatum() != null && bemB.getDatum() != null) erg = bemA.getDatum().compareTo(bemB.getDatum());
            if (erg == 0) erg = bemA.getId()-bemB.getId();
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
