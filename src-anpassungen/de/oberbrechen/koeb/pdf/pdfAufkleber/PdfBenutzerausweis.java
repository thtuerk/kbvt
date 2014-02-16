package de.oberbrechen.koeb.pdf.pdfAufkleber;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EANZuordnung;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.components.PdfEANBarcode;
import de.oberbrechen.koeb.pdf.pdfAufkleber.AbstractPdfAufkleber;
import de.oberbrechen.koeb.pdf.pdfAufkleber.PdfAufkleber79x48;

/**
 * Diese Klasse dient dazu, Leserausweise im Format 79 mm mal 48 mm auszugeben. 
 * Dieses Format entspricht in etwa Scheckkartengröße, abgerechnet einem
 * Rand von 3 mm an allen Seiten. Dieser Rand kommt zum Beispiel durch
 * Laminieren wieder hinzu, so dass die laminierten Ausweise Scheckkartengröße
 * besitzen. Gerade Leserausweise sind stark von der jeweiligen Bücherei
 * abhängig. Diese Klasse erstellt Leserausweise für die KöB Oberbrechen.
 * Sie ist auch als Vorlage für andere Büchereien gedacht, ihre eigenen
 * Ausweis-Klassen zu erstellen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfBenutzerausweis extends PdfAufkleber79x48 {
     
  BenutzerListe benutzerListe;
  PdfTemplate allgemeinTemplate;
  PdfTemplate rueckseiteTemplate;
  EANZuordnung eanZuordnung;
    
  public PdfBenutzerausweis(BenutzerListe benutzerliste) {
    eanZuordnung = Datenbank.getInstance().getEANZuordnung();
    this.benutzerListe = benutzerliste;
    setSchneideMarkierungen(AbstractPdfAufkleber.SCHNEIDEMARKIERUNG_ECKEN);
  }
  
  protected int getAufkleberAnzahl() {
    return benutzerListe.size();
  }

  protected PdfTemplate getAufkleberRueckSeite(int nr, PdfContentByte pdf) throws MalformedURLException, IOException, DocumentException {
    if (rueckseiteTemplate != null) return rueckseiteTemplate;
    
    rueckseiteTemplate = pdf.createTemplate(224, 136);

    //Logo
    URL logoURL = ClassLoader.getSystemResource(
      "de/oberbrechen/koeb/pdf/pdfAufkleber/logoKlein.png"); //$NON-NLS-1$
    Image logo = Image.getInstance(logoURL);
    logo.scalePercent(37/logo.height()*100);
    logo.setAbsolutePosition(210-logo.plainWidth(), 85);
    rueckseiteTemplate.addImage(logo);
    
    //Text neben Logo
    rueckseiteTemplate.beginText();
    rueckseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftFett, 7);
    rueckseiteTemplate.setTextMatrix(14, 115);
    rueckseiteTemplate.showText("KöB Oberbrechen"); //$NON-NLS-1$
    rueckseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, 7);    
    rueckseiteTemplate.setTextMatrix(14, 105);
    rueckseiteTemplate.showText("in der \"Alten Schule\""); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(14, 95);
    rueckseiteTemplate.showText("Frankfurter Str. 31 (Eingang vom Hof aus)"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(14, 85);
    rueckseiteTemplate.showText("65611 Brechen"); //$NON-NLS-1$
            
    rueckseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftFett, 7);
    rueckseiteTemplate.setTextMatrix(14, 60);
    rueckseiteTemplate.showText("Tel. / Fax:"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(14, 50);
    rueckseiteTemplate.showText("eMail:"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(14, 40);
    rueckseiteTemplate.showText("Homepage:"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(14, 30);
    rueckseiteTemplate.showText("Öffnungszeiten:"); //$NON-NLS-1$

    rueckseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, 7);    
    rueckseiteTemplate.setTextMatrix(75, 60);
    rueckseiteTemplate.showText("06483/806524"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(75, 50);
    rueckseiteTemplate.showText("buecherei@koeb-oberbrechen.de"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(75, 40);
    rueckseiteTemplate.showText("www.koeb-oberbrechen.de"); //$NON-NLS-1$

    rueckseiteTemplate.setTextMatrix(75, 30);
    rueckseiteTemplate.showText("sonntags 11:00 - 12:00 Uhr"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(75, 22);
    rueckseiteTemplate.showText("mittwochs 17:30 - 20:00 Uhr"); //$NON-NLS-1$
    rueckseiteTemplate.setTextMatrix(75, 14);
    rueckseiteTemplate.showText("Schulferien: mittwochs 18:30 - 20:00 Uhr"); //$NON-NLS-1$
    rueckseiteTemplate.endText();

    return rueckseiteTemplate;
  }

  protected PdfTemplate getAufkleberVorderSeite(int nr, PdfContentByte pdf) throws DocumentException, BadElementException, MalformedURLException, IOException {
    
    if (allgemeinTemplate == null) {
      allgemeinTemplate = pdf.createTemplate(224, 136);
      
      //Überschrift
      allgemeinTemplate.beginText();
      float textBreite = PdfDokumentEinstellungen.schriftFett.getWidthPoint("Leserausweis", 20); //$NON-NLS-1$
      allgemeinTemplate.setHorizontalScaling(100*140/textBreite);
      allgemeinTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftFett, 20);    
      allgemeinTemplate.setTextMatrix(14, 106);    
      allgemeinTemplate.showText("Leserausweis"); //$NON-NLS-1$
      allgemeinTemplate.endText();
      
      //Logo
      URL logoURL = ClassLoader.getSystemResource(
        "de/oberbrechen/koeb/pdf/pdfAufkleber/logo.png"); //$NON-NLS-1$
      Image logo = Image.getInstance(logoURL);
      logo.scaleToFit(120, 66);
      logo.setAbsolutePosition(84-(logo.plainWidth()/2), 65-(logo.plainHeight()/2));
      allgemeinTemplate.addImage(logo);
      
      //Rahmen für Benutzernamen
      allgemeinTemplate.setGrayFill(0.75f);
      allgemeinTemplate.setLineWidth(0.7f);
      allgemeinTemplate.rectangle(14, 14, 140, 14);
      allgemeinTemplate.fillStroke();
      allgemeinTemplate.setGrayFill(0);
    }
    
    Benutzer currentBenutzer = (Benutzer) benutzerListe.get(nr-1);
    
    PdfTemplate vorderseiteTemplate = pdf.createTemplate(224, 136);
    vorderseiteTemplate.addTemplate(allgemeinTemplate, 0, 0);
    
    //Benutzername
    vorderseiteTemplate.beginText();
    vorderseiteTemplate.setHorizontalScaling(100);
    float textBreite = PdfDokumentEinstellungen.schriftFett.getWidthPoint(
      currentBenutzer.getName(), 10);
    if (textBreite > 130) {
      vorderseiteTemplate.setHorizontalScaling(13000f/textBreite);
      textBreite = 130;
    }

    vorderseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftFett, 10);    
    vorderseiteTemplate.setTextMatrix(84-(textBreite/2), 17.5f);    
    vorderseiteTemplate.showText(currentBenutzer.getName());
    vorderseiteTemplate.endText();

    //Barcode    
    EAN benutzerEAN = eanZuordnung.getBenutzerEAN(currentBenutzer);
    PdfTemplate barcodeTemplate = PdfEANBarcode.getTemplate(benutzerEAN, pdf, 108, 42);
    vorderseiteTemplate.addTemplate(barcodeTemplate, 0, -1, 1, 0, 168, 122);
    
    return vorderseiteTemplate;
  }
}