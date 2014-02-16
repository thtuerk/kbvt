package de.oberbrechen.koeb.pdf.pdfAufkleber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.gui.components.MedienListeSortierungPanel;

class PdfMedienaufkleberMedienAusgabeKonfigDialog {
    
  private JPanel ausgabe;

  int sortierung;
  boolean umgekehrteSortierung;
  int startpos = 1;
  
  private MedienListeSortierungPanel medienListeSortierungPanel;
  private JComboBox startposComboBox;
  private int maxStartPos;
  
  public PdfMedienaufkleberMedienAusgabeKonfigDialog(int maxStartpos) {    
    this.maxStartPos = maxStartpos;
    
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    setSortierung(MedienListe.TitelAutorSortierung);
    setUmgekehrteSortierung(false);
  }

  private void jbInit() throws Exception {
    ausgabe = new JPanel();
    
    startposComboBox = new JComboBox();
    for (int i=1; i <= maxStartPos; i++)
      startposComboBox.addItem(new Integer(i));
        
    medienListeSortierungPanel = new MedienListeSortierungPanel();
    medienListeSortierungPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sortierung")); 
    
    ausgabe.setLayout(new GridBagLayout());
    ausgabe.add(new JLabel("Startposition:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0 
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 5), 0, 0));
    ausgabe.add(startposComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 0), 0, 0));
    ausgabe.add(medienListeSortierungPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
  }
  
   
  
  public int getSortierung() {
    return sortierung;
  }

  public void setSortierung(int sortierung) {
    medienListeSortierungPanel.setSortierung(sortierung);
    this.sortierung = medienListeSortierungPanel.getSortierung();
  }
  
  public boolean getUmgekehrteSortierung() {
    return umgekehrteSortierung;
  }
  
  public void setUmgekehrteSortierung(boolean umgekehrteSortierung) {
    this.umgekehrteSortierung = umgekehrteSortierung;
    medienListeSortierungPanel.setUmgekehrteSortierung(umgekehrteSortierung);
  }

  public void show(JFrame main) {
    int erg = JOptionPane.showConfirmDialog(main, ausgabe ,
        "Konfiguration",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
    if (erg == JOptionPane.OK_OPTION) {
      sortierung = medienListeSortierungPanel.getSortierung();
      umgekehrteSortierung = medienListeSortierungPanel.getUmgekehrteSortierung();
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
}