package queries;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import com.ibm.as400.access.AS400;

import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Transfers ONE file from the IBM i (IFS directory) to local directory "scriptfiles".
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ExportOneToAS400 {

    // Path to the script file
    static Path inPath = Paths.get(System.getProperty("user.dir"), "scriptfiles");
    static ResourceBundle locMessages;
    static String language;
    static String host;
    static String userName;
    static String password;
    static String ifsDirectory;
    static AS400 as400Host;
    static IFSFile ifsFile;
    static String file, wasExported, directory, notFound;
    static String messageText;

    /**
     * Obtains connection to IBM i and creates an FTP client. Then it transfers the script file from
     * local directory "scriptfiles" to the IFS directory given in parameters.
     *
     * @param scriptFileName name of the file to transfer
     * @return messageText text of a message
     */
    @SuppressWarnings("UseSpecificCatch")
    public static String transferOneToAS400(String scriptFileName) {
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
        }
        if (!ifsDirectory.substring(len - 1, len).equals("/")) {
            ifsDirectory += "/";
        }

        Locale currentLocale = Locale.forLanguageTag(language);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);
        // Localized messages
        file = locMessages.getString("File");
        wasExported = locMessages.getString("WasExported");
        directory = locMessages.getString("Directory");
        notFound = locMessages.getString("NotFound");

        // Get access to AS400
        as400Host = new AS400(host, userName);

        try {

            // Path to input script file
            Path filePath = Paths.get(System.getProperty("user.dir"), "scriptfiles", scriptFileName);
            List<String> lines;
            // Read all bytes from the scriptfiles file scriptFileName
            lines = Files.readAllLines(filePath, Charset.forName("UTF-8"));
            // Open IFS output stream file in the IFS directory
            ifsFile = new IFSFile(as400Host, ifsDirectory + scriptFileName);
            ifsFile.createNewFile();
            // Write data to the IFS file
            PrintWriter writer = new PrintWriter(new BufferedWriter(new IFSFileWriter(ifsFile)));            
            writer.println(lines);
            // Close the IFS file if it does not exist
            writer.close();
            
            messageText = file + scriptFileName + wasExported + ifsDirectory + ".";
            return messageText;
            
        } catch (Exception ioe) {
            ioe.printStackTrace();
            System.out.println("IFSOutput error: " + ioe.getLocalizedMessage());
            messageText = "IFSOutput error: " + ioe.getLocalizedMessage();

            return messageText;
        }

    }
}
