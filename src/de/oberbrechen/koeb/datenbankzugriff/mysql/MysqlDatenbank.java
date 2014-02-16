package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AusleiheFactory;
import de.oberbrechen.koeb.datenbankzugriff.BemerkungVeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.BenutzerFactory;
import de.oberbrechen.koeb.datenbankzugriff.ClientFactory;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Datenbankzugriff;
import de.oberbrechen.koeb.datenbankzugriff.EANZuordnung;
import de.oberbrechen.koeb.datenbankzugriff.EinstellungFactory;
import de.oberbrechen.koeb.datenbankzugriff.InternetfreigabeFactory;
import de.oberbrechen.koeb.datenbankzugriff.MahnungFactory;
import de.oberbrechen.koeb.datenbankzugriff.MedientypFactory;
import de.oberbrechen.koeb.datenbankzugriff.MediumFactory;
import de.oberbrechen.koeb.datenbankzugriff.MitarbeiterFactory;
import de.oberbrechen.koeb.datenbankzugriff.SystematikFactory;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungFactory;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungsgruppeFactory;
import de.oberbrechen.koeb.datenbankzugriff.VeranstaltungsteilnahmeFactory;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Diese Klasse stellt eine Verbindung mit einer MySQL-Datenbank her. Neben
 * den normalen Datenbank-Funktionen, werden daher auch Funktionen angeboten,
 * um eine Verbindung mit der Datenbank zu erhalten und Statements auf diesen.
 * Außerdem werden Methoden für Transaktionen bereitgestellt. Statements und
 * Connections werden dabei geteilt. Es ist aber nötig, dass paralle Threads 
 * sich nicht gegenseitig beeinflussen können. Deshalb erhält jeder Thread
 * eine feste Connection zugewiesen. Bei der Benutzung ist jedoch zu beachten,
 * dass keine Statements über Threadgrenzen hinweg weitergegeben werden dürfen, 
 * da Statements fest an eine Connection gebunden
 * sind. Außerdem ist auf allen Connections AutoCommit ausgeschaltet. Daher
 * sollte nur mit Transaktionen gearbeitet werden. Verschaltelte Transaktionen
 * werden so interpretiert, dass zwar keine neue Transaktion begonnen, wird,
 * aber im Folgenden zwei Beende-Befehle nötig sind. Aufgrund dieses Verhaltens
 * muss extrem vorsichtig mit Aborts umgegangen werden.
 *  
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class MysqlDatenbank extends Datenbank {
  // die statische Instanz für das Signleton
  private static MysqlDatenbank datenbankInstanz = null;
  
  static SimpleDateFormat sqldateFormat =
    new SimpleDateFormat("yyyy-MM-dd");
  
  private int[] transaktionstiefe;
    
  // die Threads, die Verbindungen anforderten und deren Anzahl
  private Thread[] threads;
  private int threadCount;
  
  // die Verbindung mit der Datenbank
  private Connection[] verbindungen;

  // Queue in der verfügbare Statements gespeichert werden
  private LinkedList<Statement>[] cache;

  //Daten um neue Connections zu erzeugen
  private String connectionStatement;
  private String configString;

  
  /**
   * Liefert ein verfügbares Statement zurück.
   * Die Erzeugung neuer <code>Statement</code>-Objekte ist aufwendig. Daher
   * werden vorhandene Objekte wiederverwendet. Die Methode <code>getStatemet
   * </code> liefert ein verfügbares, nicht aber notwendigerweise neu erzeugtes
   * Statement.
   *
   * @return verfügbares Statement
   * @see #releaseStatement
   */
  public Statement getStatement() {
    int threadID = getThreadID();
     
    if (cache[threadID].isEmpty()) {
      try {
        return (Statement) verbindungen[threadID].createStatement();
      } catch (Exception e) {
        ErrorHandler.getInstance().handleException(e, 
          "Fehler beim Erzeugen eines neuen Statements!", true);
      }
      return null;
    } else {
      return (Statement) cache[threadID].removeFirst();
    }
  }

 /**
   * Gibt ein Statement zur Verwendung frei.
   * Die Erzeugung neuer <code>Statement</code>-Objekte ist aufwendig. Daher
   * werden vorhandene Objekte wiederverwendet. Die Methode
   * <code>releaseStatemet</code> markiert das übergebene Statement als
   * verfügbar.
   *
   * @param statement das freizugebende Statement
   * @see #getStatement
   */
  public synchronized void releaseStatement(Statement statement) {
    int threadID = getThreadID();
    cache[threadID].addLast(statement);
  }


  /**
   * Liefert ein <code>Connection</code>-Objekt, das zur geöffneten Datenbank
   * gehört.
   *
   * @return Connection-Objekt
   */
  public Connection getConnection() {
    int threadID = getThreadID(); 
    return verbindungen[threadID];
  }


  /**
   * Entsprechend dem Sigelton-Pattern liefert diese Methode eine Instanz der
   * <code>Datenbank</code>-Klasse.
   *
   * @return Instanz der <code>Datenbank</code>-Klasse
   */
  public static synchronized MysqlDatenbank getMysqlInstance() {
    if (datenbankInstanz == null) { 
      Datenbank datenbank = Datenbank.getInstance();
      if (datenbank instanceof MysqlDatenbank) {
        datenbankInstanz = (MysqlDatenbank) datenbank;
      } else {
        MysqlDatenbankFactory mysqlDatenbankFactory =
          new MysqlDatenbankFactory();
        datenbankInstanz = (MysqlDatenbank) 
          mysqlDatenbankFactory.erstelleDatenbank();
      }
    }
    return datenbankInstanz;
  }

  @SuppressWarnings("unchecked")
  public MysqlDatenbank(String host, String port, String datenbankname,
    String user, String passwort) {

    
    try {
      configString =  "Host:          "+host+"\n";
      configString += "Port:          "+port+"\n";
      configString += "Datenbankname: "+datenbankname+"\n";
      configString += "User:          "+user+"\n";
      configString += "Passwort:      "+passwort;

      Class.forName("org.gjt.mm.mysql.Driver");
      
      connectionStatement = "jdbc:mysql://"+
      host+":"+port+"/"+datenbankname+"?user="+user+"&password="+passwort+"&jdbcCompliantTruncation=false";
      
      ;
    } catch (Exception e) {
      ErrorHandler.getInstance().handleException(e, "Verbinden mit Datenbank schlug fehl! Folgende "+
       "Einstellungen wurden verwendet:\n\n"+configString, true);
    }

    verbindungen = new Connection[5];
    cache = new LinkedList[5];
    transaktionstiefe = new int[5];
    threads = new Thread[5];
    threadCount = 0;
  }

  /**
   * Erstellt eine neue Connection mit der Datenbank und liefert diese zurück.
   * @return die neu erstellte Connection
   */
  public synchronized Connection createConnection() {
    Connection connection = null;
    try {
      connection =
        (Connection) DriverManager.getConnection(connectionStatement);
      connection.setUseUnicode(true);
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Verbinden mit Datenbank schlug fehl! Folgende "+
          "Einstellungen wurden verwendet:\n\n"+configString, true);
    }
    return connection;
  }
  
  @SuppressWarnings("unchecked")
  private synchronized int getThreadID() {
    //zur Zeit benutzten Eintrag suchen
    Thread currentThread = Thread.currentThread();       
    for (int i = 0; i < threadCount; i++) {
      if (threads[i] == currentThread) return i;
    }
        
    //alten Eintrag evtl. wiederverwenden
    for (int i = 0; i < threadCount; i++) {
      if (!threads[i].isAlive()) {
        threads[i] = currentThread;
        if (transaktionstiefe[i] != 0) {
          ErrorHandler.getInstance().handleError("Transaktionstiefe nicht 1!", false);
          try {
            verbindungen[i].rollback();
          } catch (SQLException e) {
            ErrorHandler.getInstance().handleException(e, true);
          }
          transaktionstiefe[i] = 0;
        }
        return i;
      }
    }
    
     
    //neuen Eintrag erstellen
    threadCount++;
    
    //Platz schaffen, falls keiner Verfügbar ist
    if (threadCount > threads.length) {
      int neueArrayGroesse = threads.length*2;
      
      Connection[] neuVerbindungen = new Connection[neueArrayGroesse];
      LinkedList<Statement>[] neuCache = new LinkedList[neueArrayGroesse];
      Thread[] neuThreads = new Thread[neueArrayGroesse];
      int[] neuTransaktionstiefe = new int[neueArrayGroesse];
      
      for (int i = 0; i < threads.length; i++) {
        neuVerbindungen[i] = verbindungen[i];
        neuCache[i] = cache[i];
        neuThreads[i] = threads[i];
        neuTransaktionstiefe[i] = transaktionstiefe[i];
      }
      
      verbindungen = neuVerbindungen;
      cache = neuCache;
      threads = neuThreads;
      transaktionstiefe = neuTransaktionstiefe;
    }
    
    int neuID = threadCount-1;
    try {
      verbindungen[neuID] = createConnection();
      verbindungen[neuID].setAutoCommit(false);
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Verbinden mit Datenbank schlug fehl! Folgende "+
          "Einstellungen wurden verwendet:\n\n"+configString, true);
    }
    transaktionstiefe[neuID] = 0;
    cache[neuID] = new LinkedList<Statement>();
    threads[neuID] = currentThread;
    
    return neuID;
  }
  
  /**
   * Beginnt eine Transaktion, d.h. das AutoCommit wird auf der Connection, 
   * die mittels getConnection abgefragt werden kann auf false gesetzt. 
   * Außerdem wird der Zähler transaktionstiefe um eins erhöht.
   */
  public synchronized void beginTransaktion() {
    int threadID = getThreadID();       
    transaktionstiefe[threadID]++;
  }
  
  /**
   * Beendet eine Transaktion, d.h. das AutoCommit wird auf der Connection, 
   * die mittels getConnection abgefragt werden kann auf true gesetzt und
   * ein commit ausgeführt. Außerdem wird der Zähler transaktionstiefe 
   * um eins erniedrigt.
   */
  public synchronized void endTransaktion() throws SQLException {
    int threadID = getThreadID();
    
    if (transaktionstiefe[threadID] <= 0)
      throw new IllegalStateException();
    transaktionstiefe[threadID]--;
    if (transaktionstiefe[threadID] == 0) {
      verbindungen[threadID].commit();
    }
  }

  protected BenutzerFactory erstelleBenutzerFactory() {
    return new MysqlBenutzerFactory();
  }

  protected MitarbeiterFactory erstelleMitarbeiterFactory() {
    return new MysqlMitarbeiterFactory();
  }

  protected VeranstaltungFactory erstelleVeranstaltungFactory() {
    return new MysqlVeranstaltungFactory();
  }

  protected MediumFactory erstelleMediumFactory() {
    return new MysqlMediumFactory();
  }

  protected AusleiheFactory erstelleAusleiheFactory() {
    return new MysqlAusleiheFactory();
  }

  protected VeranstaltungsteilnahmeFactory erstelleVeranstaltungsteilnahmeFactory() {
    return new MysqlVeranstaltungsteilnahmeFactory();
  }

  protected EinstellungFactory erstelleEinstellungFactory() {
    return new MysqlEinstellungFactory();
  }

  protected MahnungFactory erstelleMahnungFactory() {
    return new MysqlMahnungFactory();
  }

  protected MedientypFactory erstelleMedientypFactory() {
    return new MysqlMedientypFactory();
  }

  protected InternetfreigabeFactory erstelleInternetfreigabeFactory() {
    return new MysqlInternetfreigabeFactory();
  }

  protected ClientFactory erstelleClientFactory() {
    return new MysqlClientFactory();
  }

  protected SystematikFactory erstelleSystematikFactory() {
    return new MysqlSystematikFactory();
  }

  protected EANZuordnung erstelleEANZuordnung() {
    return new MysqlEANZuordnung();
  }

  protected VeranstaltungsgruppeFactory erstelleVeranstaltungsgruppeFactory() {
    return new MysqlVeranstaltungsgruppeFactory();
  }

  protected BemerkungVeranstaltungFactory erstelleBemerkungVeranstaltungFactory() {
    return new MysqlBemerkungVeranstaltungFactory();
  }

  /**
   * Eine Hilfsfunktion zum Setzen der Id von Datenbankzugriffsobjecten in
   * Prepared-Statements.
   * @param i die Parameternr
   * @param object das zu setzende Object
   * @param statement das Statement
   */
  public static void setIdInPreparedStatement(int i, Datenbankzugriff object, PreparedStatement statement) throws SQLException {
    if (object == null) {
      statement.setString(i, null);
    } else {
      statement.setInt(i, object.getId());        
    }        
  }
  
  public void executeInTransaktion(Runnable runnable) throws SQLException {
    beginTransaktion();
    runnable.run();
    endTransaktion();
  }  
}