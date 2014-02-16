package de.oberbrechen.koeb.datenbankzugriff;


/**
 * Dieses Klasse stellt einige grundlegende 
 * Funktionen einer MahnungFactory bereit. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractMahnungFactory implements MahnungFactory {

  public Mahnung erstelleMahnungFuerBenutzer(Benutzer benutzer) {
    return new AbstractMahnung(benutzer);
  }

}