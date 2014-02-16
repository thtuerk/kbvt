package de.oberbrechen.koeb.ausgaben;


/**
* Diese Klasse ist eine AusgabenFactory, es ermöglicht Ausgaben per
* Reflektion zu erstellen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class AllgemeineAusgabenFactory implements AusgabenFactory {

  String name = null;
  String ausgabe = null;
  
  public String getName() {
    return "Ausgabe";
  }

  public String getBeschreibung() {
    return "Wrapper, die eine beliebige Ausgabe mit parameterlosem Konstruktor erzeugt."; 
  }

  public void setParameter(String name, String wert) throws
    ParameterException { 
    if (name == null) throw ParameterException.ParameterNameNULL;
    if (name.equals("name")) {
      this.name = wert;
    } else if (name.equals("ausgabe")) {
      this.ausgabe = wert;
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }
    
  public void addToKnoten(AusgabenTreeKnoten knoten) 
    throws ParameterException {
    if (ausgabe == null) throw ParameterException.ParameterNichtGesetzt("ausgabe");
      
    final Ausgabe neueAusgabe;
    try {
      neueAusgabe = (Ausgabe) Class.forName(ausgabe).newInstance();
    } catch (Exception e) {
      throw new ParameterException("'"+ausgabe+"' kann nicht als Name " +        "einer Ausgabe mit parameterlosem Konstruktor interpretiert werden!\n\n"+e.getMessage());
    }
    
    knoten.addAusgabe(neueAusgabe);    
  }
}