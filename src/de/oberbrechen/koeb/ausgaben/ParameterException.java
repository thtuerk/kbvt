package de.oberbrechen.koeb.ausgaben;

/**
* Diese Exception wird geworfen, wenn beim Setzen eines Paramters in einer
* AusgabenFactory ein Problem auftritt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class ParameterException extends Exception {

  public static ParameterException ParameterNameNULL = new ParameterException("Parametername darf nicht NULL sein!");
  public static ParameterException keineParameter = new ParameterException("Diese Factory unterstützt keine Parameter!");
  
  public static ParameterException unbekannterParameter(String name) {
     return new ParameterException("Diese Factory unterstützt keinen Parameter '"+name+"'!");
  }
  
  public static ParameterException parseFehler(String wert, Exception e) {
    String message = "Beim Parsen von '"+wert+"' trat ein Fehler auf";
    if (e == null) {
      message += "!";
    } else {
      message += "!\n"+e.getMessage();
    }    
    return new ParameterException(message);
 }

  public static ParameterException ParameterNichtGesetzt(String name) {
    return new ParameterException("Der Parameter '"+name+"' muß gesetzt werden!");
  }

  public ParameterException(String s) {
    super(s);
  }

}
