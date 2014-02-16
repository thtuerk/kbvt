package de.oberbrechen.koeb.pdf.pdfAufkleber;

import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.gui.components.medienAusgabeWrapper.MedienAusgabeWrapper;

/**
 * Dieses Klasse stellt eine Factory da, die
 * die eine Ausgabe erstellt, die ein PdfMedienaufkleber ausgibt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfMedienaufkleberAusgabenFactory implements AusgabenFactory {
  
  public String getName() {
    return "Medienaufkleber"; 
  }

  public String getBeschreibung() {
    return "Erzeugt Ausgaben für Medienaufkleber!"; 
  }

  public void setParameter(String name, String wert) throws ParameterException {
    throw ParameterException.keineParameter; 
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) {
    knoten.addAusgabe(new MedienAusgabeWrapper(new PdfMedienaufkleberMedienAusgabe(true)));
    knoten.addAusgabe(new MedienAusgabeWrapper(new PdfMedienaufkleberMedienAusgabe(false)));
  }
}
