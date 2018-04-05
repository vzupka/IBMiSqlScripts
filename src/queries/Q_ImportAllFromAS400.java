package queries;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.FTP;
import com.ibm.as400.access.IFSFile;
import java.awt.Toolkit;
import java.util.List;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Import all scripts from IBM i (IFS directory) to local directory "scriptfiles".
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ImportAllFromAS400 extends SwingWorker<String, String> {

    static Q_Menu menu;
    static ResourceBundle locMessages;
    static String language;
    static String host;
    static String userName;
    static String password;
    static String ifsDirectory;
    static AS400 as400Host;
    static AS400FTP client;
    static String file, wasImported, transferEnd, dir, notFoundInDir, ioError;
    static String fileName;
    static String msgValue;

    /**
     * Constructor
     *
     * @param menu
     */
    Q_ImportAllFromAS400(Q_Menu menu) {
        Q_ImportAllFromAS400.menu = menu;
    }

    /**
     * Perform method transferAllFromAS400() and return a message text.
     *
     * @return
     */
    @Override
    public String doInBackground() {
        transferAllFromAS400(menu);
        return msgValue;
    }

    /**
     * Concludes the SwingWorker task getting the message text (task's result).
     */
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
            msgValue = get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assembles published intermediate messages
     *
     * @param msgValues
     */
    @Override
    protected void process(List<String> msgValues) {
        // Does nothing
    }

    /**
     * Obtains connection to IBM i and creates an FTP client. Program reads files
     * from the IFS directory given in parameters one after another and puts them
     * to the directory "scriptfiles".
     *
     * @param menu
     * @return
     */
    @SuppressWarnings("UseSpecificCatch")
    public static String transferAllFromAS400(Q_Menu menu) {
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
        wasImported = locMessages.getString("WasImported");
        transferEnd = locMessages.getString("TransferEnd");
        dir = locMessages.getString("Directory");
        notFoundInDir = locMessages.getString("NotFoundInDir");
        ioError = locMessages.getString("IOError");

        // Get access to AS400
        as400Host = new AS400(host, userName);

        // Create an FTP client
        client = new AS400FTP(as400Host);

        // Read files from the IFS directory and write them to local directory "scriptfiles"
        IFSFile directory = new IFSFile(as400Host, ifsDirectory);
        try {
            IFSFile[] files = directory.listFiles();
            System.out.println("files[0]: " + files[0]);
            int nbrFiles = 0;
            // Transfer all files from IFS directory to the local directory
            for (IFSFile ifsFile : files) {
                String ifsFileName = ifsFile.getPath();
                if (ifsFile.isFile()) {
                    // IFS directory path name must end with a slash (/)
                    fileName = ifsFileName.substring(ifsFileName.lastIndexOf("/") + 1);
                    // Files beginning with . in its name are ignored
                    if (!ifsFileName.substring(0, 1).equals(".")) {
                        // Path to output script file
                        Path filePath = Paths.get(System.getProperty("user.dir"), "scriptfiles", fileName);

                        client.setDataTransferType(FTP.BINARY);
                        // FTP get
                        client.get(ifsFileName, filePath.toString());
                        msgValue = file + fileName + wasImported + ifsDirectory + ".";
                        menu.msgTextArea.append(msgValue + "\n");

                        nbrFiles++;
                    }
                }
            }
            if (nbrFiles == 0) {
                menu.msgTextArea.append(file + notFoundInDir + ifsDirectory + "\n");
            }
            menu.msgTextArea.append(transferEnd + "\n");
        } catch (IOException ioe) {
            System.out.println(ioError + ioe.getLocalizedMessage());
            ioe.printStackTrace();
            menu.msgTextArea.append(ioError + ioe.getLocalizedMessage());
            return "";
        }
        return msgValue;
    }

}
