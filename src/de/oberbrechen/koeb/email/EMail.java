package de.oberbrechen.koeb.email;


import java.util.StringTokenizer;

import de.oberbrechen.koeb.datenstrukturen.Liste;

/**
 * Diese Klasse repräsentiert eine eMail.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class EMail {
  
  protected Liste<String> to;
  protected Liste<String> cc;
  protected Liste<String> bcc;
  protected String body;
  protected String subject;
  
  /**
   * Erstellt eine neue, leere eMail.
   */
  public EMail() {
    to = new Liste<String>();
    cc = new Liste<String>();
    bcc = new Liste<String>();
    body = null;
  }
  
  /**
   * Erstellt eine neue eMail Nachricht an die übergebenen Empfänger mit dem
   * übergebenen Inhalt
   * @param an die Empfänger
   * @param nachricht die Nachricht
   */
  public EMail(String an, String nachricht) {
    this();
    to.add(an);
    body = nachricht;
  }

  /**
   * Setzt den Inhalt der eMail
   * @param nachricht der neue Inhalt
   */
  public void setNachricht(String nachricht) {
    this.body = nachricht;
  }
  
  /**
   * Liefert den Inhalt der eMail
   * @return die Nachricht
   */
  public String getNachricht() {
    return body;
  }
  
  /**
   * Setzt den Betreff der eMail
   * @param betreff der neue Betreff
   */
  public void setBetreff(String betreff) {
    this.subject = betreff;
  }

  /**
   * Liefert den Betreff der eMail
   */
  public String getBetreff() {
    return subject;
  }

  /**
   * Fügt die Empfänger, die im übergebenen String durch Kommata
   * getrennt sind, zur übergebenen Liste hinzu
   * @param empfaenger
   * @param liste
   */
  private void addToListe(String empfaenger, Liste<String> liste) {
    StringTokenizer tokenizer = new StringTokenizer(empfaenger, ",");
    
    while (tokenizer.hasMoreTokens()) {
      String currentEmpfaenger = tokenizer.nextToken();
      liste.add(currentEmpfaenger.trim());
    }
  }
  
  /**
   * Fügt einen neuen Empfänger zur eMail hinzu
   * @param an der neue Empfänger
   */
  public void addEmpfaenger(String an) {
    to.add(an);
  }
  
  /**
   * Fügt neue Empfänger zur eMail hinzu, die als durch Kommata getrennten
   * String übergeben werden
   * @param an die neuen Empfänger
   */
  public void addEmpfaengerListe(String an) {
    addToListe(an, to);
  }

  
  /**
   * Fügt einen neuen Kopie-Empfänger zur eMail hinzu
   * @param an der neue Kopie-Empfänger
   */
  public void addKopieEmpfaenger(String kopie) {
    cc.add(kopie);
  }

  /**
   * Fügt neue Kopie-Empfänger zur eMail hinzu, die als durch Kommata getrennten
   * String übergeben werden
   * @param kopie die neuen Kopie-Empfänger
   */
  public void addKopieEmpfaengerListe(String kopie) {
    addToListe(kopie, cc);
  }

  /**
   * Fügt einen neuen Blindkopie-Empfänger zur eMail hinzu
   * @param an der neue Blindkopie-Empfänger
   */
  public void addBlindkopieEmpfaenger(String blindKopie) {
    bcc.add(blindKopie);
  }
  
  /**
   * Fügt neue Blindkopie-Empfänger zur eMail hinzu, die als durch Kommata getrennten
   * String übergeben werden
   * @param blindKopie die neuen Blindkopie-Empfänger
   */
  public void addBlindkopieEmpfaengerListe(String blindKopie) {
    addToListe(blindKopie, bcc);
  }

  /**
   * Liefert eine Liste aller Empfänger
   */
  public Liste<String> getEmpfaenger() {
    Liste<String> erg = new Liste<String>();
    erg.addAll(to);
    return erg;
  }
  
  /**
   * Liefert eine Liste aller Kopie-Empfänger
   */
  public Liste<String> getKopieEmpfaenger() {
    Liste<String> erg = new Liste<String>();
    erg.addAll(cc);
    return erg;
  }

  /**
   * Liefert eine Liste aller Blindkopie-Empfänger
   */
  public Liste<String> getBlindkopieEmpfaenger() {
    Liste<String> erg = new Liste<String>();
    erg.addAll(bcc);
    return erg;
  }
  
  public Liste<String> getAlleEmpfaenger() {
    Liste<String> alleEmpfaenger = new Liste<String>();
    alleEmpfaenger.addAll(to);
    alleEmpfaenger.addAll(cc);
    alleEmpfaenger.addAll(bcc);
    return alleEmpfaenger;
  }

  /**
   * Liefert eine Textdarstellung des Objektes mit allen Informationen,
   * die vor allem zum Debuggen gedacht ist.
   *
   * @return die Textdarstellung
   */
  public String toDebugString() {
    return toString();
  }

  private void appendEmpfaengerListe(Liste<String> liste, StringBuffer buffer) {
    if (!liste.isEmpty()) {
      for (int i=0; i < liste.size()-1; i++) {
        buffer.append(liste.get(i));
        buffer.append("\n         ");
      }
      
      buffer.append(liste.get(liste.size()-1)).append("\n");
    } else {
      buffer.append("-\n");
    }
  }
  
  
  public String toString() {    
    StringBuffer buffer = new StringBuffer();
    buffer.append("TO     : ");
    appendEmpfaengerListe(getEmpfaenger(), buffer);
    buffer.append("CC     : ");
    appendEmpfaengerListe(getKopieEmpfaenger(), buffer);
    buffer.append("BCC    : ");
    appendEmpfaengerListe(getBlindkopieEmpfaenger(), buffer);
    buffer.append("Betreff: ").append(getBetreff()).append("\n");
    buffer.append(getNachricht()).append("\n");
    
    return buffer.toString();
  }
  
}
