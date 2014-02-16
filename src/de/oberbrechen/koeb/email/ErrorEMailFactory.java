package de.oberbrechen.koeb.email;

import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse ist eine Factory für EMails mit einer Fehlerbeschreibung,
 * die an den Admin geschickt wird.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ErrorEMailFactory {

  protected static ErrorEMailFactory instanz;
  
  static {
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();
      
    try {
      instanz = (ErrorEMailFactory) einstellungFactory.getEinstellung(
        "de.oberbrechen.koeb.email.ErrorEMailFactory","instanz").getWertObject(
        ErrorEMailFactory.class, ErrorEMailFactory.class);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }      
  }
  
  public static ErrorEMailFactory getInstance() {
    return instanz;
  }
  
  protected SimpleDateFormat dateFormat = 
    new SimpleDateFormat("dd.MM.yyyy HH:mm");
  
  /**
   * Erstellt eine ErrorEmail mit der übergebenen Fehlerbeschreibung.
   */
  public EMail erstelleErrorEmail(String fehler) {
    //Daten sammeln
    MitarbeiterFactory mitarbeiterFactory =
      Datenbank.getInstance().getMitarbeiterFactory();
    ClientFactory clientFactory = Datenbank.getInstance().getClientFactory();
        
    Client benutzterClient = clientFactory.getBenutzenClient();
    Mitarbeiter mitarbeiter = mitarbeiterFactory.getAktuellenMitarbeiter();
        
    StringBuffer buffer = new StringBuffer();
    buffer.append("Datum      : ");
    buffer.append(dateFormat.format(new Date())).append("\n");
    buffer.append("Mitarbeiter: ");
    buffer.append(mitarbeiter != null?mitarbeiter.getBenutzer().getNameFormal():"-");
    buffer.append("\n");
    buffer.append("Client     : ").append(benutzterClient);
    buffer.append("\n\n\n");
    buffer.append(fehler);
    
    //eMail erstellen
    EMail result = new EMail();
    result.addEmpfaengerListe(Buecherei.getInstance().getAdminEmail());
    result.setBetreff("KBVT-Fehler");
    result.setNachricht(buffer.toString());
    return result;
  }
  
  /**
   * Erstellt eine ErrorEmail mit der übergebenen Fehlerbeschreibung und Exception.
   */
  public EMail erstelleErrorEmail(Exception e, String beschreibung) {
    final StringBuffer buffer = new StringBuffer();
    e.printStackTrace(new PrintWriter(new Writer(){
      public void write(char[] cbuf, int off, int len) {
        buffer.append(cbuf, off, len);
      }

      public void flush() {}
      public void close() {}
    }));
    
    String nachricht = e.getLocalizedMessage();
    if (beschreibung != null) nachricht = beschreibung + "\n\n"+nachricht;
    nachricht += "\n\n" + buffer.toString();

    return erstelleErrorEmail(nachricht);
  } 
}
