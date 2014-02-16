package de.oberbrechen.koeb.datenstrukturen;

import java.util.*;
import java.text.Collator;

import de.oberbrechen.koeb.datenbankzugriff.DatenmanipulationsFunktionen;
import de.oberbrechen.koeb.framework.ErrorHandler;

class ListeIterator<T> implements Iterator<T> {
  int pos = -1;
  Liste<T> liste;

  public ListeIterator(Liste<T> liste) {
    this.liste = liste;
  }

  public synchronized boolean hasNext() {
    return pos+1 < liste.size();
  }

  public synchronized T next() {
    pos++;
    return liste.get(pos);
  }

  public synchronized void remove() {   
    liste.remove(pos);
    pos--;
  }
}

/**
* Diese Klasse repräsentiert eine Liste, die sortiert oder unsortiert sein kann.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class Liste<T> extends Observable implements SortedSet<T>, Observer {

  private static final int initArraySize = 20;
  protected static final Collator collator = Collator.getInstance();
  
  protected static int nullCompare(String a, String b) {
    return collator.compare(DatenmanipulationsFunktionen.entferneNull(a),
        DatenmanipulationsFunktionen.entferneNull(b));    
  }
  
  //Sortiert die Elemente alphabetisch nach ihrer String-Repräsentation.
  public static final int StringSortierung = 1;

  private Comparator<T> comparator;

  private T[] daten;
  private int size;
  private boolean doppelt = true;
  private boolean erhalteSortierung;

  @SuppressWarnings("unchecked")
  public Liste() {
    size = 0;
    daten= (T[]) new Object[initArraySize];
    comparator = null;
    erhalteSortierung = true;
  }
  
  /**
   * Bestimmt, ob bei Änderung der Elemente der Liste diese Automatisch
   * neu sortiert werden soll. Standard ist true.
   * @param erhalteSortierung
   */
  public void setErhalteSortierung(boolean erhalteSortierung) {
    if (!this.erhalteSortierung && erhalteSortierung)
      this.resort();
    this.erhalteSortierung = erhalteSortierung;
  }

  /**
   * Setzt die Sortierung für die Liste.
   * @param sortierung der Comparator, mit dem die Elemente verglichen werden
   *   sollen, <code>null</code> für unsortiert
   */
  public synchronized void setSortierung(Comparator<T> sortierung) {
    this.comparator = sortierung;
    resort(); //inklusive notifyObservers
  }

  /**
   * Sortiert die Liste neu nach der gewählten Sortierung. Dies kann nötig sein,
   * wenn die Elemente der Liste zwischenzeitlich geändert wurden.
   */
  public synchronized void resort() {
    if (!istSortiert()) return;
    
    try {
      if (size() > 1) Arrays.sort(daten, 0, size(), comparator);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim "+
          "Sortieren der Liste!", false);      
    }
    
    setChanged();
    notifyObservers();    
  }

  /**
   * Wechselt die Sortierung der Liste.
   * Die verfügbaren Sortierungen sind als öffentliche Konstanten dieser Klasse
   * ansprechbar. 0 steht dafür, dass die Liste unsortiert sein soll.
   * Diese Methode entspricht einem Aufruf mit dem Parameter umgekehrte Sortierung false.
   */
  public synchronized void setSortierung(int sortierung){
    this.setSortierung(sortierung, false);
  }

  /**
   * Wechselt die Sortierung der Liste.
   * Die verfügbaren Sortierungen sind als öffentliche Konstanten dieser Klasse
   * ansprechbar. 0 steht dafür, dass die Liste unsortiert sein soll.
   *
   * @param sortierung die anzuwendende Sortierung
   * @param umgekehrteSortierung soll die Sortierreihenfolge umgekehrt werden
   */
  public synchronized void setSortierung(int sortierung, final boolean umgekehrteSortierung){
    if (sortierung == 0) {
      comparator = null;
      return;
    }

    final Comparator<T> sort;
    if (sortierung == StringSortierung) {
      final Collator col = Collator.getInstance();
      sort = new Comparator<T>() {
        public int compare(T a, T b) {
          return col.compare(a.toString(), b.toString());
      }};
    } else {
      sort = getComparatorFuerSortierung(sortierung);
    }


    comparator = new Comparator<T>() {
      public int compare(T a, T b) {
        if (a.equals(b)) return 0;
        int erg = sort.compare(a, b);
        if (umgekehrteSortierung) erg = -1*erg;
        if (erg == 0 && doppelt) erg = b.hashCode() - a.hashCode();
        return erg;
      }};

    setSortierung(comparator); //inklusive resort und notifyObservers
  }

  @SuppressWarnings("unchecked")
  public synchronized void update(Observable o, Object arg) {
    if (!istSortiert()) return;
    if (!erhalteSortierung) return;
    
    int index = indexOf(o);
    if (index == -1) return;

    //sicher, da sonst nicht von indexOf gefunden
    T ot = (T) o;
    
    //lokales Resort
    while (index > 0 && comparator.compare(daten[index-1], ot) > 0) {
      daten[index] = daten[index-1];
      index--;
    }

    while (index < this.size()-1 &&
           comparator.compare(daten[index+1], ot) < 0) {
      daten[index] = daten[index+1];
      index++;
    }
    daten[index] = ot;
    
    setChanged();
    notifyObservers();    
  }

  /**
   * Liefert einen Comparator für die übergebene Sortierung.
   * Die verfügbaren Sortierungen sind als öffentliche Konstanten dieser Klasse
   * ansprechbar. Die StringSortierung und 0 dürfen nicht verwendet werden.
   *
   * @param sortierung die anzuwendende Sortierung
   * @throws IllegalArgumentExeception falls die übergebene Sortierung unbekannt
   *  ist
   */
  protected Comparator<T> getComparatorFuerSortierung(int sortierung) {
    throw new IllegalArgumentException("Eine Sortierung mit der Nummer "+
      sortierung + " ist unbekannt!");
  }

  /**
   * Bestimmt, ob die Liste sortiert ist.
   * @return <code>true</code> gdw. die Liste sortiert ist
   */
  public synchronized boolean istSortiert() {
    return (comparator != null);
  }

  public synchronized Comparator<T> comparator() {
    return comparator;
  }

  /**
   * Bestimmt, ob einträge, die nach der Sortierung den gleichen Wert besitzen,
   * doppelt aufgenommen werden sollen. Ist doppelt auf false gesetzt, so werden
   * sie nur Objecte, die wirklich gleich sind, nicht doppelt aufgenommen. 
   * Standardeinstellung ist, dass Werte doppelt aufgenommen werden.
   * @param doppelt
   */
  public synchronized void setZeigeDoppelteEintraege(boolean doppelt) {
    this.doppelt = doppelt;    
  }
  
  @SuppressWarnings("unchecked")
  public synchronized int indexOf(Object o) {
    if (o == null) throw new NullPointerException();
    if (this.isEmpty()) return -1;
    if (!(this.first().getClass()).isInstance(o)) return -1;
    T ot = (T) o;
    
    
    if (comparator == null) {
      for (int i = 0; i < size(); i++) {
        if (daten[i] != null && daten[i].equals(ot)) return i;
      }
    } else {
      int untereSchranke = 0;
      int obereSchranke = size()-1;
      
      while (untereSchranke <= obereSchranke) {
        int testPos = (untereSchranke+obereSchranke) / 2;
        int result = comparator.compare(daten[testPos], ot);
        if (result > 0) {
          obereSchranke = testPos - 1;
        } else if (result < 0) {
          untereSchranke = testPos + 1;          
        } else {
          return testPos;
        }
      }
    }
        
    return -1;
  }

  public SortedSet<T> subSet(Object fromElement, Object toElement) {
    throw new java.lang.UnsupportedOperationException("Method subSet() not supported.");
  }

  public SortedSet<T> headSet(Object toElement) {
    throw new java.lang.UnsupportedOperationException("Method headSet() not supported.");
  }

  public SortedSet<T> tailSet(Object fromElement) {
    throw new java.lang.UnsupportedOperationException("Method tailSet() not supported.");
  }

  public synchronized T first() {
    if (isEmpty()) throw new NoSuchElementException("Set is empty!");
    return get(0);
  }

  public synchronized T last() {
    if (isEmpty()) throw new NoSuchElementException("Set is empty!");
    return get(size()-1);
  }

  public synchronized int size() {
    return size;
  }

  public synchronized boolean contains(Object o) {
    return (indexOf(o) != -1);
  }

  public synchronized Iterator<T> iterator() {
    return new ListeIterator<T>(this);
  }

  /**
   * Fügt ein Element zur Liste hinzu, wobei vom Aufrufer die Garantie
   * übernommen wird, dass das Element noch nicht in der Liste vorkommt.
   * Dies wird von der Methode add zusätzlich geprüft. Daher ist diese Methode
   * performanter.
   * @param o das neue Object
   */
  public synchronized void addNoDuplicate(T o) {
    if (daten.length == size()) vergroessereArray();

    if (o instanceof Observable) ((Observable) o).addObserver(this);

    if (istSortiert())
      addSortiert(o);
    else
      addUnsortiert(o);
      
    setChanged();
    notifyObservers();      
  }

  public synchronized boolean add(T o) {
    if (o == null) throw new NullPointerException("Zu einer Liste darf nicht NULL hinzugefuegt werden!");
    if (this.contains(o)) return false;
    addNoDuplicate(o);
    setChanged();
    notifyObservers();
    return true;
  }

  /**
   * Fügt die Elemenet der
   * uebergebene Collection zur Liste hinzu, wobei vom Aufrufer die Garantie
   * übernommen wird, dass diese Elemente noch nicht in der Liste vorkommen.
   * Dies wird von der Methode addAll zusätzlich geprüft. Daher ist die Methode
   * addAllNoDuplicate performanter.
   * @param o das neue Object
   */
  @SuppressWarnings("unchecked")
  public synchronized boolean addAllNoDuplicate(Collection<? extends T> c) {
    if (c == null || c.size() == 0) return false;
    
    garantiereGroesse(this.size()+c.size());

    boolean elementeEingefuegt = (c.size() > 0);
    Iterator i = c.iterator();
    int pos = size;
    while (i.hasNext()) {
      T currentObject = (T) i.next();
      if (currentObject instanceof Observable)
        ((Observable) currentObject).addObserver(this);
      daten[pos] = currentObject;
      pos++;
    }
    size = pos;
    resort();
    if (elementeEingefuegt) {
      setChanged();
      notifyObservers();          
    }
    return elementeEingefuegt;
  }

  @SuppressWarnings("unchecked")
  public synchronized boolean addAll(Collection<? extends T> c) {
    if (c == null || c.size() == 0) return false;

    garantiereGroesse(this.size()+c.size());

    boolean elementeEingefuegt = false;
    Iterator i = c.iterator();
    int pos = size;
    while (i.hasNext()) {
      T currentObject = (T) i.next();
      if (!contains(currentObject)) {
        elementeEingefuegt = true;
        if (currentObject instanceof Observable)
          ((Observable) currentObject).addObserver(this);
        daten[pos] = currentObject;
        pos++;
      }
    }
    size = pos;
    resort();
    
    if (elementeEingefuegt) {
      setChanged();
      notifyObservers();
    }        
    return elementeEingefuegt;
  }

  public synchronized boolean removeAll(Collection<?> c) {
    boolean geloescht = false;
    for (Object o : c) {
      geloescht |= remove(o);
    }
    
    if (geloescht) {
      setChanged();
      notifyObservers();
    }
      
   return geloescht;
  }

  /**
   * Fügt das übergebene Object zu einer sortierten Liste hinzu.
   * Es wird erwartet, dass das Object noch nicht in der Menge vorhanden ist
   * und außerdem genug Platz zur Verfügung steht.
   * @param o das hinzuzufügende Object
   */
  private synchronized void addSortiert(T o) {
    //Insertionsort
    int currentPos = size();
    while (currentPos > 0 && comparator().compare(daten[currentPos-1], o) > 0) {
      daten[currentPos] = daten[currentPos-1];
      currentPos--;
    }
    daten[currentPos] = o;
    size++;
  }

  /**
   * Fügt das übergebene Object zu einer sortierten Liste hinzu.
   * Es wird erwartet, dass das Object noch nicht in der Menge vorhanden ist
   * und außerdem genug Platz zur Verfügung steht.
   * @param o das hinzuzufügende Object
   */
  private synchronized void addUnsortiert(T o) {
    daten[size]=o;
    size++;
  }

  /**
   * Verdoppelt die zur Verfügung stehende Arraygröße.
   */
  @SuppressWarnings("unchecked")
  private synchronized int vergroessereArray() {
    T[] neueDaten = (T[]) new Object[daten.length*2];
    for (int i=0; i < size(); i++) {
      neueDaten[i] = daten[i];
    }
    daten = neueDaten;
    return daten.length;
  }

  /**
   * Erweitert den internen Datentyp, falls nötig, so dass er mindestens
   * die übergebene Datenmenge aufnehmen kann.
   *
   * @param size die benötigte Größe
   */
  @SuppressWarnings("unchecked")
  public synchronized void garantiereGroesse(int size) {
    if (daten.length > size) return;
    if (daten.length*2 > size) size = 2*daten.length;

    T[] neueDaten = (T[]) new Object[size];
    for (int i=0; i < size(); i++) {
      neueDaten[i] = daten[i];
    }
    daten = neueDaten;
  }

  public synchronized T get(int index) {
    if (index < 0 || index >= size()) { 
      ErrorHandler.getInstance().handleException(
        new IndexOutOfBoundsException("Index: "+index+", Size: "+size()), false);
      return null;
    }
    return daten[index];
  }

  /**
   * Entfernt das letzte Element aus der Liste
   * @return das entfernte Element
   */
  public synchronized T removeLast() {
    if (isEmpty()) throw new NoSuchElementException("Set is empty!");
    size--;
    return daten[size];
  }
  
  public synchronized T remove(int index) {
    if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

    if (daten[index] instanceof Observable)
      ((Observable) daten[index]).deleteObserver(this);

    size--;
    T returnValue = daten[index];
    for (int i=index; i < size(); i++) {
      daten[i] = daten[i+1];
    }
    
    setChanged();
    notifyObservers();

    return returnValue;
  }

  public synchronized boolean remove(Object o) {
    int pos = indexOf(o);
    if (pos == -1) return false;

    remove(pos);
    setChanged();
    notifyObservers();
    return true;
  }

  public synchronized void clear() {
    for (int i = 0; i < size(); i++) {
      if (daten[i] instanceof Observable)
        ((Observable) daten[i]).deleteObserver(this);
      daten[i] = null;
    }
    size = 0;
    
    setChanged();
    notifyObservers();    
  }

  public synchronized boolean equals(Object o) {
    if (!(o instanceof Liste)) return false;
    if (((Liste) o).comparator() != this.comparator()) return false;
    return super.equals(o);
  }

  public synchronized boolean isEmpty() {
    return (size == 0);
  }

  @SuppressWarnings("unchecked")
  public synchronized T[] toArray() {
    T[] erg = (T[]) new Object[this.size()];
    for (int i=0; i < this.size(); i++) {
      erg[i] = daten[i];
    }
    return erg;
  }

  @SuppressWarnings("unchecked")
  public synchronized T[] toArray(Object[] a) {
    if (a.length < size)
        a = (Object[]) java.lang.reflect.Array.newInstance(
                              a.getClass().getComponentType(), size);

    for (int i=0; i < this.size(); i++) {
      a[i] = daten[i];
    }

    if (a.length > size) a[size] = null;

    return (T[]) a;
  }

  public synchronized boolean containsAll(Collection<?> c) {
    for (Object o : c)
      if (!contains(o)) return false;

    return true;
  }

  public synchronized boolean retainAll(Collection<?> c) {
    boolean modified = false;
    Iterator<T> e = iterator();
    while (e.hasNext()) {
      if(!c.contains(e.next())) {
        e.remove();
        modified = true;
      }
    }
    
    if (modified) {
      setChanged();
      notifyObservers();
    }      
    return modified;
  }
  
  public synchronized String toString() {
    StringBuffer erg = new StringBuffer();
    for (int i=0; i < size; i++) {
      erg.append(daten[i]);
      erg.append("\n");
    }
    return erg.toString();
  }

  /**
   * Liefert die die durch Kommata getrennten, von format bestimmten
   * Repräsentationen der Elemente der Liste
   * @param format
   * @return die durch Kommata getrennte Liste
   */
  public synchronized String toKommaGetrenntenString(Format<T> format) {
    if (isEmpty()) return "";
    
    StringBuffer erg = new StringBuffer();
    for (int i=0; i < size()-1; i++) {
      erg.append(format.format(daten[i]));
      erg.append(", ");
    }
    erg.append(format.format(daten[size()-1]));
    return erg.toString();
  }

  /**
   * Liefert die die durch Kommata getrennten
   * Repräsentationen der Elemente der Liste.
   * @return die durch Kommata getrennte Liste
   */
  public synchronized String toKommaGetrenntenString() {
    return toKommaGetrenntenString(new Format<T>());
  }
}