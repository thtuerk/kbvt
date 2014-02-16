package de.oberbrechen.koeb.gui.ausleihe.mahnungenReiter;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.MahnungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Mahnungen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MahnungenTableModel extends AbstractListeSortierbaresTableModel<Mahnung> {

  /**
   * Erstellt ein neues Modell
   */
  public MahnungenTableModel() {
    initDaten(new MahnungenListe());
  }

  public int getColumnCount() {
    return 6;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Benutzer";
    if (columnIndex == 1) return "Tel.-Nr.";
    if (columnIndex == 2) return "eMail";
    if (columnIndex == 3) return "Anzahl";
    if (columnIndex == 4) return "max. Überziehung";
    if (columnIndex == 5) return "Kosten";
    return "nicht definierte Spalte";
  }

  public int getDefaultColumnWidth(int columnIndex) {
    if (columnIndex == 0) return 200;
    if (columnIndex == 1) return 200;
    if (columnIndex == 2) return 200;
    if (columnIndex == 3) return 100;
    if (columnIndex == 4) return 100;
    if (columnIndex == 5) return 100;
    return 100;
  }
  
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    try {
      Mahnung gewaehlteMahnung = (Mahnung) daten.get(rowIndex);

      switch (columnIndex) {
        case 0:
          return gewaehlteMahnung.getBenutzer().getNameFormal();
        case 1:
          return gewaehlteMahnung.getBenutzer().getTel();
        case 2:
          return gewaehlteMahnung.getBenutzer().getEMail();
        case 3:
          return Integer.toString(gewaehlteMahnung.getAnzahlGemahnteAusleihen());
        case 4:
          int ueberzogeneTage = gewaehlteMahnung.getMaxUeberzogeneTage();
          int wertZumRunden = (ueberzogeneTage+1)*2/7;
          double erg = (double) wertZumRunden / 2;
          return erg+" W.";
        case 5:
          return formatKosten(gewaehlteMahnung.getMahngebuehren());
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, false);
      return "FEHLER!";
    }

        
    return "nicht definierte Spalte";
  }


  /**
   * Läd erneut die aktuellen Mahnungen
   */
  public void reload() {
    MahnungFactory mahnungFactory =
      Datenbank.getInstance().getMahnungFactory();
    BenutzerListe benutzer = mahnungFactory.getAlleBenutzerMitMahnung();

    daten.clear();
    for (int i=0; i < benutzer.size(); i++) {
      daten.add(mahnungFactory.erstelleMahnungFuerBenutzer(
        (Benutzer) benutzer.get(i)));
    }

    fireTableDataChanged();
  }

  public int getAlignment(int rowIndex, int columnIndex) {
    if (columnIndex < 3) {
      return AUSRICHTUNG_LINKS;
    } else {
      return AUSRICHTUNG_RECHTS;
    }
  }

  protected void sortiereNachSpalteIntern(int spalte, boolean umgekehrteSortierung) {
    switch (spalte) {
      case 0: 
        daten.setSortierung(MahnungenListe.BenutzerSortierung, umgekehrteSortierung);
        return;
      case 3: 
        daten.setSortierung(MahnungenListe.MedienAnzahlSortierung, umgekehrteSortierung);
        return;
      case 4: 
        daten.setSortierung(MahnungenListe.UeberziehdauerSortierung, umgekehrteSortierung);
        return;
      case 5: 
        daten.setSortierung(MahnungenListe.MahngebuehrSortierung, umgekehrteSortierung);
        return;        
    }
  }

  public boolean istSortierbarNachSpalte(int spalte) {
    return (spalte == 0 || spalte == 3 || spalte == 4 || spalte == 5);
  }

  public void sortiereNachStandardSortierung() {
    sortiereNachSpalte(0, false);        
  }
}
