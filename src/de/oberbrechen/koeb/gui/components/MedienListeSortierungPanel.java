package de.oberbrechen.koeb.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import de.oberbrechen.koeb.datenstrukturen.MedienListe;

/**
 * Diese Klasse stellt ein Panel dar, mit dem MedienListen-Sortierungen
 * angezeigt werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MedienListeSortierungPanel extends JPanel {

  private JCheckBox umgekehrteSortierungCheckBox;
  private JRadioButton sortierungButtonTitel;
  private JRadioButton sortierungButtonAutor;
  private JRadioButton sortierungButtonMedienNr;
  

  public MedienListeSortierungPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }  
  
  public int getSortierung() {
    if (sortierungButtonAutor.isSelected())
      return MedienListe.AutorTitelSortierung;
    
    if (sortierungButtonTitel.isSelected())
      return MedienListe.TitelAutorSortierung;
    
    if (sortierungButtonMedienNr.isSelected())
      return MedienListe.MedienNummerSortierung;
    
    int sortierung = MedienListe.TitelAutorSortierung;
    setSortierung(sortierung);
    return sortierung;
  }

  public void setSortierung(int sortierung) {
    switch (sortierung) {
    case MedienListe.AutorTitelSortierung:
      sortierungButtonAutor.setEnabled(true);
      break;
    case MedienListe.MedienNummerSortierung:
      sortierungButtonMedienNr.setEnabled(true);
      break;
    default:
    case MedienListe.TitelAutorSortierung:
      sortierungButtonTitel.setEnabled(true);
      break;        
    }
  }
  
  private void jbInit() throws Exception {    
    ButtonGroup sortierungGroup = new ButtonGroup();
    sortierungButtonAutor = new JRadioButton("Autor"); 
    sortierungButtonTitel = new JRadioButton("Titel"); 
    sortierungButtonMedienNr = new JRadioButton("Mediennummer");
    sortierungGroup.add(sortierungButtonAutor);
    sortierungGroup.add(sortierungButtonTitel);
    sortierungGroup.add(sortierungButtonMedienNr);
    sortierungButtonTitel.setSelected(true);
    
    umgekehrteSortierungCheckBox = new JCheckBox("umgekehrte Sortierung");
    
    this.setLayout(new GridBagLayout());
    this.add(sortierungButtonTitel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(sortierungButtonAutor, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(sortierungButtonMedienNr, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    this.add(umgekehrteSortierungCheckBox, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
  }

  public boolean getUmgekehrteSortierung() {
    return umgekehrteSortierungCheckBox.isSelected();
  }  
  
  public void setUmgekehrteSortierung(boolean umgekehrteSortierung) {
    umgekehrteSortierungCheckBox.setSelected(umgekehrteSortierung);
  }  
}