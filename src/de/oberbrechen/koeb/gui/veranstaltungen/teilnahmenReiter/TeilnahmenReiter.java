package de.oberbrechen.koeb.gui.veranstaltungen.teilnahmenReiter;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.TableModelEvent;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.Veranstaltungsgruppe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.veranstaltungen.Main;
import de.oberbrechen.koeb.gui.veranstaltungen.VeranstaltungenMainReiter;

/**
 * Diese Klasse repräsentiert den Reiter, der das Ändern 
 * der Teilnahmen eines Benutzers an einer Veranstaltung erlaubt
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class TeilnahmenReiter extends JPanel 
  implements VeranstaltungenMainReiter, TeilnahmenReiterInterface {

  private Main hauptFenster;
  boolean istVeraenderbar;

  private JButton saveButton = new JButton();
  private JButton ladenButton = new JButton();

  private JTable veranstaltungenTable;
  private VeranstaltungenTableModel veranstaltungenTableModel;
  boolean veranstaltungenTableModelWirdVeraendert;
  private JLabel kostenLabel = new JLabel();

  /**
   * Zeigt die übergebenen Veranstaltungsgruppe an.
   * @param veranstaltungsgruppe die anzuzeigende Veranstaltungsgruppe
   */
  public void setVeranstaltungsgruppe(
    Veranstaltungsgruppe veranstaltungsgruppe) {
    if (veranstaltungsgruppe == null) return;
    
    veranstaltungenTableModelWirdVeraendert = true;            
    veranstaltungenTableModel.init(hauptFenster.getAktuellerBenutzer(),
      veranstaltungsgruppe);
    setVeraenderbar(false);
    veranstaltungenTableModelWirdVeraendert = false;            
  }

  /**
   * Zeigt den übergebenen Benutzer an.
   * @param benutzer der anzuzeigende Benutzer
   */
  public void setBenutzer(Benutzer benutzer) {
    if (benutzer == null) return;
    veranstaltungenTableModelWirdVeraendert = true;        
    setVeraenderbar(!benutzer.istGespeichert());
    veranstaltungenTableModel.init(benutzer,
      hauptFenster.getAktuelleVeranstaltungsgruppe());
    veranstaltungenTableModelWirdVeraendert = false;        
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setBenutzer(hauptFenster.getAktuellerBenutzer());
  }

  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    istVeraenderbar = veraenderbar;
    //Buttons anpassen
    hauptFenster.erlaubeAenderungen(!veraenderbar);
    if (!veraenderbar) veranstaltungenTable.clearSelection();

    ladenButton.setEnabled(veraenderbar);
    if (!veraenderbar) {
      saveButton.setText("Bearbeiten");
    } else {
      saveButton.setText("Speichern");
    }
  }

  /**
   * Erzeugt einen TeilnahmenReiter, der im übergebenen Frame angezeigt wird
   * @param parentFrame Frame, zu dem der Reiter gehört
   */
  public TeilnahmenReiter(Main parentFrame) {
    hauptFenster = parentFrame;
    veranstaltungenTableModelWirdVeraendert = true;    
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception e) {
      ErrorHandler.getInstance().handleException(e, true);
    }
    veranstaltungenTableModelWirdVeraendert = false;    
  }

  // erzeugt die GUI
  void jbInit() throws Exception {
    //Buttons bauen
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
    ladenButton.setText("Änderungen verwerfen");
    ladenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //Hier ist es wichtig, dass die Änderungen nicht in einem extra Thread
        //Durchgeführt werden, da sich das Tabellenmodell ändert
        aenderungenVerwerfen();
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
    buttonPanel.setLayout(new GridLayout(1, 2, 15, 5));
    buttonPanel.add(saveButton, null);
    buttonPanel.add(ladenButton, null);

    //Veranstaltungenliste
    veranstaltungenTableModel =
       new VeranstaltungenTableModel(this, hauptFenster.getAktuellerBenutzer(),
       hauptFenster.getAktuelleVeranstaltungsgruppe());
    veranstaltungenTable = new JTable();
    veranstaltungenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    
    ErweiterterTableCellRenderer.setzeErweitertesModell(veranstaltungenTableModel, veranstaltungenTable);

    veranstaltungenTableModel.addTableModelListener(new javax.swing.event.TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        if (!istVeraenderbar && !veranstaltungenTableModelWirdVeraendert) { 
          setVeraenderbar(true);
        }
      }
     });

    JPanel kostenPanel = new JPanel();
    kostenPanel.setLayout(new BorderLayout());
    kostenPanel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
    JLabel jLabel16 = new JLabel("Kosten:");
    kostenPanel.add(jLabel16, BorderLayout.WEST);
    kostenPanel.add(kostenLabel, BorderLayout.EAST);

    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.getViewport().add(veranstaltungenTable, null);
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140)));

    JPanel veranstaltungenPanel = new JPanel();
    veranstaltungenPanel.setLayout(new BorderLayout());
    veranstaltungenPanel.setBorder(BorderFactory.createEmptyBorder(15,10,0,10));
    veranstaltungenPanel.setMinimumSize(new Dimension(100,100));
    veranstaltungenPanel.add(jScrollPane1, BorderLayout.CENTER);
    veranstaltungenPanel.add(kostenPanel, BorderLayout.SOUTH);

    //alles Zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(veranstaltungenPanel, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  public void aktualisiere() {
    setBenutzer(hauptFenster.getAktuellerBenutzer());
    setVeranstaltungsgruppe(hauptFenster.getAktuelleVeranstaltungsgruppe());
  }

  /**
   * Speichert die gemachten Änderungen
   */
  public void saveChanges() {
    if (veranstaltungenTable.isEditing()) {
      JOptionPane.showMessageDialog(hauptFenster, "Bitte beenden Sie zuerst "+
        "die aktuelle Bearbeitung!",
        "Noch in Bearbeitung!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    veranstaltungenTableModel.save();
    setVeraenderbar(false);
  }

  /**
   * Verwirft die aktuellen Änderungen.
   */
  public void aenderungenVerwerfen() {
    if (veranstaltungenTable.isEditing()) {
      JOptionPane.showMessageDialog(hauptFenster, "Bitte beenden Sie zuerst "+
        "die aktuelle Bearbeitung!",
        "Noch in Bearbeitung!", JOptionPane.ERROR_MESSAGE);
      return;
    }

    setBenutzer(hauptFenster.getAktuellerBenutzer());
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
  }

  /**
   * Setzt die Kosten der aktuell angezeigten Veranstaltungen.
   * @param kosten die anzuzeigenden Kosten
   */
  public void setzeKosten(String kosten) {
    kostenLabel.setText(kosten);
  }
  
  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }  
}