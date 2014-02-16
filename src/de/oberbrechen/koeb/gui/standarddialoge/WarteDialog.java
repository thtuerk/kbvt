package de.oberbrechen.koeb.gui.standarddialoge;

import javax.swing.*;
import java.awt.*;

/**
 * Diese Klasse zeigt einen Dialog an, der das Logo von KBVT anzeigt und
 * zur Überbrückung von Wartezeiten dient.
 */
public class WarteDialog extends JWindow {
  /**
   * Erzeugt eine neue Instanz, die den Dialog als Chield des übergebenen
   * Frames anzeigt.
   *
   * @param mainWindow das Hauptfenster
   */
  public WarteDialog() {
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


  private void jbInit() throws Exception {
    this.getContentPane().setLayout(new BorderLayout());

    java.net.URL iconURL = ClassLoader.getSystemResource(
      "de/oberbrechen/koeb/gui/standarddialoge/wartedialog.png");
    if (iconURL != null) {
      ImageIcon icon = new ImageIcon(iconURL);
      JLabel bild = new JLabel(icon);
      this.getContentPane().add(bild, BorderLayout.CENTER);

      this.setSize(icon.getIconWidth(), icon.getIconHeight());
    }
  }

}