package de.oberbrechen.koeb.gui.admin.systematikReiter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;

/**
* Diese Klasse ist ein Tabellenmodell für einen Baum von Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class SystematikTreeNode extends DefaultMutableTreeNode {
    
  public SystematikTreeNode(Systematik systematik, DefaultTreeModel model) {
    this.setUserObject(systematik);
    setAllowsChildren(true);
    berechneChildren(model);
  }
  
  private void berechneChildren(DefaultTreeModel model) {
    Systematik systematik = getSystematik();
    SystematikListe liste;
    
    if (systematik == null) {
      liste = Datenbank.getInstance().getSystematikFactory().
        getAlleHauptSystematiken();      
    } else {
      liste = systematik.getDirekteUntersystematiken();
    }
    liste.setSortierung(SystematikListe.alphabetischeSortierung);
    
    for (int i=0; i < liste.size(); i++) {
      SystematikTreeNode newNode = new SystematikTreeNode((Systematik) liste.get(i), model); 
      model.insertNodeInto(newNode, this, getChildCount());
    }
    
  }
  
  public Systematik getSystematik() {
    return (Systematik) getUserObject();
  }
  
  public int getIndex(Systematik aktuelleSystematik) {
    for (int i = 0; i < getChildCount(); i++) {
      if (((SystematikTreeNode) getChildAt(0)).getSystematik().
          equals(aktuelleSystematik)) return i;
    }
    return -1;
  }
  
  public String toString() {
    Systematik systematik = getSystematik();
    if (systematik == null) return "Systematiken";
    return systematik.getName()+((systematik.getBeschreibung()==null)?"":" - "+systematik.getBeschreibung());
  }
}