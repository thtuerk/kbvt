package de.oberbrechen.koeb.gui.components.listenAuswahlPanel.mediumListenAuswahlPanel;

import javax.swing.JTable;

import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AusgabeAuswahl;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.ListenAuswahlPanel;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.TabellenSelektion;

/**
* Diese Klasse ist eine Implementierung
* des ListenAuswahlPanels für Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class MediumListenAuswahlPanel extends ListenAuswahlPanel<Medium> {

  public MediumListenAuswahlPanel() {
    this(false, false);
  }
    
  public MediumListenAuswahlPanel(boolean ausrichtung, boolean zeigeAuswahl) {
    super(ausrichtung, zeigeAuswahl);
  }

  protected AbstractListeSortierbaresTableModel<Medium> createDatenAuswahlTableModel(JTable table, int auswahlTyp) throws DatenbankInkonsistenzException {
    MediumFactory mediumFactory = Datenbank.getInstance().getMediumFactory();
    return new MediumTableModel(mediumFactory.getAlleMedien());
  }

  protected AbstractListeSortierbaresTableModel<Medium> createEmptyAuswahlTableModel(JTable table, int auswahlTyp) {
    return new MediumTableModel(new MedienListe());
  }
      
  /**
   * Setzt als mögliche Auswahlen die in der Konfiguration gespeicherten
   * Ausgaben. Diese müssen Medien-Ausgaben sein.
   */
  public void initAuswahlFeld(AuswahlKonfiguration konfiguration) {        
    auswahlComboBox.removeAllItems();
    auswahlComboBox.addItem("");

    for (int i=0; i < konfiguration.getAusgabeAnzahl(); i++) {
      final AusgabeAuswahl ausgabe = konfiguration.getAusgabe(i);
      if (!ausgabe.istMediumAuswahl()) {
        ErrorHandler.getInstance().handleError("Ausgabe '"+ausgabe.getTitel()+
            "' ist keine Medium-Auswahl!", false);                 
      } else {
        auswahlComboBox.addItem(new TabellenSelektion() {            
          public String toString() {
            return ausgabe.getTitel();
          }

          protected boolean istInSelektion(Object o) {             
            return ausgabe.bewerte((Medium) o);              
          }
        });
      }
    }
  }
}