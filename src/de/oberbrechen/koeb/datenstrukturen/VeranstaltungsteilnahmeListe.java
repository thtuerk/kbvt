package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Benutzern. Ein Benutzer
* ist eindeutig über seine Benutzernr identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Benutzer statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class VeranstaltungsteilnahmeListe extends Liste<Veranstaltungsteilnahme> {

  /**
   * Teilnahmen werden alphabetisch zuerst nach ihrem Nachnamen, dann
   * nach dem Vornamen des Benutzers und schließlich nach dem Titel der
   * Veranstaltung sortiert
   */
  public static final int BenutzerNachnameVornameSortierung = 2;

  /**
   * Teilnahmen werden alphabetisch zuerst nach ihrem Vornamen, dann nach dem
   * Nachnamen des Benutzers und schließlich nach dem Titel der
   * Veranstaltungsortiert
   */
  public static final int BenutzerVornameNachnameSortierung = 3;

  /**
   * Teilnahmen werden alphabetisch zuerst nach der Klasse, dann nach dem
   * Nachnamen, Vornamen des Benutzers und schließlich nach dem Titel der
   * Veranstaltung sortiert
   */
  public static final int BenutzerKlasseSortierung = 4;

  /**
   * Teilnahmen werden alphabetisch zuerst nach dem Titel der Veranstaltung,
   * dann nach der Anmeldenr sortiert.
   */
  public static final int AnmeldeDatumSortierung = 5;

  /**
   * Teilnahmen werden alphabetisch zuerst nach dem Titel der Veranstaltung,
   * dann nach der Bemerkung sortiert.
   */
  public static final int BemerkungenSortierung = 6;


  private final static Comparator<Veranstaltungsteilnahme> initComparator = new Comparator<Veranstaltungsteilnahme>() {
    public int compare(Veranstaltungsteilnahme verA, Veranstaltungsteilnahme verB) {
      int erg;
      erg = nullCompare(verA.getVeranstaltung().getTitel(), verB.getVeranstaltung().getTitel());
      if (erg != 0) return erg;
      
      boolean warteListeA = verA.istAufWarteListe();
      boolean warteListeB = verB.istAufWarteListe();
      
      if (warteListeA && !warteListeB) return 1;
      if (!warteListeA && warteListeB) return -1;
      
      return 0;
    }
  };
  

  protected Comparator<Veranstaltungsteilnahme> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }

  public static Comparator<Veranstaltungsteilnahme> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Veranstaltungsteilnahme> sort;

    switch (sortierung) {
      //Nachname/Vorname Sortierung
      case BenutzerNachnameVornameSortierung : {
        sort = new Comparator<Veranstaltungsteilnahme>() {
            public int compare(Veranstaltungsteilnahme a, Veranstaltungsteilnahme b) {
              int erg = initComparator.compare(a, b);
              if (erg != 0) return erg;
                
              Benutzer benA = a.getBenutzer();
              Benutzer benB = b.getBenutzer();              
              erg = nullCompare(benA.getNachname(), benB.getNachname());                            
              
              if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
              if (erg != 0) return erg;

              Veranstaltung verA = a.getVeranstaltung();
              Veranstaltung verB = b.getVeranstaltung();
              return nullCompare(verA.getTitel(), verB.getTitel());
            }};
        break;
      }

      //Vorname/Nachname Sortierung
      case BenutzerVornameNachnameSortierung : {
        sort = new Comparator<Veranstaltungsteilnahme>() {
            public int compare(Veranstaltungsteilnahme a, Veranstaltungsteilnahme b) {
              int erg = initComparator.compare(a, b);
              if (erg != 0) return erg;

              Benutzer benA = a.getBenutzer();
              Benutzer benB = b.getBenutzer();

              erg = nullCompare(benA.getVorname(), benB.getVorname());
              if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
              if (erg != 0) return erg;

              Veranstaltung verA = a.getVeranstaltung();
              Veranstaltung verB = b.getVeranstaltung();
              return nullCompare(verA.getTitel(), verB.getTitel());
            }};
        break;
      }

      //Sortierung nach der Klasse Sortierung
      case BenutzerKlasseSortierung : {
        sort = new Comparator<Veranstaltungsteilnahme>() {
          public int compare(Veranstaltungsteilnahme a, Veranstaltungsteilnahme b) {
            int erg = initComparator.compare(a, b);
            if (erg != 0) return erg;

            Benutzer benA = a.getBenutzer();
            Benutzer benB = b.getBenutzer();

            erg = nullCompare(benA.getKlasse(), benB.getKlasse());
            if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
            if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
            if (erg != 0) return erg;

            Veranstaltung verA = a.getVeranstaltung();
            Veranstaltung verB = b.getVeranstaltung();
            return nullCompare(verA.getTitel(), verB.getTitel());
          }};
        break;
      }

      //Sortierung nach der AnmeldeNr
      case AnmeldeDatumSortierung : {
        sort = new Comparator<Veranstaltungsteilnahme>() {

          public int compare(Veranstaltungsteilnahme a, Veranstaltungsteilnahme b) {
            int erg = initComparator.compare(a, b);
            if (erg != 0) return erg;

            return a.getAnmeldeDatum().before(b.getAnmeldeDatum())?-1:1;
          }};
        break;
      }

      //Sortierung nach der Bemerkung
      case BemerkungenSortierung : {
        sort = new Comparator<Veranstaltungsteilnahme>() {
          public int compare(Veranstaltungsteilnahme a, Veranstaltungsteilnahme b) {
            int erg = initComparator.compare(a, b);
            if (erg != 0) return erg;

            String bemA = a.getBemerkungen();
            String bemB = b.getBemerkungen();
            erg = nullCompare(bemA, bemB);

            Benutzer benA = a.getBenutzer();
            Benutzer benB = b.getBenutzer();

            if (erg == 0) erg = nullCompare(benA.getNachname(), benB.getNachname());
            if (erg == 0) erg = nullCompare(benA.getVorname(), benB.getVorname());
            if (erg != 0) return erg;

            Veranstaltung verA = a.getVeranstaltung();
            Veranstaltung verB = b.getVeranstaltung();
            return nullCompare(verA.getTitel(), verB.getTitel());
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
