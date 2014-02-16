package de.oberbrechen.koeb.gui.admin;

import javax.swing.JMenuBar;

import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.gui.AbstractMain;
import de.oberbrechen.koeb.gui.admin.alleEinstellungenReiter.AlleEinstellungenReiter;
import de.oberbrechen.koeb.gui.admin.benutzerReiter.BenutzerReiter;
import de.oberbrechen.koeb.gui.admin.clientReiter.ClientReiter;
import de.oberbrechen.koeb.gui.admin.dokumentierteEinstellungenReiter.DokumentierteEinstellungenReiter;
import de.oberbrechen.koeb.gui.admin.konfigurierbareBuechereiAusleihordnungReiter.KonfigurierbareBuechereiAusleihordnungReiter;
import de.oberbrechen.koeb.gui.admin.medientypReiter.MedientypReiter;
import de.oberbrechen.koeb.gui.admin.mitarbeiterReiter.MitarbeiterReiter;
import de.oberbrechen.koeb.gui.admin.systematikReiter.SystematikReiter;

/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die zur
 * Administration des Systems dient.
 *
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Main extends AbstractMain {
  
  public Main(boolean isMain, Mitarbeiter mitarbeiter) {
    super(isMain, "Administrationstool", Mitarbeiter.BERECHTIGUNG_ADMIN,
          "de/oberbrechen/koeb/gui/icon-admin.png", mitarbeiter);    
  }

  public static void main(String[] args) {
    init();
    new Main(true, null);
  }
  
  protected void initDaten() {
  }

  protected void reiterHinzufuegen() {
    reiter.add(new KonfigurierbareBuechereiAusleihordnungReiter(this), 
      "konfigurierbare Bücherei / Ausleihordnung");
    reiter.add(new AlleEinstellungenReiter(this), "Einstellungen");
    reiter.add(new DokumentierteEinstellungenReiter(this), "Dokumentierte Einstellungen");
    reiter.add(new BenutzerReiter(this), "Benutzer");
    reiter.add(new MitarbeiterReiter(this), "Mitarbeiter");
    reiter.add(new MedientypReiter(this), "Medientypen");
    reiter.add(new ClientReiter(this), "Clients");
    reiter.add(new SystematikReiter(this), "Systematiken");
  }
  
  protected void addMenue(JMenuBar menue) {
    menue.add(getDateiMenue(true));
    addReiterMenues(menue);
    menue.add(getInfoMenue());
  }    
}