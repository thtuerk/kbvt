package de.oberbrechen.koeb.pdf.pdfTabelle;

import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfListeDokumentFactory;

public abstract class PdfTabelleDokumentFactory extends PdfListeDokumentFactory {

  protected boolean zeigeSortierteSpalteHintergrund;
  protected boolean zeigeZeilenHintergrund;
  
  public PdfTabelleDokumentFactory() {
    zeigeSortierteSpalteHintergrund = true;
    zeigeZeilenHintergrund = true;
  }  
 
  public void setZeigeZeilenHintergrund(boolean zeigeZeilenHintergrund) {
    this.zeigeZeilenHintergrund = zeigeZeilenHintergrund;
  }
  
  public void setZeigeSortierteSpalteHintergrund(boolean zeigeSpaltenHintergrund) {
    this.zeigeSortierteSpalteHintergrund = zeigeSpaltenHintergrund;
  }
}