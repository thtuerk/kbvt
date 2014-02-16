package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

public abstract class PdfListeDokumentFactory extends PdfTemplateDokumentFactory {

  protected int sortierung;
  protected boolean umgekehrteSortierung;
  protected String titel;
  
  
  protected abstract int getStandardSortierung();
    
  
  public PdfListeDokumentFactory() {
    sortierung = getStandardSortierung();
    umgekehrteSortierung = false;
    titel="";
  }
  
  /**
   * Setzt den Titel
   * @param titel
   */
  public void setTitel(String titel) {
    this.titel = titel;
  }
  
  /**
   * Setzt die Sortierung
   * @param sortierung
   * @param umgekehrteSortierung
   */
  public void setSortierung(int sortierung, boolean umgekehrteSortierung) {
    this.sortierung = sortierung;
    this.umgekehrteSortierung = umgekehrteSortierung;
  }
}