package de.oberbrechen.koeb.pdf.pdfMedienListe;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.*;
import de.oberbrechen.koeb.pdf.pdfTabelle.*;

class PdfMedienListeTabellenModell extends TabellenModell {

  private MedienListe daten;

  public PdfMedienListeTabellenModell(
    MedienListe daten, int sortierung) {
    this(daten, sortierung, false);
  }

  public PdfMedienListeTabellenModell(
    MedienListe daten, int sortierung, boolean umgekehrteSortierung) {
    daten.setSortierung(sortierung, umgekehrteSortierung);
    this.daten = daten;
  }


  public int getSpaltenAnzahl() {
    return 3;
  }

  public int getZeilenAnzahl() {
    return daten.size();
  }

  public String getSpaltenName(int spaltenNr) {
    switch (spaltenNr) {
      case 1: return "Nr.";
      case 2: return "Titel";
      case 3: return "Autor";
    }
    return null;
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    Medium medium = (Medium) daten.get(zeilenNr - 1);

    switch (spaltenNr) {
      case 1: return medium.getMedienNr();
      case 2: return medium.getTitel();
      case 3: return medium.getAutor();
    }
    return null;
  }
}
