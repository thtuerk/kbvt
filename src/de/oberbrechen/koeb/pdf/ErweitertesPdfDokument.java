package de.oberbrechen.koeb.pdf;

import com.lowagie.text.Rectangle;

/**
 * Diese Klasse dient dazu eine Tabelle einfach als PDF-Datei auszugeben. Die
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class ErweitertesPdfDokument extends PdfDokument { 
  protected float seitenBreite;
  protected float seitenHoehe;

  protected float randLinks;
  protected float randRechts;
  protected float randOben;
  protected float randUnten;

  /**
   * Erstellt ein neues PdfDokument in der übergebenen Papiergröße.
   */
  public ErweitertesPdfDokument(Rectangle pageSize, boolean querFormat) {
    super(pageSize, querFormat);
    
    //this.pageSize ist nicht identisch mit pageSize. Es ist bereits
    //entsprechende querFormat gedreht
    seitenBreite = this.pageSize.width();
    seitenHoehe  = this.pageSize.height();

    randLinks = PdfDokumentEinstellungen.getInstance().getSeitenRandLinks();
    randRechts = PdfDokumentEinstellungen.getInstance().getSeitenRandRechts();
    randOben = PdfDokumentEinstellungen.getInstance().getSeitenRandOben();
    randUnten = PdfDokumentEinstellungen.getInstance().getSeitenRandUnten();
  }  

  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public ErweitertesPdfDokument(boolean querFormat) {
    this(PdfDokumentEinstellungen.getInstance().getPageSize(), querFormat);    
  }
  
  /**
   * Erstellt ein neues PdfDokument in Standardgröße und Hochformat
   */
  public ErweitertesPdfDokument() {
    this(false);    
  }

  /**
   * Liefert die Seitenbreite in Points
   * @return die Seitenbreite in Points
   */
  public float getSeitenBreite() {
    return seitenBreite;
  }

  /**
   * Liefert die Seitenhöhe in Points
   * @return die Seitenhöhe in Points
   */
  public float getSeitenHoehe() {
    return seitenHoehe;
  }

  /**
   * Liefert den linken Seitenrand in Points
   * @return den linken Seitenrand in Points
   */
  public float getSeitenRandLinks() {
    return randLinks;
  }

  /**
   * Liefert die Seitenbreite ohne die Ränder
   * @return die Seitenbreite ohne die Ränder
   */
  public float getVerfuegbareSeitenBreite() {
    return getSeitenBreite()-getSeitenRandLinks()-getSeitenRandRechts();
  }
  

  /**
   * Liefert die Seitenbreite ohne die Ränder
   * @return die Seitenbreite ohne die Ränder
   */
  public float getVerfuegbareSeitenHoehe() {
    return getSeitenHoehe()-getSeitenRandOben()-getSeitenRandUnten();
  }

  /**
   * Liefert den rechten Seitenrand in Points
   * @return den rechten Seitenrand in Points
   */
  public float getSeitenRandRechts() {
    return randRechts;
  }

  /**
   * Liefert den unteren Seitenrand in Points
   * @return den unteren Seitenrand in Points
   */
  public float getSeitenRandUnten() {
    return randUnten;
  }

  /**
   * Liefert den oberen Seitenrand in Points
   * @return den oberen Seitenrand in Points
   */
  public float getSeitenRandOben() {
    return randOben;
  }

  /**
   * Setzt die Seitenbreite in Points
   * @param seitenBreite die neue Seitenbreite in Points
   */
  public void setSeitenBreite(float seitenBreite) {
    this.seitenBreite = seitenBreite;
  }

  /**
   * Setzt die Seitenhöhe in Points
   * @param seitenHoehe die neue Seitenhöhe in Points
   */
  public void setSeitenHoehe(float seitenHoehe) {
    this.seitenHoehe = seitenHoehe;
  }

  /**
   * Setzt den linken Seitenrand in Points
   * @param randLinks der neue linke Seitenrand in Points
   */
  public void setSeitenRandLinks(float randLinks) {
    this.randLinks = randLinks;
  }

  /**
   * Setzt den rechten Seitenrand in Points
   * @param randRechts der neue rechte Seitenrand in Points
   */
  public void setSeitenRandRechts(float randRechts) {
    this.randRechts = randRechts;
  }

  /**
   * Setzt den unteren Seitenrand in Points
   * @param randUnten der neue untere Seitenrand in Points
   */
  public void setSeitenRandUnten(float randUnten) {
    this.randUnten = randUnten;
  }

  /**
   * Setzt den oberen Seitenrand in Points
   * @param randOben der neue obere Seitenrand in Points
   */
  public void setSeitenRandOben(float randOben) {
    this.randOben = randOben;
  }
}