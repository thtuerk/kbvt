package de.oberbrechen.koeb.server.medienabfragen;

import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenstrukturen.ISBN;

/**
* Dieses Klasse fragt die Webseite Thalia.de ab.
* 
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class Thalia extends Medienabfrage {
  
  static private HTMLTagPattern titelPattern = new HTMLTagPattern("<span id=\"_artikel_titel\">", "</span>");
  static private HTMLTagPattern unterTitelPattern = new HTMLTagPattern("<span id=\"_artikel_untertitel\">","</span>");
  static private HTMLTagPattern autorPattern = new HTMLTagPattern("<u class=\"_artikel_authors\">","</u>");
  static private HTMLTagPattern beschreibungPattern = new HTMLTagPattern("<h3>Beschreibung</h3><div class=\"container\"><div class=\"standard\">","</div>");
  static private HTMLTagPattern kurzBeschreibungPattern = new HTMLTagPattern("<h3>Kurzbeschreibung</h3><div class=\"container\"><div class=\"standard\">","</div>");
  
 
  public MedienabfrageErgebnis isbnNachschlagen(ISBN isbn) {
    if (isbn == null) return null;
    
    
    StringBuffer thaliaStringBuffer = HTMLTools.getURL("http://www.thalia.de/shop/bde_bu_hg_startseite/schnellsuche/buch/?fqbi="+isbn.getISBN(),
          "ISO-8859-1");
    if (thaliaStringBuffer == null) return null;
    
    
    String titel = titelPattern.sucheMatchDecode(thaliaStringBuffer);
    String unterTitel = unterTitelPattern.sucheMatchDecode(thaliaStringBuffer);
    String autor = autorPattern.sucheMatchDecode(thaliaStringBuffer);
    String beschreibung = beschreibungPattern.sucheMatchDecode(thaliaStringBuffer);
    String kurzBeschreibung = kurzBeschreibungPattern.sucheMatchDecode(thaliaStringBuffer);

    if (unterTitel != null) {
      if (titel == null) {
        titel = unterTitel;
      } else {
        titel = titel + " - "+unterTitel;
      }
    }    
    
    if (kurzBeschreibung != null) {
      if (beschreibung == null) {
        beschreibung = kurzBeschreibung;
      } else {
        beschreibung = kurzBeschreibung + "\n\n"+beschreibung;
      }
    }

    MedienabfrageErgebnis erg = new MedienabfrageErgebnis(isbn, titel, 
          DatenmanipulationsFunktionen.formatiereNamenListe(autor), beschreibung);
    if (!erg.enthaeltDaten()) return null;
    return erg;
  }


}
