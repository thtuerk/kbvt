package de.oberbrechen.koeb.gui.standarddialoge;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse zeigt einen Dialog an, der die Auswahl eines Datums aus einem
 * Kalender ermöglicht.
 */
public class InfoDialog extends JDialog {
  /**
   * Erzeugt eine neue Instanz, die den Dialog als Chield des übergebenen
   * Frames anzeigt.
   *
   * @param mainWindow das Hauptfenster
   */
  public InfoDialog() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.pack();
    
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((dim.width - this.getWidth())/2, (dim.height - this.getHeight())/2);
  }

  /**
   * Liest die Übergebene Resource in einen String.
   * @param resource die zu lesende Resource
   * @return den gelesenen String
   */
  private String readInString(String resource) {
    
    StringBuffer result = new StringBuffer();
    try {
      InputStream stream = ClassLoader.getSystemResourceAsStream(
          resource);
      int c;
      while ((c = stream.read()) != -1) {
        result.append((char)c);
      }
      stream.close();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Lesen der Datei '"+resource+"'!", false);      
    }    
    
    return result.toString();
  }

  private void jbInit() throws Exception {
    this.setTitle("Über KBVT");
        
    JButton closeButton = new JButton("schließen");
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        dispose();
      }      
    });
    
    JTabbedPane jTabbedPane = new JTabbedPane();
    JScrollPane ueberScrollPane =
      getTextScrollPane(readInString("de/oberbrechen/koeb/gui/standarddialoge/info.txt"));      
    JScrollPane lizenzScrollPane =
      getTextScrollPane(readInString("de/oberbrechen/koeb/gui/standarddialoge/lizenz.txt"));      
    jTabbedPane.add(ueberScrollPane, "Über");
    jTabbedPane.add(lizenzScrollPane, "Lizenz");
    
    this.getContentPane().setLayout(new GridBagLayout());
    this.getContentPane().add(jTabbedPane,
        new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
        ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
    this.getContentPane().add(closeButton,
        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));    
  }
  
  private JScrollPane getTextScrollPane(String text) {    
    JTextArea meldung = new JTextArea();
    meldung.setFont(new java.awt.Font("Monospaced", 0, 14));
    
    meldung.setWrapStyleWord(true);
    meldung.setLineWrap(false);
    meldung.setMargin(new Insets(10, 10, 10, 10));
    meldung.setEditable(false);
    meldung.setOpaque(false);
    meldung.setTabSize(4);

    JScrollPane meldungScrollPane = new JScrollPane();
    meldungScrollPane.setAutoscrolls(true);
    meldungScrollPane.setMinimumSize(new Dimension(500, 200));
    meldungScrollPane.setPreferredSize(new Dimension(500, 200));

    meldungScrollPane.getViewport().add(meldung);
    meldungScrollPane.setBorder(new EmptyBorder(5,5,5,5));
    meldung.setText(text);

    return meldungScrollPane;
  }

}