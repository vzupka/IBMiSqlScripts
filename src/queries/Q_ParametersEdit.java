package queries;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Enables editing of application parameters.
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ParametersEdit extends JDialog {

    static final long serialVersionUID = 1L;

    ResourceBundle buttons;
    ResourceBundle titles;
    ResourceBundle locMessages;

    // Name of the localized button
    String sav;
    // Names of localized labels
    String defParApp, adrSvr, usrName, libList, ifsDir, autWin, winWidth, winHeight;
    String nullMark, colSpaces, fontSize, decPattern, orEnter;
    // Localized messages
    String curDir, parSaved;

    // Constants for properties
    final String LANGUAGE = "LANGUAGE";
    final String HOST = "HOST";
    final String USER_NAME = "USER_NAME";
    final String LIBRARY = "LIBRARY";
    final String IFS_DIRECTORY = "IFS_DIRECTORY";
    final String AUTO_WINDOW_SIZE = "AUTO_WINDOW_SIZE";
    final String RESULT_WINDOW_WIDTH = "RESULT_WINDOW_WIDTH";
    final String RESULT_WINDOW_HEIGHT = "RESULT_WINDOW_HEIGHT";
    final String NULL_PRINT_MARK = "NULL_PRINT_MARK";
    final String COLUMN_SEPARATING_SPACES = "COLUMN_SEPARATING_SPACES";
    final String FONT_SIZE = "FONT_SIZE";
    final String EDIT_FONT_SIZE = "EDIT_FONT_SIZE";
    final String DECIMAL_PATTERN = "DECIMAL_PATTERN";

    Path parPath = Paths.get(System.getProperty("user.dir"), "paramfiles", "Q_Parameters.txt");
    Q_Properties prop;

    Container cont = getContentPane();
    GridBagLayout gridBagLayout = new GridBagLayout();

    GridBagConstraints gbc = new GridBagConstraints();

    JPanel titlePanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel dataPanel = new JPanel();
    JPanel messagePanel = new JPanel();
    JPanel globalPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(globalPanel, BoxLayout.Y_AXIS);

    JTextArea title;

    JRadioButton englishButton = new JRadioButton("English");
    JRadioButton czechButton = new JRadioButton("Česky");
    JCheckBox autoSizeButton = new JCheckBox("");

    // Initial parameter values - not to be empty when the application is
    // installed
    String language;
    JTextField hostTf = new JTextField();
    JTextField userNameTf = new JTextField();
    JTextField librariesTf = new JTextField();
    JTextField ifsDirectoryTf = new JTextField();
    String autoWindowSize = new String();
    JTextField windowWidthTf = new JTextField();
    JTextField windowHeightTf = new JTextField();
    JTextField nullPrintMarkTf = new JTextField();
    JTextField colSpacesTf = new JTextField();
    JTextField fontSizeTf = new JTextField();
    JTextField editFontSizeTf = new JTextField();
    JTextField decPatternTf = new JTextField();

    // These labels are NOT localized
    JTextArea englishLbl = new JTextArea(
            "Application language. Restart the application after change.");
    JTextArea czechLbl = new JTextArea("Jazyk aplikace. Po změně spusťte aplikaci znovu.");

    // Labels for text fields to localize
    JTextArea hostLbl;
    JTextArea userNameLbl;
    JTextArea librariesLbl;
    JTextArea ifsDirectoryLbl;
    JTextArea autoSizeLbl;
    JTextArea windowWidthLbl;
    JTextArea windowHeightLbl;
    JTextArea nullPrintMarkLbl;
    JTextArea colSpacesLbl;
    JTextArea fontSizeLbl;
    JTextArea decPatternLbl;

    // Button for saving data to parameter properties
    JButton saveButton;
    // Label at saveButton
    JTextArea orPressEnterLbl;

    // Messages are in text area
    JTextArea message = new JTextArea();
    // The message text area is in scroll pane
    JScrollPane scrollPane = new JScrollPane(message);

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);

    boolean fullMenu;

    /**
     * Constructor creates the window with application parameters
     */
    Q_ParametersEdit(boolean fullMenu) {
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        this.fullMenu = fullMenu;

        if (!Files.exists(parPath)) {
            Q_ParametersCreate.main();
        }
        // Get necessary properties
        prop = new Q_Properties();

        language = prop.getProperty("LANGUAGE");
        Locale currentLocale = Locale.forLanguageTag(language);

        // Get resource bundle classes
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        defParApp = titles.getString("DefParApp");
        adrSvr = titles.getString("AdrSvr");
        usrName = titles.getString("UsrName");
        libList = titles.getString("LibList");
        ifsDir = titles.getString("IfsDir");
        autWin = titles.getString("AutWin");
        winWidth = titles.getString("WinWidth");
        winHeight = titles.getString("WinHeight");
        nullMark = titles.getString("NullMark");
        colSpaces = titles.getString("ColSpaces");
        fontSize = titles.getString("FontSize");
        decPattern = titles.getString("DecPattern");
        orEnter = titles.getString("OrEnter");

        // Title of the window
        title = new JTextArea(defParApp);

        // Labels for text fields
        hostLbl = new JTextArea(adrSvr);
        userNameLbl = new JTextArea(usrName);
        librariesLbl = new JTextArea(libList);
        ifsDirectoryLbl = new JTextArea(ifsDir);
        autoSizeLbl = new JTextArea(autWin);
        windowWidthLbl = new JTextArea(winWidth);
        windowHeightLbl = new JTextArea(winHeight);
        colSpacesLbl = new JTextArea(colSpaces);
        nullPrintMarkLbl = new JTextArea(nullMark);
        fontSizeLbl = new JTextArea(fontSize);
        decPatternLbl = new JTextArea(decPattern);
        // Label near saveButton
        orPressEnterLbl = new JTextArea(orEnter);

        // Labels will have the same colors as background
        englishLbl.setBackground(titlePanel.getBackground());
        czechLbl.setBackground(titlePanel.getBackground());
        hostLbl.setBackground(titlePanel.getBackground());
        userNameLbl.setBackground(titlePanel.getBackground());
        librariesLbl.setBackground(titlePanel.getBackground());
        ifsDirectoryLbl.setBackground(titlePanel.getBackground());
        autoSizeLbl.setBackground(titlePanel.getBackground());
        windowWidthLbl.setBackground(titlePanel.getBackground());
        windowHeightLbl.setBackground(titlePanel.getBackground());
        nullPrintMarkLbl.setBackground(titlePanel.getBackground());
        colSpacesLbl.setBackground(titlePanel.getBackground());
        fontSizeLbl.setBackground(titlePanel.getBackground());
        decPatternLbl.setBackground(titlePanel.getBackground());

        orPressEnterLbl.setBackground(titlePanel.getBackground());

        // Labels are not editable
        englishLbl.setEditable(false);
        czechLbl.setEditable(false);
        hostLbl.setEditable(false);
        userNameLbl.setEditable(false);
        librariesLbl.setEditable(false);
        ifsDirectoryLbl.setEditable(false);
        autoSizeLbl.setEditable(false);
        windowWidthLbl.setEditable(false);
        windowHeightLbl.setEditable(false);
        nullPrintMarkLbl.setEditable(false);
        colSpacesLbl.setEditable(false);
        fontSizeLbl.setEditable(false);
        decPatternLbl.setEditable(false);

        orPressEnterLbl.setEditable(false);

        // Localized button label
        sav = buttons.getString("Sav");

        // Button for saving data to parameter properties
        saveButton = new JButton(sav);

        // Labels will have the same font as the first button
        englishLbl.setFont(saveButton.getFont());
        czechLbl.setFont(saveButton.getFont());
        hostLbl.setFont(saveButton.getFont());
        userNameLbl.setFont(saveButton.getFont());
        librariesLbl.setFont(saveButton.getFont());
        ifsDirectoryLbl.setFont(saveButton.getFont());
        autoSizeLbl.setFont(saveButton.getFont());
        windowWidthLbl.setFont(saveButton.getFont());
        windowHeightLbl.setFont(saveButton.getFont());
        nullPrintMarkLbl.setFont(saveButton.getFont());
        colSpacesLbl.setFont(saveButton.getFont());
        fontSizeLbl.setFont(saveButton.getFont());
        decPatternLbl.setFont(saveButton.getFont());

        orPressEnterLbl.setFont(saveButton.getFont());

        // Label at the press button will be blue
        orPressEnterLbl.setForeground(DIM_BLUE); // Dim blue
        message.setBackground(titlePanel.getBackground());
        message.setFont(saveButton.getFont()); // Font from the Save button

        scrollPane.setBorder(null);

        title.setBackground(titlePanel.getBackground());
        title.setEditable(false);

        // Localized messages
        curDir = locMessages.getString("CurDir");
        parSaved = locMessages.getString("ParSaved");

        // Language radio buttons
        englishButton.setMnemonic(KeyEvent.VK_E);
        englishButton.setActionCommand("English");
        englishButton.setSelected(true);
        englishButton.setHorizontalTextPosition(SwingConstants.LEFT);

        czechButton.setMnemonic(KeyEvent.VK_C);
        czechButton.setActionCommand("Česky");
        czechButton.setSelected(false);
        czechButton.setHorizontalTextPosition(SwingConstants.LEFT);

        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setMaximumSize(new Dimension(100, 40));
        saveButton.setMinimumSize(new Dimension(100, 40));

        // Radio and check buttons listeners
        // ---------------------------------
        // Set on English, set off Czech
        englishButton.addActionListener(ae -> {
            englishButton.setSelected(true);
            czechButton.setSelected(false);
            language = "en-US";
            System.out.println(ae.getActionCommand());
            //System.out.println(language);
        });

        // Set on Czech, set off English
        czechButton.addActionListener(ae -> {
            czechButton.setSelected(true);
            englishButton.setSelected(false);
            language = "cs-CZ";
            System.out.println(ae.getActionCommand());
            //System.out.println(language);
        });

        // Select or deselect automatic window size
        autoSizeButton.addItemListener(il -> {
            Object source = il.getSource();
            if (source == autoSizeButton) {
                if (autoSizeButton.isSelected()) {
                    autoWindowSize = "Y";
                } else {
                    autoWindowSize = "N";
                }
            }
        });

        // Get parameter properties
        // ------------------------
        // This parameter comes from radio buttons
        language = prop.getProperty(LANGUAGE);
        if (language.equals("en-US")) {
            englishButton.setSelected(true);
            czechButton.setSelected(false);
        } else if (language.equals("cs-CZ")) {
            czechButton.setSelected(true);
            englishButton.setSelected(false);
        }

        // The following parameters are editable
        hostTf.setText(prop.getProperty(HOST));
        userNameTf.setText(prop.getProperty(USER_NAME));
        librariesTf.setText(prop.getProperty(LIBRARY));
        ifsDirectoryTf.setText(prop.getProperty(IFS_DIRECTORY));
        // String "Y" or "N"
        autoWindowSize = prop.getProperty(AUTO_WINDOW_SIZE);
        windowWidthTf.setText(prop.getProperty(RESULT_WINDOW_WIDTH));
        windowHeightTf.setText(prop.getProperty(RESULT_WINDOW_HEIGHT));
        nullPrintMarkTf.setText(prop.getProperty(NULL_PRINT_MARK));
        colSpacesTf.setText(prop.getProperty(COLUMN_SEPARATING_SPACES));
        fontSizeTf.setText(prop.getProperty(FONT_SIZE));
        editFontSizeTf.setText(prop.getProperty(EDIT_FONT_SIZE));
        decPatternTf.setText(prop.getProperty(DECIMAL_PATTERN));

        // Automatic size of the window with script results
        if (autoWindowSize.equals("Y")) {
            autoSizeButton.setSelected(true);
        } else {
            autoSizeButton.setSelected(false);
        }
        autoSizeButton.setHorizontalTextPosition(SwingConstants.LEFT);

        // Build the window
        // ----------------
        // Place title in label panel
        titlePanel.add(title);
        title.setFont(new Font("Helvetica", Font.PLAIN, 20));
        titlePanel.setBorder(BorderFactory.createLineBorder(DIM_BLUE));

        buttonPanel.add(saveButton);
        buttonPanel.add(orPressEnterLbl);

        dataPanel.setLayout(gridBagLayout);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // internal padding of components
        gbc.ipadx = 0; // vodorovně
        gbc.ipady = 0; // svisle

        // Place text fields in column 0
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0;
        gbc.gridy = 0;

        dataPanel.add(englishButton, gbc);
        gbc.gridy++;
        dataPanel.add(czechButton, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(hostTf, gbc);
            gbc.gridy++;
        }
        dataPanel.add(userNameTf, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(librariesTf, gbc);
            gbc.gridy++;
            dataPanel.add(ifsDirectoryTf, gbc);
            gbc.gridy++;
        }
        dataPanel.add(autoSizeButton, gbc);
        gbc.gridy++;
        dataPanel.add(windowWidthTf, gbc);
        gbc.gridy++;
        dataPanel.add(windowHeightTf, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(nullPrintMarkTf, gbc);
            gbc.gridy++;
            dataPanel.add(colSpacesTf, gbc);
            gbc.gridy++;
            dataPanel.add(fontSizeTf, gbc);
            gbc.gridy++;
            dataPanel.add(decPatternTf, gbc);
            gbc.gridy++;
        }

        // Place labels in column 1
        gbc.anchor = GridBagConstraints.WEST;
        // gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 1;
        gbc.gridy = 0;

        dataPanel.add(englishLbl, gbc);
        gbc.gridy++;
        dataPanel.add(czechLbl, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(hostLbl, gbc);
            gbc.gridy++;
        }
        dataPanel.add(userNameLbl, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(librariesLbl, gbc);
            gbc.gridy++;
            dataPanel.add(ifsDirectoryLbl, gbc);
            gbc.gridy++;
        }
        dataPanel.add(autoSizeLbl, gbc);
        gbc.gridy++;
        dataPanel.add(windowWidthLbl, gbc);
        gbc.gridy++;
        dataPanel.add(windowHeightLbl, gbc);
        gbc.gridy++;
        if (fullMenu) {
            dataPanel.add(nullPrintMarkLbl, gbc);
            gbc.gridy++;
            dataPanel.add(colSpacesLbl, gbc);
            gbc.gridy++;
            dataPanel.add(fontSizeLbl, gbc);
            gbc.gridy++;
            dataPanel.add(decPatternLbl, gbc);
            gbc.gridy++;
        }

        // On click the button Save data
        saveButton.addActionListener(al -> {
            saveData();
            setVisible(true);
        });

        // Enable ENTER key to save action
        globalPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ENTER"), "save");
        globalPanel.getActionMap().put("save", new SaveAction());

        // Place message area in message panel
        message.setEditable(false);
        message.setText(message.getText() + curDir + System.getProperty("user.dir")
                + "               \n");
        messagePanel.setBorder(BorderFactory.createLineBorder(DIM_BLUE)); // blue
        messagePanel.add(scrollPane);
        messagePanel.setPreferredSize(new Dimension(640, 100));
        scrollPane.setPreferredSize(new Dimension(620, 80));

        // Build content pane
        globalPanel.setLayout(boxLayout);
        globalPanel.add(titlePanel);
        globalPanel.add(dataPanel);
        globalPanel.add(buttonPanel);
        globalPanel.add(messagePanel);
        globalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cont.add(globalPanel);

        setSize(760, 550);
        setLocation(200, 50);
        pack();
        setVisible(true);
    }

    /**
     * Class for saving data
     */
    class SaveAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            saveData();
            setVisible(true);
        }
    }

    /**
     * Saves input data to the parameters file
     */
    private void saveData() {
        // Check numeric values of some parameters
        String windowWidth = checkNumber(windowWidthTf.getText());
        String windowHeight = checkNumber(windowHeightTf.getText());
        String colSpace = checkNumber(colSpacesTf.getText());
        String fontSiz = checkNumber(fontSizeTf.getText());

        // Set properties with input values
        prop.setProperty(LANGUAGE, language);
        prop.setProperty(HOST, hostTf.getText());
        prop.setProperty(USER_NAME, userNameTf.getText());
        prop.setProperty(LIBRARY, librariesTf.getText());
        prop.setProperty(IFS_DIRECTORY, ifsDirectoryTf.getText());
        prop.setProperty(AUTO_WINDOW_SIZE, autoWindowSize);
        prop.setProperty(RESULT_WINDOW_WIDTH, windowWidth);
        prop.setProperty(RESULT_WINDOW_HEIGHT, windowHeight);
        prop.setProperty(NULL_PRINT_MARK, nullPrintMarkTf.getText());
        prop.setProperty(COLUMN_SEPARATING_SPACES, colSpace);
        prop.setProperty(FONT_SIZE, fontSiz);
        prop.setProperty(DECIMAL_PATTERN, decPatternTf.getText());

        // Put corrected parameters back to input fields for display
        windowWidthTf.setText(windowWidth);
        windowHeightTf.setText(windowHeight);
        colSpacesTf.setText(colSpace);
        fontSizeTf.setText(fontSiz);
    }

    /**
     * Check input of a text field if it is numeric
     *
     * @param charNumber
     * @return String - echo if correct, "0" if not integer
     */
    protected String checkNumber(String charNumber) {
        try {
            new Integer(charNumber);
        } catch (NumberFormatException nfe) {
            charNumber = "0";
        }
        return charNumber;
    }
}
