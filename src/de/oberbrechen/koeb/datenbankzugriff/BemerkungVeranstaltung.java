package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Dieses Interface repräsentiert eine Bemerkung. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface BemerkungVeranstaltung extends Bemerkung {

  /**
   * Liefert die Veranstaltung, an der der Benutzer teilnimmt
   * @return die Veranstaltung, an der der Benutzer teilnimmt
   */
  public Veranstaltung getVeranstaltung();

  /**
   * Setzt die Veranstaltung
   */
  public void setVeranstaltung(Veranstaltung veranstaltung);  

}