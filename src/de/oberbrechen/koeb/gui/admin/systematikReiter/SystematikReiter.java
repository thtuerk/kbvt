package de.oberbrechen.koeb.gui.admin.systematikReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.oberbrechen.koeb.dateien.SystematikXMLDatei;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.SystematikFactory;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.admin.AdminMainReiter;
import de.oberbrechen.koeb.gui.admin.Main;
import de.oberbrechen.koeb.gui.components.SystematikPanel;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern und Erstellen
 * von Benutzer in der GUI ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class SystematikReiter extends JPanel implements AdminMainReiter {

  private Main hauptFenster;
  private SystematikPanel systematikPanel;
  boolean istVeraenderbar;

  // die verwendeten Buttons;
  private JButton neuButton = new JButton();
  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  boolean systematikWirdGeradeGesetzt = false;
  SystematikFactory systematikFactory;
  SystematikTableModel systematikenTableModel;
  SystematikTreeModel systematikenTreeModel;
  JTree systematikBaum;
  JTable systematikTabelle;  
  
  //Doku siehe bitte Interface
  public void refresh() {
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;

    //Buttons anpassen
    neuButton.setEnabled(!veraenderbar);
    hauptFenster.erlaubeAenderungen(!veraenderbar);

    if (!veraenderbar) {
      ladenButton.setText("Löschen");
      saveButton.setText("Bearbeiten");
    } else {
      ladenButton.setText("Änderungen verwerfen");
      saveButton.setText("Speichern");
    }

    systematikPanel.setVeraenderbar(veraenderbar);
    systematikBaum.setEnabled(!veraenderbar);
    systematikTabelle.setEnabled(!veraenderbar);
    
    boolean systematikAngezeigt = (systematikPanel.getSystematik() != null);
    ladenButton.setEnabled(systematikAngezeigt); 
    saveButton.setEnabled(systematikAngezeigt);     
  }

  public SystematikReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    systematikFactory = Datenbank.getInstance().getSystematikFactory();
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setSystematik(Systematik systematik) {    
    if (systematikWirdGeradeGesetzt) return;
    
    systematikWirdGeradeGesetzt = true;
    systematikPanel.setSystematik(systematik);
    showSystematik(systematik);
    setVeraenderbar(systematik != null && systematik.istNeu());    
    systematikWirdGeradeGesetzt = false;
  }
  
  protected void showSystematik(Systematik systematik) {
    if (systematik == null || systematik.istNeu()) {
      systematikBaum.clearSelection();
      systematikTabelle.clearSelection();
    } else {
      int tabellenZeile = systematikenTableModel.getDaten().indexOf(systematik);
      systematikTabelle.getSelectionModel().setSelectionInterval(
          tabellenZeile, tabellenZeile);
      systematikTabelle.scrollRectToVisible(systematikTabelle.getCellRect(
          tabellenZeile, tabellenZeile, true));
      
      TreePath path = new TreePath(systematikenTreeModel.getTreeNode(systematik).getPath());
      systematikBaum.getSelectionModel().setSelectionPath(path);
      systematikBaum.expandPath(path.getParentPath());
      systematikBaum.scrollPathToVisible(path);
    }    
  }
  
  
  // erzeugt die GUI
  void jbInit() throws Exception {
    JPanel ButtonPanel = new JPanel();
    systematikPanel = new SystematikPanel(hauptFenster);
    systematikPanel.setMinimumSize(new Dimension(200, 350));    
    
    this.setLayout(new BorderLayout());
    neuButton.setText("Neue Systematik anlegen");
    neuButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSystematik(systematikFactory.erstelleNeu());
      }
    });
    saveButton.setText("Speichern");
    saveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (istVeraenderbar) {
          saveChanges();
        } else {
          setVeraenderbar(true);
        }
      }
    });
    ladenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!istVeraenderbar) {
          loescheSystematik();
        } else {
          aenderungenVerwerfen();
        }
      }
    });
    ButtonPanel.setLayout(new GridLayout(1, 3, 15, 5));
    ButtonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    
    //SystematikBaum
    systematikenTreeModel = new SystematikTreeModel();
    systematikBaum = new JTree();
    systematikBaum.setModel(systematikenTreeModel);

    JScrollPane systematikTreeScrollPane = new JScrollPane(systematikBaum);
    JComponentFormatierer.setDimension(systematikTreeScrollPane, new Dimension(0, 200));
    systematikTreeScrollPane.setBorder(BorderFactory.createCompoundBorder(        
        BorderFactory.createEmptyBorder(5,5,5,5),
        BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
    systematikBaum.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    systematikBaum.getSelectionModel().addTreeSelectionListener(
        new TreeSelectionListener() {
          public void valueChanged(TreeSelectionEvent e) {
            TreePath gewaehlterPfad = systematikBaum.getSelectionPath();
            if (gewaehlterPfad != null) {
              Systematik gewaehlteSystematik = 
                ((SystematikTreeNode) gewaehlterPfad.getLastPathComponent()).getSystematik();
              setSystematik(gewaehlteSystematik);
            } else {
              setSystematik(null);
            }
          }
        });
    
    //SystematikTabelle
    systematikTabelle = new JTable();
    systematikenTableModel = new SystematikTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(systematikenTableModel, systematikTabelle);
    systematikTabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    systematikTabelle.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent e) {
            int gewaehlteReihe = systematikTabelle.getSelectedRow();
            if (gewaehlteReihe != -1) {
              Systematik gewaehlteSystematik = (Systematik) 
                systematikenTableModel.get(gewaehlteReihe);
              setSystematik(gewaehlteSystematik);
            } else {
              setSystematik(null);
            }
          }
        });
    
    JScrollPane systematikScrollPane = new JScrollPane(systematikTabelle);
    JComponentFormatierer.setDimension(systematikScrollPane, new Dimension(0, 200));
    systematikScrollPane.setBorder(BorderFactory.createCompoundBorder(        
        BorderFactory.createEmptyBorder(5,5,5,5),
        BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
        
    // Reiter bauen
    JTabbedPane reiter = new JTabbedPane();
    reiter.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    systematikPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    reiter.add(systematikScrollPane, "Tabelle");
    reiter.add(systematikTreeScrollPane, "Baum");
    
    //Splitpanel
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane1.add(reiter, JSplitPane.LEFT);
    jSplitPane1.add(systematikPanel, JSplitPane.RIGHT);
    jSplitPane1.setResizeWeight(0.8);
    
    
    this.add(ButtonPanel, BorderLayout.SOUTH);
    ButtonPanel.add(saveButton, null);
    ButtonPanel.add(ladenButton, null);
    ButtonPanel.add(neuButton, null);
    this.add(jSplitPane1, BorderLayout.CENTER);
  }

  public void aktualisiere() {
    setSystematik(null);
    
    systematikPanel.aktualisiere();
    
    SystematikListe systematiken =
      systematikFactory.getAlleSystematiken();
    systematiken.setSortierung(SystematikListe.alphabetischeSortierung);
    systematikenTableModel.setDaten(systematiken);

    systematikenTreeModel.init();
      
    setVeraenderbar(false);
  }

  /**
   * Löscht die aktuelle Systematik
   */
  void loescheSystematik() {
    Systematik currentSystematik = systematikPanel.getSystematik();
    boolean loeschenOK = systematikPanel.loescheSystematik();
    if (loeschenOK) {
      systematikenTableModel.getDaten().remove(currentSystematik);
      systematikenTableModel.fireTableDataChanged();
      systematikPanel.removeSpezialisiertListe(currentSystematik);
      systematikenTreeModel.removeSystematik(currentSystematik);
      setSystematik(null);
    }
  }

  /**
   * Speichert die gemachten Änderungen
   */
  void saveChanges() {
    Systematik systematik = systematikPanel.getSystematik(); 
    boolean istNeu = systematik.istNeu();
    boolean speichernOK = systematikPanel.saveChanges();
    if (speichernOK) {
      setVeraenderbar(false);
      if (istNeu) {
        systematikPanel.addSpezialisiertListe(systematik);
        systematikenTableModel.add(systematik);      
        systematikenTreeModel.addSystematik(systematik);
      } else {
        systematikenTreeModel.updateSystematik(systematik);        
      }
      setSystematik(systematik);      
    }
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  void aenderungenVerwerfen() {
    if (systematikPanel.aenderungenVerwerfen()) {
      setVeraenderbar(false);
    }
    setSystematik(systematikPanel.getSystematik());      
  }
  
  public JMenu getMenu() {
    JMenu menue = new JMenu("Systematiken");
    JMenuItem loeschButton = new JMenuItem("Systematiken löschen");
    loeschButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        alleSystematikenLoeschen();
      }
    });
    JMenuItem importButton = new JMenuItem("Systematiken importieren");
    importButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        systematikenImportieren();
      }
    });
    JMenuItem exportButton = new JMenuItem("Systematiken exportieren");
    exportButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        systematikenExportieren();
      }
    });
    
    menue.add(exportButton);
    menue.add(importButton);
    menue.addSeparator();
    menue.add(loeschButton);
    
    return menue;
  }
  
  protected void alleSystematikenLoeschen() {
    int erg = JOptionPane.showConfirmDialog(hauptFenster, "Sollen wirklich "+
        "alle Systematiken gelöscht werden? ", "Alle Systematiken löschen",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    if (erg == JOptionPane.YES_OPTION) {
      hauptFenster.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Datenbank.getInstance().getSystematikFactory().loescheAlleSystematiken();   
      aktualisiere();
      
      hauptFenster.setCursor(Cursor.getDefaultCursor());
    }
  }

  protected JFileChooser getFileChooser() {
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new FileFilter() {

      public boolean accept(File file) {
        return file.isDirectory() || 
        file.getName().endsWith(".xml") || 
        file.getName().endsWith(".XML");
      }

      public String getDescription() {        
        return "XML-Dateien";
      }      
    });
    
    return fc;
  }
  
  protected void systematikenImportieren() {
    JFileChooser fc = getFileChooser();
    fc.showOpenDialog(hauptFenster);    
    
    File file = fc.getSelectedFile();
    if (file != null) {
      hauptFenster.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));      
      try {
        SystematikXMLDatei.getInstance().importSystematiken(file);
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Fehler beim " +
            "Importieren der Systematik-Datei '"+file.getAbsolutePath()+"'!,",
            false);
      }
      aktualisiere();
      hauptFenster.setCursor(Cursor.getDefaultCursor());
    }    
  }

  protected void systematikenExportieren() {
    
    JFileChooser fc = getFileChooser();
    fc.showSaveDialog(hauptFenster);    
    
    File file = fc.getSelectedFile();
    if (file != null) {
      hauptFenster.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      String absolutePath = file.getAbsolutePath();
      if (!absolutePath.endsWith(".xml") && !absolutePath.endsWith(".XML")) {
        absolutePath = absolutePath+".xml";
        file = new File(absolutePath);
      }
      try {
        SystematikXMLDatei.getInstance().exportSystematiken(file);
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Fehler beim " +
            "Exportieren der Systematik-Datei '"+absolutePath+"'!,",
            false);
      }
      hauptFenster.setCursor(Cursor.getDefaultCursor());
    }    
  }

  public void focusLost() {
  }
}