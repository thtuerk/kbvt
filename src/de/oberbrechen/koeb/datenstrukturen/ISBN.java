package de.oberbrechen.koeb.datenstrukturen;


/**
 * Diese Klasse repräsentiert einen ISBN-Code.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ISBN {

  private String isbn; 

  public ISBN(String isbn) {
    int laenge = isbn.length();
    if (laenge >= 11 && isbn.startsWith("978")) {
      isbn = isbn.substring(3, 12);
      laenge = 9;
    }
    
    ISBN.checkISBNSyntax(isbn);
    char pruefziffer = ISBN.berechnePruefziffer(isbn);
    if (laenge == 9) {
      this.isbn = isbn + pruefziffer;
    } else {
      this.isbn = isbn; 
    }
    if (laenge == 10 && pruefziffer != isbn.charAt(9))
      throw new IllegalArgumentException("Die übergebene ISBN-Nr ist ungültig."+
      " Die Überprüfung der Prüfziffer schlug fehl!");
  }
  
  /**
   * Checkt, ob der übergebene String eine gültige ISBN sein kann. Es wird
   * nur der Syntax getestet, nicht die Prüfziffer.
   *
   * @throws IllegalArgumentException falls der übergebene String nicht aus
   *  9 oder 10 Ziffern besteht
   */
  private static void checkISBNSyntax(String isdn) {
    int laenge = isdn.length();
    if (laenge != 9 && laenge != 10) throw new
      IllegalArgumentException("Die ISBN-Nr muss 9 oder 10 Ziffern lang sein!"+
      " Sie ist aber "+laenge+" Ziffern lang!");

    for (int i = 0; i < laenge; i++) {
      if (Character.getType(isdn.charAt(i)) != Character.DECIMAL_DIGIT_NUMBER && 
          (i != 9 || isdn.charAt(i) != 'X'))
        throw new IllegalArgumentException("Die ISBN darf nur Ziffern "+
        "enthalten! An Position "+(i+1)+" des Argumentes '"+isdn+
        "' steht aber '"+isdn.charAt(i)+"'.");
    }    
  }
  
  /**
   * Checkt, ob der übergebene String eine gültige ISBN ist. Ist dies
   * nicht der Fall wird <code>null</code> zurückgeliefert. Ansonsten wird die
   * gültige ISBN zurückgeliefert. Wird eine ISBN mit Prüfziffer übergeben,
   * so wird die Prüfziffer überprüft, ansonsten wird die Prüfziffer
   * ergänzt.
   * @param isbn die zu checkende ISBN-Nr.
   * @return <code>null</code> oder die gültige ISBN-Nr evtl. erweitert um die
   *   Prüfziffer
   */
  public static String checkISBN(String isbn) {
    int laenge = isbn.length();
    if (laenge >= 11 && isbn.startsWith("978")) {
      isbn = isbn.substring(3, 12);
      laenge = 9;
    }
    try {
      checkISBNSyntax(isbn);
    } catch (IllegalArgumentException e) {
      return null;
    }

    char pruefziffer = ISBN.berechnePruefziffer(isbn);
    if (laenge == 9) return isbn + pruefziffer;
    if (laenge == 10 && pruefziffer != isbn.charAt(9)) {
      return null;
    }
    
    return isbn;
  }

  /**
   * Berechnet die Prüfziffer der ISBN
   * @param isbn die Nummer, die gecheckt werden soll
   * @return die Prüfziffer
   * @throws IllegalArgumentException, falls der übergebene String nicht dem
   *  Syntax einer ISBN-Nr entspricht
   */
  private static char berechnePruefziffer(String isbn) {
    ISBN.checkISBNSyntax(isbn);
    int pruefsumme = 0;
    pruefsumme += 1 * Character.getNumericValue(isbn.charAt(0));
    pruefsumme += 2 * Character.getNumericValue(isbn.charAt(1));
    pruefsumme += 3 * Character.getNumericValue(isbn.charAt(2));
    pruefsumme += 4 * Character.getNumericValue(isbn.charAt(3));
    pruefsumme += 5 * Character.getNumericValue(isbn.charAt(4));
    pruefsumme += 6 * Character.getNumericValue(isbn.charAt(5));
    pruefsumme += 7 * Character.getNumericValue(isbn.charAt(6));
    pruefsumme += 8 * Character.getNumericValue(isbn.charAt(7));
    pruefsumme += 9 * Character.getNumericValue(isbn.charAt(8));

    int pruefziffer = pruefsumme % 11;    
    if (pruefziffer == 10) return 'X';
    return Integer.toString(pruefziffer).charAt(0);
  }


  /**
   * Liefert den ISBN-Code als String
   * @return den ISBN-Code als String
   */
  public String getISBN() {
    return isbn;
  }
  
  /**
   * Liefert den ISBN-Code ohne Prüfziffer als String
   * @return den ISBN-Code ohne Prüfziffer als String
   */
  public String getISBNOhnePruefziffer() {
    return isbn.substring(0,9);
  }
  
  public String toString() {
    return isbn;
  }
  
  /**
   * Liefert die zur ISBN passende EAN.
   * @return die passende EAN
   */
  public EAN convertToEAN() {
    return new EAN("978"+getISBNOhnePruefziffer());
  }
}