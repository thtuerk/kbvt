package de.oberbrechen.koeb.ausgaben;

import java.util.Collection;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractBenutzerAusgabe implements BenutzerAusgabe{

  protected BenutzerListe daten = null;
  protected int datenSortierung = BenutzerListe.NachnameVornameSortierung;
  protected boolean datenUmgekehrteSortierung = false;
  protected String datenTitel = "Benutzerliste";
  
  public void setDaten(BenutzerListe daten) {
    this.daten = daten;
  }
  
  public void setDaten(Collection<? extends Benutzer> daten) {
    this.daten = new BenutzerListe();
    this.daten.addAllNoDuplicate(daten);
  }
  
  public void setTitel(String titel) {
    this.datenTitel = titel;
  }
    
  public void setSortierung(int sortierung, boolean umgekehrteSortierung) {
    this.datenSortierung = sortierung;
    this.datenUmgekehrteSortierung = umgekehrteSortierung;
  }
  
  /**
   * Liefert eine Kopie der Daten, sortiert nach der eingestellten Sortierung
   * @return die sortierten Daten
   */
  protected BenutzerListe getSortierteBenutzerListe() {
    BenutzerListe benutzerliste = new BenutzerListe();
    benutzerliste.addAllNoDuplicate(daten);
    benutzerliste.setSortierung(datenSortierung, datenUmgekehrteSortierung);
    return benutzerliste;
  }
}