package de.oberbrechen.koeb.gui.components.listenKeySelectionManager;

/**
 * Diese Klasse ist ein KeySelectionManager für beliebige Listen.
 * Die Methode getSelectedRow bekommt einen Tastendruck übergeben
 * und liefert darauf die zu wählende Zeile. Die zur Verfügung
 * stehenden Zeilen und die darin enthaltenden Daten müssen das
 * Interface ListenKeySelectionManagerListenDaten implementieren
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ListenKeySelectionManager {

  private StringBuffer pattern;
  private long lastKeyPress;  
  private ListenKeySelectionManagerListenDaten daten;

  public ListenKeySelectionManager(ListenKeySelectionManagerListenDaten daten) {
    this.daten = daten;
    pattern = new StringBuffer();
    lastKeyPress = 0;   
  }
  
  public int selectionForKey(char aKey) {
    if (aKey == '\u001b') {
      pattern.delete(0, pattern.length());
      return 0;
    }

    if (System.currentTimeMillis() - lastKeyPress > 10000) {
      pattern.delete(0, pattern.length());
    }
    
    lastKeyPress = System.currentTimeMillis();
  
    if (!(Character.isLetterOrDigit(aKey) ||
          aKey == ',' || aKey == '-' || aKey == '\b' ||
          aKey == 'ä' || aKey == 'ö' || aKey == 'ü' || aKey == 'ß' ||
          aKey == '.' || aKey == '!' || aKey == ':' || aKey == '?' ||
          aKey == 'Ä' || aKey == 'Ö' || aKey == 'Ü' )) return -1;
    
    if (aKey == '\b') {
      if (pattern.length() > 0)
        pattern.delete(pattern.length()-1, pattern.length());
    } else {
      pattern.append(Character.toLowerCase(aKey));
    }
    
    int currentPos = -1;
    do {
      currentPos += 1;
      String eintrag = daten.getKeySelectionValue(currentPos).toLowerCase(); 
      eintrag = eintrag.replaceAll(" ", "");
      if (eintrag.startsWith(pattern.toString())) return currentPos;
    } while (currentPos < daten.size()-1);
    
    return 0;
  }
}
