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
import java.sql.Connection;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Prompt for entering selection data and run query
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_PromptForScriptName extends JDialog {

    private static final long serialVersionUID = 1L;

    Q_Properties prop;

    Locale locale;
    String language;
    ResourceBundle titles;
    String impScript, defFileScr;
    ResourceBundle buttons;
    String exit, dsp_orig, dsp_input, run_imp;

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
    Container dataContentPane;
    JScrollPane scrollPaneData;
    JLabel message;
    String errMsg = " ";
    String[] retCode;

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

    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);

    /**
     * Prompts the user for the script name to be imported
     */
    public void runPrompt() {
        this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        // Get application properties
        prop = new Q_Properties();
        language = prop.getProperty("LANGUAGE");
        Locale currentLocale = Locale.forLanguageTag(language);
        // Get resource bundle classes
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);

        // Localized button labels
        exit = buttons.getString("Exit");
        dsp_orig = buttons.getString("Dsp_orig");
        dsp_input = buttons.getString("Dsp_input");
        run_imp = buttons.getString("Run_imp");

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

        enterButton = new JButton(run_imp);
        enterButton.setMinimumSize(new Dimension(140, 35));
        enterButton.setMaximumSize(new Dimension(140, 35));
        enterButton.setPreferredSize(new Dimension(140, 35));
        enterButton.setForeground(DIM_BLUE); // Dim blue
//      enterButton.setFont(new Font("Helvetica", Font.PLAIN, 14));
        enterButton.setFont(refreshEnteredButton.getFont().deriveFont(Font.PLAIN, 15));
        buildDataPanel();

        // Set "Return" button activity
        // ----------------------------
        returnButton.addActionListener(a -> {
            dispose();
        });

        // Set "Refresh Original" button activity
        // --------------------------------------
        refreshOrigButton.addActionListener(a -> {
            errMsg = "";
            dataContentPane.removeAll();
            // Build data panel again
            buildDataPanel();
            // setSize(dataPanelGlobalWidth, dataPanelHeight);
            // setLocation(xLocation, yLocation);
            // ???? pack();
            // Make window visible
            setVisible(true);
        });

        // Set "Refresh Entered" button activity
        // -------------------------------------
        refreshEnteredButton.addActionListener(a -> {
            errMsg = "";
            // setSize(dataPanelGlobalWidth, dataPanelHeight);
            // setLocation(xLocation, yLocation);
            // ???? pack();
            // Make window visible
            setVisible(true);
        });

        // Set "Enter" button activity
        // ---------------------------
        enterButton.addActionListener(a -> {
            runImport();
        });

        // Enable ENTER key to save and return action
        // ------------------------------------------
        dataGlobalPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ENTER"), "enter");
        dataGlobalPanel.getActionMap().put("enter", new EnterAction());

        // Display the window
        // ------------------
        setSize(dataPanelGlobalWidth, dataPanelHeight);
        setLocation(xLocation, yLocation);
        // ???? pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Saves input data entered by the user in the marker array list and runs the query performer
     *
     * @return
     */
    protected String[] runImport() {
        String scriptFileName = txtFlds[0].getText();
        // Call transfer of file 
        retCode = Q_ImportOneFromAS400.transferOneFromAS400(scriptFileName);
        errMsg = retCode[1];
        message.setText(errMsg);
        if (retCode[0].equals("ERROR")) {
            message.setForeground(DIM_RED); // Dim red
        } else {
            message.setForeground(DIM_BLUE); // Dim blue
        }
        setVisible(true);
        return retCode;
    }

    /**
     * Inner class for ENTER key
     */
    class EnterAction extends AbstractAction {

        protected static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            runImport();
        }
    }

    /**
     * Builds data panel for inserting data
     */
    public void buildDataPanel() {

        titlePanel = new JPanel();
        buttonPanel = new JPanel();
        dataGlobalPanel = new JPanel();
        dataPanel = new JPanel();
        layout = new GroupLayout(dataPanel);

        // Localized titles and labels
        impScript = titles.getString("ImpScript");
        defFileScr = titles.getString("DefFileScr");

        JTextArea title2 = new JTextArea(impScript);
        title2.setFont(new Font("Helvetica", Font.PLAIN, 20));
        title2.setEditable(false);

        JTextArea title3 = new JTextArea(defFileScr);
        title3.setForeground(DIM_BLUE); // Dim blue
        title3.setEditable(false);

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
        titlePanel.setAlignmentX(Box.LEFT_ALIGNMENT);
        titlePanel.add(title2);
        titlePanel.add(title3);

        title2.setBackground(titlePanel.getBackground());
        title3.setBackground(titlePanel.getBackground());

        // Build panel with input fields with labels here!
        // -----------------------------------------------
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

//      try {
        // Arrange panels in group layout
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
                layout.createParallelGroup(LEADING).addComponent(titlePanel)
                .addComponent(inputPanel).addComponent(buttonPanel).addComponent(message)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
                layout.createSequentialGroup().addComponent(titlePanel).addComponent(inputPanel)
                .addComponent(buttonPanel).addComponent(message)));
        dataPanel.setLayout(layout);

        dataGlobalPanel.add(dataPanel);

        scrollPaneData = new JScrollPane(dataGlobalPanel);
        scrollPaneData.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        scrollPaneData.setBackground(dataPanel.getBackground());

        dataGlobalPanel.add(dataPanel);
        dataContentPane = getContentPane();
        dataContentPane.add(scrollPaneData);

        // Window height is variable depending on number of input fields
        // (size of the marker array list)
        dataPanelHeight = txtFldHeight + 20 + 250;

        setSize(dataPanelGlobalWidth, dataPanelHeight);

//      } catch (IllegalArgumentException iae) {
//         System.out.println(iae.getLocalizedMessage());
//      }
    }

    /**
     *
     */
    protected void buildInputPanel() {
        // Fill input fields with default values from marker parameters
        txtFlds = new JTextField[1];
        int idx = 0;
        try {
            // Create input fields - from marker parameters
            for (idx = 0; idx < 1; idx++) {
                txtFlds[idx] = new JTextField(".sql");
                txtFlds[idx].setFont(new Font("Helvetica", Font.PLAIN, 18));
            }
            // Create description label for one input field (script name)
            /*
         fldLbls = new JLabel[1];
         for (int i = 0; i < 1; i++) {
            fldLbls[i] = new JLabel("Jméno souboru s koncovkou .sql");
         }
             */
            inputPanel = new JPanel();
            gridBagLayout = new GridBagLayout();
            inputPanel.setLayout(gridBagLayout);

            gbc = new GridBagConstraints();
            // Place labels and input fields to columns in the input data panel
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = 0;
            gbc.gridx = 0;
            for (int i = 0; i < 1; i++) {
                txtFlds[i].setMinimumSize(new Dimension(100, txtFldHeight));
                // txtFlds[i].setMaximumSize(new Dimension(100, txtFldHeigth));
                // txtFlds[i].setPreferredSize(new Dimension(100, txtFldHeigth));
                gbc.gridy++;
                gbc.gridx = 0;
                // gbc.anchor = GridBagConstraints.WEST;
                // inputPanel.add(fldLbls[i], gbc);
                // gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                inputPanel.add(txtFlds[i], gbc);
                // gbc.gridx = 2;
                gbc.anchor = GridBagConstraints.EAST;
                // JLabel emptyLabel = new JLabel(
                // "*                                                 *");
                // inputPanel.add(emptyLabel, gbc);
                // gbc.fill = GridBagConstraints.HORIZONTAL;
            }
            inputPanel.setAlignmentX(LEFT_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Chyba sestavení paramerů v okně: pořadí " + idx + ", " + e.getClass()
                    + " " + e.getLocalizedMessage());
        }
    }
}
