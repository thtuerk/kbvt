package de.oberbrechen.koeb.datenbankzugriff.mysql.datenbankupdate;


/**
 * Diese Klasse dient dazu die Datenbank von einer Version des 
 * Datenbankschemas in die aktuelle Version zu überführen.
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class Datenbankupdate {
  
  public static void main(String[] args) {
    if (args.length == 0) return;
    
    if (args[0].equals("2")) {
      new Datenbankupdate_1_nach_2().convertDatenbank();
    }
    if (args[0].equals("3")) {
      new Datenbankupdate_2_nach_3().convertDatenbank();
    }
 }
}