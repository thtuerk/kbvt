package de.oberbrechen.koeb.gui.standarddialoge;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import com.toedter.calendar.*;
import com.toedter.components.JSpinField;

/**
 * Diese Klasse zeigt einen Dialog an, der die Auswahl eines Datums aus einem
 * Kalender ermöglicht.
 */
public class DatumAuswahlDialog {

  private JPanel uhrzeitPanel;
  private JSpinField stundenFeld;
  private JSpinField minutenFeld;
  
  private JFrame main;
  private JPanel ausgabe;
  private JCalendar kalender;
  private JLabel beschriftung;

  /**
   * Erzeugt eine neue Instanz, die den Dialog als Chield des übergebenen
   * Frames anzeigt.
   *
   * @param mainWindow das Hauptfenster
   */
  public DatumAuswahlDialog(JFrame mainWindow) {
    main = mainWindow;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Zeigt eine Dialogfeld zur Auswahl des Datums an.
   * @param beschrifung die dabei anzuzeigende Beschriftung
   * @param startDatum das Datum das zu Beginn angezeigt wird
   * @return das gewählte Datum oder <code>null</code>, falls kein
   *   Datum gewählt wurde
   */
  public Date waehleDatum(String beschriftung, Date startDatum) {
    return waehleDatum(beschriftung, startDatum, false);
  }

  /**
   * Zeigt eine Dialogfeld zur Auswahl des Datums an.
   * @param beschrifung die dabei anzuzeigende Beschriftung
   * @param startDatum das Datum das zu Beginn angezeigt wird
   * @param waehleUhrzeit bestimmt, ob nicht nur das Datum, sondern
   *    auch die Zeit gewählt werden soll
   * @return das gewählte Datum oder <code>null</code>, falls kein
   *   Datum gewählt wurde
   */
  public Date waehleDatum(String beschriftung, Date startDatum, 
    boolean waehleUhrZeit) {
    if (startDatum == null) startDatum = new Date();
    if (beschriftung == null) beschriftung = "Bitte Datum wählen:";
    this.beschriftung.setText(beschriftung);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDatum);
    minutenFeld.setValue(calendar.get(Calendar.MINUTE));
    stundenFeld.setValue(calendar.get(Calendar.HOUR_OF_DAY));
    kalender.setCalendar(calendar);
    return waehleDatumIntern(waehleUhrZeit);
  }

  /**
   * Zeigt eine Dialogfeld zur Auswahl des Datums an.
   * @return das gewählte Datum oder <code>null</code>, falls kein
   *   Datum gewählt wurde
   */
  public Date waehleDatum() {
    return waehleDatum(null, null);
  }

  private Date waehleDatumIntern(boolean waehleUhrzeit) {
    SwingUtilities.updateComponentTreeUI(ausgabe);
    uhrzeitPanel.setVisible(waehleUhrzeit);

    int erg = JOptionPane.showConfirmDialog(main, ausgabe,"Datum-Auswahl",
      JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (erg != JOptionPane.OK_OPTION) return null;

    Calendar calendar = kalender.getCalendar(); 
    calendar.set(Calendar.MINUTE, minutenFeld.getValue());
    calendar.set(Calendar.HOUR_OF_DAY, stundenFeld.getValue());
    
    return calendar.getTime();
  }

  private void jbInit() throws Exception {
    ausgabe = new JPanel();
    ausgabe.setLayout(new BorderLayout());

    beschriftung = new JLabel();
    beschriftung.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    kalender = new JCalendar();

    //Uhrzeitpanel
    uhrzeitPanel = new JPanel();
    uhrzeitPanel.setLayout(new FlowLayout());

    stundenFeld = new JSpinField();
    stundenFeld.setMaximum(23);
    stundenFeld.setMinimum(0);
    minutenFeld = new JSpinField();
    minutenFeld.setMaximum(59);
    minutenFeld.setMinimum(0);

    Dimension standardDimension =
      new Dimension(40, stundenFeld.getPreferredSize().height);
    stundenFeld.setPreferredSize(standardDimension);
    minutenFeld.setPreferredSize(standardDimension);

    uhrzeitPanel.add(stundenFeld);
    uhrzeitPanel.add(new JLabel(":"));
    uhrzeitPanel.add(minutenFeld);
    uhrzeitPanel.add(new JLabel("Uhr"));

    ausgabe.add(kalender, BorderLayout.CENTER);
    ausgabe.add(beschriftung, BorderLayout.NORTH);
    ausgabe.add(uhrzeitPanel, BorderLayout.SOUTH);
  }

}