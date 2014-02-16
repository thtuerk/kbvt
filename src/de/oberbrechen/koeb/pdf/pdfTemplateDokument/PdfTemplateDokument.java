package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import java.util.Vector;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.*;

/**
 * Ein PdfDokument, das die uebergebenen Templates alle nacheinander auf Seiten
 * verteilt.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfTemplateDokument
  extends ErweitertesPdfDokumentMitSeitenKopfFuss {
  
  //Konstanten für die Formatierung
  private static float abstandSkalierungMin = 0.5f;
  private static float abstandSkalierungMax = 2.0f;
  private static float obereRandVerschiebungMax;
  

  private KombiniertePdfTemplateFactory templateFactory;
  private float standardAbstand;
  private boolean debug = false;
  
  public PdfTemplateDokument(Rectangle pageSize, String titel, boolean querFormat) {
    super(pageSize, querFormat);
    templateFactory = new KombiniertePdfTemplateFactory();
    standardAbstand = PdfDokumentEinstellungen.getInstance().
      getPdfTemplateDokumentStandardAbstand();
    obereRandVerschiebungMax = getSeitenRandOben()/2;
    
    setSeitenFuss(new StandardSeitenFuss(titel));        
  }

  public PdfTemplateDokument(String titel, boolean querFormat) {
    this(PdfDokumentEinstellungen.getInstance().getPageSize(), titel, querFormat);
  }

  public PdfTemplateDokument(String titel) {
    this(titel, false);
  }
  
  /**
   * Fügt eine PdfTemplateFactory hinzu, d.h. die von der Factory
   * erzeugten Templates sollen in diesem Dokument angezeigt werden.
   * Die Hinzufügereihenfolge bestimmt die Reihenfolge der Templates
   * im Dokument. 
   *  
   * @param templateFactory
   */
  public void addPdfTemplateFactory(PdfTemplateFactory templateFactory) {
    this.templateFactory.addPdfTemplateFactory(templateFactory);
  }
  
  /**
   * Fügt eine PdfTemplateBeschreibung hinzu.
   * Die Hinzufügereihenfolge bestimmt die Reihenfolge der Templates
   * im Dokument. 
   */
  public void addPdfTemplateBeschreibung(PdfTemplateBeschreibung templateBeschreibung) {
    PdfTemplateFactoryBeschreibungListe factory = 
      new PdfTemplateFactoryBeschreibungListe();
    factory.addPdfTemplateBeschreibung(templateBeschreibung);
    
    addPdfTemplateFactory(factory);
  }

  /**
   * Setzt den Abstand in Points der normalerweise zwischen zwei Templates 
   * eingehalten werden soll. Ein Point entspricht 1 inch / 72.
   * @param abstand
   */
  public void setStandardTemplateAbstand(float abstand) {
    this.standardAbstand = abstand;
  }

  /**
   * Setzt den Seitenkopf für alle Seiten
   *
   * @param seitenKopf der Seitenkopf für alle anderen Seiten
   * @param seitenKopfErsteSeite der Seitenkopf für die erste Seite
   */
  public void setSeitenKopf(SeitenKopfFuss seitenKopf,
                            SeitenKopfFuss seitenKopfErsteSeite) {
    this.seitenKopf = seitenKopf;
    this.seitenKopfErsteSeite = seitenKopfErsteSeite;
  }

  /**
   * Setzt den Seitenkopf für alle Seiten
   * @param seitenKopf der Seitenkopf für alle Seiten
   */
  public void setSeitenKopf(SeitenKopfFuss seitenKopf) {
    this.seitenKopf = seitenKopf;
    this.seitenKopfErsteSeite = null;
  }

  /**
   * Setzt den Seitenfuss für alle Seiten
   * @param seitenFuss der Seitenfuss für alle Seiten
   */
  public void setSeitenFuss(SeitenKopfFuss seitenFuss) {
    this.seitenFuss = seitenFuss;
  }
  
  //Doku siehe bitte Interface
  public void schreibeInDokument(PdfWriter pdfWriter, Document doc) throws Exception {    
    int seitenNr = 1;
    PdfContentByte pdf = pdfWriter.getDirectContent();
    templateFactory.setBreite(getVerfuegbareSeitenBreite());
    
    Vector<PdfTemplateBeschreibung> aktuelleDaten = new Vector<PdfTemplateBeschreibung>();
    float verfuegbarerPlatz = 0;
    float verfuegbarerPlatzNaechsteSeite = getVerfuegbareSeitenHoeheMitKopfFuss(1);
    float abstand = 0;
    
    
    while (templateFactory.hasNextTemplate() || !aktuelleDaten.isEmpty()) {
      verfuegbarerPlatz = verfuegbarerPlatzNaechsteSeite;
      verfuegbarerPlatzNaechsteSeite = 
        getVerfuegbareSeitenHoeheMitKopfFuss(seitenNr+1);
      abstand = 0;
      
      for (int i = 0; i < aktuelleDaten.size(); i++) {
        PdfTemplateBeschreibung templateBeschreibung = 
          aktuelleDaten.get(i);
        
        verfuegbarerPlatz -= templateBeschreibung.getTemplate().getHeight();
        verfuegbarerPlatz -= templateBeschreibung.abstand;
        abstand += templateBeschreibung.abstand;        
      }
      
      //Platz auffüllen
      while (verfuegbarerPlatz > 0 && templateFactory.hasNextTemplate()) {
        float maxPlatz = Math.max(verfuegbarerPlatzNaechsteSeite, verfuegbarerPlatz);
        
        PdfTemplateBeschreibung templateBeschreibung = 
          templateFactory.getNextTemplate(verfuegbarerPlatz, 
              maxPlatz, obereRandVerschiebungMax, standardAbstand, pdf);
        
        if (templateBeschreibung.erzwingeSeitenUmbruch) {
          templateBeschreibung.abstand = 10*maxPlatz;
        }
        
        if (templateBeschreibung.verbieteSeitenUmbruchVorher && 
            !aktuelleDaten.isEmpty()) {
          PdfTemplateBeschreibung letzteBeschreibung =
            aktuelleDaten.lastElement();
          if (!letzteBeschreibung.erzwingeSeitenUmbruch) 
            letzteBeschreibung.verbieteSeitenUmbruch = true;
        }
        
        if (templateBeschreibung.getTemplate() != null) {
          aktuelleDaten.add(templateBeschreibung);
          verfuegbarerPlatz -= templateBeschreibung.getTemplate().getHeight();
          verfuegbarerPlatz -= templateBeschreibung.abstand;
          abstand += templateBeschreibung.abstand;
        } else {
          if (!aktuelleDaten.isEmpty()) {
            PdfTemplateBeschreibung vorherigeBeschreibung = 
              aktuelleDaten.get(aktuelleDaten.size()-1);
            vorherigeBeschreibung.abstand += templateBeschreibung.abstand;
            verfuegbarerPlatz -= templateBeschreibung.abstand;
            abstand += templateBeschreibung.abstand;
          }
        }
      }
      
      //verteile auf Seite, falls es etwas zu verteilen gibt
      if (aktuelleDaten.size() == 0) continue;
      
      int letzteTemplateNr = aktuelleDaten.size()-1;
      PdfTemplateBeschreibung letztesTemplate = aktuelleDaten.get(letzteTemplateNr);
      abstand -= letztesTemplate.abstand;
      verfuegbarerPlatz += letztesTemplate.abstand;
      
      //passt das letzte Template noch auf die Seite?
      if (letzteTemplateNr > 0 &&
          verfuegbarerPlatz < 0 &&  
          !(letztesTemplate.seitenAusgleich &&
          verfuegbarerPlatz > letztesTemplate.min &&
          -verfuegbarerPlatz < abstand * abstandSkalierungMin + obereRandVerschiebungMax)) {                
        letzteTemplateNr--;
        verfuegbarerPlatz += letztesTemplate.getTemplate().getHeight();
        letztesTemplate = letzteTemplateNr < 0?null:aktuelleDaten.get(letzteTemplateNr);
        if (letztesTemplate != null) {
          abstand -= letztesTemplate.abstand;
          verfuegbarerPlatz += letztesTemplate.abstand;          
        }
      }
      
      //Entferne evtl. weitere Templates, weil nach ihnen kein Seitenumbruch
      //erfolgen darf
      int letzteTemplateNrTemp = letzteTemplateNr;
      while ((letzteTemplateNrTemp >= 0) && aktuelleDaten.get(letzteTemplateNrTemp).verbieteSeitenUmbruch) {
        letzteTemplateNrTemp--;
      }
      
      //Nur wirklich entfernen, wenn nicht alle
      if (letzteTemplateNrTemp >= 0) {
        //wahrscheinlich unnoetig
        if (letztesTemplate == null) throw new NullPointerException();

        for (int i=letzteTemplateNr; i > letzteTemplateNrTemp; i--) {
          PdfTemplateBeschreibung beschreibung = 
            aktuelleDaten.get(i);
          
          verfuegbarerPlatz += beschreibung.getTemplate().getHeight();
          verfuegbarerPlatz += beschreibung.abstand;          
          abstand -= letztesTemplate.abstand;
        }        
        
        letzteTemplateNr = letzteTemplateNrTemp;
        letztesTemplate = aktuelleDaten.get(letzteTemplateNr);
      }
      //wahrscheinlich unnoetig
      if (letztesTemplate == null) throw new NullPointerException();

      float abstandSkalierung = 1;
      float verschiebung = 0;
      if (letzteTemplateNr >= 0 &&
          verfuegbarerPlatz != 0 &&          
          letztesTemplate.seitenAusgleich &&
          verfuegbarerPlatz > letztesTemplate.min &&
          verfuegbarerPlatz < letztesTemplate.max) {
        
        if (abstand != 0) {
          abstandSkalierung = 1 + (verfuegbarerPlatz / abstand);
          if (abstandSkalierung < abstandSkalierungMin) abstandSkalierung = abstandSkalierungMin;
          if (abstandSkalierung > abstandSkalierungMax) abstandSkalierung = abstandSkalierungMax;
          verfuegbarerPlatz -= abstand*abstandSkalierung - abstand;
        }
      }
      
      
      if (letzteTemplateNr >= 0 &&
          verfuegbarerPlatz != 0 &&          
          
          ((letztesTemplate.seitenAusgleich &&
          verfuegbarerPlatz > letztesTemplate.min &&
          verfuegbarerPlatz < letztesTemplate.max) || 
          verfuegbarerPlatz < 0)) {
          
          verschiebung = verfuegbarerPlatz;
      
          if (-verschiebung > obereRandVerschiebungMax) {
            ErrorHandler.getInstance().handleException(new Exception("Template passt nicht " +
                "ganz auf Seite "+seitenNr+"!\n" +
                "Verschiebung "+verschiebung+", max. "+obereRandVerschiebungMax), false);
          }
      }
      
      
      if (seitenNr == 1) {
        seitenKopfPositionErsteSeite = verschiebung;
      } else {
        seitenKopfPosition = verschiebung;
      }
      
      
      //Eigentliches Schreiben
      float yPos = getSeitenHoehe()-getSeitenRandObenMitKopf(seitenNr)-
        verschiebung;
      if (seitenNr > 1) doc.newPage();
      
      for (int i = 0; i <= letzteTemplateNr; i++) {
        PdfTemplateBeschreibung template = 
          aktuelleDaten.remove(0);
        if (debug) template.zeigeRahmen();
        yPos -= template.getTemplate().getHeight();
        pdf.addTemplate(template.getTemplate(), 1, 0, 0, 1, 
            getSeitenRandLinks(), yPos);
        yPos -= template.abstand * abstandSkalierung;
      }
      
      schreibeSeitenKopfFuss(seitenNr, pdf);
      seitenNr++;      
    }

    if (seitenNr == 1) {
      schreibeSeitenKopfFuss(1, pdf);
      seitenNr++;
    }
    
    this.finalisiere(seitenNr-1);
  }
  
  /**
   * Schaltet den Debug-Modus an oder aus. Im Debug-Modus werden
   * Rahmen um alle Templates gezeichnet, um diese klar trennen zu können.
   * 
   * @param debug
   * @return
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  
  /**
   * Fügt einen Seitenumbruch ein.
   */
  public void addSeitenUmbruch() {
    PdfTemplateBeschreibung templateBeschreibung = 
      new PdfTemplateBeschreibung(null, 0);
    
    templateBeschreibung.erzwingeSeitenUmbruch = true;
    
    addPdfTemplateBeschreibung(templateBeschreibung);
  }
  
  /**
   * Verhindert einen Seitenumbruch
   */
  public void verbieteSeitenUmbruch() {
    PdfTemplateBeschreibung templateBeschreibung = 
      new PdfTemplateBeschreibung(null, 0);
    
    templateBeschreibung.verbieteSeitenUmbruch = true;
    templateBeschreibung.verbieteSeitenUmbruchVorher = true;
    
    addPdfTemplateBeschreibung(templateBeschreibung);
  }

  /**
   * Fügt zusätzlichen Abstand ein. Der Abstand kann auch negativ sein, um
   * vorher eingefügten Abstand zu kompensieren.
   */
  public void addAbstand(float abstand) {
    PdfTemplateBeschreibung templateBeschreibung = 
      new PdfTemplateBeschreibung(null, abstand);
    
    addPdfTemplateBeschreibung(templateBeschreibung);
  }

  /**
   * Fügt zusätzlich Abstand, in Länge des Standardabstandes multipliziert
   * mit dem übergebenen Wert ein. Der Abstand kann auch negativ sein, um
   * vorher eingefügten Abstand zu kompensieren. 
   * @param der Factor für den Standardabstand
   */
  public void addAbstandRelativ(float factor) {
    addAbstand(standardAbstand*factor);
  }  

  /**
   * Fügt eine Hauptüberschrift, also eine Überschrift für das gesamte Dokument
   * ein.
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addHauptUeberschrift(String links, String mitte, String rechts) {
    templateFactory.addHauptUeberschrift(links, mitte, rechts);
  }
  
  /**
   * Fügt eine Unterüberschrift, also eine Überschrift für einen Unterabschnitt
   * ein.
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addUnterUeberschrift(String links, String mitte, String rechts) {
    templateFactory.addUnterUeberschrift(links, mitte, rechts);
  }
  
  /**
   * Fügt eine Miniüberschrift, also eine sehr kleine Überschrift ein.
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addMiniUeberschrift(String links, String mitte, String rechts) {
    templateFactory.addMiniUeberschrift(links, mitte, rechts);
  }

  /**
   * Fügt eine mittlere Überschrift, also eine Überschrift für einen 
   * Abschnitt der selbst mehrere Unterabschnitte enthält.
   * 
   * @param links der Text, der links in der Überschrift erscheinen soll
   * @param mitte der Text, der mittig in der Überschrift erscheinen soll
   * @param rechts der Text, der rechts in der Überschrift erscheinen soll
   */
  public void addMittlereUeberschrift(String links, String mitte, String rechts) {
    templateFactory.addMittlereUeberschrift(links, mitte, rechts);
  }
  
  /**
   * Fügt eine Überschrift in der übergebenen Schriftgröße, die den
   * die übergebenen Texte jeweils links, zentriert oder rechts enthält, hinzu.
   * Danach wird abstandFactor*standardAbstand Abstand gelassen.
   */  
  public void addUeberschrift(String links, String mitte, String rechts, 
      float schriftGroesse, float abstandFactor) {
    templateFactory.addUeberschrift(links, mitte, rechts, schriftGroesse, abstandFactor);
  }
}
