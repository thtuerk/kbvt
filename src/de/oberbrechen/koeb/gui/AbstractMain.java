package de.oberbrechen.koeb.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import de.oberbrechen.koeb.datenbankzugriff.Benutzer;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Einstellung;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankzugriffException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.UnpassendeEinstellungException;
import de.oberbrechen.koeb.datenstrukturen.BenutzerListe;
import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.framework.KonfigurationsDatei;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeGelesenEventHandler;
import de.oberbrechen.koeb.gui.barcodescanner.BarcodeReaderAdapter;
import de.oberbrechen.koeb.gui.components.listenAuswahlPanel.benutzerListenAuswahlPanel.BenutzerListenAuswahlPanel;
import de.oberbrechen.koeb.gui.standarddialoge.InfoDialog;
import de.oberbrechen.koeb.gui.standarddialoge.MitarbeiterAuswahlDialog;
import de.oberbrechen.koeb.gui.standarddialoge.PasswortAendernDialog;
import de.oberbrechen.koeb.gui.standarddialoge.StandardGUIErrorHandler;
import de.oberbrechen.koeb.gui.standarddialoge.WarteDialog;

/**
 * Diese Klasse ist eine abstrakte Hauptklasse für die 
 * verschiedenen graphischen Oberflächen. Sie bietet die allgemeine
 * Funktionalität an, muss aber noch konkretisiert werden.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class AbstractMain extends JFrame {

  protected JMenu[] reiterMenus;
  
  private static int instanceCount = 0;
  protected Mitarbeiter currentMitarbeiter;
  
  protected JTabbedPane reiter;
  protected MainReiter currentReiter;
  protected MitarbeiterAuswahlDialog mitarbeiterAuswahlDialog;

  protected Dimension minDimension;
  protected boolean erlaubeAenderungen;
  protected int noetigeMitarbeiterBerechtigung;

  @SuppressWarnings("null")
  public AbstractMain(boolean isMain, 
    final String titel, int noetigeMitarbeiterBerechtigung, String icon,
    Mitarbeiter paramMitarbeiter) {
    AbstractMain.instanceCount++;      
    this.noetigeMitarbeiterBerechtigung = noetigeMitarbeiterBerechtigung;
    
    ladeIcon(icon);
    if (isMain) {
      ErrorHandler.setInstance(new StandardGUIErrorHandler(this));
      setLookAndFeel();
      ErrorHandler.setInstance(new StandardGUIErrorHandler(this));
    } else {
      currentMitarbeiter = paramMitarbeiter;
    }

    /*
     * Hintergrundthread bauen. Dieser Thread initialisiert während
     * der Passworteingabe bereits die GUI und beschleunigt so den
     * Start erheblich
     */
    final AbstractMain abstractMain = this;
    Thread hintergrundThread = new Thread(new Runnable(){
      public synchronized void run() {
        // GUI-Initialisieren
        try {
          jbInit(titel);
          reiterHinzufuegen();
          initMenue();
        }
        catch(Exception e) {
          e.printStackTrace();
        }

        erlaubeAenderungen(true);
        initDaten();
        abstractMain.pack();
        minDimension = abstractMain.getSize();
        SwingUtilities.updateComponentTreeUI(abstractMain);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - abstractMain.getWidth())/2, 
          (dim.height - abstractMain.getHeight())/2);
      }});
    hintergrundThread.setPriority(Thread.MIN_PRIORITY);

    if (currentMitarbeiter == null || 
      !currentMitarbeiter.besitztBerechtigung(noetigeMitarbeiterBerechtigung)) {
      Einstellung mitarbeiterEinstellung = Datenbank.getInstance().getEinstellungFactory().getClientEinstellung(
          "de.oberbrechen.koeb.gui", "Standardmitarbeiter");
      int mitarbeiterID = mitarbeiterEinstellung.getWertInt(-1);
      if (mitarbeiterID > 0) {
        try {
          currentMitarbeiter = (Mitarbeiter) Datenbank.getInstance().
            getMitarbeiterFactory().get(mitarbeiterID);
        } catch (Exception e) {
          ErrorHandler.getInstance().handleException(e, 
              "Fehler beim Laden des Standardmitarbeiters!", false);
          currentMitarbeiter = null;
        }
      }
    }
    
    if (currentMitarbeiter == null || 
        !currentMitarbeiter.besitztBerechtigung(noetigeMitarbeiterBerechtigung)) {
      hintergrundThread.start();
      boolean wechselOK = mitarbeiterWechseln();
      if (!wechselOK) {
        hintergrundThread.interrupt();
        dispose();
        return;
      } //Abbruch, wenn kein Mitarbeiter gewählt
    } else {
      hintergrundThread.start();      
    }

    //Wartedialog anzeigen, bis hintergundThread alles initialisiert hat    
    if (hintergrundThread.isAlive()) {
      hintergrundThread.setPriority(Thread.MAX_PRIORITY);
      WarteDialog warteDialog = null;
      
      if (isMain) {
        warteDialog = new WarteDialog();
        warteDialog.setVisible(true);
      } else {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      }
      Thread.yield();

      try {
        hintergrundThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  
      if (isMain) {
        //nie null wegen isMail 
        warteDialog.dispose();
      } else {
        setCursor(Cursor.getDefaultCursor());
      }
    }

    setMitarbeiter(currentMitarbeiter);
    this.setVisible(true);
  }

  /**
   * Initialisiert das Menue
   */
  protected void initMenue() {
    JMenuBar menue = new JMenuBar();     
    addMenue(menue);
    this.setJMenuBar(menue);        
  }
  
  /**
   * Fügt das Menü hinzu
   */
  protected void addMenue(JMenuBar menue) {
    menue.add(getDateiMenue(false));
    addReiterMenues(menue);
    menue.add(getInfoMenue());
  }      

  
  /**
   * Fügt den übergebenen KeyListener zur übergebenen Componente und
   * allen Unterkomponenten hinzu.
   */
  protected void addKeyListenerAlleComponenten(Component component, 
    KeyListener keyListener) {
    
    component.addKeyListener(keyListener);
    
    if (component instanceof Container) {
      Container container = (Container) component;
      for (int i=0; i < container.getComponentCount(); i++) {
        addKeyListenerAlleComponenten(container.getComponent(i), keyListener);
      }
    }
  }
  
  /**
   * Liefert ein Info-Menue, das Informationen über KBVT anzeigt.
   * @return das erzeugte Info-Menue
   */
  protected JMenu getInfoMenue() {
    JMenu infoMenue = new JMenu("Hilfe");

    JMenuItem ueberButton = new JMenuItem("Über");
    ueberButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread() {
          public void run() {
            new InfoDialog().setVisible(true);
          }
        }.start();        
      }
    });
    
    infoMenue.add(ueberButton);
    
    return infoMenue;
  }
    
  /**
   * Liefert ein Datei-Menue, das die Standardoperationen, wie Ausgabe,
   * Mitarbeiter-Wechseln und Exit anbietet.
   * @param mininal bestimmt, ob das Menue minimal ist
   * @return das erzeugte Datei-Menue
   */
  protected JMenu getDateiMenue(boolean minimal) {
    // Menü bauen
    JMenu dateiMenue = new JMenu("Datei");
    
    if (!minimal) {
      JMenuItem mitarbeiterAendernButton = new JMenuItem("Mitarbeiter wechseln");
      mitarbeiterAendernButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          new Thread() {
            public void run() {
              mitarbeiterWechseln();
            }
          }.start();
        }
      });
      JMenuItem mitarbeiterPasswortAendern = new JMenuItem("Mitarbeiterpasswort ändern");
      mitarbeiterPasswortAendern.setToolTipText("Ändern des Passwortes des aktuellen Mitarbeiters");
      mitarbeiterPasswortAendern.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {          
          new Thread() {
            public void run() {
              mitarbeiterPasswortAendern();
            }
          }.start();
        }
      });
      JMenuItem ausgabenButton = new JMenuItem("Ausgabe");
      ausgabenButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          new Thread() {
            public void run() {
              ausgabeStarten();
            }
          }.start();
        }
      });
      
      dateiMenue.add(mitarbeiterAendernButton);
      dateiMenue.add(mitarbeiterPasswortAendern);
      dateiMenue.addSeparator();
      dateiMenue.add(ausgabenButton);
      dateiMenue.addSeparator();
    }
    
    JMenuItem exitButton = new JMenuItem("Exit");
    exitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    
    dateiMenue.add(exitButton);
    
    return dateiMenue;
  }

  /**
   * Wird aufgerufen, wenn ein anderer Reiter gewählt wurde
   */
  public void refresh() {
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));    
    if (currentReiter != null) {
      currentReiter.focusLost();
    }
    if (reiter != null) {
      currentReiter = (MainReiter) reiter.getSelectedComponent();
      if (currentReiter != null) currentReiter.refresh();
      if (reiterMenus == null) 
        reiterMenus = new JMenu[reiter.getComponentCount()];

      for (int i=0; i < reiter.getComponentCount(); i++) {
        if (reiterMenus[i] != null) {
          reiterMenus[i].setVisible(i == reiter.getSelectedIndex());
        }        
      }      
    }
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }
  
  protected void addReiterMenues(JMenuBar menue) {
    reiterMenus = new JMenu[reiter.getComponentCount()];
    for (int i=0; i < reiter.getComponentCount(); i++) {
      MainReiter currentReiter = (MainReiter) reiter.getComponent(i);
      reiterMenus[i] = currentReiter.getMenu();
      if (reiterMenus[i] != null) {
        menue.add(reiterMenus[i]);
        reiterMenus[i].setVisible(false);
      }
    }    
  }
  
  protected void ausgabeStarten() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    new de.oberbrechen.koeb.gui.ausgaben.Main(false, currentMitarbeiter);
    setCursor(Cursor.getDefaultCursor());
  }  

  public void dispose() {
    super.dispose();
    AbstractMain.instanceCount--;
    if (AbstractMain.instanceCount <= 0) System.exit(0);
  }
  
  /**
   * Initialisiert nötige Daten
   */
  protected abstract void initDaten();

  /**
   * Wechselt den aktuell angemeldeten Mitarbeiter.
   * @return true gdw ein Wechsel erfolgte
   */
  protected boolean mitarbeiterWechseln() {
    if (mitarbeiterAuswahlDialog == null)
      mitarbeiterAuswahlDialog=new MitarbeiterAuswahlDialog(this);
    final Mitarbeiter gewaehlterMitarbeiter = mitarbeiterAuswahlDialog.waehleMitarbeiter(
        noetigeMitarbeiterBerechtigung);
    if (gewaehlterMitarbeiter != null) {
      setMitarbeiter(gewaehlterMitarbeiter);
    }
    return gewaehlterMitarbeiter != null;
  }

  /**
   * Fügt die Reiter hinzu. 
   */
  protected abstract void reiterHinzufuegen();

  /**
   * Setzt das aktuelle Look & Feel.
   */
  protected void setLookAndFeel() {
    String lookAndFeel = null; 
      try {
      
      EinstellungFactory einstellungFactory =
          Datenbank.getInstance().getEinstellungFactory();
      lookAndFeel = einstellungFactory.getClientEinstellung(
          "de.oberbrechen.koeb.gui",
          "LookAndFeel").getWert(
          "com.incors.plaf.kunststoff.KunststoffLookAndFeel");
      
      UIManager.setLookAndFeel(lookAndFeel);
      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      JOptionPane.showMessageDialog( this,
        "Das Look&Feel \""+lookAndFeel+"\" konnte nicht geladen werden!", "Ladefehler",
        JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Läd und setzt das Icon des Fensters.
   */
  protected void ladeIcon(String icon) {
    java.net.URL iconURL = ClassLoader.getSystemResource(
      icon);
    if (iconURL != null)
      this.setIconImage(getToolkit().getImage(iconURL));
    else {
       JOptionPane.showMessageDialog( this,
         "Das Icon '"+icon+"' konnte nicht "+
         "geladen werden!", "Ladefehler",
         JOptionPane.ERROR_MESSAGE);
    }

  }


  /**
   * Wechselt den aktuell angemeldeten Mitarbeiter
   * @param mitarbeiter der Mitarbeiter, zu dem gewechselt werden soll
   */
  public void setMitarbeiter(Mitarbeiter mitarbeiter) {
    currentMitarbeiter = mitarbeiter;
    Datenbank.getInstance().getMitarbeiterFactory().
      setAktuellenMitarbeiter(mitarbeiter);
  }

  /**
   * Erlaubt / verbietet Ändern der angezeigten Umgebung, d.h. es wird z.B. 
   * das Wechseln des Reiters verboten. Ein solches Verbot
   * ist nötig, damit während der Dateneingabe in einem unsauberen Zustand nicht
   * die aktuelle Eingabe verlassen werden kann.
   *
   * @param erlaubt bestimmt, ob Änderungen erlaubt sind
   */
  public void erlaubeAenderungen(boolean erlaubt) {
    erlaubeAenderungen = erlaubt;
    reiter.setEnabled(erlaubt);
    
    JMenuBar menuBar = getJMenuBar();
    if (menuBar != null) {
      int menueAnzahl = menuBar.getMenuCount();
      for (int i=0; i < menueAnzahl; i++) 
        menuBar.getMenu(i).setEnabled(erlaubt);
    }
  }

  /**
   * Führt einige Initialisierungen durch.
   */
  public static void init() {
    UIManager.put("OptionPane.yesButtonText", "Ja");
    UIManager.put("OptionPane.noButtonText", "Nein");
    UIManager.put("OptionPane.cancelButtonText", "Abbrechen");

    java.io.File file = new java.io.File("einstellungen.conf");
    KonfigurationsDatei.setStandardDatei(file);   
  }
  
  protected void jbInit(String titel) throws Exception {
    //allgemeinPanel bauen
    JPanel allgemeinPanel = getAllgemeinPanel();
    if (allgemeinPanel != null) allgemeinPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

    // Reiter bauen
    reiter = new JTabbedPane();
    reiter.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
    reiter.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        refresh();
      }
    });


    // Alles zusammenbauen
    this.setTitle(titel);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setLocale(new java.util.Locale("de", "DE", "EURO"));

    this.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        kontrolliereNeueGroesse();
      }
    });

    this.getContentPane().setLayout(new BorderLayout(10, 5));
    if (allgemeinPanel != null)
      this.getContentPane().add(allgemeinPanel, BorderLayout.NORTH);
    this.getContentPane().add(reiter, BorderLayout.CENTER);
  }

  /**
   * Liefert ein Panel, dass über den Reitern angezeigt wird und
   * das so eine Anpassung der GUI ermöglicht.
   * @return
   */
  protected JPanel getAllgemeinPanel() throws Exception {
    return null;
  }

  /**
   * Liefert den aktuell angemeldeten Mitarbeiter
   * @return den aktuell angemeldeten Mitarbeiter
   */
  public Mitarbeiter getAktuellenMitarbeiter() {
    return currentMitarbeiter;
  }

  /**
   * Zeigt das Dialogfeld zum Ändern des Mitarbeiterpasswortes an und speichert
   * das veränderte Passwort.
   */
  protected void mitarbeiterPasswortAendern() {
    Mitarbeiter aktuellerMitarbeiter = this.getAktuellenMitarbeiter();
    new PasswortAendernDialog(this).changeMitarbeiterPasswort(
      aktuellerMitarbeiter, true);
    try {
      aktuellerMitarbeiter.save();
    } catch (DatenbankzugriffException e) {
      ErrorHandler.getInstance().handleException(e, "Mitarbeiterpasswort " +
          "konnte nicht geändert werden!", false);
    }
  }

  /**
   * Überprüft nach einem Resize, ob das Fenster noch die Mindestgröße besitzt.
   * Ist dies nicht der Fall, wird es vergrößert.
   */
  protected void kontrolliereNeueGroesse() {
    if (minDimension == null) return;
    Dimension currentDimension = this.getSize();
    if (currentDimension.width < minDimension.width ||
        currentDimension.height < minDimension.height) {

      Dimension newDimension = new Dimension(
        Math.max(minDimension.width, currentDimension.width),
        Math.max(minDimension.height, currentDimension.height));

      this.setSize(newDimension);
    }
  }
  
  /**
   * Zeigt ein Dialogfeld an, über das Benutzer aktivert werden können.
   * Der Status der gewählten Benutzer wird auf aktiv gesetzt und eine
   * Liste der Benutzer zurückgeliefert.
   * @return eine Liste der aktivierten Benutzer.
   */
  protected BenutzerListe benutzerAktivieren() {
    try {
      JPanel panel = new JPanel(new BorderLayout(10, 10));
      BenutzerListenAuswahlPanel auswahlPanel = 
        new BenutzerListenAuswahlPanel(false, false, BenutzerListenAuswahlPanel.AUSWAHLTYP_PASSIVE_BENUTZER);
      panel.add(auswahlPanel, BorderLayout.CENTER);
      panel.add(new JLabel("Bitte wählen Sie die Benutzer aus, die " +
        "aktiviert werden sollen:"), BorderLayout.NORTH);
      auswahlPanel.sortiereNachSpalte(1, false);
      
      int erg = JOptionPane.showConfirmDialog(this, panel,
        "Benutzerauswahl", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
        
      if (erg != JOptionPane.OK_OPTION) return null; 
      
      final BenutzerListe benutzerListe = (BenutzerListe) auswahlPanel.getAuswahl();
      Runnable setzeAufAktiv = new Runnable() {
          public void run() {
            for (int i=0; i < benutzerListe.size(); i++) {
              Benutzer benutzer = (Benutzer) benutzerListe.get(i);
              try {
                benutzer.setAktiv(true);              
                benutzer.save();
              } catch (Exception e) {
                ErrorHandler.getInstance().handleException(e, "Benutzer "+benutzer.toDebugString()+" konnte nicht gespeichert werden!", false);
              }
            }
          }
        };
        Datenbank.getInstance().executeInTransaktion(setzeAufAktiv);
        return benutzerListe;
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim Aktivieren der Benutzer!", false);
      return null;
    }
  }  
  
  /**
   * Erstellt einen BarcodeReaderAdapter und richtet ihn für das MainFenster
   * und den übergebenen EventHandler ein.
   * @param eventHandler
   */
  protected void addBarcodeScanner(BarcodeGelesenEventHandler eventHandler) {
    BarcodeReaderAdapter barcodeReader;
    try {
      barcodeReader = (BarcodeReaderAdapter) Datenbank.getInstance().
        getEinstellungFactory().getClientEinstellung(
        BarcodeReaderAdapter.class.getName(), "instance").
        getWertObject(BarcodeReaderAdapter.class, null);
    } catch (UnpassendeEinstellungException e) {
      barcodeReader = null;
      //Sollte nie auftreten, wegen Parameter null vonm getWertObject
      ErrorHandler.getInstance().handleException(e, false);
    }
    
    if (barcodeReader != null)
      barcodeReader.setEventHandler(eventHandler);
      
    if (barcodeReader instanceof KeyAdapter) {
      
      //Für JDK 1.4
      addKeyListenerAlleComponenten(this, (KeyAdapter) barcodeReader);
      
      //Für JDK 1.3
      //this.addKeyListener((KeyAdapter) barcodeReader);
    }
  }  
}