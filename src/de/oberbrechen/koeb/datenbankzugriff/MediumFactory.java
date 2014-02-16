package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;

/**
 * Dieses Interface repräsentiert eine Factory für Medientypen.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public interface MediumFactory extends DatenbankzugriffFactory<Medium> {

  /**
   * Erstellt ein neues, noch nicht in der Datenbank
   * vorhandenes Medium-Objekt
   * 
   * @return das neue Medium-Objekt
   */
  public Medium erstelleNeu(); 
  
  /**
   * Liefert eine unsortierte Liste aller Medien, die in der Datenbank
   * eingetragen sind und noch nicht aus dem Bestand entfernt wurden.
   * Es wird davon ausgegangen, dass die Ergebnisliste nicht modifiziert
   * wird.
   * @throws DatenbankInkonsistenzException
   *
   * @see MedienListe
   */
  public MedienListe getAlleMedien() throws DatenbankInkonsistenzException;
  
  /**
   * Liefert eine unsortierte Liste aller Medien, die in der Datenbank
   * eingetragen sind (inklusive derer, die bereits 
   * aus dem Bestand entfernt wurden).
   * Es wird davon ausgegangen, dass die Ergebnisliste nicht modifiziert
   * wird.
   * @throws DatenbankInkonsistenzException
   *
   * @see MedienListe
   */
  public MedienListe getAlleMedienInklusiveEntfernte() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine nach dem Titel sortierte Liste aller Medien, die in der 
   * Datenbank eingetragen sind und noch nicht aus dem Bestand entfernt wurden.
   * Diese Methode cacht das Ergebnis und dient zu Beschleunigung. 
   * Daher wird davon ausgegangen, dass die Ergebnisliste nicht modifiziert
   * wird. Sollte dies nötig sein, so ist die Methode getAlleMedien() mit
   * anschließendem Sortieren zu verwenden.
   * @throws DatenbankInkonsistenzException
   *
   * @see MedienListe
   */
  public MedienListe getAlleMedienTitelSortierung() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine nach der Mediennummer sortierte Liste aller Medien, die in der 
   * Datenbank eingetragen sind und noch nicht aus dem Bestand entfernt wurden.
   * Diese Methode cacht das Ergebnis und dient zu Beschleunigung. 
   * Daher wird davon ausgegangen, dass die Ergebnisliste nicht modifiziert
   * wird. Sollte dies nötig sein, so ist die Methode getAlleMedien() mit
   * anschließendem Sortieren zu verwenden.
   * @throws DatenbankInkonsistenzException
   *
   * @see MedienListe
   */
  public MedienListe getAlleMedienMediennrSortierung() throws DatenbankInkonsistenzException;

  /**
   * Liefert eine Liste aller Medien, die in der Datenbank
   * eingetragen sind und dem uebergebenen Medientyp sowie der uebergebenen 
   * Systematik angehoeren.
   * Sollen alle Medientypen bzw alle Systematiken ausgegeben werden, so
   * ist <code>null</code> zu uebergeben.
   * Der Paramater <code>ausBestandEntfernt</code> bestimmt, ob nur Medien,
   * die sich noch im Bestand befinden, oder nur Medien, die bereits aus dem
   * Bestand entfernt wurden, zurückgeliefert werden sollen.
   *
   * @param typ der auszugebende Medientyp
   * @param ausBestandEntfernt in welchen medien soll gesucht werden?
   * @param systematik die auszugebende Systematik
   * @throws DatenbankInkonsistenzException
   * @see MedienListe
   */
  public MedienListe getMedienListe(Medientyp typ, 
      Systematik systematik, boolean ausBestandEntfernt) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert eine Liste aller Medien, die in der Datenbank
   * eingetragen sind und dem uebergebenen Medientyp sowie der uebergebenen 
   * Systematik angehoeren.
   * Sollen alle Medientypen bzw alle Systematiken ausgegeben werden, so
   * ist <code>null</code> zu uebergeben.
   * Der Paramater <code>ausBestandEntfernt</code> bestimmt, ob nur Medien,
   * die sich noch im Bestand befinden, oder nur Medien, die bereits aus dem
   * Bestand entfernt wurden, zurückgeliefert werden sollen. Bei Übergabe von
   * <code>null</code> werden alle Medien zurückgeliefert.
   *
   * @param typ der auszugebende Medientyp
   * @param ausBestandEntfernt in welchen medien soll gesucht werden?
   * @param systematik die auszugebende Systematik
   * @throws DatenbankInkonsistenzException
   * @see MedienListe
   */
  public MedienListe getMedienListe(Medientyp typ, 
      Systematik systematik, Boolean ausBestandEntfernt) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert eine unsortierte Liste aller Autoren. 
   * @return eine unsortierte Liste aller Autoren
   */
  public Liste<String> getAlleAutoren();

  /**
   * Sucht die Medien mit den angegebenen Eigenschaften, d.h. die Medien des 
   * angegebenen Medientyps, die
   * das Stichwort irgendwo in Titel, Autor oder Beschreibung enthalten, 
   * deren Autor mit der Zeichenfolge autor beginnt und deren Titel 
   * mit der Zeichenfolge titel beginnt. Das Zeichen '%' kann als Platzhalter
   * für beliebige Zeichenfolgen verwendet werden.  
   * @param stichwort
   * @param autor
   * @param titel
   * @param medientyp
   * @return
   * @throws DatenbankInkonsistenzException
   */
  public MedienListe sucheMedien(String stichwort, String autor, String titel, Medientyp medientyp) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Medien, die in der Datenbank
   * eingetragen sind und die zum übergebenen Zeitpunkt im Bestand waren.
   * @throws DatenbankInkonsistenzException
   *
   * @see MedienListe
   */
  public MedienListe getAlleMedien(Date date) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Medien, die im übergebenen
   * Zeitraum neu eingestellt wurden.
   * @throws DatenbankInkonsistenzException
   */
  public MedienListe getEingestellteMedienInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException;

  /**
   * Liefert eine unsortierte Liste aller Medien, die im übergebenen
   * Zeitraum aus dem Bestand entfernt wurden.
   * @throws DatenbankInkonsistenzException
   */
  public MedienListe getEntfernteMedienInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException;
  
  /**
   * Liefert die nächste freie Mediennr in Abhängigkeit von dem übergebenen Pattern.
   * Es werden alle Mediennummern betrachtet, die mit dem übergebenen Pattern 
   * beginnen und dann von einer Zahl gefolgt werden. Von diesen Zahlen wird die
   * größte bestimmt, um eins erhöht und an das Pattern angehängt. Wird keine
   * Zahl nach dem Pattern in der Datenbank gefunden, wird 1 angehängt.  
   * Z.B. könnte mit dem Pattern "B 2004-" die Ausgabe "B 2004-14" oder "B 2004-1" 
   * erfolgen. 
   * 
   * @param pattern
   * @return
   */
  public String getNaechsteMedienNr(String pattern);    
}