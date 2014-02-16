package de.oberbrechen.koeb.pdf.pdfAufkleber;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.AbstractMedienAusgabe;
import de.oberbrechen.koeb.ausgaben.KonfigurierbareAusgabe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfAufkleber.AbstractPdfAufkleber;

class PdfMedienaufkleberMedienAusgabe extends AbstractMedienAusgabe 
  implements KonfigurierbareAusgabe {  
    protected boolean klein;
    protected int startPos = 1;
    protected PdfMedienaufkleberMedienAusgabeKonfigDialog konfigDialog = null;

    public PdfMedienaufkleberMedienAusgabe(boolean klein) {
      this.klein = klein;   
    }
    
    private void initKonfigDialog() {
      int aufkleberAnzahl;
      if (klein)
        aufkleberAnzahl = 48;
      else
        aufkleberAnzahl = 24;
      konfigDialog = new PdfMedienaufkleberMedienAusgabeKonfigDialog(aufkleberAnzahl);      
    }
    
    public String getName() {
      String erg = klein?" - Klein":""; //$NON-NLS-1$ //$NON-NLS-2$
      return "Medienaufkleber"+erg; //$NON-NLS-1$
    }

    public String getBeschreibung() {
      return "Ermöglicht die Ausgabe von Medienaufklebern."; //$NON-NLS-1$
    }

    protected PdfDokument getPdfDokument() throws Exception {      
      AbstractPdfAufkleber pdfAufkleber;
      if (klein) { 
        pdfAufkleber = new PdfMedienaufkleberKlein(getSortierteMedienliste());
      } else {
        pdfAufkleber = new PdfMedienaufkleber(getSortierteMedienliste());
      }
      pdfAufkleber.setStartPos(startPos);
      return pdfAufkleber;      
    }    
    
    public void konfiguriere(JFrame main) {
      if (konfigDialog == null) initKonfigDialog();
      konfigDialog.show(main);
      setSortierung(konfigDialog.getSortierung(), konfigDialog.umgekehrteSortierung);
      startPos = konfigDialog.getStartPos();
    }

    public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
      getPdfDokument().zeige(false);
    }
  
    public boolean istSpeicherbar() {
      return true;
    }
  
    public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
      getPdfDokument().schreibeInDatei(datei);
    }
    
    public String getStandardErweiterung() {
      return "pdf"; //$NON-NLS-1$
    }      
    
    public boolean benoetigtGUI() {
      return false;
    }                
}
