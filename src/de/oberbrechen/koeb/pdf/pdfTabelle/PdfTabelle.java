package de.oberbrechen.koeb.pdf.pdfTabelle;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateBeschreibung;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateFactoryMitSpalten;

/**
 * Diese Klasse dient dazu eine Tabelle einfach als PDF-Datei auszugeben.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfTabelle implements PdfTemplateFactoryMitSpalten {

  private TabellenModell tabellenModell;
  private TabellenKopf tabellenKopf;
  private int aktuelleZeile;
  private boolean erstesTemplate;
  private boolean zeigeSpaltenKopf;
  private float gesamtBreite;
  private float schriftGroesse;
  private float zeilenHoehe;
  private int spaltenAnzahl;
  private int currentSpalte;
  private int maxAnzahlZeilen;
  private int minZeilenAnzahlBeiTrennung;
  private float abstandFactor;
  
  public PdfTabelle(TabellenModell tabellenModell) {    
    this.tabellenModell = tabellenModell;
    
    aktuelleZeile = 0;
    erstesTemplate = true;
    zeigeSpaltenKopf = true;
    spaltenAnzahl = 1;
    abstandFactor = 1;
    
    schriftGroesse = PdfDokumentEinstellungen.getInstance().getSchriftgroesseNormal();
    zeilenHoehe = PdfDokumentEinstellungen.getInstance().getTabellenZeilenAbstandFaktor();
    currentSpalte = 1;
    minZeilenAnzahlBeiTrennung = 3;
  }  
  
  
  /**
   * Setzt die Mindestanzahl an Zeilen, die bei der Trennung einer Tabelle in
   * mehrere Templates mindestens auf dem letzten Template sein müssen.
   *
   */
  public void setMindestZeilenAnzahl(int zeilenAnzahl) {
    minZeilenAnzahlBeiTrennung = zeilenAnzahl;
  }
  
  public void setSpaltenAnzahl(int spalten) {
    this.spaltenAnzahl = spalten;    
  }
  
  /**
   * Setzt die Schriftgröße in Punkten
   * @param groesse die neue Schriftgröße
   */
  public void setSchriftGroesse(float groesse) {
    schriftGroesse = groesse;    
  }
  
  /**
   * Setzt die Zeilenhöhe als Faktor der Schriftgröße. Standard ist 1.5.
   * @param die neue Zeilenhöhe
   */
  public void setZeilenHöhe(float zeilenFactor) {
    zeilenHoehe = zeilenFactor;    
  }

  /**
   * Bestimmt, ob die Spaltenköpfe gezeigt werden sollen.
   * @param zeigeSpaltenKopf
   */
  public void setZeigeSpaltenKopf(boolean zeigeSpaltenKopf) {
    this.zeigeSpaltenKopf = zeigeSpaltenKopf;
  }
  
  public void setBreite(float breite) {
    this.gesamtBreite = breite;
  }
  
    
  private void init() throws SpaltenZuBreitException{
    if (zeigeSpaltenKopf) {
      this.tabellenKopf = new TabellenKopf(tabellenModell, schriftGroesse);
    } else {
      this.tabellenKopf = null;
    }
    
    //bestimme optimale Breite für alle Spalten, die veränderbar sind
    aktuelleZeile = 0;
    float benoetigteBreite = 0;

    float gesamtSpaltenAbstand = 0;
    for (int spalte=1; spalte < tabellenModell.getSpaltenAnzahl(); spalte++)
      gesamtSpaltenAbstand += tabellenModell.getSpaltenAbstand(spalte);
    float verfuegbareBreite = gesamtBreite-gesamtSpaltenAbstand;
    
    for (int spalte=1; spalte <= tabellenModell.getSpaltenAnzahl(); spalte++) {
      if (tabellenModell.getBesitztFesteBreite(spalte)) {
        verfuegbareBreite -= tabellenModell.getBreite(spalte);
      } else if (tabellenModell.getBreiteProzent(spalte) > 0){
        tabellenModell.setBreite(spalte, (gesamtBreite-gesamtSpaltenAbstand)/100*
            tabellenModell.getBreiteProzent(spalte));
        verfuegbareBreite -= tabellenModell.getBreite(spalte);
      } else {
        float optBreite = 0;
        double sumBreite = 0;
        float breite = 0;
        
        if (zeigeSpaltenKopf && tabellenKopf != null) {
          breite = tabellenKopf.getBenoetigeSpaltenBreite(spalte);
          sumBreite += breite*breite*breite*breite;
        }

        for (int zeile=1; zeile <= tabellenModell.getZeilenAnzahl(); zeile++) {
          String text = tabellenModell.getEintrag(spalte, zeile);
          if (text == null) text = "";
          breite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(text, schriftGroesse);
          sumBreite += breite*breite*breite*breite;
        }
        optBreite = (float) Math.sqrt(Math.sqrt(sumBreite / (tabellenModell.getZeilenAnzahl()+3)));
        tabellenModell.setBreite(spalte, optBreite);
        benoetigteBreite += optBreite;
      }
    }    
            
    if ((verfuegbareBreite < 0 && benoetigteBreite != 0) || 
        verfuegbareBreite < -2) {
      ErrorHandler.getInstance().handleException(new SpaltenZuBreitException(), false);
      benoetigteBreite = 0;
    }
    
    if (benoetigteBreite != 0) {      
      float skalierung = verfuegbareBreite / benoetigteBreite;
      for (int spalte=1; spalte <= tabellenModell.getSpaltenAnzahl(); spalte++) {
        if (!tabellenModell.getBesitztFesteBreite(spalte) &&
            !(tabellenModell.getBreiteProzent(spalte) > 0)) {
          float breite = tabellenModell.getBreite(spalte);
          tabellenModell.setBreite(spalte, breite*skalierung);
        }
      }           
    }    
  }
  
  /**
   * Schreibt die übergebene Zeile des Models in die übergebene Zeile der
   * aktuellen Seite der Tabelle
   *
   * @param modellZeile die Nummer der auszugebendem Zeile des Models
   * @param seitenZeile die Nummer der Seitenzeile an der die Modellzeile
   *   ausgegeben werden soll, die Zeile 1 ist dabei die letzte Zeile der Seite
   * @param seitenNr die Nummer der Seite, auf der die Zeile angezeigt werden soll
   * @param pdf das PdfContentByte, in dem ausgegeben werden soll
   */
  private void schreibeZeile(int modellZeile, int seitenZeile, PdfTemplate pdf) {
    float yPos = (seitenZeile-0.5f)*zeilenHoehe*schriftGroesse-
      0.375f*schriftGroesse;
    
    //eigentlichen Text ausgeben
    pdf.beginText();
    for (int spalte = 1; spalte <= tabellenModell.getSpaltenAnzahl(); spalte++) {
      // x-Position der Spalte bestimmen
      float links = tabellenModell.getSpaltenPositionLinks(spalte);
      float rechts = links + tabellenModell.getBreite(spalte);
      float mitte = (rechts + links) / 2;
      float breite = rechts - links;


      // Text holen und Skalierung bestimmen
      String text = tabellenModell.getEintrag(spalte, modellZeile);
      if (text == null) text = "";
      BaseFont font = getBaseFont(tabellenModell.getZellenSchrift(modellZeile, seitenZeile, spalte));     
      float textBreite = font.getWidthPoint(text, schriftGroesse);

      float skalierung = 1;
      if (textBreite > breite || tabellenModell.getSpaltenAusrichtung(spalte) ==
          TabellenModell.SPALTEN_AUSRICHTUNG_BLOCKSATZ) {
        skalierung = breite / textBreite;
        textBreite *= skalierung;
      }
      
      //Position für Ausgabe bestimmen
      float xPos = links;
      switch (tabellenModell.getSpaltenAusrichtung(spalte)) {
        case TabellenModell.SPALTEN_AUSRICHTUNG_BLOCKSATZ:
        case TabellenModell.SPALTEN_AUSRICHTUNG_LINKS:
          xPos = links; break;
        case TabellenModell.SPALTEN_AUSRICHTUNG_RECHTS:
          xPos = rechts-textBreite; break;
        case TabellenModell.SPALTEN_AUSRICHTUNG_VERTIKAL:
        case TabellenModell.SPALTEN_AUSRICHTUNG_ZENTRIERT:
          xPos = mitte-textBreite / 2; break;
      }

      //Schrift bestimmen
      pdf.setFontAndSize(font, schriftGroesse);        
      
      // Ausgabe      
      pdf.setHorizontalScaling(skalierung*100);
      pdf.setTextMatrix(xPos, yPos);
      pdf.showText(text);
    }
    pdf.endText();
  }

  /**
   * Liefert die Schrift, die zu den Konstanten aus TabellenModell paßt
   */
  protected static BaseFont getBaseFont(int id) {
    switch (id) {
      case TabellenModell.SCHRIFT_NORMAL:
        return PdfDokumentEinstellungen.schriftNormal;
      case TabellenModell.SCHRIFT_KURSIV:
        return PdfDokumentEinstellungen.schriftKursiv;
      case TabellenModell.SCHRIFT_FETT:
        return PdfDokumentEinstellungen.schriftFett;
      case TabellenModell.SCHRIFT_FETT_KURSIV:
        return PdfDokumentEinstellungen.schriftFettKursiv;
    }
    
    return null;
  }
  
  public boolean hasNextTemplate() {
    boolean erg = erstesTemplate || aktuelleZeile < tabellenModell.getZeilenAnzahl(); 
    return erg;
  }
    
  private int getZeilenAnzahlInTemplate(float laenge, 
      PdfTemplate spaltenKopfTemplate, int currentSpalte) {
    float laengeFuerZeilen = laenge;
    if (zeigeSpaltenKopf && tabellenKopf != null) laengeFuerZeilen-=spaltenKopfTemplate.getHeight();

    int zeilenAnzahlInTemplate;    
    if (currentSpalte == 1) {
      zeilenAnzahlInTemplate = (int) (laengeFuerZeilen/(schriftGroesse*zeilenHoehe));
      if (spaltenAnzahl > 1) {
        double zeilenAnzahlProSpalte = ((double) (tabellenModell.getZeilenAnzahl()-aktuelleZeile)) / spaltenAnzahl;
        if (zeilenAnzahlProSpalte > 2 && zeilenAnzahlProSpalte < zeilenAnzahlInTemplate*0.75f) {
          zeilenAnzahlInTemplate = (int) Math.ceil(zeilenAnzahlProSpalte);
        }      
      }

      int restlicheZeilen = tabellenModell.getZeilenAnzahl()-aktuelleZeile-
      spaltenAnzahl*zeilenAnzahlInTemplate;
      while (restlicheZeilen > 0 && 
          restlicheZeilen < (spaltenAnzahl*(minZeilenAnzahlBeiTrennung-1))+1) {
        restlicheZeilen -= spaltenAnzahl;
        zeilenAnzahlInTemplate--;
      }
      
      maxAnzahlZeilen = zeilenAnzahlInTemplate;      
    } else {
      zeilenAnzahlInTemplate = maxAnzahlZeilen;
    }    
        
    return zeilenAnzahlInTemplate;
  }
  
  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, 
      float maximaleLaenge, float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {

    if (erstesTemplate) {
      init();
      erstesTemplate = false;
    }
        
    float ausgleichAbstand = schriftGroesse * zeilenHoehe / 2 + 1;
    if (ausgleichAbstand > erlaubteAbweichung) ausgleichAbstand = 0;
    
    PdfTemplate spaltenKopfTemplate = null;
    if (zeigeSpaltenKopf && tabellenKopf != null) {
      spaltenKopfTemplate = 
        tabellenKopf.getTemplate(gesamtBreite, parentPdf);
    }
                                
    int zeilenAnzahlInTemplate = getZeilenAnzahlInTemplate(optimaleLaenge, 
        spaltenKopfTemplate, currentSpalte);
    if (zeilenAnzahlInTemplate < minZeilenAnzahlBeiTrennung) {
      zeilenAnzahlInTemplate = getZeilenAnzahlInTemplate(maximaleLaenge, 
          spaltenKopfTemplate, currentSpalte);
    }    
    if (zeilenAnzahlInTemplate + aktuelleZeile > tabellenModell.getZeilenAnzahl())
      zeilenAnzahlInTemplate = tabellenModell.getZeilenAnzahl() - aktuelleZeile;
    
    
    currentSpalte++;
    if (currentSpalte > spaltenAnzahl) currentSpalte=1;
    
    float templateLaenge = zeilenAnzahlInTemplate*(schriftGroesse*zeilenHoehe);
    if (spaltenKopfTemplate != null) templateLaenge+=spaltenKopfTemplate.getHeight();
    
    PdfTemplate template = parentPdf.createTemplate(gesamtBreite,
      templateLaenge);      
    
    erstelleHintergrund(zeilenAnzahlInTemplate, aktuelleZeile, template);
    erstelleHorizontaleLinien(zeilenAnzahlInTemplate, aktuelleZeile, template);
    erstelleVertikaleLinien(zeilenAnzahlInTemplate, aktuelleZeile, template);
    
    float startPos=0;    
    //Spaltentitel
    if (spaltenKopfTemplate != null) {
      template.addTemplate(spaltenKopfTemplate, 0, template.getHeight()-spaltenKopfTemplate.getHeight()-startPos);
    }

    // Die unterste Zeile trägt die Nummer 1, die vorletzte die Nummer 2 usw.
    for (int zeile=1; zeile <= zeilenAnzahlInTemplate; zeile++) {
      schreibeZeile(aktuelleZeile+zeile, zeilenAnzahlInTemplate-zeile+1, template);
    }
    aktuelleZeile += zeilenAnzahlInTemplate;
    
    PdfTemplateBeschreibung templateBeschreibung = new PdfTemplateBeschreibung(template, standardAbstand*abstandFactor);
    if (hasNextTemplate()) templateBeschreibung.setSeitenAusgleich(-ausgleichAbstand, 35);
     
    return templateBeschreibung;
  }

  /**
   * Setzt den Abstand, der nach der Überschrift frei bleiben soll,
   * als Faktor vom Standardabstand
   * @param abstandFactor
   */
  public void setAbstandFactor(float abstandFactor) {
    this.abstandFactor = abstandFactor;
  }
  
  private void erstelleHintergrund(int zeilenAnzahlInTemplate, int aktuelleZeile, PdfTemplate template) {

    int spaltenAnzahl = tabellenModell.getSpaltenAnzahl();
    boolean[][] bearbeitet = 
      new boolean[spaltenAnzahl][zeilenAnzahlInTemplate];
    for (int i = 0; i < spaltenAnzahl; i++) {
      for (int j = 0; j < zeilenAnzahlInTemplate; j++) {
        bearbeitet[i][j] = false;
      }        
    }
    
    for (int i = 1; i <= spaltenAnzahl; i++) {
      for (int j = 1; j <= zeilenAnzahlInTemplate; j++) {
        if (!bearbeitet[i-1][j-1]) {
          bearbeitet[i-1][j-1] = true;
          float hintergrund = tabellenModell.getZellenHintergrund(
            aktuelleZeile+j, j, i);            
          
          //weißer Hintergrund muss nicht gezeichnet werden            
          if (hintergrund == 1) continue; 

          //suche Ränder
          
          //suche zunächst nach Nachbarzellen in 
          //Zeile mit gleichem Hintergrund
          int i2 = i;
          while (i2 < spaltenAnzahl && tabellenModell.
            getZellenHintergrund(aktuelleZeile+j, j, i2+1) == hintergrund) {
            i2++;
            bearbeitet[i2-1][j-1] = true;              
          }

          //suche zunächst nach Nachbarzellen in 
          //Zeile mit gleichem Hintergrund
          int j2 = j;
          boolean ok = true;
          while (j2 < zeilenAnzahlInTemplate && ok) {
            for (int pos=i; pos <= i2 && ok; pos++) {
              ok = tabellenModell.getZellenHintergrund(
                aktuelleZeile+j2+1, j2+1, pos) == hintergrund;  
            }              
            if (ok) {
              j2++;
              for (int pos = i; pos <= i2; pos++) {
                bearbeitet[pos-1][j2-1] = true;
              }
            }
          }
          
          //Eigentlichen Hintergrund
          float x = tabellenModell.getSpaltenPositionLinks(i);

          float y = (zeilenAnzahlInTemplate-j2)*schriftGroesse*zeilenHoehe;
          float hoehe = (j2 - j + 1)*schriftGroesse*zeilenHoehe;
          float breite = tabellenModell.getSpaltenPositionRechts(i2)-
            tabellenModell.getSpaltenPositionLinks(i);
          if (i2 < spaltenAnzahl) 
            breite += tabellenModell.getSpaltenAbstand(i2)*
                      tabellenModell.getSpaltenAbstandHintergrund(i2);
          if (i > 1) {
            float factor = 1-tabellenModell.getSpaltenAbstandHintergrund(i-1);
            breite += tabellenModell.getSpaltenAbstand(i-1)*factor;          
            x -= tabellenModell.getSpaltenAbstand(i-1)*factor;
          }
          
          template.rectangle(x, y, breite, hoehe);
          template.setGrayFill(hintergrund);
          template.fill();
          template.setGrayFill(0);                
        }
      }        
    }         
  }

  private void erstelleVertikaleLinien(int zeilenAnzahlInTemplate, int aktuelleZeile, PdfTemplate template) {

    int spaltenAnzahl = tabellenModell.getSpaltenAnzahl();

    for (int i = 0; i <= spaltenAnzahl; i++) {
      for (int j = 1; j <= zeilenAnzahlInTemplate; j++) {
        float linienDicke = getSpaltenLinienDicke(
          aktuelleZeile, j, i);
        if (linienDicke == 0) continue;
        
        int j2 = j;
        while (j2 < zeilenAnzahlInTemplate && linienDicke == 
          getSpaltenLinienDicke(aktuelleZeile, j2+1, i)) {
            j2++;
        }
        
        float x = tabellenModell.getSpaltenPositionRechts(i);          
        if (i < spaltenAnzahl) x += tabellenModell.getSpaltenAbstand(i);
        float y = (zeilenAnzahlInTemplate-j+1)*15;
        float hoehe = (j2-j+1)*schriftGroesse*zeilenHoehe;

        template.setLineWidth(linienDicke);
        template.moveTo(x, y);
        template.lineTo(x, y-hoehe);
        template.stroke();
        j = j2+1;
      }
    }
  }

  private void erstelleHorizontaleLinien(int zeilenAnzahlInTemplate, int aktuelleZeile, PdfTemplate template) {

    int spaltenAnzahl = tabellenModell.getSpaltenAnzahl();

    for (int j = 0; j <= zeilenAnzahlInTemplate; j++) {
      for (int i = 1; i <= spaltenAnzahl; i++) {
        float linienDicke = getZeilenLinienDicke(
          zeilenAnzahlInTemplate, aktuelleZeile, j, i);
        if (linienDicke == 0) continue;
        
        int i2 = i;
        while (i2 < spaltenAnzahl && linienDicke == getZeilenLinienDicke(
          zeilenAnzahlInTemplate, aktuelleZeile, j, i2+1)) {
            i2++;
        }
        
        float x = tabellenModell.getSpaltenPositionLinks(i);

        float y = (zeilenAnzahlInTemplate-j)*schriftGroesse*zeilenHoehe;
        float breite = tabellenModell.getSpaltenPositionRechts(i2)-
          tabellenModell.getSpaltenPositionLinks(i);
        if (i2 < spaltenAnzahl && getZeilenLinienDicke(
          zeilenAnzahlInTemplate, aktuelleZeile, j, i2+1) != 0) 
          breite += tabellenModell.getSpaltenAbstand(i2);

        template.setLineWidth(linienDicke);
        template.moveTo(x, y);
        template.lineTo(x+breite, y);
        template.stroke();
        i = i2+1;
      }
    }
  }

  private float getSpaltenLinienDicke(int aktuelleZeile, int j, int i) {
    float linienDicke;
    if (i == 0) {        
      linienDicke = tabellenModell.getZellenRandRechts(aktuelleZeile+j,j,1);
    } else if (i == tabellenModell.getSpaltenAnzahl()) {
      linienDicke = tabellenModell.getZellenRandRechts(aktuelleZeile+j,j,i);            
    } else {
      linienDicke = Math.max(
        tabellenModell.getZellenRandRechts(aktuelleZeile+j,j,i),
        tabellenModell.getZellenRandLinks(aktuelleZeile+j,j,i+1));                      
    }
    return linienDicke;
  }

  private float getZeilenLinienDicke(int zeilenAnzahlInTemplate, 
    int aktuelleZeile, int j, int i) {
    float linienDicke;
    if (j == 0) {        
      linienDicke = tabellenModell.getZellenRandOben(aktuelleZeile+j+1,j+1,i);
    } else if (j == zeilenAnzahlInTemplate || 
      j + aktuelleZeile == tabellenModell.getZeilenAnzahl()) {
      linienDicke = tabellenModell.getZellenRandUnten(aktuelleZeile+j,j,i);            
    } else {
      linienDicke = Math.max(
        tabellenModell.getZellenRandUnten(aktuelleZeile+j,j,i),
        tabellenModell.getZellenRandOben(aktuelleZeile+j+1,j+1,i));                      
    }
    return linienDicke;
  }
}