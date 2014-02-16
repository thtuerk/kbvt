package de.oberbrechen.koeb.gui.ausleihe.ausleiheReiter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.einstellungen.Ausleihordnung;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.Main;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;
import de.oberbrechen.koeb.gui.standarddialoge.DatumAuswahlDialog;

/**
 * Diese Klasse repräsentiert die Ausleihendatails in dem Reiter, der die
 * Ausleihe und Rückgabe ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class AusleiheDetails extends JPanel {

  Main hauptFenster;

  JTextField medienTypFeld = new JTextField();
  SortiertComboBox<Medium> medienNrFeld;
  SortiertComboBox<Medium> titelFeld;
  JTextField autorFeld = new JTextField();
  JTextField ausleiheVonFeld = new JTextField();
  JTextField ausleiheBisFeld = new JTextField();
  JTextField rueckgabeFeld = new JTextField();
  JTextField bemerkungenFeld = new JTextField();
  JTable zeitraumTable;
  ZeitraumTableModel zeitraumTableModel;
  
  JButton kalenderButton;
  DatumAuswahlDialog datumsDialog;

  static SimpleDateFormat dateFormat =
    new SimpleDateFormat("EE, dd. MMM yyyy");

  Ausleihordnung ausleihordnung;
  Ausleihe aktuelleAusleihe;
  boolean mediumWirdGeradeVeraendert = false;

  public AusleiheDetails(Main parentFrame) {
    hauptFenster = parentFrame;
    ausleihordnung = Buecherei.getInstance().getAusleihordnung();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    datumsDialog = new DatumAuswahlDialog(hauptFenster);
    aktualisiere();
  }

  private void jbInit() throws Exception {
    JLabel jLabel1 = new JLabel("Medientyp:");
    JLabel jLabel2 = new JLabel("Medien-Nr:");
    JLabel jLabel3 = new JLabel("Titel:");
    JLabel jLabel4 = new JLabel("Autor:");
    JLabel jLabel5 = new JLabel("Ausleihe von:");
    JLabel jLabel6 = new JLabel("Ausleihe bis:");
    JLabel jLabel7 = new JLabel("zurückgegeben am:");
    JLabel jLabel8 = new JLabel("Bemerkungen:");
    JLabel jLabel9 = new JLabel("Zeiträume:");
    
    titelFeld = new SortiertComboBox<Medium>(new Format<Medium>() {
      public String format (Medium o) {
        if (o == null) return null;
        String erg = o.getTitel();
        if (erg == null) return "";
        return erg;
      }
    });
    Dimension eingabeDimension = 
      new Dimension(200, ausleiheBisFeld.getPreferredSize().height);
    JComponentFormatierer.setDimension(titelFeld, eingabeDimension);
    titelFeld.addDelayItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setMedium((Medium) titelFeld.getSelectedItem());
      }
    });
    medienNrFeld = new SortiertComboBox<Medium>(new Format<Medium>() {
      public String format (Medium o) {
        if (o == null) return null;
        String erg = o.getMedienNr();
        return erg;
      }
    });
    JComponentFormatierer.setDimension(medienNrFeld, eingabeDimension);
    medienNrFeld.addDelayItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        setMedium((Medium) medienNrFeld.getSelectedItem());
      }
    });

    kalenderButton = new JButton("Kalender");
    Dimension kalenderDimension = new Dimension(
      kalenderButton.getPreferredSize().width,
      ausleiheBisFeld.getPreferredSize().height);
    JComponentFormatierer.setDimension(kalenderButton, kalenderDimension);
    kalenderButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bestimmeSollRueckgabeDatum();
      }
    });
    medienTypFeld.setEditable(false);
    autorFeld.setEditable(false);
    ausleiheVonFeld.setEditable(false);
    ausleiheBisFeld.setEditable(false);
    rueckgabeFeld.setEditable(false);

    //Zeitraum-Tabelle
    zeitraumTable = new JTable();
    zeitraumTableModel = new ZeitraumTableModel();
    ErweiterterTableCellRenderer.setzeErweitertesModell(zeitraumTableModel, zeitraumTable);
    zeitraumTable.setEnabled(false);
    JScrollPane jScrollPane1 = new JScrollPane();
    jScrollPane1.setAutoscrolls(true);
    jScrollPane1.getViewport().add(zeitraumTable);
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder(
        Color.white,new Color(148, 145, 140)));
    JComponentFormatierer.setDimension(jScrollPane1, new Dimension(100, 75));
    
    this.setLayout(new GridBagLayout());
    this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(medienTypFeld, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(medienNrFeld, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(titelFeld, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(autorFeld, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 25, 0), 0, 0));
    this.add(jLabel5, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(ausleiheVonFeld, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel6, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(ausleiheBisFeld, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel7, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 25, 10), 0, 0));
    this.add(rueckgabeFeld, new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0,
      GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 25, 0), 0, 0));
    this.add(jLabel8, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
      GridBagConstraints.WEST, GridBagConstraints.NONE, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(bemerkungenFeld, new GridBagConstraints(1, 9, 2, 1, 1.0, 0.0,
      GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
      new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel9, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
        new Insets(0, 0, 25, 0), 0, 0));
    this.add(jScrollPane1, new GridBagConstraints(1, 8, 2, 1, 1.0, 1.0,
        GridBagConstraints.WEST, GridBagConstraints.BOTH, 
        new Insets(0, 0, 25, 0), 0, 0));
    this.add(kalenderButton, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
      GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, 
      new Insets(0, 5, 5, 0), 0, 0));
  }

  public void aktualisiere() {
    MediumFactory mediumFactory = 
      Datenbank.getInstance().getMediumFactory();
    
    try {
      titelFeld.setDaten(mediumFactory.getAlleMedienTitelSortierung());
      medienNrFeld.setDaten(mediumFactory.getAlleMedienMediennrSortierung());
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }

  /**
   * Zeigt die übergebene Ausleihe an.
   * @param ausleihe die anzuzeigende Ausleihe
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  public void setAusleihe(final Ausleihe ausleihe) {
    mediumWirdGeradeVeraendert = true;
    aktuelleAusleihe = ausleihe;

    Medium medium = (ausleihe == null ? null : ausleihe.getMedium());
    if (medium == null) {
      medienTypFeld.setText(null);
      medienNrFeld.setSelectedItem(null);
      titelFeld.setSelectedItem(null);
      autorFeld.setText(null);
    } else {
      medienTypFeld.setText(medium.getMedientyp().getName());
      medienNrFeld.setSelectedItem(medium);
      titelFeld.setSelectedItem(medium);
      autorFeld.setText(medium.getAutor());
    }
    titelFeld.fireDelayItemListenerEvent();
    medienNrFeld.fireDelayItemListenerEvent();

    if (ausleihe == null) {
      ausleiheBisFeld.setText(null);
      ausleiheVonFeld.setText(null);
      rueckgabeFeld.setText(null);
      bemerkungenFeld.setText(null);
      zeitraumTableModel.clear();
    } else {
      ausleiheVonFeld.setText(formatDate(ausleihe.getAusleihdatum()));
      ausleiheBisFeld.setText(formatDate(ausleihe.getSollRueckgabedatum()));
      rueckgabeFeld.setText(formatDate(ausleihe.getRueckgabedatum()));
      bemerkungenFeld.setText(ausleihe.getBemerkungen());
      zeitraumTableModel.setDaten(ausleihe.getAusleihzeitraeume());
    }
    
    mediumWirdGeradeVeraendert = false;
  }

  /**
   * Zeigt das übergebene Medium an
   * @param medium das anzuzeigende Medium
   */
  void setMedium(Medium medium) {
    if (mediumWirdGeradeVeraendert) return;
    mediumWirdGeradeVeraendert = true;
    if (medium == null) {
      medienTypFeld.setText(null);
      medienNrFeld.setSelectedItem(null);
      titelFeld.setSelectedItem(null);
      autorFeld.setText(null);
      titelFeld.fireDelayItemListenerEvent();
      medienNrFeld.fireDelayItemListenerEvent();
    } else {
      medienTypFeld.setText(medium.getMedientyp().getName());
      medienNrFeld.setSelectedItem(medium);
      titelFeld.setSelectedItem(medium);
      autorFeld.setText(medium.getAutor());
      titelFeld.fireDelayItemListenerEvent();
      medienNrFeld.fireDelayItemListenerEvent();

      Date ausleiheVonDatum = parseDate(ausleiheVonFeld.getText());
      Date ausleiheBisDatum = ausleihordnung.getAusleihenBisDatum(medium, ausleiheVonDatum);
      ausleiheBisFeld.setText(formatDate(ausleiheBisDatum));
    }
    mediumWirdGeradeVeraendert = false;
  }

  /**
   * Formatiert ein Datum für die Ausgabe
   * @param datum das zu formatierende Datum
   * @return das Ergebnis der Formatierung
   */
  private String formatDate(Date datum) {
    if (datum == null) return null;
    return dateFormat.format(datum);
  }

  /**
   * Parst ein von formatDate formatiertes Datum
   * @param datum der zu parsende String
   * @return das Datum, dem der Parameter entspricht
   */
  private Date parseDate(String datum) {
    try {
      if (datum == null) return null;
      return dateFormat.parse(datum);
    } catch (java.text.ParseException e) {
      return null;
    }
  }

  /**
   * Zeigt einen Kalender an, aus dem das Sollrückgabedatum ausgewählt werden
   * kann und setzt das Sollrückgabedatum.
   */
  void bestimmeSollRueckgabeDatum() {
    Date aktuellesDatum = null;
    if (ausleiheBisFeld.getText() != null &&
        !ausleiheBisFeld.getText().equals(""))
      aktuellesDatum = parseDate(ausleiheBisFeld.getText());

    Date gewaehltesDatum = datumsDialog.waehleDatum(null, aktuellesDatum);
    if (gewaehltesDatum == null) return;

    ausleiheBisFeld.setText(formatDate(gewaehltesDatum));
  }

  /**
   * Erlaubt / verbietet Ändern der angezeigten Ausleihe.
   *
   * @param erlaubt bestimmt, ob Änderungen erlaubt sind
   */
  public void erlaubeAenderungen(boolean erlaubt) {
    if (!erlaubt) {
      bemerkungenFeld.setEditable(false);
      kalenderButton.setEnabled(false);
      medienNrFeld.setEnabled(false);
      titelFeld.setEnabled(false);
    } else {
      bemerkungenFeld.setEditable(true);
      if (aktuelleAusleihe == null || !aktuelleAusleihe.istZurueckgegeben()) {
        kalenderButton.setEnabled(true);

        if (aktuelleAusleihe == null || aktuelleAusleihe.heuteGetaetigt()) {
           medienNrFeld.setEnabled(erlaubt);
           titelFeld.setEnabled(erlaubt);
        }
      }
    }
  }

  /**
   * Verwirft die Änderungen an der aktuellen Ausleihe
   */
  public void verwerfeAenderungen() {
    try {
      aktuelleAusleihe.reload();
      setAusleihe(aktuelleAusleihe);
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, false);      
    }
  }

  /**
   * Speichert die Ändeurngen an der aktuellen Ausleihe
   *
   * @throws DatenbankzugriffException falls das Speichern nicht
   *   erfolgreich abgeschlossen wurde
   */
  public void save() throws DatenbankzugriffException {
    if (aktuelleAusleihe == null)
      throw new NullPointerException("Die Ausleihe konnte nicht gespeichert "+
        "werden, da sie null war!");

    aktuelleAusleihe.setMedium((Medium) medienNrFeld.getSelectedItem());
    aktuelleAusleihe.setSollRueckgabedatum(
      parseDate(ausleiheBisFeld.getText()));
    aktuelleAusleihe.setBemerkungen(bemerkungenFeld.getText());

    aktuelleAusleihe.save();
  }

  // Doku siehe Component
  public void grabFocus() {
    if (titelFeld.isEnabled())
      titelFeld.grabFocus();
    else
      bemerkungenFeld.grabFocus();
  }

  // Doku siehe bitte MainReiter-Interface
  public void mediumEANGelesen(Medium medium) {
    if (titelFeld.isEnabled())
      setMedium(medium);
  }

}