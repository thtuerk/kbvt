package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

/**
* Diese Exception wird geworfen, wenn beim Parsen ein Fehler auftritt.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class ParseException extends RuntimeException {

  Exception e = null;
  
  public ParseException(String s) {
    super(s);
  }

  public ParseException(Exception e) {
    super(e.getLocalizedMessage());
    this.e = e;
  }

  public ParseException(Exception e, String s) {
    super(s+"\n"+e.getLocalizedMessage());
    this.e = e;
  }
}
