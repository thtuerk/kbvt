package de.oberbrechen.koeb.pdf.pdfAufkleber;

import de.oberbrechen.koeb.ausgaben.MedienAusgabe;
import de.oberbrechen.koeb.ausgaben.MedienAusgabeFactory;

/**
* Dieses Klasse stellt eine Factory da, die
* die eine MedienAusgabe erstellt, die ein PdfMedienaufkleber ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class PdfMedienaufkleberMedienAusgabeFactory implements MedienAusgabeFactory {

  private boolean klein = false;
  
  public void setParameter(String name, String wert) {
    if (name.equalsIgnoreCase("klein")) { //$NON-NLS-1$
      klein = Boolean.valueOf(wert).booleanValue();
    }
  }
  
  public MedienAusgabe createMedienAusgabe() throws Exception {
    return new PdfMedienaufkleberMedienAusgabe(klein);
  }
}
