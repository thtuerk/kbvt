package de.oberbrechen.koeb.dateien;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Systematik;
import de.oberbrechen.koeb.datenbankzugriff.SystematikFactory;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenstrukturen.SystematikListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
* Dieses Klasse dient dazu eine XML-Systematik-Datei für
* auszulesen und zu schreiben. 
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class SystematikXMLDatei {
  
  protected SystematikFactory systematikFactory =
    Datenbank.getInstance().getSystematikFactory();
  protected static SystematikXMLDatei instance;
  
  /**
   * Liefert eine Instanz der StatistikXMLDatei
   *
   * @return eine Instanz
   */
  public static SystematikXMLDatei getInstance() {
    if (instance == null) instance = new SystematikXMLDatei();

    return instance;
  }

  /**
   * Importiert alle Systematiken aus der Datei und speichert sie in der Datenbank.
   * @param file die zu importierende Datei
   */
  public void importSystematiken(File file) {
    try {
      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      IXMLReader reader = new StdXMLReader(
           new InputStreamReader(new FileInputStream(file), "UTF-8"));
      parser.setReader(reader);
      IXMLElement xml = (IXMLElement) parser.parse();
      
      Hashtable<String, Systematik> systematikHash = new Hashtable<String, Systematik>();
      for (int i=0; i < xml.getChildrenCount(); i++) {
        saveSystematik(xml.getChildAtIndex(i), systematikHash);
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Systematikdatei " +        "'"+file+"'.", false);
    }   
  }

  /**
   * Versucht die in dem übergebenen Element gespeicherten Informationen
   * in ein Sysematik-Objekt einzulesen und speichert dies.
   * @return Object
   * @throws DatenbankzugriffException
   */
  private Systematik saveSystematik(IXMLElement element, Hashtable<String, Systematik> systematikHash) throws DatenbankzugriffException {
    Systematik erg = systematikFactory.erstelleNeu();
    erg.setName(element.getAttribute("name", null));
    erg.setBeschreibung(element.getAttribute("beschreibung", null));
    
    String obersystematikString = element.getAttribute("obersystematik", null);
    if (obersystematikString != null) {
      erg.setDirekteObersystematik(
        systematikHash.get(obersystematikString));
    }
    erg.save();
    systematikHash.put(erg.getName(), erg);
    
    return erg;
  }
    
  /**
   * Liefert ein XML-Element, dass die Daten der übergebenen Systematik enthält
   * @param systematik die zu codierende Systematik
   * @return den erstellten Knoten
   * @throws DatenbankInkonsistenzException
   */
  private XMLElement getSystematikKnoten(Systematik systematik) throws DatenbankInkonsistenzException {
    XMLElement beschreibungKnoten = new XMLElement("Systematik");
    beschreibungKnoten.setAttribute("name", systematik.getName());
    
    if (systematik.getBeschreibung() != null) {
      beschreibungKnoten.setAttribute("beschreibung", systematik.getBeschreibung());
    }
    if (systematik.getDirekteObersystematik() != null) {
      beschreibungKnoten.setAttribute("obersystematik", 
          systematik.getDirekteObersystematik().getName());
    }
    
    return beschreibungKnoten;
  }
  
  /**
   * Speichert alle Informationen in die Datei   */
  public void exportSystematiken(File file) {
    IXMLElement root = new XMLElement("Systematiken");
    
    SystematikListe daten = systematikFactory.getAlleHauptSystematiken();
    for (Systematik systematik : daten) {
      addSystematikMitUntersystematiken(systematik, root);
    }
    
    try {
      OutputStreamWriter writer =
        new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      new XMLWriter(writer).write(root, true);
      writer.close();
    } catch (IOException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Exportieren der Systematiken", false);
    }    
  }

  private void addSystematikMitUntersystematiken(Systematik systematik, IXMLElement root) {
    try {
      root.addChild(getSystematikKnoten(systematik));
    } catch (DatenbankInkonsistenzException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim " +
          "Exportieren der Systematik "+systematik.getId()+"!", false);
    }
    
    for (Systematik unterSystematik : systematik.getDirekteUntersystematiken()) {
      addSystematikMitUntersystematiken(unterSystematik, root);
    }
  }
}