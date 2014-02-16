package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Ausleihen. Eine Ausleihe
* ist eindeutig über ihre Nummer identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Ausleihe-Objekte statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class AusleihenListe extends Liste<Ausleihe> {

  /**
   * Ausleihen werden alphabetisch zuerst nach ihrem Titel und dann nach dem
   * Autor ihres Mediums sortiert
   */
  public static final int MedienTitelSortierung = 2;

  /**
   * Ausleihen werden nach ihrem Ausleihdatum sortiert
   */
  public static final int AusleihdatumSortierung = 3;

  /**
   * Ausleihen werden nach ihrem Sollrückgabedatum sortiert
   */
  public static final int SollrueckgabeDatumSortierung = 4;

  /**
   * Ausleihen werden nach ihrer Nummer sortiert
   */
  public static final int AusleihenNrSortierung = 5;

  /**
   * Ausleihen werden alphabetisch nach der MedienNr sortiert
   */
  public static final int MedienNrSortierung = 6;

  /**
   * Ausleihen werden alphabetisch nach dem Vornamen, Nachnamen der Benutzer und dann
   * nach dem Medientitel sortiert
   */
  public static final int BenutzerVornameSortierung = 7;

  /**
   * Ausleihen werden alphabetisch nach dem Nachnamen, Vornamen der Benutzer und dann
   * nach dem Medientitel sortiert
   */
  public static final int BenutzerNachnameSortierung = 8;
  
  

  protected Comparator<Ausleihe> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }

  static Comparator<Ausleihe> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Ausleihe> sort;
  
    switch (sortierung) {
      case MedienTitelSortierung : {
        sort = new Comparator<Ausleihe>() {

            final Comparator<Medium> mediumComp = 
                new NullComparator<Medium>(
              MedienListe.getStaticComparatorFuerSortierung(
                MedienListe.TitelAutorSortierung));
          
            public int compare(Ausleihe a, Ausleihe b) {
              int erg = mediumComp.compare(a.getMedium(), b.getMedium());

              if (erg != 0) return erg;
              if (!(a.istZurueckgegeben())) return 1;
              if (!(b.istZurueckgegeben())) return -1;
              return a.getRueckgabedatum().compareTo(b.getRueckgabedatum());
            }};
        break;
      }

      case BenutzerNachnameSortierung : {
        sort = new Comparator<Ausleihe>() {
          
          final Comparator<Ausleihe> titelComp = getStaticComparatorFuerSortierung(MedienTitelSortierung);
          
          public int compare(Ausleihe a, Ausleihe b) {
            String benA = a.getBenutzer().getNameFormal();
            String benB = b.getBenutzer().getNameFormal();
                       
            int erg = nullCompare(benA, benB);
            if (erg != 0) return erg; 
            return titelComp.compare(a, b);
          }};
          break;
      }

      case BenutzerVornameSortierung : {
        sort = new Comparator<Ausleihe>() {
          
          Comparator<Ausleihe> titelComp = getStaticComparatorFuerSortierung(MedienTitelSortierung);
          
          public int compare(Ausleihe a, Ausleihe b) {
            String benA = a.getBenutzer().getName();
            String benB = b.getBenutzer().getName();
            
            int erg = nullCompare(benA, benB);
            if (erg != 0) return erg; 
            return titelComp.compare(a, b);
          }};
          break;
      }

      case MedienNrSortierung : {
        sort = new Comparator<Ausleihe>() {
          final Comparator<Medium> mediumComp = 
            new NullComparator<Medium>(
            MedienListe.getStaticComparatorFuerSortierung(
              MedienListe.MedienNummerSortierung));
        
          public int compare(Ausleihe a, Ausleihe b) {
            int erg = mediumComp.compare(a.getMedium(), b.getMedium());

            if (erg != 0) return erg;
            if (!(a.istZurueckgegeben())) return 1;
            if (!(b.istZurueckgegeben())) return -1;
            return a.getRueckgabedatum().compareTo(b.getRueckgabedatum());
          }};
        break;
      }

      case SollrueckgabeDatumSortierung : {
        sort = new Comparator<Ausleihe>() {
            public int compare(Ausleihe a, Ausleihe b) {
              int erg = a.getSollRueckgabedatum().compareTo(b.getSollRueckgabedatum());
              
              if (erg != 0) return erg;
              Comparator<Ausleihe> comp = getStaticComparatorFuerSortierung(MedienTitelSortierung);                
              return comp.compare(a, b);
            }};
        break;
      }

      case AusleihdatumSortierung : {
        sort = new Comparator<Ausleihe>() {
          public int compare(Ausleihe a, Ausleihe b) {
            int erg = b.getAusleihdatum().compareTo(a.getAusleihdatum());

            if (erg != 0) return erg;
            Comparator<Ausleihe> comp = getStaticComparatorFuerSortierung(MedienTitelSortierung);                
            return comp.compare(a, b);
        }};
        break;
      }

      //Nummer Sortierung
      case AusleihenNrSortierung : {
        sort = new Comparator<Ausleihe>() {
          public int compare(Ausleihe a, Ausleihe b) {
            return a.getId() - b.getId();
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
