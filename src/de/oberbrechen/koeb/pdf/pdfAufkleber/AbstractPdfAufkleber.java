package de.oberbrechen.koeb.pdf.pdfAufkleber;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import de.oberbrechen.koeb.pdf.*;

/**
 * Diese Klasse dient dazu, Aufkleber auszugeben. Es können mehere Aufkleber
 * auf einer Seite ausgegeben werden. Die genaue Größe und Position der 
 * Aufkleber werden in Unterklassen konkretisiert. Die Vorder-
 * und Rückseiten der Aufkleber werden von als Enumerations von PDF-Templates
 * übergeben.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractPdfAufkleber extends PdfDokument {

  /**
   * Keine Schneidemarkierungen für die Aufkleber
   */
  public static final int SCHNEIDEMARKIERUNG_OHNE = 1;
  
  /**
   * In den Ecken der Aufkleber werden kleine Schneidemarkierungen
   * ausgegeben 
   */
  public static final int SCHNEIDEMARKIERUNG_ECKEN = 2;
  
  /**
   * Gibt einen vollständigen Rahmen um jeden Aufkleber aus. 
   */
  public static final int SCHNEIDEMARKIERUNG_RAHMEN = 3;


  /**
   * Erstellt ein neues PdfDokument in Standardgröße.
   */
  public AbstractPdfAufkleber() {
    super(); 
  }
  
  /**
   * Erstellt ein neues PdfDokument in der übergebenen Papiergröße.
   */
  public AbstractPdfAufkleber(Rectangle pageSize) {
    super(pageSize, false);
  }
  
  protected class PdfDimension {
    float height;
    float width;    

    public PdfDimension(float width, float height) {
      this.height = height;
      this.width = width;
    }      
  }
  
  protected class PdfPosition {
    float x;
    float y;

    public PdfPosition(float x, float y) {
      this.x = x;
      this.y = y;
    }      
  }
    
  protected boolean printRueckseiten = true;
  protected int startPos = 1;
  protected int schneideMarkierungen = SCHNEIDEMARKIERUNG_ECKEN;
  
  /**
   * Liefert die linke obere Position des Aufklebers mit der übergebenen
   * Nummer.
   * 
   * @param aufkleberNr die Nummer des Aufklebers auf der Seite, die Nummern
   *   laufen von 1 bis getAufkleberAnzahlProSeite()
   * @return die linke obere Position des Aufklebers als Abstand von der
   *   linken unteren Seitenecke in Points (72 Points sind ein Inch,
   *   ein Inch sind 2,54 cm) geliefert.
   */
  protected abstract PdfPosition getAufkleberPosition(int aufkleberNr);

  /**
   * Liefert die Anzahl von Aufklebern pro Seite
   */
  protected abstract int getAufkleberAnzahlProSeite();

  /**
   * Liefert die Anzahl von darzustellenden Aufkleber
   */
  protected abstract int getAufkleberAnzahl();
  
  /**
   * Liefert die Dimension eines einzelnen Aufklebers in Points (72 Points sind ein Inch,
   * ein Inch sind 2,54 cm).
   */
  protected abstract PdfDimension getAufkleberDimension();
    
  /**
   * Liefert die Vorderseite des Aufklebers mit der übergebenen Nummer.
   * @param nr die Nummer des Aufklebers. Nummern laufen von 1 bis getAufkleberAnzahl()
   * @return die Vorderseite des Aufklebers
   */
  protected abstract PdfTemplate getAufkleberVorderSeite(int nr, PdfContentByte pdf) throws Exception;
  
  /**
   * Liefert die Rückseite des Aufklebers mit der übergebenen Nummer.
   * @param nr die Nummer des Aufklebers. Nummern laufen von 1 bis getAufkleberAnzahl()
   * @return die Rückseite des Aufklebers
   */
  protected abstract PdfTemplate getAufkleberRueckSeite(int nr, PdfContentByte pdf) throws Exception;

  /**
   * Gibt die Schneidemarken an der übergebenen 
   * Aufkleberposition in das übergebene 
   * PdfContentByte aus. Schneidemarken werden nur auf
   * den Vorderseiten der Aufkleber ausgegeben. Welche Art von
   * 
   * @param pos die Aufkleberposition
   * @param pdf das PdfContentByte für die Ausgabe
   */
  private void printSchneidemarken(int pos, PdfContentByte pdf) {
    PdfDimension dimension = getAufkleberDimension();
    PdfPosition position = getAufkleberPosition(pos);

    pdf.setLineWidth(0.3f);
    
    if (schneideMarkierungen == SCHNEIDEMARKIERUNG_RAHMEN) {
      pdf.rectangle(position.x, position.y, dimension.width, dimension.height);
      pdf.stroke();
    } else if (schneideMarkierungen == SCHNEIDEMARKIERUNG_ECKEN) {
      float markierungLaenge=7;

      pdf.moveTo(position.x+markierungLaenge, position.y);
      pdf.lineTo(position.x, position.y);
      pdf.lineTo(position.x, position.y+markierungLaenge);

      pdf.moveTo(position.x+markierungLaenge, position.y+dimension.height);
      pdf.lineTo(position.x, position.y+dimension.height);
      pdf.lineTo(position.x, position.y-markierungLaenge+dimension.height);

      pdf.moveTo(position.x-markierungLaenge+dimension.width, position.y);
      pdf.lineTo(position.x+dimension.width, position.y);
      pdf.lineTo(position.x+dimension.width, position.y+markierungLaenge);

      pdf.moveTo(position.x-markierungLaenge+dimension.width, position.y+dimension.height);
      pdf.lineTo(position.x+dimension.width, position.y+dimension.height);
      pdf.lineTo(position.x+dimension.width, position.y-markierungLaenge+dimension.height);

      pdf.stroke();
    } else if (schneideMarkierungen == SCHNEIDEMARKIERUNG_OHNE) {
      
    } else {
      throw new IllegalArgumentException("Unbekannte Schneidemarkierung!"); 
    }
  }
  
  //Doku siehe bitte Interface
  public void schreibeInDokument(PdfWriter pdfWriter, Document document) throws Exception {
    PdfContentByte pdf = pdfWriter.getDirectContent();
    PdfDimension dimension = getAufkleberDimension();
    
    //Vorderseiten
    int seitenpos = startPos;
    for (int pos = 1; pos <= getAufkleberAnzahl(); pos++) {      
      if (seitenpos > getAufkleberAnzahlProSeite()) {        
        if (printRueckseiten) {
          document.newPage();
          //Blödsinn machen, damit auch wenn keine Rückseiten kommen,
          //eine leere Seite eingefügt wird
          pdf.stroke();           
          
          //Rückseiten     
          int rueckPos = pos-getAufkleberAnzahlProSeite();          
          int rueckStartPos = 1;
          if (pos < seitenpos) {
            rueckStartPos = startPos;
            rueckPos = 1;
          }
          for (int i = rueckStartPos; i <= getAufkleberAnzahlProSeite(); i++) {
            PdfTemplate template = getAufkleberRueckSeite(rueckPos, pdf);
            if (template != null) {        
              PdfPosition position = getAufkleberPosition(i);                                   
              pdf.addTemplate(template, document.getPageSize().width()-
                position.x-dimension.width, position.y);
            }          
            rueckPos++;
          }
        }
        document.newPage();
        //Blödsinn machen, damit auch wenn keine Vorderseiten kommen,
        //eine leere Seite eingefügt wird
        pdf.stroke(); 
        seitenpos = 1;
      }

      PdfTemplate template = getAufkleberVorderSeite(pos, pdf);
      printSchneidemarken(seitenpos, pdf);           
      PdfPosition position = getAufkleberPosition(seitenpos);                        
      if (template != null) pdf.addTemplate(template, position.x, position.y);
      seitenpos++;      
    }
    
    if (printRueckseiten) {
      //Restliche Rückseiten
      document.newPage();
      //Blödsinn machen, damit auch wenn keine Rückseiten kommen,
      //eine leere Seite eingefügt wird
      pdf.stroke(); 
      int rueckPos = getAufkleberAnzahl() - seitenpos + 1;          
      int rueckStartPos = 1;
      if (getAufkleberAnzahl() < seitenpos) {
        rueckStartPos = startPos;
        rueckPos = 1;
      }
      for (int i = rueckStartPos; i < seitenpos; i++) {
        PdfTemplate template = getAufkleberRueckSeite(rueckPos, pdf);
        rueckPos++;
        if (template != null) {        
          PdfPosition position = getAufkleberPosition(i);                                   
          pdf.addTemplate(template, document.getPageSize().width()-
            position.x-dimension.width, position.y);
        }          
      }
    } 
  }
  
  /**
   * Bestimmt, welche Art von Schneidemarken verwendet werden
   * sollen. Die verfügbaren Parameter sind als öffentliche
   * Konstanten dieser Klasse zugreifbar.
   */
  public void setSchneideMarkierungen(int i) {
    schneideMarkierungen = i;
  }

  /**
   * Bestimmt, ob die Rückseiten der Aufkleber ausgegeben
   * werden sollen.
   */
  public void setPrintRueckseiten(boolean b) {
    printRueckseiten = b;
  }
  
  /**
   * Bestimmt, mit welchem Aufkleber die erste Seite beginnt. Dies ist dafür 
   * gedacht, auch nur wenige Aufkleber auf vorgefertigtes, selbstklebende
   * Vorlagen drucken zu können und die verbleibenden Aufkleber später bedrucken
   * zu können.
   * @param startPos
   */
  public void setStartPos(int startPos) {
    if (startPos > 0 && startPos <= getAufkleberAnzahlProSeite()) {
      this.startPos = startPos;
    } else {
      this.startPos = 1;
    }
  }

}