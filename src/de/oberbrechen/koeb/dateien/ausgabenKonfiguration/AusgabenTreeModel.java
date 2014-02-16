package de.oberbrechen.koeb.dateien.ausgabenKonfiguration;

import java.text.Collator;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.AusgabenTreeKnoten;
import de.oberbrechen.koeb.dateien.ausgabenKonfiguration.AusgabenFactoryBeschreibung;
import de.oberbrechen.koeb.dateien.ausgabenKonfiguration.AusgabenKonfigurationXMLDatei;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse ist das TreeModel für die Ausgaben-GUI.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AusgabenTreeModel extends DefaultTreeModel {

  class DefaultAusgabenTreeKnoten extends DefaultMutableTreeNode 
    implements AusgabenTreeKnoten {

    private String name;
    private boolean istSortiert;
  
    public void addAusgabe(Ausgabe ausgabe) {
      addAusgabe(ausgabe.getName(), ausgabe);
    }

    public void addAusgabe(String name, Ausgabe ausgabe) {
      DefaultMutableTreeNode neuerKnoten =
          new DefaultAusgabenTreeKnoten(name, false, false);
      neuerKnoten.setUserObject(ausgabe);

      
      int i = this.getChildCount();
      if (istSortiert) {
        //Suche richtige Einfügeposition
        while(i > 0 && !this.getChildAt(i-1).getAllowsChildren() 
          && Collator.getInstance().compare(this.getChildAt(i-1).toString(), name) > 0) {
          i--;
        }
      }
      this.insert(neuerKnoten, i);
    }

    public AusgabenTreeKnoten addKnoten(String name, boolean sortiert) {
      DefaultAusgabenTreeKnoten neuerKnoten =
          new DefaultAusgabenTreeKnoten(name, true, sortiert);

      int i = this.getChildCount();
      //Ende der Ordnereinträge suchen
      while(i > 0 && !this.getChildAt(i-1).getAllowsChildren()) {
        i--;
      }
      
      if (istSortiert) {
        //Suche richtige Einfügeposition
        while(i > 0 && Collator.getInstance().compare(this.getChildAt(i-1).toString(), name) > 0) {
          i--;
        }
      }
      
      this.insert(neuerKnoten, i);
      return neuerKnoten;
    }

    public AusgabenTreeKnoten getKnoten(String name) {
      int i = 0;
      while(i < this.getChildCount()) {
        DefaultAusgabenTreeKnoten node = 
          (DefaultAusgabenTreeKnoten) this.getChildAt(i);
        if (!node.getAllowsChildren()) return null;
        if (node.name.equals(name)) return node;
        i++;
      }
      return null;  
    }

    public DefaultAusgabenTreeKnoten(String name, boolean istOrdner, boolean istSortiert) {
      this.name = name;
      if (!istOrdner) 
        this.setAllowsChildren(false);
      this.istSortiert = istSortiert;
    }
    
    public String toString() {
      return name;
    }
  }



  private DefaultAusgabenTreeKnoten root;

  public AusgabenTreeModel() {
    super(new DefaultMutableTreeNode());
    root = new DefaultAusgabenTreeKnoten("Ausgaben", true, true);
    this.setRoot(root);

    try {
      AusgabenFactoryBeschreibung[] beschreibung = AusgabenKonfigurationXMLDatei.getInstance().getDaten();    
      AusgabenTreeKnoten[] knoten = new DefaultAusgabenTreeKnoten[beschreibung.length];
      
      //Knoten aufbauen
      for (int i=0; i < beschreibung.length; i++) {
        knoten[i] = null;
        try {
          StringTokenizer tokenizer = new StringTokenizer(beschreibung[i].getOrdner(), "/");
          
          AusgabenTreeKnoten aktuellerKnoten = root;
          while (tokenizer.hasMoreTokens()) {
            String neuerKnotenName = tokenizer.nextToken();
            if (neuerKnotenName != null && !neuerKnotenName.equals("")) {
              AusgabenTreeKnoten neuerKnoten = aktuellerKnoten.getKnoten(neuerKnotenName);
              if (neuerKnoten == null) neuerKnoten = aktuellerKnoten.addKnoten(neuerKnotenName, true);
              aktuellerKnoten = neuerKnoten;
            }
          }
          knoten[i] = aktuellerKnoten;
        } catch (Exception e) {
          ErrorHandler.getInstance().handleException(e, "Fehler beim Erstellen des Knotens '"+
            beschreibung[i].getOrdner(), false);
        }
      }
                  
      //Eigentliche Ausgabe
      Thread[] threads = new Thread[beschreibung.length];
      for (int i=0; i < beschreibung.length; i++) {
        final AusgabenFactoryBeschreibung aktuelleBeschreibung = beschreibung[i];
        final AusgabenTreeKnoten aktuellerKnoten = knoten[i];        
        threads[i] = new Thread(new Runnable() {
          public void run() {
            try {
              if (aktuellerKnoten != null)
                aktuelleBeschreibung.createAusgabenFactory().addToKnoten(aktuellerKnoten);
            } catch (Exception e) {
              ErrorHandler.getInstance().handleException(e, "Fehler beim Auswerten der AusgabenFactoryBeschreibung\n\n"+aktuelleBeschreibung.toString(), false);
            }
          }          
        });
        threads[i].run();
      }
      
      for (int i=0; i < beschreibung.length; i++) {
        threads[i].join();
      }
      
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Parsen der Ausgaben-Konfigurationsdatei!", false);
    }
    
    //Unnötige Knoten entfernen
    entferneUeberfluessigeKnoten(root);
  }
  
  /**
   * Entfernt Knoten, die keine Ausgaben enthalten, aber auch keine
   * Verzeichnisse repräsentieren.
   */
  private void entferneUeberfluessigeKnoten(DefaultMutableTreeNode knoten) {
    if (knoten.getChildCount() == 0 && knoten.getUserObject() == null) {
      knoten.removeFromParent();
    }
    
    for (int i=0; i < knoten.getChildCount(); i++)
      entferneUeberfluessigeKnoten(
          (DefaultMutableTreeNode) knoten.getChildAt(i));
  }
}