package queries;

import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Prompt for entering SQL parameter values and run query
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_PromptParameters extends JDialog {

    private static final long serialVersionUID = 1L;

    Q_Properties prop;

    // Locale locale;
    Locale currentLocale;
    String language;
    ResourceBundle titles;
    String scrName, defSelVals;
    ResourceBundle buttons;
    String exit, dsp_orig, dsp_input, enter;
    ResourceBundle locMessages;
    String noConn, orderErr, correctRow, orOrig, decNr, intNr, dateForm, timeForm, timeStampForm,
            otherTypes, stmtSuccess;

    // Connection object passed in
    Connection conn;

    // GUI objects
    GridBagLayout gridBagLayout;
    GridBagConstraints gbc;

    // Field labels array
    JLabel[] fldLbls;
    // Text fields array
    JTextField[] txtFlds;

    // ..Dimensions of different views
    final Integer xLocation = 300;
    final Integer yLocation = 100;

    Integer dataPanelGlobalWidth = 800;
    final Integer dataPanelWidth = 310;
    final Integer txtFldHeight = 20;
    Integer rowHeight = 0;
    Integer dataPanelHeight = 0;

    JPanel dataPanel;
    GroupLayout layout;
    JPanel dataGlobalPanel;
    String autoWindowSize;
    Container dataContentPane;
    JScrollPane scrollPaneData;
    JLabel message;
    String[] retCode = new String[2];
    JButton returnButton;
    JButton refreshOrigButton;
    JButton refreshEnteredButton;
    JButton enterButton;
    // Title of the window
    JPanel titlePanel;
    JPanel inputPanel;
    JPanel buttonPanel;

    // Work variables
    String scriptName;
    String scriptDescription;
    String scriptText;
    ArrayList<String[]> markerArrayList;
    static ArrayList<String[]> savedMarkerArrayList = new ArrayList<>();

    ArrayList<String[]> totalArrayList;
    ArrayList<String[]> headerArrayList;
    ArrayList<String[]> patternArrayList;
    ArrayList<String[]> printArrayList;
    ArrayList<String> titleArrayList;
    ArrayList<String[]> levelArrayList;
    ArrayList<String[]> summaryArrayList;
    ArrayList<String[]> summaryIndArrayList;
    ArrayList<String[]> omitArrayList;

    String decimalPattern;

    final int MARKER_POSITION_INDEX = 0;
    final int MARKER_TYPE_INDEX = 1;
    final int MARKER_TEXT_INDEX = 2;
    final int MARKER_VALUE_INDEX = 3;

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);

    /**
     * Prompts the user for entering parameter data and runs the query
     *
     * @param conn
     * @param scriptName
     * @param scriptDescription
     * @param scriptText
     * @param markerArrayList
     * @param headerArrayList
     * @param totalArrayList
     * @param patternArrayList
     * @param printArrayList
     * @param titleArrayList
     * @param levelArrayList
     * @param summaryArrayList
     * @param summaryIndArrayList
     * @param omitArrayList
     * @return
     */
    public String[] runPrompt(Connection conn, String scriptName, String scriptDescription,
            String scriptText, ArrayList<String[]> markerArrayList,
            ArrayList<String[]> headerArrayList, ArrayList<String[]> totalArrayList,
            ArrayList<String[]> patternArrayList, ArrayList<String[]> printArrayList,
            ArrayList<String> titleArrayList, ArrayList<String[]> levelArrayList,
            ArrayList<String[]> summaryArrayList, ArrayList<String[]> summaryIndArrayList,
            ArrayList<String[]> omitArrayList) {
        this.conn = conn;
        this.scriptName = scriptName;
        this.scriptDescription = scriptDescription;
        this.scriptText = scriptText;
        this.markerArrayList = markerArrayList;
        this.headerArrayList = headerArrayList;
        this.totalArrayList = totalArrayList;
        this.patternArrayList = patternArrayList;
        this.printArrayList = printArrayList;
        this.titleArrayList = titleArrayList;
        this.levelArrayList = levelArrayList;
        this.summaryArrayList = summaryArrayList;
        this.summaryIndArrayList = summaryIndArrayList;
        this.omitArrayList = omitArrayList;

        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        // Save marker array list parameters to a saved marker array list
        // in order to refresh original (default) parameter values.
        savedMarkerArrayList.clear();
        for (int idx = 0; idx < markerArrayList.size(); idx++) {
            savedMarkerArrayList.add(new String[4]);
            savedMarkerArrayList.get(idx)[MARKER_POSITION_INDEX] = markerArrayList.get(idx)[MARKER_POSITION_INDEX];
            savedMarkerArrayList.get(idx)[MARKER_TYPE_INDEX] = markerArrayList.get(idx)[MARKER_TYPE_INDEX];
            savedMarkerArrayList.get(idx)[MARKER_TEXT_INDEX] = markerArrayList.get(idx)[MARKER_TEXT_INDEX];
            savedMarkerArrayList.get(idx)[MARKER_VALUE_INDEX] = markerArrayList.get(idx)[MARKER_VALUE_INDEX];
        }

        // Get application properties
        prop = new Q_Properties();
        autoWindowSize = prop.getProperty("AUTO_WINDOW_SIZE");
        language = prop.getProperty("LANGUAGE");
        currentLocale = Locale.forLanguageTag(language);
        // Get resource bundle classes
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        // Localize messages
        noConn = locMessages.getString("NoConn");
        orderErr = locMessages.getString("OrderErr");
        correctRow = locMessages.getString("CorrectRow");
        orOrig = locMessages.getString("OrOrig");
        decNr = locMessages.getString("DecNr");
        intNr = locMessages.getString("IntNr");
        dateForm = locMessages.getString("DateForm");
        timeForm = locMessages.getString("TimeForm");
        timeStampForm = locMessages.getString("TimeStampForm");
        otherTypes = locMessages.getString("OtherTypes");
        stmtSuccess = locMessages.getString("StmtSuccess");
        // Localized button labels
        exit = buttons.getString("Exit");
        dsp_orig = buttons.getString("Dsp_orig");
        dsp_input = buttons.getString("Dsp_input");
        enter = buttons.getString("Enter");

        returnButton = new JButton(exit);
        returnButton.setMinimumSize(new Dimension(90, 35));
        returnButton.setMaximumSize(new Dimension(90, 35));
        returnButton.setPreferredSize(new Dimension(90, 35));

        refreshOrigButton = new JButton(dsp_orig);
        refreshOrigButton.setMinimumSize(new Dimension(150, 35));
        refreshOrigButton.setMaximumSize(new Dimension(150, 35));
        refreshOrigButton.setPreferredSize(new Dimension(150, 35));

        refreshEnteredButton = new JButton(dsp_input);
        refreshEnteredButton.setMinimumSize(new Dimension(140, 35));
        refreshEnteredButton.setMaximumSize(new Dimension(140, 35));
        refreshEnteredButton.setPreferredSize(new Dimension(140, 35));

        enterButton = new JButton(enter);
        enterButton.setMinimumSize(new Dimension(120, 35));
        enterButton.setMaximumSize(new Dimension(120, 35));
        enterButton.setPreferredSize(new Dimension(120, 35));
        enterButton.setForeground(DIM_BLUE); // Dim blue
        enterButton.setFont(new Font("Helvetica", Font.PLAIN, 14));
        enterButton.setFont(refreshEnteredButton.getFont().deriveFont(Font.PLAIN, 15));

        // Create window panel
        buildDataPanel();

        // Set "Return" button activity
        // ----------------------------
        returnButton.addActionListener(a -> {
            dispose();
        });

        // Set "Refresh Original" button activity
        // --------------------------------------
        refreshOrigButton.addActionListener(a -> {
            retCode[1] = "";
            dataContentPane.removeAll();
            // Restore original marker array list from the saved array list
            for (int idx = 0; idx < markerArrayList.size(); idx++) {
                markerArrayList.get(idx)[MARKER_POSITION_INDEX] = savedMarkerArrayList.get(idx)[MARKER_POSITION_INDEX];
                markerArrayList.get(idx)[MARKER_TYPE_INDEX] = savedMarkerArrayList.get(idx)[MARKER_TYPE_INDEX];
                markerArrayList.get(idx)[MARKER_TEXT_INDEX] = savedMarkerArrayList.get(idx)[MARKER_TEXT_INDEX];
                markerArrayList.get(idx)[MARKER_VALUE_INDEX] = savedMarkerArrayList.get(idx)[MARKER_VALUE_INDEX];
            }

            // Build data panel again
            buildDataPanel();
            // Y = pack the window to actual contents, N = set fixed size
            if (autoWindowSize.equals("Y")) {
                pack();
            } else {
                setSize(dataPanelGlobalWidth, dataPanelHeight);
            }
            // Make window visible
            setVisible(true);
        });

        // Set "Refresh Entered" button activity
        // -------------------------------------
        refreshEnteredButton.addActionListener(a -> {
            retCode[1] = "";
            // Y = pack the window to actual contents, N = set fixed size
            if (autoWindowSize.equals("Y")) {
                pack();
            } else {
                setSize(dataPanelGlobalWidth, dataPanelHeight);
            }
            // Make window visible
            setVisible(true);
        });

        // Set Enter button activity
        // --------------------------
        enterButton.addActionListener(a -> {
            boolean ok = enterData();
            // Data in text field will be colored black if OK
            if (ok) {
                txtFlds[errFieldIndex].setForeground(Color.BLACK);
                message.setText("");
                // Y = pack the window to actual contents, N = set fixed size
                if (autoWindowSize.equals("Y")) {
                    pack();
                } else {
                    setSize(dataPanelGlobalWidth, dataPanelHeight);
                }
                setVisible(true);
            } // Data in text field will be colored red if invalid
            else {
                message.setText(retCode[1]);
                // Y = pack the window to actual contents, N = set fixed size
                if (autoWindowSize.equals("Y")) {
                    pack();
                } else {
                    setSize(dataPanelGlobalWidth, dataPanelHeight);
                }
                setVisible(true);
            }
        });

        // Enable ENTER key to save and return action
        // ------------------------------------------
        dataGlobalPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ENTER"), "enter");
        dataGlobalPanel.getActionMap().put("enter", new EnterAction());

        // Display the window
        // ------------------
        setLocation(xLocation, yLocation);

        // Y = pack the window to actual contents, N = set fixed size
        if (autoWindowSize.equals("Y")) {
            pack();
        } else {
            setSize(dataPanelGlobalWidth, dataPanelHeight);
        }

        setVisible(true);

        // retCode[0] takes over that from runScript()
        // retCode[1] is an error message
        return retCode;
    }

    /**
     * Builds data panel for inserting or updating data
     */
    public void buildDataPanel() {

        titlePanel = new JPanel();
        buttonPanel = new JPanel();
        dataGlobalPanel = new JPanel();

        scriptDescription = scriptDescription.replaceFirst("-- ", "");

        // Localized titles and labels
        scrName = titles.getString("ScrName");
        defSelVals = titles.getString("DefSelVals");

        JTextArea title1 = new JTextArea(scriptDescription);
        title1.setFont(new Font("Helvetica", Font.PLAIN, 20));
        title1.setEditable(false);
        JTextArea title2 = new JTextArea(scrName + scriptName);
        title2.setFont(new Font("Helvetica", Font.PLAIN, 16));
        title2.setEditable(false);
        JTextArea title3 = new JTextArea(defSelVals);
        title3.setForeground(DIM_BLUE); // Dim blue
        title3.setEditable(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setAlignmentX(Box.LEFT_ALIGNMENT);
        titlePanel.add(title1);
        titlePanel.add(title2);
        titlePanel.add(title3);

        title1.setBackground(titlePanel.getBackground());
        title2.setBackground(titlePanel.getBackground());
        title3.setBackground(titlePanel.getBackground());

        // Build a panel with input fields with labels
        // -------------------------------------------
        buildInputPanel();

        // Build button row
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setAlignmentX(Box.LEFT_ALIGNMENT);
        buttonPanel.add(returnButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(refreshOrigButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(refreshEnteredButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(enterButton);

        // buttonPanel.setSize(100, 50);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 60)));

        // Set blank message initially
        message = new JLabel(" ");

        try {
            dataPanel = new JPanel();
            layout = new GroupLayout(dataPanel);
            dataPanel.setLayout(layout);

            // Arrange panels in group layout
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
                    layout.createParallelGroup(LEADING).addComponent(titlePanel)
                    .addComponent(inputPanel).addComponent(buttonPanel).addComponent(message)));
            layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
                    layout.createSequentialGroup().addComponent(titlePanel).addComponent(inputPanel)
                    .addComponent(buttonPanel).addComponent(message)));

            scrollPaneData = new JScrollPane(dataGlobalPanel);
            scrollPaneData.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            scrollPaneData.setBackground(dataPanel.getBackground());

            dataGlobalPanel.add(dataPanel);
            dataContentPane = getContentPane();
            dataContentPane.add(scrollPaneData);

            // Window height is a variable depending on number of input fields
            // i. e. the size of the marker array list or number of question marks
            // in the SQL statement.
            dataPanelHeight = markerArrayList.size() * (txtFldHeight + 20) + 260;

        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getLocalizedMessage());
        }
    }

    /**
     * Builds panel with a series of text fields and labels for SQL marker parameters
     */
    protected void buildInputPanel() {
        // Fill input fields with default values from marker parameters
        txtFlds = new JTextField[markerArrayList.size()];
        int idx = 0;
        try {
            // Create input fields - from marker parameters
            for (idx = 0; idx < markerArrayList.size(); idx++) {
                txtFlds[idx] = new JTextField(markerArrayList.get(idx)[MARKER_VALUE_INDEX]);
                txtFlds[idx].setFont(new Font("Monospaced", Font.PLAIN, 14));
            }
            // Create description labels for input fields - from marker parameters
            fldLbls = new JLabel[markerArrayList.size()];
            for (idx = 0; idx < markerArrayList.size(); idx++) {
                fldLbls[idx] = new JLabel(markerArrayList.get(idx)[MARKER_TEXT_INDEX]);
                fldLbls[idx].setFont(new Font("Monospaced", Font.PLAIN, 14));
            }

            inputPanel = new JPanel();
            gridBagLayout = new GridBagLayout();
            inputPanel.setLayout(gridBagLayout);

            gbc = new GridBagConstraints();
            // Place labels and input fields to columns in the input data panel
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = 0;
            gbc.gridx = 0;
            for (int i = 0; i < markerArrayList.size(); i++) {
                txtFlds[i].setMinimumSize(new Dimension(100, txtFldHeight));
                // txtFlds[i].setMaximumSize(new Dimension(100, txtFldHeigth));
                // txtFlds[i].setPreferredSize(new Dimension(100, txtFldHeigth));
                gbc.gridy++;
                gbc.gridx = 0;
                gbc.anchor = GridBagConstraints.WEST;
                inputPanel.add(fldLbls[i], gbc);
                gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                inputPanel.add(txtFlds[i], gbc);
                gbc.gridx = 2;
                gbc.anchor = GridBagConstraints.EAST;
                // JLabel emptyLabel = new JLabel(
                // "*                                                 *");
                // inputPanel.add(emptyLabel, gbc);
                // gbc.fill = GridBagConstraints.HORIZONTAL;
            }
            inputPanel.setAlignmentX(LEFT_ALIGNMENT);
        } catch (Exception e) {
            System.out.println(orderErr + idx + ", " + e.getClass() + " " + e.getLocalizedMessage());
        }
    }

    /**
     * Saves input parameters entered by the user in the marker array list and runs the query
     * performer
     *
     * @return boolean - data checked OK or not
     */
    int errFieldIndex;

    /**
     * Check input data. If invalid, return false, else call runScript() to perform the SQL
     * statement.
     *
     * @return boolean
     */
    @SuppressWarnings("ConvertToStringSwitch")
    protected boolean enterData() {
        retCode[1] = "";
        String paramType = "";
        // Copy input values from text fields back into the marker array list
        // to replace default values
        for (int i = 0; i < markerArrayList.size(); i++) {
            String markerType = markerArrayList.get(i)[MARKER_TYPE_INDEX];
            markerType = markerType.toUpperCase();
            try {
                // Check input values for correct data type
                // (parameter type in comment line versus SQL or Java type)
                if (markerType.equals("DEC") || markerType.equals("DECIMAL")
                        || markerType.equals("NUMERIC")) {
                    paramType = decNr;
                    // Java type BigDecimal
                    new BigDecimal(txtFlds[i].getText());
                    markerArrayList.get(i)[MARKER_VALUE_INDEX] = txtFlds[i].getText();
                } else if (markerType.equals("INT") || markerType.equals("INTEGER")
                        || markerType.equals("BIGINT")) {
                    paramType = intNr;
                    // Java Integer type
                    new Integer(txtFlds[i].getText());
                    markerArrayList.get(i)[MARKER_VALUE_INDEX] = txtFlds[i].getText();
                } else if (markerType.equals("DATE")) {
                    paramType = dateForm;
                    // Java LocalData type
                    LocalDate.parse(txtFlds[i].getText());
                    markerArrayList.get(i)[MARKER_VALUE_INDEX] = txtFlds[i].getText();
                } else if (markerType.equals("TIME")) {
                    paramType = timeForm;
                    // Java LocalTime type
                    LocalTime.parse(txtFlds[i].getText());
                    markerArrayList.get(i)[MARKER_VALUE_INDEX] = txtFlds[i].getText();
                } else if (markerType.equals("TIMESTAMP")) {
                    paramType = timeStampForm;
                    // SQL Java Toolbox type
                    Timestamp.valueOf(txtFlds[i].getText());
                    markerArrayList.get(i)[MARKER_VALUE_INDEX] = txtFlds[i].getText();
                } else {
                    // No check is performed for other parameter types
                    paramType = otherTypes;
                    markerArrayList.get(i)[MARKER_VALUE_INDEX] = txtFlds[i].getText();
                }
                // Erase error message and change red color of the input vallue to
                // black
                message.setText("");
                txtFlds[i].setForeground(Color.BLACK);

            } catch (Exception e) {
                // Correct error in input values
                int parameterPosition = i + 1;
                retCode[1] = correctRow + parameterPosition + " (" + paramType + ")" + orOrig;
                message.setText(retCode[1]);
                message.setForeground(DIM_RED); // Dim red
                // Data in text field in error will be colored red
                txtFlds[i].setForeground(DIM_RED); // Dim red
                return false;
            }
        }

        // Call script performer - runScript() method
        // ------------------------------------------
        Q_ScriptRun scrRun = new Q_ScriptRun();
        retCode = scrRun.runScript(conn, scriptName, scriptDescription, scriptText, markerArrayList,
                headerArrayList, totalArrayList, patternArrayList, printArrayList, titleArrayList,
                levelArrayList, summaryArrayList, summaryIndArrayList, omitArrayList);
        if (retCode[0].contains("UPDATE")) {
            retCode[1] = stmtSuccess;
            message.setText(retCode[1]);
            message.setForeground(DIM_BLUE); // Dim blue
            return false;
        } else {
            System.out.println(retCode[1]);
            message.setText(retCode[1]);
            message.setForeground(DIM_RED); // Dim red
            return false;
        }
    }

    /**
     * Inner class for ENTER key
     */
    class EnterAction extends AbstractAction {

        protected static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean ok = enterData();
            if (ok) {
                message.setText("");
                setVisible(true);
                // This would return to Q_ScriptRunCall list of scripts
                // dispose();
            } else {
                message.setText(retCode[1]);
                setVisible(true);
            }
        }
    }
}
