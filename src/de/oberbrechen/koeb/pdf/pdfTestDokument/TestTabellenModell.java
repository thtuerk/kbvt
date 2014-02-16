package de.oberbrechen.koeb.pdf.pdfTestDokument;

import de.oberbrechen.koeb.pdf.pdfTabelle.TabellenModell;

/**
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class TestTabellenModell extends TabellenModell {
  
  int zeilenAnzahl;
  int spaltenAnzahl;
  
  public TestTabellenModell(int zeilenAnzahl, int spaltenAnzahl) throws Exception {
    super(spaltenAnzahl, true);
    this.zeilenAnzahl = zeilenAnzahl;
    this.spaltenAnzahl = spaltenAnzahl;
  }

  public int getSpaltenAnzahl() {
    return spaltenAnzahl;
  }

  public int getZeilenAnzahl() {
    return zeilenAnzahl;
  }

  public String getSpaltenName(int spaltenNr) {
    return "Spalte "+spaltenNr;
  }

  public String getEintrag(int spaltenNr, int zeilenNr) {
    return "Zelle "+zeilenNr+"-"+spaltenNr;
  }    

  public boolean getZeigeSpaltenHintergrund(int spaltenNr) {
    return spaltenNr % 2 == 1;
  }

}