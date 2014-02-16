package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import com.lowagie.text.pdf.PdfTemplate;


/**
 * Dieses Klasse ist ein "dummer" Datencontainer.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfTemplateBeschreibung {
  
  protected PdfTemplate template;
  protected float abstand;
  protected boolean seitenAusgleich;
  protected float min, max;
  
  protected boolean erzwingeSeitenUmbruch;
  protected boolean verbieteSeitenUmbruch;
  protected boolean verbieteSeitenUmbruchVorher;
    
  public PdfTemplateBeschreibung(PdfTemplate template, float abstand) {
    this.template = template;
    this.abstand = abstand;
    
    erzwingeSeitenUmbruch = false;
    verbieteSeitenUmbruch = false;
    verbieteSeitenUmbruchVorher = false;
    
    unsetSeitenAusgleich();
  }
  
  /**
   * Setzt, ob nach dem beschriebenen Template ein Seitenumbruch
   * erfolgen muss.
   * @param erzwingeSeitenUmbruch
   */
  public void erzwingeSeitenUmbruch(boolean erzwingeSeitenUmbruch) {
    this.erzwingeSeitenUmbruch = erzwingeSeitenUmbruch;
  }
    
  /**
   * Setzt, ob nach dem beschriebenen Template ein Seitenumbruch
   * erfolgen darf.
   * @param verbieteSeitenUmbruch
   */
  public void verbieteSeitenUmbruch(boolean verbieteSeitenUmbruch) {
    this.verbieteSeitenUmbruch = verbieteSeitenUmbruch;
  }

  /**
   * Setzt, ob vor dem beschriebenen Template ein Seitenumbruch
   * erfolgen darf.
   * @param verbieteSeitenUmbruch
   */
  public void verbieteSeitenUmbruchVorher(boolean verbieteSeitenUmbruch) {
    this.verbieteSeitenUmbruchVorher = verbieteSeitenUmbruch;
  }

  /**
   * Sorgt dafür, dass wenn in einem PdfTemplateDokument dieses
   * Template als letztes auf einer Seite auftritt, die Abstände zwischen
   * Template sowie der obere und untere Seitenrand so angepasst werden, dass
   * das Template genau am Seitenende endet. Dies ist z.B. bei Tabellen aus
   * gestalterischen Gründen sinnvoll. Dabei sollte natürlich nicht zuviel 
   * Abstand eingefügt oder entfernt werden. Daher wird dies nur durchgeführt,
   * wenn der auszugeleichende Abstand zwischen min und max liegt. Beachten sie,
   * dass min auch negativ sein darf. In diesem Fall werden die Templates 
   * auf der Seite gestaucht.
   * 
   * @param min
   * @param max
   */
  public void setSeitenAusgleich(float min, float max) {
    seitenAusgleich = true;
    this.min = min;
    this.max = max;
  }
  
  public float getHeight() {
    if (template == null) return 0;
    return template.getHeight();
  }
  
  public void unsetSeitenAusgleich() {
    seitenAusgleich = false;
    min = 0;
    max = 0;    
  }

  /**
   * Liefert das eingentliche Template
   * @return
   */
  public PdfTemplate getTemplate() {
    return template;
  }
  
  /**
   * Eine zum Debuggen gedachte Methode, die einen Rahmen um das enthaltene
   * Template zeichnet. 
   *
   */
  public void zeigeRahmen() {
    if (template == null) return;
 
    template.rectangle(0, 0, template.getWidth(), template.getHeight());
    template.stroke();
  }
}