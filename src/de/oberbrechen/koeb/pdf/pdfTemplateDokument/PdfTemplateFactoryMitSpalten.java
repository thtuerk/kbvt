package de.oberbrechen.koeb.pdf.pdfTemplateDokument;


/**
 * Dieses Interface repräsentiert eine PdfTemplateFactory,
 * die speziell für die SpaltenTemplateFactory eine Methode anbietet.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface PdfTemplateFactoryMitSpalten extends PdfTemplateFactory {
  
  /**
   * Setzt die Anzahl der zu benutzenden Spalten
   * @param spalten
   */
  public void setSpaltenAnzahl(int spalten);
}