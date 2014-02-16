package de.tuerk.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
 
public class ConcatFile extends Task {

    private Vector fileSets = new Vector();
    private File concatFile;

    // The method executing the task
    public void execute() throws BuildException {
      Enumeration enumeration = fileSets.elements();
      while (enumeration.hasMoreElements()) {
        DirectoryScanner ds =((FileSet) enumeration.nextElement()).getDirectoryScanner(getProject()); 
        String[] files = ds.getIncludedFiles();
        for (int i=0; i< files.length; i++) {
          concatFile(ds.getBasedir()+"/"+files[i]); //$NON-NLS-1$
        }
      }
    }

    public void setConcatfile(File concatFile) {
      this.concatFile = concatFile;
    }
    
    private void concatFile(String file) {
      File newFile = new File(file+"-tmp"); //$NON-NLS-1$
      File oldFile = new File(file);

      PrintWriter writer;
      try {
        writer = new PrintWriter(
            new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(newFile))));      

        char[] buffer = new char[8192];

        FileReader reader = new FileReader(concatFile);
        while (true) {
            int nRead = reader.read(buffer, 0, buffer.length);
            if (nRead == -1) {
                break;
            }
            writer.write(buffer, 0, nRead);
        }
        writer.println();
        reader = new FileReader(oldFile);
        while (true) {
            int nRead = reader.read(buffer, 0, buffer.length);
            if (nRead == -1) {
                break;
            }
            writer.write(buffer, 0, nRead);
        }


        writer.flush();
        writer.close();
        oldFile.delete();
        newFile.renameTo(oldFile);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
      } catch (IOException e) {
        e.printStackTrace();
      }   
    }

    public void addFileset(FileSet fileSet) {
      fileSets.add(fileSet);
    }

}
