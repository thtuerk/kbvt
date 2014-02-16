package de.oberbrechen.koeb.gui.veranstaltungen.teilnahmenReiter;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.VeranstaltungenListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.TableRendererModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Ausleihen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class VeranstaltungenTableModel extends AbstractTableModel implements
  TableRendererModel {

  class tabellenZeile {
    int status;
    int gespeicherterStatus = status;
    Date anmeldeDatum;
    Benutzer benutzer;
    Veranstaltung veranstaltung;
    Veranstaltungsteilnahme veranstaltungsteilnahme;
    String bemerkungen;

    public tabellenZeile(Veranstaltung veranstaltung, Benutzer benutzer) throws DatenbankInkonsistenzException {
      veranstaltungsteilnahme = Datenbank.getInstance().
        getVeranstaltungsteilnahmeFactory().
        getVeranstaltungsteilnahme(benutzer, veranstaltung);
      this.veranstaltung = veranstaltung;
      this.benutzer = benutzer;      
      
      if (veranstaltungsteilnahme == null) {
        status = Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET;
        anmeldeDatum = null;
        bemerkungen = null;
      } else {
        status = veranstaltungsteilnahme.getStatus();
        anmeldeDatum = veranstaltungsteilnahme.getAnmeldeDatum();
        bemerkungen = veranstaltungsteilnahme.getBemerkungen();        
      }
      
      gespeicherterStatus = status;
    }

    public void save() throws DatenbankzugriffException {
      if (status != Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET) {
        if (veranstaltungsteilnahme == null) {
          veranstaltungsteilnahme = Datenbank.getInstance().
            getVeranstaltungsteilnahmeFactory().erstelleNeu(benutzer,
            veranstaltung);
          if (anmeldeDatum == null) anmeldeDatum = new Date();
        }
        veranstaltungsteilnahme.setStatus(status);
        veranstaltungsteilnahme.setAnmeldeDatum(anmeldeDatum);
        veranstaltungsteilnahme.setBemerkungen(bemerkungen);
        veranstaltungsteilnahme.save();
      } else {
        if (veranstaltungsteilnahme != null) {
          veranstaltungsteilnahme.loesche();
          veranstaltungsteilnahme = null;
          status = Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET;
        }
      }
      
      gespeicherterStatus = status;
    }
    
    public void setTeilnahme(boolean wert) {
      if (!wert) {
        status = Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET;
      } else {
        if (gespeicherterStatus != Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET) {
          status = gespeicherterStatus;
        } else if ((teilnahmeFactory.getBlockierendeWartelistenLaenge(veranstaltung) > 0) ||
            (veranstaltung.getMaximaleTeilnehmerAnzahl() > 0 &&
             teilnahmeFactory.getTeilnehmerAnzahl(veranstaltung) >= 
             veranstaltung.getMaximaleTeilnehmerAnzahl())) {
          status = Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND;          
        } else {
          status = Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET;                                        
        }
      }
    }
  }
  
  private static DecimalFormat waehrungsFormat = new DecimalFormat("0.00");
  
  private TeilnahmenReiterInterface teilnahmenReiter;

  private tabellenZeile[] daten;
  private VeranstaltungsteilnahmeFactory teilnahmeFactory;
  
  public VeranstaltungenTableModel(TeilnahmenReiterInterface teilnahmenReiter,
    Benutzer benutzer, Veranstaltungsgruppe veranstaltungsgruppe) {
    this.teilnahmenReiter = teilnahmenReiter;
    teilnahmeFactory = Datenbank.getInstance().getVeranstaltungsteilnahmeFactory();
    
    init(benutzer, veranstaltungsgruppe);
  }

  /**
   * Initialisiert das Modell neu für den übergebenen Benutzer und die
   * übergebene Veranstaltungsgruppe
   *
   * @param benutzer der Benutzer
   * @param veranstaltungsgruppe die Veranstaltungsgruppe
   * @throws DatenbankInkonsistenzException
   * @throws DatenbankInkonsistenzException
   */
  public void init(Benutzer benutzer,Veranstaltungsgruppe veranstaltungsgruppe) {
    try {
      if (veranstaltungsgruppe == null) {
        daten = null;
      } else {
        VeranstaltungenListe veranstaltungenListe =
          Datenbank.getInstance().getVeranstaltungFactory().
            getVeranstaltungenMitAnmeldung(veranstaltungsgruppe);
        veranstaltungenListe.setSortierung(VeranstaltungenListe.AlphabetischeSortierung);
        daten = new tabellenZeile[veranstaltungenListe.size()];
        Iterator<Veranstaltung> it = veranstaltungenListe.iterator();
  
        int pos = 0;
        while (it.hasNext()) {
          daten[pos] = new tabellenZeile(it.next(), benutzer);
          pos++;
        }
      }
      fireTableDataChanged();
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      daten = null;
      fireTableDataChanged();
    }
  }

  //Methoden für das TableModel, Doku siehe bitte dort
  public int getRowCount() {
    if (daten == null) return 0;
    return daten.length;
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return (columnIndex == 0 || (columnIndex == 5 &&
      (daten[rowIndex].status != Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET)));
  }

  public int getColumnCount() {
    return 6;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return " ";
    if (columnIndex == 1) return "Veranstaltung";
    if (columnIndex == 2) return "Teilnehmeranzahl";
    if (columnIndex == 3) return "Bezugsgruppe";
    if (columnIndex == 4) return "Kosten";
    if (columnIndex == 5) return "Bemerkungen";
    return "nicht definierte Spalte";
  }

  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) return Boolean.class;
    return String.class;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.length) return "ERROR";
    tabellenZeile gewaehlteZeile = daten[rowIndex];
    if (columnIndex == 0) 
      return new Boolean(daten[rowIndex].status != Veranstaltungsteilnahme.ANMELDESTATUS_NICHT_ANGEMELDET);
    if (columnIndex == 1) return gewaehlteZeile.veranstaltung.getTitel();
    if (columnIndex == 2) {
      int teilnehmerAnzahl = 
        teilnahmeFactory.getTeilnehmerAnzahl(gewaehlteZeile.veranstaltung);
      int warteListenLaenge = 
        teilnahmeFactory.getWartelistenLaenge(gewaehlteZeile.veranstaltung);
      int maxAnzahl = gewaehlteZeile.veranstaltung.getMaximaleTeilnehmerAnzahl();
      
      String result = Integer.toString(teilnehmerAnzahl);
      if (warteListenLaenge > 0) result += "+"+Integer.toString(warteListenLaenge);
      if (maxAnzahl > 0) result += "/"+Integer.toString(maxAnzahl);
      return result;
    }
    if (columnIndex == 3) return gewaehlteZeile.veranstaltung.getBezugsgruppe();
    if (columnIndex == 4) return gewaehlteZeile.veranstaltung.getKostenFormatiert();
    if (columnIndex == 5) return gewaehlteZeile.bemerkungen;
    return "nicht definierte Spalte";
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= daten.length) return;
    tabellenZeile gewaehlteZeile = daten[rowIndex];

    if (columnIndex == 0) {      
      gewaehlteZeile.setTeilnahme(((Boolean) aValue).booleanValue());
      teilnahmenReiter.setzeKosten(this.berechneKosten());
      fireTableRowsUpdated(rowIndex, rowIndex);
    }
    if (columnIndex == 5) {
      gewaehlteZeile.bemerkungen = (String) aValue;
    }
    fireTableCellUpdated(rowIndex, columnIndex);
  }

  /**
   * Liefert die Veranstaltung, die in der übergebenen Zeile des Models
   * dargestellt wird
   */
  public Veranstaltung getVeranstaltung(int rowIndex) {
    if (rowIndex < 0 || rowIndex >= daten.length) return null;
    return daten[rowIndex].veranstaltung;
  }

  public void save() {
    if (daten == null) return;
    for (int i = 0; i < daten.length; i++) {
      try {
        daten[i].save();
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern " +
            "der Veranstaltungsteilnahmen!", false);
      }
    }
  }

  public String berechneKosten() {
    if (daten == null || daten.length == 0) return "-";

    String waehrung = null;
    double kosten = 0;
    for (int i = 0; i < daten.length; i++) {
      if (daten[i] != null && daten[i].status == Veranstaltungsteilnahme.ANMELDESTATUS_ANGEMELDET &&
          daten[i].veranstaltung.getKosten() != 0) {
        kosten += daten[i].veranstaltung.getKosten();
        if (waehrung == null)
          waehrung = daten[i].veranstaltung.getWaehrung();
        else
          if (!waehrung.equals(daten[i].veranstaltung.getWaehrung()))
            return "???";
      }
    }

    if (kosten == 0) {
      return "-";
    } else {
      return waehrungsFormat.format(kosten)+" "+waehrung;
    }
  }

  public void fireTableDataChanged() {
    teilnahmenReiter.setzeKosten(this.berechneKosten());
    super.fireTableDataChanged();
  }

  public int getColor(int rowIndex, int columnIndex) {
    Veranstaltung veranstaltung = getVeranstaltung(rowIndex);
    boolean istBelegt = teilnahmeFactory.istBelegt(veranstaltung);
    boolean warteListeBelegt = teilnahmeFactory.getBlockierendeWartelistenLaenge(veranstaltung) > 0; 
    
    if (!istBelegt && !warteListeBelegt) {      
      return FARBE_STANDARD;
    } else if (istBelegt) {
      return FARBE_ROT;
    } else {
      return FARBE_BLAU;
    }
  }

  public int getFont(int rowIndex, int columnIndex) {
    if (daten[rowIndex].status == Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_BLOCKIEREND || 
        daten[rowIndex].status == Veranstaltungsteilnahme.ANMELDESTATUS_AUF_WARTE_LISTE_NICHT_BLOCKIEREND) 
      return SCHRIFT_KURSIV;
    else
      return SCHRIFT_STANDARD;
  }

  public int getAlignment(int rowIndex, int columnIndex) {   
    switch (columnIndex) {
      case 1:
      case 3:
      case 5:
        return AUSRICHTUNG_LINKS;
      case 4:
        return AUSRICHTUNG_RECHTS;
      default:
        return AUSRICHTUNG_ZENTRIERT;    
    }
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return 20;
      case 1: 
        return 250;
      case 2: 
        return 90;
      case 4: 
        return 120;
      default: 
        return 180;
    }
  }

  public boolean zeigeRahmen() {
    return true;
  }

  public boolean zeigeSortierung() {
    return false;
  }
}
