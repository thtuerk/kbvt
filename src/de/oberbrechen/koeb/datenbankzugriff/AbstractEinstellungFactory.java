package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.framework.ErrorHandler;


/**
 * Dieses Klasse stellt einige grundlegende 
 * Funktionen einer EinstellungenFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractEinstellungFactory extends 
  AbstractDatenbankzugriffFactory<Einstellung> implements EinstellungFactory {

  
  public Einstellung getEinstellung(
    Client client,
    Mitarbeiter mitarbeiter,
    String namespace,
    String name) {

    String nameVollstaendig = name;
    if (namespace != null)
      nameVollstaendig = namespace + "." + nameVollstaendig;
    return getEinstellung(client, mitarbeiter, nameVollstaendig);
  }
  
  
  public Einstellung getEinstellung(String namespace, String name) {
    return getEinstellung(null, null, namespace, name);
  }  

  public Einstellung getClientEinstellung(String namespace, String name) {
    ClientFactory clientFactory = Datenbank.getInstance().getClientFactory();     
    Client benutzterClient = clientFactory.getBenutzenClient();

    return getEinstellung(benutzterClient, null, 
        namespace, name);        
  }

  public Einstellung getClientMitarbeiterEinstellung(
      String namespace, String name) {
    MitarbeiterFactory mitarbeiterFactory =
      Datenbank.getInstance().getMitarbeiterFactory();
    Mitarbeiter mitarbeiter = mitarbeiterFactory.getAktuellenMitarbeiter();
    
    ClientFactory clientFactory = Datenbank.getInstance().getClientFactory();     
    Client benutzterClient = clientFactory.getBenutzenClient();

    return getEinstellung(benutzterClient, mitarbeiter, 
        namespace, name);        
  }

  public Einstellung getMitarbeiterEinstellung(String namespace, String name) {
    MitarbeiterFactory mitarbeiterFactory =
      Datenbank.getInstance().getMitarbeiterFactory();
    Mitarbeiter mitarbeiter = mitarbeiterFactory.getAktuellenMitarbeiter();

    return getEinstellung(null, mitarbeiter, 
        namespace, name);        
  }

  /**
   * Liefert die eigentliche Einstellung mit dem übergebenen
   * Namen zum übergebenen Client und Mitarbeiter. Wird keine
   * passende Einstellung gefunden, wird <code>null</code> zurückgeliefert. 
   */
  protected abstract Einstellung getEinstellungIntern(
      Client client, Mitarbeiter mitarbeiter, String name);
  
  public Einstellung getEinstellung(
      Client client, Mitarbeiter mitarbeiter, String name) {
    Einstellung erg = getEinstellungIntern(client, mitarbeiter, name);
    if (erg != null) return erg;
    
    erg = erstelleNeu(client, mitarbeiter, name);

    Einstellung allgemeinereEinstellung = null;
    
    if (mitarbeiter != null) 
      allgemeinereEinstellung = getEinstellungIntern(client, null, name);    
    if (allgemeinereEinstellung == null && client != null)
      allgemeinereEinstellung = getEinstellungIntern(null, mitarbeiter, name);
    if (allgemeinereEinstellung == null && client != null && mitarbeiter != null)
      allgemeinereEinstellung = getEinstellungIntern(null, null, name);
    if (allgemeinereEinstellung != null)
      erg.setWert(allgemeinereEinstellung.getWert());
    
    try {
      erg.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Die Einstellung "+erg.toDebugString()+" konnte nicht " +
          "gespeichert werden!", false);
    }
    return erg;
  }
}