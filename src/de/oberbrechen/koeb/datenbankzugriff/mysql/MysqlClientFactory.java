package de.oberbrechen.koeb.datenbankzugriff.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import de.oberbrechen.koeb.datenbankzugriff.AbstractClientFactory;
import de.oberbrechen.koeb.datenbankzugriff.Client;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenstrukturen.ClientListe;
import de.oberbrechen.koeb.framework.ErrorHandler;

public class MysqlClientFactory extends AbstractClientFactory {

  public Client erstelleNeu() {
    return new MysqlClient();
  }

  protected ClientListe ladeClientListe(String sqlStatement) {
    ClientListe liste = new ClientListe();
    try {
      MysqlDatenbank.getMysqlInstance().beginTransaktion();
      Statement statement = MysqlDatenbank.getMysqlInstance().getStatement();
      ResultSet result = (ResultSet) statement.executeQuery(sqlStatement);
      while (result.next()) {
        int clientId = result.getInt("id");
        
        Client client = (Client) ladeAusCache(clientId);
        if (client == null) {
          client = new MysqlClient(result);
          cache.put(new Integer(client.getId()), client);
        }
        
        liste.addNoDuplicate(client);
      }
      MysqlDatenbank.getMysqlInstance().releaseStatement(statement);
      MysqlDatenbank.getMysqlInstance().endTransaktion();
    } catch (SQLException e) {
      ErrorHandler.getInstance().handleException(e, 
        "Fehler beim Laden der Clientliste!", true);
    }

    return liste;
  }

  public ClientListe getAlleClients() {
    clearCache();
    return ladeClientListe("select * from client;");
  }
  
  public ClientListe getAlleClientsMitInternetzugang() {
    clearCache();
    return ladeClientListe("select * from client where besitztInternetzugang=1;");
  }

  public Client ladeAusDatenbank(int id) throws DatenNichtGefundenException {
    return new MysqlClient(id);
  }

}