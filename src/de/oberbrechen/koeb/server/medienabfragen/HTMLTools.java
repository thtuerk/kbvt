package de.oberbrechen.koeb.server.medienabfragen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
* Diese Klasse enthält einige statische Methoden, die
* den Umgang mit HTML Documenten erleichtern.
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class HTMLTools {
  public static String htmlDecode(String s) {
    if ((s == null) || (s.length() == 0)) return null;
    
    int i = 0, j = 0, pos = 0;
    s = s.replaceAll("\\s+", " ");
    s = s.replaceAll("<br\\s*/?>\\s*", "\n");
    s = s.replaceAll("<BR\\s*/?>\\s*", "\n");
    s = s.replaceAll("</br>\\s*", "");
    s = s.replaceAll("</BR>\\s*", "");
    s = s.replaceAll("<p\\s*/?>\\s*", "\n");
    s = s.replaceAll("<P\\s*/?>\\s*", "\n");
    s = s.replaceAll("</p>\\s*", "");
    s = s.replaceAll("</P>\\s*", "");
    s = s.replaceAll("<.*?>", "");
    s = s.replaceAll("&auml;", "ä");
    s = s.replaceAll("&uuml;", "ü");
    s = s.replaceAll("&ouml;", "ö");
    s = s.replaceAll("&Auml;", "Ä");
    s = s.replaceAll("&Uuml;", "Ü");
    s = s.replaceAll("&Ouml;", "Ö");
    s = s.replaceAll("&szlig;", "ß");
    s = s.replaceAll("&nbsp;", " ");
    s = s.replaceAll("&lt;", "<");
    s = s.replaceAll("&gt;", ">");
    s = s.replaceAll("&amp;", "&");
    s = s.replaceAll("&quot;", "\"");
    s = s.replaceAll("&apos;", "'");    
    
    StringBuffer sb = new StringBuffer();
    while ((i = s.indexOf("&#", pos)) != -1 && (j = s.indexOf(';', i)) != -1) {
      sb.append(s.substring(pos, i));

      String code = s.substring(i+2, j);
      sb.append((char) Integer.parseInt(code));
      pos = j+1;
    }
    if (sb.length() == 0)
        return s;
    else
        sb.append(s.substring(pos, s.length()));    
    return sb.toString();
  }

  static public StringBuffer getURL(String address, String charset) {
    try {
      StringBuffer sb = new StringBuffer();
      URL url = new URL(address);
      InputStreamReader reader = new InputStreamReader(url.openStream(), charset);
      BufferedReader buffer = new BufferedReader(reader);
      String zeile;
      while ((zeile = buffer.readLine()) != null) {
        sb.append(zeile);
      }
      return sb;
    } catch (MalformedURLException e) {
      return null;
    } catch (IOException e) {
      return null;
    } 
  }  
}
