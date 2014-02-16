package de.oberbrechen.koeb.gui.components;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.MutableComboBoxModel;

import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManager;
import de.oberbrechen.koeb.gui.components.listenKeySelectionManager.ListenKeySelectionManagerListenDaten;

class FormatWrapper<T> {
  T daten;
  Format<T> format;

  public FormatWrapper(T daten, Format<T> format) {
    this.daten = daten;
    this.format = format;
  }

  public String toString() {
    return format.format(daten);
  }
}

/**
 * Der StandardKeySelectionManager für die SortiertComboBox.
 */
class DefaultKeySelectionManager implements JComboBox.KeySelectionManager {

  StringBuffer pattern;
  long lastKeyPress;
  ListenKeySelectionManagerListenDaten daten;
  ListenKeySelectionManager keySelectionManager;

  public DefaultKeySelectionManager(final Format<Object> format, final ComboBoxModel aModel) {
    pattern = new StringBuffer();
    lastKeyPress = 0;
    daten = new ListenKeySelectionManagerListenDaten() {
      @SuppressWarnings("unchecked")
      public String getKeySelectionValue(int row) {
        return format.format(aModel.getElementAt(row));
      }

      public int size() {
        return aModel.getSize();
      }
    };
    keySelectionManager = new ListenKeySelectionManager(daten);
  }

  public int selectionForKey(char aKey, ComboBoxModel aModel) {
    return keySelectionManager.selectionForKey(aKey);
  }
}

/**
 * Das Model der SortiertComboBox.
 */
class SortiertMutableComboBoxModel<T> extends AbstractListModel
  implements MutableComboBoxModel {

  private Liste<T> daten;
  private T selected;
  private boolean ergaenzeNull = false;
  
  public SortiertMutableComboBoxModel(Liste<T> daten) {
    super();
    this.setDaten(daten);
  }

  public void setDaten(Liste<T> daten) {
    this.daten = daten;
    selected = null;
    if (!daten.isEmpty()) selected = daten.first();
    this.fireContentsChanged(this, 0, getSize()-1);
  }

  public void setErgaenzeNull(boolean ergaenzeNull) {
    this.ergaenzeNull = ergaenzeNull;
    this.fireContentsChanged(this, 0, getSize()-1);    
  }

  public void addAll(Collection<? extends T> c) {
    daten.addAll(c);
    if (selected == null && !daten.isEmpty()) selected = daten.first();
    this.fireContentsChanged(this, 0, daten.size()-1);
  }
  
  @SuppressWarnings("unchecked")
  public void addElement(Object obj) {
    daten.add((T) obj);
    if (selected == null && !ergaenzeNull && !daten.isEmpty()) selected = daten.first();
    this.fireContentsChanged(this, 0, getSize()-1);
  }

  @SuppressWarnings("unchecked")
  public void removeElement(Object obj) {
    daten.remove((T) obj);
    if (daten.isEmpty() || obj == selected) selected = null;
    if (selected == null && !daten.isEmpty()) selected = daten.first();
    this.fireContentsChanged(this, 0, daten.size()-1);
  }

  @SuppressWarnings("unchecked")
  public void insertElementAt(Object obj, int index) {
    if (ergaenzeNull) index++;
    daten.add((T) obj);
    if (selected == null && !daten.isEmpty()) selected = daten.first();
    this.fireContentsChanged(this, 0, getSize()-1);
  }

  public void removeElementAt(int index) {
    T obj = this.getElementAt(index);
    this.removeElement(obj);
    this.fireContentsChanged(this, 0, getSize()-1);
  }

  @SuppressWarnings("unchecked")
  public void setSelectedItem(Object obj) {
    addItem((T) obj);
    
    if (selected != obj) {
      selected = (T) obj;
      fireContentsChanged(this, -1, -1);
    }
  }

  public T getSelectedItem() {
    return selected;
  }

  public int getSize() {
    return ergaenzeNull?daten.size()+1:daten.size();
  }

  public T getElementAt(int index) {
    if (ergaenzeNull) index--;

    if (index < 0 || index >= daten.size()) return null;
    return daten.get(index);
  }

  public void addItem(T obj) {
    if (obj != null && !daten.contains(obj))
      this.addElement(obj);
  }

}


/**
 * Eine SortierComboBox ist eine JComboBox, die ihre Listeneinträge sortiert.
 * Außerdem ist es möglich, das Format, in dem die Einträge angezeigt werden
 * zu bestimmen.
 */
public class SortiertComboBox<T> extends JComboBox {

  private SortiertMutableComboBoxModel<T> model;
  private DelayItemListener delayItemListener;
  private DefaultKeySelectionManager selectManager; 
  

  /**
   * Erstellt eine SortiertComboBox, die die Listeneinträge mittels ihrer
   * toString Methode darstellt und nach dieser Stringdarstellung sortiert.
   */
  public SortiertComboBox() {
    this(true);
  }

  /**
   * Erstellt eine SortiertComboBox, die die Listeneinträge mittels ihrer
   * toString Methode darstellt und nach dieser Stringdarstellung sortiert.
   */
  public SortiertComboBox(boolean sortiert) {
    Liste<T> daten = new Liste<T>();
    if (sortiert) daten.setSortierung(new Format<T>());
    init(daten, new Format<T>());
  }

  /**
   * Erstellt eine SortiertComboBox, die die Listeneinträge mittels ihrer
   * toString Methode darstellt und nach dieser Stringdarstellung sortiert.
   */
  public SortiertComboBox(boolean sortiert, Format<T> format) {
    Liste<T> daten = new Liste<T>();
    if (sortiert) daten.setSortierung(format);
    init(daten, format);
  }

  /**
   * Erstellt eine SortiertComboBox, die die Listeneinträge mittels ihrer
   * toString Methode darstellt und die im übergegeben SortedSet gespeicherten
   * Daten in der dort festgelegten Reihenfolge enthält.
   *
   * @param daten die Daten, die dargestellt werden sollen
   */
  public SortiertComboBox(Liste<T> daten) {
    init(daten, new Format<T>());
  }

  /**
   * Erstellt eine SortiertComboBox, die die Listeneinträge mittels des
   * übergebenen Formats darstellt und nach dieser Stringdarstellung sortiert.
   *
   * @param format das Format, das zum Darstellen verwendet wird
   */
  public SortiertComboBox(final Format<T> format) {
    Liste<T> daten = new Liste<T>();
    daten.setSortierung(format);
    init(daten, format);
  }
 
  /**
   * Erstellt eine SortiertComboBox, die die Listeneinträge mittels des
   * übergebenen Formats darstellt und die im übergegeben SortedSet
   * gespeicherten Daten in der dort festgelegten Reihenfolge enthält.
   *
   * @param format das Format, das zum Darstellen verwendet wird
   * @param daten die Daten, die dargestellt werden sollen
   */
  public SortiertComboBox(final Liste<T> daten, final Format<T> format) {
    init(daten, format);
  }
  
  /**
   * Wird für Construktoren benutzt.
   * @param format das Format, das zum Darstellen verwendet wird
   * @param daten die Daten, die dargestellt werden sollen
   */
  @SuppressWarnings("unchecked")
  private void init(final Liste<T> daten, final Format<T> format) {
    model = new SortiertMutableComboBoxModel<T>(daten);
    this.setModel(model);

    delayItemListener = new DelayItemListener(500);
    this.addItemListener(delayItemListener);
    selectManager = new DefaultKeySelectionManager((Format<Object>) format, model);
    this.setKeySelectionManager(selectManager);
    
    this.setRenderer(new DefaultListCellRenderer() {
      @SuppressWarnings("unchecked")
      public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) super.getListCellRendererComponent(
          list, value, index, isSelected, cellHasFocus);
        renderer.setText(format.format((T) value));
        return renderer;
      }
    });
  }


  public void setKeySelectionManager(KeySelectionManager aManager) {
    super.setKeySelectionManager(selectManager);
  }

  /**
   * Fügt einen neuen ItemListener zu der SortiertComboBox hinzu, der aber die
   * Auswahl eines neuen Items erst gemeldet bekommt, nachdem das Item
   * mindestens eine gewisse Zeit ausgewählt blieb.   *
   * Dies kann sinnvoll sein, um bei Comboboxen, die viele Daten
   * enthalten und bei denen das Wechseln der Auswahl
   * eine längere Aktion anstößt, trotzdem
   * schnell durch die Einträge scrollen zu können.
   *
   * @param itemListener der neue Listener
   */
  public void addDelayItemListener(ItemListener itemListener) {
    delayItemListener.addItemListener(itemListener);
  }

  /**
   * Bricht die Wartezeit sofort ab und leitet das wartende Event sofort weiter.
   */
  public void fireDelayItemListenerEvent() {
    delayItemListener.fireDelayItemListenerEvent();
  }

  /**
   * Aktiviert bzw deaktiviert alle DelayItemListener.
   */
  public void setDelayItemListenerEnabled(boolean enabled) {
    delayItemListener.setEnabled(enabled);
  }

  /**
   * Setzt die Listeneinträge auf die im übergegeben SortedSet gespeicherten
   * Daten in der dort festgelegten Reihenfolge.
   *
   * @param daten die Daten, die dargestellt werden sollen
   */
  public void setDaten(Liste<T> daten) {
    model.setDaten(daten);
  }

  /**
   * Hiermit kann eingestellt werden, ob in der ersten Zeile der Auswahl der Kombobox NULL 
   * zustätzlich zu den in der Liste vorhanden Werten dargestellt wird. 
   * @param ergaenzeNull
   */
  public void setErgaenzeNull(boolean ergaenzeNull) {    
    model.setErgaenzeNull(ergaenzeNull);    
  }

  /**
   * Fügt die in der Collection enthalten Daten zur Liste hinzu.
   * @param c die hinzuzufügende Collection
   */
  public void addAll(Collection<? extends T> c) {
    model.addAll(c);
  }


  @SuppressWarnings("unchecked")
  public void setSelectedItem(Object arg0) {
    model.addItem((T) arg0);
    super.setSelectedItem(arg0);
  }
  
  @SuppressWarnings("unchecked")
  public T getSelectedItemTyped() {
    return (T) getSelectedItem();
  }

}
