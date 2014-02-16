package de.oberbrechen.koeb.gui.admin.systematikReiter;

import java.text.Collator;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
* Diese Klasse ist ein Tabellenmodell für einen Baum von Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class SystematikTreeModel extends DefaultTreeModel {
  
  protected Hashtable<Systematik, SystematikTreeNode> cache;
  
  public SystematikTreeModel() {
    super(new DefaultMutableTreeNode());
  }
  
  public void initCache(SystematikTreeNode node) {
    Enumeration<?> children = node.children();
    while (children.hasMoreElements()) {
      SystematikTreeNode nextNode = (SystematikTreeNode) children.nextElement();
      
      cache.put(nextNode.getSystematik(), nextNode);
      initCache(nextNode);
    }
  }
  
  public SystematikTreeNode getTreeNode(Systematik systematik) {
    if (systematik == null) return (SystematikTreeNode) this.getRoot();
    
    return cache.get(systematik);
  }

  public void addSystematik(Systematik systematik) {
    SystematikTreeNode parent;
    try {
      parent = getTreeNode(systematik.getDirekteObersystematik());
      SystematikTreeNode newNode = new SystematikTreeNode(systematik, this);
      addOrdered(parent, newNode);
      cache.put(systematik, newNode);
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);      
    }
  }

  public void removeSystematik(Systematik systematik) {
    SystematikTreeNode node = getTreeNode(systematik);
    removeNodeFromParent(node);
    cache.remove(systematik);
  }  

  public void updateSystematik(Systematik systematik) {
    SystematikTreeNode node = getTreeNode(systematik);
    removeNodeFromParent(node);
    
    SystematikTreeNode parent;
    try {
      parent = getTreeNode(systematik.getDirekteObersystematik());
      addOrdered(parent,node);
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }
  
  private void addOrdered(SystematikTreeNode parent, SystematikTreeNode newNode) {
    Collator collator = Collator.getInstance();
    int i = parent.getChildCount();
    
    
    while(i > 0 && collator.compare(parent.getChildAt(i-1).toString(), newNode.toString()) > 0) {
      i--;
    }
    
    insertNodeInto(newNode, parent, i);
  }

  /**
   * Initialisiert die Daten des Modells
   */
  public void init() {
    SystematikTreeNode root = new SystematikTreeNode(null, this);
    this.setRoot(root);      

    cache = new Hashtable<Systematik, SystematikTreeNode>();    
    initCache((SystematikTreeNode) getRoot());
  }  
}
