package de.oberbrechen.koeb.email;

import java.io.IOException;
import java.util.StringTokenizer;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse steuert ein externes eMail-Programm an. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class ExternerEMailHandler extends EMailHandler{
        
  protected void versendeIntern(EMail email, boolean bearbeiten) throws IOException {
    String befehl; 
              
    if (bearbeiten) {
      Einstellung einstellung = getEinstellung("AnzeigeBefehl");
      befehl = einstellung.getWert("kmail $to -cc $cc -bcc $bcc -s $s -body $body");
    } else {
      Einstellung einstellung = getEinstellung("DirektBefehl");
      befehl = einstellung.getWert();      
    }
    
    if (befehl == null || befehl.equals("")) {
      ErrorHandler.getInstance().handleError("Es ist kein externes eMail-Programm konfiguriert. Daher " +
        "kann keine eMail versendet werden!", false); 
      return;
    }
     
    StringTokenizer tokenizer = new StringTokenizer(befehl);
    String[] cmd = new String[tokenizer.countTokens()];
    int pos = 0;
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      cmd[pos] = replaceKeyWords(token, email);
      pos++;
    }

    Runtime.getRuntime().exec(cmd);    
  }
  
  private String replaceKeyWords(String org, EMail email) {
    org = replace(org, "$to", email.getEmpfaenger().toKommaGetrenntenString());
    org = replace(org, "$bcc", email.getBlindkopieEmpfaenger().toKommaGetrenntenString());
    org = replace(org, "$cc", email.getKopieEmpfaenger().toKommaGetrenntenString());
    org = replace(org, "$s", email.getBetreff()!=null?email.getBetreff():"");
    org = replace(org, "$body", email.getNachricht()!=null?email.getNachricht():"");
    return org;
  }
  
  private String replace(String org, String alt, String neu) {
    int pos = org.indexOf(alt);
    
    while (pos >= 0) {            
      org = org.substring(0, pos)+neu+org.substring(pos+alt.length());
      pos = org.indexOf(alt);      
    }
    
    return org;
  }
  
  public boolean erlaubtAnzeige() {
    return getEinstellung("erlaubtAnzeige").getWertBoolean(true);    
  }

  public boolean erlaubtDirektversand() {
    return getEinstellung("erlaubtDirektversand").getWertBoolean(false);    
  }
  
  private Einstellung getEinstellung(String name) {
    return Datenbank.getInstance().getEinstellungFactory().getClientEinstellung( 
        this.getClass().getName(), name);        
  }
}
