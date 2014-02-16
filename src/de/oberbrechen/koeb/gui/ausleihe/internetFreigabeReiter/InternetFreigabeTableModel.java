package de.oberbrechen.koeb.gui.ausleihe.internetFreigabeReiter;

import java.text.DecimalFormat;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.ClientListe;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.components.jTableTools.AbstractListeTableModel;

/**
* Diese Klasse ist ein Tabellenmodell für eine Tabelle von Ausleihen.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class InternetFreigabeTableModel extends AbstractListeTableModel<Client> {

  private static DecimalFormat zahlenFormat = new DecimalFormat("00");
  private InternetfreigabeFactory internetfreigabeFactory;

  /**
   * Erstellt ein neues Modell
   */
  public InternetFreigabeTableModel() {
    internetfreigabeFactory = 
      Datenbank.getInstance().getInternetfreigabeFactory();
    ClientFactory clientFactory =
      Datenbank.getInstance().getClientFactory();
    ClientListe clients = clientFactory.getAlleClientsMitInternetzugang();
    clients.setSortierung(ClientListe.NameSortierung);
    initDaten(clients);
  }

  public int getColumnCount() {
    return 4;
  }

  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) return "Rechner";
    if (columnIndex == 1) return "Benutzer";
    if (columnIndex == 2) return "Dauer";
    if (columnIndex == 3) return "Kosten";
    return "nicht definierte Spalte";
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Client gewaehlterClient = (Client) daten.get(rowIndex);
    Internetfreigabe gewaehlteInternetfreigabe = null;
    try {
      gewaehlteInternetfreigabe = internetfreigabeFactory.getAktuelleInternetfreigabe(
      gewaehlterClient);
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      if (columnIndex > 0) return "FEHLER!";
    }
    
    switch (columnIndex) {
      case 0:
        return gewaehlterClient.getName();
      case 1:
        if (gewaehlteInternetfreigabe == null ||
            !gewaehlteInternetfreigabe.istAktuell()) return "-";
        return gewaehlteInternetfreigabe.getBenutzer().getName();
      case 2:
        if (gewaehlteInternetfreigabe == null ||
            !gewaehlteInternetfreigabe.istAktuell()) return "-";
        int restDauerInSekunden = gewaehlteInternetfreigabe.getDauer();

        int anzahlStunden = restDauerInSekunden / 3600;
        restDauerInSekunden = restDauerInSekunden % 3600;
        int anzahlMinuten = restDauerInSekunden / 60;
        restDauerInSekunden = restDauerInSekunden % 60;

        return zahlenFormat.format(anzahlStunden)+":"+
               zahlenFormat.format(anzahlMinuten)+":"+
               zahlenFormat.format(restDauerInSekunden);
      case 3:
        if (gewaehlteInternetfreigabe == null ||
            !gewaehlteInternetfreigabe.istAktuell()) return "-";
        return formatKosten(Buecherei.getInstance().
          berechneInternetzugangsKosten(gewaehlteInternetfreigabe.getDauer()));
    }
    return "nicht definierte Spalte";
  }

  public int getColor(int rowIndex, int columnIndex) {
    Client client = (Client) get(rowIndex);
    Internetfreigabe freigabe;
    try {
      freigabe = internetfreigabeFactory.getAktuelleInternetfreigabe(client);
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, false);
      return FARBE_STANDARD;
    }
    
    
    if (freigabe == null || freigabe.istFreigegeben()) {
      return FARBE_GRUEN;
    } else if (freigabe.istAktuell()) {
      return FARBE_ROT;
    } else {
      return FARBE_STANDARD;
    }
  }

  public int getAlignment(int rowIndex, int columnIndex) {
    switch (columnIndex) {
      case 0:
      case 1:
        return AUSRICHTUNG_LINKS;
      case 2:
      case 3:
        return AUSRICHTUNG_RECHTS;        
    }
    return AUSRICHTUNG_LINKS;    
  }

  public int getDefaultColumnWidth(int columnIndex) {
    switch (columnIndex) {
      case 0:
      case 1:
        return 300;
      case 2:
      case 3:
        return 100;        
    }
    return 100;
  }
}
