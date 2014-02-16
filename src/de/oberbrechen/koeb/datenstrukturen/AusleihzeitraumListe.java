package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;

/**
* Diese Klasse repräsentiert eine sortierte Liste von 
* Ausleihzeiträumen. Es ist zu
* beachten, dass überall nur Ausleihzeiträume 
* statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class AusleihzeitraumListe extends Liste<Ausleihzeitraum> {

  /**
   * Ausleihzeiträume werden nach ihrem Beginn, dann nach ihrem Ende und
   * schließlich nach der Ausleihe sortiert. 
   * Autor ihres Mediums sortiert
   */
  public static final int beginnSortierung = 2;
  public static final int taetigungsdatumSortierung = 3;

  

  protected Comparator<Ausleihzeitraum> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }

  static Comparator<Ausleihzeitraum> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Ausleihzeitraum> sort;

    switch (sortierung) {
      
      case beginnSortierung : {
        sort = new Comparator<Ausleihzeitraum>() {
            public int compare(Ausleihzeitraum zeitraumA, Ausleihzeitraum zeitraumB) {
              if (zeitraumA.getBeginn() == null) return -1;
              if (zeitraumB.getBeginn() == null) return 1;
              int erg = zeitraumA.getBeginn().compareTo(zeitraumB.getBeginn());
              if (erg != 0) return erg;
              
              if (zeitraumA.getEnde() == null) return -1;
              if (zeitraumB.getEnde() == null) return 1;
              erg = zeitraumA.getEnde().compareTo(zeitraumB.getEnde());
              if (erg != 0) return erg;

              if (zeitraumA.getTaetigungsdatum() == null) return -1;
              if (zeitraumB.getTaetigungsdatum() == null) return 1;
              erg = zeitraumA.getTaetigungsdatum().compareTo(zeitraumB.getTaetigungsdatum());
              if (erg != 0) return erg;

              return zeitraumA.hashCode() - zeitraumB.hashCode();
            }};
        break;
      }
        
      case taetigungsdatumSortierung : {
        sort = new Comparator<Ausleihzeitraum>() {
            public int compare(Ausleihzeitraum zeitraumA, Ausleihzeitraum zeitraumB) {
              if (zeitraumA.getTaetigungsdatum() == null) return -1;
              if (zeitraumB.getTaetigungsdatum() == null) return 1;
              int erg = zeitraumA.getTaetigungsdatum().compareTo(zeitraumB.getTaetigungsdatum());
              if (erg != 0) return erg;

              if (zeitraumA.getBeginn() == null) return -1;
              if (zeitraumB.getBeginn() == null) return 1;
              erg = zeitraumA.getBeginn().compareTo(zeitraumB.getBeginn());
              if (erg != 0) return erg;
              
              if (zeitraumA.getEnde() == null) return -1;
              if (zeitraumB.getEnde() == null) return 1;
              erg = zeitraumA.getEnde().compareTo(zeitraumB.getEnde());
              if (erg != 0) return erg;
              
              return zeitraumA.hashCode() - zeitraumB.hashCode();
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
