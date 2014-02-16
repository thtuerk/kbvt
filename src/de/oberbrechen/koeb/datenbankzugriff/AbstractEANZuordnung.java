package de.oberbrechen.koeb.datenbankzugriff;

import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenNichtGefundenException;
import de.oberbrechen.koeb.datenbankzugriff.exceptions.DatenbankInkonsistenzException;
import de.oberbrechen.koeb.datenstrukturen.EAN;
import de.oberbrechen.koeb.datenstrukturen.ISBN;
import de.oberbrechen.koeb.framework.ErrorHandler;

/**
 * Stellt Standard-Implementierungen für die Methoden getMediumEAN und
 * getBenutzerEAN zur Verfügung. Für Medien beginnt die EAN mit 22, gefolgt von
 * der Id des Mediums. Für Benutzer beginnt die EAN mit EAN mit 21 gefolgt von
 * der Id. Der Benutzer mit der Id 22 erhält z.B. die EAN
 * 210000000022x, wobei x eine automatisch bestimmte Prüfziffer ist.
 * Für Medien können zusätzliche EANs vorhanden sein. Mittels sucheMedium(ean) 
 * kann nach solchen EANs gesucht werden.
 * @author thtuerk
 */
public abstract class AbstractEANZuordnung implements EANZuordnung {

  protected BenutzerFactory benutzerFactory = Datenbank.getInstance().getBenutzerFactory();
  protected MediumFactory mediumFactory = Datenbank.getInstance().getMediumFactory();    
  
  public EAN getBenutzerEAN(Benutzer benutzer) {
    StringBuffer eanBuffer = new StringBuffer();
    eanBuffer.append(benutzer.getId());
    while (eanBuffer.length() < 10) {
      eanBuffer.insert(0, "0");
    }
    eanBuffer.insert(0, "21");

    return new EAN(eanBuffer.toString());        
  }

  public EAN getMediumEAN(Medium medium) {
    StringBuffer eanBuffer = new StringBuffer();
    eanBuffer.append(medium.getId());
    while (eanBuffer.length() < 10) {
      eanBuffer.insert(0, "0");
    }
    eanBuffer.insert(0, "22");

    return new EAN(eanBuffer.toString());        
  }


  public Object getReferenz(EAN ean) {
    if (ean.getEAN().startsWith("21000")) {
      int id = Integer.parseInt(ean.getEAN().substring(5,12));
      try {
        return benutzerFactory.get(id);
      } catch (DatenNichtGefundenException e) {
        return null;
      } catch (DatenbankInkonsistenzException e) {
        ErrorHandler.getInstance().handleException(e, false);
        return null;
      }
    } else if (ean.getEAN().startsWith("22000")) {
        int id = Integer.parseInt(ean.getEAN().substring(5,12));
        try {
          return mediumFactory.get(id);
        } catch (DatenNichtGefundenException e) {
          return null;
        } catch (DatenbankInkonsistenzException e) {
          ErrorHandler.getInstance().handleException(e, false);
          return null;
        }
    } else if (ean.isISBN()) {
      Medium medium = sucheMedium(ean);
      if (medium != null) return medium;
      
      ISBN isbn = ean.convertToISBN();
      if (isbn == null) return null;
      medium = sucheMedium(isbn);
      if (medium != null) return medium;

      return isbn;
  } else {
      return sucheMedium(ean);
    }
  }

  /**
   * Versucht ein zur übergebenen EAN passendes Medium zu finden.
   * Wird kein Medium gefunden, wird <code>null</code> zurückgeliefert.
   * @param ean die EAN, deren Medium gesucht werden soll
   * @return
   * @throws DatenNichtGefundenException
   * @throws DatenbankInkonsistenzException
   */
  protected abstract Medium sucheMedium(EAN ean);


  /**
   * Versucht ein zur übergebenen ISBN passendes Medium zu finden.
   * Wird kein oder mehr als ein Medium gefunden, wird <code>null</code> zurückgeliefert.
   * @param ean die EAN, deren Medium gesucht werden soll
   * @return
   * @throws DatenNichtGefundenException
   * @throws DatenbankInkonsistenzException
   */
  protected abstract Medium sucheMedium(ISBN isbn);
}