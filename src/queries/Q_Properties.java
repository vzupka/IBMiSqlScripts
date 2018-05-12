package queries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class enables other classes to obtain application properties.
 * 
 * @author Vladimír Župka 2016
 *
 */
public class Q_Properties {
   final static String PROP_COMMENT = "SqlScripts for IBM i, © Vladimír Župka 2015";

   static Path parPath = Paths
         .get(System.getProperty("user.dir"), "paramfiles", "Q_Parameters.txt");
   static String encoding = System.getProperty("file.encoding");
   static Properties properties;

   /**
    * 
    * @param key
    * @param value
    */
   public void setProperty(String key, String value) {
      // Store properties to output file
      try {
         BufferedWriter outfile = Files.newBufferedWriter(parPath, Charset.forName(encoding));
         properties.setProperty(key, value);
         // Store properties
         properties.store(outfile, PROP_COMMENT);
         outfile.close();
      } catch (IOException ioe) {
         System.out.println(ioe.getMessage());
      }
   }
   
   /**
    * Delivers a specific property given a name: "LIBRARY_LIST" "IFS_DIRECTORY"
    * "HOST" etc.
    * 
    * @param key
    *           name of property
    * @return value of the property
    */
   public String getProperty(String key) {
      properties = new Properties();
      try {
         // Open input file for loading parameter properties
         BufferedReader infile = Files.newBufferedReader(parPath, Charset.forName(encoding));
         // Load parameter properties
         properties.load(infile);
         String value = properties.getProperty(key);
         return value;
      } catch (IOException ioe) {
         System.out.println(ioe.getMessage());
         return null;
      }
   }
}
