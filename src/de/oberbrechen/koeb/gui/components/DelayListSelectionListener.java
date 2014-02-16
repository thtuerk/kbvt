package de.oberbrechen.koeb.gui.components;

import javax.swing.Timer;
import javax.swing.event.*;

import java.awt.event.*;
import java.util.Vector;
import java.util.Iterator;

/**
 * Diese Klasse stellt einen ListSelectitionListener da,
 * der die empfangenen Events
 * zwischenspeichert und erst nach einer bestimmten Zeit an andere
 * ListSelectionListener
 * weiterleitet. Tritt in dieser Zeit ein weiteres Event ein, so wird das
 * vorherige Event verworfen und die Wartezeit neu begonnen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class DelayListSelectionListener implements ListSelectionListener {

  private Timer timer;
  ListSelectionEvent listSelectionEvent;
  Vector<ListSelectionListener> listeners;

  /**
   * Erstellt einen neuen DelayListSelectionListener,
   * der die Events nach dem übergebenen
   * Zeitraum in ms weiterleitet.
   *
   * @param delay die Wartezeit in ms
   */
  public DelayListSelectionListener(int delay) {
    listeners = new Vector<ListSelectionListener>();
    timer = new javax.swing.Timer(delay, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Iterator<ListSelectionListener> it = listeners.iterator();
        while (it.hasNext()) {
          it.next().valueChanged(listSelectionEvent);
        }
      }
    });

    timer.setInitialDelay(delay);
    timer.setRepeats(false);
  }

  public void valueChanged(ListSelectionEvent e) {
    listSelectionEvent = e;
    timer.restart();
  }

  /**
   * Fügt einen neuen Listener, an den die eintreffenden Events weitergeleitet
   * werden sollen ein.
   *
   * @param listener der neue Listener
   */
  public void addListSelectionListener(ListSelectionListener listener) {
    listeners.add(listener);
  }

  /**
   * Bricht die Wartezeit sofort ab und leitet das wartende Event sofort weiter.
   */
  public void fireDelayListSelectionEvent() {
    if (!timer.isRunning()) return;

    timer.stop();
    Iterator<ListSelectionListener> it = listeners.iterator();
    while (it.hasNext()) {
      it.next().valueChanged(listSelectionEvent);
    }
  }
}
