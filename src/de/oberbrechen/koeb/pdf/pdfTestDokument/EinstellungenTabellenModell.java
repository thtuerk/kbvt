package de.oberbrechen.koeb.pdf.pdfTestDokument;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.EinstellungenListe;
import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * Diese Klasse ist ein Modell aktuellen Ausleihen eines Benutzers
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class EinstellungenTabellenModell extends TabellenModell {
  
  private EinstellungenListe einstellungen;
  
  public EinstellungenTabellenModell() throws Exception {
    einstellungen =
      Datenbank.getInstance().getEinstellungFactory().getAlleEinstellungen();
    einstellungen.setSortierung(EinstellungenListe.AlphabetischeSortierung);
  }

  public int getSpaltenAnzahl() {
    return 4;
  }

  public int getZeilenAnzahl() {
    return einstellungen.size();
  }

  public String getSpaltenName(int spaltenNr) {
    if (spaltenNr == 1) return "Name";
    if (spaltenNr == 2) return "Client";
    if (spaltenNr == 3) return "Mitarbeiter";
    if (spaltenNr == 4) return "Wert";
    return "unbekannte Spalte";
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    Einstellung aktuelleEinstellung = 
      (Einstellung) einstellungen.get(zeilenNr-1);
    
    switch (spaltenNr) {
      case 1:
        return aktuelleEinstellung.getName();
      case 2:
        Client client = aktuelleEinstellung.getClient();
        return client == null?"-":Integer.toString(client.getId());
      case 3:
        Mitarbeiter mitarbeiter = aktuelleEinstellung.getMitarbeiter();
        return mitarbeiter == null?"-":mitarbeiter.getBenutzer().getName();
      case 4:
        return aktuelleEinstellung.getWert();
    }

    return "Fehler";
  }    
}