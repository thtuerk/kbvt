package de.oberbrechen.koeb.datenbankzugriff.mysql.datenbankupdate;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.mysql.MysqlDatenbank;
import de.oberbrechen.koeb.framework.ErrorHandler;


/**
 * Diese Klasse dient dazu die Datenbank von Version 1 des 
 * Datenbankschemas in Version 2 zu überführen.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Datenbankupdate_1_nach_2 {
 
  MysqlDatenbank mysqlDatenbank;
  Connection connection;
  
  public Datenbankupdate_1_nach_2() {
    mysqlDatenbank = MysqlDatenbank.getMysqlInstance();
    connection = mysqlDatenbank.getConnection();
  }
  
  /**
   * Konvertiert die Datenbank.
   * @param von die vorhandene Version
   * @param nach die herzustellende Version
   */
  public void convertDatenbank() {
    try {
      System.out.println("Konvertiere Datenbank von Version 1 nach Version 2");
      convertTableType();

      mysqlDatenbank.beginTransaktion();
      convertBenutzer();
      convertMitarbeiter();
      convertClient();
      convertMedientyp();
      convertInternetZugang();
      convertVeranstaltungsgruppe();
      convertVeranstaltung();
      convertTermin();
      convertBenutzerBesuchtVeranstaltung();
      convertMedium();
      convertEinstellung();

      convertSystematik();
      convertSystematikSpezialisiertEingabe();
      convertMediumGehoertZuSystematik();

      convertAusleihe();

      mysqlDatenbank.endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, "Fehler beim " +        "Konvertieren der Datenbank!", true);
    }
  }

  private void convertTableType() throws SQLException {
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table ausleihe type=InnoDB");    
    statement.execute("alter table benutzer type=InnoDB");    
    statement.execute("alter table benutzer_besucht_veranstaltung type=InnoDB");
    statement.execute("alter table client type=InnoDB");
    statement.execute("alter table einstellung type=InnoDB");
    statement.execute("alter table internet_zugang type=InnoDB");
    statement.execute("alter table medientyp type=InnoDB");
    statement.execute("alter table medium type=InnoDB");
    statement.execute("alter table medium_gehoert_zu_systematik type=InnoDB");
    statement.execute("alter table mitarbeiter type=InnoDB");
    statement.execute("alter table systematik type=InnoDB");
    statement.execute("drop table systematik_spezialisiert");
    statement.execute("alter table systematik_spezialisiert_eingabe type=InnoDB");
    statement.execute("alter table termin type=InnoDB");
    statement.execute("alter table veranstaltung type=InnoDB");
    statement.execute("alter table veranstaltungsgruppe type=InnoDB");
  }

  /**
   * Konvertiert die Benutzertabelle.
   */
  private void convertBenutzer() throws SQLException {
    System.out.println("   - Benutzer");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table benutzer change Nr ID int(11) not null auto_increment");
    statement.execute("alter table benutzer add VIP bool not null");
  }   

  /**
   * Konvertiert die Benutzertabelle. Vorher muss Benutzer konvertiert sein.
   */
  private void convertMitarbeiter() throws SQLException {
    System.out.println("   - Mitarbeiter");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table mitarbeiter change Nr ID int(11) not null auto_increment");
    statement.execute("alter table mitarbeiter change Benutzernr BenutzerID int(11)");

    //Teste Integrität der Mitarbeiter
    String fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select benutzerID from mitarbeiter left outer join benutzer as b on b.id = benutzerID where isNull(b.id) group by benutzerID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Mitarbeiter-Tabelle entdeckt. " +
          "Die folgenden Benutzer-IDs sind ungültig: "+fehlendeNummern, true);

    statement.execute("alter table mitarbeiter add foreign key benutzerID (benutzerID) references benutzer(id) " +
        "on delete cascade on update restrict");
  }  

  private void convertClient() throws SQLException {
    System.out.println("   - Client");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table client change Nr ID int(11) not null auto_increment");
  }  

  //vorher convertVeranstaltung nötig
  private void convertBenutzerBesuchtVeranstaltung() throws SQLException {
    System.out.println("   - benutzer_besucht_veranstaltung");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table benutzer_besucht_veranstaltung change Nr ID int(11) not null auto_increment");
    statement.execute("alter table benutzer_besucht_veranstaltung change Benutzernr BenutzerID int(11) not null");
    statement.execute("alter table benutzer_besucht_veranstaltung change Veranstaltungsnr VeranstaltungID int(11) not null");
    statement.execute("create index veranstaltung on benutzer_besucht_veranstaltung (VeranstaltungID)");
    statement.execute("create index benutzer on benutzer_besucht_veranstaltung (BenutzerID)");


    //teste, ob alle Benutzer vorhanden sind
    String fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select benutzerID from benutzer_besucht_veranstaltung left outer join benutzer as b on b.id = benutzerID where isNull(b.id) group by benutzerID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in benutzer_besucht_veranstaltung entdeckt. " +
          "Die folgenden Benutzer-IDs sind ungültig: "+fehlendeNummern, true);

    statement.execute("alter table benutzer_besucht_veranstaltung add foreign key " +
        "benutzerID (benutzerID) references benutzer(id) " +
        "on delete restrict on update restrict");  
    
    fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select veranstaltungID from benutzer_besucht_veranstaltung left outer join veranstaltung as v on v.id = veranstaltungID where isNull(v.id) group by veranstaltungID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in benutzer_besucht_veranstaltung entdeckt. " +
          "Die folgenden Veranstaltung-IDs sind ungültig: "+fehlendeNummern, true);    
    statement.execute("alter table benutzer_besucht_veranstaltung add foreign key " +
        "veranstaltungID (veranstaltungID) references veranstaltung(id) " +
        "on delete restrict on update restrict");  
  }  

  //vorher convertClient und convertMitarbeiter nötig
  private void convertEinstellung() throws SQLException {
    System.out.println("   - Einstellung");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table einstellung change Nr ID int(11) not null auto_increment");
    statement.execute("alter table einstellung change Client ClientID int(11)");
    statement.execute("alter table einstellung change Mitarbeiter MitarbeiterID int(11)");

    String fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select clientID from einstellung left outer join client as c on c.id = clientID where isNull(c.id) group by clientID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Tabelle Einstellung entdeckt. " +
          "Die folgenden Client-IDs sind ungültig: "+fehlendeNummern, true);    
    statement.execute("create index client on einstellung (clientID)");
    statement.execute("alter table einstellung add foreign key " +
        "clientID (clientID) references client(id) " +
        "on delete cascade on update restrict");    

    fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select mitarbeiterID from einstellung left outer join mitarbeiter as m on m.id = mitarbeiterID where isNull(m.id) group by mitarbeiterID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Tabelle Einstellung entdeckt. " +
          "Die folgenden Mitarbeiter-IDs sind ungültig: "+fehlendeNummern, true);    
    statement.execute("create index mitarbeiter on einstellung (mitarbeiterID)");
    statement.execute("alter table einstellung add foreign key " +
        "mitarbeiterID (mitarbeiterID) references mitarbeiter(id) " +
        "on delete cascade on update restrict");      
  }  

  private void convertInternetZugang() throws SQLException {
    System.out.println("   - Internetzugang");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table internet_zugang change Nr ID int(11) not null auto_increment");
    statement.execute("alter table internet_zugang change Benutzer BenutzerID int(11)");
    statement.execute("alter table internet_zugang change Client ClientID int(11)");
    statement.execute("alter table internet_zugang change Mitarbeiter MitarbeiterID int(11)");

  
    String fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select benutzerID from internet_zugang left outer join benutzer as b on b.id = benutzerID where isNull(b.id) group by benutzerID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Tabelle Internet-Zugang entdeckt. " +
          "Die folgenden Benutzer-IDs sind ungültig: "+fehlendeNummern, true);

    statement.execute("create index benutzer on internet_zugang (benutzerID)");
    statement.execute("alter table internet_zugang add foreign key " +
        "benutzerID (benutzerID) references benutzer(id) " +
        "on delete restrict on update restrict");  

    fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select clientID from internet_zugang left outer join client as c on c.id = clientID where isNull(c.id) group by clientID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Tabelle Internet-Zugang entdeckt. " +
          "Die folgenden Client-IDs sind ungültig: "+fehlendeNummern, true);    
    statement.execute("create index client on internet_zugang (clientID)");
    statement.execute("alter table internet_zugang add foreign key " +
        "clientID (clientID) references client(id) " +
        "on delete restrict on update restrict");    

    fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select mitarbeiterID from internet_zugang left outer join mitarbeiter as m on m.id = mitarbeiterID where isNull(m.id) group by mitarbeiterID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Tabelle Internet-Zugang entdeckt. " +
          "Die folgenden Mitarbeiter-IDs sind ungültig: "+fehlendeNummern, true);    
    statement.execute("create index mitarbeiter on internet_zugang (mitarbeiterID)");
    statement.execute("alter table internet_zugang add foreign key " +
        "mitarbeiterID (mitarbeiterID) references mitarbeiter(id) " +
        "on delete restrict on update restrict");      

  }  

  //vorher convertVeranstaltung nötig
  private void convertTermin() throws SQLException {
    System.out.println("   - Termin");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table termin change Nr ID int(11) not null auto_increment");
    statement.execute("alter table termin change Veranstaltung VeranstaltungID int(11)");

    String fehlendeNummern = resultToString((ResultSet) statement.executeQuery(
       "select VeranstaltungID from termin left outer join veranstaltung as v on v.id = veranstaltungID where isNull(v.id) group by veranstaltungID"));
    if (fehlendeNummern != null)
      ErrorHandler.getInstance().handleError("Inkonsistenz in Tabelle Termin entdeckt. " +
          "Die folgenden Mitarbeiter-IDs sind ungültig: "+fehlendeNummern, true);    
    statement.execute("create index veranstaltung on termin (veranstaltungID)");
    statement.execute("alter table termin add foreign key " +
        "veranstaltungID (veranstaltungID) references veranstaltung(id) " +
        "on delete restrict on update restrict");      
  }  

  private void convertVeranstaltungsgruppe() throws SQLException {
    System.out.println("   - Veranstaltungsgruppe");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table veranstaltungsgruppe drop primary key");
    statement.execute("alter table veranstaltungsgruppe add ID int(11) not null auto_increment PRIMARY KEY");
  }
  
  private void convertMedientyp() throws SQLException {
    System.out.println("   - Medientyp");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table medientyp drop primary key");
    statement.execute("alter table medientyp add ID int(11) not null auto_increment PRIMARY KEY");
    statement.execute("alter table medientyp change Plural Plural varchar(40)");
  }

  //Vorher convertMedientyp erforderlich
  private void convertMedium() throws SQLException {  
    System.out.println("   - Medium");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table medium drop primary key");
    statement.execute("alter table medium add ID int(11) not null auto_increment PRIMARY KEY");
    statement.execute("alter table medium add medientypID int(11)");
    statement.execute("alter table medium add ISBN varchar(10)");
    
    ResultSet result = 
      (ResultSet) statement.executeQuery("select id, name from medientyp");
    while (result.next()) {
      int id = result.getInt("id");
      String name = result.getString("name");
      Statement updateStatement = mysqlDatenbank.getStatement();
      updateStatement.execute("update medium set medientypID = "+id+" where medientyp=\""+name+"\"");                    
    }
    statement.execute("alter table medium drop medientyp");

    //Tabelle MedienEAN bauen
    statement.execute("CREATE TABLE medium_ean ("+
        "mediumID int(11) default NULL,"+
        "EAN varchar(13) not null default '',"+
        "PRIMARY KEY (EAN)"+
        ") TYPE=InnoDB;");
    statement.execute("create index medium on medium_ean(mediumID)");
    statement.execute("alter table medium_ean add foreign key " +
        "mediumID (mediumID) references medium(id) " +
        "on delete cascade on update restrict");      
    statement.execute("insert into medium_ean select id, EAN from medium where !isNull(EAN)");
    statement.execute("alter table medium drop ean");    
        
    statement.execute("create index medientyp on medium (medientypID)");
    statement.execute("create index isbn on medium (ISBN)");
    statement.execute("alter table medium add foreign key " +
        "medientypID (medientypID) references medientyp(id) " +
        "on delete restrict on update restrict");      

    statement.execute("create index aus_Bestand_entfernt on medium (aus_Bestand_entfernt)");
  }

  //Vorher convertVeranstaltungsgruppe erforderlich
  private void convertVeranstaltung() throws SQLException {  
    System.out.println("   - Veranstaltung");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table veranstaltung change Nr ID int(11) not null auto_increment");
    statement.execute("alter table veranstaltung add VeranstaltungsgruppeID int(11)");
    
    ResultSet result = 
      (ResultSet) statement.executeQuery("select id, name from veranstaltungsgruppe");
    while (result.next()) {
      int id = result.getInt("id");
      String name = result.getString("name");
      Statement updateStatement = mysqlDatenbank.getStatement();
      updateStatement.execute("update veranstaltung set veranstaltungsgruppeID = "+id+" where veranstaltungsgruppe=\""+name+"\"");                    
    }
    statement.execute("alter table veranstaltung drop veranstaltungsgruppe");
    statement.execute("create index veranstaltungsgruppe on veranstaltung (VeranstaltungsgruppeID)");
    statement.execute("alter table veranstaltung add foreign key " +
        "gruppeID (veranstaltungsgruppeID) references veranstaltungsgruppe(id) " +
        "on delete restrict on update restrict");      
  }

  private void convertSystematik() throws SQLException {  
    System.out.println("   - Systematik");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table systematik drop primary key");
    statement.execute("alter table systematik add ID int(11) not null auto_increment PRIMARY KEY");
    statement.execute("alter table systematik add SpezialisiertID int(11) default NULL");
    statement.execute("alter table systematik change Systematik Name varchar(50)");
    statement.execute("create index spezialisiertKey on systematik (spezialisiertID)");
    statement.execute("alter table systematik add foreign key " +
        "spezialisiertID (spezialisiertID) references systematik(id) " +
    "on delete restrict on update restrict");     
  }

  private void convertSystematikSpezialisiertEingabe() throws SQLException {  
    System.out.println("   - Systematik_Spezialisiert_Eingabe");

    Statement statement = mysqlDatenbank.getStatement();  
    ResultSet result = 
      (ResultSet) statement.executeQuery("select s1.id, s2.id from "+
        "systematik_spezialisiert_eingabe as t, systematik as s1, systematik as s2 where s1.name = t.Systematik AND s2.name = t.spezialisiert");
    while (result.next()) {
      int id = result.getInt(1);
      int spezialisiertID = result.getInt(2);
      Statement updateStatement = mysqlDatenbank.getStatement();
      updateStatement.execute("update systematik set spezialisiertID = "+
          spezialisiertID+" where id="+id);                    
    }
    statement.execute("drop table systematik_spezialisiert_eingabe");   
  }

  private void convertMediumGehoertZuSystematik() throws SQLException {  
    System.out.println("   - Medium gehört zu Systematik");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table medium_gehoert_zu_systematik rename temp");
    statement.execute("CREATE TABLE medium_gehoert_zu_systematik ("+
        "MediumID int(11) not null default 0,"+
        "SystematikID int(11) not null default 0,"+
        "KEY systematik(SystematikID),"+
        "KEY medium (MediumID),"+
        "PRIMARY KEY  (mediumID, systematikID)"+
        ") TYPE=InnoDB;");
    statement.execute("alter table medium_gehoert_zu_systematik add foreign key " +
        "mediumID (mediumID) references medium(id) " +
        "on delete cascade on update restrict");      
    statement.execute("alter table medium_gehoert_zu_systematik add foreign key " +
        "systematikID (systematikID) references systematik(id) " +
        "on delete restrict on update restrict");      
    statement.execute("insert into medium_gehoert_zu_systematik (SystematikID, MediumID) select s.id, m.id from "+
         "temp as t, systematik as s, medium as m where s.name = t.Systematik AND m.nr = t.medium");
    statement.execute("drop table temp");
  }

  private void createAusleiheTables() throws SQLException {
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("CREATE TABLE ausleihe ( "+
        "ID int(11) NOT NULL auto_increment,"+
        "MediumID int(11) default NULL,"+
        "BenutzerID int(11) default NULL,"+
        "RueckgabeDatum date default NULL,"+
        "MitarbeiterIDRueckgabe int(11) default NULL,"+
        "Bemerkungen text,"+
        "KEY medium(MediumID),"+
        "KEY benutzer(BenutzerID),"+
        "KEY mitarbeiter(MitarbeiterIDRueckgabe),"+
        "PRIMARY KEY (ID)"+
      ") TYPE=InnoDB;");
    statement.execute("alter table ausleihe add foreign key " +
        "mediumID (mediumID) references medium(id) " +
        "on delete restrict on update restrict");      
    statement.execute("alter table ausleihe add foreign key " +
        "benutzerID (benutzerID) references benutzer(id) " +
        "on delete restrict on update restrict");      
    statement.execute("alter table ausleihe add foreign key " +
        "mitarbeiterIDRueckgabe (mitarbeiterIDRueckgabe) references mitarbeiter(id) " +
        "on delete restrict on update restrict");      

    statement.execute("CREATE TABLE ausleihzeitraum ( "+
        "AusleiheID int(11) default NULL,"+
        "MitarbeiterID int(11) default NULL,"+
        "Beginn date default NULL,"+
        "Ende date default NULL,"+
        "TaetigungsDatum date default NULL,"+
        "KEY ausleihe(AusleiheID),"+
        "KEY mitarbeiter(MitarbeiterID)"+
      ") TYPE=InnoDB;");
    statement.execute("alter table ausleihzeitraum add foreign key " +
        "mitarbeiterID (mitarbeiterID) references mitarbeiter(id) " +
        "on delete restrict on update restrict");      
    statement.execute("alter table ausleihzeitraum add foreign key " +
        "ausleiheID (ausleiheID) references ausleihe(id) " +
        "on delete restrict on update restrict");          
  }
  
  private int insertAusleihe(ResultSet result) throws SQLException {
    PreparedStatement insertStatement = (PreparedStatement) connection.prepareStatement(
            "insert into ausleihe (MediumID, BenutzerID, Bemerkungen)" +
            "VALUES (?,?,?)");

    String medium = result.getString("Medium");
    int benutzerID = result.getInt("Benutzer");
    String bemerkungen = result.getString("Bemerkungen");

    int mediumID = -1;
    Statement mediumStatement = mysqlDatenbank.getStatement();
    ResultSet mediumResult = (ResultSet) mediumStatement.executeQuery(
        "select id from medium where nr = \""+medium+"\"");
    if (mediumResult.next()) mediumID = mediumResult.getInt(1);
        
    if (mediumID != -1) {
      insertStatement.setInt(1, mediumID);
    } else {
      insertStatement.setString(1, null);         
    }
    insertStatement.setInt(2, benutzerID);
    insertStatement.setString(3, bemerkungen);    
    insertStatement.execute();

    ResultSet keyResult = (ResultSet) insertStatement.getGeneratedKeys();
    keyResult.next();
    return keyResult.getInt(1);    
  }

  private void insertZeitraum(int ausleiheID, ResultSet result) throws SQLException {
    PreparedStatement insertZeitraumStatement = (PreparedStatement) connection.prepareStatement(
            "insert into ausleihzeitraum (AusleiheID, MitarbeiterID, Beginn, Ende, TaetigungsDatum)" +
            "VALUES (?,?,?,?,?)");
    insertZeitraumStatement.setInt(1, ausleiheID);
    insertZeitraumStatement.setInt(2, result.getInt("MitarbeiterAusleihe"));
    insertZeitraumStatement.setDate(3, result.getDate("AusleihDatum"));
    insertZeitraumStatement.setDate(4, result.getDate("SollrueckgabeDatum"));
    insertZeitraumStatement.setDate(5, result.getDate("AusleihDatum"));
    insertZeitraumStatement.execute();
  }

  private ResultSet findPreviousZeitraum(String medium, int benutzer, ResultSet result) throws SQLException {
    PreparedStatement selectVerlaengerungStatement = (PreparedStatement) connection.prepareStatement(
        "select * from temp where " +
        "RueckgabeDatum = ? and Benutzer = ? and Medium = ? and Nr != ?");
    selectVerlaengerungStatement.setDate(1, result.getDate("AusleihDatum"));
    selectVerlaengerungStatement.setInt(2, benutzer);
    selectVerlaengerungStatement.setString(3, medium);
    selectVerlaengerungStatement.setInt(4, result.getInt("Nr"));
    return (ResultSet) selectVerlaengerungStatement.executeQuery();    
  }
  
  private ResultSet findNextZeitraum(String medium, int benutzer, ResultSet result) throws SQLException {
    PreparedStatement selectVerlaengerungStatement = (PreparedStatement) connection.prepareStatement(
        "select * from temp where " +
        "AusleihDatum = ? and Benutzer = ? and Medium = ? and Nr != ?");
    selectVerlaengerungStatement.setDate(1, result.getDate("RueckgabeDatum"));
    selectVerlaengerungStatement.setInt(2, benutzer);
    selectVerlaengerungStatement.setString(3, medium);
    selectVerlaengerungStatement.setInt(4, result.getInt("Nr"));
    return (ResultSet) selectVerlaengerungStatement.executeQuery();    
  }

  private void updateAusleihe(int ausleiheID, ResultSet result) throws SQLException {
    PreparedStatement updateStatement = (PreparedStatement) connection.prepareStatement(
            "update ausleihe set MitarbeiterIDRueckgabe=?, RueckgabeDatum=? where id = ?");
    int mitarbeiterID = result.getInt("MitarbeiterRueckgabe");
    if (mitarbeiterID != 0) {
      updateStatement.setInt(1, mitarbeiterID);
    } else {
      updateStatement.setString(1, null);         
    }
    updateStatement.setDate(2, result.getDate("RueckgabeDatum"));
    updateStatement.setInt(3, ausleiheID);
    updateStatement.execute();    
  }
  
  //Vorher convertMedium nötig
  private void convertAusleihe() throws SQLException {  
    System.out.println("   - Ausleihe");
    Statement statement = mysqlDatenbank.getStatement();
    statement.execute("alter table ausleihe rename temp");
    createAusleiheTables();
    
    //Daten vorbereiten
    ResultSet result = (ResultSet) statement.executeQuery(
        "select max(nr) from temp");
    result.next();
    int ausleihenMaxID = result.getInt(1);

    boolean[] bearbeitet = new boolean[ausleihenMaxID+1];
    for (int i=0; i < bearbeitet.length; i++) bearbeitet[i] = false;
        
    result = (ResultSet) statement.executeQuery("select * from temp");
    while (result.next()) {
      int nr = result.getInt("Nr");
      if (!bearbeitet[nr]) {
        //Kerndaten einfügen
        int ausleiheID = insertAusleihe(result);
  
        String medium = result.getString("Medium");
        int benutzer = result.getInt("benutzer");

        insertZeitraum(ausleiheID, result);
        bearbeitet[result.getInt("nr")] = true;                  
        
        
        ResultSet sucherg = findPreviousZeitraum(medium, benutzer, result);
        while (sucherg.next()) {
          insertZeitraum(ausleiheID, sucherg);
          bearbeitet[sucherg.getInt("nr")] = true;          
          sucherg = findPreviousZeitraum(medium, benutzer, sucherg);
        }
        
        ResultSet letzterZeitraum = result;
        sucherg = findNextZeitraum(medium, benutzer, result);
        while (sucherg.next()) {
          letzterZeitraum = sucherg;
          insertZeitraum(ausleiheID, sucherg);
          bearbeitet[sucherg.getInt("nr")] = true;          
          sucherg = findNextZeitraum(medium, benutzer, sucherg);
        }
        
        updateAusleihe(ausleiheID, letzterZeitraum);
      }
    }    

    statement.execute("drop table temp");    
  }
  
  /**
   * Liest die erste Spalte des übergebenen ResultSets aus und liefert sie als durch
   * Kommata getennten String;
   * @param result das Resultset
   * @return der String
   */
  private String resultToString(ResultSet result) throws SQLException {
    String erg = null;
    while (result.next()) {
      String id = result.getString(1);
      if (erg == null) {
        erg = id;
      } else {
        erg += ", "+id;
      }
    }    
    return erg;
  }
}