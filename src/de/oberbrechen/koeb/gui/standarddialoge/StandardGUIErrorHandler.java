package de.oberbrechen.koeb.gui.standarddialoge;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.Writer;

import javax.swing.*;

import de.oberbrechen.koeb.email.EMail;
import de.oberbrechen.koeb.email.EMailHandler;
import de.oberbrechen.koeb.email.ErrorEMailFactory;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse ist eine Standardimplemntierung des Interfaces
 * DatenbankErrorHandler für Swing-Oberflächen. Tritt ein Datenbankfehler
 * auf zeigt, dieser ErrorHandler diesen in einem Dialogfeld an und beendet
 * anschließend das System.
 */
public class StandardGUIErrorHandler extends ErrorHandler {

  class NachrichtenDialog {    
    private JPanel ausgabe;
    
    public NachrichtenDialog(JFrame mainWindow, String beschreibung, 
        boolean istKritisch) {
      main = mainWindow;
      jbInit(beschreibung, istKritisch);      
      JOptionPane.showMessageDialog(main, ausgabe,
          "Problem!", JOptionPane.ERROR_MESSAGE);
    }
    
    private void jbInit(final String beschreibung, boolean istKritisch) {
      JTextArea fehlerMeldung = new JTextArea();
      ausgabe = new JPanel();
  
      JLabel jLabel1 = new JLabel("Es trat ein unerwartetes Problem auf.");
      JLabel jLabel2 = new JLabel("Die Fehlermeldung lautet:");
      JLabel beendenLabel = new JLabel();
  
      final JButton eMailButton = new JButton("eMail an Admin");
      eMailButton.setEnabled(true);
      eMailButton.addActionListener(new ActionListener() {
  
        public void actionPerformed(ActionEvent arg0) {
          if (!EMailHandler.getInstance().erlaubtAnzeige())
            eMailButton.setEnabled(false);
          
          new Thread() {
            public void run() {
              EMail eMail = ErrorEMailFactory.getInstance().erstelleErrorEmail(beschreibung);
              EMailHandler.getInstance().versende(eMail, true);                              
            }
          }.run();        
        }
        
      });
      
      beendenLabel.setFont(new Font(jLabel1.getFont().getName(),
        Font.BOLD, 15));
      beendenLabel.setForeground(Color.red);
      beendenLabel.setHorizontalAlignment(SwingConstants.LEFT);
      beendenLabel.setText("System wird beendet!");
      beendenLabel.setVisible(istKritisch);
      
      ausgabe.setLayout(new GridBagLayout());
      ausgabe.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
  
      fehlerMeldung.setWrapStyleWord(true);
      fehlerMeldung.setLineWrap(true);
      fehlerMeldung.setMargin(new Insets(5, 5, 5, 5));
      fehlerMeldung.setEditable(false);
      fehlerMeldung.setTabSize(4);
      fehlerMeldung.setFont(new java.awt.Font("Monospaced", 0, 12));
      fehlerMeldung.setText(beschreibung);
      
      JScrollPane fehlerMeldungScrollPane = new JScrollPane();
      fehlerMeldungScrollPane.setAutoscrolls(true);
      fehlerMeldungScrollPane.setMinimumSize(new Dimension(357, 100));
      fehlerMeldungScrollPane.setPreferredSize(new Dimension(357, 100));
  
      ausgabe.add(jLabel1,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      ausgabe.add(jLabel2,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
      ausgabe.add(eMailButton,     new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0
          ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      ausgabe.add(fehlerMeldungScrollPane,    new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
              ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      ausgabe.add(beendenLabel,      new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
              ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
      fehlerMeldungScrollPane.getViewport().add(fehlerMeldung);
      
      ausgabe.setPreferredSize(new Dimension(780, 480));
    }
  }
  
  private JFrame main;

  /**
   * Erzeugt eine neue Instanz, die das Dialogfeld als Chield des übergebenen
   * Frames anzeigt.
   *
   * @param mainWindow das Hauptfenster
   */
  public StandardGUIErrorHandler(JFrame mainWindow) {
    main = mainWindow;
  }
  
  public void handleError(final String beschreibung, final boolean istKritisch) {    
    new Thread() {
      public void run() {
        new NachrichtenDialog(main, beschreibung, istKritisch);   
        if (istKritisch) System.exit(1);
      }
    }.start();
  }

  public void handleException(final Exception e, final String beschreibung, 
      final boolean istKritisch) {
    new Thread() {
      public void run() {
        final StringBuffer buffer = new StringBuffer();
        e.printStackTrace(new PrintWriter(new Writer(){
          public void write(char[] cbuf, int off, int len) {
            buffer.append(cbuf, off, len);
          }

          public void flush() {}
          public void close() {}
        }));
        
        String nachricht = e.getLocalizedMessage();
        if (beschreibung != null) nachricht = beschreibung + "\n\n"+nachricht;
        nachricht += "\n\n" + buffer.toString();

        e.printStackTrace();
        handleError(nachricht, istKritisch);
      }      
    }.start();
  }
}