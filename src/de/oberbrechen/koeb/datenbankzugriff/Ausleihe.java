package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.einstellungen.WidersprichtAusleihordnungException;

/**
* Dieses Interface repräsentiert eine Ausleihe.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public interface Ausleihe extends Datenbankzugriff {
  
   /**
   * Bestimmt, ob die Ausleihe überzogen ist, d.h. das Sollrückgabedatum
   * bereits verstrichen ist
   *
   * @return <code>true</code> gdw. die Ausleihe überzogen ist
   */
  public boolean istUeberzogen();

  /**
   * Liefert die Anzahl der Tage, die die Ausleihe überzogen ist.
   * @return die Anzahl der Tage, die die Ausleihe überzogen ist
   */
  public int getUeberzogeneTage();

  /**
   * Bestimmt, ob die Ausleihe schon zurückgegeben ist.
   * @return <code>true</code> gdw. die Ausleihe zurückgegeben ist
   */
  public boolean istZurueckgegeben();

  /**
   * Bestimmt, ob es sich um eine aktuelle Ausleihe handelt. Eine Ausleihe ist
   * aktuell, wenn sie noch nicht oder heute zurückgegeben wurde.
   *
   * @return <code>true</code> gwd. es sich um eine aktuelle Ausleihe handelt
   */
  public boolean istAktuell();

  /**
   * Bestimmt, ob die Ausleihe heute zurückgegeben wurde; d.h. ob das
   * Rueckgabedatum dem aktuellen Datum entspricht und diese
   * Ausleihe von keiner anderen Ausleihe verlängert wird.
   *
   * @return <code>true</code> gdw. die Ausleihe heute zurückgegeben wurde.
   */
  public boolean heuteZurueckgegeben();

  /**
   * Bestimmt, ob die Ausleihe aktuell verlängert wurde; d.h. ob diese
   * Ausleihe eine Verlängerung heute verlängert wurde.
   *
   * @return <code>true</code> gdw. die Ausleihe aktuell verlängert wurde.
   */
  public boolean istAktuellVerlaengert();

  /**
   * Bestimmt, ob die Ausleihe heute getätigt wurde; d.h. ob das Medium
   * heute ausgeliehen wurde.
   * @return <code>true</code> gdw. die Ausleihe heute getätigt wurde
   */
  public boolean heuteGetaetigt();

  /**
   * Liefert den Benutzer, den diese Ausleihe betrifft
   * @return den Benutzer, den diese Ausleihe betrifft
   */
  public Benutzer getBenutzer();

  /**
   * Liefert das Medium, das diese Ausleihe betrifft
   * @return den Medium, das diese Ausleihe betrifft
   */
  public Medium getMedium();

  /**
   * Liefert den Mitarbeiter, der das Medium herausgab. D.h. es wird der
   * Mitarbeiter geliefert, der den Ersten Ausleihzeitraum der Ausleihe eintrug.
   * @return den Mitarbeiter, der das Medium herausgab
   */
  public Mitarbeiter getMitarbeiterAusleihe();

  /**
   * Liefert den Mitarbeiter, der das Medium zurücknahm
   * @return den Mitarbeiter, der das Medium zurücknahm
   */
  public Mitarbeiter getMitarbeiterRueckgabe();

  /**
   * Liefert das Datum, an dem das Medium ausgeliehen wurde, d.h. den Beginn
   * des ersten Ausleihzeitraums.
   * @return das Datum, an dem das Medium ausgeliehen wurde
   */
  public Date getAusleihdatum();

  /**
   * Liefert das Datum, an dem das Medium zurückgegeben werden soll, d.h. das
   * Ende des bisher letzten Ausleihzeitraums
   * @return das Datum, an dem das Medium zurückgegeben werden soll
   */
  public Date getSollRueckgabedatum();

  /**
   * Liefert das Datum, an dem das Medium zurückgegeben wurde. Ist
   * das Medium noch nicht zurückgegeben wird <code>null</code> geliefert.
   *
   * @return das Datum, an dem das Medium zurückgegeben wurde
   */
  public Date getRueckgabedatum();

  /**
   * Liefert die Bemerkungen zu dieser Ausleihe.
   * @return die Bemerkungen zu dieser Ausleihe
   */
  public String getBemerkungen();

  /**
   * Setzt den Benutzer, den diese Ausleihe betrifft
   * @param benutzer der neue Benutzer, den diese Ausleihe betrifft
   */
  public void setBenutzer(Benutzer benutzer);

  /**
   * Setzt das Medium, das diese Ausleihe betrifft
   * @param medium das neue Medium, das diese Ausleihe betrifft
   */
  public void setMedium(Medium medium);

  /**
   * Setzt den Mitarbeiter, der das Medium herausgab, d.h. den Mitarbeiter des
   * ersten Ausleihzeitraums.
   * @param mitarbeiterAusleihe der neue Mitarbeiter, der das Medium herausgab
   */
  public void setMitarbeiterAusleihe(Mitarbeiter mitarbeiterAusleihe);

  /**
   * Setzt den Mitarbeiter, der das Medium zurücknahm
   * @param mitarbeiterRueckgabe der neue Mitarbeiter, der das Medium zurücknahm
   */
  public void setMitarbeiterRueckgabe(Mitarbeiter mitarbeiterRueckgabe);

  /**
   * Setzt das Datum, an dem das Medium ausgeliehen wurde, d.h den Beginn
   * des ertsen Ausleihzeitraums.
   * @param ausleihdatum das neue Datum, an dem das Medium ausgeliehen wurde
   */
  public void setAusleihdatum(java.util.Date ausleihdatum);

  /**
   * Setzt das Datum, an dem das Medium zurückgegeben werden soll, d.h. das
   * Ende des letzen Ausleihzeitraums.
   * @param sollrueckgabedatum das neue Datum, an dem das Medium zurückgegeben
   *   werden soll
   */
  public void setSollRueckgabedatum(java.util.Date sollrueckgabedatum);

  /**
   * Setzt das Datum, an dem das Medium zurückgegeben wurde.
   * <code>null</code> bedeutet, dass das Medium noch nicht zurückgegeben wurde.
   *
   * @param rueckgabedatum das Datum, an dem das Medium zurückgegeben wurde
   */
  public void setRueckgabedatum(java.util.Date rueckgabedatum);

  /**
   * Setzt die Bemerkungen zu dieser Ausleihe.
   * @param bemerkungen die neuen Bemerkungen zu dieser Ausleihe
   */
  public void setBemerkungen(String bemerkungen);

  /**
   * Verlängert die Ausleihe bis mindestens zum übergebenen Datum. 
   * Zum Verlängern wird der übergebene Mitarbeiter verwendet. D.h.
   * es wird evtl. mehrfach verlängert, bis das Sollrückgabedatum mindestens
   * dem übergebenen Datum entspricht.
   * @param datum das Datum, bis zu dem verlängert werden soll
   * @param mitarbeiter der zu verwendende Mitarbeiter
   * @throws DatenbankInkonsistenzException
   * @throws WidersprichtAusleihordnungException
   */
  public void verlaengereBisMindestens(
    java.util.Date datum,  Mitarbeiter mitarbeiter) throws DatenbankInkonsistenzException, WidersprichtAusleihordnungException;

  /**
   * Verlängert die aktuelle Ausleihe und liefert das Ergebnis der Verlängerung.
   * Zum Verlängern wird der übergebene Mitarbeiter verwendet.
   *
   * @param mitarbeiter der Mitarbeiter, die die Verlängerung tätigt
   * @throws DatenbankInkonsistenzException falls die Ausleihe aus
   *   Konsistenzgründen nicht verlängert werden darf
   * @throws WidersprichtAusleihordnungException falls die Ausleihe nicht
   *   verlängert werde darf, weil dies der Ausleihordnung widerspricht
   * @return die Ausleihe, die die diese Ausleihe verlängert
   * @throws DatenbankInkonsistenzException
   * @throws WidersprichtAusleihordnungException
   */
  public void verlaengere(Mitarbeiter mitarbeiter) throws DatenbankInkonsistenzException, WidersprichtAusleihordnungException;

  /**
   * Macht das verlängern der Ausleihe rückgängig.
   *
   * @throws DatenbankInkonsistenzException falls das Verlängern aus
   *   Konsistenzgründen nicht rückgängig gemacht werden darf
   * @return die Ausleihe, die von dieser Ausleihe verlängert wurde
   * @throws DatenbankInkonsistenzException
   */
  public void verlaengernRueckgaengig() throws DatenbankInkonsistenzException;

  /**
   * Gibt die aktuelle Ausleihe zurück. Zur Rückgabe wird das aktuelle
   * Datum und der übergebene Mitarbeiter verwendet.
   *
   * @param mitarbeiter der Mitarbeiter, die die Rückgabe tätigt
   */
  public void zurueckgeben(Mitarbeiter mitarbeiter);

  /**
   * Liefert die Anzahl der Verlängerungen dieser Ausleihe.
   * @return die Anzahl der Verlängerungen dieser Ausleihe
   */
  public int getAnzahlVerlaengerungen();
  
  /**
   * Liefert die Anzahl der Ausleihzeiträume dieser Ausleihe
   * @return die Anzahl der Ausleihzeiträume dieser Ausleihe
   */
  public int getAnzahlAusleihzeitraeume();
  
  /**
   * Liefert die Ausleihzeiträume dieser Ausleihe sortiert nach
   * dem Tätigungsdatum
   * @return die Ausleihzeiträume dieser Ausleihe
   */
  public AusleihzeitraumListe getAusleihzeitraeume();
    
  /**
   * Liefert den ersten Ausleihzeitraum dieser Ausleihe
   * @return den ersten Ausleihzeitraum dieser Ausleihe
   */
  public Ausleihzeitraum getErstenAusleihzeitraum();
  
  /**
   * Liefert den letzten Ausleihzeitraum dieser Ausleihe
   * @return den letzten Ausleihzeitraum dieser Ausleihe
   */
  public Ausleihzeitraum getLetztenAusleihzeitraum();

  /**
   * Liefert das Tätigungsdatum dieser Ausleihe, also das Datum,
   * an dem der erste Zeitraum dieser Ausleihe freigegeben wurde.
   * @return das Tätigungsdatum
   */
  public Date getTaetigungsdatum();  

  /**
   * Setzt das Tätigungsdatum dieser Ausleihe, also das Datum,
   * an dem der erste Zeitraum dieser Ausleihe freigegeben wurde.
   * @param das neue Tätigungsdatum
   */
  public void setTaetigungsdatum(Date datum);

  /**
   * Liefert den Ausleihzeitraum, der auf den übergebenen folgt oder
   * <code>null</code> falls kein solcher existiert.
   * @param ausleihzeitraum
   * @return den nächsten Ausleihzeitraum
   */
  public Ausleihzeitraum getNaechstenAusleihzeitraum(
      Ausleihzeitraum ausleihzeitraum); 
}

