package queries;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;

/**
 * Transfers ONE file from IBM i (IFS directory) to local directory
 * "scriptfiles".
 * 
 * @author Vladimír Župka 2016
 *
 */
public class Q_ImportOneFromAS400 {
   // Path to result filter files (coming from GUI programs)
   static Path inPath = Paths.get(System.getProperty("user.dir"), "scriptfiles");
   static ResourceBundle locMessages;
   static String language;
   static String host;
   static String userName;
   static String password;
   static String ifsDirectory;
   static AS400 as400Host;
   static AS400FTP client;
   static String[] retCode;
   static String file, wasImported, toDir, notFoundInDir;

   /**
    * Obtains connection to IBM i and creates an FTP client. Then it transfers
    * the script file from the IFS directory given in parameters to local
    * directory "scriptfiles".
    * 
    * @param scriptFileName
    *           name of the file to transfer
    * @return retCode String array with 2 elements: 
    *         0 = POSITIVE/ERROR,
    *         1 = Error message
    */
   public static String[] transferOneFromAS400(String scriptFileName) {
      Q_Properties prop = new Q_Properties();
      language = prop.getProperty("LANGUAGE");
      host = prop.getProperty("HOST");
      userName = prop.getProperty("USER_NAME");
      ifsDirectory = prop.getProperty("IFS_DIRECTORY");

      // Append forward slash if not not present at the end of the path
      int len = ifsDirectory.length();
      if (len == 0) {
         ifsDirectory = "/";
         len = 1;
      }      if (!ifsDirectory.substring(len - 1, len).equals("/")) {
         ifsDirectory += "/";
      }
      Locale currentLocale = Locale.forLanguageTag(language);
      locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);
      // Localized messages
      file = locMessages.getString("File");
      wasImported = locMessages.getString("WasImported");
      toDir = locMessages.getString("ToDir");
      notFoundInDir = locMessages.getString("NotFoundInDir");

      // Get access to AS400
      as400Host = new AS400(host, userName);
      // Create an FTP client
      client = new AS400FTP(as400Host);
      // Return code has 2 parts: 1. incicator "ERROR" / "POSITIVE", 2. message
      // text
      retCode = new String[2];

      // Transfer script file from the local directory to IFS directory using
      // FTP
      try {
         // Path to output script file
         Path filePath = Paths.get(System.getProperty("user.dir"), "scriptfiles", scriptFileName);

         // FTP get
         client.get(ifsDirectory + scriptFileName, filePath.toString());
         retCode[0] = "POSITIVE";
         retCode[1] = file + scriptFileName + wasImported + ifsDirectory + toDir;
         return retCode;
      } catch (Exception e) {
         retCode[0] = "ERROR";
         retCode[1] = file + scriptFileName + notFoundInDir + ifsDirectory
               + ". Systémová zpráva:  " + e.getClass();
         return retCode;
      }
   }
}
