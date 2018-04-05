package queries;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Display list for selection SQL scripts and calls a program to run the selected script.
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ScriptRunCall extends JFrame {

    static final long serialVersionUID = 1L;

    Q_Properties prop;

    Locale currentLocale;

    String language;
    ResourceBundle titles;
    String titRun, scriptNam, scriptDes, searchScript;
    ResourceBundle buttons;
    String exit, run_sel, refresh, imp_script;
    ResourceBundle locMessages;
    String connOk, curDir, noRowSel, inputError;

    // Connection to the server
    static Connection conn;

    String host;

    // Vector for returning messages to Menu
    @SuppressWarnings("UseOfObsoleteCollectionType")
//    Vector<String> messages = new Vector<>();
    ArrayList<String> messages = new ArrayList<>();

    // Dimensions of different window containers
    final Integer scriptListGlobalWidth = 1050;
    final Integer scriptListGlobalHeight = 700;
    final Integer scriptListPanelWidth = scriptListGlobalWidth;
    final Integer scriptListPanelHeight = scriptListGlobalHeight - 210;

    final Integer scriptListWidth = scriptListPanelWidth;
    final Integer scriptListHeight = scriptListPanelHeight;
    final Integer xLocation = 400;
    final Integer yLocation = 40;

    final Integer tableRowHeight = 24;
    final Integer firstTableColumnWidth = 200;
    final Integer secondTableColumnWidth = 660;

    final String PARAM_SEPARATOR = ";";

    // List (table) of filter records for a specific entry type
    JTable scriptList;

    // Data model for the table
    DefaultTableModel tableModel;

    // scriptNames with the key of scriptName
    TreeMap<String, String> scriptNames = new TreeMap<>();
    Map.Entry<String, String> mapEntry;

    // Index of table row selected (by the user or the program)
    int scriptListIndexSel;
    // List selection model
    ListSelectionModel rowIndexList;

    String scriptName;
    String scriptDescription;

    String selectedScript;
    String qryString;

    // Work file record data length
    Integer recordLength;

    // Visible data part of the record (before mask part)
    Integer dataLength;
    // Table consists of rows and columns named "records"
    Object[][] records;
    int nbrOfRows; // number of rows in table

    // Model enabling table row selection
    ListSelectionModel selModel;

    // Directory containing the SQL script files
    Path scriptDirectoryPath = Paths.get(System.getProperty("user.dir"), "scriptfiles");
    Path scriptIn;
    BufferedReader infileScript;

    // Area for text of the script result
    JTextArea resultTextArea = new JTextArea();

    // Name of the file containing an SQL script
    String fileName;

    String[] markerValues;
    static ArrayList<String[]> markerArrayList = new ArrayList<>();
    final String PARAM_PREFIX = "--;?";

    String[] headerValues;
    static ArrayList<String[]> headerArrayList = new ArrayList<>();
    final String HEADER_PREFIX = "--;H";

    String[] totalValues;
    static ArrayList<String[]> totalArrayList = new ArrayList<>();
    final String TOTAL_PREFIX = "--;T";

    String[] patternValues;
    static ArrayList<String[]> patternArrayList = new ArrayList<>();
    final String DECIMAL_PREFIX = "--;D";

    String[] printValues;
    static ArrayList<String[]> printArrayList = new ArrayList<>();
    final String PRINT_PREFIX = "--;P";

    // Exception in title headings - array list has
    // single String elements only, not String[] elements.
    static ArrayList<String> titleArrayList = new ArrayList<>();
    final String TITLE_PREFIX = "--;t";

    static ArrayList<String[]> levelArrayList = new ArrayList<>();
    String[] levelValues;
    final String LEVEL_PREFIX = "--;L";

    String[] summaryValues;
    static ArrayList<String[]> summaryArrayList = new ArrayList<>();
    final String SUMMARY_PREFIX = "--;S";

    String[] summaryIndValues;
    static ArrayList<String[]> summaryIndArrayList = new ArrayList<>();
    final String SUM_IND_PREFIX = "--;s";

    String[] omitValues;
    static ArrayList<String[]> omitArrayList = new ArrayList<>();
    final String OMIT_PREFIX = "--;O";

    // Window container
    Container cont = getContentPane();

    JPanel scriptListGlobalPanel;
    JPanel dataGlobalPanel;
    Container listContentPane;
    Container dataContentPane;
    JLabel prompt;
    GroupLayout layout = new GroupLayout(getContentPane());

    // Return code array: [0] - tag "runScript" or "runPrompt",
    // [1] - error message text
    String[] retCode = new String[2];

    /**
     * Building of script list in scriptListPanel
     */
    // Components for scriptListGlobalPanel
    JPanel scriptListTitlePanel;
    JPanel scriptListPanel;
    JScrollPane scrollPane;
    JPanel scriptListMsgPanel;
    JPanel scriptListButtonPanel;

    JLabel scriptListTitle;

    JTextField searchField;
    JLabel searchLabel;
    JLabel scriptListPrompt;
    JLabel scriptListPrompt2;
    JLabel scriptListMsg;
    String msgText;

    JButton scriptListExitButton;
    JButton scriptListRunButton;
    JButton scriptListRefreshButton;
    JButton scriptListImportButton;

    // Table columns for the list
    TableColumn colscriptName;
    TableColumn colQueryDesc;

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);

    /**
     * Constructor
     */
    public Q_ScriptRunCall() {
        prop = new Q_Properties();
        host = prop.getProperty("HOST");
        language = prop.getProperty("LANGUAGE");

        // Get resource bundle class
        currentLocale = Locale.forLanguageTag(language);
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);
    }

    /**
     * Builds script list in scriptListPanel
     *
     * @param menu
     */
    public void buildScriptList(Q_Menu menu) {
        // Localized titles
        titRun = titles.getString("TitRun");
        scriptNam = titles.getString("ScriptNam");
        scriptDes = titles.getString("ScriptDes");
        searchScript = titles.getString("SearchScript");

        // Localized button labels
        exit = buttons.getString("Exit");
        run_sel = buttons.getString("Run_sel");
        refresh = buttons.getString("Refresh");
        imp_script = buttons.getString("Imp_script");

        // Localized messages
        curDir = locMessages.getString("CurDir");
        noRowSel = locMessages.getString("NoRowSel");
        inputError = locMessages.getString("InputError");

        conn = getConnectionDB2();

        // Create a new table model
        tableModel = new Q_TableModel();

        // Build the window
        scriptListTitlePanel = new JPanel();
        scriptListPanel = new JPanel();
        searchField = new JTextField("");
        searchLabel = new JLabel(searchScript);
        searchLabel.setForeground(DIM_BLUE); // Dim blue

        scrollPane = new JScrollPane();
        scriptListMsgPanel = new JPanel();
        scriptListButtonPanel = new JPanel();

        scriptListTitle = new JLabel();
        scriptListPrompt = new JLabel();
        scriptListPrompt2 = new JLabel();
        scriptListMsg = new JLabel();

        scriptListExitButton = new JButton(exit);
        scriptListExitButton.setMinimumSize(new Dimension(90, 35));
        scriptListExitButton.setMaximumSize(new Dimension(90, 35));
        scriptListExitButton.setPreferredSize(new Dimension(90, 35));

        scriptListRunButton = new JButton(run_sel);
        scriptListRunButton.setMinimumSize(new Dimension(140, 35));
        scriptListRunButton.setMaximumSize(new Dimension(140, 35));
        scriptListRunButton.setPreferredSize(new Dimension(140, 35));
        scriptListRunButton.setForeground(DIM_BLUE); // Dim blue
        scriptListRunButton.setFont(new Font("Helvetica", Font.PLAIN, 14));
        scriptListRunButton.setFont(scriptListExitButton.getFont().deriveFont(Font.PLAIN, 15));

        scriptListRefreshButton = new JButton(refresh);
        scriptListRefreshButton.setMinimumSize(new Dimension(140, 35));
        scriptListRefreshButton.setMaximumSize(new Dimension(140, 35));
        scriptListRefreshButton.setPreferredSize(new Dimension(140, 35));
        scriptListImportButton = new JButton(imp_script);
        scriptListImportButton.setMinimumSize(new Dimension(140, 35));
        scriptListImportButton.setMaximumSize(new Dimension(140, 35));
        scriptListImportButton.setPreferredSize(new Dimension(140, 35));

        // Empty table scriptList with data model
        scriptList = new JTable(tableModel);

        // Attributes of the filter list table
        scriptList.setFont(new Font("Helvetica", Font.PLAIN, 13));
        scriptList.getTableHeader().setFont(new Font("Helvetica", Font.BOLD, 12));
        scriptList.getTableHeader().setPreferredSize(new Dimension(10, 26));
        scriptList.setGridColor(Color.LIGHT_GRAY);
        scriptList.setRowHeight(tableRowHeight);
        scriptList.setGridColor(Color.WHITE);

        // Behavior at manual change of column width
        scriptList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Title panel in the filter list window
        // Set title
        BoxLayout boxLayoutY = new BoxLayout(scriptListTitlePanel, BoxLayout.Y_AXIS);
        scriptListTitlePanel.setLayout(boxLayoutY);

        scriptListTitle.setText(titRun);
        scriptListTitle.setFont(new Font("Helvetica", Font.PLAIN, 20));
        scriptListTitle.setForeground(DIM_BLUE); // Dim blue
        scriptListTitle.setMinimumSize(new Dimension(scriptListWidth, 20));
        scriptListTitle.setPreferredSize(new Dimension(scriptListWidth, 20));
        scriptListTitle.setMaximumSize(new Dimension(scriptListWidth, 20));
        scriptListTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(scriptListTitle);
        scriptListTitlePanel.add(Box.createRigidArea(new Dimension(10, 10)));

        scriptListTitlePanel.add(scriptListPrompt);
        scriptListTitlePanel.add(scriptListPrompt2);

        scriptListTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(scriptListTitle);

        searchLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(searchLabel);

        searchField.setMaximumSize(new Dimension(200, 25));
        searchField.setPreferredSize(new Dimension(200, 25));
        searchField.setMinimumSize(new Dimension(200, 25));
        searchField.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(searchField);

        // Align the title panel
        scriptListTitlePanel.setAlignmentX(Box.CENTER_ALIGNMENT);

        // Scroll pane contains the scriptList table inside
        scrollPane = new JScrollPane(scriptList);
        scrollPane.setMaximumSize(new Dimension(scriptListWidth, scriptListPanelHeight));
        scrollPane.setMinimumSize(new Dimension(scriptListWidth, scriptListPanelHeight));
        scrollPane.setPreferredSize(new Dimension(scriptListWidth, scriptListPanelHeight));
        scrollPane.setBackground(scriptListPanel.getBackground());
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        scriptListPanel.add(scrollPane);
        scriptListPanel.setMinimumSize(new Dimension(scriptListWidth, scriptListPanelHeight));
        scriptListPanel.setPreferredSize(new Dimension(scriptListWidth, scriptListPanelHeight));
        scriptListPanel.setMaximumSize(new Dimension(scriptListWidth, scriptListPanelHeight));
        // scriptListPanel.setBorder(BorderFactory.createLineBorder(Color.red));

        // Message panel in the list window
        scriptListMsgPanel.setMinimumSize(new Dimension(scriptListWidth, 30));
        scriptListMsgPanel.setPreferredSize(new Dimension(scriptListWidth, 30));
        scriptListMsgPanel.setMaximumSize(new Dimension(scriptListWidth, 30));
        BoxLayout msgLayoutX = new BoxLayout(scriptListMsgPanel, BoxLayout.X_AXIS);
        scriptListMsgPanel.setLayout(msgLayoutX);
        scriptListMsgPanel.setAlignmentX(Box.CENTER_ALIGNMENT);

        // Button panel in the list window
        BoxLayout buttonLayoutX = new BoxLayout(scriptListButtonPanel, BoxLayout.X_AXIS);
        scriptListButtonPanel.setLayout(buttonLayoutX);
        scriptListButtonPanel.add(scriptListExitButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListImportButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListRefreshButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListRunButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.setMinimumSize(new Dimension(scriptListWidth, 50));
        scriptListButtonPanel.setPreferredSize(new Dimension(scriptListWidth, 50));
        scriptListButtonPanel.setMaximumSize(new Dimension(scriptListWidth, 50));

        // Set contents of the global panel in the list window
        scriptListGlobalPanel = new JPanel();
        BoxLayout globBoxLayoutY = new BoxLayout(scriptListGlobalPanel, BoxLayout.Y_AXIS);
        scriptListGlobalPanel.setLayout(globBoxLayoutY);
        scriptListGlobalPanel.add(Box.createRigidArea(new Dimension(1, 25)));
        scriptListGlobalPanel.add(scriptListTitlePanel);
        scriptListGlobalPanel.add(scriptListPanel);
        scriptListGlobalPanel.add(scriptListMsgPanel);
        scriptListGlobalPanel.add(scriptListButtonPanel);
        scriptListGlobalPanel.add(Box.createRigidArea(new Dimension(1, 50)));

        // Column headings with text
        tableModel.addColumn(scriptNam);
        tableModel.addColumn(scriptDes);

        // Properties of table columns
        // ---------------------------
        colscriptName = scriptList.getColumnModel().getColumn(0);
        colscriptName.setMaxWidth(firstTableColumnWidth);
        colscriptName.setMinWidth(firstTableColumnWidth);
        colscriptName.setPreferredWidth(firstTableColumnWidth);
        colQueryDesc = scriptList.getColumnModel().getColumn(1);
        colQueryDesc.setPreferredWidth(secondTableColumnWidth);

        // Row selection model (selection of single row)
        selModel = scriptList.getSelectionModel();
        scriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Row selection model registration
        selModel.addListSelectionListener(sl -> {
            // scriptListMsg.setForeground(DIM_BLUE); // Dim blue
            rowIndexList = (ListSelectionModel) sl.getSource();
            scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
            if (!rowIndexList.isSelectionEmpty()) {
                scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                selectedScript = (String) records[scriptListIndexSel][0];
                scriptDescription = (String) records[scriptListIndexSel][1];
            } else { // No row was selected
                scriptListIndexSel = -1;
            }

        });

        // Set Return button activity (return to type code list)
        // -----------------------------------------------------
        scriptListExitButton.addActionListener(a -> {
            setVisible(false);
            dispose();
        });

        // Set Run button activity (on mouse click)
        // ----------------------------------------
        scriptListRunButton.addActionListener(a -> {
            scriptListMsg.setForeground(DIM_BLUE); // Dim blue
            scriptListMsgPanel.removeAll();
            if (rowIndexList != null) { // row index not empty
                // A table row was selected
                if (scriptListIndexSel >= 0) {
                    scriptListMsg.setText("");
                    scriptListMsgPanel.add(scriptListMsg);

                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                    // Get script name and desctiption from the selected row
                    selectedScript = (String) records[scriptListIndexSel][0];
                    scriptDescription = (String) records[scriptListIndexSel][1];
                    // Perform the script
                    retCode = performScript(selectedScript, scriptDescription);
                    // Handle messages
                    scriptListMsg.setText("");
                    if (!retCode[1].equals("")) {
                        scriptListMsg.setText(retCode[1]);
                        scriptListMsgPanel.add(scriptListMsg);
                        if (!retCode[0].contains("ERROR")) {
                            scriptListMsg.setForeground(DIM_BLUE); // blue
                        }
                        if (retCode[0].contains("ERROR")) {
                            scriptListMsg.setForeground(DIM_RED); // red
                        }
                    }
                    repaint();
                    setVisible(true);
                } // Selected row index is negative (-1)
                else {
                    scriptListMsg.setText(noRowSel);
                    scriptListMsg.setForeground(DIM_RED); // red
                    scriptListMsgPanel.add(scriptListMsg);
                    setVisible(true);
                }
            } // Row index list is empty
            else {
                scriptListMsg.setText(noRowSel);
                scriptListMsg.setForeground(DIM_RED); // red
                scriptListMsgPanel.add(scriptListMsg);
                setVisible(true);
            }
        });

        // Set Refresh button activity
        // ---------------------------
        scriptListRefreshButton.addActionListener(a -> {
            // Read scripts and put it into a list
            msgText = readInputFiles();
            scriptListMsg.setText(msgText);
            scriptListMsgPanel.add(scriptListMsg);
            readLinesForScriptList();
            this.setVisible(true);
        });

        // Set Import button activity
        // --------------------------
        scriptListImportButton.addActionListener(a -> {
            scriptListMsg.setForeground(DIM_BLUE); // Dim blue
            scriptListMsgPanel.removeAll();
            if (rowIndexList != null) { // row index not empty
                // A table row was selected
                if (scriptListIndexSel >= 0) {
                    scriptListMsg.setText("");
                    scriptListMsgPanel.add(scriptListMsg);

                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                    // Get script name and desctiption from the selected row
                    selectedScript = (String) records[scriptListIndexSel][0];
                    scriptDescription = (String) records[scriptListIndexSel][1];
                    // Process script
                    retCode = importScript(selectedScript, scriptDescription);
                    // Handle messages
                    if (retCode[0].contains("ERROR") && !retCode[1].isEmpty()) {
                        scriptListMsg.setText(retCode[1]);
                        scriptListMsgPanel.add(scriptListMsg);
                        setVisible(true);
                    }
                } // Selected row index is negative (-1) - Prompt for script name
                // to import
                else {
                    Q_PromptForScriptName promptForScriptName = new Q_PromptForScriptName();
                    promptForScriptName.runPrompt();
                }
            } // Row index list is empty - Prompt for script name to import
            else {
                Q_PromptForScriptName promptForScriptName = new Q_PromptForScriptName();
                promptForScriptName.runPrompt();
            }

            // Read scripts and put them into a list
            msgText = readInputFiles();
            scriptListMsg.setText(msgText);
            if (msgText.isEmpty()) {
                scriptListMsg.setText(curDir + System.getProperty("user.dir"));
                scriptListMsg.setForeground(DIM_BLUE);
            } else {
                scriptListMsg.setText(msgText);
                scriptListMsg.setForeground(DIM_BLUE);
            }
            scriptListMsgPanel.add(scriptListMsg);
            readLinesForScriptList();
        });

        // Read script files from directory "scriptfiles" and puts data
        // in the tree map "scriptNames" (String, StringBuilder)
        msgText = readInputFiles();
        scriptListMsg.setText(msgText);
        scriptListMsgPanel.add(scriptListMsg);

        if (msgText.isEmpty()) {
//            scriptListMsg.setText(curDir + System.getProperty("user.dir"));
            scriptListMsg.setForeground(DIM_BLUE);
        } else {
            scriptListMsg.setText(msgText);
            scriptListMsg.setForeground(DIM_RED);
        }
        scriptListMsgPanel.add(scriptListMsg);

        // Prepare scriptList table for display
        // ------------------------------------
        // Read all records from the input file and put its data into
        // the corresponding rows/columns.
        readLinesForScriptList();

        // Activate content of the window
        // ------------------------------
        listContentPane = this.getContentPane();
        // ( up, left, down, right )
        scriptListGlobalPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        // Add the global panel to window container
        listContentPane.add(scriptListGlobalPanel);

        setSize(scriptListGlobalWidth, scriptListGlobalHeight);
        setLocation(xLocation, yLocation);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    } // Build script list

    /**
     * Read all scriptNames from the tree map and place its data into array records to display the
     * JTable of the script list
     *
     * @return int - last index of the array of table records
     */
    protected int readLinesForScriptList() {
        // Array of rows and columns with values for the script list table
        records = new Object[nbrOfRows][2];

        // If scriptNames TreeMap is not empty, fill the filter list with these
        // scriptNames
        if (!scriptNames.isEmpty()) {
            int row = 0;
            // Read first line
            mapEntry = scriptNames.firstEntry();
            while (mapEntry != null) {
                // Add record data from scriptNames to the array
                records[row][0] = mapEntry.getKey();
                records[row][1] = mapEntry.getValue();
                // Read next line from the file
                mapEntry = scriptNames.higherEntry(mapEntry.getKey());
                row++;
            }
        }
        // Fill table model with data from records array
        fillTable();
        // This is the row index of the last record in the list
        return scriptNames.size();
    }

    /**
     * Reads script files from directory "scriptfiles" and puts data in a tree map "scriptNames"
     * (String, StringBuilder)
     *
     * @return
     */
    protected String readInputFiles() {
        // Prepare list of queries from script files placed in directory
        // "scriptfiles"
        scriptNames.clear();
        try {
            // Read script file names from the directory "scriptfiles"
            // in the window list of queries
            if (Files.isDirectory(scriptDirectoryPath)) {
                String[] fileNames = scriptDirectoryPath.toFile().list();
                nbrOfRows = 0;
                // Process list of script files
                for (String file_name : fileNames) {
                    // Get only files that conform to the search text
//                     - file name greater or equal to the search text.
//                    if (fileName.compareToIgnoreCase(searchField.getText()) >= 0) {                    
                    if (file_name.toUpperCase().contains(searchField.getText().toUpperCase())) {
                        // Create path to the script file
                        scriptIn = Paths.get(System.getProperty("user.dir"), "scriptfiles", file_name);
                        // Files not ending with .SQL or beginning with dot are ignored
                        if (file_name.toUpperCase().lastIndexOf(".SQL") < 0
                                || file_name.substring(0, 1).equals(".")) {
                            continue;
                        }
                        // Open the script file
                        infileScript = Files.newBufferedReader(scriptIn, Charset.forName("UTF-8"));

                        // If the first line is a simple comment beginning with "--"
                        // in the first position, and it is not "--;",
                        // the description is the text following the -- characters.
                        // Otherwise the description is empty.
                        String scriptLine = infileScript.readLine();
                        while (scriptLine != null) {
                            if (!scriptLine.isEmpty() && scriptLine.length() >= 3) {
                                if (scriptLine.substring(0, 2).equals("--")
                                        && !scriptLine.substring(0, 3).equals("--;")) {
                                    scriptDescription = scriptLine.substring(2);
                                    break;
                                } else {
                                    scriptDescription = "";
                                    break;
                                }
                            }
                            scriptLine = infileScript.readLine();
                        }
                        if (scriptLine == null) {
                            scriptDescription = "";
                        }
                        // Close the script file
                        infileScript.close();

                        // Put the file name with the description to the map
                        // for the list of scripts in the table
                        scriptNames.put(file_name, scriptDescription);

                        nbrOfRows++;
                    }
                    fileName = file_name;
                }
            }
            return "";
        } catch (IOException exc) {
            exc.printStackTrace();
            System.out.println("IOException readInputFiles: " + exc.getLocalizedMessage() + ", after the file " + fileName);
            return inputError + fileName;
        }
    }

    /**
     * Perform the selected script ("QUERY" or "UPDATE")
     *
     * @param selectedScript
     * @param scriptDescription
     * @return
     */
    // Buffer containing text of one SQL statement
    StringBuilder statementBuf;

    @SuppressWarnings("IndexOfReplaceableByContains")
    public String[] performScript(String selectedScript, String scriptDescription) {

        conn = getConnectionDB2();
        System.out.println("conn VVVV: "+conn);
        // Obtain object for connection to database DB2
        if (conn == null) {
            String messageText;
            conn = new Q_ConnectDB().connect();
            if (conn == null) {
                messageText = Q_ConnectDB.msg;
                retCode[0] += "ERROR";
                retCode[1] = messageText;
                messages.add(messageText);
                System.out.println(messageText);
            } else {
                connOk = locMessages.getString("ConnOk");
                currentLocale = Locale.forLanguageTag(language);
                DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
                        DateFormat.DEFAULT, currentLocale);
                Date date = new Date();
                String dateStr = formatter.format(date);
                messageText = connOk + host + " - " + dateStr;
                retCode[0] += "CONN_OK";
                retCode[1] = messageText;
                messages.add(messageText);
                System.out.println(messageText);
            }
        }

        scriptName = selectedScript.substring(0, selectedScript.indexOf("."));
        fileName = selectedScript;

        // Create path to the script file
        scriptIn = Paths.get(System.getProperty("user.dir"), "scriptfiles", fileName);
        // Create paths to text files
        Path workfilesTxt = Paths.get(System.getProperty("user.dir"), "workfiles", "Print.txt");
        Path printfilesTxt = Paths.get(System.getProperty("user.dir"), "printfiles", scriptName + ".txt");

        // Count of "UPDATE" statements
        int numberOfUpdates = 0;

        try {
            // Process the script file
            // -----------------------
            /*
          * // Prepend title lines to the result and the text files //
          * ---------------------------------------------------- // - script
          * description String title = scriptDescription + "\n\n";
          * resultTextArea.setText(title); // - date and time - localized
          * currentLocale = Locale.forLanguageTag(language); DateFormat
          * formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
          * DateFormat.DEFAULT, currentLocale); Date date = new Date(); title =
          * formatter.format(date) + "\n\n"; resultTextArea.append(title);
             */
            ArrayList<String> lines = new ArrayList<>();
            // The first entry of the array list (and the text files)
            // is the title of the script (script description and date)
            lines.add(resultTextArea.getText());

            // Create two text files from the array list "lines"
            Files.write(workfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(printfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            // Open the script file
            infileScript = Files.newBufferedReader(scriptIn, Charset.forName("UTF-8"));

            // Begin assembly of a new SQL statement
            // -------------------------------------
            statementBuf = new StringBuilder();

            // Clear marker array list
            markerArrayList = new ArrayList<>();
            // Clear header array list
            headerArrayList = new ArrayList<>();
            // Clear total array list
            totalArrayList = new ArrayList<>();
            // Clear decimal pattern arrayList
            patternArrayList = new ArrayList<>();
            // Clear print arrayList
            printArrayList = new ArrayList<>();
            // Clear title arrayList
            titleArrayList = new ArrayList<>();
            // Clear level arrayList
            levelArrayList = new ArrayList<>();
            // Clear summary arrayList
            summaryArrayList = new ArrayList<>();
            // Clear summary indicators arrayList
            summaryIndArrayList = new ArrayList<>();
            // Clear omitted column names arrayList
            omitArrayList = new ArrayList<>();

            // Read the first line of the script file
            String scriptLine = infileScript.readLine();

            // Read all lines of the script and write them to a string buffer
            while (scriptLine != null) {
                // A semicolon is on the line and is before the first
                // simple comment mark (--)
                // or there is no simple comment mark (--).
                // This ensures that this is not a semicolon belonging to
                // a definition line specification.
                if (scriptLine.indexOf(";") >= 0 && (scriptLine.indexOf(";") < scriptLine.indexOf("--"))
                        || scriptLine.indexOf(";") >= 0 && scriptLine.indexOf("--") == -1) {

                    // Semicolon ends the SQL statement in the script
                    // but is not allowed as part of the SQL statement itself
                    scriptLine = scriptLine.replace(";", " ");

                    // Add the input line to the statement buffer
                    statementBuf.append(scriptLine);
                    // ???? statementBuf.append("\n");

                    // Perform the SQL statement that ends with the semicolon
                    // ------------------------------------------------------
                    retCode = performSqlStatement(conn, scriptDescription);
                    if (retCode[0].contains("UPDATE")) {
                        numberOfUpdates++;
                    }

                    // Clear the buffer for a next SQL statement
                    statementBuf.setLength(0);
                    // Clear marker array list
                    markerArrayList = new ArrayList<>();
                    // Clear header array list
                    headerArrayList = new ArrayList<>();
                    // Clear total array list
                    totalArrayList = new ArrayList<>();
                    // Clear decimal pattern arrayList
                    patternArrayList = new ArrayList<>();
                    // Clear print arrayList
                    printArrayList = new ArrayList<>();
                    // Clear title arrayList
                    titleArrayList = new ArrayList<>();
                    // Clear level arrayList
                    levelArrayList = new ArrayList<>();
                    // Clear summary arrayList
                    summaryArrayList = new ArrayList<>();
                    // Clear summary indicators arrayList
                    summaryIndArrayList = new ArrayList<>();
                    // Clear omitted column names arrayList
                    omitArrayList = new ArrayList<>();

                    // Read next line of the script file
                    scriptLine = infileScript.readLine();

                    // Continue with the next SQL statement
                    continue;
                }
                // Process definition lines
                // ------------------------
                // Line beginning with prefix "--;?" is the description
                // of the form:
                // --;? number; type; description; value;
                // example:
                // --;? 1; CHAR; Číslo zboží od:; 00001;
                if (scriptLine.indexOf(PARAM_PREFIX) == 0) {
                    // The values are delimited by semicolons and are placed
                    // in the array "markerValues".
                    markerValues = getMarkerValues(scriptLine);
                    // Array list has as many elements (markerValues) as
                    // there is --;? lines and should be the same as
                    // there are markers in the SQL statement.
                    markerArrayList.add(markerValues);
                }
                // Line beginning with prefix "--;H" is the description
                // of the form:
                // --;H header1 ; header2 ; ...;
                // example with three headers:
                // --;H Číslo zboží, Název, Součet ceny,
                if (scriptLine.indexOf(HEADER_PREFIX) == 0) {
                    // The values are delimited by semicolons and are placed
                    // in the array "headerValues".
                    headerValues = getHeaderValues(scriptLine);
                    // Array list has as many elements (headerValues) as
                    // there are --;H lines.
                    headerArrayList.add(headerValues);
                }
                // Line beginning with prefix "--;T" is the description
                // of the form:
                // --;T spaceBefore; spaceAfter; nullPrintMark;
                // example with three headers:
                // --;T 1; 1; -;
                if (scriptLine.indexOf(TOTAL_PREFIX) == 0) {
                    // The values are delimited by semicolons and are placed
                    // in the array "totalValues".
                    totalValues = getTotalValues(scriptLine);
                    // Array list has as many elements (totalValues) as
                    // there are --;T lines but it should be only one.
                    // If more than one, the FIRST line will be used.
                    totalArrayList.add(totalValues);
                }
                // Line beginning with prefix --;D is the description of a mask
                // formatting the output decimal number for a column name
                if (scriptLine.indexOf(DECIMAL_PREFIX) == 0) {
                    patternValues = getPatternValues(scriptLine);
                    // Array list has as many elements (patternValues) as
                    // there are --;D lines
                    patternArrayList.add(patternValues);
                }
                // Line beginning with prefix --;P is the description of printing:
                // - printer size A4 or A3
                // - font size FSn (n is number in print points)
                // - page orientation PORTRAIT (default) or LANDSCAPE
                // - LMn left margin
                // - RMn right margin
                // - TMn top margin
                // - BMn bottom margin
                if (scriptLine.indexOf(PRINT_PREFIX) == 0) {
                    printValues = getPrintValues(scriptLine);
                    // Array list has as many elements (printValues) as
                    // there are --;P lines but it should be only one.
                    // If more than one, the first --;P the FIRST line will be used.
                    printArrayList.add(printValues);
                }
                // Line beginning with prefix --;t is the title line with
                // possibly inserted variables &column_name for values
                // of omitted columns
                if (scriptLine.indexOf(TITLE_PREFIX) == 0) {
                    // Array list has as many elements (titles) as
                    // there are --;t lines
                    titleArrayList.add(getTitleValues(scriptLine));
                }
                // Line beginning with prefix --;L is the description of a level
                // break
                if (scriptLine.indexOf(LEVEL_PREFIX) == 0) {
                    levelValues = getLevelValues(scriptLine);
                    // Array list has as many elements (levelValues) as
                    // there are --;L lines
                    levelArrayList.add(levelValues);
                }
                // Line beginning with prefix --;S is the description of summary
                // functions SUM, AVG, MAX, MIN, COUNT
                if (scriptLine.indexOf(SUMMARY_PREFIX) == 0) {
                    summaryValues = getSummaryValues(scriptLine);
                    // Array list has as many elements (summaryValues) as
                    // there are --;S lines.
                    summaryArrayList.add(summaryValues);
                }
                // Line beginning with prefix --;s is the description of
                // indicator texts for summary functions SUM, AVG, MAX, MIN, COUNT
                if (scriptLine.indexOf(SUM_IND_PREFIX) == 0) {
                    summaryIndValues = getSummaryIndValues(scriptLine);
                    // Array list has as many elements (summaryIndValues) as
                    // there are --;s lines but it should be only one.
                    // If more than one, the FIRST line will be used.
                    summaryIndArrayList.add(summaryIndValues);
                }
                // Line beginning with prefix --;O is the description of
                // column names to be omitted from output
                if (scriptLine.indexOf(OMIT_PREFIX) == 0) {
                    omitValues = getOmitValues(scriptLine);
                    // Array list has as many elements (sumValues) as
                    // there are --;O lines.
                    omitArrayList.add(omitValues);
                }
                // Append the line to the buffer
                statementBuf.append(scriptLine).append("\n");

                // Read tne next line
                scriptLine = infileScript.readLine();
            } // end while

            // Perform the last SQL statement
            // ------------------------------
            retCode = performSqlStatement(conn, scriptDescription);
            if (retCode[0].contains("UPDATE")) {
                numberOfUpdates++;
            }

            // Close the script file
            infileScript.close();

            // If at least one statement was UPDATE type (non-query)
            // display the whole script (with possible QUERY results)
            // at the end of multi-script processing.
            // ------------------------------------------------------
            if (numberOfUpdates > 0) {
                ArrayList<String> lineList = null;
                // Read all lines of the text file Print.txt into the array list
                try {
                    lineList = (ArrayList<String>) Files.readAllLines(workfilesTxt);
                } catch (IOException ioe) {
                    ioe.getStackTrace();
                }
                // Concatenate all text lines from the list obtained from the print
                // file with new lines.
                String printText = lineList.stream().reduce("", (a, b) -> a + b + "\n");
                // Set the text to the resultTextArea (containing titles already)
                resultTextArea.setText(printText);

                // Display the result window (without column headings)
                int nbrHdrLines = 0;
                headerArrayList = null;
                // Column separating spaces are empty
                ArrayList<String> columnHeaders = new ArrayList<>();
                new Q_PrintOneFile(scriptName, resultTextArea, nbrHdrLines, headerArrayList, printArrayList, columnHeaders);
            }
            // This is the end of script processing

        } catch (IOException ioe) {
            ioe.printStackTrace();
            scriptListMsg.setText("IOException!");
        }
        return retCode;
    }

    /**
     *
     * @param conn
     * @param scriptDescription
     * @return
     */
    protected String[] performSqlStatement(Connection conn, String scriptDescription) {

        retCode[1] = "";

        // Set the SQL statement description
        // ---------------------------------
        String stmtDescription = "";
        // If the first line is a simple comment beginning with "--"
        // in the first position and it is not "--;",
        // the description is the text following the -- characters.
        // Otherwise the description will be empty.
        String[] lines = statementBuf.toString().split("\n", 0);
        for (String line : lines) {
            if (!line.isEmpty() && line.length() >= 3) {
                if (line.substring(0, 2).equals("--") && !line.substring(0, 3).equals("--;")) {
                    stmtDescription = line.substring(2);
                    break;
                } else {
                    stmtDescription = "";
                    break;
                }
            }
        }

        // When no markers are in the statement, perform the statement directly.
        // ---------------
        if (markerArrayList.isEmpty()) {
            // Call statement performer for query or update
            Q_ScriptRun scriptRun = new Q_ScriptRun();
            retCode = scriptRun.runScript(conn, scriptName, stmtDescription, statementBuf.toString(),
                    markerArrayList, headerArrayList, totalArrayList, patternArrayList, printArrayList,
                    titleArrayList, levelArrayList, summaryArrayList, summaryIndArrayList,
                    omitArrayList);
            // If OK set info message
            retCode[0] += "runScript"; // Append to "QUERY" or "UPDATE"
        } // When there are some markers, display prompt for parameters.
        // ---------------------------
        else {
            // First check marker definition entries.
            // If entries are less or more than 4, correct them.
            for (int idx = 0; idx < markerArrayList.size(); idx++) {
                markerValues = markerArrayList.get(idx);
                if (markerValues.length != 4) {
                    int len = Math.min(markerValues.length, 4);
                    // Create a new array
                    // Copy existing entries
                    String[] markerVals = new String[4];
                    System.arraycopy(markerValues, 0, markerVals, 0, len);
                    // Add missing missing entries with empty strings
                    for (int in = len; in < 4; in++) {
                        markerVals[in] = "";
                    }
                    markerArrayList.set(idx, markerVals);
                }
            }
            // Prompt for marker values.
            Q_PromptParameters pmt = new Q_PromptParameters();
            // Method runPrompt returns a message string (is empty if OK)
            retCode = pmt.runPrompt(conn, scriptName, stmtDescription, statementBuf.toString(),
                    markerArrayList, headerArrayList, totalArrayList, patternArrayList, printArrayList,
                    titleArrayList, levelArrayList, summaryArrayList, summaryIndArrayList,
                    omitArrayList);
            retCode[0] += "runPrompt"; // Append to "QUERY" or "UPDATE"
        }

        return retCode;
    }

    /**
     * Extract marker parameter values from the --;? comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getMarkerValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line,
        markerValues = scriptLine.substring(PARAM_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Spaces on both sides of parameters are trimmed
        for (int i = 0; i < markerValues.length; i++) {
            // Marker parameter values are trimmed due to numbers
            markerValues[i] = markerValues[i].trim();
        }
        return markerValues;
    }

    /**
     * Extract header values from the --;H comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getHeaderValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line,
        headerValues = scriptLine.substring(HEADER_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Header values are NOT trimmed, they allow spaces before and after
        // text.
        return headerValues;
    }

    /**
     * Extract values from --;T comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getTotalValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line to an auxiliary
        // array
        totalValues = scriptLine.substring(TOTAL_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Replace original array elements only if there is a value between
        // semicolons
        if (totalValues.length > 0) {
            for (int i = 0; i < totalValues.length; i++) {
                // Replace original elements by the non-empty (trimmed) values
                // in the array
                totalValues[i] = totalValues[i].trim();
            }
        }
        return totalValues;
    }

    /**
     *
     * @param scriptLine
     * @return
     */
    protected String[] getPatternValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line,
        patternValues = scriptLine.substring(DECIMAL_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Spaces on both sides of parameters are trimmed
        for (int i = 0; i < patternValues.length; i++) {
            // Marker parameter values are trimmed due to numbers
            patternValues[i] = patternValues[i].trim();
        }
        return patternValues;
    }

    /**
     * Extract values from --;P comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getPrintValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line to an auxiliary
        // array
        ArrayList<String> printParams = new ArrayList<>();
        printValues = scriptLine.substring(PRINT_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Replace original array elements only if there is a value between
        // semicolons
        if (printValues.length > 0) {
            for (String printValue : printValues) {
                // System.out.println("printValues[idx]: " + printValues[idx]);
                // Trim original elements and add to array list
                printParams.add(printValue.trim());
            }
            // 6 print parameters
            if (printValues.length < 6) {
                for (int in = 0; in < 7 - printValues.length; in++) {
                    printParams.add("");
                }
            }
        }
        printValues = printParams.toArray(printValues);
        return printValues;
    }

    /**
     *
     * @param scriptLine
     * @return
     */
    protected String getTitleValues(String scriptLine) {
        // Return the whole text of the title line
        // with possible one or more variables &column_name
        return scriptLine.substring(TITLE_PREFIX.length());
    }

    /**
     * Extract values from --;L comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getLevelValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line to an auxiliary
        // array
        ArrayList<String> levelParams = new ArrayList<>();
        levelValues = scriptLine.substring(LEVEL_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Replace original array elements only if there is a value between
        // semicolons
        if (levelValues.length > 0) {
            for (String levelValue : levelValues) {
                // Trim original elements and add to array list
                levelParams.add(levelValues[0].trim()); // level number or empty
                if (levelValues.length > 1) {
                    // Level text is not trimmed!
                    levelParams.add(levelValues[1]); // level text of separation line
                }
                if (levelValues.length > 2) {
                    levelParams.add(levelValues[2].trim()); // level column name
                }
                if (levelValues.length > 3) {
                    levelParams.add(levelValues[3].trim()); // new page flag
                }
                // There are 4 level parameters - set missing ones to empty string
                if (levelValues.length < 4) {
                    for (int in = 0; in < 5 - levelValues.length; in++) {
                        levelParams.add("");
                    }
                }
            }
        }
        levelValues = levelParams.toArray(levelValues);
        return levelValues;
    }

    /**
     * Extract values from --;S comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getSummaryValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line to an auxiliary
        // array
        ArrayList<String> sumParams = new ArrayList<>();
        summaryValues = scriptLine.substring(SUMMARY_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Replace original array elements only if there is a value between
        // semicolons
        if (summaryValues.length > 0) {
            for (String summaryValue : summaryValues) {
                // Trim original elements and add to array list
                sumParams.add(summaryValue.trim());
            }
            // 6 summary parameters - pad missing with empty strings
            if (summaryValues.length < 6) {
                for (int in = 0; in < 7 - summaryValues.length; in++) {
                    sumParams.add("");
                }
            }
        }
        summaryValues = sumParams.toArray(summaryValues);
        return summaryValues;
    }

    /**
     * Extract values from --;s comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getSummaryIndValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line to an auxiliary
        // array
        ArrayList<String> sumParams = new ArrayList<>();
        summaryIndValues = scriptLine.substring(SUM_IND_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Replace original array elements only if there is a value between
        // semicolons
        if (summaryIndValues.length > 0) {
            for (String summaryIndValue : summaryIndValues) {
                // Trim original elements and add to array list
                sumParams.add(summaryIndValue.trim());
            }
            // 5 summary parameters - pad missing with empty strings
            if (summaryIndValues.length < 5) {
                for (int in = 0; in < 6 - summaryIndValues.length; in++) {
                    sumParams.add("");
                }
            }
        }
        summaryIndValues = sumParams.toArray(summaryIndValues);
        return summaryIndValues;
    }

    /**
     * Extract values from --;O comment line
     *
     * @param scriptLine
     * @return
     */
    protected String[] getOmitValues(String scriptLine) {
        // Extract parameters separated by semicolon from the line to an auxiliary
        // array
        ArrayList<String> sumParams = new ArrayList<>();
        omitValues = scriptLine.substring(OMIT_PREFIX.length()).split(PARAM_SEPARATOR, 0);
        // Replace original array elements only if there is a value between
        // semicolons
        if (omitValues.length > 0) {
            for (String omitValue : omitValues) {
                // Trim original elements and add to array list
                sumParams.add(omitValue.trim());
            }
        }
        omitValues = sumParams.toArray(omitValues);
        return omitValues;
    }

    /**
     * Call program to import SQL script from IBM i (IFS directory)
     *
     * @param selectedScript
     * @param scriptDescription
     * @return
     */
    public String[] importScript(String selectedScript, String scriptDescription) {
        // Call transfer of file
        retCode = Q_ImportOneFromAS400.transferOneFromAS400(selectedScript);
        String errMsg = retCode[1];
        scriptListMsg.setText(errMsg);
        if (retCode[0].contains("ERROR")) {
            scriptListMsg.setForeground(DIM_RED); // Dim red
        } else {
            scriptListMsg.setForeground(DIM_BLUE); // Dim blue
        }
        setVisible(true);
        return retCode;
    }

    String valueInMsg = "";

    /**
     * Get connection to DB2
     *
     * @return
     */
    protected Connection getConnectionDB2() {
        // Obtain object for connection to database DB2
        if (conn == null) {
            String messageText;
            conn = new Q_ConnectDB().connect();
            if (conn == null) {
                messageText = Q_ConnectDB.msg;
                retCode[0] += "ERROR";
                retCode[1] = messageText;
                messages.add(messageText);
                System.out.println(messageText);
            } else {
                connOk = locMessages.getString("ConnOk");
                currentLocale = Locale.forLanguageTag(language);
                DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
                        DateFormat.DEFAULT, currentLocale);
                Date date = new Date();
                String dateStr = formatter.format(date);
                messageText = connOk + host + " - " + dateStr;
                retCode[0] += "CONN_OK";
                retCode[1] = messageText;
                messages.add(messageText);
                System.out.println(messageText);
            }
        }
        return conn;
    }

    /**
     * Table model for the scriptList (JTable)
     */
    class Q_TableModel extends DefaultTableModel {

        protected static final long serialVersionUID = 1L;

        // Determines type of data in the cell
        @Override
        public Class<? extends Object> getColumnClass(int col) {
            return getValueAt(0, col).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            // Determines what cells are editable
            // The data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public Object getValueAt(int row, int col) {
            // System.out.println(
            // "getValueAt: (" + row + "," + col + "): " + records[row][col]);
            return records[row][col];
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            // System.out.println("setValueAt: (" + row + "," + col + "): " +
            // value);
            records[row][col] = value;
        }

    }

    /**
     * Fills the table with data obtained from the array "records".
     */
    protected void fillTable() {
        // Delete table rows
        tableModel.setRowCount(0);
        // Fill table with data from the array: records[][]
        for (int i = 0; i < nbrOfRows; i++) {
            tableModel.addRow((Object[]) records[i]);
        }
    }

    public static void main(String... strings) {
        new Q_ScriptRunCall();

    }
}
