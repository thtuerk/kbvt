package de.oberbrechen.koeb.einstellungen;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.framework.ErrorHandler;
/**
 * Diese Klasse ist eine konfigurierbare Implementierung einer Buecherei.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class KonfigurierbareBuecherei extends Buecherei {

  
  
  class MedientypEinstellung {
    Medientyp medientyp;
    String MediennrPraefix;
    boolean einstellungsjahrInMediennr;
  }
  
  private Vector<MedientypEinstellung> medientypEinstellungen;
  private double internetKostenProEinheitInEuro;
  private int laengeInternetEinheitInSec;
  private int kulanzZeitInternetZugangInSec;
  private boolean[] istGeoeffnet;
  
  protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy"); //$NON-NLS-1$
  protected Ausleihordnung ausleihordnung;

  protected Einstellung adminEMail;
  protected Einstellung buechereiEMail;
  protected Einstellung buechereiName;
  
  protected static final String klassenName = "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei"; //$NON-NLS-1$
  
  public KonfigurierbareBuecherei() {
    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();    
    
    adminEMail = einstellungFactory.getEinstellung(
        klassenName, "adminEMail"); //$NON-NLS-1$
    buechereiEMail = einstellungFactory.getEinstellung(
        klassenName, "buechereiEMail"); //$NON-NLS-1$
    buechereiName = einstellungFactory.getEinstellung(
        klassenName, "buechereiName");     //$NON-NLS-1$
    
    try {
      ausleihordnung = ((AusleihordnungFactory) einstellungFactory.
        getEinstellung(klassenName, 
        "ausleihordnung").getWertObject(AusleihordnungFactory.class,  //$NON-NLS-1$
        KonfigurierbareAusleihordnungFactory.class)).
        createAusleihordnung();
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    
    kulanzZeitInternetZugangInSec = einstellungFactory.getEinstellung( 
      klassenName, "kulanzZeitInternetZugangInSec"). //$NON-NLS-1$
      getWertInt(90);
    laengeInternetEinheitInSec = einstellungFactory.getEinstellung( 
      klassenName, "laengeInternetEinheitInSec").getWertInt(900); //$NON-NLS-1$
    internetKostenProEinheitInEuro = einstellungFactory.
      getEinstellung(klassenName, 
      "internetKostenProEinheitInEuro").getWertDouble(0.5); //$NON-NLS-1$
    
    istGeoeffnet = new boolean[8];
    istGeoeffnet[Calendar.MONDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Montag"). //$NON-NLS-1$
      getWertBoolean(false);
    istGeoeffnet[Calendar.TUESDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Dienstag"). //$NON-NLS-1$
      getWertBoolean(false);
    istGeoeffnet[Calendar.WEDNESDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Mittwoch"). //$NON-NLS-1$
      getWertBoolean(false);
    istGeoeffnet[Calendar.THURSDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Donnerstag"). //$NON-NLS-1$
      getWertBoolean(false);
    istGeoeffnet[Calendar.FRIDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Freitag"). //$NON-NLS-1$
      getWertBoolean(false);
    istGeoeffnet[Calendar.SATURDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Samstag"). //$NON-NLS-1$
      getWertBoolean(false);
    istGeoeffnet[Calendar.SUNDAY] = einstellungFactory.getEinstellung(
      klassenName, "istGeoeffnet.Sonntag"). //$NON-NLS-1$
      getWertBoolean(false);

    
    MedientypFactory medientypFactory = 
      Datenbank.getInstance().getMedientypFactory();
    medientypEinstellungen = new Vector<MedientypEinstellung>();
    Iterator<Medientyp> medienTypIterator = 
      medientypFactory.getAlleMedientypen().iterator();
    while (medienTypIterator.hasNext()) {
      Medientyp currentTyp = medienTypIterator.next();
      
      MedientypEinstellung einstellung = new MedientypEinstellung();
      einstellung.MediennrPraefix = 
        einstellungFactory.getEinstellung( 
        klassenName, "MediennrPraefix."+currentTyp.getName()). //$NON-NLS-1$
        getWert(currentTyp.getName());
      if (einstellung.MediennrPraefix != null) {
        einstellung.MediennrPraefix = einstellung.MediennrPraefix.trim();
        if (einstellung.MediennrPraefix.equals(""))  //$NON-NLS-1$
          einstellung.MediennrPraefix = null;
      } 
      einstellung.einstellungsjahrInMediennr = 
        einstellungFactory.getEinstellung(klassenName,
        "EinstellungsjahrInMediennr."+currentTyp.getName()).getWertBoolean(true); //$NON-NLS-1$
      einstellung.medientyp = currentTyp;
      medientypEinstellungen.add(einstellung);
    }
  }

  // Doku siehe bitte Buecherei
  public Ausleihordnung getAusleihordnung() {
    return ausleihordnung;
  }

  // Doku siehe bitte Buecherei
  public boolean istGeoeffnet(Calendar datum) {
    return (istGeoeffnet[datum.get(Calendar.DAY_OF_WEEK)]);
  }

  // Doku siehe bitte Buecherei
  public double berechneInternetzugangsKosten(int dauer) {
    if (dauer < kulanzZeitInternetZugangInSec) return 0;
    int anzahlEinheiten = (dauer - kulanzZeitInternetZugangInSec) / laengeInternetEinheitInSec + 1;
    return anzahlEinheiten*internetKostenProEinheitInEuro;
  }

  public String getStandardMedienNr(Medientyp medientyp, Date einstellungsDatum) {
    if (medientyp == null || einstellungsDatum == null)
      throw new NullPointerException();
    
    MedientypEinstellung benoetigteEinstellung = null;
    Iterator<MedientypEinstellung> it = medientypEinstellungen.iterator();
    while (it.hasNext() && benoetigteEinstellung == null) {
      MedientypEinstellung aktuelleEinstellung = 
        it.next();
      if (aktuelleEinstellung.medientyp.equals(medientyp))
        benoetigteEinstellung = aktuelleEinstellung;          
    }
    if (benoetigteEinstellung == null) return "???"; //$NON-NLS-1$
    
    String jahr = dateFormat.format(einstellungsDatum);
    StringBuffer medienNr = new StringBuffer();
    if (benoetigteEinstellung.MediennrPraefix != null) {
      medienNr.append(benoetigteEinstellung.MediennrPraefix);
      medienNr.append(" "); //$NON-NLS-1$
    }
    if (benoetigteEinstellung.einstellungsjahrInMediennr) {
      medienNr.append(jahr);
      medienNr.append("-"); //$NON-NLS-1$
    } 

    return Datenbank.getInstance().getMediumFactory().getNaechsteMedienNr(
       medienNr.toString());
  }

  /**
   * Liefert einen Comparator, der dazu dient, zwei Medien anhand ihrer 
   * Mediennummer miteinander zu vergleichen. 
   * Diese Klasse zerlegt die Nummer in 2-3 Teile, den Medientyp,
   * das Einstellungsjahr und die Einstellungsnummer im Jahr. Diese Teile
   * werden lexikographisch verglichen. Beispielsweise wird B 2002-20 in
   * B, 2002 und 20 verlegt. Es gilt B 2002-3 < B 2002-20 wegen 3 < 20. Man
   * beachte, dass dies ein Unterschied zum alphabetischen 
   * Vergleich der Mediennr als String darstellt.
   */
  protected Comparator<String> erstelleMedienNrComparator() {
    return new Comparator<String>() {

      private Collator collocator = Collator.getInstance();

      public int compare(String a, String b) {
        try {
          StringTokenizer stringTokenizerA = new StringTokenizer(a, " -"); //$NON-NLS-1$
          StringTokenizer stringTokenizerB = new StringTokenizer(b, " -"); //$NON-NLS-1$

          // Medientypvergleich
          if (stringTokenizerA.hasMoreTokens() && stringTokenizerB.hasMoreTokens()) {
            String tokenA = stringTokenizerA.nextToken();
            String tokenB = stringTokenizerB.nextToken();

            int erg = collocator.compare(tokenA, tokenB);
            if (erg != 0) return erg;
          }

          // Jahresvergleich
          if (stringTokenizerA.hasMoreTokens() && stringTokenizerB.hasMoreTokens()) {
            int tokenA = Integer.parseInt(stringTokenizerA.nextToken());
            int tokenB = Integer.parseInt(stringTokenizerB.nextToken());

            int erg = tokenA - tokenB;
            if (erg != 0) return erg;
          }

          // Nummernvergleich
          if (stringTokenizerA.hasMoreTokens() && stringTokenizerB.hasMoreTokens()) {
            int tokenA = Integer.parseInt(stringTokenizerA.nextToken());
            int tokenB = Integer.parseInt(stringTokenizerB.nextToken());

            int erg = tokenA - tokenB;
            if (erg != 0) return erg;
          }

          if (stringTokenizerA.hasMoreTokens() && stringTokenizerB.hasMoreTokens()) {
            throw new Exception("Unerwartetes Mediennummernformat");
          }
          if (!stringTokenizerA.hasMoreTokens()) return -1;
          if (!stringTokenizerB.hasMoreTokens()) return 1;
          return 0;
        } catch (Exception e) {
          // unerwartetes Medienformat
          return collocator.compare(a, b);
        }

      }
    };
  }

  public String getAdminEmail() {
    return adminEMail.getWert("t_tuerk@gmx.de"); //$NON-NLS-1$
  }

  /**
   * Setzt die eMail-Adresse des System-Administrators
   */
  public void setAdminEmail(String eMail) {
    try {
      adminEMail.setWert(eMail);
      adminEMail.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }

  public String getBuechereiEmail() {
    return buechereiEMail.getWert("buecherei@koeb-oberbrechen.de"); //$NON-NLS-1$
  }

  /**
   * Setzt die eMail-Adresse der Bücherei
   */
  public void setBuechereiEmail(String eMail) {
    try {
      buechereiEMail.setWert(eMail);
      buechereiEMail.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }

  /**
   * Setzt den Namen der Bücherei
   */
  public void setBuechereiName(String name) {
    try {
      buechereiName.setWert(name);
      buechereiName.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }
  
  public String getBuechereiName() {
    return buechereiName.getWert("Büchereiname");
  }

}
