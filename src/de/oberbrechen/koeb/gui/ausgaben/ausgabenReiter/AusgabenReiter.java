package de.oberbrechen.koeb.gui.ausgaben.ausgabenReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.dateien.ausgabenKonfiguration.AusgabenTreeModel;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.MainReiter;
import de.oberbrechen.koeb.gui.ausgaben.Main;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse ist die Hauptklasse für die graphische Oberfläche, die für
 * Auswahl von Ausgaben gedacht ist.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AusgabenReiter extends JPanel implements MainReiter {
  private Main hauptFenster;
  private TreeModel model;
  private Ausgabe aktuelleAusgabe;

  private JTextArea beschreibung;
  private JButton ausfuehrenButton;
  private JButton speichernButton;
  private JTextField nameFeld;

  private JFileChooser fileChooser;
  
  public AusgabenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    fileChooser = new JFileChooser();

    //Panel bauen
    JPanel allgemeinPanel = new JPanel();
    allgemeinPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
    allgemeinPanel.setLayout(new GridBagLayout());
    beschreibung = new JTextArea();
    beschreibung.setEditable(false);
    beschreibung.setLineWrap(true);
    beschreibung.setWrapStyleWord(true);
    ausfuehrenButton = new JButton("ausführen");
    ausfuehrenButton.setEnabled(false);
    ausfuehrenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            starteAusgabe();
          }
        }.start();
      }
    });
    speichernButton = new JButton("speichern");
    speichernButton.setEnabled(false);
    speichernButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        speichereAusgabe();
      }
    });
    nameFeld = new JTextField();
    nameFeld.setEditable(false);

    JScrollPane jScrollPaneBeschreibung = new JScrollPane(beschreibung);
    JComponentFormatierer.setDimension(jScrollPaneBeschreibung, new Dimension(200, 10));

    this.setMinimumSize(new Dimension(174, 168));
    this.setLayout(new BorderLayout());
    allgemeinPanel.add(new JLabel("Name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
    allgemeinPanel.add(nameFeld, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(new JLabel("Beschreibung:"),  new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 0, 0, 0), 0, 0));
    allgemeinPanel.add(jScrollPaneBeschreibung,       new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    allgemeinPanel.add(speichernButton,         new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 5), 0, 0));
    allgemeinPanel.add(ausfuehrenButton,         new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

    //Baum bauen
    model = new AusgabenTreeModel();
    JTree ausgabenTree = new JTree(model);

    ausgabenTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent event) {
        final TreePath tp = event.getNewLeadSelectionPath();
        if (tp != null && ((TreeNode) tp.getLastPathComponent()).isLeaf()) {
          setNeueAusgabe(((DefaultMutableTreeNode) tp.getLastPathComponent()));
        } else {
          setNeueAusgabe(null);
        }
      }
    }
    );

    //alles zusammenbauen
    JScrollPane jScrollPane = new JScrollPane(ausgabenTree);
    JComponentFormatierer.setDimension(jScrollPane, new Dimension(200, 200));
    JPanel baumPanel = new JPanel();
    baumPanel.setLayout(new BorderLayout());
    baumPanel.add(jScrollPane, BorderLayout.CENTER);
    baumPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

    JSplitPane jSplitPane = new JSplitPane();
    jSplitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    jSplitPane.add(baumPanel, JSplitPane.LEFT);
    jSplitPane.add(allgemeinPanel, JSplitPane.RIGHT);
    add(jSplitPane, BorderLayout.CENTER);
  }

 
/**
   * Setzt eine neue Ausgabe zur Ausführung. Übergeben wird der Knoten,
   * der diese Ausgabe repräsentiert
   */
  void setNeueAusgabe(DefaultMutableTreeNode node) {
    if (node == null) {
      nameFeld.setText("");
      aktuelleAusgabe = null;
      beschreibung.setText("");
      ausfuehrenButton.setEnabled(false);
      speichernButton.setEnabled(false);
    } else {
      aktuelleAusgabe = (Ausgabe) node.getUserObject();
      nameFeld.setText(aktuelleAusgabe.getName());
      beschreibung.setText(aktuelleAusgabe.getBeschreibung());
      ausfuehrenButton.setEnabled(true);
      speichernButton.setEnabled(aktuelleAusgabe.istSpeicherbar());
    }
  }

  protected void starteAusgabe() {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    try {
      aktuelleAusgabe.run(hauptFenster, true);
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Beim Ausführen der "+
        "Ausgabe trat ein Fehler auf:", false);
    }
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  protected void speichereAusgabe() {
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    try {            
      fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);     
      if (aktuelleAusgabe.getStandardErweiterung() != null) {
        fileChooser.setFileFilter(new FileFilter() {
          public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(aktuelleAusgabe.getStandardErweiterung());
          }
    
          public String getDescription() {
            return "*."+aktuelleAusgabe.getStandardErweiterung();
          }
        });
      }
      fileChooser.setSelectedFile(new File(aktuelleAusgabe.getName()+"."+aktuelleAusgabe.getStandardErweiterung()));
      if (fileChooser.showSaveDialog(hauptFenster) == JFileChooser.APPROVE_OPTION) {;
        File file = fileChooser.getSelectedFile();
        aktuelleAusgabe.schreibeInDatei(hauptFenster, true, file);
      }      
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Beim Ausführen der "+
        "Ausgabe trat ein Fehler auf:", false);
    }
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));  
  }
  
  public void aktualisiere() {
  }

  public void refresh() {
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}