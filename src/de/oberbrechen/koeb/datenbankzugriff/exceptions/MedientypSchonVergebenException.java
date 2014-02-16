package de.oberbrechen.koeb.datenbankzugriff.exceptions;

import de.oberbrechen.koeb.datenbankzugriff.Medientyp;

/**
* Diese Exception wird geworfen, wenn versucht wird, zwei
* Medientypen mit dem selben Namen zu verwenden.
*
* @author Thomas Türk (t_tuerk@gmx.de)

*/
public class MedientypSchonVergebenException extends
  EindeutigerSchluesselSchonVergebenException {

  /**
   * Erstellt eine neue MedientypSchonVergebenException mit
   * der übergebenen Fehlermeldung und dem übergebenen Medientyp, 
   * der den eindeutigen Schlüssel schon verwendet.
   *
   * @param meldung die Beschreibung der aufgetretenen Ausnahme
   * @param konfliktMedientyp der Medientyp, der den Namen
   *   schon verwendet
   */
  public MedientypSchonVergebenException(String meldung,
    Medientyp konfliktMedientyp) {

    super(meldung, konfliktMedientyp);
  }

  /**
   * Erstellt eine neue MedientypSchonVergebenException mit
   * einer Standardfehlermeldung und dem übergebenen Medientyp, 
   * das den eindeutigen Schlüssel schon verwendet.
   *
   * @param konfliktMedientyp der Medientyp, der den Namen
   *   schon verwendet
   */
  public MedientypSchonVergebenException(Medientyp konfliktMedientyp) {
   super("Der Medientyp '"+konfliktMedientyp.getName()+"' wird "+
        "bereits verwendet.", konfliktMedientyp);
  }

  /**
   * Liefert den Medientyp, der den Namen schon verwendet
   * @return den Medientyp, der den Namen schon verwendet
   */
  public Medientyp getKonfliktMedientyp() {
    return (Medientyp) super.getKonfliktDatensatz();
  }

}
