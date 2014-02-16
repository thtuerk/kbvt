package de.oberbrechen.koeb.pdf.pdfAufkleber;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.AusgabenFactory;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.ausgaben.ParameterException;
import de.oberbrechen.koeb.gui.components.benutzerAusgabeWrapper.BenutzerAusgabeWrapper;

/**
* Dieses Klasse stellt eine Factory da, die
* die eine Ausgabe erstellt, die ein PdfMedienaufkleber ausgibt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class PdfBenutzerausweisAusgabenFactory implements AusgabenFactory {  
  public String getName() {
    return "Benutzerausweise";
  }

  public String getBeschreibung() {
    return "Gibt Benutzerausweise aus!";
  }

  public void setParameter(String name, String wert) throws ParameterException {
    throw ParameterException.keineParameter;
  }

  public void addToKnoten(AusgabenTreeKnoten knoten) {
    Ausgabe ausgabe = new BenutzerAusgabeWrapper(new PdfBenutzerausweisBenutzerAusgabe()); 
    knoten.addAusgabe(ausgabe);
  }
}