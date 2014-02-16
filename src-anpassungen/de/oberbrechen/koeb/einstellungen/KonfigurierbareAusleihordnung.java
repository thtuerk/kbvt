package de.oberbrechen.koeb.einstellungen;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import de.oberbrechen.koeb.datenbankzugriff.Ausleihe;
import de.oberbrechen.koeb.datenbankzugriff.Ausleihzeitraum;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Medientyp;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
* Diese Klasse ist eine konfigurierbare Implementierung einer Ausleihordnung.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class KonfigurierbareAusleihordnung extends Ausleihordnung {

  class MedientypEinstellung {
    Medientyp medientyp;
    int mindestAusleihdauerInTagen;
  }

  protected int kulanzZeitInTagen;
  protected double mahngebuehrProMediumProWocheInEuro;
  protected Vector<MedientypEinstellung> medientypEinstellungen;
  
  protected static final String klassenName = "de.oberbrechen.koeb.einstellungen.KonfigurierbareAusleihordnung"; //$NON-NLS-1$
  
  public KonfigurierbareAusleihordnung() {
    EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();
    
    kulanzZeitInTagen = einstellungFactory.getEinstellung( 
      klassenName, "kulanzZeitInTagen").getWertInt(14); //$NON-NLS-1$
    mahngebuehrProMediumProWocheInEuro = einstellungFactory.getEinstellung(
      klassenName, 
      "mahngebuehrProMediumProWocheInEuro").getWertDouble(0.25); //$NON-NLS-1$
      
    medientypEinstellungen = new Vector<MedientypEinstellung>();
    Iterator<Medientyp> medienTypIterator = Datenbank.getInstance().getMedientypFactory().
      getAlleMedientypen().iterator();
    while (medienTypIterator.hasNext()) {
      Medientyp currentTyp = medienTypIterator.next();
      MedientypEinstellung einstellung = new MedientypEinstellung();
      einstellung.medientyp = currentTyp;
      einstellung.mindestAusleihdauerInTagen = 
        einstellungFactory.getEinstellung(klassenName, 
        "mindestAusleihdauerInTagen."+currentTyp.getName()). //$NON-NLS-1$
        getWertInt(21);
      medientypEinstellungen.add(einstellung);
    }
  }
  
  /**
   * Berechnet die Mahngebühren für die übergebene Ausleihe.
   * @return die Mahngebühren für die übergebene Ausleihe
   */
  public double berechneMahngebuehren(Ausleihe ausleihe) {       
    //VIPs nichts berechnen    
    if (ausleihe.getBenutzer().istVIP()) return 0;
      
    //ansonsten
    int sum=0;
    
    AusleihzeitraumListe ausleihen = ausleihe.getAusleihzeitraeume();
    for (int i=0; i < ausleihen.size(); i++) {      
      Date von = ((Ausleihzeitraum) ausleihen.get(i)).getEnde();
      Date bis = i < ausleihen.size()-1?
                   ((Ausleihzeitraum) ausleihen.get(i+1)).getBeginn():
                   new Date();
      
      if (von != null && bis != null) {
        int ueberzogeneTage = 
          DatenmanipulationsFunktionen.getDifferenzTage(von, bis);
        if (ueberzogeneTage > kulanzZeitInTagen) sum+=ueberzogeneTage;
      }      
    }
    
    int anzahlUeberzogeneWochen = (int) Math.floor(
      (double) sum / 7);
    return anzahlUeberzogeneWochen*mahngebuehrProMediumProWocheInEuro;
  }

  
  // Doku siehe bitte Ausleihordnung
  public Date getAusleihenBisDatum(Medium medium, Date datum) {
    Medientyp medientyp = null;
    if (medium == null) {
      try {
        medientyp = Datenbank.getInstance().
          getMedientypFactory().getMeistBenutztenMedientyp();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, true);
      }
    } else {
      medientyp = medium.getMedientyp();
    }

    MedientypEinstellung benoetigteEinstellung = null;
    Iterator<MedientypEinstellung> it = medientypEinstellungen.iterator();
    while (it.hasNext() && benoetigteEinstellung == null) {
      MedientypEinstellung aktuelleEinstellung = 
        it.next();
      if (aktuelleEinstellung.medientyp.equals(medientyp))
        benoetigteEinstellung = aktuelleEinstellung;          
    }
    //Hier wird immer etwas gefunden, da vorher !alle! Medientypen initialisiert wurden
    if (benoetigteEinstellung == null) throw new NullPointerException();
    
    Calendar neuesDatum = Calendar.getInstance();
    if (datum != null) neuesDatum.setTime(datum);
    neuesDatum.add(Calendar.DATE, benoetigteEinstellung.mindestAusleihdauerInTagen);

    return Buecherei.getInstance().getNaechstesOeffnungsdatum(
      neuesDatum.getTime());
  }
}
