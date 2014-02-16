package de.oberbrechen.koeb.ausgaben;

/**
* Dieses Interface repräsentiert Factory, die in der Lage ist, eine 
* Veranstaltungsgruppen-Ausgaben zu erzeugen. Dieses
* Interface implementierende Klassen sollten einen parameterlosen Konstruktor
* besitzen, um leicht automatisch erzeugt werden zu können. Eventuell nötige
* Parameter können mittels setParameter übergeben werden.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public interface VeranstaltungsgruppeAusgabeFactory {

  /**
   * Setzt einen Parameter. Paramter können mehrfach gesetzt werden.
   * Dies hat evtl. eine andere Interpretation als das einmalige Setzen. 
   * Dagegen darf die Reihenfolge der Paramter keine Bedeutung besitzen.
   * @param name der Name des Parameters
   * @param wert der Wert des Parameters
   * @throws ParameterException falls beim Setzen des Paramters 
   *         ein Problem auftritt
   */
  public void setParameter(String name, String wert) throws ParameterException;  
    
  /**
   * Erzeugt die VeranstaltungsgruppeAusgabe.  
   * @return die erzeugte VeranstaltungsgruppeAusgabe
   */
  public VeranstaltungsgruppeAusgabe createVeranstaltungsgruppeAusgabe() throws Exception;
}