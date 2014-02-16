package de.oberbrechen.koeb.framework;

/**
 * Diese Klasse dient dazu die einzelnenen Zeichen eines Strings als
 * HEX-Zeichen zu kodieren und wieder zu dekodieren.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class HexEncoder {

  public static String hexEncodeString(String string) {
    StringBuffer buffer = new StringBuffer();
    
    for (int i=0; i < string.length(); i++) {
      char currentChar = string.charAt(i);
      String currentCharEncode = Integer.toHexString(currentChar);
      for (int j=currentCharEncode.length(); j < 4; j++) buffer.append("0");
      buffer.append(currentCharEncode).append(" ");
    }
    return buffer.toString();
  }
  
  public static String hexEncodeChar(char character) {
   String charEncode = Integer.toHexString(character);
   for (int j=charEncode.length(); j < 4; j++) charEncode="0"+charEncode;
   return charEncode;
  }
  
  public static String hexDecodeString(String input) {
    try {
      StringBuffer buffer = new StringBuffer();
  
      String string = input.trim();
      int lastPos = 0;
      int pos = string.indexOf(' ', 0);
      do {
        String currentPart = pos != -1?string.substring(lastPos, pos):string.substring(lastPos);      
        char currentChar = (char) Integer.parseInt(currentPart,16);
        buffer.append(currentChar);
  
        lastPos = pos+1;
        pos = string.indexOf(' ', lastPos);   
      } while (pos != -1);
      return buffer.toString();

    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, false);
      return "";
    }      
  }      
}
