package de.oberbrechen.koeb.gui.admin.dokumentierteEinstellungenReiter;

import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungBeschreibung;
import de.oberbrechen.koeb.dateien.einstellungenDoku.EinstellungenDokuXMLDatei;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse ist das TreeModel für die Einstellungen-GUI.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class EinstellungenTreeModel extends DefaultTreeModel {

  private static final long serialVersionUID = 1L;
  
  public EinstellungenTreeModel() {
    super(new DefaultMutableTreeNode("Einstellungen"));
    
    try {      
      Hashtable<String, DefaultMutableTreeNode> gruppenHash = new Hashtable<String, DefaultMutableTreeNode>();
      EinstellungenDokuXMLDatei einstellungenDoku = 
        EinstellungenDokuXMLDatei.getInstance();
      EinstellungBeschreibung[] beschreibungen = einstellungenDoku.getDaten();    
      
      //Dokumentierte Einstellungen
      for (EinstellungBeschreibung b : beschreibungen) {
        DefaultMutableTreeNode gruppenNode = getGruppenKnoten(b.getGruppe(), gruppenHash);
        gruppenNode.add(new DefaultMutableTreeNode(b));
      }
                  
      //Undokumentierte Einstellungen
      Liste<String> dokumentiert = new Liste<String>();
      for (EinstellungBeschreibung b : beschreibungen)        
        dokumentiert.add(b.getName());
      
      String[] versteckt = einstellungenDoku.getVersteckteEinstellungen();
      for (String v : versteckt) {        
        dokumentiert.add(v);
      }
      
      Liste<String> undokumentiert = Datenbank.getInstance().
        getEinstellungFactory().getAlleEinstellungenNamen();
      for (String currentDokumentiert : dokumentiert) {
        if (currentDokumentiert.endsWith("*")) {
          String currentDokumentiertPraefix = 
            currentDokumentiert.substring(0, currentDokumentiert.length()-1);
          Iterator<String> it = undokumentiert.iterator();
          while (it.hasNext()) {
            String currentUndokumentiert = it.next(); 
            if (currentUndokumentiert.startsWith(currentDokumentiertPraefix)) 
              it.remove();
          }
        } else {
          undokumentiert.remove(currentDokumentiert);
        }
      }
      
      if (undokumentiert.size() > 0) {
        DefaultMutableTreeNode gruppenNode = getGruppenKnoten("undokumentierte Einstellungen", gruppenHash);
        for (String undok : undokumentiert) {        
          EinstellungBeschreibung beschreibung = 
            EinstellungBeschreibung.createUndokumentierteEinstellung(undok);
          
          gruppenNode.add(new DefaultMutableTreeNode(beschreibung));
        }
      }                
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Parsen der Ausgaben-Konfigurationsdatei!", false);
    }    
  }
  
  private DefaultMutableTreeNode getGruppenKnoten(String gruppe, Hashtable<String, DefaultMutableTreeNode> gruppenHash) {
    DefaultMutableTreeNode result = gruppenHash.get(gruppe);
    
    if (result == null) {
      result = new DefaultMutableTreeNode(gruppe);
      ((DefaultMutableTreeNode) getRoot()).add(result);
      gruppenHash.put(gruppe, result);
    }
    
    return result;
  }
}