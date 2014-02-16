package de.oberbrechen.koeb.gui.ausleihe.ausleiheReiter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import de.oberbrechen.koeb.datenbankzugriff.*;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.MediumBereitsVerliehenException;
import de.oberbrechen.koeb.einstellungen.Ausleihordnung;
import de.oberbrechen.koeb.einstellungen.Buecherei;
import de.oberbrechen.koeb.einstellungen.VerlaengerungsInformationen;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.gui.ausleihe.AusleiheMainReiter;
import de.oberbrechen.koeb.gui.ausleihe.Main;
import de.oberbrechen.koeb.gui.framework.JComponentFormatierer;

/**
 * Diese Klasse repräsentiert den Reiter, der die Ausleihe und Rückgabe
 * ermöglicht.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class AusleiheReiter extends JPanel implements AusleiheMainReiter {

  //Konstanten für die bearbeitenButtonFunktion
  private static final int SPEICHERN = 0;
  private static final int BEARBEITEN = 1;

  //Konstanten für die verwerfenButtonFunktion
  private static final int AENDERUNGEN_VERWERFEN = 0;
  private static final int AUSLEIHE_LOESCHEN = 1;
  private static final int VERLAENGERN_RUECKGAENGIG = 2;
  private static final int RUECKGABE_RUECKGAENGIG = 3;

  Main hauptFenster;
  JButton bearbeitenButton = new JButton();
  int bearbeitenButtonFunktion;
  JButton verlaengernButton = new JButton();
  JButton verwerfenButton = new JButton();
  int verwerfenButtonFunktion;
  JButton zurueckgebenButton = new JButton();

  private AusleihenTabelle ausleihenTabelle;
  private AusleiheDetails ausleiheDetails;
  private Ausleihordnung ausleihordnung;
  private Ausleihe[] aktuelleAusleihen;
  private boolean erlaubeAenderungen;

  private static SimpleDateFormat dateFormat =
      new SimpleDateFormat("dd. MMMM yyyy");
  private AusleiheFactory ausleiheFactory =
    Datenbank.getInstance().getAusleiheFactory();


  public AusleiheReiter(Main parentFrame) {
    ausleihordnung = Buecherei.getInstance().getAusleihordnung();
    hauptFenster = parentFrame;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    //Buttonpanel
    bearbeitenButton.setText("Ausleihe bearbeiten");
    bearbeitenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switch (bearbeitenButtonFunktion) {
          case BEARBEITEN:   bearbeiteAusleihe(); break;
          case SPEICHERN:    speichereAenderungen(); break;
        }
      }
    });
    verwerfenButton.setText("Änderungen verwerfen");
    verwerfenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switch (verwerfenButtonFunktion) {
          case AENDERUNGEN_VERWERFEN:    verwerfeAenderungen(); break;
          case RUECKGABE_RUECKGAENGIG:   macheRueckgabeRueckgaengig(); break;
          case VERLAENGERN_RUECKGAENGIG: macheVerlaengernRueckgaengig(); break;
          case AUSLEIHE_LOESCHEN:        loescheAusleihe(); break;
        }
      }
    });
    verlaengernButton.setText("Ausleihe verlängern (3)");
    JComponentFormatierer.setDimension(verlaengernButton,
        verlaengernButton.getPreferredSize());
    verlaengernButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        verlaengereAusleihe();
      }
    });
    zurueckgebenButton.setText("Medium zurückgeben");
    zurueckgebenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mediumZurueckgeben();
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1,4,15,0));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
    buttonPanel.add(bearbeitenButton, null);
    buttonPanel.add(verwerfenButton, null);
    buttonPanel.add(zurueckgebenButton, null);
    buttonPanel.add(verlaengernButton, null);

    ausleiheDetails = new AusleiheDetails(hauptFenster);
    ausleihenTabelle = new AusleihenTabelle(this, hauptFenster);
    ausleihenTabelle.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    ausleiheDetails.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

    //Splitpanel
    JSplitPane jSplitPane1 = new JSplitPane();
    jSplitPane1.setBorder(BorderFactory.createEmptyBorder());
    jSplitPane1.add(ausleiheDetails, JSplitPane.LEFT);
    jSplitPane1.add(ausleihenTabelle, JSplitPane.RIGHT);

    //alles zusammenbauen
    this.setLayout(new BorderLayout());
    this.add(jSplitPane1, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  //Doku siehe bitte Interface
  public void aktualisiere() {
    ausleiheDetails.aktualisiere();
  }

  //Doku siehe bitte Interface
  public void setBenutzer(Benutzer benutzer) {
    if (benutzer == null) return;

    erlaubeAenderungen(false);
    ausleihenTabelle.ladeAusleihenVon(benutzer);
  }

  //Doku siehe bitte Interface
  public void refresh() {
    setBenutzer(hauptFenster.getAktivenBenutzer());
  }

  /**
   * Zeigt die übergebene Ausleihe an. D.h. es wird der zur Ausleihe
   * gehörende Benutzer gesetzt und der Ausleihe-Reiter aktiviert. Dort wird,
   * falls möglich, die übergebene Ausleihe ausgewählt.
   *
   * @param ausleihe die anzuzeigende Ausleihe
   */
  public void zeigeAusleihe(Ausleihe ausleihe) {
    Ausleihe[] ausleihen = new Ausleihe[1];
    ausleihen[0] = ausleihe;
    ausleihenTabelle.markiereAusleihen(ausleihen);
  }


  protected void ausleihenAuswahlGewechselt(Ausleihe neueAusleihe) {
    if (neueAusleihe == null) {
      ausleihenAuswahlGewechselt(new Ausleihe[0]);      
    } else {
      Ausleihe[] ausleihen = new Ausleihe[1];
      ausleihen[0] = neueAusleihe;
      ausleihenAuswahlGewechselt(ausleihen);
    }
  }

  /**
   * Über diese Methode wird dem Reiter mitgeteilt, dass sich
   * die in der Liste ausgewählte Ausleihe geändert hat.
   *
   * @param neueAusleihe die neu gewählte Ausleihe
   */
  protected void ausleihenAuswahlGewechselt(Ausleihe[] neueAusleihen) {
    aktuelleAusleihen = neueAusleihen;
    initButtons(false);
      
    if (neueAusleihen.length == 1) {
      ausleiheDetails.setAusleihe(neueAusleihen[0]);
    } else {
      ausleiheDetails.setAusleihe(null);
    }
  }

  /**
   * Erstellt eine neue Ausleihe und zeigt sie zum Bearbeiten an
   */
  public void neueAusleiheErstellen() {
    aktuelleAusleihen = new Ausleihe[1]; 
    aktuelleAusleihen[0] = ausleiheFactory.erstelleNeu();
    aktuelleAusleihen[0].setBenutzer(hauptFenster.getAktivenBenutzer());
    aktuelleAusleihen[0].setMitarbeiterAusleihe(hauptFenster.getAktuellenMitarbeiter());
    ausleiheDetails.setAusleihe(aktuelleAusleihen[0]);
    bearbeiteAusleihe();
  }

  /**
   * Erlaubt / verbietet das Ändern der angezeigten Ausleihe.
   *
   * @param erlaubt bestimmt, ob Änderungen erlaubt sind
   */
  public void erlaubeAenderungen(boolean erlaubt) {
    ausleiheDetails.erlaubeAenderungen(erlaubt);
    ausleihenTabelle.erlaubeAenderungen(!erlaubt);
    hauptFenster.erlaubeAenderungen(!erlaubt);
    erlaubeAenderungen = erlaubt;
  }

  /**
   * Verwirft die an der aktuellen Ausleihe gemachten Änderungen
   */
  void verwerfeAenderungen() {
    ausleiheDetails.verwerfeAenderungen();

    //wenn die Ausleihe noch nicht gespeichert ist, verwerfe sie komplett
    if (aktuelleAusleihen[0].istNeu())
      ausleihenTabelle.markiereErsteAusleihe();

    erlaubeAenderungen(false);
    initButtons(false);
    ausleihenTabelle.grabFocus();
  }

  /**
   * Diese Methode dient dazu, MediumBereitsVerliehenExceptions, die beim
   * Speichern einer Ausleihe auftreten zu behandeln.
   *
   * @return <code>true</code> wenn das Speichern trotz Exception
   *   erfolgreich verlief
   * @throws MediumBereitsVerliehenException
   */
  private boolean behandleMediumBereitsVerliehenException
    (MediumBereitsVerliehenException e) {

    boolean speichernOK = false;

    if (e.getKonfliktAusleihe().getBenutzer().equals(
        e.getVerursachendeAusleihe().getBenutzer())) {

      if (e.getKonfliktAusleihe().heuteZurueckgegeben()) {
        JOptionPane.showMessageDialog(hauptFenster,
          "Das Medium '"+e.getKonfliktAusleihe().getMedium().getTitel()+
          "' ("+e.getKonfliktAusleihe().getMedium().getMedienNr() + ")\n"+
          "war seit dem "+
          dateFormat.format(e.getKonfliktAusleihe().getAusleihdatum()) +
          " bis heute von "+e.getKonfliktAusleihe().getBenutzer().getName()+
          " ausgeliehen!\n\nVerlängern Sie gegebenenfalls diese Ausleihe!",
          "Ausleihe nicht möglich!",
          JOptionPane.ERROR_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(hauptFenster,
          "Das Medium '"+e.getKonfliktAusleihe().getMedium().getTitel()+
          "' ("+e.getKonfliktAusleihe().getMedium().getMedienNr() + ")\n"+
          "ist bereits seit dem "+
          dateFormat.format(e.getKonfliktAusleihe().getAusleihdatum()) +
          " von "+e.getKonfliktAusleihe().getBenutzer().getName()+
          " ausgeliehen!\n\nVerlängern Sie gegebenenfalls diese Ausleihe!",
          "Ausleihe nicht möglich!",
          JOptionPane.ERROR_MESSAGE);
      }
    } else {
      int erg = JOptionPane.showConfirmDialog(hauptFenster,
        "Das Medium '"+e.getKonfliktAusleihe().getMedium().getTitel()+
        "' ("+e.getKonfliktAusleihe().getMedium().getMedienNr() + ")\n"+
        "ist seit dem "+
        dateFormat.format(e.getKonfliktAusleihe().getAusleihdatum())+" von "+
        e.getKonfliktAusleihe().getBenutzer().getName()+" ausgeliehen!\n\n"+
        "Wollen Sie wirklich diese Ausleihe beenden und das "+
        "Medium\nneu an "+e.getVerursachendeAusleihe().getBenutzer().getName()+
        " ausleihen?",
        "Ausleihe-Konflikt!", JOptionPane.YES_NO_OPTION,
        JOptionPane.ERROR_MESSAGE);
      if (erg == JOptionPane.YES_OPTION) {
        Ausleihe konfliktAusleihe = e.getKonfliktAusleihe();
        Date rueckgabeDatum = konfliktAusleihe.getSollRueckgabedatum();
        if (e.getVerursachendeAusleihe().getAusleihdatum().before(rueckgabeDatum))
          rueckgabeDatum = e.getVerursachendeAusleihe().getAusleihdatum();

        konfliktAusleihe.setRueckgabedatum(rueckgabeDatum);
        konfliktAusleihe.setBemerkungen("Ausleihe irregulär zurückgegeben!");
        konfliktAusleihe.setMitarbeiterRueckgabe(
          hauptFenster.getAktuellenMitarbeiter());
        try {
          konfliktAusleihe.save();
          speichernOK = true;
        } catch (DatenbankzugriffException e1) {
          ErrorHandler.getInstance().handleException(e, "Die in Konflikt " +
              "stehende Ausleihe konnte nicht gespeichert werden!", false);
          speichernOK = false;
        }

      }
    }
    return speichernOK;
  }

  /**
   * Speichere die an der aktuellen Ausleihe gemachten Änderungen
   */
  void speichereAenderungen() {
    boolean speichernOK = true;
    try {
      ausleiheDetails.save();
    } catch (MediumBereitsVerliehenException e) {
      speichernOK = behandleMediumBereitsVerliehenException(e);
      if (speichernOK) {
        speichereAenderungen();
        return;      
      }
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, 
          "Ausleihe konnte nicht gespeichert werden!", false);
      speichernOK = false;
    }
    if (speichernOK) {
      Ausleihe geaenderteAusleihe = aktuelleAusleihen[0];
      ausleihenTabelle.add(geaenderteAusleihe);
      ausleihenTabelle.refresh();
      Ausleihe[] ausleihen = new Ausleihe[1];
      ausleihen[0] = geaenderteAusleihe;      
      ausleihenTabelle.markiereAusleihen(ausleihen);
      erlaubeAenderungen(false);
      initButtons(false);
      ausleihenTabelle.grabFocus();
    }
  }

  /**
   * Erlaubt das Bearbeiten der aktuellen Ausleihe
   */
  public void bearbeiteAusleihe() {
    erlaubeAenderungen(true);
    initButtons(true);
    ausleiheDetails.grabFocus();
  }

  /**
   * Setzt die Buttons passend zur aktuellen Ausleihe und abhängig davon,
   * ob sie gerade bearbeitet wird oder nicht
   *
   * @param wirdBearbeitet gibt an, ob die aktuelle Ausleihe gerade bearbeitet
   *   wird
   */
  public void initButtons(boolean wirdBearbeitet) {
    bearbeitenButton.setText("Ausleihe bearbeiten");
    bearbeitenButtonFunktion = BEARBEITEN;
    bearbeitenButton.setEnabled(false);
    verwerfenButton.setText("Ausleihe löschen");
    verwerfenButton.setEnabled(false);
    verlaengernButton.setText("Ausleihe verlängern");
    verlaengernButton.setEnabled(false);
    zurueckgebenButton.setEnabled(false);

    if (aktuelleAusleihen.length == 1) {
      String verlaengernText = "Ausleihe verlängern";
      int anzahlVerlaengerungen = aktuelleAusleihen[0].getAnzahlVerlaengerungen();
      if (anzahlVerlaengerungen > 0) {
        verlaengernText += " ("+(anzahlVerlaengerungen+1)+")";
        verlaengernButton.setText(verlaengernText);
      }
    } 
    
    if (aktuelleAusleihen.length == 0) return;
    
    boolean heuteZurueck = true;
    boolean aktuellVerlaengert = true;
    boolean heuteGetaetigt = true;
    boolean nichtBesonders = true;
    for (int i=0; i < aktuelleAusleihen.length; i++) {
       heuteZurueck = heuteZurueck && aktuelleAusleihen[i].heuteZurueckgegeben();
       aktuellVerlaengert = aktuellVerlaengert && aktuelleAusleihen[i].istAktuellVerlaengert();
       heuteGetaetigt = heuteGetaetigt && aktuelleAusleihen[i].heuteGetaetigt();
       nichtBesonders = nichtBesonders &&
         !aktuelleAusleihen[i].heuteZurueckgegeben() &&
         !aktuelleAusleihen[i].istAktuellVerlaengert() &&
         !aktuelleAusleihen[i].heuteGetaetigt();
    }
    
    
    if (heuteZurueck) {
      verwerfenButton.setText("Rückgabe rückgängig");
      verwerfenButtonFunktion = RUECKGABE_RUECKGAENGIG;
      verwerfenButton.setEnabled(true);
      bearbeitenButton.setEnabled(true);
    } else if (aktuellVerlaengert) {
      verwerfenButton.setText("Verlängern rückgängig");
      verwerfenButtonFunktion = VERLAENGERN_RUECKGAENGIG;
      verwerfenButton.setEnabled(true);
      bearbeitenButton.setEnabled(true);
      verlaengernButton.setEnabled(true);
    } else if (heuteGetaetigt) {
      bearbeitenButton.setEnabled(true);
      verwerfenButton.setText("Ausleihe löschen");
      verwerfenButtonFunktion = AUSLEIHE_LOESCHEN;
      if (aktuelleAusleihen.length == 1) verwerfenButton.setEnabled(true);
      zurueckgebenButton.setEnabled(true);
      verlaengernButton.setEnabled(true);
    } else if (nichtBesonders) {
      verlaengernButton.setEnabled(true);
      zurueckgebenButton.setEnabled(true);
    }
    if (aktuelleAusleihen.length != 1) bearbeitenButton.setEnabled(false);

    if (verlaengernButton.isEnabled() && aktuelleAusleihen.length == 1) {
      VerlaengerungsInformationen infos =
        ausleihordnung.getVerlaengerungsInformationen(aktuelleAusleihen[0], new Date());
      if (!infos.istErlaubt()) {
        verlaengernButton.setEnabled(false);
        verlaengernButton.setToolTipText(infos.getBemerkungen());
      }
    }

    if (wirdBearbeitet) {
      bearbeitenButton.setText("Speichern");
      bearbeitenButtonFunktion = SPEICHERN;
      verwerfenButton.setText("Änderungen verwerfen");
      verwerfenButtonFunktion = AENDERUNGEN_VERWERFEN;

      bearbeitenButton.setEnabled(true);
      verwerfenButton.setEnabled(true);
      verlaengernButton.setEnabled(false);
      zurueckgebenButton.setEnabled(false);
    }
  }


  /**
   * Gibt das aktuelle Medium zurück.
   */
  void mediumZurueckgeben() {
    for (int i = 0; i < aktuelleAusleihen.length; i++) {
      try {
        aktuelleAusleihen[i].zurueckgeben(hauptFenster.getAktuellenMitarbeiter());
        aktuelleAusleihen[i].save();
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Medium konnte nicht " +
            "zurückgegeben werden!", false);
      }    
    }
    ausleihenTabelle.aktualisiere();
    ausleihenTabelle.markiereAusleihen(aktuelleAusleihen);
  }

  /**
   * Verlängert die aktuelle Ausleihe;
   */
  void verlaengereAusleihe() {
    for (int i = 0; i < aktuelleAusleihen.length; i++) {
      try {
        aktuelleAusleihen[i].verlaengere(hauptFenster.getAktuellenMitarbeiter());
        aktuelleAusleihen[i].save();
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Ausleihe konnte nicht " +
            "verlängert werden!", false);
      }
    }
    ausleihenTabelle.aktualisiere();
    ausleihenTabelle.markiereAusleihen(aktuelleAusleihen);
  }

  /**
   * Macht das Verlängeren der aktuellen Ausleihe rückgängig
   */
  void macheVerlaengernRueckgaengig() {
    for (int i = 0; i < aktuelleAusleihen.length; i++) {
      try {
        aktuelleAusleihen[i].verlaengernRueckgaengig();
        aktuelleAusleihen[i].save();
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, "Verlängern der Ausleihe " +
            "konnte nicht rückgängig gemacht werden!", false);
      }
    }
    ausleihenTabelle.aktualisiere();
    ausleihenTabelle.markiereAusleihen(aktuelleAusleihen);
  }

  /**
   * Löscht die aktuelle Ausleihe
   */
  void loescheAusleihe() {
    int erg = JOptionPane.showConfirmDialog(hauptFenster,"Soll die Ausleihe "+
      "wirklich gelöscht werden?", "Löschbestätigung",
      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

    if (erg == JOptionPane.YES_OPTION) {
      try {
        Ausleihe alteAusleihe = aktuelleAusleihen[0];
        alteAusleihe.loesche();
        ausleihenTabelle.remove(alteAusleihe);
        ausleihenTabelle.markiereErsteAusleihe();
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, "Ausleihe konnte " +
            "nicht gelöscht werden!", false);
      }
    }
  }

  /**
   * Macht die Rückgabe der aktuellen Ausleihe rückgängig.
   */
  void macheRueckgabeRueckgaengig() {
    for (int i = 0; i < aktuelleAusleihen.length; i++) {
      try {
        aktuelleAusleihen[i].setRueckgabedatum(null);
        aktuelleAusleihen[i].save();
        ausleihenTabelle.aktualisiere();
      } catch (MediumBereitsVerliehenException e) {
        try {
          aktuelleAusleihen[i].reload();
        } catch (Exception e2) {
          ErrorHandler.getInstance().handleException(e2, false);
        }
        JOptionPane.showMessageDialog(hauptFenster,
          "Das Zurückgeben des Mediums kann nicht rückgängig gemacht "+
          "werden, da \n"+
          "das Medium '"+e.getKonfliktAusleihe().getMedium().getTitel()+
          "' ("+e.getKonfliktAusleihe().getMedium().getMedienNr() + ")\n"+
          "inzwischen von "+e.getKonfliktAusleihe().getBenutzer().getName()+
          " ausgeliehen wurde.\n\nMachen Sie bitte zuerst diese Ausleihe "+
          "rückgängig!", "Rückgängig machen der Rückgabe nicht möglich!",
          JOptionPane.ERROR_MESSAGE);
      } catch (DatenbankzugriffException e) {
        ErrorHandler.getInstance().handleException(e, false);
      }
    }
    ausleihenTabelle.aktualisiere();
    ausleihenTabelle.markiereAusleihen(aktuelleAusleihen);
  }

  // Doku siehe bitte Interface
  public void mediumEANGelesen(Medium medium) {
    if (erlaubeAenderungen) {
      ausleiheDetails.mediumEANGelesen(medium);
    } else {
      Ausleihe[] alteAusleihen = aktuelleAusleihen;
      boolean speichernOK = true;
      try {
        aktuelleAusleihen = new Ausleihe[1];
        aktuelleAusleihen[0] = ausleiheFactory.erstelleNeu();
        aktuelleAusleihen[0].setMedium(medium);
        aktuelleAusleihen[0].setBenutzer(hauptFenster.getAktivenBenutzer());
        aktuelleAusleihen[0].setMitarbeiterAusleihe(hauptFenster.getAktuellenMitarbeiter());
        aktuelleAusleihen[0].save();
      } catch (MediumBereitsVerliehenException e) {
        speichernOK = behandleMediumBereitsVerliehenException(e);
        if (speichernOK) {
          mediumEANGelesen(medium);
          return;      
        }
      } catch (DatenbankzugriffException e) {
        JOptionPane.showMessageDialog(hauptFenster, e.getMessage(),
          "Ausleihe konnte nicht gespeichert werden!", 
          JOptionPane.ERROR_MESSAGE);
        speichernOK = false;
      }
      if (speichernOK) {
        Ausleihe geaenderteAusleihe = aktuelleAusleihen[0];
        ausleihenTabelle.add(geaenderteAusleihe);
        ausleihenTabelle.aktualisiere();
        ausleihenTabelle.markiereAusleihen(aktuelleAusleihen);
        erlaubeAenderungen(false);
        initButtons(false);
        ausleihenTabelle.grabFocus();
      } else {
        aktuelleAusleihen = alteAusleihen;
      }
    }
  }

  public JMenu getMenu() {
    return null;
  }
  
  public void focusLost() {
  }
}