package de.oberbrechen.koeb.erstelleBestandHTML;

import de.oberbrechen.koeb.erstelleBestandHTML.XMLDatei;


/**
 * Dummys  -Klasse für Tests.
 * 
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class Main {
  
  public static void main(String[] args) throws Exception {
    XMLDatei datei = new XMLDatei(args[0]);
    String dir = args[1];
    SuchkriteriumFile suchkriteriumFile = new SuchkriteriumFile();
    
    Liste[] daten = datei.getDaten();
    for (int i=0; i < daten.length; i++)
      daten[i].writeToFile(dir, suchkriteriumFile);
    
    suchkriteriumFile.writeToFile(dir);
  }
}
