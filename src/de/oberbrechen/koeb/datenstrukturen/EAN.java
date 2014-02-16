package de.oberbrechen.koeb.datenstrukturen;

import java.util.BitSet;

/**
 * Diese Klasse repräsentiert einen EAN13-Code.
 * Bei diesen Barcodes stehen 12 Ziffern zur Codierung zur Verfügung.
 * Die 13. ist eine Prüfziffer.
 * Diese Klasse erstellt neue EAN-Codes, berechnet und überprüft die Prüfziffern,
 * liefert zu EAN-Nummern den passenden Datenbankeintrag. Außerdem kann man die
 * Strichcodedarstellung der Nummer erhalten.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class EAN {

  private static final String[] codeA = {"000XX0X", "00XX00X", "00X00XX", "0XXXX0X", "0X000XX", "0XX000X", "0X0XXXX", "0XXX0XX", "0XX0XXX", "000X0XX"};
  private static final String[] codeB = {"0X00XXX", "0XX00XX", "00XX0XX", "0X0000X", "00XXX0X", "0XXX00X", "0000X0X", "00X000X", "000X00X", "00X0XXX"};
  private static final String[] codeC = {"XXX00X0", "XX00XX0", "XX0XX00", "X0000X0", "X0XXX00", "X00XXX0", "X0X0000", "X000X00", "X00X000", "XXX0X00"};
  private static final String[] ziffer1 = {"AAAAAA", "AABABB", "AABBAB", "AABBBA", "ABAABB", "ABBAAB", "ABBBAA", "ABABAB", "ABABBA", "ABBABA"};

  public static final int UnbekannterTypEAN = 0;
  public static final int MediumEAN = 1;
  public static final int BenutzerEAN = 2;

  private String ean; //die eigentliche EAN-Nr

  /**
   * Prüft die EAN-Nr mit Hilfe der Prüfziffer und sucht nach dem passenden
   * Eintrag in der Datenbank, der von dieser Nr repräsentiert wird.
   *
   * @param ean die EAN-Nr, 12-stellige-Nummern werden um die Prüfziffer
   *   erweitert, bei 13-stelligen wird die Prüfziffer gecheckt
   * @throws IllegalArgumentException falls der übergebene String nicht aus
   *  12 oder 13 Ziffern besteht oder der Check der Prüfziffer fehlschlägt
   */
  public EAN(String ean) {
    EAN.checkEANSyntax(ean);
    this.ean = EAN.checkEAN(ean); 
    if (this.ean == null)
      throw new IllegalArgumentException("Die übergebene EAN-Nr ist ungültig. "+
      "Die Überprüfung der Prüfziffer schlug fehl!");
  }
  
  /**
   * Checkt, ob der übergebene String eine gültige EAN sein kann. Es wird
   * nur der Syntax getestet, nicht die Prüfziffer.
   *
   * @throws IllegalArgumentException falls der übergebene String nicht aus
   *  12 oder 13 Ziffern besteht
   */
  private static void checkEANSyntax(String ean) {
    int laenge = ean.length();
    if (laenge != 12 && laenge != 13) throw new
      IllegalArgumentException("Die EAN-Nr muss 12 oder 13 Ziffern lang sein!"+
      " Sie ist aber "+laenge+" Ziffern lang!");

    for (int i = 0; i < ean.length(); i++) {
      if (Character.getType(ean.charAt(i)) != Character.DECIMAL_DIGIT_NUMBER)
        throw new IllegalArgumentException("Die EAN darf nur Ziffern "+
        "enthalten! An Position "+i+" des Argumentes '"+ean+"' steht aber '"+
        ean.charAt(i)+"'.");
    }
  }
  
  /**
   * Checkt, ob der übergebene String eine gültige EAN ist. Ist dies
   * nicht der Fall wird <code>null</code> zurückgeliefert. Ansonsten wird die
   * gültige EAN zurückgeliefert. Wird eine EAN mit Prüfziffer übergeben,
   * so wird die Prüfziffer überprüft, ansonsten wird die Prüfziffer
   * ergänzt.
   * @param ean die zu checkende EAN
   * @return <code>null</code> oder die gültige EAN evtl. erweitert um die
   *   Prüfziffer
   */
  public static String checkEAN(String ean) {
    try {
      checkEANSyntax(ean);
    } catch (IllegalArgumentException e) {
      return null;
    }

    int pruefziffer = EAN.berechnePruefziffer(ean);

    int laenge = ean.length();
    if (laenge == 12) return ean + pruefziffer;
    if (laenge == 13 && pruefziffer != Character.getNumericValue(ean.charAt(12))) {
      return null;
    }
    
    return ean;
  }

  /**
   * Berechnet die Prüfziffer der EAN
   * @param ean die Nummer, die gecheckt werden soll
   * @return die Prüfziffer
   * @throws IllegalArgumentException, falls der übergebene String nicht dem
   *  Syntax einer EAN-Nr entspricht
   */
  private static int berechnePruefziffer(String ean) {
    EAN.checkEANSyntax(ean);
    int pruefsumme = 0;
    pruefsumme += 1 * Character.getNumericValue(ean.charAt(0));
    pruefsumme += 3 * Character.getNumericValue(ean.charAt(1));
    pruefsumme += 1 * Character.getNumericValue(ean.charAt(2));
    pruefsumme += 3 * Character.getNumericValue(ean.charAt(3));
    pruefsumme += 1 * Character.getNumericValue(ean.charAt(4));
    pruefsumme += 3 * Character.getNumericValue(ean.charAt(5));
    pruefsumme += 1 * Character.getNumericValue(ean.charAt(6));
    pruefsumme += 3 * Character.getNumericValue(ean.charAt(7));
    pruefsumme += 1 * Character.getNumericValue(ean.charAt(8));
    pruefsumme += 3 * Character.getNumericValue(ean.charAt(9));
    pruefsumme += 1 * Character.getNumericValue(ean.charAt(10));
    pruefsumme += 3 * Character.getNumericValue(ean.charAt(11));

    int pruefziffer = (10 - pruefsumme) % 10;
    if (pruefziffer < 0) pruefziffer+=10;
    return pruefziffer;
  }

  /**
   * Liefert die Strichcodedarstellung des EAN-Codes als Bitset. Ein gesetztes 
   * Bit bedeutet einen Strich, ein nicht gesetzes Bit eine Lücke. Die insgesamt
   * 95 Striche und Lücken eines EAN 13-Codes dürfen beliebig breit sein; 
   * sie müssen nur alle die gleiche Breite besitzen.
   * @return die Strichcodedarstellung des EAN-Codes
   */
  public BitSet getBitSet() {
    StringBuffer erg = new StringBuffer();
    
    //Beginn
    erg.append("X0X");
    
    //Kodierung für die ersten 6 Ziffern bestimmen
    String kodierung = ziffer1[Character.getNumericValue(ean.charAt(0))];
    
    //erste 6 Ziffern (eigentlich 2-7). Die erste steckt in der Kodierung
    for (int i=1; i < 7; i++) {
      if (kodierung.charAt(i-1) == 'A') {
        erg.append(codeA[Character.getNumericValue(ean.charAt(i))]);
      } else {
        erg.append(codeB[Character.getNumericValue(ean.charAt(i))]);
      }
    }    
    
    //Trennzeichen
    erg.append("0X0X0");
    
    //letze 6 Ziffern 
    for (int i=7; i < 13; i++) {
      erg.append(codeC[Character.getNumericValue(ean.charAt(i))]);
    }
   
    //Ende
    erg.append("X0X");

    //Umwandlung in Bitset
    BitSet bitset = new BitSet(95);
    for (int i=0; i < erg.length(); i++) {
      if (erg.charAt(i) == 'X') {
        bitset.set(i);
      } else {
        bitset.clear(i);
      }
    }

    return bitset;
  }

  /**
   * Liefert den EAN-Code als String
   * @return den EAN-Code als String
   */
  public String getEAN() {
    return ean;
  }

  public String toString() {
    return ean;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof EAN)) return false;
    
    return ((EAN) obj).ean.equals(ean);
  }

  /**
   * Liefert die zur EAN passende ISBN oder null falls keine solche existiert.
   * @return die passende EAN
   */
  public ISBN convertToISBN() {
    try {
       return new ISBN(ean);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
  
  /**
   * Bestimmt, ob die EAN eine ISBN repräsentiert.
   * @return die passende EAN
   */
  public boolean isISBN() {
    return ean.startsWith("978");
  }
  
}