package de.oberbrechen.koeb.gui.components.einstellungBindung;

/**
 * Diese Klasse dient dazu eine Einstellung an eine 
 * JComponent zu binden. Initial wird der JComponent der Wert der 
 * Einstellung zugewiesen. Dies geschieht mit Hilfe der Methode refresh().
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public interface JComponentEinstellungBindung {

 /**
  * Weist der JComponent den Wert der Einstellung zu.
  */
 public void refresh();
}