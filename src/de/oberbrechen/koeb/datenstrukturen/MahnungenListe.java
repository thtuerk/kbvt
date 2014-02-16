package de.oberbrechen.koeb.datenstrukturen;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.framework.ErrorHandler;

import java.util.Comparator;

/**
* Diese Klasse repräsentiert eine sortierte Liste von Mahnungen. Eine Mahnung
* ist eindeutig über ihren Benutzer identifiziert und wird nur einmal in
* die Liste aufgenommen. Es ist zu
* beachten, dass überall nur Mahnung-Objekte statt allgemeiner Objekte als
* Parameter übergeben werden dürfen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MahnungenListe extends Liste<Mahnung> {

  /**
   * Mahnungen werden alphabtisch nach dem Nachnamen und dann nach dem
   * Vornamen ihres Benutzers sortiert
   */
  public static final int BenutzerSortierung = 2;

  /**
   * Ausleihen werden nach ihrer maximalen Ueberziehdauer sortiert
   */
  public static final int UeberziehdauerSortierung = 3;

  /**
   * Mahnungen werden nach den Mahngebühren sortiert
   */
  public static final int MahngebuehrSortierung = 4;

  /**
   * Mahnungen werden nach der Anzahl der ueberzogenen Medien sortiert
   */
  public static final int MedienAnzahlSortierung = 5;

  protected Comparator<Mahnung> getComparatorFuerSortierung(int sortierung) {
    return getStaticComparatorFuerSortierung(sortierung);
  }

  public static Comparator<Mahnung> getStaticComparatorFuerSortierung(int sortierung) {
    final Comparator<Mahnung> sort;

    switch (sortierung) {
      case BenutzerSortierung : {
        sort = new Comparator<Mahnung>() {
            public int compare(Mahnung ma, Mahnung mb) {
              int erg = nullCompare(ma.getBenutzer().getNameFormal(),
                                    mb.getBenutzer().getNameFormal());
              if (erg == 0) erg = mb.getBenutzer().getId() -
                                  ma.getBenutzer().getId();
              return erg;
            }};
        break;
      }

      case UeberziehdauerSortierung: {
        sort = new Comparator<Mahnung>() {
            public int compare(Mahnung ma, Mahnung mb) {
              int erg = 0;
              try {
                erg = mb.getMaxUeberzogeneTage() - ma.getMaxUeberzogeneTage();
              } catch (DatenbankInkonsistenzException e) {
                ErrorHandler.getInstance().handleException(e, false);
                erg = 0;
              }
              if (erg == 0) erg = nullCompare(ma.getBenutzer().getNameFormal(),
                                              mb.getBenutzer().getNameFormal());
              if (erg == 0) erg = mb.getBenutzer().getId() -
                                  ma.getBenutzer().getId();
              return erg;
            }};
        break;
      }

      case MahngebuehrSortierung : {
        sort = new Comparator<Mahnung>() {
          public int compare(Mahnung ma, Mahnung mb) {
            int erg = 0;
            try {
              double differenz = mb.getMahngebuehren() - ma.getMahngebuehren(); 
              if (differenz < 0) {
                  erg = -1;
              } 
              if (differenz > 0) {
                erg = 1;
              }
            } catch (DatenbankInkonsistenzException e) {
              ErrorHandler.getInstance().handleException(e, false);
              erg = 0;
            }
            if (erg == 0) erg = nullCompare(ma.getBenutzer().getNameFormal(),
                                            mb.getBenutzer().getNameFormal());
            if (erg == 0) erg = mb.getBenutzer().getId() -
                                ma.getBenutzer().getId();
            return erg;
        }};
        break;
      }

      //Nummer Sortierung
      case MedienAnzahlSortierung : {
        sort = new Comparator<Mahnung>() {
          public int compare(Mahnung ma, Mahnung mb) {
            int erg = 0;
            
            try {
              erg = mb.getAnzahlGemahnteAusleihen() - ma.getAnzahlGemahnteAusleihen();
            } catch (DatenbankInkonsistenzException e) {
              ErrorHandler.getInstance().handleException(e, false);
              erg = 0;
            }
            if (erg == 0) erg = nullCompare(ma.getBenutzer().getNameFormal(),
                                            mb.getBenutzer().getNameFormal());
            if (erg == 0) erg = mb.getBenutzer().getId() -
                                ma.getBenutzer().getId();
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
