package de.oberbrechen.koeb.datenbankzugriff;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import de.oberbrechen.koeb.framework.ErrorHandler;
import de.oberbrechen.koeb.framework.KonfigurationsDatei;


/**
 * Diese Klasse kapselt die grundlegenden Funktionalität für den Zugriff auf
 * die Datenbank. Eine Implementierung soll eine Verbindung mit der 
 * Datenbank (oder evtl. anderen Speicherformen) her
 * und diese in geeigneter Weise den Implementierungen zur Verfügung stellen.
 * Außerdem bietet sie grundlegende Funktionen zur Konvertierung der in der
 * Datenbank verwendeten Datentypen.<br>
 * Um möglichst einfachen Zugriff zu gewährleisten, ist diese Klasse
 * als Singleton realisiert. Um Unabhängigkeit von der verwendeten Datenbank
 * zu erreichen ist diese Klasse abstrakt und muss um die datenbankspezifischen
 * Teile (fast alles ;-) erweitert werden. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public abstract class Datenbank {

  // die statische Instanz für das Signleton
  private static Datenbank datenbankInstanz = null;
  private Vector<DatenbankzugriffFactory<?>> datenbankzugriffFactories;
  
  private AusleiheFactory ausleiheFactory;
  private BenutzerFactory benutzerFactory;
  private ClientFactory clientFactory;
  private EinstellungFactory einstellungFactory;
  private InternetfreigabeFactory internetfreigabeFactory;
  private MitarbeiterFactory mitarbeiterFactory;
  private MediumFactory mediumFactory;
  private MedientypFactory medientypFactory;
  private MahnungFactory mahnungFactory;
  private SystematikFactory systematikFactory;
  private VeranstaltungFactory veranstaltungFactory;
  private VeranstaltungsgruppeFactory veranstaltungsgruppeFactory;
  private VeranstaltungsteilnahmeFactory veranstaltungsteilnahmeFactory;
  private EANZuordnung eanZuordnung;
  private BemerkungVeranstaltungFactory bemerkungVeranstaltungFactory;
  
  public Datenbank() {
    datenbankzugriffFactories = new Vector<DatenbankzugriffFactory<?>>();
    
    benutzerFactory = null;
  }
  
  /**
   * Registriert eine neue DatenbankzugriffFactory 
   */
  public void registriereDatenbankzugriffFactory(
    DatenbankzugriffFactory<?> datenbankzugriffFactory) {
    datenbankzugriffFactories.add(datenbankzugriffFactory);    
  }
  
  /**
   * Löscht den Cache aller registrierten Factories
   */
  public synchronized void clearCache() {
    Iterator<DatenbankzugriffFactory<?>> it = datenbankzugriffFactories.iterator();
    while (it.hasNext()) {
      it.next().clearCache();  
    }
  }
  
  /**
   * Entsprechend dem Sigelton-Pattern liefert diese Methode eine Instanz der
   * <code>Datenbank</code>-Klasse.
   *
   * @return Instanz der <code>Datenbank</code>-Klasse
   */
  public static synchronized Datenbank getInstance() {
     if (datenbankInstanz == null) {
       KonfigurationsDatei confDatei =
        KonfigurationsDatei.getStandardKonfigurationsdatei();

       String datenbankFactoryClass = confDatei.getProperty("Datenbank-Factory",
         "de.oberbrechen.koeb.datenbankzugriff.mysql.MysqlDatenbankFactory");
       
       DatenbankFactory datenbankFactory = null;
       try {
         datenbankFactory = (DatenbankFactory) 
          Class.forName(datenbankFactoryClass).newInstance();
         datenbankInstanz = datenbankFactory.erstelleDatenbank();     
       } catch (Exception e) {
         ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Erstellen der DatenbankFactory '"+datenbankFactoryClass+          "'!", true);
       }
     } 
     return datenbankInstanz;
  }

  /**
   * Löscht die Instanz der Datenbank, alle Caches, etc.
   */
  public static synchronized void clear() {
     datenbankInstanz = null;
     System.gc();
  }

  /**
   * Führt das übergebene Runnable-Object in einer Transaktion aus, falls
   * die Datenbank Transaktionen unterstützt.
 * @throws SQLException
   */
  public void executeInTransaktion(Runnable runnable) throws SQLException {
    runnable.run();
  }
  
  /**
   * Erstellt eine neue BenutzerFactory
   * @return eine neue BenutzerFactory
   */
  protected abstract BenutzerFactory erstelleBenutzerFactory();
  
  /**
   * Liefert eine BenutzerFactory.
   * @return eine BenutzerFactory.
   */
  public synchronized BenutzerFactory getBenutzerFactory() {
    if (benutzerFactory == null)
      benutzerFactory = erstelleBenutzerFactory();
    
    return benutzerFactory;
  }

  /**
   * Erstellt eine neue MitarbeiterFactory
   * @return eine neue MitarbeiterFactory
   */
  protected abstract MitarbeiterFactory erstelleMitarbeiterFactory();
  
  /**
   * Liefert eine MitarbeiterFactory.
   * @return eine MitarbeiterFactory.
   */
  public synchronized MitarbeiterFactory getMitarbeiterFactory() {
    if (mitarbeiterFactory == null)
      mitarbeiterFactory = erstelleMitarbeiterFactory();
    
    return mitarbeiterFactory;
  }

  /**
   * Erstellt eine neue VeranstaltungFactory
   * @return eine neue VeranstaltungFactory
   */
  protected abstract VeranstaltungFactory erstelleVeranstaltungFactory();
  
  /**
   * Liefert eine VeranstaltungFactory.
   * @return eine VeranstaltungFactory.
   */
  public synchronized VeranstaltungFactory getVeranstaltungFactory() {
    if (veranstaltungFactory == null)
      veranstaltungFactory = erstelleVeranstaltungFactory();
    
    return veranstaltungFactory;
  }

  /**
   * Erstellt eine neue MediumFactory
   * @return eine neue MediumFactory
   */
  protected abstract MediumFactory erstelleMediumFactory();
  
  /**
   * Liefert eine MediumFactory.
   * @return eine MediumFactory.
   */
  public synchronized MediumFactory getMediumFactory() {
    if (mediumFactory == null)
      mediumFactory = erstelleMediumFactory();
    
    return mediumFactory;
  }

  /**
   * Erstellt eine neue AusleiheFactory
   * @return eine neue AusleiheFactory
   */
  protected abstract AusleiheFactory erstelleAusleiheFactory();
  
  /**
   * Liefert eine AusleiheFactory.
   * @return eine AusleiheFactory.
   */
  public synchronized AusleiheFactory getAusleiheFactory() {
    if (ausleiheFactory == null)
      ausleiheFactory = erstelleAusleiheFactory();
    
    return ausleiheFactory;
  }

  /**
   * Erstellt eine neue VeranstaltungsteilnahmeFactory
   * @return eine neue VeranstaltungsteilnahmeFactory
   */
  protected abstract VeranstaltungsteilnahmeFactory 
    erstelleVeranstaltungsteilnahmeFactory();
  
  /**
   * Liefert eine VeranstaltungsteilnahmeFactory.
   * @return eine VeranstaltungsteilnahmeFactory.
   */
  public synchronized VeranstaltungsteilnahmeFactory getVeranstaltungsteilnahmeFactory() {
    if (veranstaltungsteilnahmeFactory == null)
      veranstaltungsteilnahmeFactory = erstelleVeranstaltungsteilnahmeFactory();
    
    return veranstaltungsteilnahmeFactory;
  }

  /**
   * Erstellt eine neue EinstellungFactory
   * @return eine neue EinstellungFactory
   */
  protected abstract EinstellungFactory erstelleEinstellungFactory();
  
  /**
   * Liefert eine EinstellungFactory.
   * @return eine EinstellungFactory.
   */
  public synchronized EinstellungFactory getEinstellungFactory() {
    if (einstellungFactory == null)
      einstellungFactory = erstelleEinstellungFactory();
    
    return einstellungFactory;
  }

  /**
   * Erstellt eine neue MahnungFactory
   * @return eine neue MahnungFactory
   */
  protected abstract MahnungFactory erstelleMahnungFactory();
  
  /**
   * Liefert eine MahnungFactory.
   * @return eine MahnungFactory.
   */
  public synchronized MahnungFactory getMahnungFactory() {
    if (mahnungFactory == null)
      mahnungFactory = erstelleMahnungFactory();
    
    return mahnungFactory;
  }

  /**
   * Erstellt eine neue MedientypFactory
   * @return eine neue MedientypFactory
   */
  protected abstract MedientypFactory erstelleMedientypFactory();
  
  /**
   * Liefert eine MedientypFactory.
   * @return eine MedientypFactory.
   */
  public synchronized MedientypFactory getMedientypFactory() {
    if (medientypFactory == null)
      medientypFactory = erstelleMedientypFactory();
    
    return medientypFactory;
  }

  /**
   * Erstellt eine neue InternetfreigabeFactory
   * @return eine neue InternetfreigabeFactory
   */
  protected abstract InternetfreigabeFactory erstelleInternetfreigabeFactory();
  
  /**
   * Erstellt eine neue ClientFactory
   * @return eine neue ClientFactory
   */
  protected abstract ClientFactory erstelleClientFactory();
  
  /**
   * Liefert eine ClientFactory.
   * @return eine ClientFactory.
   */
  public synchronized ClientFactory getClientFactory() {
    if (clientFactory == null)
      clientFactory = erstelleClientFactory();
    
    return clientFactory;
  }

  /**
   * Erstellt eine neue SystematikFactory
   * @return eine neue SystematikFactory
   */
  protected abstract SystematikFactory erstelleSystematikFactory();
  
  /**
   * Liefert eine SystematikFactory.
   * @return eine SystematikFactory.
   */
  public synchronized SystematikFactory getSystematikFactory() {
    if (systematikFactory == null)
      systematikFactory = erstelleSystematikFactory();
    
    return systematikFactory;
  }

  /**
   * Erstellt eine neue VeranstaltungsgruppeFactory
   * @return eine neue VeranstaltungsgruppeFactory
   */
  protected abstract VeranstaltungsgruppeFactory 
    erstelleVeranstaltungsgruppeFactory();
  
  /**
   * Erstellt eine neue VeranstaltungbemerkungFactory
   * @return eine neue VeranstaltungbemerkungFactory
   */
  protected abstract BemerkungVeranstaltungFactory 
    erstelleBemerkungVeranstaltungFactory();

  /**
   * Liefert eine VeranstaltungsbemerkungFactory.
   * @return eine VeranstaltungsbemerkungFactory.
   */
  public synchronized BemerkungVeranstaltungFactory 
    getBemerkungVeranstaltungFactory() {

    if (bemerkungVeranstaltungFactory == null)
      bemerkungVeranstaltungFactory = erstelleBemerkungVeranstaltungFactory();
    
    return bemerkungVeranstaltungFactory;
  }

  
  /**
   * Liefert eine VeranstaltungsgruppeFactory.
   * @return eine VeranstaltungsgruppeFactory.
   */
  public synchronized VeranstaltungsgruppeFactory 
    getVeranstaltungsgruppeFactory() {

    if (veranstaltungsgruppeFactory == null)
      veranstaltungsgruppeFactory = erstelleVeranstaltungsgruppeFactory();
    
    return veranstaltungsgruppeFactory;
  }

  public synchronized InternetfreigabeFactory getInternetfreigabeFactory() {
    if (internetfreigabeFactory == null)
      internetfreigabeFactory = erstelleInternetfreigabeFactory();
    
    return internetfreigabeFactory;
  }

  /**
   * Erstellt eine neue EANZuordnung
   * @return eine neue EANZuordnung
   */
  protected abstract EANZuordnung erstelleEANZuordnung();
  
  /**
   * Liefert eine EANZuordnung
   * @return eine EANZuordnung
   */
  public synchronized EANZuordnung getEANZuordnung() {
    if (eanZuordnung == null)
      eanZuordnung = erstelleEANZuordnung();
    
    return eanZuordnung;
  }
}