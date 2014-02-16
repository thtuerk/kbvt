package de.oberbrechen.koeb.dateien.einstellungenDoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.Vector;

import net.n3.nanoxml.*;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Dieses Klasse dient dazu eine XML-Beschreibung der Einstellungen auszulesen. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class EinstellungenDokuXMLDatei {

  private static EinstellungenDokuXMLDatei instance = null; 
  private static String standardDir = null; 

  private Vector<EinstellungBeschreibung> daten;
  private Vector<String> versteckteEinstellungen;
    
  /**
   * Liefert die EinstellungenDokuXMLDatei, die alle Dateien, die
   * sich im Verzeichnis die vorher mittels
   * setStandardDatei(File datei) eingestellt wurde.
   *
   * @return die Standardkonfigurationsdatei
   */
  public static EinstellungenDokuXMLDatei getInstance() {
    if (standardDir == null) {
      EinstellungFactory einstellungFactory =
      Datenbank.getInstance().getEinstellungFactory();
      standardDir = einstellungFactory.getEinstellung(
        "de.oberbrechen.koeb.gui.ausgaben.einstellungenDoku",
        "StandardDir").getWert("einstellungen/Einstellungen-Doku");
    }

    if (instance == null) instance = new EinstellungenDokuXMLDatei(standardDir);

    return instance;
  }

  /**
   * Erstellt ein neues  EinstellungenDokuXMLDatei-Objekt, das die Dateien
   * im übergebenen Verzeichnis ausliest.
   */
  public EinstellungenDokuXMLDatei(String dir) {
    this.daten = new Vector<EinstellungBeschreibung>();
    this.versteckteEinstellungen = new Vector<String>();
      
    loadDir(dir);    
  }

  /**
   * Läd alle Informationen und fügt sie den Vectoren Daten
   * und versteckteEinstellungen hinzu.
   */
  private void loadDir(String dir) {
    File dirFile = new File(dir);
    File[] inputFiles = dirFile.listFiles(new FilenameFilter() {
      public boolean accept(File arg0, String arg1) {
        return arg1.endsWith(".xml") || 
          arg1.endsWith(".XML");
      }});    
    
    for (int i=0; i < inputFiles.length; i++)
      load(inputFiles[i]);
  }
  
  /**
   * Läd alle Informationen aus der übergebenen Datei und fügt
   * sie den Vectoren daten und versteckteEinstellungen hinzu.
   */
  private void load(File file) {
    try {
      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      IXMLReader reader = new StdXMLReader(
           new InputStreamReader(new FileInputStream(file), "UTF-8"));
      parser.setReader(reader);
      IXMLElement xml = (IXMLElement) parser.parse();
      
      if (xml.getChildrenCount() > 0) {
        addVersteckteEinstellungen(xml.getChildAtIndex(0));
      }
      
      for (int i=1; i < xml.getChildrenCount(); i++) {
        addEinstellungenGruppe(xml.getChildAtIndex(i));
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Konfigurationsdatei " +        "'"+file+"'.", false);
    }   
  }

  private void addVersteckteEinstellungen(IXMLElement element) {
    for (int i=0; i < element.getChildrenCount(); i++) {
      IXMLElement einstellung = element.getChildAtIndex(i);
      versteckteEinstellungen.add(einstellung.getContent());
    }
  }

  private void addEinstellungenGruppe(IXMLElement element) {
    String gruppenName = element.getAttribute("name", null);
    if (gruppenName == null || gruppenName.trim().length() == 0) {
      ErrorHandler.getInstance().handleError("Ungültiger Gruppename!", false);
      gruppenName = "ungültiger Name";
    }
    
    EinstellungTest gruppenTest = createEinstellungTest(
        element.getAttribute("testClass", null));

    for (int i=0; i < element.getChildrenCount(); i++) {
      addEinstellung(element.getChildAtIndex(i), gruppenName, gruppenTest);
    }
  }

  private EinstellungTest createEinstellungTest(String klassenname) {
    if (klassenname == null || klassenname.trim().length() == 0) return null;
    
    try { 
      return (EinstellungTest) Class.forName(klassenname).newInstance();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, false);
      return null;
    }
  }

  private String formatContent(String content) {
    StringBuffer buffer = new StringBuffer();
    
    String trimContent = content.trim();
    
    int leerzeichenCount = 0;
    for (int i=0; i < trimContent.length(); i++) {
      char currentChar = trimContent.charAt(i);
      
      if (Character.isWhitespace(currentChar)) {
        leerzeichenCount++;
        currentChar = ' ';
      } else {
        leerzeichenCount = 0;
      }
      
      if (leerzeichenCount < 2) buffer.append(currentChar);
    }
        
    return buffer.toString();
  }
  
  private void addEinstellung(IXMLElement element, String gruppenName, EinstellungTest gruppenTest) {
    EinstellungBeschreibung beschreibung = new EinstellungBeschreibung();

    beschreibung.setGruppe(gruppenName);
    beschreibung.setName(element.getAttribute("name", null));    
    beschreibung.setTyp(element.getAttribute("typ", null));    
    beschreibung.setClient(!element.getAttribute("client", "NO").equals("NO"));
    beschreibung.setMitarbeiter(!element.getAttribute("mitarbeiter", "NO").equals("NO"));
    beschreibung.setTest(createEinstellungTest(element.getAttribute("testClass", null)));
    if (beschreibung.getTest() == null) beschreibung.setTest(gruppenTest);
            
    beschreibung.setStandardWert(element.getChildAtIndex(0).getContent());    
    for (int i=1; i < element.getChildrenCount()-1; i++) {
      beschreibung.addWert(element.getChildAtIndex(i).getContent());
    }
    beschreibung.setBeschreibung(formatContent(element.getChildAtIndex(element.getChildrenCount()-1).getContent()));
    
    daten.add(beschreibung);
  }
    
  public EinstellungBeschreibung[] getDaten() {
    return daten.toArray(new EinstellungBeschreibung[daten.size()]);  
  }
  
  public String[] getVersteckteEinstellungen() {
    return versteckteEinstellungen.toArray(new String[versteckteEinstellungen.size()]);  
  }  
}