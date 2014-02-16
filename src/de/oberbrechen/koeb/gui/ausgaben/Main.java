package de.oberbrechen.koeb.gui.ausgaben;

import javax.swing.JMenuBar;

import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.gui.AbstractMain;
import de.oberbrechen.koeb.gui.ausgaben.ausgabenReiter.AusgabenReiter;

/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die für
 * Auswahl von Ausgaben gedacht ist.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Main extends AbstractMain {

  public Main(boolean isMain, Mitarbeiter mitarbeiter) {
    super(isMain, "Ausgaben", Mitarbeiter.BERECHTIGUNG_STANDARD,
          "de/oberbrechen/koeb/gui/icon-ausgaben.png", mitarbeiter);
  }

  public static void main(String[] args) {
    init();
    new Main(true, null);
  }

  protected void initDaten() {
  }

  protected void reiterHinzufuegen() {
    reiter.add(new AusgabenReiter(this), "Ausgaben");
  }
  
  protected void addMenue(JMenuBar menue) {    
    menue.add(getDateiMenue(true));
    addReiterMenues(menue);
    menue.add(getInfoMenue());    
  }      
}