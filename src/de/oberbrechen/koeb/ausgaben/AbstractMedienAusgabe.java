package de.oberbrechen.koeb.ausgaben;

import java.util.Collection;

import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractMedienAusgabe implements MedienAusgabe{

  protected MedienListe daten = null;
  protected int datenSortierung = MedienListe.TitelAutorSortierung;
  protected boolean datenUmgekehrteSortierung = false;
  protected String datenTitel = "Medienliste";
  
  public void setDaten(MedienListe daten) {
    this.daten = daten;
  }
  
  public void setDaten(Collection<? extends Medium> daten) {
    this.daten = new MedienListe();
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
  protected MedienListe getSortierteMedienliste() {
    MedienListe medienliste = new MedienListe();
    medienliste.addAllNoDuplicate(daten);
    medienliste.setSortierung(datenSortierung, datenUmgekehrteSortierung);
    return medienliste;
  }
}