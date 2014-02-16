package de.oberbrechen.koeb.datenstrukturen;

import java.util.Comparator;

import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.einstellungen.Buecherei;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Medien. Ein Medium
* ist eindeutig über seine Nummer identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Medium-Objekte statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MedienListe extends Liste<Medium> {

  /**
   * Medien werden alphabetisch zuerst nach ihrem Titel und dann nach dem
   * Autor sortiert
   */
  public static final int TitelAutorSortierung = 2;

  /**
   * Medien werden alphabetisch zuerst nach ihrem Autor und dann nach dem
   * Titel sortiert
   */
  public static final int AutorTitelSortierung = 3;

  /**
   * Medien werden nach ihrer Nummer sortiert.
   */
  public static final int MedienNummerSortierung = 4;

  /**
   * Medien werden nach ihrem Einstellungsdatum und dann nach ihrem
   * Titel sortiert.
   */
  public static final int EinstellungsdatumSortierung = 5;

  /**
   * Medien werden nach ihrer ISBN sortiert.
   */
  public static final int ISBNSortierung = 6;

  
  protected Comparator<Medium> getComparatorFuerSortierung(int sortierung) {
      return getStaticComparatorFuerSortierung(sortierung);
  }

  public static Comparator<Medium> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Medium> sort;
    final Comparator<String> medienNrSort = 
      Buecherei.getInstance().getMedienNrComparator();    

    switch (sortierung) {
      case TitelAutorSortierung : {
        sort = new Comparator<Medium>() {
            public int compare(Medium a, Medium b) {
              int erg = nullCompare(a.getTitel(), b.getTitel());                       
              if (erg == 0) erg = nullCompare(
                a.getAutor(), b.getAutor());
              if (erg == 0) erg = medienNrSort.compare(a.getMedienNr(), b.getMedienNr());
              return erg;
            }};
        break;
      }

      case EinstellungsdatumSortierung : {
        sort = new Comparator<Medium>() {
            public int compare(Medium mediumA, Medium mediumB) {
              
              int erg = 0;
              if (mediumA.getEinstellungsdatum() == null &&
                  mediumB.getEinstellungsdatum() != null) erg = -1;
              if (mediumA.getEinstellungsdatum() == null &&
                  mediumB.getEinstellungsdatum() == null) erg = 0;
              if (mediumA.getEinstellungsdatum() != null &&
                  mediumB.getEinstellungsdatum() == null) erg = 1;
              if (mediumA.getEinstellungsdatum() != null &&
                  mediumB.getEinstellungsdatum() != null) 
                erg = mediumA.getEinstellungsdatum().compareTo(
                    mediumB.getEinstellungsdatum());              
              if (erg == 0) erg = nullCompare(
                  mediumA.getTitel(), mediumB.getTitel());
              if (erg == 0) erg = nullCompare(mediumA.getAutor(), mediumB.getAutor());
              if (erg == 0) erg = medienNrSort.compare(mediumA.getMedienNr(), mediumB.getMedienNr());
              return erg;
            }};
        break;
      }

      case AutorTitelSortierung : {
        sort = new Comparator<Medium>() {
            public int compare(Medium a, Medium b) {
              int erg = nullCompare(a.getAutor(), b.getAutor());
              if (erg == 0) erg = nullCompare(a.getTitel(), b.getTitel());
              if (erg == 0) erg = medienNrSort.compare(a.getMedienNr(), b.getMedienNr());
              return erg;
            }};
        break;
      }

      case ISBNSortierung : {
        sort = new Comparator<Medium>() {
            public int compare(Medium a, Medium b) {
              ISBN isbnA = a.getISBN();
              ISBN isbnB = b.getISBN();
              String stringA = isbnA==null?null:isbnA.getISBN();
              String stringB = isbnB==null?null:isbnB.getISBN();
              int erg = nullCompare(stringA, stringB);
              if (erg == 0) erg = medienNrSort.compare(a.getMedienNr(), b.getMedienNr());
              return erg;
            }};
        break;
      }

      //Nummer Sortierung
      case MedienNummerSortierung : {
        sort = new Comparator<Medium>() {
          public int compare(Medium a, Medium b) {
            return medienNrSort.compare(a.getMedienNr(), b.getMedienNr());
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
