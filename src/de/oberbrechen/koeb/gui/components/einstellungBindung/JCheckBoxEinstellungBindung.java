package de.oberbrechen.koeb.gui.components.einstellungBindung;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse dient dazu eine BOOL-Wertige-Einstellung an eine 
 * JCheckBox zu binden. Initial wird der JCheckbox der Wert der 
 * Einstellung zugewiesen. Bei Änderung der Checkbox wird automatisch
 * der Einstellungswert in der Datenbank aktualisiert.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class JCheckBoxEinstellungBindung implements JComponentEinstellungBindung {

  final JCheckBox jCheckBox;
  final boolean standard;
  final Einstellung einstellung;  
    
  /**
   * Erstellt eine neue JCheckBoxEinstellungBindung, die
   * die übergebene Checkbox an die beschriebene Einstellung bindet und
   * als Standardwert standard verwendet.
   * @param jCheckBox
   * @param einstellung
   * @param standard
   */
  public JCheckBoxEinstellungBindung(JCheckBox jCheckBox, 
    Einstellung einstellung, boolean standard) {

    this.jCheckBox = jCheckBox;
    this.standard = standard;
    this.einstellung = einstellung;

    initActionListener();
    refresh();
  }

  private void initActionListener() {
    jCheckBox.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        boolean value = jCheckBox.isSelected();
        einstellung.setWertBoolean(value);
        try {
          einstellung.save();
        } catch (DatenbankzugriffException e2) {
          ErrorHandler.getInstance().handleException(e2, false);
        }
      }    
    });
  }

  public void refresh() {
    jCheckBox.setSelected(einstellung.getWertBoolean(standard));        
  }
}