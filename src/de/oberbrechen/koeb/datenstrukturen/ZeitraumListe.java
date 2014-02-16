package de.oberbrechen.koeb.datenstrukturen;

import java.util.Calendar;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Zeiträumen.
* Jeder Zeitraum wird nur einmal in die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Termine statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class ZeitraumListe extends Liste<Zeitraum> {

  /**
   * Termine werde chronologisch sortiert
   */
  public static final int chronologischeSortierung = 2;
  public static final int uhrzeitSortierung = 3;

  protected Comparator<Zeitraum> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
  
  public static Comparator<Zeitraum> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Zeitraum> sort;

    switch (sortierung) {
      case chronologischeSortierung : {
        sort = new Comparator<Zeitraum>() {
            public int compare(Zeitraum a, Zeitraum b) {
              if (a.getBeginn() == null) return -1;
              if (b.getBeginn() == null) return 1;
              int erg = a.getBeginn().compareTo(b.getBeginn());
              if (a.getEnde() == null) return -1;
              if (b.getEnde() == null) return 1;
              if (erg == 0) 
                erg = a.getEnde().compareTo(b.getEnde());
              return erg;
            }};
        break;
      }

      case uhrzeitSortierung : {
        sort = new Comparator<Zeitraum>() {
            public int compare(Zeitraum a, Zeitraum b) {
              if ((a.getBeginn() == null) && (b.getBeginn() == null)) return 0;
              if (a.getBeginn() == null) return -1;
              if (b.getBeginn() == null) return 1;

              Calendar zeitraumA = Calendar.getInstance();
              Calendar zeitraumB = Calendar.getInstance();
              zeitraumA.setTime(a.getBeginn());
              zeitraumB.setTime(b.getBeginn());

              int erg = zeitraumA.get(Calendar.HOUR_OF_DAY) - 
                        zeitraumB.get(Calendar.HOUR_OF_DAY);
              if (erg != 0) return erg;
              erg = zeitraumA.get(Calendar.MINUTE) - 
                    zeitraumB.get(Calendar.MINUTE);
              if (erg != 0) return erg;


              
              if (((Zeitraum) a).getEnde() == null && ((Zeitraum) b).getEnde() == null) return 0;
              if (((Zeitraum) a).getEnde() == null) return -1;
              if (((Zeitraum) b).getEnde() == null) return 1;

              zeitraumA.setTime(((Zeitraum) a).getEnde());
              zeitraumB.setTime(((Zeitraum) b).getEnde());

              erg = zeitraumA.get(Calendar.HOUR_OF_DAY) - 
                    zeitraumB.get(Calendar.HOUR_OF_DAY);
              if (erg != 0) return erg;
              erg = zeitraumA.get(Calendar.MINUTE) - 
                    zeitraumB.get(Calendar.MINUTE);
              
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
