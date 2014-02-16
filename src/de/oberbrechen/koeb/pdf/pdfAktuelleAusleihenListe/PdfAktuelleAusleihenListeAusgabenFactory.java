package de.oberbrechen.koeb.pdf.pdfAktuelleAusleihenListe;

import java.io.File;

import javax.swing.JFrame;

import de.oberbrechen.koeb.ausgaben.AbstractBenutzerAusgabe;
import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.BenutzerAusgabe;
import de.oberbrechen.koeb.ausgaben.BenutzerAusgabeFactory;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.gui.components.benutzerAusgabeWrapper.BenutzerAusgabeWrapper;

/**
* Diese Factory erstellt eine Ausgabe, die eine PDF-Liste aller 
* Mahnungen ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class PdfAktuelleAusleihenListeAusgabenFactory implements 
  BenutzerAusgabeFactory, AusgabenFactory {

  public BenutzerAusgabe createBenutzerAusgabe() {
    return new AbstractBenutzerAusgabe() {
      public java.lang.String getName() {
        return "Aktuelle Ausleihen";
      }

      public java.lang.String getBeschreibung() {
        return "Liefert eine Liste der aktuellen Ausleihen der ausgewählten"+
          "Benutzer!";
      }

      public void run(JFrame hauptFenster, boolean zeigeDialog) throws Exception {
        new PdfAktuelleAusleihenListeFactory(getSortierteBenutzerListe()).createPdfDokument().zeige(false);
      }
    
      public boolean istSpeicherbar() {
        return true;
      }
    
      public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog, File datei) throws Exception {
        new PdfAktuelleAusleihenListeFactory(getSortierteBenutzerListe()).createPdfDokument().schreibeInDatei(datei);
      }
      
      public String getStandardErweiterung() {
        return "pdf";
      }        
      
      public boolean benoetigtGUI() {
        return false;
      }          
    };
  }
      
  public void setParameter(String name, String wert) throws ParameterException {
    throw ParameterException.keineParameter;
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) {
    knoten.addAusgabe(new BenutzerAusgabeWrapper(createBenutzerAusgabe()));
  }

}