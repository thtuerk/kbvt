package de.oberbrechen.koeb.gui.ausleihe.ausleihenInfoReiter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.AusleihenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.AusleiheMainReiter;
import de.oberbrechen.koeb.gui.ausleihe.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.components.medienDetails.MedienDetailsPanel;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse repräsentiert den Reiter, der Informationen über ein Medium
 * ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class AusleihenInfoReiter extends JPanel implements AusleiheMainReiter {

  Main hauptFenster;
  MedienDetailsPanel mediumDetailsPanel;
  AusleiheTableModel ausleihenModel;
  JTable ausleihenTabelle;
  AusleiheFactory ausleiheFactory;

  public AusleihenInfoReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    ausleiheFactory = Datenbank.getInstance().getAusleiheFactory();
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
    aktualisiere();
  }

  private void jbInit() throws Exception {
    //Ausleihen-Tabelle
    JScrollPane ausleihenScrollPane = new JScrollPane();
    ausleihenScrollPane.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10),
        BorderFactory.createEtchedBorder(
          Color.white,
          new Color(148, 145, 140))));
    JComponentFormatierer.setDimension(
      ausleihenScrollPane,
      new Dimension(100, 40));
    ausleihenScrollPane.setPreferredSize(new Dimension(500, 40));
    ausleihenModel = new AusleiheTableModel();
    ausleihenTabelle = new JTable();
    ErweiterterTableCellRenderer.setzeErweitertesModell(ausleihenModel, ausleihenTabelle);
    ausleihenScrollPane.getViewport().add(ausleihenTabelle);

    ausleihenTabelle
      .getSelectionModel()
      .addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {        
        if (e.getValueIsAdjusting()) return;
        
        int gewaehlteReihe = ausleihenTabelle.getSelectedRow();
        if (gewaehlteReihe != -1) {
          Ausleihe gewaehlteAusleihe = (Ausleihe) ausleihenModel.get(gewaehlteReihe);
          mediumDetailsPanel.setMedium(gewaehlteAusleihe.getMedium());
        } else {
          mediumDetailsPanel.setMedium(null);
        }
      }
    });

    mediumDetailsPanel = new MedienDetailsPanel();
    mediumDetailsPanel.setVeraenderbar(false);
    mediumDetailsPanel.setBorder(
      BorderFactory.createEmptyBorder(10, 10, 10, 10));

    //Splitpanel
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane1.add(ausleihenScrollPane, JSplitPane.LEFT);
    jSplitPane1.add(mediumDetailsPanel, JSplitPane.RIGHT);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(jSplitPane1, BorderLayout.CENTER);
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
    this.refresh();
    mediumDetailsPanel.aktualisiere();
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
    if (benutzer == null)
      return;

    AusleihenListe liste;
    try {
      liste = ausleiheFactory.getAlleAusleihenVon(benutzer);
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      liste = new AusleihenListe();
    }
    ausleihenModel.setDaten(liste);

    if (liste.size() > 0)
      ausleihenTabelle.getSelectionModel().addSelectionInterval(0, 0);
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setBenutzer(hauptFenster.getAktivenBenutzer());
    mediumDetailsPanel.refresh();
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}