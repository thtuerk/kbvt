package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.EANListe;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;

/**
 * Dieses Interface repräsentiert ein Medium der Bücherei. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface Medium extends Datenbankzugriff {

  /**
   * Liefert den Titel des Medium
   * @return Titel des Mediums
   */
  public String getTitel();

  /**
   * Setzt den Titel des Medium
   * @param titel der neue Titel des Mediums
   */
  public void setTitel(String titel);

  /**
   * Liefert die ISBN-Nummer des Medium
   * @return ISDN-Nummer des Mediums
   */
  public ISBN getISBN();

  /**
   * Setzt die ISDN-Nummer des Medium
   * @param isdn die neue ISDN-Nummer des Mediums
   */
  public void setISBN(ISBN isbn);

  /**
   * Liefert den Autor des Medium
   * @return Autor des Mediums
   */
  public String getAutor();

  /**
   * Setzt den Autor des Medium. Wegen der Sortierung empfiehlt sich die Form
   * Nachname, Vorname
   * @param autor der neue Autor des Mediums
   */
  public void setAutor(String autor);
  
  /**
   * Liefert die Beschreibung des Medium
   * @return Beschreibung des Mediums
   */
  public String getBeschreibung();

  /**
   * Setzt die Beschreibung des Medium
   * @param beschreibung die neue Beschreibung des Mediums
   */
  public void setBeschreibung(String beschreibung);

  /**
   * Liefert den Typ des Medium
   * @return Typ des Mediums
   */
  public Medientyp getMedientyp();

  /**
   * Setzt den Typ des Medium
   * @param typ der neue Typ des Mediums
   */
  public void setMedientyp(Medientyp typ);

  /**
   * Liefert die Nr des Medium
   * @return Nr des Mediums
   */
  public String getMedienNr();

  /**
   * Setzt die Nr des Medium
   * @param nr die neue Nr des Mediums
   */
  public void setMediennr(String nr);
    
  /**
   * Liefert die zusätzlichen EAN-Nummern des Medium. Ein Medium besitzt 
   * generell zwei Arten von EAN-Nummern: einerseits besitzt jedes Medium eine
   * automatisch aus seiner ID genierte EAN und evtl. zusätzliche, explizit
   * angegebene EANs. Hier werden nur die explizit gesetzten EANs geliefert.
   * @return eine Liste der EAN-Nummern des Mediums
   */
  public EANListe getEANs();

  /**
   * Setzt die zusätzlichen EAN-Nummern des Medium. Ein Medium besitzt 
   * generell zwei Arten von EAN-Nummern: einerseits besitzt jedes Medium eine
   * automatisch aus seiner ID genierte EAN und evtl. zusätzliche, explizit
   * angegebene EANs. Hier werden die explizit gesetzten EANs gesetzt.
   * @param ean die neue EAN-Nr des Mediums
   */
  public void setEANs(EANListe ean);

  /**
   * Liefert die Medienanzahl des Mediums
   * @return die Medienanzahl des Mediums
   */
  public int getMedienAnzahl();

  /**
   * Setzt die Medienanzahl des Mediums
   * @param anzahl die neue Medienanzahl des Mediums
   */
  public void setMedienAnzahl(int anzahl);

  /**
   * Liefert das Einstellungsdatum des Mediums
   * @return Einstellungsdatum des Mediums
   */
  public Date getEinstellungsdatum();
  
  /**
   * Setzt das Einstellungsdatum des Mediums
   * @param datum das neue Einstellungsdatum des Mediums
   */
  public void setEinstellungsdatum(Date datum);

  /**
   * Liefert das Datum, an dem das Medium aus dem Bestand entfernt wurde.
   * <code>null</code> wird für noch im Bestand befindliche Medien
   * zurückgeliefert.
   * @return das Datum, an dem das Medium aus dem Bestand entfernt wurde
   */
  public Date getEntfernungsdatum();

  /**
   * Setzt das Datum, an dem das Medium aus dem Bestand entfernt wurde.
   * @param datum das neue Entfernungsdatum des Mediums
   */
  public void setEntfernungsdatum(Date datum);

  /**
   * Liefert die Systematiken, zu denen das Medium direkt gehört
   * @return eine Liste aller Systematiken, zu denen das Medium direkt gehört
   */
  public SystematikListe getSystematiken();

  /**
   * Liefert die Systematiken, zu denen das Medium direkt oder indirekt gehört
   * @return eine Liste aller Systematiken, zu denen das Medium gehört
   */
  public SystematikListe getSystematikenMitUntersystematiken();

  /**
   * Liefert die Systematiken, zu denen das Medium direkt gehört.
   * Diese werden - durch Kommata getrennt - in einem String zurückgegeben.
   * @return eine kommatagetrennten String aller Systematiken, 
   *   zu denen das Medium direkt gehört
   */
  public String getSystematikenString();

  /**
   * Setzt die Systematiken, zu denen das Medium direkt gehört
   * @param systematiken die neue Liste aller Systematiken, 
   *   zu denen das Medium direkt gehört
   */
  public void setSystematiken(SystematikListe systematiken);

  /**
   * Prüft, ob das Medium zur übergebenen Systematik gehört. Dabei wird nicht
   * nur getestet, ob das Medium direkt zur übergebenen Systematik gehört, 
   * sondern es werden auch alle Untersystematiken überprüft.
   * 
   * @param systematik die zu testenden Systematiken
   * @return <code>TRUE</code> gdw. das Medium zur übergebenen Systematik
   *   gehört.
   */
  public boolean gehoertZuSystematik(Systematik systematik) 
    throws DatenbankInkonsistenzException;

  /**
   * Prüft, ob das Medium zur einer der übergebenen Systematiken gehört. 
   * Dabei wird nicht nur getestet, ob das Medium direkt zu einer der
   * übergebenen Systematiken gehört, 
   * sondern es werden auch alle Untersystematiken überprüft.
   * 
   * @param systematiken die zu testenden Systematiken
   * @return <code>TRUE</code> gdw. das Medium zur übergebenen Systematik
   *   gehört.
   * @throws DatenbankInkonsistenzException
   */
  public boolean gehoertZuSystematik(SystematikListe systematiken) throws DatenbankInkonsistenzException; 
  
  /**
   * Überprüft, ob das Medium noch im Bestand ist
   * @return <code>TRUE</code> falls das Medium noch im Bestand ist<br>
   * <code>FALSE</code> falls es bereits aus dem Bestand entfernt wurde
   */
  public boolean istNochInBestand();

  /**
   * Liefert die Anzahl der Tage, vor denen das Medium eingestellt wurde.
   * @return die Anzahl der Tage, vor denen das Medium eingestellt wurde.
   */
  public int getEinstellungsdauerInTagen();

  /**
   * Bestimmt, ob das Medium innerhalb der letzten 3 Wochen eingestellt
   * wurde.
   */
  public boolean istNeuEingestellt();

}