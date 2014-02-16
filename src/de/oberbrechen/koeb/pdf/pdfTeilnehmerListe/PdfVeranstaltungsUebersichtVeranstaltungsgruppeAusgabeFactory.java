package de.oberbrechen.koeb.pdf.pdfTeilnehmerListe;

import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabe;
import de.oberbrechen.koeb.ausgaben.VeranstaltungsgruppeAusgabeFactory;

/**
 * Dieses Klasse stellt eine Factory da, die
 * die eine Ausgabe erstellt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfVeranstaltungsUebersichtVeranstaltungsgruppeAusgabeFactory implements 
  VeranstaltungsgruppeAusgabeFactory {
  
  boolean zeigeBemerkungen = false;
  
  public void setParameter(String name, String wert) throws ParameterException {
    if (name != null && name.equals("zeigeBemerkungen")) {
      zeigeBemerkungen = Boolean.valueOf(wert).booleanValue();
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }

  public VeranstaltungsgruppeAusgabe createVeranstaltungsgruppeAusgabe() throws Exception {
    return new PdfVeranstaltungsUebersichtVeranstaltungsgruppeAusgabe(zeigeBemerkungen);
  }
}
