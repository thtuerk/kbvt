package de.oberbrechen.koeb.pdf.pdfAufkleber;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.AbstractBenutzerAusgabe;
import de.oberbrechen.koeb.ausgaben.KonfigurierbareAusgabe;
import de.oberbrechen.koeb.pdf.PdfDokument;

/**
 * Dieses Klasse stellt eine Factory da, die
 * die eine Ausgabe erstellt, die ein PdfMedienaufkleber ausgibt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfBenutzerausweisBenutzerAusgabe extends AbstractBenutzerAusgabe 
  implements KonfigurierbareAusgabe {

  PdfBenutzerausweisBenutzerAusgabeKonfigDialog konfigDialog;
  
  public PdfBenutzerausweisBenutzerAusgabe() {
  }
  
  public String getName() {
    return "Benutzerausweise";
  }

  public String getBeschreibung() {
    return "Gibt Benutzerausweise aus!";
  }

  protected PdfDokument getPdfDokument() throws Exception {
    if (konfigDialog == null) 
      konfigDialog = new PdfBenutzerausweisBenutzerAusgabeKonfigDialog();
    
    PdfBenutzerausweis ausweis = new PdfBenutzerausweis(
        getSortierteBenutzerListe());
    ausweis.setStartPos(konfigDialog.getStartPos());
    ausweis.setPrintRueckseiten(konfigDialog.getZeigeRueckseite());
    return ausweis;
  }
  
  public void konfiguriere(JFrame main) {
    if (konfigDialog == null) 
      konfigDialog = new PdfBenutzerausweisBenutzerAusgabeKonfigDialog();

    konfigDialog.show(main);
    setSortierung(konfigDialog.getSortierung(), konfigDialog.umgekehrteSortierung);
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