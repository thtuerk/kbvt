package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;
import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Mitarbeitern. 
* Ein Mitarbeiter
* ist eindeutig über seine Id identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Mitarbeiter statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MitarbeiterListe extends Liste<Mitarbeiter> {

  /**
   * Mitarbeiter werden alphabetisch zuerst nach ihrem Nachnamen und dann nach dem
   * Vornamen sortiert
   */
  public static final int NachnameVornameSortierung = 2;

  /**
   * Mitarbeiter werden alphabetisch zuerst nach ihrem Vornamen und dann nach dem
   * Nachnamen sortiert
   */
  public static final int VornameNachnameSortierung = 3;

  protected Comparator<Mitarbeiter> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
  
  public static Comparator<Mitarbeiter> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Mitarbeiter> sort;

    switch (sortierung) {
      //Nachname/Vorname Sortierung
      case NachnameVornameSortierung : {
        sort = new Comparator<Mitarbeiter>() {
          public int compare(Mitarbeiter a, Mitarbeiter b) {
            int erg;

            Benutzer benA = a.getBenutzer();
            Benutzer benB = b.getBenutzer();
            erg = nullCompare(benA.getNachname(), benB.getNachname());
            if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
            return erg;
          }};
        break;
      }

      //Vorname/Nachname Sortierung
      case VornameNachnameSortierung : {
        sort = new Comparator<Mitarbeiter>() {

          public int compare(Mitarbeiter a, Mitarbeiter b) {
            int erg;

            Benutzer benA = a.getBenutzer();
            Benutzer benB = b.getBenutzer();
            erg = nullCompare(benA.getVorname(), benB.getVorname());
            if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
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

  /**
   * Liefert eine Liste aller Benutzer, die als Mitarbeiter in der Liste
   * stehen.
   * @return die Liste der Benutzer
   */
  public BenutzerListe convertToBenutzerListe() {
    BenutzerListe result = new BenutzerListe();
    Iterator<Mitarbeiter> it = this.iterator();
    while (it.hasNext()) {
      result.add(it.next().getBenutzer());
    }
    return result;
  }
}
