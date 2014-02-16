package de.oberbrechen.koeb.gui.components.listenAuswahlPanel;

import javax.swing.ListSelectionModel;
import de.oberbrechen.koeb.datenstrukturen.Liste;

/**
 * Diese Klasse repräsentiert eine Selektion, die in Tabelle durchgeführt
 * werden soll. 
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class TabellenSelektion {
      
  public void selektiere(Liste<?> liste, ListSelectionModel model) {
    model.setValueIsAdjusting(false);
    model.clearSelection();
    for (int i=0; i < liste.size(); i++) {
      if (istInSelektion(liste.get(i))) model.addSelectionInterval(i, i);
    }    
    model.setValueIsAdjusting(true);
  }
  
  protected abstract boolean istInSelektion(Object o);
}
