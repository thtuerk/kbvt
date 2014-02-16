package de.oberbrechen.koeb.pdf.pdfTabelle;

/**
 * Diese Klasse repräsentiert ein Modell für die Klasse PdfTabelle.
 * Das Tabellenmodell enthält alle nötigen Informationen über die
 * Daten, die in die Tabelle geschrieben werden sollen. Außerdem sind
 * Informationen über die Darstellung der Tabelle enthalten.
 * Bei Verwendung des Standardkonstruktors ist zu beachten, dass dieser
 * die Methode getSpaltenAnzahl() verwendet. Steht die spaltenAnzahl zum 
 * Aufruf des Super-Konstruktors noch nicht fest, kann der Konstruktor, der
 * die Spaltenanzahl als Parameter erhält, benutzt werden. Ist die Spaltenanzahl
 * auch nicht statisch bestimmbar, kann eine willkürliche Spaltenanzahl 
 * verwendet werden und der Fehler anschließend durch Aufruf von 
 * init behoben werden.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class TabellenModell {

  public static final int SPALTEN_AUSRICHTUNG_LINKS = 1;
  public static final int SPALTEN_AUSRICHTUNG_RECHTS = 2;
  public static final int SPALTEN_AUSRICHTUNG_ZENTRIERT = 3;
  public static final int SPALTEN_AUSRICHTUNG_BLOCKSATZ = 4;
  public static final int SPALTEN_AUSRICHTUNG_VERTIKAL = 5;

  public static final int SCHRIFT_NORMAL = 1;
  public static final int SCHRIFT_KURSIV = 2;
  public static final int SCHRIFT_FETT = 3;
  public static final int SCHRIFT_FETT_KURSIV = 4;
  
  protected static float zeilenHintergrund = 0.9f;
  protected static float zeilenHintergrundHell = 0.95f;
  protected static float spaltenHintergrund = 0.8f;
  
  boolean[] besitztFesteBreite;
  boolean[] zeigeSpaltenHintergrund;
  float[] breite;
  float[] breiteProzent;
  float[] spaltenAbstandHintergrund;
  float[] spaltenAbstand;
  int[] spaltenAusrichtung;
  
  boolean zeigeZeilenHintergrund = true;

  /**
   * Liefert die Spaltenanzahl der Tabelle
   * @return die Spaltenanzahl
   */
  abstract public int getSpaltenAnzahl();

  /**
   * Liefert die Zeilenanzahl der Tabelle
   * @return die Zeilenanzahl
   */
  abstract public int getZeilenAnzahl();

  /**
   * Liefert den Namen der Spalte mit der übergebenen Nummer. Die Spalten werden
   * ab 1 bis Spaltenanzahl durchnummeriert.
   *
   * @param spaltenNr die Nummer der Spalte, deren Name geliefert werden soll
   * @return den Namen der Spalte
   */
  abstract public String getSpaltenName(int spaltenNr);

  /**
   * Liefert den String, der in der Zelle in der übergebenen Spalte und
   * Zeile dargestellt werden sollen
   *
   * @param spaltenNr die Nummer der Spalte
   * @param zeilenNr die Nummer der Zeile
   * @return den darzustellenden String
   */
  abstract public String getEintrag(int spaltenNr, int zeilenNr);

  /**
   * Initialisiert die Variablen außer Spaltenname für die
   * in Spaltenanzahl gespeicherte Anzahl von Spalten
   */
  protected void init(int spaltenAnzahl) {
    spaltenAbstand = new float[spaltenAnzahl-1];
    spaltenAbstandHintergrund = new float[spaltenAnzahl-1];
    for (int i=0; i < spaltenAnzahl-1; i++) {
      spaltenAbstand[i] = 10;
      spaltenAbstandHintergrund[i] = 0.5f;
    }

    spaltenAusrichtung = new int[spaltenAnzahl];
    besitztFesteBreite = new boolean[spaltenAnzahl];
    zeigeSpaltenHintergrund = new boolean[spaltenAnzahl];
    breite = new float[spaltenAnzahl];
    breiteProzent = new float[spaltenAnzahl];
    for (int i=0; i < spaltenAnzahl; i++) {
      besitztFesteBreite[i] = false;
      zeigeSpaltenHintergrund[i] = false;
      breite[i] = 80;
      breiteProzent[i] = -10;
      spaltenAusrichtung[i] = TabellenModell.SPALTEN_AUSRICHTUNG_LINKS;
    }
  }

  /**
   * Erstellt ein neues SpaltenModell mit der übergebenen Anzahl an Spalten
   */
  protected TabellenModell(int spaltenAnzahl, boolean init) {
    if (init) init(spaltenAnzahl);
  }

  /**
   * Erstellt ein neues SpaltenModell mit der übergebenen Anzahl an Spalten
   */
  protected TabellenModell() {
    init(getSpaltenAnzahl());
  }

  /**
   * Liefert den Abstand zwischen der übergebenen und der
   * darauffolgenden Spalte. Vor der ersten und nach der letzten Spalte gibt
   * es keinen Abstand.
   *
   * @param spaltenNr die Nummer der Spalte
   * @return den Abstand zwischen der übergebenen und der darauffolgenden Spalte
   */
  public float getSpaltenAbstand(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    if (spaltenNr == getSpaltenAnzahl())
      throw new IllegalArgumentException("Nach der letzten Spalte existiert " +
        "kein Abstand!");
    return spaltenAbstand[spaltenNr-1];
  }

  /**
   * Setzt den Abstand zwischen zwei Spalten für alle Spalten
   * @param spaltenAbstand der Abstand in points
   */
  public void setSpaltenAbstand(float spaltenAbstand) {
    for (int i=0; i < getSpaltenAnzahl()-1; i++)
      this.spaltenAbstand[i] = spaltenAbstand;
  }

  /**
   * Setzt den Abstand nach der übergebenen Spalte
   * @param spaltenNr die Nummer der Spalte
   * @param spaltenAbstand der Abstand in points
   */
  public void setSpaltenAbstand(int spaltenNr, float spaltenAbstand) {
    pruefeSpaltenNr(spaltenNr);
    if (spaltenNr == getSpaltenAnzahl())
      throw new IllegalArgumentException("Nach der letzten Spalte darf kein "+
        "Abstand gesetzt werden!");

    this.spaltenAbstand[spaltenNr-1] = spaltenAbstand;
  }

  /**
   * Liefert die Hintergrundverteilung des Abstandes zwischen der übergebenen und der
   * darauffolgenden Spalte. Der Wert bestimmt, wieviel Prozent des Hintergrundes
   * der übergebenen Spalte benutzt werden sollen. Für den Rest wird der
   * Hintergrund der dahinterliegenden Spalte verwendet. Der Rückgabewert liegt
   * als immer zwischen 0 und 1. 
   *
   * @param spaltenNr die Nummer der Spalte
   * @return den Abstand zwischen der übergebenen und der darauffolgenden Spalte
   */
  public float getSpaltenAbstandHintergrund(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    if (spaltenNr == getSpaltenAnzahl())
      throw new IllegalArgumentException("Nach der letzten Spalte existiert " +        "kein Abstand!");
    return spaltenAbstandHintergrund[spaltenNr-1];
  }

  /**
   * Setzt die Hintergrundverteilung des Abstandes zwischen der übergebenen und der
   * darauffolgenden Spalte. Der Wert bestimmt, wieviel Prozent des Hintergrundes
   * der übergebenen Spalte benutzt werden sollen. Für den Rest wird der
   * Hintergrund der dahinterliegenden Spalte verwendet. Der Wert muss also
   * immer zwischen 0 und 1 liegen. 
   * @param spaltenNr die Nummer der Spalte
   * @param spaltenAbstandHintergrundVerteilung die neue Verteilung 
   */
  public void setSpaltenAbstandHintergrund(
    int spaltenNr, float spaltenAbstandHintergrundVerteilung){
    pruefeSpaltenNr(spaltenNr);
    if (spaltenNr == getSpaltenAnzahl())
      throw new IllegalArgumentException("Nach der letzten Spalte darf kein "+
        "Abstand gesetzt werden!");

    this.spaltenAbstandHintergrund[spaltenNr-1] = 
      spaltenAbstandHintergrundVerteilung;
  }

  /**
   * Bestimmt, ob die Spalte mit der übergebenen Nummer eine feste Breite
   * besitzt. In der Standardimplementierung wird immer false zurückgeliefert.
   *
   * @param spaltenNr die Nummer der Spalte
   * @return <code>true</code> gdw. die Spalte eine feste Breite besitzt
   */
  public boolean getBesitztFesteBreite(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    return besitztFesteBreite[spaltenNr-1];
  }


  /**
   * Legt fest, ob die Spalte mit der übergebenen Nummer eine feste Breite
   * besitzt
   *
   * @param spaltenNr die Nummer der Spalte
   * @param besitztFesteBreite <code>true</code> gdw. die Spalte eine
   *   feste Breite besitzen soll
   */
  public void setBesitztFesteBreite(int spaltenNr, boolean besitztFesteBreite) {
    pruefeSpaltenNr(spaltenNr);
    this.besitztFesteBreite[spaltenNr-1] = besitztFesteBreite;
  }

  /**
   * Liefert die Breite in Points der Spalte mit der übergebenen Nummer
   *
   * @param spaltenNr die Nummer der Spalte, deren Breite geliefert werden soll
   * @return die Breite der Spalte in Points
   */
  public float getBreite(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    return breite[spaltenNr-1];
  }

  /**
   * Setzt die Breite der Spalte der übergebenen Nummer in Points
   *
   * @param spaltenNr die Nummer der Spalte, deren Breite gesetzt werden soll
   * @param breite die zu setztende Breite in Points
   */
  public void setBreite(int spaltenNr, float breite) {
    pruefeSpaltenNr(spaltenNr);
    this.breite[spaltenNr-1] = breite;
  }
  
  /**
   * Setzt die Breite der Spalte der übergebenen Nummer in Points und legt
   * fest, dass die Spalte diese feste Breite besitzt
   *
   * @param spaltenNr die Nummer der Spalte, deren Breite gesetzt werden soll
   * @param breite die zu setztende Breite in Points
   */
  public void setFesteBreite(int spaltenNr, float breite) {
    setBreite(spaltenNr, breite);
    setBesitztFesteBreite(spaltenNr, true);   
  }
  
  
  /**
   * Liefert die Breite der Spalte der übergebenen Nummer in Prozent der 
   * Gesamtbreite der Tabelle
   *
   * @param spaltenNr die Nummer der Spalte, deren Breite geliefert werden soll
   * @return die Breite der Spalte in Prozent
   */
  public float getBreiteProzent(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    return breiteProzent[spaltenNr-1];
  }

  /**
   * Setzt die Breite der Spalte der übergebenen Nummer in Prozent der 
   * Gesamtbreite der Tabelle
   *
   * @param spaltenNr die Nummer der Spalte, deren Breite gesetzt werden soll
   * @param breite die zu setzende Breite in Prozent
   */
  public void setBreiteProzent(int spaltenNr, float breite) {
    pruefeSpaltenNr(spaltenNr);
    this.breiteProzent[spaltenNr-1] = breite;
  }

  /**
   * Überprüft, ob eine Spalte mit der übergebenen Nummer existiert.
   * @param spaltenNr die zu testende Spaltennummer
   * @throws IllegalArgumentException falls keine Spalte mit der übergebenen
   *   Nummer existiert
   */
  private void pruefeSpaltenNr(int spaltenNr) {
    if (spaltenNr < 1 || spaltenNr > getSpaltenAnzahl())
      throw new IllegalArgumentException("Es existiert keine Spalte mit der "+
                                     "Nummer "+spaltenNr+"!");
  }

  public String toString() {
    int spaltenAnzahl = getSpaltenAnzahl();
    StringBuffer buffer = new StringBuffer();
    for(int i=1; i < spaltenAnzahl; i++) {
      buffer.append(this.getSpaltenName(i)).append(", ");
    }
    buffer.append(this.getSpaltenName(spaltenAnzahl));

    return buffer.toString();
  }

  /**
   * Liefert eine String-Darstellung des Modells für Debug-Zwecke. Die
   * einzelnen Spalten werden in einer eingenen Zeile in folgemdem Format
   * ausgegeben <code>Nr: Spaltenname (Breite)</code>. Wird die Breite in
   * eckigen Klammern ausgegeben, bedeutet dies, dass die Spaltenbreite fest
   * ist.
   *
   * @return die String-Darstellung des Modells
   */
  public String toDebugString() {
    StringBuffer buffer = new StringBuffer();
    int spaltenAnzahl = getSpaltenAnzahl();
    for(int i=1; i < spaltenAnzahl; i++) {
      buffer.append(i).append(": ").append(this.getSpaltenName(i)).append(" ");
      if (getBesitztFesteBreite(i)) buffer.append("["); else buffer.append("(");
      buffer.append(getBreite(i));
      if (getBesitztFesteBreite(i)) buffer.append("]"); else buffer.append(")");
      buffer.append("\n");
    }
    return buffer.toString();
  }

  /**
   * Liefert die Ausrichtung der Spalte mit der übergebenen Nummer. Die
   * möglichen Ausrichtungen sind als öffentliche Konstanten dieser Klasse
   * ansprechbar. In der Standardi
   *
   * @param spaltenNr die Nummer der Spalte, deren Ausrichtung
   *   geliefert werden soll
   * @return die Breite der Spalte in Points
   */
  public int getSpaltenAusrichtung(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    return spaltenAusrichtung[spaltenNr - 1];
  }

  /**
   * Setzt die Ausrichtung der übergebenen Spalte. Die verfügbaren Ausrichtungen
   * sind als öffentliche Konstanten dieser Klasse ansprechbar.
   *
   * @param spaltenNr die Nummer der Spalte, deren Ausrichtung gesetzt werden
   *   soll
   * @param int ausrichtung
   */
  public void setSpaltenAusrichtung(int spaltenNr, int ausrichtung) {
    pruefeSpaltenNr(spaltenNr);
    spaltenAusrichtung[spaltenNr - 1] = ausrichtung;
  }

  /**
   * Bestimmt, ob der Hintergrund der Spalte mit der übergebenen Nummer
   * gezeichnet werden soll.
   *
   * @param spaltenNr die Nummer der Spalte
   * @param zeigeSpaltenHintergrund <code>true</code> gdw.
   *   der Spalten-Hintergrund gezeigt werden soll
   */
  public void setZeigeSpaltenHintergrund(int spaltenNr,
    boolean zeigeSpaltenHintergrund) {
    pruefeSpaltenNr(spaltenNr);
    this.zeigeSpaltenHintergrund[spaltenNr - 1] = zeigeSpaltenHintergrund;
  }

  /**
   * Bestimmt, ob der Hintergrund der Spalte mit der übergebenen Nummer
   * gezeichnet werden soll.
   *
   * @param spaltenNr die Nummer der Spalte
   * @return <code>true</code> gdw. der Spalten-Hintergrund gezeigt werden soll
   */
  public boolean getZeigeSpaltenHintergrund(int spaltenNr) {
    pruefeSpaltenNr(spaltenNr);
    return zeigeSpaltenHintergrund[spaltenNr - 1];
  }

  /**
   * Bestimmt, ob der Hintergrund der Zeile mit der übergebenen Nummer
   * gezeichnet werden soll.
   *
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @return <code>true</code> gdw. der Zeilen-Hintergrund gezeigt werden soll
   */
  public boolean getZeigeZeilenHintergrund(int modellZeile, int seitenZeile) {
    return zeigeZeilenHintergrund && (seitenZeile % 2) == 0;
  }

  /**
   * Liefert die Summe der Spaltenbreiten und Abstände der Spalten vor der
   * übergebenen Spalte, also die Position
   * in Points der Spalte mit der übergebenen Nummer
   * @param spaltenNr die Nummer der Spalte
   * @return die linke Position in Points der Spalte
   */
  public float getSpaltenPositionLinks(int spaltenNr) {
    if (spaltenNr < 1 || spaltenNr > getSpaltenAnzahl())
      throw new IllegalArgumentException("Es existiert keine Spalte mit der "+
                                     "Nummer "+spaltenNr+"!");

    float links = 0;
    for (int i = 1; i < spaltenNr; i++)
      links += getBreite(i)+getSpaltenAbstand(i);
    return links;
  }
  
  /**
   * Liefert die Summe der Spaltenbreiten und Abstände der Spalten vor der
   * übergebenen Spalte sowie die Breite der Spalte, also die Position
   * in Points der Spalte mit der übergebenen Nummer
   * @param spaltenNr die Nummer der Spalte
   * @return die rechte Position in Points der Spalte
   */
  public float getSpaltenPositionRechts(int spaltenNr) {
    return getSpaltenPositionLinks(spaltenNr)+getBreite(spaltenNr);
  }

  /**
   * Liefert die Dicke des Rahmens in Point, der oben um die entsprechende
   * Zelle gezogen werden soll
   * 
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @param spalte die Spalte der Zelle
   */
  public float getZellenRandOben(int modellZeile, int seitenZeile, int spalte) {
    return 0;
  }

  /**
   * Liefert die Dicke des Rahmens in Point, der unten um die entsprechende
   * Zelle gezogen werden soll
   * 
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @param spalte die Spalte der Zelle
   */
  public float getZellenRandUnten(int modellZeile, int seitenZeile, int spalte) {
    return 0;
  }

  /**
   * Liefert die Dicke des Rahmens in Point, der links um die entsprechende
   * Zelle gezogen werden soll
   * 
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @param spalte die Spalte der Zelle
   */
  public float getZellenRandLinks(int modellZeile, int seitenZeile, int spalte){
    return 0;
  }

  /**
   * Liefert die Dicke des Rahmens in Point, der rechts um die entsprechende
   * Zelle gezogen werden soll
   * 
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @param spalte die Spalte der Zelle
   */
  public float getZellenRandRechts(
    int modellZeile, int seitenZeile, int spalte){
    return 0;
  }

  /**
   * Liefert die Hintergrund der entsprechenden Zelle
   * 
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @param spalte die Spalte der Zelle
   */
  public float getZellenHintergrund(
    int modellZeile, int seitenZeile, int spalte){

    if (getZeigeZeilenHintergrund(modellZeile, seitenZeile) &&
        !getZeigeSpaltenHintergrund(spalte)) return zeilenHintergrund;
    if (!getZeigeZeilenHintergrund(modellZeile, seitenZeile) &&
        getZeigeSpaltenHintergrund(spalte)) return spaltenHintergrund;
    if (getZeigeZeilenHintergrund(modellZeile, seitenZeile) &&
        getZeigeSpaltenHintergrund(spalte)) {
      //Zeilen- und Spaltenhintergruende 'verflechten'      
      int count = 0;
      for (int i = 1; i < seitenZeile; i++)
        if (getZeigeZeilenHintergrund(modellZeile-seitenZeile-i, i)) count++;
      for (int i = 1; i < spalte; i++)
        if (getZeigeSpaltenHintergrund(i)) count++;
        
      return (count % 2 == 1)?spaltenHintergrund:zeilenHintergrund;
    }        

    return 1;
  }
  
  /**
   * Bestimmt, ob jede zweite Zeile grau hinterlegt werden soll
   * @param zeigeZeilenHintergrund <code>true</code> gdw. jede
   *   zweite Zeile grau hinterlegt werden soll
   */
  public void setZeigeZeilenHintergrund(
      boolean zeigeZeilenHintergrund) {
    this.zeigeZeilenHintergrund = zeigeZeilenHintergrund;
  }
  
  /**
   * Bestimmt die Schriftattribute der Zelle
   * 
   * @param modellZeile die Zeile im Modell der Zelle
   * @param seitenZeile die Zeile auf der Seite der Zelle
   * @param spalte die Spalte der Zelle
   */
  public int getZellenSchrift(int modellZeile, int seitenZeile, int spalte) {
    return SCHRIFT_NORMAL;
  }  
}