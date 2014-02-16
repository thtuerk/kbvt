package de.oberbrechen.koeb.datenbankzugriff;

import java.net.InetAddress;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.framework.KonfigurationsDatei;

/**
 * Dieses Klasse stellt einige grundlegende
 * Funktionen einer ClientFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractClientFactory 
  extends AbstractDatenbankzugriffFactory<Client> implements ClientFactory {

  protected static Client benutzerClient = null;

  public Client getBenutzenClient() {
    if (benutzerClient != null) return benutzerClient;

    String clientNrString = 
      KonfigurationsDatei.getStandardKonfigurationsdatei().
        getProperty("ClientNr");
    int clientNr = -1;
    try {
      clientNr = Integer.parseInt(clientNrString);
    } catch (NumberFormatException e) {
      //nichts zu tun, clientNr bleibt -1, Client wird nicht gefunden und neuer Client
      //erstellt
    }
    
    try {
      benutzerClient = (Client) get(clientNr);
    } catch (DatenNichtGefundenException e) {
      ErrorHandler.getInstance().handleError("Der benutzte Client " +        "konnte nicht bestimmt werden, da ein Client mit der Nummer '" +        clientNrString+"' nicht in der Datenbank eingetragen ist! Ein neuer " +        "Client wird erstellt!", false);
      try {
        benutzerClient = erstelleNeu();
        InetAddress inetAdress = InetAddress.getLocalHost();
        benutzerClient.setName(inetAdress.getHostName());
        benutzerClient.setIP(inetAdress.getHostAddress());
        benutzerClient.save();
        KonfigurationsDatei.getStandardKonfigurationsdatei().setProperty(
          "ClientNr", String.valueOf(benutzerClient.getId()));
      } catch (Exception f) {
        ErrorHandler.getInstance().handleException(f, "Der benutzte Client " +
          "konnte nicht in die Datenbank eingetragen werden!", true);
      }
    } catch (DatenbankInkonsistenzException e) {
      //sollte nicht auftreten, da Clientfactory diese Exception nicht wirft.
      ErrorHandler.getInstance().handleException(e, true);
    }
    return benutzerClient;
  }  
}
