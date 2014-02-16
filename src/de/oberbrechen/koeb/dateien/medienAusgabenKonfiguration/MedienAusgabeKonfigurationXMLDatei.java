package de.oberbrechen.koeb.dateien.medienAusgabenKonfiguration;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import net.n3.nanoxml.*;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
* Dieses Klasse dient dazu eine XML-Konfigurationsdatei für
* die MedienAusgaben auszulesen und zu schreiben. 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class MedienAusgabeKonfigurationXMLDatei {

  private String file;

  private static String standardDatei = null; 
  private static MedienAusgabeKonfigurationXMLDatei instance;

  private Vector<MedienAusgabeFactoryBeschreibung> daten;
  
  /**
   * Setzt die Standard-Konfigurationsdatei
   * @param datei die neuen Standard-Konfigurationsdatei
   */
  public static void setStandardDatei(String datei) {
    standardDatei = datei;
    instance = null;
  }

  /**
   * Liefert die Standardkonfigurationsdatei, die vorher mittels
   * setStandardDatei(File datei) eingestellt wurde.
   *
   * @return die Standardkonfigurationsdatei
   */
  public static MedienAusgabeKonfigurationXMLDatei getInstance() {
    if (standardDatei == null) {
      EinstellungFactory einstellungFactory =
        Datenbank.getInstance().getEinstellungFactory();
      standardDatei = einstellungFactory.getClientEinstellung(
        "de.oberbrechen.koeb.dateien.medienAusgabenKonfiguration.MedienAusgabeKonfigurationXMLDatei",
        "Standarddatei").getWert("einstellungen/MedienAusgabeKonfiguration.xml");
    }

    if (instance == null) instance = new MedienAusgabeKonfigurationXMLDatei(standardDatei);

    return instance;
  }

  /**
   * Erstellt ein neues MedienAusgabeKonfigurationXMLDatei-Objekt, das seine Informationen
   * in der übergebenen Datei speichert. Die zur Zeit in dieser Datei
   * gespeicherten Informationen werden geladen.
   *
   * @param datei die Datei, in der die Informationen gespeichert werden
   */
  public MedienAusgabeKonfigurationXMLDatei(String datei) {
    this.file = datei;
    load();
  }

  /**
   * Läd alle Informationen aus der Datei.
   */
  private void load() {
    daten = new Vector<MedienAusgabeFactoryBeschreibung>();

    try {
      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      IXMLReader reader = new StdXMLReader(
           new InputStreamReader(new FileInputStream(file), "UTF-8"));
      parser.setReader(reader);
      IXMLElement xml = (IXMLElement) parser.parse();
      
      for (int i=0; i < xml.getChildrenCount(); i++) {
        daten.add(getMedienAusgabeFactoryBeschreibung(xml.getChildAtIndex(i)));
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Konfigurationsdatei " +        "'"+file+"'.", false);
    }   
  }

  /**
   * Versucht die in dem übergebenen Element gespeicherten Informationen
   * in ein MedienAusgabeFactoryBeschreibung-Objekt einzulesen.
   * @return Object
   */
  private MedienAusgabeFactoryBeschreibung getMedienAusgabeFactoryBeschreibung(IXMLElement element) {
    MedienAusgabeFactoryBeschreibung erg = new MedienAusgabeFactoryBeschreibung();
    erg.setKlasse(element.getAttribute("class", null));
    
    Vector<Parameter> parameter = erg.getParameter();
    for (int i=0; i < element.getChildrenCount(); i++) {
      IXMLElement parameterElement = element.getChildAtIndex(i);
      Parameter newParameter = new Parameter();
      newParameter.name = parameterElement.getAttribute("name", null);
      newParameter.wert = parameterElement.getContent();
      parameter.add(newParameter);
    }
    
    return erg;
  }

  public MedienAusgabeFactoryBeschreibung[] getDaten() {
    return daten.toArray(new MedienAusgabeFactoryBeschreibung[daten.size()]);  
  }
  
  public void setDaten(MedienAusgabeFactoryBeschreibung[] daten) {
    Vector<MedienAusgabeFactoryBeschreibung> neueDaten = new Vector<MedienAusgabeFactoryBeschreibung>();
    for (int i=0; i < daten.length; i++) {
      neueDaten.add(daten[i]);  
    }
    this.daten = neueDaten;
    save();
  }
  
  /**
   * Speichert alle Informationen in die Datei   */
  private void save() {
    IXMLElement root = new XMLElement("MedienAusgabeKonfiguration");    
    Iterator<MedienAusgabeFactoryBeschreibung> it = daten.iterator();
    while (it.hasNext()) {
      MedienAusgabeFactoryBeschreibung beschreibung = 
        it.next();
      
      XMLElement beschreibungKnoten = new XMLElement("MedienAusgabeFactory");
      beschreibungKnoten.setAttribute("class", beschreibung.getKlasse());
      
      Iterator<Parameter> paramIt = beschreibung.getParameter().iterator();
      while (paramIt.hasNext()) {
        Parameter param = paramIt.next();
        XMLElement parameterKnoten = new XMLElement("Parameter");
        parameterKnoten.setAttribute("name", param.name);
        parameterKnoten.setContent(param.wert);
        beschreibungKnoten.addChild(parameterKnoten);
      }
      root.addChild(beschreibungKnoten);
    }
    
    try {
      FileWriter fileWriter = new FileWriter(file);
      new XMLWriter(fileWriter).write(root, true);
      fileWriter.close();
    } catch (IOException e) {
    }    
  }
}