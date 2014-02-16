package de.oberbrechen.koeb.pdf.pdfMahnungsListe;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfTabelle.PdfTabelle;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokument;
import de.oberbrechen.koeb.pdf.pdfTemplateDokument.PdfTemplateDokumentFactory;

/**
 * Erstellt ein PdfDokument, das eine Liste aller aktuellen Mahnungen
 * enthaelt.
 */
public class PdfMahnungsListeFactory extends PdfTemplateDokumentFactory {
  
  int mindestTageUeberzogen;
  String titel;
  
  public PdfMahnungsListeFactory(int mindestTageUeberzogen, String titel) {
    this.mindestTageUeberzogen = mindestTageUeberzogen;
    this.titel = titel;
  }
  
  
  public PdfDokument createPdfDokument() throws Exception {
    PdfTemplateDokument result = createLeeresPdfTemplateDokument(titel);
    result.addHauptUeberschrift(titel, null, null);
        
    MahnungFactory mahnungFactory = Datenbank.getInstance().getMahnungFactory();
    BenutzerListe benutzerListe = mahnungFactory.getAlleBenutzerMitMahnung();
    benutzerListe.setSortierung(BenutzerListe.NachnameVornameSortierung);
    for (int i = 0; i < benutzerListe.size(); i++) {
      Mahnung mahnung = mahnungFactory.erstelleMahnungFuerBenutzer(
          (Benutzer) benutzerListe.get(i));
      if (mahnung.getMaxUeberzogeneTage() > mindestTageUeberzogen) {
        addMahnungsTemplateFactories(mahnung, result);
      }
    }
    
    return result;
  }

  private void addMahnungsTemplateFactories(Mahnung mahnung, PdfTemplateDokument result) throws Exception {
    
    StringBuffer ueberschrift = new StringBuffer();
    Benutzer benutzer = mahnung.getBenutzer();
    ueberschrift.append(benutzer.getNameFormal());
    if (benutzer.getEMail() != null || benutzer.getTel() != null) {
      ueberschrift.append(" (");
      if (benutzer.getTel() != null) {
        ueberschrift.append("Tel.: ").append(benutzer.getTel());
        if (benutzer.getEMail() != null) {
          ueberschrift.append(", ");
        }        
      }
      
      if (benutzer.getEMail() != null) {
        ueberschrift.append("eMail: ").append(benutzer.getEMail());
      }
      
      ueberschrift.append(")");       
    }

    result.addUnterUeberschrift(ueberschrift.toString(), null, null);
        
    MahnungTabellenModell tabellenModell = new MahnungTabellenModell(mahnung);      
    PdfTabelle tabelle = new PdfTabelle(tabellenModell);
    result.addPdfTemplateFactory(tabelle);
  }
}
