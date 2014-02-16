package de.oberbrechen.koeb.erstelleBestandHTML;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 * Diese Klasse repr�sentiert eine Datei, die die Suchkriterien enth�lt.
 * 
 * @author Thomas T�rk (t_tuerk@gmx.de)
 */
public class SuchkriteriumFile {

  Vector anfragen = new Vector();
  Vector titel = new Vector();
  
  public int addKriterium(String titel, Suchkriterium kriterium) {
    this.titel.add(titel);
    this.anfragen.add(kriterium.getAnfrage());
    return this.titel.size();
  }
  
  private void addTitel(StringBuffer buffer) {
    buffer.append("function get_MedienListe($Nr) {\n"); //$NON-NLS-1$
    buffer.append("   switch($Nr) {\n"); //$NON-NLS-1$
    
    for (int i=0; i < anfragen.size();i++) {
      buffer.append("      case ").append(i+1).append(":\n"); //$NON-NLS-1$ //$NON-NLS-2$
      buffer.append("         $Anfrage = \"").append(anfragen.get(i)).append("\";\n"); //$NON-NLS-1$ //$NON-NLS-2$
      buffer.append("         break;\n"); //$NON-NLS-1$
    }
    
    buffer.append("   }\n"); //$NON-NLS-1$
    buffer.append("   return DB_query($Anfrage);\n"); //$NON-NLS-1$
    buffer.append("}\n\n"); //$NON-NLS-1$
  }
  
  private void addAnfragen(StringBuffer buffer) {
    buffer.append("function get_MedienListeTitel($Nr) {\n"); //$NON-NLS-1$
    buffer.append("   switch($Nr) {\n"); //$NON-NLS-1$
    
    for (int i=0; i < titel.size();i++) {
      buffer.append("      case ").append(i+1).append(": "); //$NON-NLS-1$ //$NON-NLS-2$
      buffer.append("return \"").append(titel.get(i)).append("\";\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    buffer.append("   }\n"); //$NON-NLS-1$
    buffer.append("}\n\n");     //$NON-NLS-1$
  }
  
  public void writeToFile(String dir) throws IOException {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?\n");     //$NON-NLS-1$
    addTitel(buffer);
    addAnfragen(buffer);
    buffer.append("?>\n"); //$NON-NLS-1$
    
    FileWriter writer = new FileWriter(dir+"medienlistenanfragen.inc"); //$NON-NLS-1$
    writer.write(buffer.toString());
    writer.close();    
  }
}
