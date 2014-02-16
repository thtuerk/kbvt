package de.oberbrechen.koeb.dateien.veranstaltungsgruppeAusgabenKonfiguration;

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
* die VeranstaltungsgruppeAusgaben auszulesen und zu schreiben. 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/
public class VeranstaltungsgruppeAusgabeKonfigurationXMLDatei {

  private String file;

  private static String standardDatei = null; 
  private static VeranstaltungsgruppeAusgabeKonfigurationXMLDatei instance;

  private Vector<VeranstaltungsgruppeAusgabeFactoryBeschreibung> daten;
  
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
  public static VeranstaltungsgruppeAusgabeKonfigurationXMLDatei getInstance() {
    if (standardDatei == null) {
      EinstellungFactory einstellungFactory =
        Datenbank.getInstance().getEinstellungFactory();
      standardDatei = einstellungFactory.getClientEinstellung(
        "de.oberbrechen.koeb.dateien.veranstaltungsgruppeAusgabenKonfiguration.VeranstaltungsgruppeAusgabeKonfigurationXMLDatei",
        "Standarddatei").getWert("einstellungen/VeranstaltungsgruppeAusgabeKonfiguration.xml");
    }

    if (instance == null) instance = new VeranstaltungsgruppeAusgabeKonfigurationXMLDatei(standardDatei);

    return instance;
  }

  /**
   * Erstellt ein neues MedienAusgabeKonfigurationXMLDatei-Objekt, das seine Informationen
   * in der übergebenen Datei speichert. Die zur Zeit in dieser Datei
   * gespeicherten Informationen werden geladen.
   *
   * @param datei die Datei, in der die Informationen gespeichert werden
   */
  public VeranstaltungsgruppeAusgabeKonfigurationXMLDatei(String datei) {
    this.file = datei;
    load();
  }

  /**
   * Läd alle Informationen aus der Datei.
   */
  private void load() {
    daten = new Vector<VeranstaltungsgruppeAusgabeFactoryBeschreibung>();

    try {
      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      IXMLReader reader = new StdXMLReader(
           new InputStreamReader(new FileInputStream(file), "UTF-8"));
      parser.setReader(reader);
      IXMLElement xml = (IXMLElement) parser.parse();
      
      for (int i=0; i < xml.getChildrenCount(); i++) {
        daten.add(getVeranstaltungsgruppeAusgabeFactoryBeschreibung(xml.getChildAtIndex(i)));
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Konfigurationsdatei " +        "'"+file+"'.", false);
    }   
  }

  /**
   * Versucht die in dem übergebenen Element gespeicherten Informationen
   * in ein VeranstaltungsgruppeAusgabeFactoryBeschreibung-Objekt einzulesen.
   * @return Object
   */
  private VeranstaltungsgruppeAusgabeFactoryBeschreibung getVeranstaltungsgruppeAusgabeFactoryBeschreibung(IXMLElement element) {
    VeranstaltungsgruppeAusgabeFactoryBeschreibung erg = new VeranstaltungsgruppeAusgabeFactoryBeschreibung();
    erg.setKlasse(element.getAttribute("class", null));
    erg.setName(element.getAttribute("name", null));
    
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

  public VeranstaltungsgruppeAusgabeFactoryBeschreibung[] getDaten() {
    return daten.toArray(new VeranstaltungsgruppeAusgabeFactoryBeschreibung[daten.size()]);  
  }
  
  public void setDaten(VeranstaltungsgruppeAusgabeFactoryBeschreibung[] daten) {
    Vector<VeranstaltungsgruppeAusgabeFactoryBeschreibung> neueDaten = new Vector<VeranstaltungsgruppeAusgabeFactoryBeschreibung>();
    for (int i=0; i < daten.length; i++) {
      neueDaten.add(daten[i]);  
    }
    this.daten = neueDaten;
    save();
  }
  
  /**
   * Speichert alle Informationen in die Datei   */
  private void save() {
    IXMLElement root = new XMLElement("VeranstaltungsgruppeAusgabeKonfiguration");    
    Iterator<VeranstaltungsgruppeAusgabeFactoryBeschreibung> it = daten.iterator();
    while (it.hasNext()) {
      VeranstaltungsgruppeAusgabeFactoryBeschreibung beschreibung = 
        it.next();
      
      XMLElement beschreibungKnoten = new XMLElement("VeranstaltungsgruppeAusgabeFactory");
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