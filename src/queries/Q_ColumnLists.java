/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the columnListArea.
 */
package queries;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

/**
 *
 * @author vzupka
 */
public class Q_ColumnLists extends JFrame {

    Properties prop;
    String filePathString;
    Path parPath = Paths.get(System.getProperty("user.dir"), "paramfiles", "Q_Parameters.txt");
    BufferedReader infile;

    // Connection to DB2
    static Connection conn;

    // Return code array: [0] - tag "runScript" or "runPrompt",
    // [1] - error message text
    String[] retCode = new String[2];

    ArrayList<String> messages = new ArrayList<>();
    String connOk;
    String language, host, userName, libraryList;

    Locale currentLocale;
    ResourceBundle locMessages;

    DatabaseMetaData dbMetaData;

    ResultSet rs;

    String[] libraries;
    String library;
    String table;
    String column;
    String columnList;

    // Localized text objects
    Locale locale;
    ResourceBundle titles;
    ResourceBundle buttons;
    String titleCol, dragNames;
    String deleteCol;
    String buildColList;
    String buildColsItem;
    String removeSelCols;
    String removeAllCols;
    String selectAllCols;
    String selAllItems;
    String editSelCols;
    String selectAllColsItem;

    String schemaLabelText;
    String tableLabelText;
    String titleResCol;
    String copyToEditor;
    String horiz, horizTip, append, appendTip;

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);
    Color VERY_LIGHT_BLUE = Color.getHSBColor(0.60f, 0.05f, 0.98f);

    // Empty left vector for list of all columns
    Vector<String> vectorLeft = new Vector<>();

    // Left ist containing the vector
    JList<String> listLeft = new JList<>(vectorLeft);

    // Right list has also a list model
    JList<String> listRight = new JList<>();
    // List model for right list
    DefaultListModel<String> listRightModel = new DefaultListModel<>();
    // Drag and drop Transfer handler for right list
    ListLeftRightTransfHdlr listLeftRightTransfHdlr = new ListLeftRightTransfHdlr();

    JLabel message = new JLabel("");

    JPopupMenu leftPopupMenu = new JPopupMenu();
    JMenuItem selectAllLeftItems = new JMenuItem();

    JPopupMenu rightPopupMenu = new JPopupMenu();
    JMenuItem selectAllRightItems = new JMenuItem();

    JMenuItem removeSelColsItem = new JMenuItem();
    JMenuItem buildColumnsItem = new JMenuItem();

    static int windowWidth = 585;
    static int windowHeight = 700;

    JPanel globalPanel = new JPanel();
    JLabel topTitle;
    Font topTitleFont;
    Font columnListAreaFont;
    JLabel schemaLabel;
    JLabel tableLabel;
    JLabel dragPrompt;
    JButton removeSelColsButton;
    JButton buildColListButton;

    JScrollPane scrollPaneLeft = new JScrollPane(listLeft);
    JScrollPane scrollPaneRight;

    int scrollPaneListWidth = 190;
    int scrollPaneListHeight = 220;

    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();

    JLabel colListTitle;
    Font colListTitleFont;

    JLabel copyToEditPrompt;

    JCheckBox horizontal;
    boolean horizontalSet = false;
    JCheckBox appended;
    boolean appendedSet = false;

    JTextArea columnListArea;
    JScrollPane scrollPaneEdit;

    int scrollPaneEditWidth = windowWidth - 40;
    int scrollPaneEditHeight = 220;

    GroupLayout layout = new GroupLayout(globalPanel);

    JComboBox libraryComboBox = new JComboBox();
    LibraryComboBoxListener libraryComboBoxListener = new LibraryComboBoxListener();

    JComboBox fileComboBox = new JComboBox();
    FileComboBoxListener fileComboBoxListener = new FileComboBoxListener();

    LeftListPromptMouseListener leftListPromptMouseListener = new LeftListPromptMouseListener();
    RightListPromptMouseListener rightListPromptMouseListener = new RightListPromptMouseListener();

    Q_ScriptEdit editFile;

    /**
     *
     * Constructor
     *
     * @param editFile
     */
    public Q_ColumnLists(Q_ScriptEdit editFile) {
        this.editFile = editFile;

        prop = new Properties();
        String encoding = System.getProperty("file.encoding", "UTF-8");
        try {
            // Get values from properties and set variables and text fields
            infile = Files.newBufferedReader(parPath, Charset.forName(encoding));
            prop.load(infile);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        host = prop.getProperty("HOST");
        userName = prop.getProperty("USER_NAME");
        language = prop.getProperty("LANGUAGE");
        libraryList = prop.getProperty("LIBRARY_LIST");

        // Get resource bundle class
        currentLocale = Locale.forLanguageTag(language);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        // Split library names from the library list into an array 
        // - separated by one or more non-word characters (\W):
        libraries = libraryList.split("\\W+");

        // Activate combo box listeners
        libraryComboBox.addActionListener(libraryComboBoxListener);
        fileComboBox.addActionListener(fileComboBoxListener);

    }

    /**
     * Create window with listeners.
     */
    protected void createWindow() {

        // Get connection to the database
        try {
            conn = getConnectionDB2();
            // Get meta-data of the database.
            dbMetaData = conn.getMetaData();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        language = prop.getProperty("LANGUAGE"); // local language

        // Localization classes
        currentLocale = Locale.forLanguageTag(language);
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);

        // Localized titles
        titleCol = titles.getString("TitleCol");

        schemaLabelText = titles.getString("SchemaLabel");
        tableLabelText = titles.getString("TableLabel");

        dragNames = titles.getString("DragNames");
        buildColsItem = titles.getString("BuildColsItem"); // Popup menu - right list
        removeSelCols = titles.getString("RemoveSelCols"); // Popup menu - right list
        selAllItems = titles.getString("SelAllItems"); // Popup menu - left list

        titleResCol = titles.getString("TitleResCol");
        copyToEditor = titles.getString("CopyToEditor");

        horiz = titles.getString("Horiz");
        append = titles.getString("Append");
        horizTip = titles.getString("HorizTip");
        appendTip = titles.getString("AppendTip");


        // Start window construction
        // -------------------------

        topTitle = new JLabel(titleCol);

        schemaLabel = new JLabel(schemaLabelText);
        schemaLabel.setForeground(DIM_BLUE);

        tableLabel = new JLabel(tableLabelText);
        tableLabel.setForeground(DIM_BLUE);

        dragPrompt = new JLabel(dragNames);
        dragPrompt.setForeground(DIM_BLUE); // Dim blue

        topTitleFont = new Font("Helvetica", Font.PLAIN, 20);
        topTitle.setFont(topTitleFont);

        libraryComboBox.setPreferredSize(new Dimension(130, 20));
        libraryComboBox.setMaximumSize(new Dimension(130, 20));
        libraryComboBox.setMinimumSize(new Dimension(130, 20));
        libraryComboBox.setEditable(true);
        libraryComboBox.setToolTipText("Library list.");

        fileComboBox.setPreferredSize(new Dimension(130, 20));
        fileComboBox.setMaximumSize(new Dimension(130, 20));
        fileComboBox.setMinimumSize(new Dimension(130, 20));
        fileComboBox.setEditable(true);
        fileComboBox.setToolTipText("File list.");

        // Localized button labels
        removeSelCols = buttons.getString("RemoveSelCols");
        buildColList = buttons.getString("BuildColList");

        removeSelColsButton = new JButton(removeSelCols);
        buildColListButton = new JButton(buildColList);

        leftPopupMenu.add(selectAllLeftItems);
        selectAllLeftItems.setText(selAllItems); // Left popup menu
        selectAllLeftItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        rightPopupMenu.add(removeSelColsItem);
        rightPopupMenu.add(buildColumnsItem);
        rightPopupMenu.add(selectAllRightItems);
        buildColumnsItem.setText(buildColsItem);
        removeSelColsItem.setText(removeSelCols);
        selectAllRightItems.setText(selAllItems);
        selectAllRightItems.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        // Fill the left list with column names.
        listLeft.setListData(vectorLeft);
        // Set the left list as enabled for dragging from.
        listLeft.setDragEnabled(true);

        // Activate mouse listener for the LĘFT list
        listLeft.addMouseListener(leftListPromptMouseListener);

        message.setText("");

        // Create right list (user library list) using the DefaultListModel 
        listRight = new JList(listRightModel);
        listRight.setDragEnabled(true);
        listRight.setDropMode(DropMode.INSERT);

        // Activate mouse listener for the RIGHT list
        listRight.addMouseListener(rightListPromptMouseListener);

        scrollPaneLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Maximum width += 1000
        scrollPaneLeft.setMaximumSize(new Dimension(scrollPaneListWidth + 1000, scrollPaneListHeight));
        // Preferred width = 0
        scrollPaneLeft.setPreferredSize(new Dimension(0, scrollPaneListHeight));
        scrollPaneLeft.setBackground(scrollPaneLeft.getBackground());
        scrollPaneLeft.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        scrollPaneRight = new JScrollPane(listRight);
        scrollPaneRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Maximum width += 1000
        scrollPaneRight.setMaximumSize(new Dimension(scrollPaneListWidth + 1000, scrollPaneListHeight));
        // Preferred width = 0
        scrollPaneRight.setPreferredSize(new Dimension(0, scrollPaneListHeight));
        scrollPaneRight.setBackground(scrollPaneLeft.getBackground());
        scrollPaneRight.setBorder(BorderFactory.createEmptyBorder());

        colListTitle = new JLabel(titleResCol);
        colListTitleFont = new Font("Helvetica", Font.PLAIN, 20);
        colListTitle.setFont(colListTitleFont);

        appended = new JCheckBox(append);
        appended.setToolTipText(appendTip);
        horizontal = new JCheckBox(horiz);
        horizontal.setToolTipText(horizTip);

        // Create text area for edited column lists
        columnListArea = new JTextArea();
        columnListArea.setFont(listLeft.getFont());
        
        // Create scroll pane for columnListArea 
        scrollPaneEdit = new JScrollPane(columnListArea);
        scrollPaneEdit.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPaneEdit.setBackground(scrollPaneEdit.getBackground());
        scrollPaneEdit.setBorder(BorderFactory.createEmptyBorder());

        // Light sky blue for IBM i (PC color is resolved in the constructor later).
        scrollPaneEdit.setBackground(VERY_LIGHT_BLUE);
        columnListArea.setBackground(VERY_LIGHT_BLUE);

        // Disable dragging from the column list text area (important)
        //columnListArea.setDragEnabled(false);
        // Disable editing the column list text area (important)
        //columnListArea.setEditable(false);

        // Fill the LIBRARY combo box with LIBRARY NAMES.
        for (String lib : libraries) {
            libraryComboBox.addItem(lib);
        }

        // Initially set the first item of the LIBRARY combo box as selected.
        libraryComboBox.setSelectedIndex(0);

        // Fill the FILE combo box with FILE_NAMES.
        try {
            // Fill the FILE combo box with FILE NAMES from the selected library.
            library = (String) libraryComboBox.getSelectedItem();
            // Get table descriptions in the schema and put them in the fileComboBox list.
            // catalog, schema, table-pattern, table-types:
            rs = dbMetaData.getTables(null, library, null, null);
            boolean ok = rs.next();
            while (ok) {
                // Get table name from the table description. 
                table = rs.getString("TABLE_NAME");
                fileComboBox.addItem(table);
                //System.out.println("Schema/Table : " + library + "/" + table);
                ok = rs.next();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        // Generate initial COLUMN LIST from the full number of table columns    
        initialColumnList();

        copyToEditPrompt = new JLabel(copyToEditor);
        copyToEditPrompt.setForeground(DIM_BLUE); // Dim blue

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(topTitle)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(libraryComboBox)
                        .addComponent(schemaLabel)
                )
                .addGroup(layout.createSequentialGroup()
                        .addComponent(fileComboBox)
                        .addComponent(tableLabel)
                )
                .addGap(20)
                .addComponent(dragPrompt)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPaneLeft)
                        .addComponent(scrollPaneRight)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(removeSelColsButton)
                                .addComponent(buildColListButton)
                                .addComponent(horizontal)
                                //.addComponent(appended)
                        )
                )
                .addGap(20)
                .addComponent(colListTitle)
                .addComponent(copyToEditPrompt)
                .addComponent(scrollPaneEdit)
                .addComponent(message));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(topTitle)
                .addGroup(layout.createParallelGroup()
                        .addComponent(libraryComboBox)
                        .addComponent(schemaLabel)
                )
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileComboBox)
                        .addComponent(tableLabel)
                )
                .addGap(20)
                .addComponent(dragPrompt)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPaneLeft)
                        .addComponent(scrollPaneRight)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(removeSelColsButton)
                                .addComponent(buildColListButton)
                                .addComponent(horizontal)
                                //.addComponent(appended)
                        )
                )
                .addGap(20)
                .addComponent(colListTitle)
                .addComponent(copyToEditPrompt)
                .addComponent(scrollPaneEdit)
                .addComponent(message));


        // Listeners
        // =========

        // Transfer handler for the RIGHT list
        // ----------------
        listRight.setTransferHandler(listLeftRightTransfHdlr);

        // Popup menu listeners
        // --------------------

        // LEFT popup menu item listener - select all items in the left frame
        selectAllLeftItems.addActionListener(ae -> {
            listLeft.setSelectionInterval(0, vectorLeft.size() - 1);
        });

        // Right popup menu item listener - remove selected columns
        removeSelColsItem.addActionListener(ae -> {
            removeSelectedColumns();
        });

        // Right popup menu item listener - select all items in the right frame
        selectAllRightItems.addActionListener(ae -> {
            listRight.setSelectionInterval(0, listRightModel.size() - 1);
        });

        // Right popup menu item listener - build column list
        buildColumnsItem.addActionListener(a -> {
            generateColumnList();
        });

        // Button listeners
        // ----------------

        // Remove selected columns button listener
        removeSelColsButton.addActionListener(a -> {
            removeSelectedColumns();
        });

        // Build column list button listener
        buildColListButton.addActionListener(a -> {
            generateColumnList();
        });


        // Horizontal check box listener
        // -----------------------------
        horizontal.addItemListener(il -> {
            Object source = il.getSource();
            if (source == horizontal) {
                if (horizontal.isSelected()) {
                    horizontalSet = true;
                } else {
                    horizontalSet = false;
                }
            }
        });

        // Appended check box listener
        // ---------------------------
        appended.addItemListener(il -> {
            Object source = il.getSource();
            if (source == appended) {
                if (appended.isSelected()) {
                    appendedSet = true;
                } else {
                    appendedSet = false;
                }
            }
        });

        //
        // Finish window construction
        //
        globalPanel.setLayout(layout);
        globalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Container cont = getContentPane();
        cont.add(globalPanel);

        // Make window visible 
        setSize(windowWidth, windowHeight);
        //setLocation(300, 320);
        setLocation(editFile.windowX + 670, editFile.windowY);
        setVisible(true);
        //pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        columnListArea.requestFocus(); // Focus in the text area (not to the first text field - schema)
    }

    /**
     * Get connection to DB2.
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
     * Copy selected items from the left frame to the right frame.
     *
     * @param index
     */
    protected void copyLeftToRight(Integer index) {
        // Copy selected items from the left frame to the right frame
        List<String> itemsLeft = listLeft.getSelectedValuesList();
        if (!listRightModel.isEmpty()) {
            // Add selected items from the left list after non-empty right list 
            int lastRightIndex = listRightModel.size() - 1;
            for (int idx = itemsLeft.size() - 1; idx >= 0; idx--) {
                boolean foundInRight = false;
                // Find out if the left item matches any right item
                for (int jdx = 0; jdx < lastRightIndex + 1; jdx++) {
                    if (itemsLeft.get(idx).equals(listRightModel.get(jdx))) {
                        foundInRight = true;
                    }
                }
                // If the left item does not match any item in the right box vector
                // add the item at the end of the vector items in the right box.
                if (!foundInRight) {
                    if (index == null) {
                        listRightModel.addElement(itemsLeft.get(idx));
                    } else {
                        listRightModel.insertElementAt(itemsLeft.get(idx), index);
                    }
                }
            }
        } else {
            // Add selected items from the whole left list in the empty right list
            for (int idx = 0; idx < itemsLeft.size(); idx++) {
                listRightModel.add(idx, itemsLeft.get(idx));
            }
        }
        // Clear selection in the left list
        listLeft.clearSelection();
        repaint();
    }

    /**
     * Column list from all items of the RIGHT frame is generated.
     */
    protected void generateColumnList() {
        if (!appendedSet) {
            // Empty column list text area
            columnListArea.setText("");
            columnListArea.removeAll();
        }

        // Get number of elements in the right frame
        Enumeration enumeration = listRightModel.elements();
        int numberOfAllColumns = 0;
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            numberOfAllColumns++;
        }
        System.out.println("numberOfAllColumns: "+numberOfAllColumns);
        // Only non-empty column list can be generated
        if (numberOfAllColumns > 0) {
            if (!appendedSet) {
                // Empty column list to be generated
                columnList = "";
            } else {
                if (columnList != null) {
                    columnList += ",";
                } else {
                    columnList = ",";
                }
                if (!horizontalSet) {
                    columnList += "\n";
                } else {
                    columnList += " ";
                }
            }
            for (int index = 0; index < numberOfAllColumns; index++) {
                // Add the column to the column list
                columnList += listRightModel.get(index);
                // Add comma and new line but the last column
                if (index != numberOfAllColumns - 1) {
                    columnList += ",";
                    if (!horizontalSet) {
                        columnList += "\n";
                    } else {
                        columnList += " ";
                    }
                }
            }
            columnListArea.setText(columnList);
        }
    }

    /**
     * Remove selected items from the RIGHT frame.
     */
    protected void removeSelectedColumns() {
        List<String> itemsRight = listRight.getSelectedValuesList();
        if (!itemsRight.isEmpty() && !listRightModel.isEmpty()) {
            for (Object item : itemsRight) {
                listRightModel.removeElement(item);
            }
        }
    }

    /**
     *
     */
    class LibraryComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            // Deactivate file combo boxes se that writing in it does not invoke its listener.
            fileComboBox.removeActionListener(fileComboBoxListener);

            // Fill the file combo box with file names from the selected library.
            library = (String) libraryComboBox.getSelectedItem();
            fileComboBox.removeAllItems();
            try {
                fileComboBox.removeAllItems();
                // Get table descriptions in the schema.
                // catalog, schema, table-pattern, table-types:
                rs = dbMetaData.getTables(null, library, null, null);
                boolean ok = rs.next();
                while (ok) {
                    // Get table name from the table description.
                    table = rs.getString("TABLE_NAME");
                    fileComboBox.addItem(table);
                    //System.out.println("Schema/Table : " + library + "/" + table);
                    ok = rs.next();
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            // Set all column names of the table in the LEFT list
            setColNamesToLeftList();

            // Activate file combo box again.
            fileComboBox.addActionListener(fileComboBoxListener);
        }
    }

    /**
     * Set column names to LEFT list and remove all elements from the RIGHT list.
     */
    class FileComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            setColNamesToLeftList();
            //listRightModel.removeAllElements();
        }
    }

    /**
     * All column names from the table (view) is set to the list in the LEFT frame
     * and the full column list is generated.
     */
    protected void setColNamesToLeftList() {

        // Get column names from the table description and set them in both LEFT and RIGHT list.
        table = (String) fileComboBox.getSelectedItem();
        vectorLeft.removeAllElements();
        try {
            // Get column names from the table description and set them in the left list.
            // catalog, schema, table-pattern, table-types:
            rs = dbMetaData.getColumns(null, library, table, null);
            boolean ok = rs.next();
            while (ok) {
                column = rs.getString("COLUMN_NAME");
                vectorLeft.addElement(column);
                //System.out.println("Column : " + library + "/" + table + "/" + column);
                ok = rs.next();
            }
            listLeft.setListData(vectorLeft);

            // Generate initial full column list of the table (view)
            initialColumnList();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Generates a column list from all table columns (vectorLeft).
     */
    protected void initialColumnList() {
        columnListArea.setText(""); // Empty column list text area
        columnListArea.removeAll();
        columnList = ""; // Empty column list to be generated
        for (int elem = 0; elem < vectorLeft.size(); elem++) {
            columnList += vectorLeft.elementAt(elem);
            if (elem != vectorLeft.size() - 1) {
                columnList += ",\n";
            }
        }
        columnListArea.setText(columnList);
    }

    /**
     *
     */
    class ListLeftRightTransfHdlr extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport info) {
            // Check for String flavor
            if (!info.isDrop()) {
                return false;
            }
            return true;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection("");
        }

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }

            JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
            int index = dl.getIndex();
            boolean insert = dl.isInsert();
            // Perform the actual import.  
            if (insert) {
                copyLeftToRight(index);
            }
            return true;
        }
    }

    /**
     * Left list mouse listener
     */
    class RightListPromptMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

            Point pt = new Point(mouseEvent.getX(), mouseEvent.getY());

            // On right click show the popup menu with commands.
            if ((mouseEvent.getButton() == MouseEvent.BUTTON3)) {
                // Show the right popup menu
                rightPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }

    /**
     * Left list mouse listener
     */
    class LeftListPromptMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

            Point pt = new Point(mouseEvent.getX(), mouseEvent.getY());

            // On right click show the popup menu with commands.
            if ((mouseEvent.getButton() == MouseEvent.BUTTON3)) {
                // Show the right popup menu
                leftPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }
    }

}
