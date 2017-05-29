package queries;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
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
import javax.swing.KeyStroke;
import javax.swing.GroupLayout.Alignment;

/**
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_PrintOneFile extends JDialog {

    static final long serialVersionUID = 1L;
    Locale locale;
    ResourceBundle titles;
    String titView, titPage;
    ResourceBundle buttons;
    String exit, print;

    Q_Properties prop;
    String language;
    int windowHeight;
    int windowWidth;
    int screenWidth;
    int screenHeight;
    String autoWindowSize;
    Font font; // Monospaced font as a default

    // Resulting font size can be from 6 points up
    int fontSize;

    // Font size in number of print points 
    // 1 point is 1/72 of an inch
    String fontSizeScript; // Font size as String from script

    // Media size - A4 (default) or A3
    MediaSize mediaSize;
    String mediaSizeScript;

    // Page orientation 
    // PORTRAIT (default) or LANDSCAPE
    String orientation;

    // Page margins
    int leftMargin; // LMn
    int rightMargin; // RMn
    int topMargin; // TMn
    int bottomMargin; // BMn

    // Difference between heights of A4 and LETTER
    float LETTER_LENGTH_CORRECTION;
    float LETTER_WIDTH_CORRECTION;

    // Number of print points in 1 millimeter
    float pointsInMM = (float) (72 / 25.4);
    float mmInPoint = (float) (25.4 / 72);
    float mmWidth;
    float mmHeight;
    float imWidth;
    float imHeight;
    float pagePrintableWidth;
    float pagePrintableHeight;
    float pageLength; // in number of lines
    float pageWidth; // in number of characters
    float pageLengthInPoints; // in number of print points
    float pageWidthInPoints; // in number of print points
    float nbrFooterLines;
    float os_correction;

    PageFormat pageFormat;

    FilePageRenderer pageRenderer;

    String scriptName;
    JTextArea resultTextArea;
    int nbrHdrLines;
    ArrayList<String[]> headerArrayList;
    ArrayList<String[]> printArrayList;
    String[] printValues;
    ArrayList<String> columnHeaders;
    
    JButton returnButton;
    JButton printButton;
    JPanel buttonPanel;

    // Text in resultTextArea will contain the file contents as a string
    JScrollPane scrollPane = new JScrollPane();
    JPanel globalPanel = new JPanel();
    Container container;
    GroupLayout layout = new GroupLayout(globalPanel);

    JLabel title;

    // Lines from the print file "Print.txt"
    ArrayList<String> lineList = new ArrayList<>();

    // Printer job
    PrinterJob printerJob;
    PrintRequestAttributeSet attr_set;

    ArrayList<String> lineVector;
    ArrayList<ArrayList<String>> pageVector;
    int pageNumber;
    
    final Color DIM_BLUE = new Color(50, 60, 160);
    final Color DIM_RED = new Color(190, 60, 50);
    Color VERY_LIGHT_BLUE = Color.getHSBColor(0.60f, 0.05f, 0.98f);

    /**
     * Constructor
     *
     * @param scriptName
     * @param resultTextArea
     * @param nbrHdrLines
     * @param headerArrayList
     * @param printArrayList
     * @param columnHeaders
     */
    @SuppressWarnings("ConvertToStringSwitch")
    protected Q_PrintOneFile(String scriptName, JTextArea resultTextArea, int nbrHdrLines,
            ArrayList<String[]> headerArrayList, ArrayList<String[]> printArrayList, ArrayList<String> columnHeaders) {
        super();
        super.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        this.scriptName = scriptName;
        this.resultTextArea = resultTextArea;
        this.nbrHdrLines = nbrHdrLines;
        this.headerArrayList = headerArrayList;
        this.printArrayList = printArrayList;
        this.columnHeaders = columnHeaders;

        // Get application properties
        prop = new Q_Properties();
        windowHeight = new Integer(prop.getProperty("RESULT_WINDOW_HEIGHT"));
        windowWidth = new Integer(prop.getProperty("RESULT_WINDOW_WIDTH"));
        autoWindowSize = prop.getProperty("AUTO_WINDOW_SIZE");

        // Set parameters for page format
        // ------------------------------      
        // Set print request attributes
        attr_set = new HashPrintRequestAttributeSet();

        // Default media size A4
        mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
        attr_set.add(Chromaticity.MONOCHROME);

        // Default print values if print parameters are not specified in the script
        fontSize = 9; // print points
        mediaSizeScript = "A4";
        orientation = "PORTRAIT";
        leftMargin = 10; // mm
        rightMargin = 10; // mm
        topMargin = 10; // mm
        bottomMargin = 10; // mm

        // Get print values from comment line --;P if present
        if (!printArrayList.isEmpty()) {
            printValues = printArrayList.get(0); // First element only

            // Only 7 elements are processed, others are ignored
            // 1. Media size A4 / A3
            mediaSizeScript = printValues[0].toUpperCase();
            if (!mediaSizeScript.equals("A4") && !mediaSizeScript.equals("A3")
                    && !mediaSizeScript.equals("LETTER")) {
                mediaSizeScript = "A4";
            }
            // System.out.println("mediaSizeScript: "+mediaSizeScript);       

            // 2. Font size
            // If not empty in script - take fontSize from script
            try {
                fontSizeScript = printValues[1].substring(2); // n from FSn
                fontSize = new Integer(fontSizeScript);
            } catch (Exception e) {
                fontSize = 9; // Default 9 points if not number
            }
            // System.out.println("fontSize in points: "+fontSize);       

            // 3. Page orientation LANDSCAPE / L or PORTRAIT / P 
            orientation = printValues[2].toUpperCase();
            if (orientation.equals("L") || orientation.equals("LANDSCAPE")) {
                orientation = "LANDSCAPE";
            }
            if (orientation.equals("P") || orientation.equals("PORTRAIT")) {
                orientation = "PORTRAIT";
            }
            // System.out.println(orientation);

            // 4. Left margin
            try {
                String leftMarginStr = printValues[3].substring(2); // n from LMn
                leftMargin = new Integer(leftMarginStr);
            } catch (Exception e) {
                leftMargin = 10;
            }
            // System.out.println("leftMargin in mm: "+leftMargin);

            // 5. Right margin
            try {
                String rightMarginStr = printValues[4].substring(2); // n from RMn
                rightMargin = new Integer(rightMarginStr);
            } catch (Exception e) {
                rightMargin = 10;
            }
            // System.out.println("rightMargin in mm: "+rightMargin);

            // 6. Top margin
            try {
                String topMarginStr = printValues[5].substring(2); // n from TMn
                topMargin = new Integer(topMarginStr);
            } catch (Exception e) {
                topMargin = 10;
            }
            // System.out.println("topMargin in mm: "+topMargin);

            // 7. Bottom margin
            try {
                String bottomMarginStr = printValues[6].substring(2); // n from TMn
                bottomMargin = new Integer(bottomMarginStr);
            } catch (Exception e) {
                bottomMargin = 10;
            }
            // System.out.println("bottomMargin in mm: "+bottomMargin);

            // Get width and heigth of the page (A4, A3)
            if (mediaSizeScript.toUpperCase().equals("A4")) {
                mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
                attr_set.add(MediaSizeName.ISO_A4);
            } else if (mediaSizeScript.toUpperCase().equals("A3")) {
                mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A3);
                attr_set.add(MediaSizeName.ISO_A3);
            } else if (mediaSizeScript.toUpperCase().equals("LETTER")) {
                mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.NA_LETTER);
                attr_set.add(MediaSizeName.NA_LETTER);
            } else {
                // Default A4
                mediaSize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
                attr_set.add(MediaSizeName.ISO_A4);
            }
        }

        // Get media size in millimeters
        float[] mmSize = mediaSize.getSize(MediaPrintableArea.MM);
        mmWidth = mmSize[0];
        mmHeight = mmSize[1];

        Paper paper = new Paper();
        // Set dimensions in print points corresponding to dimensions in millimeters

        paper.setSize((mmWidth) * pointsInMM, (mmHeight) * pointsInMM);

        // Mac OS X prints out of paper when leftMargin = 0 in Landscape
        Properties sysProp = System.getProperties();
        if (sysProp.getProperty("os.name").contains("Mac OS") && orientation.equals("LANDSCAPE")) {
            os_correction = 4f;
        } else {
            os_correction = 0;
        }

        // Width of the printable area of the paper in print points
        pagePrintableWidth = (int) ((mmWidth) * pointsInMM);
        // Length of the printable area of the paper in print points
        pagePrintableHeight = (int) ((mmHeight) * pointsInMM);

        // Set the printable (and imageable) area of the paper
        paper.setImageableArea(0, 0, pagePrintableWidth, pagePrintableHeight);

        if (orientation.equals("LANDSCAPE")) {
            attr_set.add(OrientationRequested.LANDSCAPE);
        } else {
            attr_set.add(OrientationRequested.PORTRAIT);
        }

        // Create a printer job
        printerJob = PrinterJob.getPrinterJob();

        // Get page format from the printer job with for a set of attributes
        pageFormat = printerJob.getPageFormat(attr_set);

        // Set the page format to the printer job
        pageFormat.setPaper(paper);

        // Create and show window
        // ----------------------
        createWindow();
    }

    /**
     * Create and show window
     */
    private void createWindow() {

        language = prop.getProperty("LANGUAGE");
        Locale currentLocale = Locale.forLanguageTag(language);

        // Get resource bundle classes
        titles = ResourceBundle.getBundle("locales.L_TitleLabelBundle",
                currentLocale);
        buttons = ResourceBundle.getBundle("locales.L_ButtonBundle",
                currentLocale);

        titView = titles.getString("TitView");
        title = new JLabel(titView + " " + scriptName);

        titPage = titles.getString("TitPage");

        // Localized button labels
        exit = buttons.getString("Exit");
        print = buttons.getString("Print");

        returnButton = new JButton(exit);
        returnButton.setMinimumSize(new Dimension(95, 35));
        returnButton.setMaximumSize(new Dimension(95, 35));
        returnButton.setPreferredSize(new Dimension(95, 35));

        printButton = new JButton(print);
        printButton.setMinimumSize(new Dimension(120, 35));
        printButton.setMaximumSize(new Dimension(120, 35));
        printButton.setPreferredSize(new Dimension(120, 35));
        printButton.setForeground(DIM_BLUE); // Dim blue
        printButton.setFont(new Font("Helvetica", Font.PLAIN, 14));

        // Convert lines in text area into array list of lines
        // needed for page rendering
        String[] strArr = resultTextArea.getText().split("\n");
        for (int idx = 0; idx < strArr.length; idx++) {
            lineList.add(idx, strArr[idx]);
        }

        // Build window
        // ------------
        // Set print text to resultTextArea
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultTextArea.setEditable(false);
        resultTextArea.setBackground(VERY_LIGHT_BLUE); // blue
                
        // Set scroll pane with the result text area
        scrollPane.setViewportView(resultTextArea);
        screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        // Set maximum dimensions of the scroll pane
        scrollPane.setMaximumSize(new Dimension(screenWidth - 65, screenHeight - 180));

        Font titleFont = new Font("Helvetica", Font.PLAIN, 20);
        title.setFont(titleFont);

        // Build button row
        buttonPanel = new JPanel();

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setAlignmentX(Box.LEFT_ALIGNMENT);
        buttonPanel.add(returnButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(printButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 60)));

        // Lay out components in the window
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
                layout.createParallelGroup(Alignment.LEADING).addComponent(title)
                .addComponent(scrollPane).addComponent(buttonPanel)));
        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
                layout.createSequentialGroup().addComponent(title)
                .addComponent(scrollPane).addComponent(buttonPanel)));
        globalPanel.setLayout(layout);
        globalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Set "Return" button activity
        // ----------------------------
        returnButton.addActionListener(a -> {
            dispose();
        });

        // Set Print button activity
        // --------------------------
        printButton.addActionListener(a -> {
            printResult();
            // Hide the window
            dispose();
            // Call itself again
            new Q_PrintOneFile(scriptName, resultTextArea, nbrHdrLines, headerArrayList, printArrayList, columnHeaders);
        });

        // Enable ENTER key to save and return action
        // ------------------------------------------
        globalPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ENTER"), "print");
        globalPanel.getActionMap().put("print", new Action());

        container = getContentPane();
        container.add(globalPanel);

        // Y = pack the window to actual contents, N = set fixed size
        if (autoWindowSize.equals("Y")) {
           pack();
        } else {
            setSize(windowWidth, windowHeight);
        }

        setLocation(0, 0);

        // Show the window
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    } // End of createWindow()

    /**
     * Prints contents of the query result (file Print.txt)
     */
    protected void printResult() {
        // Page renderer is an object which prepares and prints a page
        pageRenderer = new FilePageRenderer(pageFormat);
        JScrollPane jsp = new JScrollPane(pageRenderer);
//        scrollPane.removeAll();
//        scrollPane.add(jsp);
        validate();

        printerJob.setPrintable(pageRenderer, pageFormat);

        // Printing with dialog
        boolean ok = printerJob.printDialog(attr_set);
        if (ok) {
            try {
                printerJob.print();
            } catch (PrinterException pe) {
                System.out.println(pe);
                pe.printStackTrace();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        } else {
            // System.out.println("Printer dialog canceled.");
        }

        /* // ????		
       // Printing wihout dialog
		 try { 
		    printerJob.print(attr_set); 
		 } catch (Exception e) {
		    e.printStackTrace(); 
		 }
         */ // ????			
    }

    /**
     * Inner class for ENTER key
     */
    class Action extends AbstractAction {

        protected static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            printResult();
        }
    }

    /**
     * Class to render contents of the file for printing
     */
    class FilePageRenderer extends JComponent implements Printable {

        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         *
         * @param file
         * @param pageFormat
         * @throws IOException
         */
        public FilePageRenderer(PageFormat pageFormat) {
            font = new Font("Monospaced", Font.PLAIN, fontSize);
            // The line vector contains all lines of the file to print
            lineVector = new ArrayList<>();
            for (int i = 0; i < lineList.size(); i++) {
                lineVector.add(lineList.get(i));
            }
            // Divide the lines into formatted pages
            formatPages(pageFormat);
        }

        /**
         * Format printer pages - divide lines from the file into pages
         *
         * @param pageFormat
         */
        final void formatPages(PageFormat pageFormat) {
            pageVector = new ArrayList<>();
            ArrayList<String> page = new ArrayList<>();

            if (orientation.equals("LANDSCAPE")) {
                // Heigth and Width are reversed
                imWidth = mmHeight * pointsInMM;
                imHeight = mmWidth * pointsInMM - 8f * pointsInMM;
                pageLengthInPoints = imHeight - (2 * bottomMargin + 5) * pointsInMM;
                pageLength = pageLengthInPoints / fontSize;
                pageWidthInPoints = imWidth;
                pageWidth = pageWidthInPoints / fontSize;
                nbrFooterLines = 2;
            } else {
                // Orientation Portrait
                imWidth = mmWidth * pointsInMM;
                imHeight = mmHeight * pointsInMM - 8f * pointsInMM;
                pageLengthInPoints = imHeight - (2 * bottomMargin + 5) * pointsInMM;
                pageLength = pageLengthInPoints / fontSize;
                pageWidthInPoints = imWidth;
                pageWidth = pageWidthInPoints / fontSize;
                nbrFooterLines = 2;
            }

            // Current y-coordinate of the line is measured in points (1/72 of an
            // inch). E. g. font size 12 gives 12/72 of an inch (25.4 mm) = 4.23 mm
            // Number of header lines is ZERO when printing result of a non-query statement.
            // Set y-coordinate to 0 - beginning of the page
            float y = 0;
            pageNumber = 1;
            int rest;
            // Subtract 3 for constant title lines of the query (script description and date)
            // because the lines are already present on the first page along with column headers.
            int lineCount = -3; 

            // Add ordinary (except last shorter) pages to the page vector
            // -----------------------------------------------------------
            for (int indx = 0; indx < lineVector.size(); indx++) {
                // The current line is the string from the line vector at the current index
                String line = (String) lineVector.get(indx);
                // Add font size to y-coordinate for the next line
                // Test y-coordinate for end of page.
                // If the data line y-coordinate is greater than the end of page
                // (i. e. page height),
                // the current page is completed:
                // - a footer is added, the page is added to the page vector,
                // - a new page is created,
                // - header lines are added at the beginning of the page,
                // - the y-coordinate of DATA lines is reset to 0
                if (y >= pageLengthInPoints - nbrFooterLines * fontSize) {
                    // Add footer (two lines) at the end of page 2 lines
                    page.add("");
                    page.add(titPage + pageNumber);
                    // Add the page to the page vector
                    pageVector.add(page);
                    // Create a new page
                    page = new ArrayList<>();

                    // Header array list is NULL when printing result of a non-query
                    // statement. It is non-null even for standard headers.
                    if (headerArrayList != null) {
                        // Add header lines to the page
                        for (int idx = 0; idx < columnHeaders.size(); idx++) {
                            /*
                            String hdrLine = "";
                            for (String headerValue : headerArrayList.get(idx)) {
                                hdrLine += headerValue + colSepSpaces;
                            }
                            page.add(hdrLine);
                            */
                            page.add(columnHeaders.get(idx));
                        }
                    }

                    // Reset y-coordinate to the line after header lines
                    y = fontSize * nbrHdrLines;
                    // Increase page number
                    pageNumber++;
                    // Reset line count
                    lineCount = 0;
                } else if (line.endsWith("\u0001")) {
                    // Unicode binary 1 is used as an invisible flag for skip to new line
                    rest = (int) (pageLength - lineCount - nbrHdrLines + 1);

                    /*
                    System.out.println("pageLength: " + pageLength);
                    System.out.println("rest: " + rest);
                    System.out.println("nbrHdrLines: " + nbrHdrLines);
                     */
                    
                    // Pad rest of page by empty lines before printing footer
                    if (rest > rest - nbrFooterLines) {
                        // Some lines are reserved for the footer (page number)
                        for (int i = 0; i < rest - nbrFooterLines; i++) {
                            page.add("");
                        }
                    }
                    // Add footer at the end of the page
                    page.add("");
                    page.add(titPage + pageNumber);

                    // Add the last page to page vector -
                    // if its length is greater than number of header lines
                    if (page.size() > nbrHdrLines) {
                        pageVector.add(page);
                    }
                    // Create a new page
                    page = new ArrayList<>();
                    
                    // Header array list is NULL when printing result of a non-query
                    // statement. It is non-null even for standard headers.
                    if (headerArrayList != null) {
                        // Add header lines to the page
                        for (int idx = 0; idx < columnHeaders.size(); idx++) {
                            page.add(columnHeaders.get(idx));
                        }                           
                    }
                    // Reset y-coordinate to the line after header lines
                    y = fontSize * nbrHdrLines;
                    // Increase page number
                    pageNumber++;
                    // Reset line count
                    lineCount = 0;
                }
                // Add font size to y-coordinate for drawing the next line
                y += fontSize;
                // Add the line to the current page and increment line count               
                page.add(line);
                lineCount++;
            }
            // Last page
            // ---------
            // rest of lines on the last page
            rest = (int) (pageLength - lineCount - nbrHdrLines + 1);

            /*
            System.out.println("pageLength: " + pageLength);
            System.out.println("rest: " + rest);
            System.out.println("nbrHdrLines: " + nbrHdrLines);
             */
            // Pad rest of page by empty lines before printing footer
            if (rest > rest - nbrFooterLines) {
                // Some lines are reserved for the footer (page number)
                for (int i = 0; i < rest - nbrFooterLines; i++) {
                    page.add("");
                }
            }
            // Footer at the end of the report
            page.add("");
            page.add(titPage + pageNumber);

            // Add the last page to page vector -
            // if its length is greater than number of header lines
            if (page.size() > nbrHdrLines) {
                pageVector.add(page);
            }
        }

        /**
         * Prints the graphical picture of current page
         */
        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
            if (pageIndex >= pageVector.size()) {
                return NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) g;

            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            /*
			// Draw rectangle for testing
			Rectangle2D rectangle = new Rectangle2D.Float(0, 0, imWidth, imHeight);
			g2.setPaint(Color.BLACK);
			g2.setStroke(new BasicStroke((float) 2.0));
			g2.draw(rectangle);
             */
            // Clip the page text drawn below with this clip rectangle
            // to blank out right margin
            Rectangle2D clipRectangle
                    = new Rectangle2D.Float(0, 0, imWidth - rightMargin * pointsInMM, imHeight);
            g2.setPaint(Color.WHITE); // make clip rectangle invisible
            // g2.setPaint(Color.RED); // make clip rectangle red
            g2.draw(clipRectangle);
            g2.setClip(clipRectangle);

            // Draw the page text lines
            ArrayList<String> page = (ArrayList<String>) pageVector.get(pageIndex);
            g2.setFont(font);
            g2.setPaint(Color.BLACK);
            // Draw print lines of the page in graphics context
            float x = (leftMargin + os_correction) * pointsInMM;
            // os_correction is 4f for Mac OS, 0 for Windows
            float y = fontSize + topMargin * pointsInMM;
            for (int i = 0; i < page.size(); i++) {
                String line = (String) page.get(i);
                if (line.length() > 0) {
                    g2.drawString(line, x, y);
                }
                y += fontSize;
            }

            // Paint contents of the page
            paint(g2);
            return PAGE_EXISTS;
        }
    }
}
