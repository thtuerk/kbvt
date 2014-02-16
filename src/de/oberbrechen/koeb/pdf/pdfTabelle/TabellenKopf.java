package de.oberbrechen.koeb.pdf.pdfTabelle;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;

/**
 * Der TabellenKopf.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class TabellenKopf {

  private final BaseFont schrift = PdfDokumentEinstellungen.schriftFettKursiv;

  private TabellenModell tabellenModell;
  private int[] spaltenAusrichtung;
  private float maximalHoehe;
  private float skalierungVertikal;
  private float schriftGroesse;
  
  public TabellenKopf(TabellenModell tabellenModell, float schriftGroesse) {
    this.tabellenModell = tabellenModell;
    this.schriftGroesse = schriftGroesse;
    this.maximalHoehe = PdfDokumentEinstellungen.getInstance().getTabellenKopfMaximalHoehe();
    
    spaltenAusrichtung = new int[tabellenModell.getSpaltenAnzahl()];
    for (int i=0; i < spaltenAusrichtung.length; i++) {
      spaltenAusrichtung[i] = tabellenModell.getSpaltenAusrichtung(i+1);
    }
  }

  private float getHoehe() {
    float maxHoehe = schriftGroesse;
    for(int i=1; i <= tabellenModell.getSpaltenAnzahl(); i++) {
      if (spaltenAusrichtung[i-1] ==
          TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL) {
        float hoehe = schrift.getWidthPoint(tabellenModell.getSpaltenName(i),
          schriftGroesse);
        if (hoehe > maxHoehe) maxHoehe = hoehe;
      }
    }

    maxHoehe+=5;
    if (maximalHoehe < maxHoehe) return maximalHoehe;
    return maxHoehe;
  }
  
  /**
   * Liefert das TabellenModell des TabellenKopfs
   * @return das TabellenModell des TabellenKopfs
   */
  public TabellenModell getTabellenModell() {
    return tabellenModell;
  }
  
  /**
   * Setzt die Maximalhöhe, die der Seitenkopf einnehmen darf.
   * @param maxHoehe die maximale Höhe in Points
   */
  public void setMaximalHoehe(float maxHoehe) {
    this.maximalHoehe = maxHoehe;
  }

  /**
   * Liefert die Maximalhöhe, die der Seitenkopf einnehmen darf.
   * @return die Maximalhöhe, die der Seitenkopf einnehmen darf
   */
  public float getMaximalHoehe() {
    return this.maximalHoehe;
  }
  
  /**
   * Gibt den Spaltentitel für die übergebene Spalte
   * an der übergebenen y-Position im übergebenen PdfContentByte aus.
   *
   * @param spaltenNr die auszugebenende Spaltennummer
   * @param yPos die y-Position für die Ausgabe in Points
   */
  protected void schreibeSpalte(int spaltenNr, float yPos,
    PdfTemplate template) {

    String text = tabellenModell.getSpaltenName(spaltenNr);
    if (text == null) return;

    //einige Grundlegende Paramter bestimmen
    float linkerRand = tabellenModell.getSpaltenPositionLinks(spaltenNr);
    float rechterRand = linkerRand + tabellenModell.getBreite(spaltenNr);
    float breite = tabellenModell.getBreite(spaltenNr);
    float mitte = (rechterRand + linkerRand) / 2;

    int ausrichtung = spaltenAusrichtung[spaltenNr - 1];
    float textBreite = schrift.getWidthPoint(text, schriftGroesse);
    float skalierung = 1;
    if (((ausrichtung == TabellenModell.SPALTEN_AUSRICHTUNG_LINKS ||
          ausrichtung == TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS ||
          ausrichtung == TabellenModell.SPALTEN_AUSRICHTUNG_ZENTRIERT) &&
          (textBreite > breite)) ||
          ausrichtung == TabellenModell.SPALTEN_AUSRICHTUNG_BLOCKSATZ) {
       skalierung = breite / textBreite;
       textBreite *= skalierung;
    }
    if (ausrichtung == TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL) {
      skalierung = skalierungVertikal;
    }

    //Text ausgeben
    template.beginText();
    template.setFontAndSize(schrift, schriftGroesse);
    template.setHorizontalScaling(skalierung*100);

    switch (ausrichtung) {
      case TabellenModell.SPALTEN_AUSRICHTUNG_BLOCKSATZ:
      case TabellenModell.SPALTEN_AUSRICHTUNG_LINKS:
        template.setTextMatrix(linkerRand, yPos);
        template.showText(text);
        break;
      case TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS:
        template.setTextMatrix(rechterRand - textBreite, yPos);
        template.showText(text);
        break;
      case TabellenModell.SPALTEN_AUSRICHTUNG_ZENTRIERT:
        template.setTextMatrix(mitte - textBreite / 2, yPos);
        template.showText(text);
        break;
      case TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL:
        template.setTextMatrix(0, 1, -1, 0, mitte+3*schriftGroesse/8, yPos);
        template.showText(text);
        break;
    }
    template.endText();
  }

  /**
   * Liefert die Breite, die der SeitenKopfFuss optimalerweise für die
   * übergebene Spalte benötigt.
   *
   * @param spaltenNr die Nummer der Spalte, deren optimale Breite geliefert
   *   werden soll
   * @return die optimale Breite der Spalte
   */
  public float getBenoetigeSpaltenBreite(int spaltenNr) {
    if (spaltenNr < 1 || spaltenNr > spaltenAusrichtung.length)
      throw new IllegalArgumentException("Es existiert keine Spalte mit der "+
                                     "Nummer "+spaltenNr+"!");
    if (spaltenAusrichtung[spaltenNr - 1] ==
        TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL) return 10;

    String text = tabellenModell.getSpaltenName(spaltenNr);
    if (text == null) return 0;
    return schrift.getWidthPoint(text, schriftGroesse);
  }

  //Doku siehe bitte Interface
  public PdfTemplate getTemplate(float breite, PdfContentByte pdf) {
      
    float hoehe = this.getHoehe();
    PdfTemplate template = pdf.createTemplate(breite, hoehe);      

    //Vertikale Skalierung bestimmen
    skalierungVertikal = 1;
    for (int i=0; i < tabellenModell.getSpaltenAnzahl(); i++) {
      if (spaltenAusrichtung[i] == TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL){
        float textBreite=schrift.getWidthPoint(
          tabellenModell.getSpaltenName(i+1), schriftGroesse);
        if (textBreite*skalierungVertikal + 5 > maximalHoehe) {
          skalierungVertikal = (maximalHoehe-5) / textBreite;
        }
      }
    }

    //Beschriftungen
    for (int i=1; i <= tabellenModell.getSpaltenAnzahl(); i++) {
      schreibeSpalte(i, 5, template);
    }

    //zeichne Trennlinie
    template.moveTo(0, 0);
    template.lineTo(breite, 0);
    template.stroke();
    
    return template;
  }

  /**
   * Setzt die Ausrichtung der übergebenen Spalte. Die verfügbaren Ausrichtungen
   * sind als öffentliche Konstanten der Klasse SpaltenModell ansprechbar.
   *
   * @param spaltenNr die Nummer der Spalte, deren Ausrichtung gesetzt werden
   *   soll
   * @param int ausrichtung
   * @see SpaltenModell
   */
  public void setSpaltenAusrichtung(int spaltenNr, int ausrichtung) {
    if (spaltenNr < 1 || spaltenNr > spaltenAusrichtung.length)
      throw new IllegalArgumentException("Es existiert keine Spalte mit der "+
                                     "Nummer "+spaltenNr+"!");
    spaltenAusrichtung[spaltenNr - 1] = ausrichtung;
  }
}