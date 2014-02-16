package de.oberbrechen.koeb.gui.framework ;

import javax.swing.*;
import java.awt.Dimension;

/**
 * Diese Klasse dient JComponents zu formatieren.
 *
 * @author Thomas Türk (t_tuerk@gmx.de)
 */

public class JComponentFormatierer {

  /**
   * Setzt minimum, maximum und preferredDimension der übergebenen JComponent
   * auf den übergebenen Wert
   *
   * @param componente die zu verändernde JComponent
   * @param dimension die zu setzende Dimension
   */
  public static void setDimension(JComponent componente, 
                                  Dimension dimension) {
    componente.setMinimumSize(dimension);
    componente.setMaximumSize(dimension);
    componente.setPreferredSize(dimension);
  }


}
