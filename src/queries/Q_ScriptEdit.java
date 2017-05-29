package queries;

import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;
import java.nio.charset.CharsetEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Enables editing of an SQL script using editor pane.
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ScriptEdit extends JDialog {

   protected static final long serialVersionUID = 1L;

   Connection conn;
   String[] retCode;

   Q_Properties prop;

   Locale currentLocale;
   String language;

   ResourceBundle titles;
   String wrtScrN, defNewScr, editScrN, editFontSizeLbl, nonAscii;

   ResourceBundle buttons;
   String undoTxt, redoTxt, cancel, save, runScript;

   ResourceBundle locMessages;
   String ioError;

   int windowWidth = 730;
   int windowHeight = 450;
   String autoWindowSize = "";

   String typeCode;
   Path scriptPath;
   String fileName;
   URL scriptURL;
   List<String> list;
   JTextArea editor;

   final int TAB_SIZE = 4;

   JLabel fontSizeLabel = new JLabel();
   JTextField fontSizeField = new JTextField();
   String editFontSize;
   int editFontSizeInt;

   JButton undoButton = new JButton();
   JButton redoButton = new JButton();

   JButton returnButton;
   JButton saveButton;
   JButton runButton;

   JPanel buttonPanel = new JPanel();
   JPanel messagePanel = new JPanel();
   JLabel invalidScriptName;
   JLabel nonAsciiName;

   JTextField textField;

   GroupLayout layout = new GroupLayout(getContentPane());

   final Color DIM_BLUE = new Color(50, 60, 160);
   final Color DIM_RED = new Color(190, 60, 50);

   /**
    * Listener for the edits on the current document.
    */
   protected UndoableEditListener undoHandler = new UndoHandler();
   /** UndoManager that we add edits to. */
   protected UndoManager undo = new UndoManager();

   // --- action implementations -----------------------------------
   private UndoAction undoAction = new UndoAction();
   private RedoAction redoAction = new RedoAction();

   /**
    * Editing text file - query script
    *
    * @param fileName
    */
   public void scriptEdit(String fileName) {
      this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

      // File name may be null in case of creating a new script file
      this.fileName = fileName;

      // Get application properties
      prop = new Q_Properties();
      editFontSize = prop.getProperty("EDIT_FONT_SIZE");
      try {
         editFontSizeInt = new Integer(editFontSize);
      } catch (Exception exc) {
         editFontSizeInt = 14;
      }
      autoWindowSize = prop.getProperty("AUTO_WINDOW_SIZE");
      language = prop.getProperty("LANGUAGE");
      currentLocale = Locale.forLanguageTag(language);
      // Get resource bundle classes
      titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle", currentLocale);
      buttons = ResourceBundle.getBundle("locales.L_ButtonBundle", currentLocale);
      locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

      // Localized button labels
      undoTxt = buttons.getString("Undo");
      redoTxt = buttons.getString("Redo");
      save = buttons.getString("Save");
      cancel = buttons.getString("Cancel");
      runScript = buttons.getString("RunScript");

      undoButton = new JButton(undoTxt);
      redoButton = new JButton(redoTxt);
      returnButton = new JButton(cancel);
      runButton = new JButton(runScript);
      saveButton = new JButton(save);

      returnButton.setMinimumSize(new Dimension(80, 35));
      returnButton.setMaximumSize(new Dimension(80, 35));
      returnButton.setPreferredSize(new Dimension(80, 35));

      saveButton.setMinimumSize(new Dimension(120, 35));
      saveButton.setMaximumSize(new Dimension(120, 35));
      saveButton.setPreferredSize(new Dimension(120, 35));

      runButton.setMinimumSize(new Dimension(120, 35));
      runButton.setMaximumSize(new Dimension(120, 35));
      runButton.setPreferredSize(new Dimension(120, 35));

      buttonPanel.add(returnButton);
      buttonPanel.add(saveButton);
      buttonPanel.add(runButton);

      textField = new JTextField("");
      textField.setFont(new Font("Monospaced", Font.PLAIN, 14));
      textField.setPreferredSize(new Dimension(300, 20));
      textField.setMaximumSize(new Dimension(300, 20));
      textField.setMinimumSize(new Dimension(300, 20));

      // Create an editor pane for a file name. The file name may be null
      // in case of creating a new script file.
      editor = createEditor(fileName);

      // Replace TAB characters with TAB_SIZE spaces in the text area
      String text = editor.getText();
      text = text.replace("\t", fixedLengthSpaces(TAB_SIZE));
      editor.setText(text);

      // Set scroll bar to top
      editor.setCaretPosition(0);
      // Listener for undoable edits
      editor.getDocument().addUndoableEditListener(undoHandler);
      // Undo button listener
      undoButton.addActionListener(new UndoAction());
      // Redo button listener
      redoButton.addActionListener(new RedoAction());

      editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
      // Create a scrollPane to contain the editorPane
      JScrollPane editorScrollPane = new JScrollPane(editor);
      editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      editorScrollPane.setMinimumSize(new Dimension(windowWidth, windowHeight));
      editorScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      // Localized titles and labels
      wrtScrN = titles.getString("WrtScrN");
      defNewScr = titles.getString("DefNewScr");
      editScrN = titles.getString("EditScrN");
      editFontSizeLbl = titles.getString("EditFontSize");
      // Localized messages
      ioError = locMessages.getString("IOError");
      nonAscii = locMessages.getString("NonAscii");

      // Prompt to write script name with .sql ending
      invalidScriptName = new JLabel(wrtScrN);
      nonAsciiName = new JLabel(nonAscii);

      fontSizeLabel.setText(editFontSizeLbl);
      fontSizeField.setText(editFontSize);
      fontSizeField.setPreferredSize(new Dimension(30, 20));
      fontSizeField.setMaximumSize(new Dimension(30, 20));

      // If file name does not exist - the user enters a new script name
      JLabel prompt = new JLabel();
      if (fileName == null) {
         editor.setText("");
         textField.setText(".sql");
         prompt.setText(defNewScr);
      } // File name exists - the user edits existing script text
      else {
         textField.setText(fileName);
         prompt.setText(editScrN);
      }
      prompt.setForeground(DIM_BLUE); // Dim blue

      // Message panel
      messagePanel.setPreferredSize(new Dimension(0, 50));
      messagePanel.removeAll();

      // Return button - return from editor and wipe the window
      returnButton.addActionListener(sb -> {
         dispose();
      });

      // Run button - run the script
      runButton.addActionListener(sb -> {
         String scriptName = textField.getText();
         String scriptLine;
         String scriptDescription = "";
         BufferedReader infileScript;
         // First save the script to the file
         saveScript();
         // Get script description
         try {
            // Open the script file
            infileScript = Files.newBufferedReader(scriptPath, Charset.forName("UTF-8"));
            // If the first line is a simple comment beginning with "--"
            // in the first position, and it is not "--;",
            // the description is the text following the -- characters.
            // Otherwise the description is empty.
            scriptLine = infileScript.readLine();
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

            // Close the script file
            infileScript.close();
         } catch (Exception ioe) {
            ioe.printStackTrace();
         }

         // Perform the script
         Q_ScriptRunCall src = new Q_ScriptRunCall();
         retCode = src.performScript(scriptName, scriptDescription);
         // Handle messages
         messagePanel.removeAll();
         JLabel msg;
         if (!retCode[1].isEmpty()) {
            msg = new JLabel(retCode[1]);
            messagePanel.add(msg);
            msg.setForeground(DIM_BLUE); // blue
            if (retCode[0].contains("ERROR")) {
               msg.setForeground(DIM_RED); // red
            }
         }
         repaint();
         setVisible(true);
      });

      // Save button - save contents of the editor pane to a text file
      saveButton.addActionListener(sb -> {
         saveScript();
         // Clear messages
         JLabel msg = new JLabel("");
         messagePanel.add(msg);
         repaint();
         setVisible(true);
      });

      layout.setAutoCreateGaps(true);
      layout.setAutoCreateContainerGaps(true);

      SequentialGroup sg1 = layout.createSequentialGroup()
            .addComponent(textField)
            .addComponent(fontSizeLabel)
            .addComponent(fontSizeField)
            .addComponent(undoButton)
            .addComponent(redoButton);
      ParallelGroup pg1 = layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(textField)
            .addComponent(fontSizeLabel)
            .addComponent(fontSizeField)
            .addComponent(undoButton)
            .addComponent(redoButton);

      layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(LEADING)
            .addComponent(prompt)
            .addGroup(sg1)
            .addComponent(editorScrollPane)
            .addComponent(buttonPanel)
            .addComponent(messagePanel)));
      layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createSequentialGroup()
            .addComponent(prompt)
            .addGroup(pg1)
            .addComponent(editorScrollPane)
            .addComponent(buttonPanel)
            .addComponent(messagePanel)));

      Container cont = this.getContentPane();
      cont.setLayout(layout);

      // Y = pack the window to actual contents, N = set fixed size
      if (autoWindowSize.equals("Y")) {
         pack();
      } else {
         setSize(windowWidth + 80, windowHeight + 200);
      }

      setLocation(600, 40);
      setVisible(true);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }

   /**
    * Creates the editor pane
    *
    * @param fileName
    * @return editorPane JEditorPane object
    */
   protected JTextArea createEditor(String fileName) {
      editor = new JTextArea();
      editor.setEditable(true);
      // Editor pane will contain contents of the script file
      try {
         // Script file name does not exist - get a file name from input field
         // and create Path for the NEW FILE
         if (fileName == null) {
            scriptPath = Paths.get(System.getProperty("user.dir"), "scriptfiles", textField.getText());
         } // Script file name exists - create Path for EXISTING FILE NAME
         else {
            scriptPath = Paths.get(System.getProperty("user.dir"), "scriptfiles", fileName);
         }
         if (Files.exists(scriptPath)) {
            list = Files.readAllLines(scriptPath, Charset.forName("UTF-8"));
            if (list != null) {
               // Concatenate all text lines from the list obtained from the print file
               String text = list.stream().reduce("", (a, b) -> a + b + "\n");
               editor.setText(text);
            }
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
         System.out.println(ioError + ioe.getLocalizedMessage());
      }
      return editor;
   }

   protected void saveScript() {
      messagePanel.removeAll();
      // File name contains non-ASCII characters
      if (!isPureAscii(textField.getText())) {
         textField.setForeground(DIM_RED);
         nonAsciiName.setForeground(DIM_RED); // red
         messagePanel.add(nonAsciiName);
         setVisible(true);
      } // File name does not end with .sql - Send an error message
      else if (!textField.getText().toUpperCase().endsWith(".SQL")) {
         textField.setForeground(DIM_RED);
         invalidScriptName.setForeground(DIM_RED); // red
         messagePanel.add(invalidScriptName);
         setVisible(true);
      } // File name OK - Rewrite the existing file or write a new file.
      else {
         messagePanel.removeAll();
         repaint();
         textField.setForeground(Color.BLACK);
         try {
            // Create path to the existing or a new file
            scriptPath = Paths.get(System.getProperty("user.dir"), "scriptfiles", textField.getText());

            String[] lines = editor.getText().split("\n");
            ArrayList<String> arrlines = new ArrayList<>();
            for (int idx = 0; idx < lines.length; idx++) {
               arrlines.add(idx, lines[idx]);
            }
            // Rewrite the existing file or create and write a new file with the
            // contents of the editor pane.
            Files.write(scriptPath, arrlines, StandardCharsets.UTF_8);
            // Set the new contents of the file back to the editor pane and
            // display it.
            if (Files.exists(scriptPath)) {
               list = Files.readAllLines(scriptPath, Charset.forName("UTF-8"));
               if (list != null) {
                  // Concatenate all text lines from the list obtained from the
                  // script file
                  String text = list.stream().reduce("", (a, b) -> a + b + "\n");
                  // Set the text lines to the editor pane
                  editor.setText(text);
               }
            }
         } catch (Exception exc) {
            JLabel msg = new JLabel(ioError + exc.getLocalizedMessage());
            msg.setForeground(DIM_RED); // red
            System.out.println(msg);
            messagePanel.add(msg);
            setVisible(true);
         }
      }
   }

   static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1

   public static boolean isPureAscii(String str) {
      return asciiEncoder.canEncode(str);
   }

   /**
    * Create String of spaces with a given length
    * 
    * @param length
    * @return
    */
   private String fixedLengthSpaces(int length) {
      char[] spaces = new char[length];
      for (int idx = 0; idx < length; idx++) {
         spaces[idx] = ' ';
      }
      return String.copyValueOf(spaces);
   }

   class UndoHandler implements UndoableEditListener {

      /**
       * Messaged when the Document has created an edit, the edit is added to
       * "undo", an instance of UndoManager.
       */
      public void undoableEditHappened(UndoableEditEvent uee) {
         undo.addEdit(uee.getEdit());
         undoAction.update();
         redoAction.update();
      }
   }

   class UndoAction extends AbstractAction {

      public UndoAction() {
         super("Undo");
         setEnabled(false);
      }

      public void actionPerformed(ActionEvent e) {
         try {
            undo.undo();
         } catch (CannotUndoException ex) {
            // Logger.getLogger(UndoAction.class.getName()).log(Level.SEVERE, "Unable to undo", ex);
         }
         update();
         redoAction.update();
      }

      protected void update() {
         if (undo.canUndo()) {
            setEnabled(true);
            putValue(Action.NAME, undo.getUndoPresentationName());
         } else {
            setEnabled(false);
            putValue(Action.NAME, "Undo");
         }
      }
   }

   class RedoAction extends AbstractAction {

      public RedoAction() {
         super("Redo");
         setEnabled(false);
      }

      public void actionPerformed(ActionEvent ae) {
         try {
            undo.redo();
         } catch (CannotRedoException cre) {
            // Logger.getLogger(RedoAction.class.getName()).log(Level.SEVERE, "Unable to redo", cre);
         }
         update();
         undoAction.update();
      }

      protected void update() {
         if (undo.canRedo()) {
            setEnabled(true);
            putValue(Action.NAME, undo.getRedoPresentationName());
         } else {
            setEnabled(false);
            putValue(Action.NAME, "Redo");
         }
      }
   }

   /**
    * Main method for testing
    *
    * @param args
    *           not used
    */
   public static void main(String[] args) {
      Q_ScriptEdit se = new Q_ScriptEdit();
      se.scriptEdit("    prázdný.sql");
   }
}
