package de.oberbrechen.koeb.pdf.pdfTemplateDokument;

import java.util.Vector;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;

public class BeschriftungenPdfTemplateFactory implements PdfTemplateFactory {
  
  class Eintrag {
    int spalte;
    int zeile;
    String beschriftung;
    String text;
  }
  
  class spaltenBreite {
    int spalte;
    float breite;
  }
  
  private float breite = 0;
  private boolean hasNextTemplate = true;  
  private float schriftGroesse;
  private float zeilenAbstandFaktor;
  private float spaltenAbstandFaktor = 0.5f;
  private Vector<Eintrag> daten;
  
  private int spaltenAnzahl;
  private float[] festeSpaltenBreiten;
  private boolean[] besitztFesteBreite;
  private boolean[] zeigeExtraBeschriftungsSpalte;
  private float abstandFactor;
  
  public BeschriftungenPdfTemplateFactory(int spaltenAnzahl) {
    this.schriftGroesse = PdfDokumentEinstellungen.getInstance().getSchriftgroesseNormal();
    this.zeilenAbstandFaktor = PdfDokumentEinstellungen.getInstance().getAbsatzZeilenAbstandFaktor();
    
    daten = new Vector<Eintrag>();
    abstandFactor = 1;
    this.spaltenAnzahl = spaltenAnzahl;
    
    besitztFesteBreite = new boolean[spaltenAnzahl];
    festeSpaltenBreiten = new float[spaltenAnzahl];
    zeigeExtraBeschriftungsSpalte = new boolean[spaltenAnzahl];
    
    for (int i=0; i < spaltenAnzahl; i++) {
      besitztFesteBreite[i] = false;
      zeigeExtraBeschriftungsSpalte[i] = true;
    }
  }
  
  /**
   * Setzt den Abstand, der nach der Überschrift frei bleiben soll,
   * als Faktor vom Standardabstand
   * @param abstandFactor
   */
  public void setAbstandFactor(float abstandFactor) {
    this.abstandFactor = abstandFactor;
  }
  
  /**
   * Bestimmt, ob die Beschriftungen der übergebenen Spalte in einer extra 
   * Spalte angezeigt werden sollen
   * @param spalte
   * @param zeigeExtraSpalte
   */
  public void setZeigeExtraBeschriftungsspalte(int spalte, boolean zeigeExtraSpalte) {
    zeigeExtraBeschriftungsSpalte[spalte-1] = zeigeExtraSpalte;
  }
  
  /**
   * Setzt die Breite der Spalte in Prozent der Gesamtbreite.
   * @param spalte
   * @param breite
   */
  public void setSpaltenBreite(int spalte, float breite) {
    besitztFesteBreite[spalte-1] = true;
    festeSpaltenBreiten[spalte-1] = breite;
  }
  
  /**
   * Setzt die zu verwendende Schriftgröße
   * @param schriftGroesse
   */
  public void setSchriftGroesse(float schriftGroesse) {
    this.schriftGroesse = schriftGroesse;
  }
  
  /**
   * Setzt den Abstand zwischen zwei Zeilen
   * @param faktor
   */
  public void setZeilenAbstandFaktor(float faktor) {
    this.zeilenAbstandFaktor = faktor;
  }
  
  /**
   * Setzt den Abstand zwischen zwei Spalten
   * @param faktor
   */
  public void setSpaltenAbstandFaktor(float faktor) {
    this.spaltenAbstandFaktor = faktor;
  }
  
  public void setBreite(float breite) {
    this.breite = breite;
  }

  public boolean hasNextTemplate() {
    return hasNextTemplate;
  }
  
  /**
   * Fügt einen Eintrag in angegebenen Spalte hinzu, der die übergebene
   * Beschriftung und den übergebenen Text enthält.
   * @param spalte die Spalte 
   * @param text der Text
   * @param beschriftung die Beschriftung
   */
  public void addEintrag(int spalte, String text, String beschriftung) {
    if (spalte > spaltenAnzahl) {
      ErrorHandler.getInstance().handleError("Spalte "+spalte+" nicht erlaubt, " +
          "da nur "+spaltenAnzahl+" Spalten zulässig!", false);
      return;
    }
    
    Eintrag eintrag = new Eintrag();
    eintrag.spalte = spalte;
    eintrag.beschriftung = beschriftung;
    eintrag.text = text;
    daten.add(eintrag);    
  }
  
  /**
   * Fügt einen Eintrag in angegebenen Spalte hinzu, der keine
   * Beschriftung und den übergebenen Text enthält.
   * @param spalte die Spalte 
   * @param text der Text
   */
  public void addEintrag(int spalte, String text) {
    addEintrag(spalte, text, null);
  }
  
  private int verteileZeilen(int spaltenAnzahl) {
    int[] aktuelleZeile = new int[spaltenAnzahl];
    
    for (Eintrag eintrag : daten) {
    
      if (!zeigeExtraBeschriftungsSpalte[eintrag.spalte-1]) {
        if (eintrag.beschriftung != null && 
            aktuelleZeile[eintrag.spalte-1] > 0) {
          aktuelleZeile[eintrag.spalte-1]++;        
        }
        aktuelleZeile[eintrag.spalte-1]++;        
        eintrag.zeile = aktuelleZeile[eintrag.spalte-1];
        if (eintrag.beschriftung != null) 
            aktuelleZeile[eintrag.spalte-1]++;        
      } else {
        aktuelleZeile[eintrag.spalte-1]++;        
        eintrag.zeile = aktuelleZeile[eintrag.spalte-1];
      }
    }

    int zeilenAnzahl = 0;
    for (int i=0; i < spaltenAnzahl; i++) {
      if (aktuelleZeile[i] > zeilenAnzahl) zeilenAnzahl = aktuelleZeile[i]; 
    }
    
    return zeilenAnzahl;    
  }
  
  private float[] bestimmeMaxSpaltenBreiten(int spaltenAnzahl) {
    float[] spaltenBreiten = new float[spaltenAnzahl*2];
    
    for (Eintrag eintrag : daten) {

      if (eintrag.beschriftung != null) {
        float beschriftungBreite = PdfDokumentEinstellungen.schriftFett.
          getWidthPoint(eintrag.beschriftung, schriftGroesse);
        
        if (beschriftungBreite > spaltenBreiten[2*(eintrag.spalte-1)])
          spaltenBreiten[2*(eintrag.spalte-1)] = beschriftungBreite;
      }
      
      if (eintrag.text != null) {
        float textBreite = PdfDokumentEinstellungen.schriftNormal.
          getWidthPoint(eintrag.text, schriftGroesse);
        int spaltenNr = 2*(eintrag.spalte-1)+1;
        if (!zeigeExtraBeschriftungsSpalte[eintrag.spalte-1]) spaltenNr--;
        
        if (textBreite > spaltenBreiten[spaltenNr])
          spaltenBreiten[spaltenNr] = textBreite;        
      }
    }
        
    return spaltenBreiten;
  }

  
  private float bestimmeSkalierung(float[] spaltenBreiten, int spaltenAnzahl) throws Exception {
    float spaltenAbstand = spaltenAbstandFaktor*schriftGroesse;
    float verfuegbareBreite = breite;    
    float summe = 0;
    
    float festeBreiteSkalierung = 1;
    for (int i=0; i < spaltenAnzahl; i++) {
      if (besitztFesteBreite[i]) {
        float aktuelleSpaltenBreite = breite*festeSpaltenBreiten[i]-spaltenAbstand;
        if (i != spaltenAnzahl-1) aktuelleSpaltenBreite -= 2*spaltenAbstand;
        if (!zeigeExtraBeschriftungsSpalte[i]) aktuelleSpaltenBreite+=spaltenAbstand;
        
        float aktuelleSkalierung = aktuelleSpaltenBreite /
          (spaltenBreiten[2*i]+spaltenBreiten[2*i+1]);
        if (aktuelleSkalierung < festeBreiteSkalierung)
          festeBreiteSkalierung = aktuelleSkalierung;
        verfuegbareBreite -= breite*festeSpaltenBreiten[i];
      } else {
        summe += spaltenBreiten[2*i];
        summe += spaltenBreiten[2*i+1];
        if (zeigeExtraBeschriftungsSpalte[i]) verfuegbareBreite -= spaltenAbstand;         
        if (i != spaltenAnzahl-1) verfuegbareBreite -= 2*spaltenAbstand;
      }
    }

    if (verfuegbareBreite < -2) throw new Exception("Spalten zu breit!");    
    float skalierung = verfuegbareBreite / summe;
    return (skalierung < festeBreiteSkalierung)?skalierung:festeBreiteSkalierung;
  }
  
  private float[] bestimmeSpaltenStartPositionen(float[] spaltenBreiten, float skalierung) {
    float[] spaltenStartPos = new float[spaltenBreiten.length+1];
    float spaltenAbstand = spaltenAbstandFaktor*schriftGroesse;
    spaltenStartPos[0] = 0;    
    
    for (int i=0; i < spaltenAnzahl; i++) {
      if (besitztFesteBreite[i]) {
        spaltenStartPos[2*i+1]= spaltenStartPos[2*i]+spaltenAbstand+
          skalierung*spaltenBreiten[2*i];
        spaltenStartPos[2*i+2]= spaltenStartPos[2*i]+festeSpaltenBreiten[i]*breite;
      } else {
        spaltenStartPos[2*i+1] = spaltenStartPos[2*i]+spaltenAbstand+
          skalierung*spaltenBreiten[2*i];
        if (!zeigeExtraBeschriftungsSpalte[i])
          spaltenStartPos[2*i+1]-=spaltenAbstand;
        spaltenStartPos[2*i+2] = spaltenStartPos[2*i+1]+2*spaltenAbstand+
          skalierung*spaltenBreiten[2*i+1];
      }
    }
    
    return spaltenStartPos;
  }
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge, float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {
    hasNextTemplate = false;
  
    int zeilenAnzahl = verteileZeilen(spaltenAnzahl);
    float[] spaltenBreiten = bestimmeMaxSpaltenBreiten(spaltenAnzahl);
    float skalierung = bestimmeSkalierung(spaltenBreiten, spaltenAnzahl);
    float[] spaltenStartPos = bestimmeSpaltenStartPositionen(spaltenBreiten, skalierung);
    
    float hoehe = (zeilenAnzahl-1)*zeilenAbstandFaktor*schriftGroesse+5*schriftGroesse/4;
    PdfTemplate template = parentPdf.createTemplate(breite, hoehe);
    

    template.beginText();
    template.setHorizontalScaling(skalierung*100);
    for (Eintrag eintrag : daten) {

      float y = hoehe - (eintrag.zeile-1)*zeilenAbstandFaktor*schriftGroesse-
        schriftGroesse;
      
      if (eintrag.beschriftung != null) {
        float x = spaltenStartPos[(eintrag.spalte-1)*2];
        template.setFontAndSize(PdfDokumentEinstellungen.schriftFett, schriftGroesse);
        template.setTextMatrix(x, y);
        template.showText(eintrag.beschriftung);
      }
      
      if (eintrag.text != null) {        
        float x;
        if (!zeigeExtraBeschriftungsSpalte[eintrag.spalte-1]) {
          x = spaltenStartPos[(eintrag.spalte-1)*2];
          if (eintrag.beschriftung != null) 
            y -= zeilenAbstandFaktor*schriftGroesse;
        } else {
          x = spaltenStartPos[(eintrag.spalte-1)*2+1];
        }
        template.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, schriftGroesse);
        template.setTextMatrix(x, y);
        template.showText(eintrag.text);
      }
    }
    
    template.endText();    
    return new PdfTemplateBeschreibung(template, standardAbstand*abstandFactor);
  }
}
