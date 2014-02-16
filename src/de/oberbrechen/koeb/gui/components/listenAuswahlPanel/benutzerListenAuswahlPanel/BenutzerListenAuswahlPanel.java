package de.oberbrechen.koeb.gui.components.listenAuswahlPanel.benutzerListenAuswahlPanel;

import java.awt.Dimension;

import javax.swing.JTable;

import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AusgabeAuswahl;
import de.oberbrechen.koeb.dateien.auswahlKonfiguration.AuswahlKonfiguration;
import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeSortierbaresTableModel;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.ListenAuswahlPanel;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.TabellenSelektion;

/**
* Diese Klasse ist eine Implementierung
* des ListenAuswahlPanels für Systematiken.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class BenutzerListenAuswahlPanel extends ListenAuswahlPanel<Benutzer> {
  
  public static final int AUSWAHLTYP_AKTIVE_BENUTZER = 0; //STANDARD
  
  public static final int AUSWAHLTYP_ALLE_BENUTZER = 1;
  public static final int AUSWAHLTYP_PASSIVE_BENUTZER = 2;
  
  public BenutzerListenAuswahlPanel() {
    this(false, false, AUSWAHLTYP_AKTIVE_BENUTZER);
  }

  public BenutzerListenAuswahlPanel(boolean ausrichtung, boolean zeigeAuswahl, int auswahlTyp) {
    super(ausrichtung, zeigeAuswahl, auswahlTyp);
    this.setPreferredSize(new Dimension(600, 200));
    auswahlTabelle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    datenTabelle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
   }

  protected AbstractListeSortierbaresTableModel<Benutzer> createDatenAuswahlTableModel(JTable table, int auswahlTyp) {
    BenutzerFactory benutzerFactory = 
      Datenbank.getInstance().getBenutzerFactory();
    
    BenutzerListe daten = null;
    switch (auswahlTyp) {
      case AUSWAHLTYP_AKTIVE_BENUTZER:
        daten = benutzerFactory.getAlleAktivenBenutzer();
        break;
      case AUSWAHLTYP_ALLE_BENUTZER:
        daten = benutzerFactory.getAlleBenutzer();
        break;
      case AUSWAHLTYP_PASSIVE_BENUTZER:
        daten = benutzerFactory.getAllePassivenBenutzer();
        break;
      default:
        ErrorHandler.getInstance().handleError("Unbekannter Auswahltyp " +auswahlTyp, false);
        daten = benutzerFactory.getAlleBenutzer();
        break;
    }
    
    return new BenutzerTableModel(daten);
  }

  protected AbstractListeSortierbaresTableModel<Benutzer> createEmptyAuswahlTableModel(JTable table, int auswahlTyp) {
    return new BenutzerTableModel(new BenutzerListe());
  }
  
  public void initAuswahlFeld(AuswahlKonfiguration konfiguration) {        
    auswahlComboBox.removeAllItems();
    auswahlComboBox.addItem("");

    for (int i=0; i < konfiguration.getAusgabeAnzahl(); i++) {
      final AusgabeAuswahl ausgabe = konfiguration.getAusgabe(i);
      if (!ausgabe.istBenutzerAuswahl()) {
        ErrorHandler.getInstance().handleError("Ausgabe '"+ausgabe.getTitel()+
            "' ist keine Benutzer-Auswahl!", false);                 
      } else {
        auswahlComboBox.addItem(new TabellenSelektion() {            
          public String toString() {
            return ausgabe.getTitel();
          }

          protected boolean istInSelektion(Object o) {             
            return ausgabe.bewerte((Benutzer) o);              
          }
        });
      }
    }
  }
}