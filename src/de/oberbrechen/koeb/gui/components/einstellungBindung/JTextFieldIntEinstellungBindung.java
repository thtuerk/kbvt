package de.oberbrechen.koeb.gui.components.einstellungBindung;


import java.awt.event.*;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse dient dazu String-Einstellung an eine 
 * JTextField zu binden. Initial wird dem JTextField der Wert der 
 * Einstellung zugewiesen. Bei Änderung des JTextFields wird automatisch
 * der Einstellungswert in der Datenbank aktualisiert.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class JTextFieldIntEinstellungBindung 
  implements JComponentEinstellungBindung {
    
  final int standard;
  final JTextField jTextField;
  final Einstellung einstellung;  
  final JFrame hauptFenster;
    
  /**
   * Erstellt eine neue JTextFieldIntEinstellungBindung, die
   * das übergebene JTextField an die beschriebene Einstellung 
   * bindet und als Standardwert standard verwendet.
   * @param jCheckBox
   * @param einstellung
   * @param standard
   */
  public JTextFieldIntEinstellungBindung(JFrame hauptFenster, 
    JTextField jTextField, Einstellung einstellung, 
    int standard) {

    this.hauptFenster = hauptFenster;
    this.standard = standard;
    this.jTextField = jTextField;
    this.einstellung = einstellung;

    init();
    refresh();
  }
  
  private void init() {
    final javax.swing.Timer timer = new javax.swing.Timer(500, 
      new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setzeWert(false);
      }
    });
    timer.setRepeats(false);
    
    jTextField.addKeyListener(new KeyListener() {

      public void keyTyped(KeyEvent e) {}
      public void keyReleased(KeyEvent e) {}

      public void keyPressed(KeyEvent e) {
        timer.restart();        
      }

    });
    jTextField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
      }
  
      public void focusLost(FocusEvent e) {
        setzeWert(true);
      }
    });
    jTextField.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        setzeWert(true);
      }    
    });
  }

  //Doku siehe bitte Interface
  public void refresh() {
    jTextField.setText(Integer.toString(einstellung.getWertInt(
      standard)));        
  }

  /**
   * Speichert den im Feld gespeicherten Wert in der Datenbank.
   * @param zeigeFehler sollen Fehler beim Speichern gemeldet oder
   *  ignoriert werden
   */
  void setzeWert(boolean zeigeFehler) {
    String value = jTextField.getText();
    try {
      int intValue = Integer.parseInt(value);
      einstellung.setWertInt(intValue);
      einstellung.save();       
    } catch (NumberFormatException e) {
      if (zeigeFehler) {
        refresh();
        JOptionPane.showMessageDialog(hauptFenster, "Die Eingabe '"+
          value + "' kann\nnicht als positive Zahl interpretiert\n"+
          "werden!",
          "Ungültige Eingabe!",
          JOptionPane.ERROR_MESSAGE);
      }
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }        
  }
}