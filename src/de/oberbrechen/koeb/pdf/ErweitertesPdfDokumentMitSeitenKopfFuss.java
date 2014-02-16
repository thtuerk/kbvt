package de.oberbrechen.koeb.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

/**
 * Diese Klasse dient dazu eine Tabelle einfach als PDF-Datei auszugeben. Die
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class ErweitertesPdfDokumentMitSeitenKopfFuss
  extends ErweitertesPdfDokument {

  protected SeitenKopfFuss seitenKopfErsteSeite;
  protected SeitenKopfFuss seitenKopf;
  protected SeitenKopfFuss seitenFuss;

  //Anpassungen der Position des Seitenkopfes; um diese Werte wird der
  //Seitenkopf nach Unten verschoben
  protected float seitenKopfPosition = 0;
  protected float seitenKopfPositionErsteSeite = 0;

  
  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public ErweitertesPdfDokumentMitSeitenKopfFuss() {
    super();
  }
  
  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public ErweitertesPdfDokumentMitSeitenKopfFuss(boolean querFormat) {
    super(querFormat);
  }

  /**
   * Erstellt ein neues PdfDokument in der übergebenen Papiergröße.
   */  
  public ErweitertesPdfDokumentMitSeitenKopfFuss(Rectangle pageSize,
      boolean querFormat) {
    
    super(pageSize, querFormat);
  }
  
  /**
   * Liefert den unteren Seitenrand in Points, wobei die Höhe des Seitenfußes
   * zum normalen Rand hinzugerechnet wird
   *
   * @param die Nummer der Seite
   * @return den unteren Seitenrand in Points unter Berücksichtigung des
   *   Seitenfußes
   * @throws Exception
   */
  public float getSeitenRandUntenMitFuss(int seitenNr) throws Exception {
    if (seitenFuss == null) return randUnten;
    return randUnten + seitenFuss.getHoehe(seitenNr);
  }
  
  /**
   * Liefert die Seitenhöhe ohne die Ränder und ohne den Seitenkopf und -fuß
   * @return die Seitenhöhe ohne die Ränder und ohne den Seitenkopf und -fuß
   */
  public float getVerfuegbareSeitenHoeheMitKopfFuss(int seitenNr) throws Exception {
    return getSeitenHoehe()-getSeitenRandObenMitKopf(seitenNr)-
      getSeitenRandUntenMitFuss(seitenNr);
  }
  

  /**
   * Liefert den oberen Seitenrand in Points, wobei die Höhe des Seitenkopfes
   * zum normalen Rand hinzugerechnet wird
   * @param die Nummer der Seite
   * @return den oberen Seitenrand in Points unter Berücksichtigung des
   *   Seitenkopfes
   * @throws Exception
   */
  public float getSeitenRandObenMitKopf(int seitenNr) throws Exception {
    if (seitenNr == 1) {
      if (seitenKopfErsteSeite != null)
        return randOben + seitenKopfErsteSeite.getHoehe(1);
    } else {
      if (seitenKopf != null) return randOben + seitenKopf.getHoehe(seitenNr);
    }
    return randOben;
  }

  /**
   * Finalisiert alle seit Seitenköpfe / -füße 
   * mit der übergebenen Gesamtseitenanzahl.
   * @param gesamtseitenAnzahl
   */
  public void finalisiere(int gesamtseitenAnzahl) {  
    if (seitenKopf != null) seitenKopf.finalisiere(gesamtseitenAnzahl);
    if (seitenKopfErsteSeite != null) 
      seitenKopfErsteSeite.finalisiere(gesamtseitenAnzahl);
    if (seitenFuss != null) seitenFuss.finalisiere(gesamtseitenAnzahl);
  }
  
  /**
   * Schreibt den Seitenkopf und -fuß auf die aktuelle Seite
   * @throws Exception
   */
  protected void schreibeSeitenKopfFuss(int seitenNr, PdfContentByte pdf) throws Exception {
    PdfTemplate template;
    if (seitenNr == 1 && seitenKopfErsteSeite != null) {
      template = seitenKopfErsteSeite.getSeitenKopfFuss(this, pdf, seitenNr);
      pdf.addTemplate(template, 0, this.getSeitenHoehe()-
        getSeitenRandObenMitKopf(1)-seitenKopfPositionErsteSeite);
    } else if (seitenKopf != null) {
      template = seitenKopf.getSeitenKopfFuss(this, pdf, seitenNr);
      pdf.addTemplate(template, 0, this.getSeitenHoehe()-
        getSeitenRandObenMitKopf(seitenNr)-seitenKopfPosition);
    }

    if (seitenFuss != null) {
      template = seitenFuss.getSeitenKopfFuss(this, pdf, seitenNr);      
      pdf.addTemplate(template, 0, this.getSeitenRandUnten());
    }
  }
}