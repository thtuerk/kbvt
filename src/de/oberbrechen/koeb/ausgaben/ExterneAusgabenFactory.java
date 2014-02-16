package de.oberbrechen.koeb.ausgaben;

import java.awt.*;
import java.io.*;
import java.io.IOException;

import javax.swing.*;

/**
* Diese Klasse ist eine AusgabenFactory, es ermöglicht Ausgaben zu
* erstellen, die beliebige externe Programme starten kann.
*
* @author Thomas Türk (t_tuerk@gmx.de)
*/

public class ExterneAusgabenFactory implements AusgabenFactory {

  String name = null;
  String beschreibung = null;
  String befehl = null;
  boolean zeigeAusgaben = false;

  public String getName() {
    return "Externe Ausgabe";
  }

  public String getBeschreibung() {
    return "Erstellt Ausgabe, die einen exterenen Befehl ausführt. Mittels des" +      "Parameters 'befehl' kann der auszuführende Befehl angegeben werden. " +      "Die Parameter 'name' und 'beschreibung' bestimmen den Namen und" +      "die Beschreibung der Ausgabe."; 
  }

  public void setParameter(String name, String wert) throws
    ParameterException { 
    if (name == null) throw ParameterException.ParameterNameNULL;
    if (name.equals("name")) {
      this.name = wert;
    } else if (name.equals("beschreibung")) {
      this.beschreibung = wert;
    } else if (name.equals("befehl")) {
      this.befehl = wert;
    } else if (name.equals("zeigeAusgaben")) {
      this.zeigeAusgaben = Boolean.valueOf(wert).booleanValue();
    } else {
      throw ParameterException.unbekannterParameter(name);
    }
  }
    
  public void addToKnoten(AusgabenTreeKnoten knoten) 
    throws ParameterException {
    if (befehl == null) throw ParameterException.ParameterNichtGesetzt("befehl");
      
    knoten.addAusgabe(new ExterneAusgabe(name, beschreibung, befehl));    
  }

class ExterneAusgabe implements Ausgabe {
  
  public boolean istSpeicherbar() {
    return false;
  }
  
  public boolean benoetigtGUI() {
    return false;
  }

  public String getStandardErweiterung() {
    return null;
  }
  
  public void schreibeInDatei(JFrame hauptFenster, boolean zeigeDialog,
      File datei) throws Exception {
    throw new NichtSpeicherbarException();
  }
  
  
  private JPanel ausgabe;
  private JTextArea meldungen;
  private JScrollPane meldungenScrollPane;
  private JLabel befehlLabel;  

  private String name;
  private String beschreibung;
  private String befehl;
  
  public ExterneAusgabe(String name, String beschreibung, String befehl) throws ParameterException {
    if (name != null) {
      this.name = name; 
    } else {
      this.name = "Externe Ausgabe";
    }
    if (beschreibung != null) {
      this.beschreibung = beschreibung;
    } else {
      this.beschreibung = "Führt den Befehl '"+befehl+"' aus!"; 
    }
    
    if (befehl == null) throw ParameterException.ParameterNichtGesetzt("befehl");
    this.befehl = befehl;
    jbInit();
  }

  public String getName() {
    return name;
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public void run(JFrame hauptFenster, boolean zeigeDialog) throws IOException {
    final Process process = Runtime.getRuntime().exec(befehl);
    
    if (hauptFenster != null && zeigeAusgaben) {
      meldungen.setText(null);
      Thread outputThread = new Thread(new Runnable() {
        public void run() {
          try {
            BufferedReader input = new BufferedReader(new 
              InputStreamReader(process.getInputStream()));
            String line = input.readLine();
            while (line != null) {
              meldungen.append(line+"\n");
              line = input.readLine();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }});
      outputThread.start();
      Thread errorThread = new Thread(new Runnable() {
        public void run() {
          try {
            BufferedReader input = new BufferedReader(new 
              InputStreamReader(process.getErrorStream()));
            String line = input.readLine();
            while (line != null) {
              meldungen.append("FEHLER: "+line+"\n");
              line = input.readLine();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }});
      errorThread.start();
      Thread endeThread = new Thread(new Runnable() {
        public void run() {
          boolean weiter = true;
          while (weiter) {
            try {
              process.waitFor();
              weiter = false;
            } catch (InterruptedException e1) {
              weiter = true;
            }
          }
          befehlLabel.setText(befehl + " - fertig");
        }});
      endeThread.start();
         
      JOptionPane.showMessageDialog(hauptFenster, ausgabe,
        "Befehl ausführen!", JOptionPane.OK_OPTION);
  
      process.destroy();
    }
  }
    
  private void jbInit() {
    meldungen = new JTextArea();
    ausgabe = new JPanel();

    befehlLabel = new JLabel(befehl+" ...");

    ausgabe.setLayout(new GridBagLayout());
    ausgabe.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    meldungen.setWrapStyleWord(true);
    meldungen.setLineWrap(true);
    meldungen.setMargin(new Insets(5, 5, 5, 5));
    meldungen.setEditable(false);
    meldungen.setTabSize(4);
    meldungen.setFont(new java.awt.Font("Monospaced", 0, 12));

    meldungenScrollPane = new JScrollPane();
    meldungenScrollPane.setAutoscrolls(true);
    meldungenScrollPane.setMinimumSize(new Dimension(357, 100));
    meldungenScrollPane.setPreferredSize(new Dimension(357, 100));

    ausgabe.add(befehlLabel,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    ausgabe.add(meldungenScrollPane,    new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    meldungenScrollPane.getViewport().add(meldungen);
    
    ausgabe.setPreferredSize(new Dimension(780, 480));
  }
}
}