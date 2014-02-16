package de.oberbrechen.koeb.gui.components.listenAuswahlPanel.systematikListenAuswahlPanel;

import javax.swing.JTable;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.SystematikFactory;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.ListenAuswahlPanel;

/**
* Diese Klasse ist eine Implementierung
* des ListenAuswahlPanels für Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class SystematikListenAuswahlPanel extends ListenAuswahlPanel<Systematik> {

  public SystematikListenAuswahlPanel() {
    super(true, false);
  }

  protected AbstractListeSortierbaresTableModel<Systematik> createDatenAuswahlTableModel(JTable table, int auswahlTyp) {
    SystematikFactory systematikFactory = 
      Datenbank.getInstance().getSystematikFactory();
    SystematikListe alleSystematiken = systematikFactory.getAlleSystematiken();
    
    return new SystematikTableModel(alleSystematiken);
  }

  protected AbstractListeSortierbaresTableModel<Systematik> createEmptyAuswahlTableModel(JTable table, int auswahlTyp) {
    return new SystematikTableModel(new SystematikListe());
  }
}