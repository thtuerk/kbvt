package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.ausgaben.PdfSpeicherbareAusgabe;
import de.oberbrechen.koeb.ausgaben.TeilnehmerlisteAusgabenFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltung;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsgruppenListe;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungsteilnahmeListe;
import de.oberbrechen.koeb.pdf.PdfDokument;
import de.oberbrechen.koeb.pdf.pdfBenutzerListe.PdfBenutzerListeFactory;

/**
* Diese Klasse stellt eine Factory da, die
* eine Ausgabe erstellt, die eine Pdf-Teilnehmerliste 
* ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class PdfTeilnehmerlisteAusgabenFactory implements AusgabenFactory, TeilnehmerlisteAusgabenFactory {

  class GesamtlisteAusgabe extends PdfSpeicherbareAusgabe {
    private Veranstaltungsgruppe gruppe;
    private boolean zeigeBemerkungen;
    private boolean zeigeZusammenfassung;
    private boolean querFormat;
    private boolean detailliert;
    
    public GesamtlisteAusgabe(Veranstaltungsgruppe gruppe, 
        boolean zeigeZusammenfassung, boolean zeigeBemerkungen, boolean querFormat, boolean detailliert) {
      this.gruppe = gruppe;
      this.zeigeBemerkungen = zeigeBemerkungen;
      this.zeigeZusammenfassung = zeigeZusammenfassung;
      this.querFormat = querFormat;
      this.detailliert = detailliert;
    }

    public String getName() {
      StringBuffer result = new StringBuffer();
      result.append("Gesamtliste - ");
      result.append(gruppe.getName());
      if (detailliert) {
        result.append(" - DETAILLIERT ");
      }
      if (querFormat) {
        result.append(" - QUER");
      }
      
      return result.toString();
    }

    public String getBeschreibung() {
      return "Erstellt eine PDF-Teilnehmerliste der Veranstaltungsgruppe '"+
          gruppe.toString()+"'!";
    }

    protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
      if (!detailliert) {
        PdfTeilnehmerListeVeranstaltungsgruppeFactory factory =
          new PdfTeilnehmerListeVeranstaltungsgruppeFactory(gruppe);
        
        factory.setZeigeBemerkungen(zeigeBemerkungen);
        factory.setZeigeZusammenfassung(zeigeZusammenfassung);
        factory.setQuerformat(querFormat);
        return factory.createPdfDokument();
      } else {
        BenutzerListe liste =
            Datenbank.getInstance().getVeranstaltungsteilnahmeFactory().getTeilnehmerListe(
                gruppe);
        PdfBenutzerListeFactory factory = new PdfBenutzerListeFactory(liste);
        factory.setQuerformat(querFormat);
        factory.setTitel("Teilnehmerliste - "+gruppe.getName());
        return factory.createPdfDokument();
      }
    }
    
    public boolean benoetigtGUI() {
      return false;
    }                
  }

  class TeilnehmerUebersichtAusgabe extends PdfSpeicherbareAusgabe {
    private Veranstaltungsgruppe gruppe;
  
    public TeilnehmerUebersichtAusgabe(Veranstaltungsgruppe gruppe) {
      this.gruppe = gruppe;
    }
  
    public String getName() {
      return "Teilnehmerübersicht - "+gruppe.toString();
    }
  
    public String getBeschreibung() {
      return "Erstellt eine Übersicht über die Teilnehmer an Veranstaltungen " +
          "der Veranstaltungsgruppe '"+gruppe.toString()+"'!";
    }
  
    protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
      PdfTeilnehmerUebersichtVeranstaltungsgruppeFactory factory = new
        PdfTeilnehmerUebersichtVeranstaltungsgruppeFactory(gruppe);
      return factory.createPdfDokument();
    }
    
    public boolean benoetigtGUI() {
      return false;
    }                
  }

  class VeranstaltungsUebersichtAusgabe extends PdfSpeicherbareAusgabe {
    private Veranstaltungsgruppe gruppe;
  
    public VeranstaltungsUebersichtAusgabe(Veranstaltungsgruppe gruppe) {
      this.gruppe = gruppe;
    }
  
    public String getName() {
      return "Veranstaltungsübersicht - "+gruppe.toString();
    }
  
    public String getBeschreibung() {
      return "Erstellt eine Übersicht über die Veranstaltungen der Veranstaltungsgruppe '"+
        gruppe.toString()+"'!";
    }
  
    protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
      PdfVeranstaltungsUebersichtVeranstaltungsgruppeFactory factory = 
        new PdfVeranstaltungsUebersichtVeranstaltungsgruppeFactory(gruppe);      
      return factory.createPdfDokument();
    }
    
    public boolean benoetigtGUI() {
      return false;
    }                
  }

  class VeranstaltungslisteAusgabe extends PdfSpeicherbareAusgabe {
    private Veranstaltung veranstaltung;
    private int sortierung;
    private boolean zeigeBemerkungen;
    private boolean zeigeBeschreibung;

    public VeranstaltungslisteAusgabe(Veranstaltung veranstaltung,
                              int sortierung, boolean zeigeBemerkungen, 
                              boolean zeigeBeschreibung) {
      this.veranstaltung = veranstaltung;
      this.sortierung = sortierung;
      this.zeigeBemerkungen = zeigeBemerkungen;
      this.zeigeBeschreibung= zeigeBeschreibung;
    }

    public String getName() {
      return "Teilnehmerliste - "+veranstaltung.getTitel();
    }

    public String getBeschreibung() {
      String sortString = null;
      switch (sortierung) {
        case VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung:
          sortString = "dem Namen der Teilnehmer";
          break;
        case VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung:
          sortString = "der Anmeldenummer";
          break;
        case VeranstaltungsteilnahmeListe.BemerkungenSortierung:
          sortString = "den Bemerkungen";
          break;
        case VeranstaltungsteilnahmeListe.BenutzerKlasseSortierung:
          sortString = "der Klasse der Teilnehmer";
          break;
      }
      return "Erstellt eine nach "+sortString+" sortierte "+
          "PDF-Teilnehmerliste der Veranstaltung '"+
          veranstaltung.getTitel()+"' der Veranstaltunsgruppe '"+
          veranstaltung.getVeranstaltungsgruppe().getName()+"'!";
    }

    protected PdfDokument getPdfDokument(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
      PdfTeilnehmerListeVeranstaltungFactory factory =
        new PdfTeilnehmerListeVeranstaltungFactory(veranstaltung);
      factory.setSortierung(sortierung, false);
      factory.setZeigeBemerkungen(zeigeBemerkungen);
      factory.setZeigeBeschreibung(zeigeBeschreibung);
      return factory.createPdfDokument();
    }
    
    public boolean benoetigtGUI() {
      return false;
    }                
  }



  // Hauptklasse
  public Ausgabe createTeilnehmerlisteVeranstaltungAusgabe(
    Veranstaltung veranstaltung, int sortierung, 
    boolean zeigeBemerkungen, boolean zeigeBeschreibung) {
    return new VeranstaltungslisteAusgabe(veranstaltung, sortierung,
        zeigeBemerkungen, zeigeBeschreibung);
  }

  public Ausgabe createTeilnehmerlisteVeranstaltungsgruppeAusgabe(
      Veranstaltungsgruppe veranstaltungsgruppe, boolean zeigeBemerkungen, boolean querFormat,
      boolean detailliert) {
     
    return new GesamtlisteAusgabe(veranstaltungsgruppe, false, zeigeBemerkungen, querFormat, detailliert);
  }

  public Ausgabe createTeilnehmerlisteVeranstaltungsgruppeAusgabe(
      Veranstaltungsgruppe veranstaltungsgruppe, boolean zeigeBemerkungen) {
    
    return new GesamtlisteAusgabe(veranstaltungsgruppe, false, zeigeBemerkungen, false, false);
  }
  
    
  public Ausgabe createTeilnehmerUebersichtVeranstaltungsgruppeAusgabe(
    Veranstaltungsgruppe veranstaltungsgruppe) {
    
    return new TeilnehmerUebersichtAusgabe(veranstaltungsgruppe);
  }
       
  public Ausgabe createVeranstaltungsUebersichtVeranstaltungsgruppeAusgabe(
    Veranstaltungsgruppe veranstaltungsgruppe) {
      
    return new VeranstaltungsUebersichtAusgabe(veranstaltungsgruppe);
  }

  public String getName() {
    return "PDF-Teilnehmerlisten";
  }

  public String getBeschreibung() {
    return "Erstellt Teilnehmerlisten für alle Veranstaltungen!";
  }

  public void setParameter(String name, String wert) throws ParameterException {
    throw ParameterException.keineParameter;
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) throws InterruptedException {
    final AusgabenTreeKnoten root = knoten.addKnoten("Veranstaltungen", false);
    final VeranstaltungsgruppenListe veranstaltungsgruppen =
      Datenbank.getInstance().getVeranstaltungsgruppeFactory().
      getAlleVeranstaltungsgruppen();
    veranstaltungsgruppen.setSortierung(
        VeranstaltungenListe.AlphabetischeSortierung, true);
    
    Thread[] threads = new Thread[veranstaltungsgruppen.size()];
    for (int i=0; i < threads.length; i++) {
      final Veranstaltungsgruppe veranstaltungsgruppe =
        (Veranstaltungsgruppe) veranstaltungsgruppen.get(i);
      threads[i] = new Thread(new Runnable() {        
        public void run() {
          addVeranstaltungsgruppeAusgaben(root, veranstaltungsgruppe);      
        }          
      });
      threads[i].run();
    }
    
    for (int i=0; i < threads.length; i++) {
      threads[i].join();
    }
  }
  
  private void addVeranstaltungsgruppeAusgaben(AusgabenTreeKnoten root, 
    Veranstaltungsgruppe gruppe) {
    
    AusgabenTreeKnoten knoten = root.addKnoten(gruppe.getName(), true);
    int teilnehmerAnzahl = Datenbank.getInstance().
      getVeranstaltungsteilnahmeFactory().getTeilnehmerAnzahl(gruppe);
    knoten.addAusgabe("Gesamtliste ("+teilnehmerAnzahl+")", 
      createTeilnehmerlisteVeranstaltungsgruppeAusgabe(gruppe, true, false, false));
    knoten.addAusgabe("Gesamtliste - Querformat ("+teilnehmerAnzahl+")", 
        createTeilnehmerlisteVeranstaltungsgruppeAusgabe(gruppe, true, true, false));
    knoten.addAusgabe("detaillierte Teilnehmerliste ("+teilnehmerAnzahl+")", 
        createTeilnehmerlisteVeranstaltungsgruppeAusgabe(gruppe, true, false, true));
    knoten.addAusgabe("Teilnehmerübersicht", 
        createTeilnehmerUebersichtVeranstaltungsgruppeAusgabe(gruppe));
    knoten.addAusgabe("Veranstaltungsübersicht", 
        createVeranstaltungsUebersichtVeranstaltungsgruppeAusgabe(gruppe));
  
    VeranstaltungenListe veranstaltungen = Datenbank.getInstance().
      getVeranstaltungFactory().getVeranstaltungenMitAnmeldung(gruppe); 
    veranstaltungen.setSortierung(
        VeranstaltungenListe.AlphabetischeSortierung, true);
    int[] teilnehmerAnzahlen = Datenbank.getInstance().
      getVeranstaltungsteilnahmeFactory().getTeilnehmerAnzahl(veranstaltungen);
    
    AusgabenTreeKnoten bemerkungenKnoten = knoten.addKnoten("mit Bemerkungen", true);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Anmeldenummer", VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung, true);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Bemerkungen", VeranstaltungsteilnahmeListe.BemerkungenSortierung, true);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Klasse", VeranstaltungsteilnahmeListe.BenutzerKlasseSortierung, true);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Name", VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung, true);
    
    bemerkungenKnoten = knoten.addKnoten("ohne Bemerkungen", true);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Anmeldenummer", VeranstaltungsteilnahmeListe.AnmeldeDatumSortierung, false);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Bemerkungen", VeranstaltungsteilnahmeListe.BemerkungenSortierung, false);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Klasse", VeranstaltungsteilnahmeListe.BenutzerKlasseSortierung, false);
    addVeranstaltungsAusgaben(bemerkungenKnoten, veranstaltungen, teilnehmerAnzahlen, "Name", VeranstaltungsteilnahmeListe.BenutzerNachnameVornameSortierung, false);        
  }
  

private void addVeranstaltungsAusgaben(AusgabenTreeKnoten root, 
    VeranstaltungenListe veranstaltungen, int[] teilnehmerAnzahlen, String sortierungName, int sortierung, boolean zeigeBemerkungen) {
    AusgabenTreeKnoten sortKnoten = root.addKnoten(sortierungName, true);
    for (int i=0; i < veranstaltungen.size(); i++) {
      Veranstaltung veranstaltung = (Veranstaltung) veranstaltungen.get(i);
      sortKnoten.addAusgabe(veranstaltung.getTitel()+" ("+
        teilnehmerAnzahlen[i]+")",         
        createTeilnehmerlisteVeranstaltungAusgabe(
        veranstaltung, sortierung, zeigeBemerkungen, true));
    }
  }

}