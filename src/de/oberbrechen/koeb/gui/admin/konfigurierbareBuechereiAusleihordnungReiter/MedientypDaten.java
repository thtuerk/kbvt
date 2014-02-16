/**
 * Der Datentyp für das Tablemodell
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
package de.oberbrechen.koeb.gui.admin.konfigurierbareBuechereiAusleihordnungReiter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.*;
import de.oberbrechen.koeb.framework.ErrorHandler;


class MedientypDaten {
  Medientyp medientyp;
  Einstellung mindestAusleihDauerEinstellung;
  Einstellung medienNrPraefixEinstellung;
  Einstellung langesMedienNrFormatEinstellung;
  int mediennr;
  
  public MedientypDaten(Medientyp medientyp) throws DatenNichtGefundenException, DatenbankInkonsistenzException {
    MedientypFactory medientypFactory =
      Datenbank.getInstance().getMedientypFactory();
    this.medientyp = (Medientyp) medientypFactory.ladeAusDatenbank(
      medientyp.getId());
    mediennr = (int) (Math.random()*200);  
    
    initEinstellungen();       
  }
  
  private void initEinstellungen() {
    EinstellungFactory einstellungFactory = 
      Datenbank.getInstance().getEinstellungFactory();

    mindestAusleihDauerEinstellung = 
      einstellungFactory.getEinstellung(
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareAusleihordnung", 
      "mindestAusleihdauerInTagen."+getMedientyp());
    
    medienNrPraefixEinstellung = 
      einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "MediennrPraefix."+getMedientyp());

    langesMedienNrFormatEinstellung = 
      einstellungFactory.getEinstellung( 
      "de.oberbrechen.koeb.einstellungen.KonfigurierbareBuecherei", 
      "EinstellungsjahrInMediennr."+getMedientyp());      
  }
  
  public boolean isLangesMedienNrFormat() {
    return langesMedienNrFormatEinstellung.getWertBoolean(true);
  }

  public String getMedienNrPraefix() {
    return medienNrPraefixEinstellung.getWert(getMedientyp());
  }

  public String getMedientyp() {
    return medientyp.getName();
  }

  public int getMindestAusleihDauer() {
    return mindestAusleihDauerEinstellung.getWertInt(21);
  }

  public String getPlural() {
    return medientyp.getPlural();
  }

  public void setLangesMedienNrFormat(boolean b) {
    try {
      langesMedienNrFormatEinstellung.setWertBoolean(b);
      langesMedienNrFormatEinstellung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Mediennr.-Format konnte "+
          "nicht gespeichert werden!", false);
    }
  }

  public void setMedienNrPraefix(String string) {      
    try {
      medienNrPraefixEinstellung.setWert(string);
      medienNrPraefixEinstellung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Mediennr.-Präfix konnte "+
          "nicht gespeichert werden!", false);
    }
  }

  public void setMedientyp(String string, JFrame hauptFenster) {
    int oldMedientypId = medientyp.getId();
    try {
      medientyp.setName(string);
      medientyp.save();

      //Einstellungen anpassen
      int mindestAusleihdauer = mindestAusleihDauerEinstellung.getWertInt(21);
      String medienNrPraefix = medienNrPraefixEinstellung.getWert("01");
      boolean langesMedienNrFormat = 
        langesMedienNrFormatEinstellung.getWertBoolean(true);

      mindestAusleihDauerEinstellung.loesche();
      medienNrPraefixEinstellung.loesche();
      langesMedienNrFormatEinstellung.loesche();
      
      initEinstellungen();
      
      mindestAusleihDauerEinstellung.setWertInt(mindestAusleihdauer);
      mindestAusleihDauerEinstellung.save();
      medienNrPraefixEinstellung.setWert(medienNrPraefix);
      medienNrPraefixEinstellung.save();
      langesMedienNrFormatEinstellung.setWertBoolean(langesMedienNrFormat);
      langesMedienNrFormatEinstellung.save();
    } catch (MedientypSchonVergebenException e) {
      try {
        JOptionPane.showMessageDialog(hauptFenster, "Der Medientyp '" +
        string+"' existiert bereits!",
        "Ungültige Eingabe!", JOptionPane.ERROR_MESSAGE);
      
        medientyp = (Medientyp) 
          Datenbank.getInstance().getMedientypFactory().
          ladeAusDatenbank(oldMedientypId);        
      } catch (DatenbankzugriffException e2) {
        ErrorHandler.getInstance().handleException(e2, true);
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern des Medientyps "+medientyp.toDebugString()+"!", false);        
    }
  }

  public void setPlural(String string) {
    try {
      medientyp.setPlural(string);
      medientyp.save();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Speichern des Medientyps "+medientyp.toDebugString()+"!", false);        
    }
  }

  public void setMindestAusleihDauer(int i) {
    try {
      mindestAusleihDauerEinstellung.setWertInt(i);
      mindestAusleihDauerEinstellung.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Mindestausleihdauer konnte "+
          "nicht gespeichert werden!", false);
    }     
  }

  public String getBeispiel() {
    StringBuffer buffer = new StringBuffer();
    if (medienNrPraefixEinstellung.getWert() != null && 
      !medienNrPraefixEinstellung.getWert().trim().equals("") )
      buffer.append(medienNrPraefixEinstellung.getWert()).append(" ");
    if (isLangesMedienNrFormat()) buffer.append("2003-");
    buffer.append(mediennr);
    return buffer.toString();
  }
}