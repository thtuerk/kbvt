package de.oberbrechen.koeb.pdf.pdfAufkleber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.gui.components.BenutzerListeSortierungPanel;

public class PdfBenutzerausweisBenutzerAusgabeKonfigDialog {
    
  private JPanel ausgabe;

  int sortierung;
  boolean umgekehrteSortierung;
  int startpos = 1;
  
  private BenutzerListeSortierungPanel benutzerListeSortierungPanel;
  private JComboBox startposComboBox;
  private int maxStartPos;
  private JCheckBox zeigeRueckSeiteCheckBox;
  
  public PdfBenutzerausweisBenutzerAusgabeKonfigDialog() {    
    this.maxStartPos = 12;
    
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    setSortierung(MedienListe.TitelAutorSortierung);
    setUmgekehrteSortierung(false);
    setZeigeRueckseite(true);
  }

  private void jbInit() throws Exception {
    ausgabe = new JPanel();
    
    startposComboBox = new JComboBox();
    for (int i=1; i <= maxStartPos; i++)
      startposComboBox.addItem(new Integer(i));
    zeigeRueckSeiteCheckBox = new JCheckBox("erstelle Rückseiten"); 
        
    benutzerListeSortierungPanel = new BenutzerListeSortierungPanel();
    benutzerListeSortierungPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sortierung")); 
    
    ausgabe.setLayout(new GridBagLayout());
    ausgabe.add(new JLabel("Startposition:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0 
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 5), 0, 0));
    ausgabe.add(startposComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 0), 0, 0));
    ausgabe.add(benutzerListeSortierungPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 0), 0, 0));
    ausgabe.add(zeigeRueckSeiteCheckBox, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }
  
   
  
  public int getSortierung() {
    return sortierung;
  }

  public void setSortierung(int sortierung) {
    benutzerListeSortierungPanel.setSortierung(sortierung);
    this.sortierung = benutzerListeSortierungPanel.getSortierung();
  }
  
  public boolean getUmgekehrteSortierung() {
    return umgekehrteSortierung;
  }
  
  public void setUmgekehrteSortierung(boolean umgekehrteSortierung) {
    this.umgekehrteSortierung = umgekehrteSortierung;
    benutzerListeSortierungPanel.setUmgekehrteSortierung(umgekehrteSortierung);
  }

  public void show(JFrame main) {
    int erg = JOptionPane.showConfirmDialog(main, ausgabe ,"Konfiguration", 
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
    if (erg == JOptionPane.OK_OPTION) {
      sortierung = benutzerListeSortierungPanel.getSortierung();
      umgekehrteSortierung = benutzerListeSortierungPanel.getUmgekehrteSortierung();
      startpos = startposComboBox.getSelectedIndex()+1;
    }
    
    setSortierung(sortierung);
    setUmgekehrteSortierung(umgekehrteSortierung);
    setStartPos(startpos);
  }
  
  public int getStartPos() {
    return startpos;
  }
  
  public void setStartPos(int startPos) {
    if (startPos > 0 && startPos <= maxStartPos) {
      startposComboBox.setSelectedIndex(startPos-1);
    } else {
      startposComboBox.setSelectedIndex(startPos);
    }
  }
  
  public boolean getZeigeRueckseite() {
    return zeigeRueckSeiteCheckBox.isSelected();
  }
  
  public void setZeigeRueckseite(boolean zeige) {
    zeigeRueckSeiteCheckBox.setSelected(zeige);
  }
  
}