package de.oberbrechen.koeb.dateien.einstellungenDoku.einstellungenTests;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungTest;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.framework.HexEncoder;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeGelesenEventHandler;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeReaderAdapter;

/**
 * Diese Klasse führt einen PDF-Test aus.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BarcodeScannerEinstellungenTest implements EinstellungTest, 
  BarcodeGelesenEventHandler {

  private JDialog dialog;
  private JPanel ausgabe;
  private BarcodeReaderAdapter barcodeReaderAdapter;
  private JTextField eingabeFeld;
  private JTextArea hexEingabe;
  
  public BarcodeScannerEinstellungenTest() {
    jbInit();
  }
  
  private void jbInit() {    
    hexEingabe = new JTextArea();
    ausgabe = new JPanel();
              
    eingabeFeld = new JTextField();
        
    ausgabe.setLayout(new GridBagLayout());
    ausgabe.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    
    hexEingabe.setWrapStyleWord(true);
    hexEingabe.setLineWrap(true);
    hexEingabe.setMargin(new Insets(5, 5, 5, 5));
    hexEingabe.setEditable(false);
    hexEingabe.setTabSize(4);
    hexEingabe.setFont(new java.awt.Font("Monospaced", 0, 12));
   
    JButton zurueckButton = new JButton("zurücksetzen");
    zurueckButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        hexEingabe.setText("");
        eingabeFeld.setText("");
        eingabeFeld.requestFocus();
      }});
    
    
    JScrollPane hexScrollPane = new JScrollPane();
    hexScrollPane.setAutoscrolls(true);
    hexScrollPane.setMinimumSize(new Dimension(357, 100));
    hexScrollPane.setPreferredSize(new Dimension(357, 100));
    
    ausgabe.add(new JLabel("Eingabe:"),     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
    ausgabe.add(eingabeFeld,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabe.add(new JLabel("Hex-Darstellung:"),    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    ausgabe.add(hexScrollPane,    new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    ausgabe.add(zurueckButton,    new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
    hexScrollPane.getViewport().add(hexEingabe);
    
    eingabeFeld.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent arg0) {}

      public void keyReleased(KeyEvent arg0) {}

      public void keyTyped(KeyEvent e) {
        hexEingabe.setText(hexEingabe.getText()+HexEncoder.hexEncodeChar(e.getKeyChar())+" ");
      }      
    });
    
    ausgabe.setPreferredSize(new Dimension(780, 480));
  }
     
  
  public void testeEinstellung(JFrame main, Einstellung einstellung) throws Exception {
    //Alten barcodeReaderAdapter entfernen
    if (barcodeReaderAdapter != null && barcodeReaderAdapter instanceof KeyAdapter)
      eingabeFeld.removeKeyListener((KeyAdapter) barcodeReaderAdapter);        
    
    
    try {
      barcodeReaderAdapter = (BarcodeReaderAdapter) Datenbank.getInstance().
      getEinstellungFactory().getClientEinstellung(
          BarcodeReaderAdapter.class.getName(), "instance").
          getWertObject(BarcodeReaderAdapter.class, null);
    } catch (UnpassendeEinstellungException e) {
      barcodeReaderAdapter = null;
      //Sollte nie auftreten, wegen Parameter null vonm getWertObject
      ErrorHandler.getInstance().handleException(e, false);            
    }

    if (barcodeReaderAdapter == null) {
      JOptionPane.showMessageDialog(main, "Kein Barcodescanner eingestellt!",
          "Kein Barcodescanner!", JOptionPane.WARNING_MESSAGE);  
      return;
    }
    
    
    barcodeReaderAdapter.setEventHandler(this);    
    if (barcodeReaderAdapter instanceof KeyAdapter)
      eingabeFeld.addKeyListener((KeyAdapter) barcodeReaderAdapter);      
    
    
    dialog = new JDialog(main, "Barcodescanner-Test", true);
    dialog.getContentPane().add(ausgabe);
    dialog.pack();
    //dialog.setLocationRelativeTo(main);
    hexEingabe.setText("");
    eingabeFeld.setText("");
    eingabeFeld.requestFocus();
    dialog.setVisible(true);
  }

  public void barcodeGelesen(String barcode) {
    JOptionPane.showMessageDialog(dialog, "Barcode '"+barcode+"' gelesen!",
        "Barcode gelesen!", JOptionPane.INFORMATION_MESSAGE);
  }

  public void barcodeStartGelesen() {
  }
}
