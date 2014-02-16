package de.oberbrechen.koeb.gui.components.einstellungBindung;

import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JTextField;

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

public class JTextFieldEinstellungBindung implements JComponentEinstellungBindung {

  final Einstellung einstellung;  
  final JTextField jTextField;
  final JFrame hauptFenster;
  final String standard;
    
  /**
   * Erstellt eine neue JTextFieldEinstellungBindung, die
   * das übergebene JTextField an die beschriebene Einstellung 
   * bindet und als Standardwert standard verwendet.
   * @param jCheckBox
   * @param einstellung
   * @param standard
   */
  public JTextFieldEinstellungBindung(JFrame hauptFenster, 
    JTextField jTextField, Einstellung einstellung, String standard) {
      
    this.hauptFenster = hauptFenster;
    this.standard = standard;
    this.jTextField = jTextField;
    this.einstellung = einstellung;

    init();
    refresh();
  }

  /**
   * Speichert den im Feld gespeicherten Wert in der Datenbank.
   * @param zeigeFehler sollen Fehler beim Speichern gemeldet oder
   *  ignoriert werden
   */
  void setzeWert() {
    String value = jTextField.getText();
    einstellung.setWert(value);
    try {
      einstellung.save(); 
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }        
  }
  
  private void init() {
    final javax.swing.Timer timer = new javax.swing.Timer(500, 
      new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setzeWert();
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
        setzeWert();
      }
    });
    jTextField.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        setzeWert();
      }    
    });
  }

  //Doku siehe bitte Interface
  public void refresh() {
    jTextField.setText(einstellung.getWert(standard));        
  }
}