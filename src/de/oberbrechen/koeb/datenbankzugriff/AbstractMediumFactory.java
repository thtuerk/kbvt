package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;

/**
 * Diese Klasse stellt einige grundlegende 
 * Funktionen einer MediumFactory bereit.
 *  
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public abstract class AbstractMediumFactory 
  extends AbstractDatenbankzugriffFactory<Medium> implements MediumFactory {

  public MedienListe getAlleMedienMediennrSortierung() throws DatenbankInkonsistenzException {
    MedienListe medienListe = getAlleMedien();
    medienListe.setSortierung(MedienListe.MedienNummerSortierung);
    return medienListe;
  }

  public MedienListe getAlleMedienTitelSortierung() throws DatenbankInkonsistenzException {
    MedienListe medienListe = getAlleMedien();
    medienListe.setSortierung(MedienListe.TitelAutorSortierung);
    return medienListe;
  }

  public MedienListe getMedienListe(Medientyp typ, 
      Systematik systematik, boolean ausBestandEntfernt) throws DatenbankInkonsistenzException {
  
    return getMedienListe(typ, systematik, new Boolean(ausBestandEntfernt));
  }
}