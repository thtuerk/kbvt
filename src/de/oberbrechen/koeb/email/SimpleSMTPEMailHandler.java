package de.oberbrechen.koeb.email;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sun.misc.BASE64Encoder;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenstrukturen.Liste;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class SimpleSMTPEMailHandler extends EMailHandler {

  protected static String from =
    Datenbank.getInstance().getEinstellungFactory().getEinstellung(
        "de.oberbrechen.koeb.email.SimpleSMTPEMailHandler", "Absender").getWert(null);
  protected static String server =
    Datenbank.getInstance().getEinstellungFactory().getEinstellung(
        "de.oberbrechen.koeb.email.SimpleSMTPEMailHandler", "Server").getWert(null);
  protected static String username =
    Datenbank.getInstance().getEinstellungFactory().getEinstellung(
        "de.oberbrechen.koeb.email.SimpleSMTPEMailHandler", "Username").getWert(null);
  protected static String password =
    Datenbank.getInstance().getEinstellungFactory().getEinstellung(
        "de.oberbrechen.koeb.email.SimpleSMTPEMailHandler", "Passwort").getWert(null);

  protected BASE64Encoder encoder = new BASE64Encoder();    

  protected String encodeEmpfaenger(String empfaenger) {
    if (empfaenger == null) return "";
    int pos = empfaenger.indexOf('<');
    if (pos <= 0) return empfaenger;

    return encode(empfaenger.substring(0, pos))+empfaenger.substring(pos);
  }

  protected String encodeEmpfaengerListe(Liste<String> liste) {
    Liste<String> encodedListe = new Liste<String>();
    
    for (String empf : liste) {
      encodedListe.add(encodeEmpfaenger(empf));
    }
    
    return encodedListe.toKommaGetrenntenString();
  }
  
  protected String encode(String string) {
    if (string == null || string.length() == 0) return "";       

    byte[] bytes = string.getBytes();
    return "=?ISO-8859-1?B?"+encoder.encode(bytes)+"?=";    
  }

  
  protected void checkResponse(BufferedReader in) throws Exception {
    String response = in.readLine();
    if(response.startsWith("4") || response.startsWith("5")) {
      throw new Exception(response);
    }
  }
  
  protected void writeln(String line, BufferedWriter out) throws Exception {
    out.write(line);
    out.newLine();
    out.flush();
  }  
  
  protected void writelnCheck(String line, BufferedWriter out, BufferedReader in) throws Exception {
    writeln(line, out);
    checkResponse(in);
  }
    
  protected void versendeIntern(EMail email, boolean bearbeiten) {
    try {      
      //Initialisiere Verbindung mit SMTP Server
      Socket socket = new Socket(server, 25);
      socket.setSoTimeout(5000);
      socket.setKeepAlive(true);
      BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      BufferedWriter socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  
      checkResponse(socketIn);
      
      writelnCheck("HELO " + InetAddress.getByName(InetAddress.getLocalHost().getHostAddress())
          .getHostName(), socketOut, socketIn);
          
      //AUTH PLAIN
      if (username != null && password != null && username.trim().length() > 0) {
        writelnCheck("AUTH PLAIN",socketOut, socketIn);
        
        BASE64Encoder encoder = new BASE64Encoder();
        String login = "\u0000"+username+"\u0000"+password;    
        byte[] bytes = login.getBytes();
        writelnCheck(encoder.encode(bytes),socketOut, socketIn);    
      }
        
      //Eigentliche eMail
      writelnCheck("MAIL FROM:" + encodeEmpfaenger(from), socketOut, socketIn);
  
      Liste<String> alleEmpfaenger = email.getAlleEmpfaenger();
      for (int i=0; i < alleEmpfaenger.size(); i++) {
        String recipient = (String) alleEmpfaenger.get(i);
        writelnCheck("RCPT TO:" + encodeEmpfaenger(recipient), socketOut, socketIn);
      }
      
      writelnCheck("DATA", socketOut, socketIn);        
  
      //Klappt leider nur ab 1.4: SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
      //Daher dieser "dumme" Workaround
      SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss '+0200'", Locale.US);
      
      writeln("MIME-Version: 1.0", socketOut);
      writeln("From: " + encodeEmpfaenger(from), socketOut);
      writeln("To: "+encodeEmpfaengerListe(email.getEmpfaenger()), socketOut);
      writeln("Cc: "+encodeEmpfaengerListe(email.getKopieEmpfaenger()), socketOut);
      writeln("Bcc: "+encodeEmpfaengerListe(email.getBlindkopieEmpfaenger()), socketOut);
      writeln("Subject: "+encode(email.getBetreff()), socketOut);
      writeln("Date: "+dateFormat.format(new Date()), socketOut);
      writeln("Content-Type: text/plain; charset=iso-8859-1", socketOut);
      writeln("Message-ID: "+createMessageID(), socketOut);
      writeln("",socketOut);            
      writeln(encodeNachricht(email.getNachricht()), socketOut);
      writelnCheck(".", socketOut, socketIn);
      
      //Ende
      writelnCheck("QUIT", socketOut, socketIn);
      socket.close();
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Versenden der folgenden eMail:\n"+email.toDebugString(),false);
    }
  }

  private String encodeNachricht(String line) {
    if (line.startsWith(".")) return "."+line;
    return line;
  }

  protected String createMessageID() {
    return "<"+System.currentTimeMillis()+"@kbvt.koeb-oberbrechen.de>";
  }

  public boolean erlaubtAnzeige() {
    return false;
  }

  public boolean erlaubtDirektversand() {
    return true;
  }
}
