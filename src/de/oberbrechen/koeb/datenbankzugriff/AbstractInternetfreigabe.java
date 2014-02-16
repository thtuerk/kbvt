package de.oberbrechen.koeb.datenbankzugriff;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;

/**
* Diese Klasse repräsentiert eine Internetfreigabe der Bücherei.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public abstract class AbstractInternetfreigabe extends AbstractDatenbankzugriff
  implements Internetfreigabe {

  // Die Attribute der Internetfreigabe
  protected Client client;
  protected Benutzer benutzer;
  protected Mitarbeiter mitarbeiter;
  protected Date von, bis;
    
  public String toString() {
    return "Internetfreigabe für "+client.getName();
  }

  public String toDebugString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    StringBuffer ausgabe = new StringBuffer();
    ausgabe.append("Internetfreigabe ").append(getId()).append("\n");
    ausgabe.append("-------------------------------\n");
    ausgabe.append("Rechner    : ").append(getClient().getName()).append("\n");
    ausgabe.append("Benutzer   : ").append(getBenutzer().getName()).append("\n");
    ausgabe.append("Mitarbeiter: ").append(getMitarbeiter().
      getBenutzer().getName()).append("\n");
    ausgabe.append("Startzeit  : ").append(dateFormat.format(von)).append("\n");
    ausgabe.append("Stoppzeit  : ").append(dateFormat.format(bis)).append("\n");
    return ausgabe.toString();
  }

  public int getDauer() {
    if (von == null) return 0;

    long freigabeVon = von.getTime();

    long freigabeBis;
    if (bis != null)
      freigabeBis = bis.getTime();
    else
      freigabeBis = System.currentTimeMillis();

    long dauerInMillisekunden = freigabeBis - freigabeVon;
    long dauerInSekunden = dauerInMillisekunden / 1000;

    return (int) dauerInSekunden;
  }
  
  public Date getStartZeitpunkt() {
    if (von == null) return null;
    return new Date(von.getTime());
  }

  public Date getEndZeitpunkt() {
    if (bis == null) return null;
    return new Date(bis.getTime());
  }

  public Client getClient() {
    return client;
  }


  public boolean istAktuell() {
    //Freigabe läuft noch
    if (bis == null) return true;

    long beendetSeitMillisekunden = System.currentTimeMillis() - bis.getTime();
    return (beendetSeitMillisekunden < 1000*60*60*3);
  }

  public Benutzer getBenutzer() {
    return benutzer;
  }

  public Mitarbeiter getMitarbeiter() {
    return mitarbeiter;
  }

  public boolean istFreigegeben() {
    return (bis == null);
  }
  
  public void sperren() throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    Datenbank.getInstance().getInternetfreigabeFactory().
      sperren(this.getClient());
    this.reload();
  }  
}