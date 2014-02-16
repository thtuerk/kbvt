package de.oberbrechen.koeb.gui.ausleihe.mahnungenReiter;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.ausgaben.MahnungslisteAusgabenFactory;
import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.email.EMail;
import de.oberbrechen.koeb.email.EMailHandler;
import de.oberbrechen.koeb.email.MahnungEMailFactory;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.AusleiheMainReiter;
import de.oberbrechen.koeb.gui.ausleihe.Main;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.pdf.pdfMahnungsListe.PdfMahnungslisteAusgabenFactory;

/**
 * Diese Klasse repräsentiert den Reiter, der die Anzeige der Mahnungen
 * ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MahnungenReiter extends JPanel implements AusleiheMainReiter {
  private static final long serialVersionUID = 1L;

  Main hauptFenster;

  JTable mahnungenTable;
  MahnungenTableModel model;

  JButton zuBenutzerButton;
  JButton eMailButton;
  JButton mahnungslisteButton;

  MahnungslisteAusgabenFactory mahnungslisteAusgabeFactory;

  public MahnungenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();
    try {
      mahnungslisteAusgabeFactory = (MahnungslisteAusgabenFactory) 
        einstellungFactory.getClientEinstellung( 
          "de.oberbrechen.koeb.pdf", "MahnungslisteAusgabe").
          getWertObject(MahnungslisteAusgabenFactory.class, 
          PdfMahnungslisteAusgabenFactory.class);
    } catch (UnpassendeEinstellungException e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    //Buttons bauen
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    buttonPanel.setLayout(new GridLayout(1, 2, 15, 5));
    zuBenutzerButton = new JButton("Details");

    zuBenutzerButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        zuBenutzer();
      }
    });
    zuBenutzerButton.setEnabled(false);

    boolean zeigeEMailButton =
      Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
        this.getClass().getName(), "erstelleEmailMahnungen").getWertBoolean(true)
      && EMailHandler.getInstance().erlaubtAnzeige();    
        
    eMailButton = new JButton("eMail");
    if (zeigeEMailButton) {
      eMailButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          new Thread() {
            public void run() {
              eMailAusgeben();
            }
          }.start();
        }
      });
      eMailButton.setEnabled(false);
    }  
      
    mahnungslisteButton = new JButton("Mahnungsliste");
    mahnungslisteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            mahnungslisteAusgeben();
          }
        }.start();
      }
    });
  
    buttonPanel.add(zuBenutzerButton);
    if (zeigeEMailButton) buttonPanel.add(eMailButton);
    buttonPanel.add(mahnungslisteButton);
    

    //Tabelle bauen
    mahnungenTable = new JTable();
    model = new MahnungenTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(model, mahnungenTable);
    mahnungenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mahnungenTable.setColumnSelectionAllowed(false);
    mahnungenTable.getSelectionModel().addListSelectionListener(
      new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        if (mahnungenTable.getSelectedRow() == -1) {
          zuBenutzerButton.setEnabled(false);
          eMailButton.setEnabled(false);
        } else {
          Mahnung aktuelleMahnung = (Mahnung) model.get(mahnungenTable.getSelectedRow());
          zuBenutzerButton.setEnabled(true);
          eMailButton.setEnabled(aktuelleMahnung.getBenutzer().getEMail() != null);              
        }
      }
    });

    JScrollPane mahnungenScrollPane = new JScrollPane();
    JComponentFormatierer.setDimension(
      mahnungenScrollPane, new Dimension(0, 40));
    mahnungenScrollPane.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEmptyBorder(10,10,10,10),
      BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140))));
    mahnungenScrollPane.getViewport().add(mahnungenTable, null);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    this.add(mahnungenScrollPane, BorderLayout.CENTER);
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  //Doku siehe bitte Interface
  public void refresh() {
    model.reload();
  }

  void zuBenutzer() {
    Mahnung aktuelleMahnung = (Mahnung) model.get(mahnungenTable.getSelectedRow());
    hauptFenster.setAktivenBenutzer(aktuelleMahnung.getBenutzer());
    hauptFenster.zeigeAusleiheReiter();
  }

  void mahnungslisteAusgeben() {
    try {
      this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      Ausgabe ausgabe =mahnungslisteAusgabeFactory.createMahnungslisteAusgabe();
      ausgabe.run(hauptFenster, true);
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Ausgeben der Mahnungsliste:", false);
    }
  }
  
  void eMailAusgeben() {
    try {
      this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      Mahnung aktuelleMahnung = (Mahnung) model.get(mahnungenTable.getSelectedRow());
      EMail email = MahnungEMailFactory.getInstance().erstelleMahnungsEmail(aktuelleMahnung);
      EMailHandler.getInstance().versende(email, true);
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Ausgeben der eMail:", false);
    }
  }

  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}