package de.oberbrechen.koeb.pdf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;


/**
 * Der Standard-Seitenfuss.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class StandardSeitenFuss implements SeitenKopfFuss{

  public static final int LAYOUT_NUR_SEITE = 1;
  public static final int LAYOUT_TITEL_SEITE = 2;
  public static final int LAYOUT_ZEIT_TITEL_SEITE = 3;


  private Vector<PdfPreparedTemplate> templates;
  private String zeit;
  private String titel;
  private int layout;

  public StandardSeitenFuss(String titel) {

    this.layout = LAYOUT_ZEIT_TITEL_SEITE;
    this.titel = titel;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMM. yyyy H:mm");
    zeit = dateFormat.format(new Date())+" Uhr";
    
    templates = new Vector<PdfPreparedTemplate>();
  }
  
  /**
   * Setzt das Layout. Die erlaubten Layouts stehen als öffentliche
   * Konstanten zur Verfügung.
   *
   * @param layout das zu setzende Layout
   */
  public void setLayout(int layout) {
    this.layout = layout;
  }
  
  /**
   * Setzt den Titel des Seitenfusses neu
   * @param titel der neue Titel
   */
  public void setTitel(String titel) {
    this.titel = titel;
  }

  //Doku siehe bitte Interace
  public float getHoehe(int seitenNr) {
    return PdfDokumentEinstellungen.getInstance().getSchriftgroesseSeitenfuss()*5/4+2;
  }
  
  //Doku siehe bitte Interface
  public void finalisiere(int gesamtseitenAnzahl) {
    for (int i = 0; i < templates.size(); i++) {
      PdfPreparedTemplate preparedTemplate =
        templates.get(i);
      ErweitertesPdfDokument pdfDokument =
        preparedTemplate.pdfDokument;
      PdfTemplate template = preparedTemplate.template;
      float hoehe = this.getHoehe(preparedTemplate.seitenNr);      
      
      //bestimme Positionen
      float linkerRand = pdfDokument.getSeitenRandLinks();
      float rechterRand = pdfDokument.getSeitenBreite()-
                          pdfDokument.getSeitenRandRechts();
      float mitte = (rechterRand + linkerRand) / 2;
      float breite = rechterRand - linkerRand;
  
      //zeichne Trennlinie
      template.moveTo(linkerRand, hoehe);
      template.lineTo(rechterRand, hoehe);
      template.stroke();
  
      //Text besitimmen
      String seite = "Seite "+preparedTemplate.seitenNr+"/"+
                     gesamtseitenAnzahl;
  
      String textLinks = null;
      String textRechts = null;
      String textMitte = null;
  
      switch (layout) {
        case LAYOUT_NUR_SEITE:
          textMitte = seite;
          break;
        case LAYOUT_TITEL_SEITE:
          textLinks = titel;
          textRechts = seite;
          break;
        case LAYOUT_ZEIT_TITEL_SEITE:
          textMitte = titel;
          textLinks = zeit;
          textRechts = seite;
          break;
        default:
          textMitte = titel;
          textLinks = zeit;
          textRechts = seite;
      }
  
      //Text ausgeben
      final BaseFont schrift = PdfDokumentEinstellungen.schriftNormal;
      final float schriftGroesse = PdfDokumentEinstellungen.getInstance().getSchriftgroesseSeitenfuss();
  
      float textLinksBreite = 0;
      if (textLinks != null)
        textLinksBreite = schrift.getWidthPoint(textLinks, schriftGroesse);
  
      float textMitteBreite = 0;
      if (textMitte != null)
        textMitteBreite = schrift.getWidthPoint(textMitte, schriftGroesse);
  
      float textRechtsBreite = 0;
      if (textRechts != null)
        textRechtsBreite = schrift.getWidthPoint(textRechts, schriftGroesse);
  
      float textBreite = textLinksBreite + textMitteBreite + textRechtsBreite;
  
      float textScalierung = 1;
      if ((textBreite + 20)*textScalierung > breite)
        textScalierung =  breite / (textBreite + 20);
  
      if ((textMitteBreite/2 + textLinksBreite + 10)*textScalierung > breite / 2)
        textScalierung = breite / (2*(textMitteBreite/2 + textLinksBreite + 10));
  
      if ((textMitteBreite/2 + textRechtsBreite + 10)*textScalierung > breite / 2)
        textScalierung = breite / (2*(textMitteBreite/2 + textRechtsBreite + 10));
  
  
      template.beginText();
      template.setFontAndSize(schrift, schriftGroesse);
      template.setHorizontalScaling(textScalierung*100);
  
      float yPos = hoehe-schriftGroesse-2;
      if (textLinks != null) {
        template.setTextMatrix(linkerRand, yPos);
        template.showText(textLinks);
      }
  
      if (textMitte != null) {
        template.setTextMatrix(mitte - (textMitteBreite * textScalierung / 2), yPos);
        template.showText(textMitte);
      }
  
      if (textRechts != null) {
        template.setTextMatrix(rechterRand - textRechtsBreite*textScalierung, yPos);
        template.showText(textRechts);
      }
  
      template.endText();
    }        
    templates.clear();
  }

  //Doku siehe bitte Interface
  public PdfTemplate getSeitenKopfFuss(ErweitertesPdfDokument pdfDokument, PdfContentByte pdf, int seitenNr) {
    float hoehe = this.getHoehe(seitenNr);
    PdfTemplate template = pdf.createTemplate(pdfDokument.getSeitenBreite(),
      hoehe);
    
    PdfPreparedTemplate preparedTemplate = new PdfPreparedTemplate();
    preparedTemplate.template = template;
    preparedTemplate.seitenNr = seitenNr;
    preparedTemplate.pdfDokument = pdfDokument;
    templates.add(preparedTemplate);

    return template;
  }
}

class PdfPreparedTemplate {
  public PdfTemplate template;
  public int seitenNr;
  public ErweitertesPdfDokument pdfDokument;
}
