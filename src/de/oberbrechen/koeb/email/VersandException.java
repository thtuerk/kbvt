package de.oberbrechen.koeb.email;

/**
 * Diese Exception kann beim Versand von eMails auftreten. 
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */
public class VersandException extends Exception {
  
  public VersandException(String s) {
    super(s);
  }
}
