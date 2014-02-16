package de.oberbrechen.koeb.server.medienabfragen;

import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenstrukturen.ISBN;

/**
* Ein Record, der als Rückgabeergebis von Medienabfragen dient.
* 
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class MedienabfrageErgebnis {

  private ISBN isbn;
  private String autor;
  private String titel;
  private String beschreibung;

  
  /**
   * Initialiert das Ergebnis
   * @param isbn die ISBN, die nachgeschalgen wurde
   * @param autor der gefundene Autor
   * @param titel der gefundene Titel
   * @param beschreibung die gefundene Beschreibung
   */
  public MedienabfrageErgebnis(ISBN isbn, String titel, String autor, String beschreibung) {
    this.isbn = isbn;
    this.autor = DatenmanipulationsFunktionen.formatString(autor);
    this.titel = DatenmanipulationsFunktionen.formatString(titel);
    this.beschreibung = DatenmanipulationsFunktionen.formatString(beschreibung);
  }

  public String getAutor() {
    return autor;
  }


  public String getBeschreibung() {
    return beschreibung;
  }


  public ISBN getIsbn() {
    return isbn;
  }


  public String getTitel() {
    return titel;
  }

  
  public String toString() {
    return "Medienabfrage ("+isbn+")";
  }

  public String toDebugString() {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Medienabfrage für ISBN ").append(isbn).append("\n");
    ausgabe.append("-------------------------------\n");
    ausgabe.append("Titel: ").append(titel).append("\n");
    ausgabe.append("Autor: ").append(autor).append("\n\n");
    ausgabe.append("Beschreibung:\n");
    ausgabe.append(beschreibung);
    ausgabe.append("\n");
    return ausgabe.toString();
  }

  
  /**
   * Liefert, ob irgendwelche Daten gefunden wurden.
   * @return ob Titel, Autor oder Beschreibung gesetzt sind
   */
  public boolean enthaeltDaten() {
    return (titel != null) || (autor != null) || (beschreibung != null);
  }
}
