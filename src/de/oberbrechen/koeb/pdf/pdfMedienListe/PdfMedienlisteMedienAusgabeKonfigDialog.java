package de.oberbrechen.koeb.pdf.pdfMedienListe;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import de.oberbrechen.koeb.datenstrukturen.MedienListe;
import de.oberbrechen.koeb.gui.components.MedienListeSortierungPanel;

class PdfMedienlisteMedienAusgabeKonfigDialog {
    
  private JPanel ausgabe;

  String titel;
  int sortierung;
  boolean umgekehrteSortierung;
  boolean zeigeZeilenHintergrund;
  boolean zeigeSpaltenHintergrund;
  boolean querFormat;
  
  private JTextField titelFeld;
  private JCheckBox zeigeSpaltenHintergrundCheckBox;
  private JCheckBox zeigeZeilenHintergrundCheckBox;
  private JCheckBox querFormatCheckBox;
  private MedienListeSortierungPanel medienListeSortierungPanel;
  
  public PdfMedienlisteMedienAusgabeKonfigDialog(boolean spaltenZeilenEinstellungen) {
    try {
      jbInit(spaltenZeilenEinstellungen);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    setTitel("Medienliste");
    setSortierung(MedienListe.TitelAutorSortierung);
    setUmgekehrteSortierung(false);
    setZeigeSpaltenHintergrund(true);
    setZeigeZeilenHintergrund(true);
  }

  private void jbInit(boolean spaltenZeilenEinstellungen) throws Exception {
    ausgabe = new JPanel();

    titelFeld = new JTextField();
    zeigeSpaltenHintergrundCheckBox = new JCheckBox("Spalten-Hintergrund");
    zeigeZeilenHintergrundCheckBox = new JCheckBox("Zeilen-Hintergrund");
    querFormatCheckBox = new JCheckBox("Querformat");
    
    medienListeSortierungPanel = new MedienListeSortierungPanel();
    medienListeSortierungPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sortierung"));
    
    ausgabe.setLayout(new GridBagLayout());
    ausgabe.add(new JLabel("Titel:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 15, 5), 0, 0));
    ausgabe.add(titelFeld, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 0), 0, 0));
    ausgabe.add(medienListeSortierungPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 15, 0), 0, 0));
    
    if (spaltenZeilenEinstellungen) {
      ausgabe.add(zeigeSpaltenHintergrundCheckBox, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0
          ,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));        
      ausgabe.add(zeigeZeilenHintergrundCheckBox, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0
          ,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    }
    ausgabe.add(querFormatCheckBox, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0
        ,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));        
  }
  
  public int getSortierung() {
    return sortierung;
  }

  public void setSortierung(int sortierung) {
    medienListeSortierungPanel.setSortierung(sortierung);
    this.sortierung = medienListeSortierungPanel.getSortierung();
  }

  public String getTitel() {
    return titel;
  }

  public void setTitel(String titel) {
    this.titel = titel;
    titelFeld.setText(titel);
  }
  
  public boolean getUmgekehrteSortierung() {
    return umgekehrteSortierung;
  }
  
  public void setUmgekehrteSortierung(boolean umgekehrteSortierung) {
    this.umgekehrteSortierung = umgekehrteSortierung;
    medienListeSortierungPanel.setUmgekehrteSortierung(umgekehrteSortierung);
  }

  public boolean getZeigeSpaltenHintergrund() {
    return zeigeSpaltenHintergrund;
  }

  public void setZeigeSpaltenHintergrund(boolean zeigeSpaltenHintergrund) {
    this.zeigeSpaltenHintergrund = zeigeSpaltenHintergrund;
    zeigeSpaltenHintergrundCheckBox.setSelected(zeigeSpaltenHintergrund);
  }
  
  public void setZeigeZeilenHintergrund(boolean zeigeZeilenHintergrund) {
    this.zeigeZeilenHintergrund = zeigeZeilenHintergrund;
    zeigeZeilenHintergrundCheckBox.setSelected(zeigeZeilenHintergrund);
  }
  
  public void setQuerformat(boolean querFormat) {
    this.querFormat = querFormat;
    querFormatCheckBox.setSelected(querFormat);
  }
  
  public boolean getQuerFormat() {
    return querFormat;
  }
  
  public boolean getZeigeZeilenHintergrund() {
    return zeigeZeilenHintergrund;
  }



  public void show(JFrame main) {
    int erg = JOptionPane.showConfirmDialog(main, ausgabe ,"Konfigurieren",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
    if (erg == JOptionPane.OK_OPTION) {
      titel = titelFeld.getText();
      sortierung = medienListeSortierungPanel.getSortierung();
      umgekehrteSortierung = medienListeSortierungPanel.getUmgekehrteSortierung();
      zeigeSpaltenHintergrund = zeigeSpaltenHintergrundCheckBox.isSelected();
      zeigeZeilenHintergrund = zeigeZeilenHintergrundCheckBox.isSelected();
      querFormat = querFormatCheckBox.isSelected();
    }
    
    setTitel(titel);
    setSortierung(sortierung);
    setUmgekehrteSortierung(umgekehrteSortierung);
    setZeigeSpaltenHintergrund(zeigeSpaltenHintergrund);
    setZeigeZeilenHintergrund(zeigeZeilenHintergrund);    
    setQuerformat(querFormat);    
  }
}