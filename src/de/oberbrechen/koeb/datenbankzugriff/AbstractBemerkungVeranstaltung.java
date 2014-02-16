package de.oberbrechen.koeb.datenbankzugriff;



/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Bemerkung.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractBemerkungVeranstaltung extends AbstractBemerkung
  implements BemerkungVeranstaltung {

  protected Veranstaltung veranstaltung;  

 
  public String toDebugString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(toDebugString("Veranstaltungsbemerkung"));
    buffer.append("\n");
    if (veranstaltung != null) {
      buffer.append(veranstaltung.getTitel());
      buffer.append("(");
      buffer.append(veranstaltung.getId());
      buffer.append(")\n");
    }
    
    return buffer.toString();
  }  

  public Veranstaltung getVeranstaltung() {
    return veranstaltung;
  }

  public void setVeranstaltung(Veranstaltung veranstaltung) {
    setIstNichtGespeichert();
    this.veranstaltung = veranstaltung;
  }
}