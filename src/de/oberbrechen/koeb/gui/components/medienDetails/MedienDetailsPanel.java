package de.oberbrechen.koeb.gui.components.medienDetails;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Medium;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.Format;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.SortiertComboBox;
import de.oberbrechen.koeb.gui.components.jTableTools.ErweiterterTableCellRenderer;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse stellt ein Panel dar, mit dem Medien-Daten 
 * angezeigt werden können. Eine Auswahl verschiedener Medien 
 * ist auch möglich, je nachdem wie setVeraenderbar gesetzt wurde.
 * Im Gegensatz zu MedienPanel können die Medien aber nicht
 * bearbeitet werden. Außerdem werden nicht alle, sondern nur
 * die wichtigsten Informationen angezeigt.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class MedienDetailsPanel extends JPanel {
  
  private Medium currentMedium;
  private JTextField medienTypFeld;
  SortiertComboBox<Medium> medienNrFeld;
  SortiertComboBox<Medium> titelFeld;
  private JTextField autorFeld;
  private SystematikTableModel systematiken;
  private JTextArea beschreibungFeld;
  private Vector<MedienDetailsListener> medienDetailsListener;

  /**
   * Zeigt das übergebene Medium an.
   * @param medium das anzuzeigende Medium
   */
  public void setMedium(Medium medium) {
    currentMedium = medium;
    medienNrFeld.setDelayItemListenerEnabled(false);
    titelFeld.setDelayItemListenerEnabled(false);
    if (medium == null) {
      medienTypFeld.setText(null);
      medienNrFeld.setSelectedItem(null);
      titelFeld.setSelectedItem(null);
      autorFeld.setText(null);
      systematiken.setDaten(new SystematikListe());
      beschreibungFeld.setText(null);
    } else {
      medienTypFeld.setText(medium.getMedientyp().getName());
      medienNrFeld.setSelectedItem(medium);
      titelFeld.setSelectedItem(medium);
      autorFeld.setText(medium.getAutor());

      systematiken.setDaten(medium.getSystematiken());
      beschreibungFeld.setText(medium.getBeschreibung());
    }    
    
    Iterator<MedienDetailsListener> it = medienDetailsListener.iterator();
    while (it.hasNext()) {
      it.next().
        neuesMediumAusgewaehlt(currentMedium);
    }
    medienNrFeld.setDelayItemListenerEnabled(true);
    titelFeld.setDelayItemListenerEnabled(true);
  }
  
  /**
   * Mit dieser Methode wird der GUI mitgeteilt, ob der aktuell angezeigte
   * Benutzer verändert werden darf. Dies ist wichtig, da abhängig davon einige
   * Buttons verändert werden müssen.
   * @param gespeichert ist Benutzer gespeichert oder nicht?
   */
  public void setVeraenderbar(boolean veraenderbar) {
    medienNrFeld.setEnabled(veraenderbar);
    titelFeld.setEnabled(veraenderbar);
  }

  public MedienDetailsPanel() {
    currentMedium = null;
    medienDetailsListener = new Vector<MedienDetailsListener>();
    try {
      jbInit();
      aktualisiere();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }  

  /**
   * Fügt einen neuen Listener hinzu, der bei Änderung
   * des Mediums dieses Panels informiert wird.
   * @param listener der hinzuzufügende Listener
   */
  public void addMedienDetailsListener(MedienDetailsListener listener) {
    if (listener != null) medienDetailsListener.add(listener);  
  }
  
  // erzeugt die GUI
  void jbInit() throws Exception {
    //Medium-Details bauen
    JLabel jLabel1 = new JLabel("Medientyp:");
    JLabel jLabel2 = new JLabel("Medien-Nr:");
    JLabel jLabel3 = new JLabel("Titel:");
    JLabel jLabel4 = new JLabel("Autor:");
    JLabel jLabel5 = new JLabel("Systematik:");
    JLabel jLabel6 = new JLabel("Beschreibung:");
    medienTypFeld = new JTextField();
    autorFeld = new JTextField();
    beschreibungFeld = new JTextArea();
    beschreibungFeld.setEnabled(true);
    beschreibungFeld.setEditable(false);
    beschreibungFeld.setWrapStyleWord(true);
    beschreibungFeld.setLineWrap(true);

    titelFeld = new SortiertComboBox<Medium>(new Format<Medium>() {
      public String format (Medium o) {
        if (o == null) return null;
        String erg = o.getTitel();
        if (erg == null) return "";
        return erg;
      }
    });
    Dimension eingabeDimension = new Dimension(200, medienTypFeld.getPreferredSize().height);
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
        setMedium(medienNrFeld.getSelectedItemTyped());
      }
    });

    medienTypFeld.setEditable(false);
    autorFeld.setEditable(false);
    medienNrFeld.setEditable(false);
    medienTypFeld.setEditable(false);

    systematiken = new SystematikTableModel();
    JTable systematikTabelle = new JTable();
    ErweiterterTableCellRenderer.setzeErweitertesModell(systematiken, systematikTabelle);
    systematikTabelle.setEnabled(false);

    JScrollPane systematikScrollPane = new JScrollPane(systematikTabelle);
    JComponentFormatierer.setDimension(systematikScrollPane, new Dimension(0, 40));
    JScrollPane beschreibungScrollPane = new JScrollPane(beschreibungFeld);
    JComponentFormatierer.setDimension(beschreibungScrollPane, new Dimension(0, 40));

    this.setLayout(new GridBagLayout());
    this.add(jLabel1,              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel2,          new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(jLabel3,         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel4,         new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    this.add(jLabel6,          new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
    this.add(jLabel5,         new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));

    this.add(medienTypFeld, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(medienNrFeld, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(titelFeld, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(autorFeld, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    this.add(beschreibungScrollPane,     new GridBagConstraints(1, 4, 1, 1, 1.0, 5.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
    this.add(systematikScrollPane,   new GridBagConstraints(1, 5, 1, 1, 1.0, 5.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

  }


  public void aktualisiere() {        
    try {
      MediumFactory mediumFactory = Datenbank.getInstance().getMediumFactory();
      
      titelFeld.setDaten(mediumFactory.getAlleMedienTitelSortierung());
      medienNrFeld.setDaten(mediumFactory.getAlleMedienMediennrSortierung());
      this.refresh();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
    }
  }
  
  /**
   * Aktualisiert das angezeigte Medium 
   */
  public void refresh() {
    if (currentMedium != null) {
      try {
        currentMedium.reload();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
        currentMedium = null;
      }
    } 
    this.setMedium(currentMedium);
  }

  /**
   * Liefert das aktuell angezeigte Medium
   */
  public Medium getMedium() {
    return currentMedium;
  }

}