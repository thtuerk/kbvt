package de.oberbrechen.koeb.pdf.pdfMedienListe;

import java.util.Iterator;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.MedientypListe;
import de.oberbrechen.koeb.gui.components.medienAusgabeWrapper.MedienAusgabeWrapper;
import de.oberbrechen.koeb.pdf.PdfDokument;

/**
 * Dieses Klasse stellt eine Factory da, die
 * die eine Ausgabe erstellt, die eine PdfMedienliste ausgibt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfMedienlisteAusgabenFactory implements AusgabenFactory {

  public String getName() {
    return "PDF-Medienlisten";
  }

  public String getBeschreibung() {
    return "Erzeugt einen Ordner 'Medienlisten', der die 3 Unterordner " +      "'Titel', 'Mediennummer' und 'Autor' enthält. Jeder dieser Ordner " +      "enthält Ausgaben, die eine nach dem entsprechenden Kriterium sortierte " +      "PDF-Medienliste aller Medien sowie PDF-Medienlisten aller Medien eines" +      "Medientyps erstellen.";
  }

  public void setParameter(String name, String wert) throws ParameterException {
    throw ParameterException.keineParameter;
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) {
    AusgabenTreeKnoten root = knoten.addKnoten("Medienlisten", true);

    AusgabenTreeKnoten sortierungNode = root.addKnoten("Titel", false);    
    addAusgabenFuerSortierung(sortierungNode, MedienListe.TitelAutorSortierung);

    sortierungNode = root.addKnoten("Autor", false);    
    addAusgabenFuerSortierung(sortierungNode, MedienListe.AutorTitelSortierung);

    sortierungNode = root.addKnoten("Mediennummer", false);    
    addAusgabenFuerSortierung(sortierungNode, MedienListe.MedienNummerSortierung);
    
    //Gesamtliste
    PdfMedienlisteMedienAusgabe konfigurierbareAusgabe = 
      new PdfMedienlisteMedienAusgabe() {
      
      public String getName() {
        return "konfigurierbare Medienliste";
      }
      
      public String getBeschreibung() {
        return "Ermöglicht die Erstellung einer Medienliste, die vorher " +
            "konfiguriert werden kann.";        
      }
      
    };

    PdfMedienlisteMedienAusgabeDetailliert konfigurierbareAusgabeDetailliert = 
      new PdfMedienlisteMedienAusgabeDetailliert() {
      
      public String getName() {
        return "konfigurierbare Medienliste - Detailliert";
      }
      
      public String getBeschreibung() {
        return "Ermöglicht die Erstellung einer detaillierten Medienliste, die vorher " +
            "konfiguriert werden kann.";        
      }      
    };
    
    
    root.addAusgabe("konfigurierbare Medienliste", 
        new MedienAusgabeWrapper(konfigurierbareAusgabe));
    
    root.addAusgabe("konfigurierbare Medienliste-Detailliert", 
        new MedienAusgabeWrapper(konfigurierbareAusgabeDetailliert));
    
  }

  private void addAusgabenFuerSortierung(AusgabenTreeKnoten root, int sortierung) {
    String sortierungName = "";
    switch (sortierung) {
      case MedienListe.MedienNummerSortierung: 
        sortierungName = "Mediennummer"; break;
      case MedienListe.TitelAutorSortierung:
        sortierungName = "Titel"; break;
      case MedienListe.AutorTitelSortierung:
        sortierungName = "Autor"; break;      
    }

    //Gesamtliste
    Ausgabe gesamtAusgabe = new MedientyplisteAusgabe(
      "Gesamtliste", "Erstellt eine PDF-Datei, die eine Gesamtliste aller aktuellen Medien enthaelt "+
      " - sortiert nach "+sortierungName, "Gesamtliste - "+sortierungName,
      sortierung, null, false);

    root.addAusgabe(gesamtAusgabe);

    Ausgabe gesamtAusgabeDetailliert = new MedientyplisteAusgabe(
        "Gesamtliste - Detailliert", "Erstellt eine PDF-Datei, die eine detaillierte Gesamtliste aller aktuellen Medien enthaelt "+
        " - sortiert nach "+sortierungName, "Gesamtliste - "+sortierungName,
        sortierung, null, true);
    root.addAusgabe(gesamtAusgabeDetailliert);

    //Medientyplisten
    MedientypFactory medientypFactory = 
      Datenbank.getInstance().getMedientypFactory();
    MedientypListe medientypListe = medientypFactory.getAlleMedientypen();
    medientypListe.setSortierung(MedientypListe.StringSortierung);
    Iterator<Medientyp> it = medientypListe.iterator();
    while (it.hasNext()) {
      Medientyp medientyp = it.next();
      Ausgabe ausgabe = new MedientyplisteAusgabe(medientyp.getPlural(),  
        "Eine nach "+sortierungName+" sortierte PDF-Liste aller "+
        medientyp.getPlural()+".", medientyp.getPlural()+" - "+sortierungName, 
        sortierung, medientyp, false);
      root.addAusgabe(ausgabe);
      
      Ausgabe ausgabeDetailliert = new MedientyplisteAusgabe(medientyp.getPlural()+" - Detailliert",  
          "Eine nach "+sortierungName+" sortierte detaillierte PDF-Liste aller "+
          medientyp.getPlural()+".", medientyp.getPlural()+" - "+sortierungName, 
          sortierung, medientyp, true);
        root.addAusgabe(ausgabeDetailliert);      
    }
  }
}


class MedientyplisteAusgabe extends PdfSpeicherbareAusgabe {

  private int sortierung;
  private String name;
  private String titel;
  private String beschreibung;
  private Medientyp medientyp;
  private boolean detailliert;
  
  public MedientyplisteAusgabe(String name, 
      String beschreibung, String titel, int sortierung, 
      Medientyp medientyp, boolean detailliert) {
    
    this.sortierung = sortierung;
    this.name = name; 
    this.titel = titel;
    this.beschreibung = beschreibung;
    this.medientyp = medientyp;
    this.detailliert = detailliert;
  }

  public String getName() {
    return name;
  }

  public String getBeschreibung() {
    return beschreibung;
  }
  
  protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
    MediumFactory mediumFactory = Datenbank.getInstance().getMediumFactory();   
    MedienListe medienliste = 
      mediumFactory.getMedienListe(medientyp, null, false);
    
    PdfDokument pdfDokument = null;
    if (detailliert) {
      PdfMedienListeDetailliertFactory pdfMedienListeDetailliertFactory = 
        new PdfMedienListeDetailliertFactory(medienliste);
      pdfMedienListeDetailliertFactory.setTitel(titel);
      pdfMedienListeDetailliertFactory.setSortierung(sortierung, false);
      pdfDokument = pdfMedienListeDetailliertFactory.createPdfDokument();
    } else {
      PdfMedienListeFactory pdfMedienListeFactory = 
        new PdfMedienListeFactory(medienliste);
      pdfMedienListeFactory.setTitel(titel);
      pdfMedienListeFactory.setSortierung(sortierung, false);
      pdfDokument = pdfMedienListeFactory.createPdfDokument();      
    }
    return pdfDokument;
  }
  
  public boolean benoetigtGUI() {
    return false;
  }              
}
