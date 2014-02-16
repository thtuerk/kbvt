package de.oberbrechen.koeb.gui.ausleihe.medienInfoReiter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Date;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.*;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.components.medienDetails.*;
import de.oberbrechen.koeb.gui.components.medienDetails.MedienDetailsListener;
import de.oberbrechen.koeb.gui.framework.*;
import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.*;


/**
 * Diese Klasse repräsentiert den Reiter, der Informationen über ein Medium
 * ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MedienInfoReiter extends JPanel implements 
  AusleiheMainReiter, MedienDetailsListener {

  private Main hauptFenster;

  private MedienDetailsPanel mediumDetailsPanel;
  private AusleiheTableModel ausleihen;
  private Ausleihe aktuelleAusleihe;
  private Ausleihe ersteAusleihe;
  private JButton zurAktuellenAusleiheButton;
  private JButton irregulaerZurueckgebenButton;
  private AusleiheFactory ausleiheFactory;

  public MedienInfoReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    ausleiheFactory = Datenbank.getInstance().getAusleiheFactory();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    aktualisiere();
  }

  private void jbInit() throws Exception {
    //Buttonpanel
    irregulaerZurueckgebenButton = new JButton("Medium irregulär zurückgeben");
    zurAktuellenAusleiheButton = new JButton("zur aktuellen Ausleihe");

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1,4,15,0));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    zurAktuellenAusleiheButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        zeigeAktuelleAusleihe();
      }
    });
    irregulaerZurueckgebenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        irregulaerZurueckgeben();
      }
    });
    buttonPanel.add(irregulaerZurueckgebenButton, null);
    buttonPanel.add(zurAktuellenAusleiheButton, null);

    //Ausleihen-Tabelle
    JScrollPane ausleihenScrollPane = new JScrollPane();
    ausleihenScrollPane.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(10,10,10,10),
      BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
    JComponentFormatierer.setDimension(
      ausleihenScrollPane, new Dimension(0, 40));
    JTable ausleihenTabelle = new JTable();
    ausleihen = new AusleiheTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(ausleihen, ausleihenTabelle);
    ausleihenTabelle.setEnabled(false);
    ausleihenScrollPane.getViewport().add(ausleihenTabelle);

    mediumDetailsPanel = new MedienDetailsPanel();
    mediumDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,4,10));    
    mediumDetailsPanel.addMedienDetailsListener(this);
    
    //Splitpanel
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane1.add(mediumDetailsPanel, JSplitPane.LEFT);
    jSplitPane1.add(ausleihenScrollPane, JSplitPane.RIGHT);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(jSplitPane1, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
    mediumDetailsPanel.aktualisiere();
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
    irregulaerZurueckgebenButton.setEnabled(ersteAusleihe != null &&
      (ersteAusleihe.istZurueckgegeben() || !ersteAusleihe.getBenutzer().equals(
      hauptFenster.getAktivenBenutzer())));
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
    //Mediumdetails wird aktualisiert. Dies aktualisiert automatisch
    //auch die Ausleihen, etc.
    mediumDetailsPanel.setMedium(medium);
  }

  /**
   * Zeigt Details zum übergebenen Medium an
   */
  public void setMedium(Medium medium) {
    mediumEANGelesen(medium);
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setBenutzer(hauptFenster.getAktivenBenutzer());
    mediumDetailsPanel.refresh();
  }

  /**
   * Zeigt die aktuelle Ausleihe an.
   */
  void zeigeAktuelleAusleihe() {
    hauptFenster.zeigeAusleihe(aktuelleAusleihe);
  }

  /**
   * Gibt das Medium irregulär zurück
   */
  void irregulaerZurueckgeben() {
    try {
      Medium aktuellesMedium = mediumDetailsPanel.getMedium();
      if (aktuellesMedium == null) return;
      Benutzer aktuellerBenutzer = hauptFenster.getAktivenBenutzer();
  
      if (ersteAusleihe == null || ersteAusleihe.istZurueckgegeben()) {
        int erg = JOptionPane.showConfirmDialog(hauptFenster, "Wollen Sie "+
          "wirklich das Medium '"+aktuellesMedium.getTitel()+"' ("+
          aktuellesMedium.getMedienNr()+")\nals von "+aktuellerBenutzer.getName()+
          " zurückgegeben eintragen,\nobwohl dieses Medium zur Zeit nicht als\n"+
          "ausgeliehen eingetragen ist?",
          "Irreguläre Rückgabe bestätigen", JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
        if (erg != JOptionPane.YES_OPTION) return;
  
        Ausleihe neueAusleihe = ausleiheFactory.erstelleNeu();
        neueAusleihe.setMedium(aktuellesMedium);
        neueAusleihe.setBenutzer(aktuellerBenutzer);
        neueAusleihe.setMitarbeiterAusleihe(hauptFenster.getAktuellenMitarbeiter());
        neueAusleihe.setMitarbeiterRueckgabe(hauptFenster.getAktuellenMitarbeiter());
        neueAusleihe.setRueckgabedatum(new Date());
        neueAusleihe.setBemerkungen("Ausleihe irregulär zurueckgegeben!");
        neueAusleihe.save();
  
      } else {
        int erg = JOptionPane.showConfirmDialog(hauptFenster, "Wollen Sie "+
          "wirklich das Medium '"+aktuellesMedium.getTitel()+"' ("+
          aktuellesMedium.getMedienNr()+")\nals von "+aktuellerBenutzer.getName()+
          " zurückgegeben eintragen,\nobwohl dieses Medium zur Zeit als "+
          "von\n"+ersteAusleihe.getBenutzer().getName()+ " ausgeliehen "+
          "eingetragen ist?",
          "Irreguläre Rückgabe bestätigen", JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
        if (erg != JOptionPane.YES_OPTION) return;
  
        Date rueckgabeDatum = new Date();
        if (ersteAusleihe.getSollRueckgabedatum().before(rueckgabeDatum))
          rueckgabeDatum = ersteAusleihe.getSollRueckgabedatum();
        ersteAusleihe.setRueckgabedatum(rueckgabeDatum);
        ersteAusleihe.setMitarbeiterRueckgabe(
          hauptFenster.getAktuellenMitarbeiter());
        ersteAusleihe.setBemerkungen("Ausleihe irregulär zurueckgegeben!");
        ersteAusleihe.save();
  
        Ausleihe neueAusleihe = ausleiheFactory.erstelleNeu();
        neueAusleihe.setMedium(aktuellesMedium);
        neueAusleihe.setBenutzer(aktuellerBenutzer);
        neueAusleihe.setRueckgabedatum(new Date());
        neueAusleihe.setMitarbeiterAusleihe(hauptFenster.getAktuellenMitarbeiter());
        neueAusleihe.setMitarbeiterRueckgabe(hauptFenster.getAktuellenMitarbeiter());
        neueAusleihe.setBemerkungen("Ausleihe irregulär zurueckgegeben!");
        neueAusleihe.save();
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
    this.refresh();
  }

  public void neuesMediumAusgewaehlt(Medium medium) {
    try {
      if (medium == null) {
        ausleihen.setDaten(new AusleihenListe());
        aktuelleAusleihe = null;
        ersteAusleihe = null;
      } else {
        AusleihenListe liste = ausleiheFactory.getAlleAusleihenVon(medium);
        liste.setSortierung(AusleihenListe.AusleihdatumSortierung, false);
        ausleihen.setDaten(liste);
        if (liste.size() > 0) {
          ersteAusleihe = (Ausleihe) liste.first();
          aktuelleAusleihe = ersteAusleihe;
          if (aktuelleAusleihe.istZurueckgegeben()) aktuelleAusleihe = null;
        } else {
          ersteAusleihe = null;
          aktuelleAusleihe = null;
        }
      }    
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      ausleihen.setDaten(new AusleihenListe());
      aktuelleAusleihe = null;
      ersteAusleihe = null;      
    }
    
    zurAktuellenAusleiheButton.setEnabled(aktuelleAusleihe != null);
    irregulaerZurueckgebenButton.setEnabled(ersteAusleihe != null &&
      (ersteAusleihe.istZurueckgegeben() || !ersteAusleihe.getBenutzer().equals(
      hauptFenster.getAktivenBenutzer())));

  }

  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}