package de.oberbrechen.koeb.gui.ausleihe.medienSucheReiter;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.datenstrukturen.MedientypListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.AusleiheMainReiter;
import de.oberbrechen.koeb.gui.ausleihe.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.components.medienDetails.MedienDetailsPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;


/**
 * Diese Klasse repräsentiert den Reiter, der die Suche von 
 * Informationen über ein Medium ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MedienSucheReiter extends JPanel implements 
  AusleiheMainReiter {

  private Main hauptFenster;
  private JButton suchenButton;
  private JButton mediumAnzeigenButton;
  private MedienTableModel medienModel;
  private JTextField stichwortTextField;
  private JTextField titelTextField;
  private JTextField autorTextField;
  private JComboBox medientypComboBox;

  private MedienDetailsPanel medienDetailsPanel;
  
  public MedienSucheReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    aktualisiere();
  }

  private void jbInit() throws Exception {
    //Buttons
    suchenButton = new JButton("Suchen");
    mediumAnzeigenButton = new JButton("Details anzeigen");
    mediumAnzeigenButton.setEnabled(false);
    
    suchenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            suche();
          }
        }.start();
      }
    });
    mediumAnzeigenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hauptFenster.zeigeMedium(medienDetailsPanel.getMedium());
      }
    });

    //SuchenPanel
    stichwortTextField = new JTextField();
    titelTextField = new JTextField();
    autorTextField = new JTextField();
    medientypComboBox = new JComboBox();
               
    medientypComboBox.addItem("");
    MedientypListe medientypDaten = Datenbank.getInstance().getMedientypFactory().getAlleMedientypen();
    medientypDaten.setSortierung(MedientypListe.StringSortierung);
    for (int i=0; i < medientypDaten.size(); i++) {
      medientypComboBox.addItem(medientypDaten.get(i));
    }
    
    
    JComponentFormatierer.setDimension(medientypComboBox, titelTextField.getPreferredSize());
    JComponentFormatierer.setDimension(suchenButton, 
        new Dimension(suchenButton.getPreferredSize().width, titelTextField.getPreferredSize().height));
           
    
    JPanel searchPanel = new JPanel();
    searchPanel.setLayout(new GridBagLayout());
    searchPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    
    JLabel medienTypLabel = new JLabel("Medientyp:");
    JComponentFormatierer.setDimension(medienTypLabel, new JLabel("Beschreibung:").getPreferredSize());
    searchPanel.add(new JLabel("Stichwort:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
    searchPanel.add(stichwortTextField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    searchPanel.add(new JLabel("Autor:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
    searchPanel.add(autorTextField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    searchPanel.add(new JLabel("Titel:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
    searchPanel.add(titelTextField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    searchPanel.add(medienTypLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 10), 0, 0));
    searchPanel.add(medientypComboBox, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
    searchPanel.add(suchenButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    

    //MediumPanel
    medienDetailsPanel = new MedienDetailsPanel();
    medienDetailsPanel.setVeraenderbar(false);
    
    JPanel mediumPanel = new JPanel();
    mediumPanel.setLayout(new GridBagLayout());
    mediumPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    mediumPanel.add(medienDetailsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 0), 0, 0));
    mediumPanel.add(mediumAnzeigenButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    
    //Medien-Tabelle
    JScrollPane medienlisteScrollPane = new JScrollPane();
    medienlisteScrollPane.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(10,10,10,10),
      BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
    JComponentFormatierer.setDimension(
      medienlisteScrollPane, new Dimension(0, 40));

    final JTable medienTabelle = new JTable(medienModel);
    medienTabelle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);    
    medienModel = new MedienTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(medienModel, medienTabelle);
    
    medienlisteScrollPane.getViewport().add(medienTabelle);
        
    medienTabelle.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {          

          public void valueChanged(ListSelectionEvent e) {
            int zeile = medienTabelle.getSelectedRow();
            if (zeile == -1) {
              medienDetailsPanel.setMedium(null);
              mediumAnzeigenButton.setEnabled(false);
            } else {
              mediumAnzeigenButton.setEnabled(true);
              Medium medium = (Medium) medienModel.get(zeile);
              medienDetailsPanel.setMedium(medium);              
            }
          }
          
        });
    //Splitpanel
    JSplitPane jSplitPane2 = new JSplitPane();
    jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane2.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane2.add(searchPanel, JSplitPane.TOP);
    jSplitPane2.add(mediumPanel, JSplitPane.BOTTOM);
    jSplitPane2.setResizeWeight(0);
    
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane1.add(jSplitPane2, JSplitPane.LEFT);
    jSplitPane1.add(medienlisteScrollPane, JSplitPane.RIGHT);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(jSplitPane1, BorderLayout.CENTER);
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
    //Mediumdetails wird aktualisiert. Dies aktualisiert automatisch
    //auch die Ausleihen, etc.
  }

  //Doku siehe bitte Interface
  public void refresh() {
  }

  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
  
  void suche() {
    MediumFactory mediumFactory = Datenbank.getInstance().getMediumFactory();
    
    String stichwort = stichwortTextField.getText();
    String autor = autorTextField.getText();
    String titel = titelTextField.getText();
    Medientyp medientyp = null; 
    if (medientypComboBox.getSelectedItem() instanceof Medientyp)
      medientyp = (Medientyp) medientypComboBox.getSelectedItem();
      
    try {
      final MedienListe liste = mediumFactory.sucheMedien(
          stichwort, autor, titel, medientyp);
      
      SwingUtilities.invokeLater(new Thread() {
        public void run() {
          medienModel.setDaten(liste);
        }
      });      
      
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      medienModel.setDaten(new MedienListe());
    }
  }
}