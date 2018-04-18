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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import javax.swing.JTextArea;

/**
 * Edit and maintain SQL query scripts
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ScriptEditCall extends JFrame {

    protected static final long serialVersionUID = 1L;

    Q_Properties prop;

    Locale locale;
    ResourceBundle titles;
    String titEdit, scriptNam, scriptDes, searchScript;
    ResourceBundle buttons;
    String exit, new_script, edit_sel, del_sel, refresh, sav_to_svr, read_from_svr;
    ResourceBundle locMessages;
    String curDir, noRowUpd, noRowDel, noRowSav, file, wasDelLoc, wasDel, notInDir, script,
            wasSavedTo, ioError, inputError;
    // Object of calling class
    //static Q_Menu menu = Q_Menu.getQ_Menu();

    String scriptName;
    String scriptDescription;

    // scriptNames with the key of scriptName
    TreeMap<String, String> scriptNames = new TreeMap<>();
    Map.Entry<String, String> mapEntry;

    // Variables for stream files
    Path scriptDirectoryPath;
    Path scriptOutPath;
    Path scriptIn;
    BufferedReader infileScript;
    static String language;
    static String host;
    static String userName;
    static String password;

    // Components for scriptListGlobalPanel
    JPanel scriptListTitlePanel;
    JPanel scriptListPanel;
    JScrollPane scrollPane;
    JPanel messagePanel;
    JPanel scriptListButtonPanel;

    JLabel scriptListTitle;
    JLabel scriptListMsgLabel;
    String msgText;

    JButton scriptListExitButton;
    JButton scriptListAddButton;
    JButton scriptListUpdButton;
    JButton scriptListDelButton;
    JButton scriptListRefreshButton;
    JButton scriptListExportButton;
    JButton scriptListImportButton;

    JTextField searchField;
    JLabel searchLabel;
    String searchText;
    String searchPattern;
    String searchWildCard;

    // Dimensions of different stage views
    final Integer scriptListGlobalWidth = 1050;
    final Integer scriptListGlobalHeight = 700;
    final Integer scriptListPanelWidth = scriptListGlobalWidth;
    final Integer scriptListPanelHeight = scriptListGlobalHeight - 210;

    final Integer scriptListWidth = scriptListPanelWidth;
    final Integer scriptListHeight = scriptListPanelHeight;
    final Integer xLocation = 400;
    final Integer yLocation = 50;

    final Integer tableRowHeight = 24;
    final Integer firstTableColumnWidth = 200;
    final Integer secondTableColumnWidth = 660;

    // List (table) of records
    JTable scriptList;
    ScriptTableMouseAdapter scriptTableMouseAdapter;

    // Data model for the table
    DefaultTableModel tableModel;

    // Index of table row selected (by the user or the program)
    int scriptListIndexSel;
    // List selection model
    ListSelectionModel rowIndexList;

    String qryString;

    // Work file record data length
    Integer recordLength;

    // Visible data part of the record (before mask part)
    Integer dataLength;

    Object[][] records; // table contents
    int nbrOfRows; // number of rows in table

    JPanel scriptListGlobalPanel;
    JPanel dataGlobalPanel;
    Container listContentPane;
    Container dataContentPane;

    ListSelectionModel selModel;

    // Table columns for the list
    TableColumn colscriptName;
    TableColumn colQueryDesc;

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);

    Properties sysProp;
    String operatingSystem;
    final String WINDOWS = "WINDOWS";
    final String UNIX = "UNIX";
    String pcFileSep; // PC file separator ( / in unix, \ in Windows )

    Q_ScriptEdit scriptEdit;
    WindowScriptEditCallAdapter windowScriptEditCallAdapter;

    /**
     * Constructor
     */
    public Q_ScriptEditCall() {

        // Get or set application properties
        // ---------------------------------
        sysProp = System.getProperties();

        // Root symbols for local host are different in Windows and unix
        //
        if (sysProp.get("os.name").toString().contains("Windows")) {
            operatingSystem = WINDOWS;
            // Windows:
            pcFileSep = "\\"; // single \
        } else {
            // Unix systems:
            operatingSystem = UNIX;
            pcFileSep = "/";
        }
        // Menu bar in Mac operating system will be in the system menu bar
        if (sysProp.get("os.name").toString().toUpperCase().contains("MAC")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        prop = new Q_Properties();
        language = prop.getProperty("LANGUAGE");
        host = prop.getProperty("HOST");
        userName = prop.getProperty("USER_NAME");

        tableModel = new Q_TableModel();

        // Directory with script files
        scriptDirectoryPath = Paths.get(System.getProperty("user.dir"), "scriptfiles");
        // Files for storing and reading records
        scriptOutPath = Paths.get(System.getProperty("user.dir"), "scriptfiles", scriptName + ".sql");
        scriptIn = Paths.get(System.getProperty("user.dir"), "scriptfiles", scriptName + ".sql");

        Locale currentLocale = Locale.forLanguageTag(language);
        // Get resource bundle classes
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        scriptTableMouseAdapter = new ScriptTableMouseAdapter();
    }


    /**
     * Builds query script list in scriptListPanel
     *
     */
    public void buildScriptList() {

        // Localized titles
        titEdit = titles.getString("TitEdit");
        scriptNam = titles.getString("ScriptNam");
        scriptDes = titles.getString("ScriptDes");
        searchScript = titles.getString("SearchScript");

        // Localized button labels
        exit = buttons.getString("Exit");
        new_script = buttons.getString("New_script");
        edit_sel = buttons.getString("Edit_sel");
        del_sel = buttons.getString("Del_sel");
        refresh = buttons.getString("Refresh");
        sav_to_svr = buttons.getString("Sav_to_svr");
        read_from_svr = buttons.getString("Read_from_svr");

        scriptListTitlePanel = new JPanel();
        scriptListPanel = new JPanel();
        scrollPane = new JScrollPane();
        messagePanel = new JPanel();
        scriptListButtonPanel = new JPanel();

        scriptListTitle = new JLabel();
        searchField = new JTextField("");
        searchLabel = new JLabel(searchScript);
        searchLabel.setForeground(DIM_BLUE); // Dim blue

        scriptListMsgLabel = new JLabel();

        scriptListExitButton = new JButton(exit);
        scriptListExitButton.setMinimumSize(new Dimension(70, 35));
        scriptListExitButton.setMaximumSize(new Dimension(70, 35));
        scriptListExitButton.setPreferredSize(new Dimension(70, 35));

        scriptListAddButton = new JButton(new_script);
        scriptListAddButton.setMinimumSize(new Dimension(150, 35));
        scriptListAddButton.setMaximumSize(new Dimension(150, 35));
        scriptListAddButton.setPreferredSize(new Dimension(150, 35));

        scriptListUpdButton = new JButton(edit_sel);
        scriptListUpdButton.setMinimumSize(new Dimension(130, 35));
        scriptListUpdButton.setMaximumSize(new Dimension(130, 35));
        scriptListUpdButton.setPreferredSize(new Dimension(130, 35));

        scriptListDelButton = new JButton(del_sel);
        scriptListDelButton.setMinimumSize(new Dimension(130, 35));
        scriptListDelButton.setMaximumSize(new Dimension(130, 35));
        scriptListDelButton.setPreferredSize(new Dimension(130, 35));

        scriptListRefreshButton = new JButton(refresh);
        scriptListRefreshButton.setMinimumSize(new Dimension(140, 35));
        scriptListRefreshButton.setMaximumSize(new Dimension(140, 35));
        scriptListRefreshButton.setPreferredSize(new Dimension(140, 35));

        scriptListExportButton = new JButton(sav_to_svr);
        scriptListExportButton.setMinimumSize(new Dimension(130, 35));
        scriptListExportButton.setMaximumSize(new Dimension(130, 35));
        scriptListExportButton.setPreferredSize(new Dimension(130, 35));

        scriptListImportButton = new JButton(read_from_svr);
        scriptListImportButton.setMinimumSize(new Dimension(140, 35));
        scriptListImportButton.setMaximumSize(new Dimension(140, 35));
        scriptListImportButton.setPreferredSize(new Dimension(140, 35));

        // Empty table scriptList with data model
        scriptList = new JTable(tableModel);
        scriptList.addMouseListener(scriptTableMouseAdapter);

        // Attributes of the list table
        scriptList.setFont(new Font("Helvetica", Font.PLAIN, 13));
        scriptList.getTableHeader().setFont(new Font("Helvetica", Font.BOLD, 12));
        scriptList.getTableHeader().setPreferredSize(new Dimension(10, 26));
        scriptList.setGridColor(Color.LIGHT_GRAY);
        scriptList.setRowHeight(tableRowHeight);
        scriptList.setGridColor(Color.WHITE);

        // Behavior at manual change of column width
        scriptList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Title panel in the list window
        BoxLayout boxLayoutY = new BoxLayout(scriptListTitlePanel, BoxLayout.Y_AXIS);
        scriptListTitlePanel.setLayout(boxLayoutY);

        scriptListTitle.setText(titEdit);
        scriptListTitle.setFont(new Font("Helvetica", Font.PLAIN, 20));
        scriptListTitle.setMinimumSize(new Dimension(scriptListPanelWidth, 20));
        scriptListTitle.setPreferredSize(new Dimension(scriptListPanelWidth, 20));
        scriptListTitle.setMaximumSize(new Dimension(scriptListPanelWidth, 20));
        scriptListTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(scriptListTitle);

        searchLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(searchLabel);

        searchField.setMaximumSize(new Dimension(200, 25));
        searchField.setPreferredSize(new Dimension(200, 25));
        searchField.setMinimumSize(new Dimension(200, 25));
        searchField.setAlignmentX(Box.LEFT_ALIGNMENT);
        scriptListTitlePanel.add(searchField);

        scriptListTitlePanel.add(Box.createRigidArea(new Dimension(10, 10)));

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
        // Localized messages
        curDir = locMessages.getString("CurDir");
        noRowUpd = locMessages.getString("NoRowUpd");
        noRowDel = locMessages.getString("NoRowDel");
        file = locMessages.getString("File");
        wasDelLoc = locMessages.getString("WasDelLoc");
        wasDel = locMessages.getString("WasDel");
        notInDir = locMessages.getString("NotInDir");
        noRowSav = locMessages.getString("NoRowSav");
        script = locMessages.getString("Script");
        wasSavedTo = locMessages.getString("WasSavedTo");
        ioError = locMessages.getString("IOError");
        inputError = locMessages.getString("InputError");

        BoxLayout msgLayoutX = new BoxLayout(messagePanel, BoxLayout.X_AXIS);
        messagePanel.setLayout(msgLayoutX);
        messagePanel.setMinimumSize(new Dimension(scriptListWidth, 30));
        messagePanel.setPreferredSize(new Dimension(scriptListWidth, 30));
        messagePanel.setMaximumSize(new Dimension(scriptListWidth, 30));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Button panel in the list window
        BoxLayout buttonLayoutX = new BoxLayout(scriptListButtonPanel, BoxLayout.X_AXIS);
        scriptListButtonPanel.setLayout(buttonLayoutX);
        scriptListButtonPanel.add(scriptListExitButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListAddButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListUpdButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListRefreshButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListExportButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListImportButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.add(scriptListDelButton);
        scriptListButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        scriptListButtonPanel.setMinimumSize(new Dimension(scriptListPanelWidth, 50));
        scriptListButtonPanel.setPreferredSize(new Dimension(scriptListPanelWidth, 50));
        scriptListButtonPanel.setMaximumSize(new Dimension(scriptListPanelWidth, 50));

        // Set contents of the global panel in the list window
        scriptListGlobalPanel = new JPanel();
        BoxLayout globBoxLayoutY = new BoxLayout(scriptListGlobalPanel, BoxLayout.Y_AXIS);
        scriptListGlobalPanel.setLayout(globBoxLayoutY);
        scriptListGlobalPanel.add(Box.createRigidArea(new Dimension(1, 25)));
        scriptListGlobalPanel.add(scriptListTitlePanel);
        scriptListGlobalPanel.add(scriptListPanel);
        scriptListGlobalPanel.add(messagePanel);
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
        // Initial message indicating current directory
        scriptListMsgLabel.setForeground(Color.BLACK);
        scriptListMsgLabel.setText(curDir + System.getProperty("user.dir"));
        messagePanel.add(scriptListMsgLabel);

        // Row selection model (selection of single row)
        selModel = scriptList.getSelectionModel();
        scriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Row selection model registration
        selModel.addListSelectionListener(sl -> {
            rowIndexList = (ListSelectionModel) sl.getSource();
            scriptListIndexSel = rowIndexList.getLeadSelectionIndex();

            if (!rowIndexList.isSelectionEmpty()) {
                scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                selScriptFileName = (String) records[scriptListIndexSel][0];
            } else { // No row was selected
                scriptListIndexSel = -1;
            }
        });

        // Register window listener
        // ------------------------
        windowScriptEditCallAdapter = new WindowScriptEditCallAdapter();
        this.addWindowListener(windowScriptEditCallAdapter);

        // Set Search field activity
        // -------------------------
        searchField.addActionListener(a -> {
            // Read records from database and put it into a list
            readInputFiles();
            readLinesForScriptList();
            scriptListMsgLabel.setText("");
            messagePanel.add(scriptListMsgLabel);
            this.setVisible(true);
        });

        // Set Return button activity (return to type code list)
        // -----------------------------------------------------
        scriptListExitButton.addActionListener(a -> {
            //setVisible(false);
            //this.dispose();
            if (scriptEdit != null) {
                scriptEdit.dispose();
                scriptEdit = null;
            }
            Q_ScriptEditCall.this.dispose();
        });

        // Set Add button activity (on mouse click)
        // ----------------------------------------
        scriptListAddButton.addActionListener(a -> {
            String scriptFileName;
            messagePanel.removeAll();
            messagePanel.repaint();

            if (rowIndexList != null) { // row index not empty
                if (scriptListIndexSel >= 0) {
                    // If a row was selected get its index
                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                    // Get file name from the selected row
                    selScriptFileName = (String) records[scriptListIndexSel][0];
                } else {
                    selScriptFileName = "_New_script.sql";
                }
            } else {
                selScriptFileName = "_New_script.sql";
            }

            // Prompt for a new script name in a dialog
            fileName = new Q_GetTextFromDialog("CREATE NEW SCRIPT FILE")
                    .getTextFromDialog("Enter a file name with suffix  .sql  for the new script.", "New script: ",
                            " ", selScriptFileName, xLocation + 700, yLocation + 100);
            // If the dialog was canceled - report a message
            if (fileName == null) {
                scriptListMsgLabel.setText("No script file was created. Dialog was canceled.");
                scriptListMsgLabel.setForeground(DIM_RED); // red
                messagePanel.add(scriptListMsgLabel);
                this.setVisible(true);
                return;
            }
            // If the dialog was entered - continue in creating a new file
            scriptFileName = fileName;

            // Create a new empty file with the script name entered in the dialog
            try {
                Files.createFile(Paths.get("scriptfiles" + "/" + fileName));
                // The search field is set so that the only new script will be selected in the list.
                searchField.setText(fileName);
                readInputFiles();
                readLinesForScriptList();
                scriptListMsgLabel.setText("New script file  " + scriptFileName + "  was created.");
                scriptListMsgLabel.setForeground(DIM_BLUE); // blue
                messagePanel.add(scriptListMsgLabel);
                this.setVisible(true);

            } catch (Exception exc) {
                // File already exists.
                //exc.printStackTrace();
                searchField.setText(fileName);
                readInputFiles();
                readLinesForScriptList();
                scriptListMsgLabel.setText("Error: File  " + scriptFileName + "   already exists. - " + exc.toString());
                scriptListMsgLabel.setForeground(DIM_RED); // red
                messagePanel.add(scriptListMsgLabel);
                this.setVisible(true);
            }
        });

        // Set Update button activity (on mouse click)
        // -------------------------------------------
        scriptListUpdButton.addActionListener(a -> {
            messagePanel.removeAll();
            messagePanel.repaint();
            if (rowIndexList != null) { // row index not empty
                if (scriptListIndexSel >= 0) {
                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();

                    // Get index and seq. number of the selected row
                    selScriptFileName = (String) records[scriptListIndexSel][0];

                    // Call script editing program
                    JTextArea textArea = new JTextArea();
                    JTextArea textArea2 = new JTextArea();
                    scriptEdit = new Q_ScriptEdit(Q_ScriptEditCall.this, textArea, textArea2, selScriptFileName, "rewritePcFile");
                } else {
                    scriptListMsgLabel.setText(noRowUpd);
                    scriptListMsgLabel.setForeground(DIM_RED); // red
                    messagePanel.add(scriptListMsgLabel);
                    this.setVisible(true);
                }
            } else {
                scriptListMsgLabel.setText(noRowUpd);
                scriptListMsgLabel.setForeground(DIM_RED); // red
                messagePanel.add(scriptListMsgLabel);
                this.setVisible(true);
            }
        });

        // Set Delete button activity
        // --------------------------
        scriptListDelButton.addActionListener(a -> {
            // System.out.println("Row Index List: " + rowIndexList);
            // If the list is not empty
            if (rowIndexList != null) {
                if (scriptListIndexSel >= 0) {
                    // Get sequential number from selected row
                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                    selScriptFileName = (String) records[scriptListIndexSel][0];

                    // Clear message panel
                    messagePanel.removeAll();
                    messagePanel.repaint();
                    String messageText;

                    // Delete the file from local directory
                    Path scriptDelPath = Paths.get(System.getProperty("user.dir"), "scriptfiles",
                            selScriptFileName);
                    try {
                        Files.delete(scriptDelPath);

                        // Remove line with selected sequential number from the map
                        scriptNames.remove(selScriptFileName);
                        // Read the files again for refresh
                        readInputFiles();
                        // Refresh the table view
                        readLinesForScriptList();

                        messageText = file + selScriptFileName + wasDelLoc;
                        scriptListMsgLabel.setText(messageText);
                        scriptListMsgLabel.setForeground(DIM_BLUE); // blue
                        messagePanel.add(scriptListMsgLabel);
                        //System.out.println(messageText);
                    } catch (IOException ioe) {
                        messageText = ioError + ioe.getLocalizedMessage();
                        scriptListMsgLabel.setText(messageText);
                        scriptListMsgLabel.setForeground(DIM_RED); // red
                        System.out.println(messageText);
                    }

                    // Read the files again for refresh
                    readInputFiles();
                    // and refresh the table view
                    readLinesForScriptList();
                    this.setVisible(true);
                } else {
                    scriptListMsgLabel.setText(noRowDel);
                    scriptListMsgLabel.setForeground(DIM_RED); // red
                    messagePanel.add(scriptListMsgLabel);
                    this.setVisible(true);
                }
            } else {
                scriptListMsgLabel.setText(noRowDel);
                scriptListMsgLabel.setForeground(DIM_RED); // red
                messagePanel.add(scriptListMsgLabel);
                this.setVisible(true);
            }
        });

        // Set Refresh button activity
        // ---------------------------
        scriptListRefreshButton.addActionListener(a -> {
            // Read records from script files placed in directory "scriptfiles" and put them into a list
            readInputFiles();
            readLinesForScriptList();
            scriptListMsgLabel.setText("");
            messagePanel.add(scriptListMsgLabel);
            this.setVisible(true);
        });

        // Set Export file button activity
        // -------------------------------
        scriptListExportButton.addActionListener(a -> {
            // Clear message panel
            messagePanel.removeAll();
            messagePanel.repaint();

            scriptListMsgLabel.setText(script + scriptName + wasSavedTo + scriptOutPath.toString());
            scriptListMsgLabel.setForeground(DIM_BLUE); // Dim blue
            messagePanel.add(scriptListMsgLabel);

            scriptListMsgLabel.setText("");
            // System.out.println("Row Index List: " + rowIndexList);
            // If the list is not empty
            if (rowIndexList != null) {
                if (scriptListIndexSel >= 0) {
                    // Get script file name from selected row
                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                    selScriptFileName = (String) records[scriptListIndexSel][0];
                    // System.out.println("selScriptFileName: " + selScriptFileName);
                    // Call export to AS/400 IFS directory directly - file name is known.
                    // It is not necessary to request the user for the exported file name.
                    String msg = Q_ExportOneToAS400.transferOneToAS400(selScriptFileName);
                    scriptListMsgLabel.setText(msg);
                    scriptListMsgLabel.setForeground(DIM_BLUE); // blue
                    messagePanel.add(scriptListMsgLabel);
                    setVisible(true);
                } else {
                    scriptListMsgLabel.setText(noRowSav);
                    scriptListMsgLabel.setForeground(DIM_RED); // red
                    messagePanel.add(scriptListMsgLabel);
                    this.setVisible(true);
                }
            } else {
                scriptListMsgLabel.setText(noRowSav);
                scriptListMsgLabel.setForeground(DIM_RED); // Dim red
                messagePanel.add(scriptListMsgLabel);
                this.setVisible(true);
            }
        });

        // Set Import file button activity
        // -------------------------------
        scriptListImportButton.addActionListener(a -> {

            scriptListMsgLabel.setText("");
            scriptListMsgLabel.setForeground(DIM_BLUE); // Dim blue
            messagePanel.add(scriptListMsgLabel);

            // Clear message panel
            messagePanel.removeAll();
            messagePanel.repaint();

            // System.out.println("Row Index List: " + rowIndexList);
            // If the list is not empty
            if (rowIndexList != null) {
                if (scriptListIndexSel >= 0) {
                    // scriptListMsgLabel.setText("");
                    messagePanel.add(scriptListMsgLabel);
                    scriptListIndexSel = rowIndexList.getLeadSelectionIndex();
                    // Get script name and desctiption from the selected row
                    String selectedScript = (String) records[scriptListIndexSel][0];
                    scriptDescription = (String) records[scriptListIndexSel][1];
                    // Import script whose name is known. Method importScript
                    // makes use of class ImportOneFromAS400.
                    String[] retCode = importScript(selectedScript, scriptDescription);
                    // Handle messages
                    if (retCode[0].equals("runScript") && !retCode[1].isEmpty()) {
                        scriptListMsgLabel.setText(retCode[1]);
                        messagePanel.add(scriptListMsgLabel);
                        setVisible(true);
                    }
                } // Row index of table line is negative - Prompt for script name to import
                else {
                    // Script file name is not known - the user must enter a name in a dialog.
                    Q_PromptForScriptName prompt = new Q_PromptForScriptName();
                    prompt.runPrompt();
                }
            } // Row index list is empty - Prompt for script name to import
            else {
                Q_PromptForScriptName prompt = new Q_PromptForScriptName();
                prompt.runPrompt();
            }
        });

        // Read script files from directory "scriptfiles" and puts data
        // in the tree map "scriptNames" (String, StringBuilder)
        msgText = readInputFiles();
        scriptListMsgLabel.setText(msgText);
        messagePanel.add(scriptListMsgLabel);

        if (msgText.isEmpty()) {
            scriptListMsgLabel.setForeground(DIM_BLUE);
        } else {
            scriptListMsgLabel.setText(msgText);
            scriptListMsgLabel.setForeground(DIM_RED);
        }
        messagePanel.add(scriptListMsgLabel);

        // Prepare scriptList table for display
        // ------------------------------------
        // Read all records from the input file and put its data into the corresponding rows/columns.
        readLinesForScriptList();


        // Activate content of the window
        // ------------------------------
        listContentPane = this.getContentPane();
        // ( top, left, bottom, right )
        scriptListGlobalPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        listContentPane.add(scriptListGlobalPanel);

        setSize(scriptListGlobalWidth, scriptListGlobalHeight);
        this.setLocation(xLocation, yLocation);
        pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Selected script file name
    String selScriptFileName;

    /**
     * Imports the script from IBM i (IFS directory) to local directory "scriptfiles"
     *
     * @param selectedScript
     * @param scriptDescription
     * @return
     */
    public String[] importScript(String selectedScript, String scriptDescription) {
        // Call transfer of file
        String[] retCode = Q_ImportOneFromAS400.transferOneFromAS400(selectedScript);
        String errMsg = retCode[1];
        scriptListMsgLabel.setText(errMsg);
        if (retCode[0].equals("ERROR")) {
            scriptListMsgLabel.setForeground(DIM_RED); // Dim red
        } else {
            scriptListMsgLabel.setForeground(DIM_BLUE); // Dim blue
        }
        setVisible(true);
        return retCode;
    }

    int rows;

    /**
     * Read all scriptNames from the tree map and place its data into array records to display the
     * JTable of the script list
     *
     * @return int - last index of the array of table records
     */
    protected int readLinesForScriptList() {
        // Array of rows and columns with values for the script list table
        records = new Object[nbrOfRows][2];

        // If scriptNames TreeMap is not empty, fill the list with these scriptNames
        if (!scriptNames.isEmpty()) {
            rows = 0;

            // Read first line
            mapEntry = scriptNames.firstEntry();
            while (mapEntry != null) {
                // Add record data from scriptNames to the array
                records[rows][0] = mapEntry.getKey();
                records[rows][1] = mapEntry.getValue();

                // Read next line from the file
                mapEntry = scriptNames.higherEntry(mapEntry.getKey());

                rows++;
            }
        }
        // Fill table model with data from records array
        fillTable();
        // This is the row index of the last record in the list
        return scriptNames.size();
    }

    /**
     * Reads script files from directory "" and puts data in a tree map "scriptNames" (String,
     * StringBuilder)
     *
     * @return
     */
    String fileName;

    protected String readInputFiles() {
        // Prepare list (tree map) of queries from script files placed in directory "scriptfiles"
        try {
            // Read script file names from the directory "scriptfiles" in the window list of queries
            if (Files.isDirectory(scriptDirectoryPath)) {
                String[] fileNames = scriptDirectoryPath.toFile().list();
                if (fileNames != null) {
                    nbrOfRows = 0;
                    scriptNames.clear();
                    searchText = searchField.getText().toUpperCase();

                    // Prepare wild card with * and ? for searching files
                    searchPattern = searchText;
                    if (searchText.isEmpty()) {
                        searchPattern = "*";
                    }
                    searchWildCard = searchPattern.replace("*", ".*");
                    searchWildCard = searchWildCard.replace("?", ".");

                    // Process list of script files
                    for (String file_name : fileNames) {
                        // Get only files that conform to the search text.
                        if (file_name.toUpperCase().matches(searchWildCard)) {
                            // Create path to the script file
                            scriptIn = Paths.get(System.getProperty("user.dir"), "scriptfiles", file_name);

                            // Open the script file
                            infileScript = Files.newBufferedReader(scriptIn, Charset.forName("UTF-8"));

                            // If the first line is a simple comment beginning with "--" in the first position, and it is not "--;",
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
                            // Put the file name in the tree map
                            scriptNames.put(file_name, scriptDescription);
                            nbrOfRows++;
                        }
                        fileName = file_name;
                    }
                }
            }
            return "";
        } catch (IOException exc) {
            exc.printStackTrace();
            String message = inputError + fileName;
            System.out.println(message);
            return message;
        }
    }

    String valueInMsg = "";

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
     * Fills table with data obtained from the array "records".
     */
    protected void fillTable() {
        // Delete table rows
        tableModel.setRowCount(0);
        // Fill table with data from the array: records[][]
        for (int i = 0; i < nbrOfRows; i++) {
            tableModel.addRow((Object[]) records[i]);
        }
    }

    /**
     * Launch script editor on double click
     */
    class ScriptTableMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            // On double click on a table row - call Q_ScriptEdit for the selected file
            if (mouseEvent.getClickCount() == 2) {
                // Call script editing program
                JTextArea textArea = new JTextArea();
                JTextArea textArea2 = new JTextArea();
                scriptEdit = new Q_ScriptEdit(Q_ScriptEditCall.this, textArea, textArea2, selScriptFileName, "rewritePcFile");
            }
        }
    }

    /**
     * Window adapter closes the Q_ScriptEdit and also this window.
     */
    class WindowScriptEditCallAdapter extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent we) {
            if (scriptEdit != null) {
                scriptEdit.dispose();
                scriptEdit = null;
            }
            Q_ScriptEditCall.this.dispose();
        }
    }
}
