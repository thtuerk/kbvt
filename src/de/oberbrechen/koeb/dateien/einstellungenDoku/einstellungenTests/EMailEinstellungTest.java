package de.oberbrechen.koeb.dateien.einstellungenDoku.einstellungenTests;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungTest;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.email.EMail;
import de.oberbrechen.koeb.email.EMailHandler;

/**
 * Diese Klasse führt einen eMail-Test aus.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class EMailEinstellungTest implements EinstellungTest {

  public void testeEinstellung(JFrame main, Einstellung einstellung) throws Exception {
    EMail email = new EMail();

    String buechereiEMail = Datenbank.getInstance().
        getEinstellungFactory().getEinstellung(
        "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei",
        "buechereiEMail").getWert();
    
    buechereiEMail = JOptionPane.showInputDialog(main, "An welche Adresse soll die Test-EMail versand werden?", buechereiEMail);

    email.addEmpfaenger(buechereiEMail);
    email.addKopieEmpfaenger(buechereiEMail);
    email.addBlindkopieEmpfaenger(buechereiEMail);
    email.setBetreff("Test-EMail");

    EMailHandler.clearInstance();
    email.setNachricht("Nachrichtentext - Ansicht");    
    EMailHandler.getInstance().versende(email, true);

    email.setNachricht("Nachrichtentext - Direktversand");    
    EMailHandler.getInstance().versende(email, false);
  }
}
