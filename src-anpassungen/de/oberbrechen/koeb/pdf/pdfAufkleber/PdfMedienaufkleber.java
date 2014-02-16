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
import de.oberbrechen.koeb.pdf.pdfAufkleber.PdfAufkleber70x36;

/**
 * Diese Klasse erstellt Medienaufkleber.
 *
 * @author Thomas Tï¿½rk (t_tuerk@gmx.de)
 */
public class PdfMedienaufkleber extends PdfAufkleber70x36 {
     
  private float logoHeight;
  MedienListe medienListe;
  PdfTemplate allgemeinTemplate;
  EANZuordnung eanZuordnung = Datenbank.getInstance().getEANZuordnung();
  
  public PdfMedienaufkleber(MedienListe medienliste) {
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
      allgemeinTemplate = pdf.createTemplate(198.3333f, 102.5f);
            
      //Logo
      URL logoURL = ClassLoader.getSystemResource(
        "de/oberbrechen/koeb/pdf/pdfAufkleber/logo.png"); //$NON-NLS-1$
      Image logo = Image.getInstance(logoURL);
      logo.scalePercent(8000/logo.width());     
      logoHeight = logo.plainHeight();
      logo.setAbsolutePosition(14, 88.5f-logo.plainHeight());
      allgemeinTemplate.addImage(logo);
    }
    
    Medium currentMedium = medienListe.get(nr-1);
    
    PdfTemplate vorderseiteTemplate = pdf.createTemplate(198.3333f, 102.5f);
    vorderseiteTemplate.addTemplate(allgemeinTemplate, 0, 0);
    
    //Barcode
    EAN mediumEAN = eanZuordnung.getMediumEAN(currentMedium);
    PdfTemplate barcodeTemplate = PdfEANBarcode.getTemplate(mediumEAN, pdf, 85, logoHeight);
    vorderseiteTemplate.addTemplate(barcodeTemplate, 99, 88.5f-logoHeight);
  
    //Medienbeschreibung
    String titel = currentMedium.getTitel();
    if (titel == null || titel.equals("")) titel="-"; //$NON-NLS-1$ //$NON-NLS-2$
    String autor = currentMedium.getAutor();
    if (autor == null || autor.equals("")) autor="-"; //$NON-NLS-1$ //$NON-NLS-2$
    String medienNr = currentMedium.getMedienNr();

    float maxBreite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(titel, 8.5f);
    float breite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(autor, 8.5f);
    if (maxBreite < breite) maxBreite = breite;
    breite = PdfDokumentEinstellungen.schriftNormal.getWidthPoint(medienNr, 8.5f);
    if (maxBreite < breite) maxBreite = breite;   
    
    float labelAbstand = 50;
    maxBreite+=labelAbstand; //Abstand fï¿½r Labels
    
    float skalierung=1;
    if (maxBreite > 170) skalierung = 170/maxBreite; 
          
    vorderseiteTemplate.beginText();
    vorderseiteTemplate.setHorizontalScaling(skalierung*100);
    vorderseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftFett, 8.5f);
    vorderseiteTemplate.setTextMatrix(14, 38);
    vorderseiteTemplate.showText("Medien-Nr:"); //$NON-NLS-1$
    vorderseiteTemplate.setTextMatrix(14, 26);
    vorderseiteTemplate.showText("Titel:"); //$NON-NLS-1$
    vorderseiteTemplate.setTextMatrix(14, 14);
    vorderseiteTemplate.showText("Autor:"); //$NON-NLS-1$

    vorderseiteTemplate.setFontAndSize(PdfDokumentEinstellungen.schriftNormal, 8.5f);
    vorderseiteTemplate.setTextMatrix(14+labelAbstand*skalierung, 38);
    vorderseiteTemplate.showText(medienNr);
    vorderseiteTemplate.setTextMatrix(14+labelAbstand*skalierung, 26);
    vorderseiteTemplate.showText(titel);
    vorderseiteTemplate.setTextMatrix(14+labelAbstand*skalierung, 14);
    vorderseiteTemplate.showText(autor);
      
    vorderseiteTemplate.endText();
    return vorderseiteTemplate;
  }
}