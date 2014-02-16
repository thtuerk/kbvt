package de.oberbrechen.koeb.erstelleBestandHTML;

import java.util.Vector;

import net.n3.nanoxml.*;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class XMLDatei {

  Vector daten;

  public XMLDatei(String datei) {
    load(datei);
  }

  /**
   * L�d alle Informationen aus der Datei.
   */
  private void load(String file) {
    daten = new Vector();

    try {
      IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
      IXMLReader reader = StdXMLReader.fileReader(file);
      parser.setReader(reader);
      IXMLElement xml = (IXMLElement) parser.parse();
      
      for (int i=0; i < xml.getChildrenCount(); i++) {
        daten.add(getListe(xml.getChildAtIndex(i)));
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Laden der Konfigurationsdatei " + //$NON-NLS-1$        "'"+file+"'.", false); //$NON-NLS-1$ //$NON-NLS-2$
    }   
  }

  private Liste getListe(IXMLElement element) {
    Liste erg = new Liste(element.getAttribute("Titel", null), element.getAttribute("FileName", null)); //$NON-NLS-1$ //$NON-NLS-2$
    
    for (int i=0; i < element.getChildrenCount(); i++) {
      IXMLElement child = element.getChildAtIndex(i);
      if (child.getName().equalsIgnoreCase("Link")) { //$NON-NLS-1$
        erg.addLink(child.getAttribute("Name", null), child.getAttribute("to",null)); //$NON-NLS-1$ //$NON-NLS-2$
      } else if (child.getName().equalsIgnoreCase("Eintrag")) { //$NON-NLS-1$
        erg.addEintrag(getEintrag(child));
      } else {
        throw new RuntimeException("Unerwarteter Typ "+child.getName()); //$NON-NLS-1$
      }
    }
    
    return erg;
  }

  private Eintrag getEintrag(IXMLElement element) {
    String titel = element.getAttribute("Titel", null); //$NON-NLS-1$
    Suchkriterium kriterium = getSuchkriterium(element.getChildAtIndex(0));
    Eintrag erg = new Eintrag(titel, kriterium);
    
    for (int i=1; i < element.getChildrenCount(); i++) {
      IXMLElement child = element.getChildAtIndex(i);
      erg.addEintrag(getEintrag(child));
    }
    
    return erg;
  }
 
  private Suchkriterium getSuchkriterium(IXMLElement element) {
    Suchkriterium erg = new Suchkriterium();
    
    for (int i=0; i < element.getChildrenCount(); i++) {
      IXMLElement child = element.getChildAtIndex(i);
      if (child.getName().equalsIgnoreCase("Medientyp")) { //$NON-NLS-1$
        erg.setMedientyp(child.getContent());      
      } else if (child.getName().equalsIgnoreCase("Systematik")) { //$NON-NLS-1$
        erg.addSystematik(child.getContent());
      } else if (child.getName().equalsIgnoreCase("EinstellungVorMaxTagen")) { //$NON-NLS-1$
        erg.setEinstellungsdauer(child.getContent());
      } else {                        
        throw new RuntimeException("Unerwarteter Typ "+child.getName()); //$NON-NLS-1$
      }
    }
    
    return erg;
  }  
    
  public Liste[] getDaten() {
    return (Liste[]) daten.toArray(new Liste[daten.size()]);  
  }
}