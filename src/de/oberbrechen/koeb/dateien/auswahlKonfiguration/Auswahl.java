package de.oberbrechen.koeb.dateien.auswahlKonfiguration;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;


/**
 * Diese Klasse repräsentiert eine Auswahl. Diese wird 
 * als Syntaxbaum abgespeichert und muss noch interpretiert und
 * ausgewertet werden. Diese Klasse ist ein "dummer"
 * Datencontainer. Dementsprechend sind die meisten
 * Attribute public.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
class Auswahl {
  
  protected static final int AUSWAHL_TYP_MEDIUM=1;
  protected static final int AUSWAHL_TYP_BENUTZER=2;
  protected static final int AUSWAHL_TYP_AUSLEIHZEITRAUM=4;
  protected static final int AUSWAHL_ALLE_TYPEN=7;
  
  public static final int AND = 1;
  public static final int OR = 2;
  public static final int NOT = 3;
  public static final int GDW = 4;
  public static final int IMPLIES = 5;
  public static final int EINDEUTIG = 6;
  public static final int TRUE = 7;
  public static final int FALSE = 8;

  public static final int MEDIUM_SYSTEMATIK = 9;
  public static final int MEDIUM_MEDIENTYP = 10;
  public static final int AUSWAHL_REFERENZ = 11;
  public static final int GEQ_ZAHL = 12;
  public static final int GREATER_ZAHL = 13;
  public static final int EQUAL_ZAHL = 14;
  public static final int NOT_EQUAL_ZAHL = 15;
  public static final int AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM = 16;
  public static final int MEDIUM_IST_IN_BESTAND = 17;
  public static final int MEDIUM_KEINE_SYSTEMATIK = 18;
  public static final int MEDIUM_KEINE_BESCHREIBUNG = 19;
  
  //Factory-Methoden
  private static Auswahl createAuswahl(int typ, Object a1, Object a2) {    
    Auswahl erg = new Auswahl();
    erg.typ = typ;
    erg.data= new Object[2];
    erg.data[0] = a1;
    erg.data[1] = a2;    
    return erg;
  }
  
  public static Auswahl createANDAuswahl(Auswahl a1, Auswahl a2) {    
    return createAuswahl(AND, a1, a2);
  }
  
  public static Auswahl createORAuswahl(Auswahl a1, Auswahl a2) {    
    return createAuswahl(OR, a1, a2);
  }
  
  public static Auswahl createGDWAuswahl(Auswahl a1, Auswahl a2) {
    return createAuswahl(GDW, a1, a2);
  }
  
  public static Auswahl createIMPLIESAuswahl(Auswahl a1, Auswahl a2) {
    return createAuswahl(IMPLIES, a1, a2);
  }
      
  public static Auswahl createREFERENZAuswahl(Auswahl a) {
    Auswahl erg = new Auswahl();
    erg.typ = AUSWAHL_REFERENZ;    
    erg.data= new Auswahl[1];
    erg.data[0] = a;
    return erg;
  }
    
  public static Auswahl createTRUEAuswahl() {
    Auswahl erg = new Auswahl();
    erg.typ = TRUE;    
    erg.data=null;
    return erg;
  }
  
  public static Auswahl createFALSEAuswahl() {
    Auswahl erg = new Auswahl();
    erg.typ = FALSE;    
    erg.data= null;
    return erg;
  }
  
  public static Auswahl createSystematikAuswahl(String[] v) {
    try {
      Auswahl erg = new Auswahl();
      erg.typ = MEDIUM_SYSTEMATIK;    
  
      SystematikFactory systematikFactory = 
        Datenbank.getInstance().getSystematikFactory();
      SystematikListe liste = new SystematikListe();
      for (int i=0; i < v.length; i++) {
        Systematik systematik = systematikFactory.get(v[i]);
        liste.add(systematik);
      }
      
      erg.data = new SystematikListe[1];
      erg.data[0]=liste;
      return erg;
    } catch (Exception e) {
      throw new ParseException(e);
    }
  }
  
  public static Auswahl createKeineSystematikAuswahl() {
    Auswahl erg = new Auswahl();
    erg.typ = MEDIUM_KEINE_SYSTEMATIK;    
    erg.data=null;
    return erg;
  }
  
  public static Auswahl createNICHT_EINGESTELLTES_MEDIUMAuswahl() {
    Auswahl erg = new Auswahl();
    erg.typ = AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM;
    erg.data = null;
    return erg;
  }  
  
  public static Auswahl createMedienInBestandAuswahl() {
    Auswahl erg = new Auswahl();
    erg.typ = MEDIUM_IST_IN_BESTAND;
    erg.data = null;
    return erg;
  }
  
  public static Auswahl createKeineBeschreibungAuswahl() {
    Auswahl erg = new Auswahl();
    erg.typ = MEDIUM_KEINE_BESCHREIBUNG;
    erg.data = null;
    return erg;
  }
  
  public static Auswahl createMedientypAuswahl(String[] s) {
    try {
      Auswahl erg = new Auswahl();
      erg.typ = MEDIUM_MEDIENTYP;    
  
      MedientypFactory factory = 
        Datenbank.getInstance().getMedientypFactory();
      erg.data= new Medientyp[s.length];    
      for (int i=0; i < s.length; i++) {
          erg.data[i] = factory.get(s[i]);
      }
      
      return erg;
    } catch (Exception e) {
      throw new ParseException(e.getLocalizedMessage());
    }
  }
  
  public static Auswahl createEINDEUTIGAuswahl(Auswahl[] v) {
    Auswahl erg = new Auswahl();
    erg.typ = EINDEUTIG;        
    erg.data= v;
    return erg;
  }
  
  public static Auswahl createGEQ_ZAHLAuswahl(AuswahlWert w1, AuswahlWert w2) {
    return createAuswahl(GEQ_ZAHL, w1, w2);
  }
  
  public static Auswahl createGREATER_ZAHLAuswahl(AuswahlWert w1, AuswahlWert w2) {
    return createAuswahl(GREATER_ZAHL, w1, w2);
  }

  public static Auswahl createEQ_ZAHLAuswahl(AuswahlWert w1, AuswahlWert w2) {
    return createAuswahl(EQUAL_ZAHL, w1, w2);
  }
  
  
  public static Auswahl createNOTAuswahl(Auswahl a) {
    Auswahl erg = new Auswahl();
    erg.typ = NOT;
    erg.data= new Object[1];
    erg.data[0] = a;
    return erg;
  }
  
  protected int typ;
  protected Object[] data;
      
  void addToBuffer(int level, StringBuffer buffer) {
    for (int i=0; i < level; i++)
      buffer.append("--");

    switch (typ) {
      case NOT:
        buffer.append("NOT"); break;
      case TRUE:
        buffer.append("TRUE"); break;
      case FALSE:
        buffer.append("FALSE"); break;
      case OR:
        buffer.append("OR"); break;        
      case AND:
        buffer.append("AND"); break;
      case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
        buffer.append("AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM"); break;
      default:
        buffer.append(typ); break;        
    }
      
    buffer.append("\n");
    if (data != null) {
      for (int i=0; i < data.length; i++) {
        if (data[i] instanceof Auswahl) {
          ((Auswahl) data[i]).addToBuffer(level+1, buffer);
        } else {
          for (int j=0; j <= level; j++)
            buffer.append("--");
          buffer.append(data[i].toString()).append("\n");
        }
      }
    }
  }
  
  public String toString() {
    return toDebugString();
  }
  
  public String toDebugString() {
    StringBuffer buffer = new StringBuffer();
    addToBuffer(0, buffer);
    return buffer.toString();
  }

  public Auswahl deepCopy() { 
    Auswahl copy = new Auswahl();
    copy.typ = this.typ;
    copy.data = new Object[this.data.length];
    
    for (int i=0; i < copy.data.length; i++) {
      Object eintrag = this.data[i];
      if (eintrag instanceof Auswahl) {
        copy.data[i] = ((Auswahl) eintrag).deepCopy();
      } else {
        copy.data[i] = eintrag;        
      }
    }
    
    return copy;
  }
  
  public boolean equals(Object o) {
    if (o == null) return false;    
    if (!(o instanceof Auswahl)) return false;
    if (o == this) return true;
    
    Auswahl a = (Auswahl) o;
    
    if (this.typ != a.typ) return false;
    
    boolean ok = true;
    for (int i=0; i < a.data.length && i < this.data.length && ok; i++) {
      ok = data[i].equals(a.data[i]); 
    }
    
    return ok;    
  }
  
  /**
   * Liefert die Anzahl der Knoten, die sich in dieser Auswahl befinden.
   * Wird für Debuging und Optimierungen benötigt.
   * @return die Anzahl der Knoten
   */
  public int getKnotenAnzahl() {
    int sum = 1; //er selbst;
    for (int i=0; i < data.length; i++) {
      Object eintrag = this.data[i];
      if (eintrag instanceof Auswahl) {
        sum += ((Auswahl) eintrag).getKnotenAnzahl();
      } else {
        sum += 1;        
      }
    }
    return sum;
  }

  public boolean istAusleihzeitraumAuswahl() {
    return (getAuswahltyp() & AUSWAHL_TYP_AUSLEIHZEITRAUM) != 0;
  }
  

  public boolean istMediumAuswahl() {
    return (getAuswahltyp() & AUSWAHL_TYP_MEDIUM) != 0;
  }  
  
  public boolean istBenutzerAuswahl() {
    return (getAuswahltyp() & AUSWAHL_TYP_BENUTZER) != 0;
  }    
  
  private int getAuswahltyp() {
    switch (typ) {
      case TRUE:
      case FALSE:
        return AUSWAHL_ALLE_TYPEN;
      case NOT:
      case IMPLIES:
      case GDW:
      case OR:
      case AND:
      case AUSWAHL_REFERENZ:        
      case EINDEUTIG:        
        int erg = AUSWAHL_ALLE_TYPEN; 
        for (int i=0; i < data.length; i++) {
          erg = ((Auswahl) data[i]).getAuswahltyp() & erg;
        }
        return erg;
      case MEDIUM_SYSTEMATIK:
      case MEDIUM_KEINE_SYSTEMATIK:
      case MEDIUM_KEINE_BESCHREIBUNG:
      case MEDIUM_IST_IN_BESTAND:
      case MEDIUM_MEDIENTYP:
        return AUSWAHL_TYP_AUSLEIHZEITRAUM+AUSWAHL_TYP_MEDIUM;
      case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
        return AUSWAHL_TYP_AUSLEIHZEITRAUM;        
      case NOT_EQUAL_ZAHL:         
      case EQUAL_ZAHL:         
      case GREATER_ZAHL:         
      case GEQ_ZAHL:         
        erg = AUSWAHL_ALLE_TYPEN; 
        for (int i=0; i < data.length; i++) {
          if (data[i] instanceof Auswahl)
            erg = ((AuswahlWert) data[i]).getWertTyp() & erg;          
        }
        return erg;
      default:
        throw new RuntimeException("Unbeachteter Fall "+typ);        
    }
  }
  
  /**
   * Prüft, ob das Object zu dieser Auswahl gehört
   */
  public boolean istInAuswahl(AuswahlObject o) {
    return istInAuswahl(o, null);
  }

  public boolean istInAuswahl(AuswahlObject o, AuswahlKonfiguration cache) {    
    switch (typ) {
      case TRUE:
        return true;
      case FALSE:
        return false;
      case NOT:
        return !((Auswahl) data[0]).istInAuswahl(o, cache);
      case IMPLIES:
        boolean wertA1 = ((Auswahl) data[0]).istInAuswahl(o, cache); 
        boolean wertA2 = ((Auswahl) data[1]).istInAuswahl(o, cache); 
        return (!wertA1) || wertA2;
      case GDW:
        wertA1 = ((Auswahl) data[0]).istInAuswahl(o, cache); 
        wertA2 = ((Auswahl) data[1]).istInAuswahl(o, cache); 
        return (!wertA1 && !wertA2) || (wertA1 && wertA2);
      case OR:
        boolean wert = false;
        for (int i=0; i < data.length; i++) {
          wert = wert || ((Auswahl) data[i]).istInAuswahl(o, cache);
        }
        return wert;        
      case AND:
        wert = true;
        for (int i=0; i < data.length; i++) {
          wert = wert && ((Auswahl) data[i]).istInAuswahl(o, cache);
        }
        return wert;        
      case EINDEUTIG:        
        int sum = 0;
        for (int i=0; i < data.length && sum < 2; i++) {
          if (((Auswahl) data[i]).istInAuswahl(o, cache)) sum++;
        }
        return sum < 2;        
      case MEDIUM_IST_IN_BESTAND:
        return o.getMedium() != null && 
               o.getMedium().istNochInBestand();
      case MEDIUM_SYSTEMATIK:
        try {
          return o.getMedium() != null && 
                 o.getMedium().gehoertZuSystematik((SystematikListe) data[0]);
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Bestimmen " +
              "der Systematikzugehörigkeit!", true);
          return false;
        }
      case MEDIUM_KEINE_SYSTEMATIK:
        return o.getMedium() != null && 
        o.getMedium().getSystematiken().isEmpty();
      case MEDIUM_KEINE_BESCHREIBUNG:
        return o.getMedium() != null && 
        o.getMedium().getBeschreibung() == null;
      case MEDIUM_MEDIENTYP:
        if (o.getMedium() == null) return false;
        
        wert = false;
        for (int i=0; i < data.length && !wert; i++) {
          wert = wert || 
            (o.getMedium().getMedientyp() != null && 
             o.getMedium().getMedientyp().equals(data[i]));
        }

        return wert;        
      case AUSLEIHE_NICHT_EINGESTELLTES_MEDIUM:
        return o.getMedium() == null;
      case AUSWAHL_REFERENZ:         
        if (cache == null)
          return ((Auswahl) data[0]).istInAuswahl(o, cache);
        return cache.istInAuswahl(o, (Auswahl) data[0]);
      case GEQ_ZAHL:         
      case GREATER_ZAHL:         
      case EQUAL_ZAHL:         
      case NOT_EQUAL_ZAHL:         
        Double w1 = (Double) ((AuswahlWert) data[0]).getValue(o);
        Double w2 = (Double) ((AuswahlWert) data[1]).getValue(o);
        if (w1 == null || w2 == null) return false;
        
        if (typ == GEQ_ZAHL)
          return w1.doubleValue() >= w2.doubleValue();
        else if (typ == GREATER_ZAHL)
          return w1.doubleValue() > w2.doubleValue();
        else if (typ == EQUAL_ZAHL)
          return w1.doubleValue() == w2.doubleValue();
        else if (typ == NOT_EQUAL_ZAHL)
          return w1.doubleValue() != w2.doubleValue();
      default:
      throw new RuntimeException("Unbeachteter Fall "+typ);        
    }
  }

  public static Auswahl createNEQ_ZAHLAuswahl(AuswahlWert w1, AuswahlWert w2) {
    return createAuswahl(NOT_EQUAL_ZAHL, w1, w2);
  }
  
  public MedienListe bewerte(MedienListe eingabe) {
    AuswahlObject object = new AuswahlObject();
    MedienListe ausgabe = new MedienListe();
    for (int i=0; i < eingabe.size(); i++) {
      Medium medium = (Medium) eingabe.get(i);
      object.setDaten(medium);
      if (istInAuswahl(object))
        ausgabe.add(medium);
    }
    return ausgabe;
  }
}