package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;

import de.oberbrechen.koeb.datenbankzugriff.Einstellung;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Medien. Ein Medium
* ist eindeutig über seine Nummer identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Medium-Objekte statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class EinstellungenListe extends Liste<Einstellung> {

  /**
   * Einstellungen werden nach ihrem Namen alphabetisch sortiert
   */
  public static final int AlphabetischeSortierung = 2;


  protected Comparator<Einstellung> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
  
  public static Comparator<Einstellung> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Einstellung> sort;
    
    switch (sortierung) {
      case AlphabetischeSortierung :
        {
          sort = new Comparator<Einstellung>() {
            public int compare(Einstellung einstellungA, Einstellung einstellungB) {
              int erg =  nullCompare(einstellungA.getName(), einstellungB.getName());

              if (erg == 0) {
                if (einstellungA.getClient() == null
                  && einstellungB.getClient() != null)
                  erg = 1;
                if (einstellungA.getClient() != null
                  && einstellungB.getClient() == null)
                  erg = -1;
                if (einstellungA.getClient() != null
                  && einstellungB.getClient() != null) {
                  erg = nullCompare(
                      einstellungA.getClient().getName(),
                      einstellungB.getClient().getName());
                }
              }

              if (erg == 0) {
                if (einstellungA.getMitarbeiter() == null
                  && einstellungB.getMitarbeiter() != null)
                  erg = 1;
                if (einstellungA.getMitarbeiter() != null
                  && einstellungB.getMitarbeiter() == null)
                  erg = -1;
                if (einstellungA.getMitarbeiter() != null
                  && einstellungB.getMitarbeiter() != null) {
                  erg = nullCompare(
                      einstellungA.getMitarbeiter().getBenutzer().getName(),
                      einstellungB.getMitarbeiter().getBenutzer().getName());
                }
              }

              return erg;
            }
          };
          break;
        }

      default :
        throw new IllegalArgumentException(
          "Eine Sortierung mit der Nummer " + sortierung + " ist unbekannt!");
    }

    return sort;
  }
}
