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
public class PdfTeilnehmerlisteVeranstaltungsgruppeAusgabeFactory implements 
  VeranstaltungsgruppeAusgabeFactory {
  
  boolean zeigeZusammenfassung = false;
  boolean zeigeBemerkungen = true;
  boolean querFormat = false;
  
  public void setParameter(String name, String wert) throws ParameterException {
    if (name != null && name.equals("zeigeZusammenfassung")) {
      zeigeZusammenfassung = Boolean.valueOf(wert).booleanValue();
    } else if (name != null && name.equals("zeigeBemerkungen")) {
      zeigeBemerkungen = Boolean.valueOf(wert).booleanValue();
    } else if (name != null && name.equals("querFormat")) {
      querFormat = Boolean.valueOf(wert).booleanValue();
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }

  public VeranstaltungsgruppeAusgabe createVeranstaltungsgruppeAusgabe() throws Exception {
    return new PdfTeilnehmerlisteVeranstaltungsgruppeAusgabe(
        zeigeZusammenfassung, zeigeBemerkungen, querFormat);
  }
}
