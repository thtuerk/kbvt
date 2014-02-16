package de.oberbrechen.koeb.erstelleBestandHTML;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import de.oberbrechen.koeb.erstelleBestandHTML.Eintrag;


/**
 * Diese Klasse repr�sentiert eine Liste der XML-Datei
 * 
 * @author Thomas T�rk (t_tuerk@gmx.de)
 */
public class Liste {

  int id = 0;
  
  String titel;
  String filename;
  
  Vector linkNames;
  Vector linkTos;
  
  Vector eintraege;
  
  public Liste(String titel, String filename) {
    this.titel = titel;
    this.filename = filename;
    linkNames = new Vector();
    linkTos = new Vector();
    eintraege = new Vector();
  }
  
  public void addLink(String name, String to) {
    linkNames.add(name);
    linkTos.add(to);
  }
  
  public void addEintrag(Eintrag eintrag) {
    eintraege.add(eintrag);  
  }
  
  public void writeToFile(String dir, SuchkriteriumFile file) throws IOException {
    System.err.println(filename+"\n"); //$NON-NLS-1$
    StringBuffer buffer = new StringBuffer();
    addHeader(buffer);
    addLinks(buffer);
    addDaten(buffer, file);
    addFooter(buffer);
    
    FileWriter writer = new FileWriter(dir+filename);
    writer.write(buffer.toString());
    writer.close();
    System.err.println("\n\n");     //$NON-NLS-1$
  }
  
  
  public void addDaten(StringBuffer buffer, SuchkriteriumFile file) {
    buffer.append("<h1>").append(titel).append("</h1>\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
    
    buffer.append("<?php\n"); //$NON-NLS-1$
    buffer.append("veraendereOffen();\n\n"); //$NON-NLS-1$
    
    for(int i=0; i < eintraege.size(); i++) {
      Eintrag eintrag = (Eintrag) eintraege.get(i);
      eintrag.addToBuffer(buffer, 0, file, this);
    }    
    
    buffer.append("?>"); //$NON-NLS-1$
  }
  
  public void addLinks(StringBuffer buffer) {
    buffer.append("<table class=\"link\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n"); //$NON-NLS-1$
    buffer.append("<tr><td class=\"link\">\n"); //$NON-NLS-1$
    buffer.append("   <a class=\"link\" target=\"_top\" href=\"../index.php\">Startseite</a>\n"); //$NON-NLS-1$
    
    for (int i=0; i < linkNames.size(); i++) {
      if (linkTos.get(i) == null || linkTos.get(i).equals("")) { //$NON-NLS-1$
        buffer.append("   | <font class=\"activeLink\">"); //$NON-NLS-1$
        buffer.append(linkNames.get(i));
        buffer.append("</font> \n"); //$NON-NLS-1$
      } else {
        buffer.append("   | <a class=\"link\" href=\""); //$NON-NLS-1$
        buffer.append(linkTos.get(i));
        buffer.append("\">"); //$NON-NLS-1$
        buffer.append(linkNames.get(i));
        buffer.append("</a>\n");         //$NON-NLS-1$
      }
    }
    buffer.append("</td></tr>\n"); //$NON-NLS-1$
    buffer.append("</table><hr>\n");         //$NON-NLS-1$
  }
  
  public void addHeader(StringBuffer buffer) {
    buffer.append("<?php\n"); //$NON-NLS-1$
    buffer.append("   session_start();\n"); //$NON-NLS-1$
    buffer.append("   require \"medienlisten.inc\";\n"); //$NON-NLS-1$
    buffer.append("?>\n\n"); //$NON-NLS-1$

    buffer.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"); //$NON-NLS-1$
    buffer.append("<html>\n"); //$NON-NLS-1$
    buffer.append("<head>\n"); //$NON-NLS-1$
    buffer.append("   <title>").append(titel).append("</title>\n"); //$NON-NLS-1$ //$NON-NLS-2$
    buffer.append("   <link rel=stylesheet type=\"text/css\" href=\"../koeb.css\">\n"); //$NON-NLS-1$
    buffer.append("</head>\n\n"); //$NON-NLS-1$

    buffer.append("<body marginheight=\"0\" class=\"oben\">"); //$NON-NLS-1$
  }
  
  public void addFooter(StringBuffer buffer) {
    buffer.append("</body>\n"); //$NON-NLS-1$
    buffer.append("</html>\n"); //$NON-NLS-1$
  }

  public int getId() {
    id++;
    return id;
  }  
}
