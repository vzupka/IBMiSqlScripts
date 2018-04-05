package queries;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import com.ibm.as400.access.AS400;

import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.FTP;
import com.ibm.as400.access.IFSFile;


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
    static AS400FTP client;
    static IFSFile ifsFile;
    static String file, wasExported, directory, notFound;
    static String messageText;

    /**
     * Obtains connection to IBM i and transfers the script file from
     * local directory "scriptfiles" to the IFS directory named in parameters.
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

            // Path to input script file
            Path filePath = Paths.get(System.getProperty("user.dir"), "scriptfiles", scriptFileName);
            // Create an FTP client
            client = new AS400FTP(as400Host);

            // Transfer script file from the local directory to IFS directory using FTP
            try {
                client.setDataTransferType(FTP.BINARY);
                // FTP put
                client.put(filePath.toString(), ifsDirectory + scriptFileName);
                messageText = file + scriptFileName + wasExported + ifsDirectory + ".";
                return messageText;
            } catch (Exception exc) {
                messageText = directory + ifsDirectory + notFound + exc.getClass() + ", " + exc.toString();
                System.out.println(messageText);
                return messageText;
            }
        }
    }
