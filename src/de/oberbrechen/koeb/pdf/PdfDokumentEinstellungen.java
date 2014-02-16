package de.oberbrechen.koeb.pdf;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse kapselt alle Einstellungen, die 
 * für PDF-Dokumente wichtig sind.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfDokumentEinstellungen {

  public static BaseFont schriftNormal;
  public static BaseFont schriftKursiv;
  public static BaseFont schriftFettKursiv;
  public static BaseFont schriftFett;
  public static PdfDokumentEinstellungen instance = null;
  
  static {
    try {
      schriftNormal = BaseFont.createFont(BaseFont.HELVETICA,
        BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      schriftFett = BaseFont.createFont(BaseFont.HELVETICA_BOLD,
        BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      schriftFettKursiv = BaseFont.createFont(BaseFont.HELVETICA_BOLDOBLIQUE,
        BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      schriftKursiv = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE,
          BaseFont.CP1252, BaseFont.NOT_EMBEDDED);      
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Initialisieren der Klasse PdfDokument!", true);
    }
  }    
  
  public static PdfDokumentEinstellungen getInstance() {
    if (instance == null) instance = new PdfDokumentEinstellungen();
    return instance;
  }
  
  /**
   * Setzt die Einstellungen zurück.
   */
  public static void reset() {
    instance = null;
  }
  
      
  Rectangle pageSize;
  String anzeigeBefehl;
  float schriftSkalierung;
  float seitenRandLinks;
  float seitenRandRechts;
  float seitenRandOben;
  float seitenRandUnten;
  
  float absatzZeilenAbstandFaktor;
  float tabellenZeilenAbstandFaktor;
  
  public PdfDokumentEinstellungen() {
    EinstellungFactory einstellungFactory = Datenbank.getInstance().getEinstellungFactory();
    
    //PageSize dekodieren
    String pagesize = einstellungFactory.getEinstellung(
        this.getClass().getName(), "PageSize").getWert("A4");
    if (pagesize.equalsIgnoreCase("A4")) {
      this.pageSize = PageSize.A4;
    } else if (pagesize.equalsIgnoreCase("A5")) {
      this.pageSize = PageSize.A5;         
    } else if (pagesize.equalsIgnoreCase("A3")) {
      this.pageSize = PageSize.A3;         
    } else if (pagesize.equalsIgnoreCase("letter")) {
      this.pageSize = PageSize.LETTER;         
    } else if (pagesize.equalsIgnoreCase("legal")) {
      this.pageSize = PageSize.LEGAL;         
    } else {
      this.pageSize = PageSize.A4;      
    }
  
    anzeigeBefehl = einstellungFactory.getClientMitarbeiterEinstellung( 
        "de.oberbrechen.koeb.pdf.PdfDokument", 
        "PDFViewer").getWert(null);
    
        
    schriftSkalierung = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "schriftSkalierung").getWertFloat(1f);
    seitenRandLinks = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "seitenRandLinks").getWertFloat(57);
    seitenRandRechts = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "seitenRandRechts").getWertFloat(57);
    seitenRandOben = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "seitenRandOben").getWertFloat(57);
    seitenRandUnten = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "seitenRandUnten").getWertFloat(42);
    
    absatzZeilenAbstandFaktor = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "absatzZeilenAbstandFaktor").getWertFloat(1.2f);
    tabellenZeilenAbstandFaktor = einstellungFactory.getMitarbeiterEinstellung(
        this.getClass().getName(), "tabellenZeilenAbstandFaktor").getWertFloat(1.5f);
  }
      
  /**
   * Liefert die Standard-Seitengröße für PDF-Dateien
   */
  public Rectangle getPageSize() {
    return pageSize;
  }
  
  /**
   * Liefert den Befehl, der benutzt wird, um Pdf-Dateien anzuzeigen
   */
  public String getAnzeigeBefehl() {
    return anzeigeBefehl;
  }

  /**
   * Liefert die Schriftgröße für den Seitenfuss
   */
  public float getSchriftgroesseSeitenfuss() {
    return getSchriftgroesseNormal();
  }

  /**
   * Liefert die Schriftgröße für das Kalenderelement
   */
  public float getSchriftgroesseKalendar() {
    return getSchriftgroesseNormal();
  }

  public float getSchriftgroesseKlein() {
    return 8*schriftSkalierung;
  }  

  public float getSchriftgroesseNormal() {
    return 10*schriftSkalierung;
  }  

  public float getSchriftgroesseGross() {
    return 12*schriftSkalierung;
  }

  public float getTabellenZeilenAbstandFaktor() {
    return tabellenZeilenAbstandFaktor;
  }

  public float getTabellenKopfMaximalHoehe() {
    return 150*schriftSkalierung;
  }

  public float getAbsatzZeilenAbstandFaktor() {
    return absatzZeilenAbstandFaktor;
  }

  public float getSchriftgroesseUeberschriftNormal() {
    return 24*schriftSkalierung;
  }  

  public float getSchriftgroesseUeberschriftKlein() {
    return 18*schriftSkalierung;
  }  
  
  public float getSchriftgroesseUeberschriftSehrKlein() {
    return 14*schriftSkalierung;
  }  
  
  public float getSchriftgroesseUeberschriftMini() {
    return 10*schriftSkalierung;
  }

  public float getAbstandUeberschriftNormal() {
    return 1f;
  }

  public float getAbstandUeberschriftKlein() {
    return 0.4f;
  }    
  
  public float getAbstandUeberschriftSehrKlein() {
    return 0.2f;
  }    

  public float getAbstandUeberschriftMini() {
    return 0.1f;
  }

  public float getPdfTemplateDokumentStandardAbstand() {
    return 25f*schriftSkalierung;
  }

  public float getSeitenRandLinks() {
    return seitenRandLinks;
  }    

  public float getSeitenRandRechts() {
    return seitenRandRechts;
  }    

  public float getSeitenRandOben() {
    return seitenRandOben;
  }    

  public float getSeitenRandUnten() {
    return seitenRandUnten;
  }    
}