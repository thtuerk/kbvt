package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Benutzern. Ein Benutzer
* ist eindeutig über seine Benutzernr identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Benutzer statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class BenutzerListe extends Liste<Benutzer> {

  /**
   * Benutzer werden alphabetisch zuerst nach ihrem Nachnamen und dann nach dem
   * Vornamen sortiert
   */
  public static final int NachnameVornameSortierung = 2;

  /**
   * Benutzer werden alphabetisch zuerst nach ihrem Vornamen und dann nach dem
   * Nachnamen sortiert
   */
  public static final int VornameNachnameSortierung = 3;

  /**
   * Benutzer werden nach der Nummer sortiert, neue Benutzer ohne Nummer werden
   * dabei an den Anfang der Liste sortiert
   */
  public static final int BenutzernrSortierung = 4;

  /**
   * Benutzer werden nach ihrer Klasse dann nach dem Nachnamen, Vornamen
   * sortiert.
   */
  public static final int KlasseNachnameVornameSortierung = 5;

  /**
   * Benutzer werden nach ihrem Anmeldedatum sortiert.
   */
  public static final int AnmeldedatumSortierung = 6;


  protected Comparator<Benutzer> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }
    
  protected Comparator<Benutzer> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Benutzer> sort;

    switch (sortierung) {
      //Nachname/Vorname Sortierung
      case NachnameVornameSortierung : {
        sort = new Comparator<Benutzer>() {
          public int compare(Benutzer benA, Benutzer benB) {
            int erg = nullCompare(benA.getNachname(), benB.getNachname());
            if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
            return erg;
          }};
        break;
      }

      //Vorname/Nachname Sortierung
      case VornameNachnameSortierung : {
        sort = new Comparator<Benutzer>() {
          public int compare(Benutzer benA, Benutzer benB) {
            int erg = nullCompare(benA.getVorname(), benB.getVorname());
            if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
            return erg;
          }};
        break;
      }

      //Nummer Sortierung
      case BenutzernrSortierung : {
        sort = new Comparator<Benutzer>() {
            public int compare(Benutzer benA, Benutzer benB) {
              return benA.getId() - benB.getId();
            }};
        break;
      }

      //Klasse Sortierung
      case KlasseNachnameVornameSortierung : {
        sort = new Comparator<Benutzer>() {
          public int compare(Benutzer benA, Benutzer benB) {
            int erg = nullCompare(benA.getKlasse(), benB.getKlasse());
            if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
            if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
            return erg;
          }};
        break;
      }

      //Anmeldedatum Sortierung
      case AnmeldedatumSortierung : {
        sort = new Comparator<Benutzer>() {

          public int compare(Benutzer benA, Benutzer benB) {
            int erg = benA.getAnmeldedatum().compareTo(benB.getAnmeldedatum());            
            if (erg == 0) erg = nullCompare(benA.getKlasse(), benB.getKlasse());            
            if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
            if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
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
