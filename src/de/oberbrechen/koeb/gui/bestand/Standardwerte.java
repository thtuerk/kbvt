package de.oberbrechen.koeb.gui.bestand;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Dieses Klasse kapselt Standardwerte, die bei der Erstellung einen neuen
 * Mediums benutzt werden.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Standardwerte {

  public Medientyp medientyp;
  public int nr, medienanzahl;
  public String autor, titel, beschreibung;
  public boolean mediennrVorschlagen;
  public Date einstellungsDatum;
  public SystematikListe systematiken;
  
  public Standardwerte() {
    nr = 1;
    medienanzahl = 1;
    autor = null;
    titel = null;
    beschreibung = null;
    mediennrVorschlagen = false;
    einstellungsDatum = new Date();
    systematiken = new SystematikListe();
    
    try {
      medientyp = Datenbank.getInstance().
        getMedientypFactory().getMeistBenutztenMedientyp();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Medientyp konnte nicht " +
          "bestimmt werden!", true);
    }
  }

  /**
   * Weist dem Medium die gespeicherten Standardwerte zu.
   */
  public void initialisiereMedium(Medium medium) {
    medium.setMedienAnzahl(medienanzahl);
    medium.setAutor(bestimmmeText(autor));
    medium.setTitel(bestimmmeText(titel));
    medium.setBeschreibung(bestimmmeText(beschreibung));
    medium.setEinstellungsdatum(einstellungsDatum);
    medium.setSystematiken(systematiken);
    medium.setMedientyp(medientyp);
    
    if (mediennrVorschlagen) {
      medium.setMediennr(Buecherei.getInstance().getStandardMedienNr(
          medientyp, einstellungsDatum));       
    }    
    
    nr++;
  }
  
  private String bestimmmeText(String string) {
    if (string == null) return null;
    
    
    StringBuffer erg = new StringBuffer();
    
    int i = 0;
    while (i < string.length()) {
      char currentChar = string.charAt(i);
      
      //nächstes Zeichen einfach übernehmen
      if (currentChar == '\\') {
        erg.append((i < string.length() - 1)?string.charAt(i+1):' ');
        i+=2;
      } else if (currentChar == '$' && i < string.length() -2 &&
          (string.charAt(i+1) == 'n' || string.charAt(i+1) == 'N') &&
          (string.charAt(i+2) == 'r' || string.charAt(i+2) == 'R')) {
        erg.append(nr);
        i+=3;
      } else {
        erg.append(currentChar);
        i++;
      }
    }
    return erg.toString();
  }
}