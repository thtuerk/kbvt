package de.oberbrechen.koeb.server.ausgaben;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

import Acme.Serve.Serve;
import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.dateien.ausgabenKonfiguration.AusgabenTreeModel;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;

/**
 * Diese Klasse stellt einige Ausgaben über einen
 * simplen Webserver zur Verfügung.
 */ 

public class Main {
    public static void main( String[] args ) throws Exception{
      if (args.length == 0) return;
      
      if (args[0].equalsIgnoreCase("server")) {
        int port = 9090;
        if (args.length > 1) {
          try {
            port = Integer.parseInt(args[1]);
          } catch (Exception e) {
            System.err.println("Unpassende Port-Nummer '"+args[1]+"'!");            
          }
          starteServer(port);
        }
      }
      
      if (args[0].equalsIgnoreCase("req")) {
        if (args.length > 1) sendFile(args[1]);
      }
    }
    
    private static DefaultMutableTreeNode getNodeFromPath(AusgabenTreeModel model, String path) {
      int lastPos = path.lastIndexOf('/');
      path = path.substring(0, lastPos);

      DefaultMutableTreeNode node;
      node = (DefaultMutableTreeNode) model.getRoot();
      try {
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreTokens()) {
          node = (DefaultMutableTreeNode) node.getChildAt(Integer.parseInt(tokenizer.nextToken()));
        }
      } catch (Exception e) {
        return null;        
      }
      return node;
    }
    
    public static void sendFile(String path) throws Exception {     
        if ( path == null || path.charAt( 0 ) != '/' ) return;
        
        AusgabenTreeModel model = new AusgabenTreeModel();
        DefaultMutableTreeNode node = getNodeFromPath(model, path);
        if (!node.isLeaf()) return;
        
        Ausgabe currentAusgabe = (Ausgabe) node.getUserObject();
        if (!currentAusgabe.istSpeicherbar() || currentAusgabe.benoetigtGUI()) return;        
    
        File file = File.createTempFile("ausgaben-srv", currentAusgabe.getStandardErweiterung());
        try {
          currentAusgabe.schreibeInDatei(null, false, file);
        } catch (Exception e) {
          return;        
        }

        InputStream in = new FileInputStream( file );
        Acme.Utils.copyStream( in, System.out );
        in.close();
        file.delete();
        Datenbank.clear();
    }
    
    public static void starteServer(int port){
      Serve serve = new Serve( port );

      // Any custom Servlets should be added here.
      serve.addServlet("*", new AusgabenServlet());
      //serve.addDefaultServlets( false );
      System.out.println("Ausgaben-Server auf Port "+port+" gestartet...");
      serve.serve();
      System.out.println("Ausgaben-Server beendet");
      System.exit( 0 );
    }    
}
