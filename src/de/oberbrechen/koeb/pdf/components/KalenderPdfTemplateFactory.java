package de.oberbrechen.koeb.pdf.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateBeschreibung;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateFactory;

/**
 * Diese Klasse stellt die nötige Funktionalität bereit, um einen Monat als
 * Kalender in eine Pdf-Datei auszugeben.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class KalenderPdfTemplateFactory implements PdfTemplateFactory {

  private static final SimpleDateFormat ueberschriftFormat =
    new SimpleDateFormat("MMMM yyyy");
  private static final SimpleDateFormat hashtableFormat =
    new SimpleDateFormat("yyyy-MM-dd");

  private float schriftGroesse;
  private float breite;
  private Hashtable<String, String> hashtable;
  
  private Date startDatum = null;
  private Date endeDatum = null;
  private Date minDatum = null;
  private Date maxDatum = null;

  private int currentMonat = -1;
  private int currentJahr = -1;
  private int maxMonat = -1;
  private int maxJahr = -1;
  
  public KalenderPdfTemplateFactory() {
    hashtable = new Hashtable<String, String>();
    schriftGroesse = PdfDokumentEinstellungen.getInstance().getSchriftgroesseKalendar();
  }

  /**
   * Setzt die zu verwendende Schriftgröße
   * @param schriftGroesse
   */
  public void setSchriftGroesse(float schriftGroesse) {
    this.schriftGroesse = schriftGroesse;
  }
  
  public void setBreite(float breite) {
    this.breite = breite;
  }
  
  /**
   * Setzt einen String, der im Kalender am übergebenen Datum angezeigt
   * werden soll.
   *
   * @param tag der Tag im Monat, in dem der Eintrag angezeigt werden soll
   * @param monat der Monat, in dem der Eintrag angezeigt werden soll
   * @param jahr das Jahr, in dem der Eintrag angezeigt werden soll
   * @param eintrag der Eintrag
   */
  public void setEintrag(int tag, int monat, int jahr, String eintrag) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(jahr, monat-1, tag);
    setEintrag(calendar.getTime(), eintrag);
  }

  /**
   * Fügt den String zu eim Eintrag hinzu, der im Kalender am übergebenen 
   * Datum angezeigt werden soll. Solche Einträge werden durch Kommata
   * getrennt ausgegeben.
   *
   * @param tag der Tag im Monat, in dem der Eintrag angezeigt werden soll
   * @param monat der Monat, in dem der Eintrag angezeigt werden soll
   * @param jahr das Jahr, in dem der Eintrag angezeigt werden soll
   * @param eintrag der Eintrag
   */
  public void addEintrag(int tag, int monat, int jahr, String eintrag) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(jahr, monat-1, tag);
    addEintrag(calendar.getTime(), eintrag);
  }

  /**
   * Setzt einen String, der im Kalender am übergebenen Datum angezeigt
   * werden soll.
   *
   * @param datum das Datum, an dem der Eintrag angezeigt werden soll
   * @param eintrag der Eintrag
   */
  public void setEintrag(Date datum, String eintrag) {
    String key = hashtableFormat.format(datum);
    if (eintrag != null)
      hashtable.put(key, eintrag);
    else
      hashtable.remove(key);
    
    if (minDatum == null || datum.before(minDatum)) minDatum = datum;
    if (maxDatum == null || datum.after(maxDatum)) maxDatum = datum;    
  }

  /**
   * Fügt den String zu eim Eintrag hinzu, der im Kalender am übergebenen 
   * Datum angezeigt werden soll. Solche Einträge werden durch Kommata
   * getrennt ausgegeben.
   *
   * @param datum das Datum, an dem der Eintrag angezeigt werden soll
   * @param eintrag der Eintrag
   */
  public void addEintrag(Date datum, String eintrag) {
    if (eintrag == null) return;
    String key = hashtableFormat.format(datum);
    
    Object alterEintrag = hashtable.get(key);
    if (alterEintrag == null)
      hashtable.put(key, eintrag);
    else
      hashtable.put(key, alterEintrag.toString()+", "+eintrag);
    
    if (minDatum == null || datum.before(minDatum)) minDatum = datum;
    if (maxDatum == null || datum.after(maxDatum)) maxDatum = datum;
  }

  /**
   * Liefert ein Template, in dem der Kalender für den übergebenen Monat und
   * das übergebene Jahr dargestellt wird.
   */
  private PdfTemplate getKalenderTemplate(int monat, int jahr, PdfContentByte pdf) {
    float zellenBreite = breite / 7;
    Calendar kalender = Calendar.getInstance();
    kalender.set(jahr, monat-1, 1);
    int ersterWochentag = (kalender.get(Calendar.DAY_OF_WEEK) - 2) % 7;
    if (ersterWochentag < 0) ersterWochentag += 7;

    int tageInMonat = kalender.getActualMaximum(Calendar.DAY_OF_MONTH);
    int zeilen = (int) (Math.ceil(((double) (ersterWochentag+tageInMonat)) / 7));
    float hoehe = (float) (zellenBreite*zeilen+schriftGroesse*3.5);

    PdfTemplate template = pdf.createTemplate(breite, hoehe);

    String ueberschrift = ueberschriftFormat.format(kalender.getTime());
    float ueberschriftBreite = PdfDokumentEinstellungen.schriftFett.
      getWidthPoint(ueberschrift, (int) (schriftGroesse*1.5));

    template.beginText();
    if (ueberschriftBreite > breite) {
      template.setHorizontalScaling(breite / ueberschriftBreite * 100);
      ueberschriftBreite = breite;
    }
    template.setFontAndSize(PdfDokumentEinstellungen.schriftFett, (int)(schriftGroesse*1.5));
    template.setTextMatrix((breite - ueberschriftBreite)/2,
                           (float) (hoehe-schriftGroesse*1.4));
    template.showText(ueberschrift);
    template.setHorizontalScaling(100);    
    template.endText();

    //Male Hintergrund
    template.rectangle(0, (float) (hoehe-schriftGroesse*3.5), breite,
                       (float) (schriftGroesse*1.5));
    template.setGrayFill(0.7f);
    template.fill();

    template.rectangle(zellenBreite*ersterWochentag, zellenBreite*(zeilen-1),
      breite-zellenBreite*ersterWochentag, zellenBreite);
    template.rectangle(0, zellenBreite, breite, (zeilen-2)*zellenBreite);
    template.rectangle(0, 0,
      zellenBreite*(7-zeilen*7+ersterWochentag+tageInMonat), zellenBreite);
    template.setGrayFill(0.9f);
    template.fill();


    //Tage im Monat
    int zeile = zeilen;
    int spalte = ersterWochentag;
    for (int i=1; i <= tageInMonat; i++) {
      kalender.set(Calendar.DAY_OF_MONTH, i);
      if (hashtable.containsKey(hashtableFormat.format(kalender.getTime()))) {
      template.rectangle(spalte*zellenBreite, zeile*zellenBreite,
        zellenBreite, -zellenBreite);
      }
      spalte++; if (spalte == 7) {spalte = 0; zeile--;}
    }
    template.setGrayFill(0.7f);
    template.fill();

    zeile = zeilen;
    spalte = ersterWochentag;
    for (int i=1; i <= tageInMonat; i++) {
      template.rectangle(spalte*zellenBreite, zeile*zellenBreite,
        zellenBreite/3, -zellenBreite/3);
      spalte++; if (spalte == 7) {spalte = 0; zeile--;}
    }
    template.setGrayFill(1);
    template.setLineWidth(0.2f);
    template.fillStroke();

    //Male Linien
    template.setGrayFill(0);
    template.setLineWidth(1);

    template.moveTo(0,0);
    template.lineTo(breite, 0);
    template.moveTo(0, (float) (hoehe-schriftGroesse*3.5));
    template.lineTo(breite, (float) (hoehe-schriftGroesse*3.5));
    template.moveTo(0, hoehe-schriftGroesse*2);
    template.lineTo(breite, hoehe-schriftGroesse*2);

    template.moveTo(0,0);
    template.lineTo(0,hoehe-schriftGroesse*2);
    template.moveTo(breite,0);
    template.lineTo(breite,hoehe-schriftGroesse*2);
    template.stroke();

    template.setLineWidth(0.2f);
    for (int i=1; i < 7; i++) {
      template.moveTo(i*zellenBreite,0);
      template.lineTo(i*zellenBreite,hoehe-schriftGroesse*2);
    }

    for (int i=1; i < zeilen; i++) {
      template.moveTo(0, i*zellenBreite);
      template.lineTo(breite,i*zellenBreite);
    }
    template.stroke();


    //Text

    //Wochentage
    String[] wochenTage = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
    template.beginText();
    template.setFontAndSize(PdfDokumentEinstellungen.schriftFettKursiv, schriftGroesse);
    for (int i=0; i < 7; i++) {
      float textBreite = PdfDokumentEinstellungen.schriftFettKursiv.getWidthPoint(
        wochenTage[i], schriftGroesse);
      template.setTextMatrix((float) (zellenBreite*(i+0.5)-textBreite/2),
                             (float) (hoehe-schriftGroesse*3.1));
      template.showText(wochenTage[i]);
    }

    //Tage im Monat
    zeile = zeilen;
    spalte = ersterWochentag;
    for (int i=1; i <= tageInMonat; i++) {
      String text = Integer.toString(i);
      float textBreite =
        PdfDokumentEinstellungen.schriftNormal.getWidthPoint(text, schriftGroesse / 2);

      template.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, schriftGroesse / 2);
      template.setTextMatrix((spalte+1f/6f)*zellenBreite-textBreite/2,
        (zeile-1f/6f)*zellenBreite-schriftGroesse/4+schriftGroesse/16);
      template.showText(text);

      kalender.set(Calendar.DAY_OF_MONTH, i);
      text = hashtable.get(hashtableFormat.format(kalender.getTime()));
      if (text != null) {
        textBreite =
          PdfDokumentEinstellungen.schriftNormal.getWidthPoint(text, schriftGroesse);
        float skalierung = 1;
        if (textBreite > zellenBreite-4) {
          skalierung = (zellenBreite-4) / textBreite;
          textBreite *= skalierung;
        }

        template.setHorizontalScaling(skalierung*100);
        template.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, schriftGroesse);
        template.setTextMatrix((spalte+0.5f)*zellenBreite-textBreite/2,
          (zeile-1)*zellenBreite+3);
        template.showText(text);
        template.setHorizontalScaling(100);
      }

      spalte++; if (spalte == 7) {spalte = 0; zeile--;}
    }
    template.endText();

    return template;
  }

  public boolean hasNextTemplate() {
    return currentMonat < 0 || currentJahr < maxJahr || 
      (currentJahr == maxJahr && currentMonat <= maxMonat);
  }

  public PdfTemplateBeschreibung getNextTemplate(float optimaleLaenge, float maximaleLaenge, float erlaubteAbweichung, float standardAbstand, PdfContentByte parentPdf) throws Exception {
    //Noch nicht initialisiert
    if (currentMonat < 0) {
      Date min = startDatum;
      if (min == null) min = minDatum;
      if (min == null) min = new Date();
      
      Date max = endeDatum;
      if (max == null) max = maxDatum;
      if (max == null) max = new Date();

    
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(min);
      currentMonat = calendar.get(Calendar.MONTH)+1;
      currentJahr = calendar.get(Calendar.YEAR);

      calendar.setTime(max);
      maxMonat = calendar.get(Calendar.MONTH)+1;
      maxJahr = calendar.get(Calendar.YEAR);      
    }
      
    PdfTemplate template = getKalenderTemplate(currentMonat, currentJahr, parentPdf);    
    
    currentMonat++;
    if (currentMonat==13) {
      currentJahr++;
      currentMonat=1;
    }
    
    return new PdfTemplateBeschreibung(template, standardAbstand);
  }
}