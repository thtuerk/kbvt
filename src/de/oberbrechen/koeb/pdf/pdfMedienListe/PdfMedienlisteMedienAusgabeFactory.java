package de.oberbrechen.koeb.pdf.pdfMedienListe;

import de.oberbrechen.koeb.ausgaben.*;

/**
 * Dieses Klasse stellt eine Factory dar, die
 * die eine Ausgabe erstellt, die eine PdfMedienliste ausgibt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class PdfMedienlisteMedienAusgabeFactory implements 
  MedienAusgabeFactory {

  boolean detailliert = false;
  
  public void setParameter(String name, String wert) throws ParameterException {
    if (name.equalsIgnoreCase("detailliert")) {
      detailliert = Boolean.valueOf(wert).booleanValue();
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }

  public MedienAusgabe createMedienAusgabe() throws Exception {
    if (detailliert) {
      return new PdfMedienlisteMedienAusgabeDetailliert();
    } else {
      return new PdfMedienlisteMedienAusgabe();      
    }
  }
}
