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
public class PdfTeilnehmerUebersichtVeranstaltungsgruppeAusgabeFactory implements 
  VeranstaltungsgruppeAusgabeFactory {
  
  boolean zeigeZusammenfassung = false;
  
  public void setParameter(String name, String wert) throws ParameterException {
    throw ParameterException.keineParameter;
  }

  public VeranstaltungsgruppeAusgabe createVeranstaltungsgruppeAusgabe() throws Exception {
    return new PdfTeilnehmerUebersichtVeranstaltungsgruppeAusgabe();
  }
}
