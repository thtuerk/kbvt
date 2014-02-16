package de.oberbrechen.koeb.pdf.pdfAufkleber;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EANZuordnung;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.pdf.PdfDokumentEinstellungen;
import de.oberbrechen.koeb.pdf.components.PdfEANBarcode;
import de.oberbrechen.koeb.pdf.pdfAufkleber.AbstractPdfAufkleber;
import de.oberbrechen.koeb.pdf.pdfAufkleber.PdfAufkleber35x36;

/**
 * Diese Klasse erstellt kleine Medienaufkleber
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfMedienaufkleberKlein extends PdfAufkleber35x36 {
     
  float logoWidth;
  MedienListe medienListe;
  PdfTemplate allgemeinTemplate;
  EANZuordnung eanZuordnung;
  
  public PdfMedienaufkleberKlein(MedienListe medienliste) {
    eanZuordnung = Datenbank.getInstance().getEANZuordnung();
    this.medienListe = medienliste;
    setSchneideMarkierungen(AbstractPdfAufkleber.SCHNEIDEMARKIERUNG_ECKEN);
    setPrintRueckseiten(false);
  }
  
  protected int getAufkleberAnzahl() {
    return medienListe.size();
  }

  protected PdfTemplate getAufkleberRueckSeite(int nr, PdfContentByte pdf) {
    return null;
  }

  protected PdfTemplate getAufkleberVorderSeite(int nr, PdfContentByte pdf) throws DocumentException, BadElementException, MalformedURLException, IOException {
    if (allgemeinTemplate == null) {
      allgemeinTemplate = pdf.createTemplate(99f, 102.5f);
            
      //Logo
      URL logoURL = ClassLoader.getSystemResource(
        "de/oberbrechen/koeb/pdf/pdfAufkleber/logoKlein.png"); //$NON-NLS-1$
      Image logo = Image.getInstance(logoURL);
      logo.scalePercent(20/logo.height()*100);
      logoWidth = logo.plainWidth()+5;
      logo.setAbsolutePosition(55, 10);
      logo.setRotationDegrees(90);
      allgemeinTemplate.addImage(logo);
    }
    
    Medium currentMedium = (Medium) medienListe.get(nr-1);
    
    PdfTemplate vorderseiteTemplate = pdf.createTemplate(99, 102.5f);
    vorderseiteTemplate.addTemplate(allgemeinTemplate, 0, 0);
    
    //Barcode
    EAN mediumEAN = eanZuordnung.getMediumEAN(currentMedium);
    PdfTemplate barcodeTemplate = PdfEANBarcode.getTemplate(mediumEAN, pdf, 82.5f, 35);
    vorderseiteTemplate.addTemplate(barcodeTemplate, 0, 1, 1, 0, 14, 10);
  
    //Medienbeschreibung
    String titel = currentMedium.getTitel();
    if (titel == null || titel.equals("")) titel="-";  //$NON-NLS-1$//$NON-NLS-2$
    String autor = currentMedium.getAutor();
    if (autor == null || autor.equals("")) autor="-"; //$NON-NLS-1$ //$NON-NLS-2$
    String medienNr = currentMedium.getMedienNr();

    float maxBreite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(titel, 8.5f);
    float breite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(autor, 8.5f)+logoWidth;
    if (maxBreite < breite) maxBreite = breite;
    breite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(medienNr, 8.5f)+logoWidth;
    if (maxBreite < breite) maxBreite = breite;   
        
    float skalierung=1;
    if (maxBreite > 82.5f) skalierung = 82.5f/maxBreite; 
          
    vorderseiteTemplate.beginText();
    vorderseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, 8.5f);
    vorderseiteTemplate.setHorizontalScaling(skalierung*100);
    vorderseiteTemplate.setTextMatrix(0, 1, -1, 0, 63.5f, 10+logoWidth);
    vorderseiteTemplate.showText(medienNr);
    vorderseiteTemplate.setTextMatrix(0, 1, -1, 0, 73.5f, 10+logoWidth);
    vorderseiteTemplate.showText(autor);
    vorderseiteTemplate.setTextMatrix(0, 1, -1, 0, 83.5f, 10);
    vorderseiteTemplate.showText(titel);
      
    vorderseiteTemplate.endText();
    return vorderseiteTemplate;
  }
}