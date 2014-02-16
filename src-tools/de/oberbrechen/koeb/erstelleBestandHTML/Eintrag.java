package de.oberbrechen.koeb.erstelleBestandHTML;

import java.util.Vector;


/**
 * Diese Klasse repr�sentiert eine Eintrag der XML-Datei
 * 
 * @author Thomas T�rk (t_tuerk@gmx.de)
 */
public class Eintrag {
  
  String titel;
  Suchkriterium suchkriterium;
  
  Vector eintraege;
  
  public Eintrag(String titel, Suchkriterium suchkriterium) {
    this.titel = titel;
    eintraege = new Vector();
    this.suchkriterium = suchkriterium;
  }
    
  public void addEintrag(Eintrag eintrag) {
    eintraege.add(eintrag);
  }
  
  public boolean besitztUntereintraege() {
    return eintraege.size() > 0;
  }
  
  private void appendIndentation(StringBuffer buffer, int level) {
    for (int i=0; i < level; i++) {
      buffer.append("  ");       //$NON-NLS-1$
    }
  }
  public void addToBuffer(StringBuffer buffer, int level, SuchkriteriumFile file, Liste liste) {   
    for (int i=0; i < level; i++) {
      System.err.print("  ");       //$NON-NLS-1$
    }
    System.err.println("- "+titel); //$NON-NLS-1$
    
    int anfrageId = file.addKriterium(titel, suchkriterium);
    int trefferAnzahl = suchkriterium.getTrefferAnzahl();
    int id = 0;
    if (besitztUntereintraege()) {
      id = liste.getId(); 
    }
    
    appendIndentation(buffer, level);
    buffer.append("showEintrag(\""); //$NON-NLS-1$
    buffer.append(titel);
    buffer.append(" (").append(trefferAnzahl).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
    buffer.append("\", ").append(id).append(", ").append(level); //$NON-NLS-1$ //$NON-NLS-2$
    buffer.append(", ").append(anfrageId).append(");\n"); //$NON-NLS-1$ //$NON-NLS-2$
    
    if (besitztUntereintraege()) {
      appendIndentation(buffer, level);
      buffer.append("if (istOffen("); //$NON-NLS-1$
      buffer.append(id);
      buffer.append(")) {\n"); //$NON-NLS-1$
      
      for(int i=0; i < eintraege.size(); i++) {
        Eintrag eintrag = (Eintrag) eintraege.get(i);
        eintrag.addToBuffer(buffer, level+1, file, liste);
      }
      
      appendIndentation(buffer, level);
      buffer.append("}\n"); //$NON-NLS-1$
    }
  }
}
