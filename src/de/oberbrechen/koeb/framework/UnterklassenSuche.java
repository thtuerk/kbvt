package de.oberbrechen.koeb.framework;

/**
 * Gefunden unter 
 * http://www.javaworld.com/javaworld/javatips/jw-javatip113.html
 * RTSI.java
 *
 * Created: Wed Jan 24 11:15:02 2001
 *
 */

import java.io.*;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.jar.*;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This utility class is looking for all the classes implementing or 
 * inheriting from a given interface or class.
 * (RunTime Subclass Identification) by Daniel Le Berre
 * 
 * Auf eigene Bedürfnisse angepasst von Thomas Türk
 *
 * @author <a href="mailto:daniel@satlive.org">Daniel Le Berre</a>
 * @author <a href="mailto:t_tuerk@gmx.de">Thomas Türk</a>
 */
public class UnterklassenSuche {

  /**
   * Display all the classes inheriting or implementing a given
   * class in the currently loaded packages.
   * @param tosubclassname the name of the class to inherit from
   * @return all found subclasses
   */
  public static Class<?>[] find(Class<?> tosubclass) {
    Vector<Class<?>> foundClasses = new Vector<Class<?>>();
    Package.getPackage("de.oberbrechen.koeb.pdf.pdfMedienListe");
    Package[] pcks = Package.getPackages();
    for (int i=0;i<pcks.length;i++) {
      Class<?>[] found = find(pcks[i].getName(),tosubclass);
      for (int j=0; j < found.length; j++) {
        foundClasses.add(found[j]);
      }
    }
    return foundClasses.toArray(new Class[foundClasses.size()]);
  }
  
  
  /**
   * Display all the classes inheriting or implementing a given
   * class in a given package.
   * @param pckgname the fully qualified name of the package
   * @param tosubclass the Class object to inherit from
   * @return all found subclasses in the package
   */
  public static Class<?>[] find(String pckgname, Class<?> tosubclass) {
    Vector<Class<?>> foundClasses = new Vector<Class<?>>();

    // Code from JWhich
    // ======
    // Translate the package name into an absolute path
    String name = new String(pckgname);
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    name = name.replace('.', '/');

    // Get a File object for the package
    URL url = tosubclass.getResource(name);
    //URL url = ClassLoader.getSystemClassLoader().getResource(name);

    // Happens only if the jar file is not well constructed, i.e.
    // if the directories do not appear alone in the jar file like here:
    // 
    //          meta-inf/
    //          meta-inf/manifest.mf
    //          commands/                  <== IMPORTANT
    //          commands/Command.class
    //          commands/DoorClose.class
    //          commands/DoorLock.class
    //          commands/DoorOpen.class
    //          commands/LightOff.class
    //          commands/LightOn.class
    //          RTSI.class
    //
    if (url == null) {
      return new Class[0];
    }
    
    File directory = new File(url.getFile());

    // New code
    // ======
    if (directory.exists()) {
      // Get the list of the files contained in the package
      String[] files = directory.list();
      for (int i = 0; i < files.length; i++) {

        // we are only interested in .class files
        if (files[i].endsWith(".class")) {
          // removes the .class extension
          String classname = files[i].substring(0, files[i].length() - 6);
          try {
            // Try to create an instance of the object
            Class<?> foundClass = Class.forName(pckgname + "." + classname);
            if (tosubclass.isAssignableFrom(foundClass)) {
              foundClasses.add(foundClass);         
            }
          } catch (ClassNotFoundException cnfex) {
          }
        }
      }
    } else {
      try {
        // It does not work with the filesystem: we must
        // be in the case of a package contained in a jar file.
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        String starts = conn.getEntryName();
        JarFile jfile = conn.getJarFile();
        Enumeration<?> e = jfile.entries();
        while (e.hasMoreElements()) {
          ZipEntry entry = (ZipEntry) e.nextElement();
          String entryname = entry.getName();
          if (entryname.startsWith(starts)
            && (entryname.lastIndexOf('/') <= starts.length())
            && entryname.endsWith(".class")) {
            String classname = entryname.substring(0, entryname.length() - 6);
            if (classname.startsWith("/"))
              classname = classname.substring(1);
            classname = classname.replace('/', '.');
            try {
              // Try to create an instance of the object
              Class<?> foundClass = Class.forName(classname);
              if (tosubclass.isAssignableFrom(foundClass)) {
                foundClasses.add(foundClass);         
              }
            } catch (ClassNotFoundException cnfex) {
            }
          }
        }
      } catch (IOException ioex) {
      }
    }
    System.out.println(foundClasses.size());
    return foundClasses.toArray(new Class[foundClasses.size()]);
  }
}
