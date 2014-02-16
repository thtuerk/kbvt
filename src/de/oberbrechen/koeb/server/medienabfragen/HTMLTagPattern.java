package de.oberbrechen.koeb.server.medienabfragen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Diese Klasse dient dazu Pattern der Form "<tag>aaa</tag>" zu finden und
* den Inhalt aaa zu extrahieren. Zudem werden HTML-Entities in aaa umgeschrieben.
* 
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class HTMLTagPattern {
  private Pattern pattern;
  private int startTag;
  private int endTag;
  
  public HTMLTagPattern(String startTag, String endTag) {
     pattern = Pattern.compile(startTag+".*?"+endTag);
     this.startTag = startTag.length();
     this.endTag = endTag.length();
  }
  
  public String sucheMatch(CharSequence cs) {
    Matcher m = pattern.matcher(cs);
    if (m.find()) {
      String s = m.group();
      return s.substring(startTag,s.length()-endTag);
    } else {
      return null;
    }
  }


  public String sucheMatchDecode(CharSequence cs) {
    return HTMLTools.htmlDecode(sucheMatch(cs));
  }

}
