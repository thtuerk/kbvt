package de.oberbrechen.koeb.server.ausgaben;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import sun.misc.BASE64Decoder;
import Acme.Serve.ServeUtils;
import Acme.Serve.servlet.ServletException;
import Acme.Serve.servlet.http.HttpServlet;
import Acme.Serve.servlet.http.HttpServletRequest;
import Acme.Serve.servlet.http.HttpServletResponse;

import com.lowagie.text.html.HtmlEncoder;

import de.oberbrechen.koeb.ausgaben.Ausgabe;
import de.oberbrechen.koeb.dateien.ausgabenKonfiguration.AusgabenTreeModel;
import de.oberbrechen.koeb.datenbankzugriff.Datenbank;
import de.oberbrechen.koeb.datenbankzugriff.Mitarbeiter;
import de.oberbrechen.koeb.datenbankzugriff.MitarbeiterFactory;

public class AusgabenServlet extends HttpServlet {
    AusgabenTreeModel model;
    
    public AusgabenServlet() {
       model = new AusgabenTreeModel();
       new Timer().scheduleAtFixedRate(new TimerTask() {

        public void run() {
          synchronized (model) {
             model = new AusgabenTreeModel();    
          }        
        }}, 600000, 600000);
    }

    public String getServletInfo() {
      return "Ein Servlet für Ausgaben.";
    }

    /// Services a single request from the client.
    // @param req the servlet request
    // @param req the servlet response
    // @exception ServletException when an exception has occurred
    public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
      boolean headOnly;
      if ( req.getMethod().equalsIgnoreCase( "get" ) )
          headOnly = false;
      else if ( req.getMethod().equalsIgnoreCase( "head" ) )
          headOnly = true;
      else {
        res.sendError( HttpServletResponse.SC_NOT_IMPLEMENTED );
        return;
      }

      //Passwort prüfen
      if (!validLogin(req)) {
        res.setIntHeader("WWW-Authenticate: Basic realm=\"Ausgaben\"", 401);
        res.sendError(401);
        return;
      }

      
      String path = req.getServletPath();
      if ( path == null || path.charAt( 0 ) != '/' )
      {
        res.sendError( HttpServletResponse.SC_BAD_REQUEST );
        return;
      }
      
      int lastPos = path.lastIndexOf('/');
      path = path.substring(0, lastPos);

      DefaultMutableTreeNode node;
      synchronized (model) {
        node = (DefaultMutableTreeNode) model.getRoot();
        try {
          StringTokenizer tokenizer = new StringTokenizer(path, "/");
          while (tokenizer.hasMoreTokens()) {
            node = (DefaultMutableTreeNode) node.getChildAt(Integer.parseInt(tokenizer.nextToken()));
          }
        } catch (Exception e) {
          res.sendError( HttpServletResponse.SC_NOT_FOUND );
          return;        
        }
      }
      
      if (node.isLeaf()) {
        serveFile(req, res, headOnly, node, path);        
      } else {
        serveDirectory(req, res, headOnly, node, path);        
      }
  }

    
  private boolean validLogin(HttpServletRequest req) {
    String auth = req.getHeader("authorization");
    if (auth == null || !auth.startsWith("Basic ")) return false;
    
    byte[] authbyte;
    try {
      authbyte = new BASE64Decoder().decodeBuffer(auth.substring(6));
    } catch (IOException e) {
      return false;
    }
    
    auth = new String(authbyte);
    int trennPos = auth.indexOf(':');
    String user = auth.substring(0, trennPos);
    String passwd = auth.substring(trennPos+1);
    
    MitarbeiterFactory mitarbeiterFactory = 
      Datenbank.getInstance().getMitarbeiterFactory();
    try {
      int id = mitarbeiterFactory.sucheMitarbeiterBenutzername(user);
      Mitarbeiter mitarb = (Mitarbeiter) mitarbeiterFactory.get(id);
      return mitarb.checkMitarbeiterPasswort(passwd);
    } catch (Exception e) {
      return false;
    }    
  }

  private void serveFile( HttpServletRequest req, HttpServletResponse res, boolean headOnly, DefaultMutableTreeNode node, String path) throws IOException {
      log( "getting " + path );
      Ausgabe currentAusgabe = (Ausgabe) node.getUserObject();
      if (!currentAusgabe.istSpeicherbar() || currentAusgabe.benoetigtGUI()) {
        res.sendError( HttpServletResponse.SC_BAD_REQUEST );
        return;        
      }
  
      File file = File.createTempFile("ausgaben-srv", currentAusgabe.getStandardErweiterung());
      try {
        currentAusgabe.schreibeInDatei(null, false, file);
      } catch (Exception e) {
        res.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        return;        
      }

      res.setStatus( HttpServletResponse.SC_OK );
      res.setContentType( getServletContext().getMimeType( file.getAbsolutePath() ));
      res.setContentLength( (int) file.length() );
      res.setDateHeader( "Last-modified", System.currentTimeMillis() );
      OutputStream out = res.getOutputStream();

      InputStream in = new FileInputStream( file );
      Acme.Utils.copyStream( in, out );
      in.close();
      out.close();
      file.delete();
      Datenbank.clear();
    }
    
    private void printDirNode(TreeNode node, PrintStream p, String dir, int level, int maxlevel) {        
      for (int i=0; i < node.getChildCount(); i++) {
        DefaultMutableTreeNode currentChild = 
          (DefaultMutableTreeNode) node.getChildAt(i);

        Ausgabe ausgabe = (Ausgabe) currentChild.getUserObject();
        if (currentChild.isLeaf() &&
            (!ausgabe.istSpeicherbar() || ausgabe.benoetigtGUI())) continue;
        
        if (currentChild.isLeaf()) {
          p.print("<a class=\"file"+level+"\" href=\""+dir+"/"+i+"/"+HtmlEncoder.encode(currentChild.toString())+"."+
              ausgabe.getStandardErweiterung()+
              "\">");
          p.println(HtmlEncoder.encode(currentChild.toString())+"."+ausgabe.getStandardErweiterung());
          p.print("</a>");
        } else {
          p.print("<a class=\"dir"+level+"\" href=\""+dir+"/"+i+"/index.html\">");
          if (level == maxlevel)
            p.print("+ ");
          else
            p.print("- ");
          p.println(currentChild.toString());
          p.print("</a>");
        }
        if (!currentChild.isLeaf() && level < maxlevel) {
          printDirNode(currentChild, p, dir+"/"+i, level+1, maxlevel);
        }
      }      
    }
    

    private void serveDirectory( HttpServletRequest req, HttpServletResponse res, boolean headOnly, TreeNode node, String path) throws IOException {
      log( "indexing " + path );
      res.setStatus( HttpServletResponse.SC_OK );
      res.setContentType( "text/html; charset=UTF-8" );
      OutputStream out = res.getOutputStream();
      if ( ! headOnly ) {
          String titel = "Ausgaben";
          if (node != model.getRoot()) titel += " - "+node;
          PrintStream p = new PrintStream( new BufferedOutputStream( out ) );
          p.println( "<HTML><HEAD>" );
          p.println( "<TITLE>"+ titel + "</TITLE>" );
          p.println( "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
          p.println( "<style type=\"text/css\">");
          p.println( "a  {color:#000000; text-decoration:none; font-family:sans-serif;}");
          p.println( "a.dir0 {font-weight:bold; font-size:large; margin-left:0em;line-height:2em}");
          p.println( "a.dir1 {font-weight:bold; font-size:large; margin-left:2em;line-height:2em}");
          p.println( "a.dir2 {font-weight:bold; font-size:medium; margin-left:4em;line-height:2em}");
          p.println( "a.dir3 {font-weight:bold; font-size:small; margin-left:6em;line-height:2em}");
          p.println( "a.dir4 {font-weight:bold; font-size:small; margin-left:8em;line-height:2em}");
          p.println( "a.file0 {font-size:large; margin-left:0em;color:#CC3333}");
          p.println( "a.file1 {font-size:large; margin-left:2em;color:#CC3333}");
          p.println( "a.file2 {font-size:medium; margin-left:4em;color:#CC3333}");
          p.println( "a.file3 {font-size:small; margin-left:6em;color:#CC3333}");
          p.println( "a.file4 {font-size:small; margin-left:8em;color:#CC3333}");
          p.println("</style>");

          
          
          
          p.println( "</HEAD><BODY>" );
          p.println( "<H2>"+ titel +"</H2>" );
          p.println( "<PRE>" );
          if (node == model.getRoot()) {
            p.println("<a class=\"dir0\" href=\"/\">/</a>");
            printDirNode(node, p, "", 1, 2);
          } else {
            int pathpos = path.lastIndexOf('/');
            if (pathpos > 0) {
              String oldpath = path.substring(0, pathpos);
              p.println("<a class=\"dir0\" href=\""+oldpath+"/index.html\">- "+node.getParent()+"</a>");
            } else {
              p.println("<a class=\"dir0\" href=\"/\">/</a>");              
            }
            p.println("<a class=\"dir1\" href=\""+path+"/index.html\">- "+node+"</a>");
            printDirNode(node, p, path, 2, 3);            
          }
          p.println( "</PRE>" );
          ServeUtils.writeAddress( p );
          p.println( "</BODY></HTML>" );
          p.flush();
          }
      out.close();
      }
    }
