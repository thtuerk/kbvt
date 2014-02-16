package de.oberbrechen.koeb.gui.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;

/**
 * Diese Klasse stellt ein Panel dar, mit dem MedienListen-Sortierungen
 * angezeigt werden können.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class BenutzerListeSortierungPanel extends JPanel {

  private JCheckBox umgekehrteSortierungCheckBox;
  private JRadioButton sortierungButtonNachname;
  private JRadioButton sortierungButtonVorname;
  private JRadioButton sortierungButtonAnmeldeDatum;
  

  public BenutzerListeSortierungPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }  
  
  public int getSortierung() {
    if (sortierungButtonNachname.isSelected())
      return BenutzerListe.NachnameVornameSortierung;
    
    if (sortierungButtonVorname.isSelected())
      return BenutzerListe.VornameNachnameSortierung;
    
    if (sortierungButtonAnmeldeDatum.isSelected())
      return BenutzerListe.AnmeldedatumSortierung;
    
    int sortierung = BenutzerListe.NachnameVornameSortierung;
    setSortierung(sortierung);
    return sortierung;
  }

  public void setSortierung(int sortierung) {
    switch (sortierung) {
    case BenutzerListe.AnmeldedatumSortierung:
      sortierungButtonAnmeldeDatum.setEnabled(true);
      break;        
    case BenutzerListe.VornameNachnameSortierung:
      sortierungButtonVorname.setEnabled(true);
      break;
    default:
    case BenutzerListe.NachnameVornameSortierung:
      sortierungButtonNachname.setEnabled(true);
      break;
    }
  }
  
  private void jbInit() throws Exception {    
    ButtonGroup sortierungGroup = new ButtonGroup();
    sortierungButtonNachname = new JRadioButton("Nachname"); 
    sortierungButtonVorname = new JRadioButton("Vorname"); 
    sortierungButtonAnmeldeDatum = new JRadioButton("Anmeldedatum");
    sortierungGroup.add(sortierungButtonNachname);
    sortierungGroup.add(sortierungButtonVorname);
    sortierungGroup.add(sortierungButtonAnmeldeDatum);
    sortierungButtonNachname.setSelected(true);
    
    umgekehrteSortierungCheckBox = new JCheckBox("umgekehrte Sortierung");
    
    this.setLayout(new GridBagLayout());
    this.add(sortierungButtonNachname, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(sortierungButtonVorname, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(sortierungButtonAnmeldeDatum, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
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