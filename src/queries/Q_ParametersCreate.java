package queries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Creates a default parameters in file JE_Parameters.txt in directory "paramfiles".
 * 
 * @author Vladimír Župka 2016
 *
 */
public class Q_ParametersCreate {
   final static String PROP_COMMENT = "SqlScripts for IBM i, © Vladimír Župka 2015";

   static Path outPath = Paths.get(System.getProperty("user.dir"), "paramfiles",
         "Q_Parameters.txt");
   static String encoding = System.getProperty("file.encoding");
   /**
    * Main method. Sets parameter properties and writes (stores) them
    * in file Q_Parameters.txt in directory "paramfiles".
    * @param strings       not used
    */
   public static void main(String... strings) {
      Properties properties = new Properties();
      properties.setProperty("LANGUAGE", "cs-CZ");
      properties.setProperty("HOST", "193.179.195.133");
      properties.setProperty("USER_NAME", "VZUPKA");
      properties.setProperty("LIBRARY", "KOLEKCE, VZSQL, VZTOOL, CORPDATA");
      properties.setProperty("IFS_DIRECTORY", "/home/vzupka/");
      properties.setProperty("AUTO_WINDOW_SIZE", "Y");
      properties.setProperty("RESULT_WINDOW_WIDTH", "450");
      properties.setProperty("RESULT_WINDOW_HEIGHT", "450");
      properties.setProperty("NULL_PRINT_MARK", "-");
      properties.setProperty("COLUMN_SEPARATING_SPACES", "1");
      properties.setProperty("FONT_SIZE", "9");
      properties.setProperty("EDIT_FONT_SIZE", "14");
      properties.setProperty("DECIMAL_PATTERN", "");

      try {
         // If the Parameters.txt file does not exist, create one
         if (!Files.exists(outPath)) {
            Files.createFile(outPath);
         }
         // Create a new text file in directory "paramfiles"
         BufferedWriter outfile = Files.newBufferedWriter(outPath, Charset.forName(encoding));
         properties.store(outfile, PROP_COMMENT);

      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
   }
}
