package de.oberbrechen.koeb.email;

import java.text.SimpleDateFormat;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Mahnung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse erstellt eine EMail mit einer Mahnung, für überzogene
 * 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MahnungEMailFactory {

  protected static MahnungEMailFactory instanz;
  
  static {
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();
      
    try {
      instanz = (MahnungEMailFactory) einstellungFactory.getEinstellung(
        "de.oberbrechen.koeb.email.MahnungEMailFactory","instanz").getWertObject(
        MahnungEMailFactory.class, MahnungEMailFactory.class);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }      
  }
  
  public static MahnungEMailFactory getInstance() {
    return instanz;
  }

  protected SimpleDateFormat dateFormat = 
    new SimpleDateFormat("d. MMMM yyyy");
  
  protected void appendAusleihenliste(AusleihenListe liste, StringBuffer buffer) {
    for (int i=0; i < liste.size(); i++) {
      Ausleihe currentAusleihe = (Ausleihe) liste.get(i);
      buffer.append(currentAusleihe.getMedium().getMedienNr()).append("\n");
      buffer.append(currentAusleihe.getMedium().getTitel()).append("\n");
      if (currentAusleihe.getMedium().getAutor() != null)
        buffer.append(currentAusleihe.getMedium().getAutor()).append("\n");
      buffer.append("überzogen seit: ").append(dateFormat.format(currentAusleihe.getSollRueckgabedatum())).append("\n\n");
    }
  }
  
  /**
   * Erstellt eine Mahnungsemail mit der übergebenen Fehlerbeschreibung.
   * @throws DatenbankInkonsistenzException
   */
  public EMail erstelleMahnungsEmail(Mahnung mahnung) throws DatenbankInkonsistenzException {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Dies ist eine automatisch erstellte eMail. " +
        "Falls die Medien in der Zwischenzeit zurückgegeben wurden, ist diese eMail ohne Bedeutung.\n");
    buffer.append("-----------------------------------------------------------------------------\n\n");
    
    buffer.append(mahnung.getBenutzer().getName());
    buffer.append(" hat zur Zeit folgende Medien bei der " +
        Buecherei.getInstance().getBuechereiName() +
        " überzogen:\n\n");
    
    AusleihenListe liste = mahnung.getGemahnteAusleihenListe();
    liste.setSortierung(AusleihenListe.MedienTitelSortierung);
    appendAusleihenliste(liste, buffer);
          
    buffer.append("Bitte geben Sie die überzogenen Medien zurück.\n\nIhr Büchereiteam");

    EMail result = new EMail();
    if (mahnung.getBenutzer().getEMail() != null)
      result.addEmpfaenger(mahnung.getBenutzer().getEMailMitName());
    result.setBetreff("Mahnung der "+Buecherei.getInstance().getBuechereiName());
    result.setNachricht(buffer.toString());
    
    return result;
  }   
}
