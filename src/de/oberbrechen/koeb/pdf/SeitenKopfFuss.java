package de.oberbrechen.koeb.pdf;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

/**
 * Dieses Interface repräsentiert einen Seitenkopf oder Seitenfuss. Die 
 * Benutzung erfolgt wie folgt: Ein ErweitertesPdfDokumentMitSeitenKopfFuss
 * fordert mittels getSeitenKopfFuss einen Seitenkopf bzw. -fuss an.
 * Das Template muss zwar beim Aufruf von getSeitenKopfFuss erstellt,
 * aber nicht vollständig ausgefüllt werden. So ist es möglich, 
 * z.B. die Gesamtseitenanzahl erst nachträglich einzutragen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface SeitenKopfFuss {

  /**
   * Liefert ein Template, das den Seitenkopf / -fuss für die 
   * übergebene Seite enthält.
   *
   * @param pdfDokument das PdfDokument für das der Seitenkopf / -fuss
   *   geliefert werden soll
   * @param pdf das Contentbyte, in dem das Template später ausgegeben
   *   werden soll
   * @param seitenNr die Seitennr, für die der Seitenkopf / -fuss
   *   geliefert werden soll
   * @return das Template
   * @throws Exception
   */
  public PdfTemplate getSeitenKopfFuss(ErweitertesPdfDokument
    pdfDokument, PdfContentByte pdf, int seitenNr) throws Exception;

  /**
   * Liefert die Höhe des Seitenkopfes / -fusses für die übergebene Seite
   * @param seitenNr die Seitennr
   * @return die Höhe
   * @throws Exception
   */
  public float getHoehe(int seitenNr) throws Exception;
  
  /**
   * Finalisiert alle seit dem letzten Aufruf von <code>finalisiere</code> mittels getSeitenKopfFuss
   * abgerufenen Templates mit der übergebenen Gesamtseitenanzahl.
   * @param gesamtseitenAnzahl
   */
  public void finalisiere(int gesamtseitenAnzahl);  
}