package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihzeitraumListe;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.datenstrukturen.Zeitraum;


/**
 * Dieses Klasse stellt einige grundlegende
 * Funktionen einer BenutzerFactory bereit. 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractBenutzerFactory extends AbstractDatenbankzugriffFactory<Benutzer> 
  implements BenutzerFactory {

  public BenutzerListe getAktiveBenutzerInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException {
    BenutzerListe liste = getAktiveLeserInZeitraum(zeitraum);
    liste.addAll(Datenbank.getInstance().getVeranstaltungsteilnahmeFactory().getTeilnehmerListeInZeitraum(zeitraum));
    return liste;
  }

  public BenutzerListe getAktiveLeserInZeitraum(Zeitraum zeitraum) throws DatenbankInkonsistenzException {
    AusleiheFactory ausleiheFactory = Datenbank.getInstance().getAusleiheFactory();
    AusleihzeitraumListe ausleihzeitraumListe =
      ausleiheFactory.getGetaetigteAusleihzeitraeumeInZeitraum(zeitraum);
    
    BenutzerListe leser = new BenutzerListe();
    leser.setSortierung(BenutzerListe.BenutzernrSortierung);
    for (int i=0; i < ausleihzeitraumListe.size(); i++) {
      Ausleihzeitraum ausleihzeitraum = (Ausleihzeitraum) 
        ausleihzeitraumListe.get(i);
      leser.add(ausleihzeitraum.getAusleihe().getBenutzer());
    }
    
    return leser;
  }
}