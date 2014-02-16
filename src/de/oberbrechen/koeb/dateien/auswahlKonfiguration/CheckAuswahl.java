package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

/**
 * Diese Klasse repräsentiert einen Check, der auf einer Auswahl
 * basiert. Dabei handelt es sich um eine Auswahl mit einem Titel. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class CheckAuswahl {
    
  String titel;
  Auswahl auswahl;
  
  public CheckAuswahl(String titel, Auswahl auswahl) {
    this.titel = titel;
    this.auswahl = auswahl;
  }
  
  protected Auswahl getAuswahl() {
    return auswahl;
  }

  public String getTitel() {
    return titel;
  }
  
  public String toString() {
    return toDebugString();
  }
  
  public String toDebugString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(titel+"\n");
    buffer.append(auswahl.toDebugString());
    return buffer.toString();
  }
}
