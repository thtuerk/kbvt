package de.oberbrechen.koeb.datenbankzugriff;

import java.util.Date;
import java.util.Iterator;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.*;

/**
 * Diese Klasse bietet die grundlegenden Methoden des
 * Interfaces Medium.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public abstract class AbstractMedium extends AbstractDatenbankzugriff
  implements Medium {

  // Die Attribute des Mediums wie in der Datenbank
  protected String nr, titel, autor, beschreibung;
  protected EANListe eanListe;
  protected Medientyp medientyp;
  protected int medienAnzahl;
  protected Date eingestellt_seit, aus_Bestand_entfernt;
  protected ISBN isbn;

  //die Systematiken, zu denen das Medium direkt gehört
  protected SystematikListe systematiken;
  
  /**
   * Erstellt ein neues <code>Medium</code>-Objekt. Dazu wird das Medium
   * beim speichern neu in die Datenbank geschrieben.
   */
  public AbstractMedium() {
    nr = null;
    titel = null;
    autor = null;
    beschreibung = null;
    eanListe = new EANListe();
    medientyp = null;
    medienAnzahl = 1;
    eingestellt_seit = new Date();
    aus_Bestand_entfernt = null;
    systematiken = new SystematikListe();
  }

  public String getTitel() {
    return titel;
  }

  public void setTitel(String titel) {
    setIstNichtGespeichert();
    this.titel = DatenmanipulationsFunktionen.formatString(titel);
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    setIstNichtGespeichert();
    this.autor = DatenmanipulationsFunktionen.formatString(autor);
  }
  
  public String getBeschreibung() {
    return beschreibung;
  }

  public void setBeschreibung(String beschreibung) {
    setIstNichtGespeichert();
    this.beschreibung = DatenmanipulationsFunktionen.formatString(beschreibung);
  }

  public Medientyp getMedientyp() {
    return medientyp;
  }

  public void setMedientyp(Medientyp typ) {
    setIstNichtGespeichert();
    this.medientyp = typ;
  }

  /**
   * Liefert die Nr des Medium
   * @return Nr des Mediums
   */
  public String getMedienNr() {
    return nr;
  }

  public void setMediennr(String nr) {
    setIstNichtGespeichert();
    this.nr = DatenmanipulationsFunktionen.formatString(nr);
  }

  public EANListe getEANs() {
    EANListe result = new EANListe();
    result.addAllNoDuplicate(eanListe);
    return result;
  }

  public void setEANs(EANListe eanListe) {
    setIstNichtGespeichert();
    this.eanListe.clear();
    this.eanListe.addAllNoDuplicate(eanListe);
  }

  public int getMedienAnzahl() {
    return medienAnzahl;
  }

  public void setMedienAnzahl(int anzahl) {
    if (anzahl <= 0) throw new IllegalArgumentException("" +      "Die Medienanzahl '"+anzahl+"' ist ungültig!");
    setIstNichtGespeichert();
    this.medienAnzahl = anzahl;
  }

  public Date getEinstellungsdatum() {
    return DatenmanipulationsFunktionen.cloneDate(eingestellt_seit);
  }

  public void setEinstellungsdatum(Date datum) {    
    setIstNichtGespeichert();
    this.eingestellt_seit = 
      DatenmanipulationsFunktionen.cloneDate(datum);
  }

  public Date getEntfernungsdatum()  {
    return DatenmanipulationsFunktionen.cloneDate(aus_Bestand_entfernt);
  }

  public void setEntfernungsdatum(Date datum) {
    setIstNichtGespeichert();
    this.aus_Bestand_entfernt = 
      DatenmanipulationsFunktionen.cloneDate(datum);
  }

  public SystematikListe getSystematiken() {
    SystematikListe result = new SystematikListe();
    result.addAllNoDuplicate(systematiken);
    return result;
  }

  public String getSystematikenString() {
    Iterator<Systematik> it = getSystematiken().iterator();
    StringBuffer buffer = new StringBuffer();
    
    if (it.hasNext()) buffer.append(it.next().getName());    
    while (it.hasNext()) {
      buffer.append(", ");
      buffer.append(it.next().getName());          
    }
    return buffer.toString();
  }

  public SystematikListe getSystematikenMitUntersystematiken() { 
    SystematikListe erg = new SystematikListe();
    
    Iterator<Systematik> systematikIt = getSystematiken().iterator();
    while (systematikIt.hasNext()) {
      Systematik aktuelleSystematik = systematikIt.next();
      erg.addAll(aktuelleSystematik.getAlleUntersystematiken());
    }
    
    return erg;
  }
  
  public void setSystematiken(SystematikListe systematiken) {
    setIstNichtGespeichert();
    this.systematiken.clear();
    this.systematiken.addAllNoDuplicate(systematiken);
  }

  public boolean gehoertZuSystematik(Systematik systematik) 
    throws DatenbankInkonsistenzException {
          
    SystematikListe liste = new SystematikListe();
    liste.addNoDuplicate(systematik);
    return gehoertZuSystematik(liste);
  }

  public boolean gehoertZuSystematik(SystematikListe systematikListe) 
    throws DatenbankInkonsistenzException {

    systematiken = getSystematiken();
    int systematikenSize = systematiken.size(); 
    int systematikListeSize = systematikListe.size();
    
    if (systematikenSize == 0 || systematikListeSize == 0) return false;
      
    for (int i=0; i < systematikenSize; i++) {
      Systematik testSystematik = systematiken.get(i);
      
      for (int j=0; j < systematikListeSize; j++) {
        Systematik systematik = systematikListe.get(j);
        if (testSystematik.istUntersystematikVon(systematik)) return true;
      }
    }
    
    return false;
  }

  public boolean istNochInBestand() {
    return (getEntfernungsdatum() == null);
  }

  public String toDebugString()  {
    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append(this.getMedienNr()).append(" (").
      append(this.getMedientyp()).append(")\n");
    ausgabe.append("-------------------------------\n");
    if (this.getTitel() != null) 
      ausgabe.append(this.getTitel()).append("\n");
    if (this.getAutor() != null) 
      ausgabe.append(this.getAutor()).append("\n");
    if (this.getEinstellungsdatum() != null) {
      ausgabe.append("eingestellt seit    : ");
      ausgabe.append(
        DatenmanipulationsFunktionen.formatDate(getEinstellungsdatum())).
        append("\n");
    }
    ausgabe.append("aus Bestand entfernt: ");
    ausgabe.append(
      DatenmanipulationsFunktionen.formatDate(this.getEntfernungsdatum()));
    ausgabe.append("\nMedienanzahl        : ");
    ausgabe.append(this.getMedienAnzahl());
    if (this.getISBN() != null) {
      ausgabe.append("\nISBN                : ");
      ausgabe.append(this.getISBN().getISBN());      
    }
    
    ausgabe.append("\n");
    if (this.getBeschreibung() != null) {
      ausgabe.append("\n");
      ausgabe.append(this.getBeschreibung());
    }

    ausgabe.append("\n\nSystematiken:\n");

    Iterator<Systematik> itSys = getSystematiken().iterator();
    while (itSys.hasNext()) {
      Systematik sys = itSys.next();
      ausgabe.append(sys.getName()).append(" - ").
        append(sys.getBeschreibung()).append("\n");
    }

    ausgabe.append("\n\nEANs:\n");
    Iterator<EAN> itEan = getEANs().iterator();
    while (itEan.hasNext()) {
      ausgabe.append(itEan.next().getEAN()).append("\n");
    }

    return ausgabe.toString();
  }

  public String toString() {
    return getTitel()+" ("+getMedienNr()+")";
  }

  public ISBN getISBN() {
    return isbn;
  }

  public void setISBN(ISBN isbn) {
    setIstNichtGespeichert();
    this.isbn = isbn;
  }
  
  public int getEinstellungsdauerInTagen() {
    Date einstellungsDatum = getEinstellungsdatum();
    if (einstellungsDatum == null) return -1;
    
    return DatenmanipulationsFunktionen.getDifferenzTage(
        einstellungsDatum, new Date());
  }

  public boolean istNeuEingestellt() {          
    return (getEinstellungsdatum() != null) &&
      this.getEinstellungsdatum().after(new Date(
        System.currentTimeMillis()-1000*60*60*24*21));
  }

}