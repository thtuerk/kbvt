package de.oberbrechen.koeb.gui.components;

import javax.swing.Timer;
import java.awt.event.*;
import java.util.Vector;
import java.util.Iterator;

/**
 * Diese Klasse stellt einen ItemListener dar, der die empfangenen Events
 * zwischenspeichert und erst nach einer bestimmten Zeit an andere ItemListener
 * weiterleitet. Tritt in dieser Zeit ein weiteres Event ein, so wird das
 * vorherige Event verworfen und die Wartezeit neu begonnen.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class DelayItemListener implements ItemListener {

  private boolean enabled;
  private Timer timer;
  ItemEvent itemEvent;
  Vector<ItemListener> listeners;

  /**
   * Erstellt einen neuen DelayItemListener der die Events nach dem übergebenen
   * Zeitraum in ms weiterleitet.
   *
   * @param delay die Wartezeit in ms
   */
  public DelayItemListener(int delay) {
    listeners = new Vector<ItemListener>();
    timer = new javax.swing.Timer(delay, new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Iterator<ItemListener> it = listeners.iterator();
        while (it.hasNext()) {
          it.next().itemStateChanged(itemEvent);
        }
      }
    });

    timer.setInitialDelay(delay);
    timer.setRepeats(false);
    enabled=true;
  }

  public void itemStateChanged(ItemEvent e) {
    if (!enabled) return;
    itemEvent = e;
    timer.restart();
  }

  /**
   * Fügt einen neuen Listener, an den die eintreffenden Events weitergeleitet
   * werden sollen ein.
   *
   * @param itemListener der neue Listener
   */
  public void addItemListener(ItemListener itemListener) {
    listeners.add(itemListener);
  }

  /**
   * Bricht die Wartezeit sofort ab und leitet das wartende Event sofort weiter.
   */
  public void fireDelayItemListenerEvent() {
    if (!timer.isRunning()) return;

    timer.stop();
    Iterator<ItemListener> it = listeners.iterator();
    while (it.hasNext()) {
      it.next().itemStateChanged(itemEvent);
    }
  }

  /**
   * Bestimmt, ob der DelayItemListener aktiv ist, d.h. ob er
   * irgendwelche Events weiterleitet. Ein bereits gestarteter
   * Timer wird aber nicht abgebrochen. 
   * @param enabled
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
