package de.oberbrechen.koeb.server.medienabfragen;

import de.oberbrechen.koeb.datenstrukturen.ISBN;

/**
* Dieses Klasse liefert bei Abfragen nie ein Ergebnis. Dieses jedoch sehr schnell :-).
* 
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class LeereMedienAbfrage extends Medienabfrage {
  
  public MedienabfrageErgebnis isbnNachschlagenMitTimeout(ISBN isbn) {
    return null;
  }

  public MedienabfrageErgebnis isbnNachschlagen(ISBN isbn, long timeout) {
    return null;
  }

  public MedienabfrageErgebnis isbnNachschlagen(ISBN isbn) {
    return null;
  }
}
