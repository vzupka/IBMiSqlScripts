package queries;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JTextArea;

/**
 * Class to perform an SQL statement
 *
 * @author Vladimír Župka 2016
 *
 */
public class Q_ScriptRun {

    String language;
    Locale currentLocale;
    ResourceBundle locMessages;
    String stmtSuccess, numParError, sqlError, otherError, colNam, noMatch, noConn,
            missingSummaryColumn, invalidColumn;

    Connection conn;
    String statement; // Text of SQL statement
    String scriptName;
    String query_or_update;
    PreparedStatement pstmt; // Prepared statement
    Statement stmt; // SQL statement (object)
    ResultSet rs; // Database result set
    ResultSetMetaData rsmd; // Result set meta data
    String[] retCode = new String[2];

    JTextArea resultTextArea;
    JTextArea summaryTextArea;
    String printLine;
    int sumCol;

    ArrayList<String> colNames;
    ArrayList<Integer> colTypes;
    ArrayList<Integer> colScales;
    ArrayList<Integer> colIndexes;
    ArrayList<Integer> colLengths;

    ArrayList<String> allColNames;
    ArrayList<Integer> allColTypes;
    ArrayList<Integer> allColScales;
    ArrayList<Integer> allColIndexes;
    ArrayList<Integer> allColLengths;

    int[] colStrPositions;
    int[] colEndPositions;

    ArrayList<String[]> markerArrayList;
    ArrayList<String[]> headerArrayList;
    ArrayList<String> columnHeaders;
    ArrayList<String[]> totalArrayList;
    ArrayList<String[]> patternArrayList;
    ArrayList<String[]> printArrayList;
    ArrayList<String> titleArrayList;
    ArrayList<String[]> levelArrayList;
    ArrayList<String[]> summaryArrayList;
    ArrayList<String[]> summaryArrayListWork;
    ArrayList<String[]> summaryIndArrayList;
    ArrayList<String[]> omitArrayList;
    ArrayList<String> omittedColNames;
    ArrayList<String> omittedColValues;
    ArrayList<Integer> omittedColIndexes;
    ArrayList<String> variableNames;
    ArrayList<String> variableValues;
    ArrayList<Integer> variableStarts;
    ArrayList<Integer> variableEnds;
    ArrayList<String> levelBreakNames;
    ArrayList<Object> levelBreakValues;
    ArrayList<Object> lastNonNulllevelBreakValues;
    ArrayList<String> detColNames;
    ArrayList<String> detColValues;
    ArrayList<String> summaryColNames;

    // Helper object for header array list to be saved and restored 
    ArrayList<String[]> headerArrayListSaved;
    String[] stdHdrValues;

    ArrayList<Integer> summaryColIndexes;
    ArrayList<Integer> summaryColTypes;

    String[][] summaryIndications;
    String[] summaryIndValues;

    // Indexes of values in summary decAccumulators
    final int SUM_INDEX = 0;
    final int AVG_INDEX = 1;
    final int MAX_INDEX = 2;
    final int MIN_INDEX = 3;
    final int COUNT_INDEX = 4;

    // Indexes of summary values in --;S line
    final int NAME_IND = 0;
    final int SUM_IND = 1;
    final int AVG_IND = 2;
    final int MAX_IND = 3;
    final int MIN_IND = 4;
    final int COUNT_IND = 5;

    final BigDecimal DECIMAL_MIN = new BigDecimal(-999999999999999999L);
    final BigDecimal DECIMAL_ZERO = new BigDecimal(0);
    final BigDecimal DECIMAL_ONE = new BigDecimal(1);
    final BigDecimal DECIMAL_MAX = new BigDecimal(999999999999999999L);

    final Long BINARY_MIN = -999999999999999999L;
    final Long BINARY_ZERO = 0L;
    final Long BINARY_ONE = 1L;
    final Long BINARY_MAX = 999999999999999999L;

    final Date DATE_MIN = new java.sql.Date(-999999999999999999L);
    final Date DATE_MAX = new java.sql.Date(999999999999999999L);

    final Time TIME_MIN = new java.sql.Time(-999999999999999999L);
    final Time TIME_MAX = new java.sql.Time(999999999999999999L);

    final Timestamp TIMEST_MIN = new java.sql.Timestamp(-999999999999999999L);
    final Timestamp TIMEST_MAX = new java.sql.Timestamp(999999999999999999L);

    final String CHAR_MIN = " ";
    final String CHAR_MAX = "\uFFFF";

    boolean atLeastOneSUM;
    boolean atLeastOneAVG;
    boolean atLeastOneMAX;
    boolean atLeastOneMIN;
    boolean atLeastOneCOUNT;

    boolean isDataNull;

    BigDecimal[][][] decAccumulators;
    BigDecimal[][] decTotalAccumulators;

    Long[][][] binAccumulators;
    Long[][] binTotalAccumulators;

    Date[][][] datAccumulators;
    Date[][] datTotalAccumulators;

    Time[][][] timAccumulators;
    Time[][] timTotalAccumulators;

    Timestamp[][][] timstAccumulators;
    Timestamp[][] timstTotalAccumulators;

    String[][][] strAccumulators;
    String[][] strTotalAccumulators;

    String[][] headerValues;

    String[] patternValues;
    NumberFormat nf;
    DecimalFormat df;
    String decimalPattern;
    String patternColName;
    Integer[] maxHeaderWidths;
    String[] printValues;
    String[] omitValues;

    String colData;

    String columnData;
    String colSepSpaces;
    String leadingSumString;
    String leadingAvgString;
    String leadingMaxString;
    String leadingMinString;
    String leadingCountString;
    String leadingString;

    StringBuilder printLineSUM = new StringBuilder();
    StringBuilder printLineAVG = new StringBuilder();
    StringBuilder printLineMAX = new StringBuilder();
    StringBuilder printLineMIN = new StringBuilder();
    StringBuilder printLineCOUNT = new StringBuilder();
    // Spaces inserted at the beginning of print line if prefixes
    // are longer than starting position of the first column
    String leftPrintLinePadding;

    String nullPrintMark;

    StringBuilder titleBuf;
    StringBuilder headerBuf;
    StringBuilder dataBuf;

    final int MARKER_POSITION_INDEX = 0;
    final int MARKER_TYPE_INDEX = 1;
    final int MARKER_TEXT_INDEX = 2;
    final int MARKER_VALUE_INDEX = 3;

    String defaultDecPattern;
    String decNumberEdited;

    String[] levelValues;
    int level;
    String[] sumValues;

    String[] totalValues;
    Integer spaceB;
    Integer spaceA;
    boolean[] newPage;
    // Number of lines for result report header
    int nbrTitleLines;
    // Number of lines for page header
    int nbrHdrLines;

    Path workfilesTxt = Paths.get(System.getProperty("user.dir"), "workfiles", "Print.txt");
    Path printfilesTxt;

    Path workfilesClob = Paths.get(System.getProperty("user.dir"), "workfiles", "CLOB.txt");

    /**
     * Performs the SQL statement
     *
     * @param conn Connection object for DB2 database
     * @param scriptName Name of the script - the file name without ".sql"
     * @param stmtDescription Description of the query script from the first comment line
     * @param statement Text of the SQL statement
     * @param markerArrayLst List of values describing "?" markers in the SQL statement
     * @param headerArrayLst List of values describing column headers
     * @param totalArrayLst List of values describing adjusting of lines of null column values
     * @param patternArrayLst List of values describing decimal number format
     * @param printArrayLst List of values describing print format
     * @param titleArrayLst List of title lines with possible variables
     * @param levelArrayLst List of summary levels
     * @param summaryArrayLst List of summary columns
     * @param summaryIndArrayLst List of summary indicators
     * @param omitArrayLst List of omitted columns
     *
     * @return String[] Return code as array
     *
     */    
    @SuppressWarnings({"CallToPrintStackTrace", "null", "UnusedAssignment"})
    public String[] runScript(Connection conn, String scriptName, String stmtDescription,
            String statement, ArrayList<String[]> markerArrayLst, ArrayList<String[]> headerArrayLst,
            ArrayList<String[]> totalArrayLst, ArrayList<String[]> patternArrayLst,
            ArrayList<String[]> printArrayLst, ArrayList<String> titleArrayLst,
            ArrayList<String[]> levelArrayLst, ArrayList<String[]> summaryArrayLst,
            ArrayList<String[]> summaryIndArrayLst, ArrayList<String[]> omitArrayLst) {
        this.conn = conn;
        this.scriptName = scriptName;
        this.markerArrayList = markerArrayLst;
        this.headerArrayList = headerArrayLst;
        this.totalArrayList = totalArrayLst;
        this.patternArrayList = patternArrayLst;
        this.printArrayList = printArrayLst;
        this.titleArrayList = titleArrayLst;
        this.levelArrayList = levelArrayLst;
        this.summaryArrayList = summaryArrayLst;
        this.summaryIndArrayList = summaryIndArrayLst;
        this.omitArrayList = omitArrayLst;
        this.statement = statement;

        // try to connect database
        conn = new Q_ConnectDB().connect();

        // Get application properties
        Q_Properties prop = new Q_Properties();
        nullPrintMark = prop.getProperty("NULL_PRINT_MARK");

        // Number of column separation spaces
        colSepSpaces = prop.getProperty("COLUMN_SEPARATING_SPACES");
        // Convert the string to a number
        @SuppressWarnings("UnusedAssignment")
        Integer nbrColSpaces = 0;
        try {
            nbrColSpaces = new Integer(colSepSpaces);
        } catch (NumberFormatException nfe) {
            nbrColSpaces = 0;
            colSepSpaces = "";
        }
        // Create a character array with this number of spaces
        char[] colSpacesArr = new char[nbrColSpaces];
        Arrays.fill(colSpacesArr, ' ');
        // Convert the array to a string
        colSepSpaces = String.valueOf(colSpacesArr);

        language = prop.getProperty("LANGUAGE");
        defaultDecPattern = prop.getProperty("DECIMAL_PATTERN");
        currentLocale = Locale.forLanguageTag(language);
        locMessages = ResourceBundle.getBundle("locales.L_MessageBundle", currentLocale);

        stmtSuccess = locMessages.getString("StmtSuccess");
        numParError = locMessages.getString("NumParError");
        sqlError = locMessages.getString("SqlError");
        otherError = locMessages.getString("OtherError");
        colNam = locMessages.getString("ColNam");
        noMatch = locMessages.getString("NoMatch");
        noConn = locMessages.getString("NoConn");
        missingSummaryColumn = locMessages.getString("MissingSummaryColumn");
        invalidColumn = locMessages.getString("InvalidColumn");

        // ========================
        // Process an SQL statement
        // ========================
        // Initial values for vertical print formatting
        spaceB = 0;
        spaceA = 0;
        leftPrintLinePadding = "";

        // Save original header array list from input parameters (even empty one)
        headerArrayListSaved = new ArrayList<>();
        headerArrayListSaved.addAll(headerArrayLst);

        try {
            // Create path to the script file
            printfilesTxt = Paths.get(System.getProperty("user.dir"), "printfiles", scriptName + ".txt");
            // No error message initially
            retCode[1] = "";

            resultTextArea = new JTextArea();

            // If no connection to database exists report the error
            // "No connection to the server."
            if (conn == null) {
                String msg = noConn;
                reportError(resultTextArea, msg);
            }

            if (!markerArrayList.isEmpty()) {
                // Definitions --;? = marker array list.
                // ----------------
                // If marker array list contains entries process the definition lines
                String[] markerValues;
                // Prepare SQL statement from script
                pstmt = conn.prepareCall(statement, ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                // Process marker values - set values to SQL marker positions
                for (int idx = 0; idx < markerArrayList.size(); idx++) {
                    markerValues = markerArrayList.get(idx);
                    String markerPosition = markerValues[MARKER_POSITION_INDEX];
                    // Check values of numeric, date/time, or binary values for
                    // formal correctness before running of SQL statement.
                    // In case of error input window is displayed again
                    // with an error message.
                    switch (markerValues[MARKER_TYPE_INDEX].toUpperCase()) {
                        case "DEC":
                        case "DECIMAL":
                            pstmt.setBigDecimal(new Integer(markerPosition),
                                    new BigDecimal(markerValues[MARKER_VALUE_INDEX]));
                            break;
                        case "INT":
                        case "INTEGER":
                            pstmt.setInt(new Integer(markerPosition),
                                    new Integer(markerValues[MARKER_VALUE_INDEX]));
                            break;
                        case "DATE":
                            pstmt.setDate(new Integer(markerPosition),
                                    Date.valueOf(markerValues[MARKER_VALUE_INDEX]));
                            break;
                        case "TIME":
                            pstmt.setTime(new Integer(markerPosition),
                                    Time.valueOf(markerValues[MARKER_VALUE_INDEX]));
                            break;
                        case "TIMESTAMP":
                            pstmt.setTimestamp(new Integer(markerPosition),
                                    Timestamp.valueOf(markerValues[MARKER_VALUE_INDEX]));
                            break;
                        // Remaining types - (var)char, (var)graphic etc.
                        default:
                            // JDBC converts Object to the appropriate SQL type
                            pstmt.setObject(new Integer(markerPosition), markerValues[MARKER_VALUE_INDEX]);
                    }
                }
                // Decision if PREPARED STATEMENT is QUERY or UPDATE
                boolean isQuery = pstmt.execute();
                if (isQuery) {
                    rs = pstmt.getResultSet();
                    query_or_update = "QUERY";
                } else {
                    pstmt.getUpdateCount();
                    query_or_update = "UPDATE";
                }
                // End of marker definitions processing

            } else {
                // No --;? definitions.
                // --------------------
                // If marker array list is empty make decision if STATEMENT is QUERY or UPDATE
                stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                boolean isQuery = stmt.execute(statement);
                if (isQuery) {
                    rs = stmt.getResultSet();
                    query_or_update = "QUERY";
                } else {
                    stmt.getUpdateCount();
                    query_or_update = "UPDATE";
                }
            }

            // Non-query statement
            // ===================
            if (query_or_update.equals("UPDATE")) {
                retCode[0] += "UPDATE";
                retCode[1] = stmtSuccess;

                // Append the statement to the empty result text area
                resultTextArea.append(statement);

                // Put contents of the result text area into an array list
                ArrayList<String> lines = new ArrayList<>();
                String[] lineArr = resultTextArea.getText().split("\n");
                for (int idx = 0; idx < lineArr.length; idx++) {
                    lines.add(idx, lineArr[idx]);
                }
                // Add the confirmation of the successful statement completion.
                lines.add(stmtSuccess);
                // Append result of the script to text files
                Files.write(workfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                Files.write(printfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            }

            // Query statement - evaluate and process its result set
            // ===============
            if (query_or_update.equals("QUERY")) {
                retCode[0] += "QUERY";

                // Add title line with statement description to the result
                String title = stmtDescription + "\n\n";
                resultTextArea.append(title);
                // Add second title line date and time localized to the result
                currentLocale = Locale.forLanguageTag(language);
                DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
                        DateFormat.DEFAULT, currentLocale);
                java.util.Date date = new java.util.Date();
                title = formatter.format(date) + "\n\n";
                resultTextArea.append(title);

                rsmd = rs.getMetaData();

                // Create lists of all column characteristics
                allColNames = new ArrayList<>();
                allColIndexes = new ArrayList<>();
                allColTypes = new ArrayList<>();
                allColScales = new ArrayList<>();
                allColLengths = new ArrayList<>();
                // Create characteristics of columns without omitted ones
                colNames = new ArrayList<>();
                colIndexes = new ArrayList<>();
                colTypes = new ArrayList<>();
                colScales = new ArrayList<>();
                colLengths = new ArrayList<>();

                // Create a new print data buffer
                dataBuf = new StringBuilder("");

                // Definition --;O = column names omitted from output
                // ---------------
                // Fill list of column names omitted from output
                omittedColNames = new ArrayList<>();
                if (!omitArrayList.isEmpty()) {
                    for (int om = 0; om < omitArrayList.size(); om++) {
                        omitValues = omitArrayList.get(om);
                        // Column names omitted from output
                        for (int in = 0; in < omitValues.length; in++) {
                            String omittedColName = omitArrayList.get(om)[in].trim().toUpperCase();
                            if (!omittedColName.isEmpty()) {
                                omittedColNames.add(omittedColName);
                            }
                        }
                    }
                }
                // System.out.println("omittedColNames: " + omittedColNames);

                // -----------------------------------
                // Process FIRST row of the result set
                // -----------------------------------
                // Set cursor to the FIRST ROW - RECORD from the result set
                // If the result set is empty, no
                if (rs.first()) {
                    // Null flag is off at the beginning of detail row processing
                    isDataNull = false;

                    // Get characteristics of ALL COLUMNS
                    for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                        allColNames.add(rsmd.getColumnName(col));
                        allColLengths.add(rsmd.getColumnDisplaySize(col));
                        allColTypes.add(rsmd.getColumnType(col));
                        allColScales.add(rsmd.getScale(col));
                        allColIndexes.add(col);
                        // If any column value is NULL 
                        // - set on the detail row null flag
                        if (rs.getObject(col) == null) {
                            isDataNull = true;
                        }
                    }
// ????
                    /*
                    System.out.println("allColNames      : " + allColNames);
                    System.out.println("allColLengths    : " + allColLengths);
                    System.out.println("allColIndexes    : " + allColIndexes);
                    System.out.println("allColTypes      : " + allColTypes);
                    System.out.println("allColScales     : " + allColScales);
                    System.out.println();
                     */

                    // Get values of OMITTED columns if there is at least one
                    // that is not empty (nothing after the last semicolon)
                    if (omittedColNames.size() > 0) {
                        omittedColValues = new ArrayList<>();
                        omittedColIndexes = new ArrayList<>();

                        // Fill the list with empty strings
                        for (int om = 0; om < omittedColNames.size(); om++) {
                            omittedColValues.add(om, "");
                        }
                        // Get column values for omitted column names
                        for (int om = 0; om < omittedColNames.size(); om++) {
                            for (int colNamesIndex = 0; colNamesIndex < allColNames
                                    .size(); colNamesIndex++) {
                                if (omittedColNames.get(om).equals(allColNames.get(colNamesIndex))) {
                                    if (!allColNames.get(colNamesIndex).isEmpty()) {
                                        omittedColIndexes.add(colNamesIndex + 1);
                                    }
                                    break;
                                }
                            }
                        }
                        // Edit omitted column values (numeric, other type, null)
                        for (int om = 0; om < omittedColIndexes.size(); om++) {
                            for (int colNamesIndex = 0; colNamesIndex < allColNames
                                    .size(); colNamesIndex++) {
                                int column = omittedColIndexes.get(om) - 1;
                                if (allColScales.get(column) > 0 || allColTypes.get(column) == Types.NUMERIC
                                        || allColTypes.get(column) == Types.DECIMAL) {
                                    BigDecimal dec = rs.getBigDecimal(omittedColIndexes.get(om));
                                    omittedColValues.set(om,
                                            editNumericDecimal(dec, omittedColNames.get(om)));
                                    // columnData = rightAdjust(colData, maxHeaderWidths[column]);
                                } else if (rs.getObject(omittedColIndexes.get(om)) != null) {
                                    omittedColValues.set(om, rs.getObject(omittedColIndexes.get(om)).toString());
                                } else {
                                    omittedColValues.set(om, nullPrintMark);
                                }
                            }
                        }
                    }
// ????
                    /*
                    System.out.println("omittedColNames  : " + omittedColNames);
                    System.out.println("omittedColIndexes: " + omittedColIndexes);
                    System.out.println("omittedColValues : " + omittedColValues);
                    System.out.println();
                     */

                    // Get FIRST detail values of ALL columns (including omitted ones) 
                    // for &NAME variables in title lines
                    detColNames = new ArrayList<>();
                    detColValues = new ArrayList<>();
                    for (int colNameIndex = 0; colNameIndex < allColNames.size(); colNameIndex++) {
                        detColNames.add(allColNames.get(colNameIndex));
                        detColValues.add(rs.getString(colNameIndex + 1));
                    }

                    // Definitions --;t = title lines with possible variables
                    // ----------------
                    // Print title lines                    
                    if (!titleArrayList.isEmpty()) {
                        for (int in = 0; in < titleArrayList.size(); in++) {
                            String titleHeader = getValuesForVariables(titleArrayList.get(in), detColNames,
                                    detColValues);
                            // Add resulting title header to the result text area
                            resultTextArea.append(titleHeader + "\n");
                        }
                    }

                    for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                        colNames.clear();
                        colLengths.clear();
                        colTypes.clear();
                        colScales.clear();
                        colIndexes.clear();
                    }

                    // Get column characteristics without omitted columns
                    // --------------------------------------------------
                    if (!omittedColNames.isEmpty()) {
                        for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                            boolean matched = false;
                            for (int om = 0; om < omittedColNames.size(); om++) {
                                if (omittedColNames.get(om).equals(rsmd.getColumnName(col))) {
                                    matched = true;
                                }
                            }
                            if (matched == false) {
                                colNames.add(rsmd.getColumnName(col));
                                colLengths.add(rsmd.getColumnDisplaySize(col));
                                colTypes.add(rsmd.getColumnType(col));
                                colScales.add(rsmd.getScale(col));
                                colIndexes.add(col);
                            }
                        }
                    } else {
                        // Column characteristics are identical with ALL columns
                        for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                            colNames = allColNames;
                            colLengths = allColLengths;
                            colTypes = allColTypes;
                            colScales = allColScales;
                            colIndexes = allColIndexes;
                        }
                    }
// ????                  
                    /*
                    System.out.println("colNames         : " + colNames);
                    System.out.println("colLengths       : " + colLengths);
                    System.out.println("colIndexes       : " + colIndexes);
                    System.out.println("colTypes         : " + colTypes);
                    System.out.println("colScales        : " + colScales);
                     */

                    // Prepare processing of summary values
                    if (!levelArrayList.isEmpty()) {

                        // Definition --;S = column names to be summarized and types of summarization
                        // ---------------
                        // Get array of summary values from --;S lines with 6 elements
                        if (!summaryArrayList.isEmpty()) {
                            // Sort the original summary array list by column names to the order 
                            // as entered in SELECT list
                            summaryArrayListWork = new ArrayList<>();
                            for (int idx = 0; idx < colNames.size(); idx++) {
                                for (int sumNam = 0; sumNam < summaryArrayList.size(); sumNam++) {
                                    if (!summaryArrayList.get(sumNam)[NAME_IND].isEmpty()) {
                                        if (summaryArrayList.get(sumNam)[NAME_IND].toUpperCase().equals(colNames.get(idx))) {
                                            summaryArrayListWork.add(summaryArrayList.get(sumNam));
                                        }
                                    }
                                }
                            }
                            summaryArrayList.clear();
                            summaryArrayList.addAll(summaryArrayListWork);

                            // Prepare summary column names and indications of summary type
                            summaryColNames = new ArrayList<>();
                            summaryIndications = new String[summaryArrayList.size()][6];
                            // Get summary names and indicators from --;S comment lines
                            for (int sumNam = 0; sumNam < summaryArrayList.size(); sumNam++) {
                                // Get non-empty summary names from the first element of the summary array
                                if (!summaryArrayList.get(sumNam)[NAME_IND].isEmpty()) {
                                    summaryColNames.add(summaryArrayList.get(sumNam)[NAME_IND].toUpperCase());
                                }
                                // Copy 6 values from comment lines (the first value is the column name)
                                System.arraycopy(summaryArrayList.get(sumNam), 0, summaryIndications[sumNam], COUNT_IND + 1, 0);
                            }
                            // Clear summary indicators arrays
                            for (int sumNam = 0; sumNam < summaryArrayList.size(); sumNam++) {
                                for (int in = SUM_IND; in < COUNT_IND + 1; in++) {
                                    summaryIndications[sumNam][in] = "";
                                }
                            }
                            // Sort summary indications according to their correct order in the array:
                            // SUM, AVG, MAX, MIN, COUNT
                            atLeastOneSUM = false;
                            atLeastOneAVG = false;
                            atLeastOneMAX = false;
                            atLeastOneMIN = false;
                            atLeastOneCOUNT = false;
                            for (int sumNam = 0; sumNam < summaryArrayList.size(); sumNam++) {
                                for (int in = SUM_IND; in < COUNT_IND + 1; in++) {
                                    for (int i = SUM_IND; i < COUNT_IND + 1; i++) {
                                        if (summaryArrayList.get(sumNam)[i].equals("S")) {
                                            summaryIndications[sumNam][SUM_IND] = "S";
                                            atLeastOneSUM = true;
                                        }
                                        if (summaryArrayList.get(sumNam)[i].equals("A")) {
                                            summaryIndications[sumNam][AVG_IND] = "A";
                                            atLeastOneAVG = true;
                                        }
                                        if (summaryArrayList.get(sumNam)[i].equals("M")) {
                                            summaryIndications[sumNam][MAX_IND] = "M";
                                            atLeastOneMAX = true;
                                        }
                                        if (summaryArrayList.get(sumNam)[i].equals("m")) {
                                            summaryIndications[sumNam][MIN_IND] = "m";
                                            atLeastOneMIN = true;
                                        }
                                        if (summaryArrayList.get(sumNam)[i].equals("C")) {
                                            summaryIndications[sumNam][COUNT_IND] = "C";
                                            atLeastOneCOUNT = true;
                                        }
                                    }
                                }
                            }
                        }
                        // Prepare control level processing
                        // Get FIRST level break values for comparison
                        getFirstLevelBreakValues();
                        // Initialize new page indicators
                        newPage = new boolean[levelArrayList.size()];
                        for (int idx = 0; idx < newPage.length; idx++) {
                            newPage[idx] = false;
                        }
                        // Initialize accumulators for summary values
                        if (!summaryArrayList.isEmpty()) {
                            initializeAccumulators();
                        }
                    }

                    // Definitions --;T = how to mark identical values in a group except the first 
                    // ----------------
                    if (!totalArrayList.isEmpty()) {
                        String stringSpaces = "";
                        // Only the FIRST element (index 0) of the list is taken
                        // because only one comment line can be used.
                        totalValues = totalArrayList.get(0);
                        // Spaces before. May have form Bn. Defaults to 0.
                        if (totalValues[0].isEmpty()) {
                            stringSpaces = "";
                        } else if (totalValues[0].substring(0, 1).toUpperCase().equals("B")) {
                            stringSpaces = totalValues[0].substring(1);
                        } else {
                            stringSpaces = totalValues[0];
                        }
                        try {
                            spaceB = new Integer(stringSpaces);
                        } catch (Exception e) {
                            spaceB = 0;
                        }
                        // Spaces after. May have form An. Defaults to 0.
                        if (totalValues[1].isEmpty()) {
                            stringSpaces = "";
                        } else if (totalValues[1].substring(0, 1).toUpperCase().equals("A")) {
                            stringSpaces = totalValues[1].substring(1);
                        } else {
                            stringSpaces = totalValues[1];
                        }
                        try {
                            spaceA = new Integer(stringSpaces);
                        } catch (Exception e) {
                            spaceA = 0;
                        }
                        // Null mark
                        if (totalValues.length > 2) {
                            nullPrintMark = totalValues[2];
                        }
                        // Number of COLUMN SEPARATING SPACES. May have form Sn
                        // Sefaults to 1.
                        if (totalValues.length > 3) {
                            if (totalValues[3].isEmpty()) {
                                // Default from Parameters
                                stringSpaces = colSepSpaces;
                            } else if (totalValues[3].substring(0, 1).toUpperCase().equals("S")) {
                                stringSpaces = totalValues[3].substring(1);
                            } else {
                                stringSpaces = totalValues[3];
                            }
                            try {
                                Integer newNbrColSpaces = new Integer(stringSpaces);
                                // Create a character array with this number of spaces
                                colSpacesArr = new char[newNbrColSpaces];
                            } catch (Exception e) {
                                nbrColSpaces = 1;
                            }
                        }
                    } else {
                        // If no --T; definition is specified take default definitions
                        // spaces before = 0,
                        // spaces after = 0,
                        // null mark = value from application parameters,
                        // column separating spaces = value from application parameters                        
                        colSpacesArr = new char[nbrColSpaces];
                    }
                    // Create a character array with the number of SPACES
                    // from application parameters
                    Arrays.fill(colSpacesArr, ' ');
                    // Convert the array to a string
                    colSepSpaces = String.valueOf(colSpacesArr);

                    // Assemble column headers and their maximum width for subsequent processing
                    // -------------------------------------------------------------------------
                    // - printing headers
                    // - calculating starting and ending positions of columns
                    buildColumnHeaders();

                    // Create arrays for starting and ending column positions
                    colStrPositions = new int[colNames.size()];
                    colEndPositions = new int[colNames.size()];

                    // Assemble leading strings with prefixes for summary value lines
                    // ------------------------
                    if (!levelArrayList.isEmpty()) {
                        // Default prefixes
                        String[] prefix = {"SUM", "AVG", "MAX", "MIN", "COUNT"};
                        int maxPrefixLength = 0;
                        // Create prefixes if specified in comment line --;s
                        if (!summaryIndArrayList.isEmpty()) {
                            // Summary indicator values can be indexed by SUM_INDEX,
                            // AVG_INDEX, MAX_INDEX, MIN_INDEX, COUNT_INDEX
                            summaryIndValues = new String[summaryIndArrayList.get(0).length];
                            for (int in = 0; in < summaryIndArrayList.get(0).length; in++) {
                                summaryIndValues[in] = summaryIndArrayList.get(0)[in];
                                // If --;s line is specified defaults are replaced
                                if (!summaryIndValues[in].isEmpty()) {
                                    prefix[in] = summaryIndValues[in];
                                    if (prefix[in].length() > maxPrefixLength) {
                                        maxPrefixLength = prefix[in].length();
                                    }
                                }
                            }
                        } else {
                            maxPrefixLength = "COUNT".length();
                        }

                        // Positions and widths of printed columns - first version.
                        // --------------------
                        // This is needed for deriving leftPrintLinePadding          
                        colStrPositions[0] = 0;
                        if (colLengths.size() > 1) {
                            colEndPositions[0] = Math.max(colLengths.get(1), maxHeaderWidths[0])
                                    + colSepSpaces.length();
                        }
                        // Create array of next start and end positions considering maximum column widths
                        for (int col = 1; col < colLengths.size(); col++) {
                            int printPrevColWidth = Math.max(colLengths.get(col - 1),
                                    maxHeaderWidths[col - 1]);
                            int printCurrColWidth = Math.max(colLengths.get(col), maxHeaderWidths[col]);
                            colStrPositions[col] = colStrPositions[col - 1] + printPrevColWidth
                                    + colSepSpaces.length();
                            colEndPositions[col] = colEndPositions[col - 1] + printCurrColWidth
                                    + colSepSpaces.length();
                        }

                        // Calculate leading strings for summarized values
                        // -------------------------
                        if (!summaryArrayList.isEmpty()) {
                            // Leading string before the first summarized value -
                            // - at the beginning of the summary line
                            leadingSumString = "";
                            leadingAvgString = "";
                            leadingMaxString = "";
                            leadingMinString = "";
                            leadingCountString = "";
                            int len = 0;
                            int leadStringLength = 0;
                            endLoop:
                            for (int col = 0; col < colNames.size(); col++) {
                                for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                                    if (summaryColNames.get(sumNam).equals(colNames.get(col))) {
                                        // leadStringLength is offset to the FIRST summarized column
                                        // = column start position
                                        // minus (col# * (#col.separating.spaces - 1))
                                        leadStringLength = colStrPositions[col]
                                                - (col) * (colSepSpaces.length() - 1);
                                        leftPrintLinePadding = "";
                                        if (leadStringLength - maxPrefixLength <= 0) {
                                            leftPrintLinePadding = getSpaces(maxPrefixLength - leadStringLength + 1);
                                            leadStringLength = 0;
                                        }
                                        // Leading strings for 6 summarizing methods: SUM, AVG, MAX, MIN, COUNT
                                        // Leading string length is mostly positive if there is place enough
                                        // before start position of the first summarized column
                                        if (leadStringLength > 0) {
                                            // Length = length of leading string - prefix length
                                            len = leadStringLength - prefix[SUM_INDEX].length();
                                            // Leading string = spaces + prefix
                                            leadingSumString = getSpaces(len) + prefix[SUM_INDEX];
                                            len = leadStringLength - prefix[AVG_INDEX].length();
                                            leadingAvgString = getSpaces(len) + prefix[AVG_INDEX];
                                            len = leadStringLength - prefix[MAX_INDEX].length();
                                            leadingMaxString = getSpaces(len) + prefix[MAX_INDEX];
                                            len = leadStringLength - prefix[MIN_INDEX].length();
                                            leadingMinString = getSpaces(len) + prefix[MIN_INDEX];
                                            len = leadStringLength - prefix[COUNT_INDEX].length();
                                            leadingCountString = getSpaces(len) + prefix[COUNT_INDEX];
                                        } else {
                                            // Leading string length comes out negative if the first column is summerized
                                            leadingSumString = getSpaces(maxPrefixLength - prefix[SUM_INDEX].length()) + prefix[SUM_INDEX];
                                            leadingAvgString = getSpaces(maxPrefixLength - prefix[AVG_INDEX].length()) + prefix[AVG_INDEX];
                                            leadingMaxString = getSpaces(maxPrefixLength - prefix[MAX_INDEX].length()) + prefix[MAX_INDEX];
                                            leadingMinString = getSpaces(maxPrefixLength - prefix[MIN_INDEX].length()) + prefix[MIN_INDEX];
                                            leadingCountString = getSpaces(maxPrefixLength - prefix[COUNT_INDEX].length()) + prefix[COUNT_INDEX];
                                        }
                                        break endLoop;
                                    }
                                }
                            }
                        }
                    }

                    // Column headers processing (number of header lines counted beforehand)
                    // -------------------------                    
                    headerBuf = new StringBuilder(leftPrintLinePadding);
                    columnHeaders = new ArrayList<>();
                    if (!headerArrayList.isEmpty()) {
                        for (int hdr = 0; hdr < headerArrayList.size(); hdr++) {
                            for (int i = 0; i < colNames.size(); i++) {
                                headerBuf.append(headerValues[hdr][i]).append(colSepSpaces);
                            }
                            // Create list of user column headers for print program Q_PringOneFile.
                            columnHeaders.add(headerBuf.toString());
                            // Append the header line to the result text area
                            resultTextArea.append(headerBuf.toString() + "\n");
                            // Clear this header line for next header line
                            headerBuf = new StringBuilder(leftPrintLinePadding);
                        }
                    } else {
                        for (int col = 0; col < colNames.size(); col++) {
                            // Append next header value to the line
                            headerBuf.append(stdHdrValues[col]).append(colSepSpaces);
                        }
                        // Create list of standard column headers for print program
                        // Q_PringOneFile.                        
                        columnHeaders.add(headerBuf.toString());
                        // Append the header line to the result text area
                        resultTextArea.append(headerBuf.toString() + "\n");
                        // Clear the header line buffer
                        headerBuf = new StringBuilder(leftPrintLinePadding);
                    }

                    // Positions of printed columns considering possible left padding
                    // ----------------------------
                    // Positions are corrected by left padding if summary text precedes the FIRST column.                        
                    colStrPositions[0] = leftPrintLinePadding.length();
                    if (colLengths.size() > 1) {
                        colEndPositions[0] = leftPrintLinePadding.length() + Math.max(colLengths.get(1), maxHeaderWidths[0])
                                + colSepSpaces.length();
                    } else {
                        colEndPositions[0] = leftPrintLinePadding.length() + maxHeaderWidths[0]
                                + colSepSpaces.length();
                    }
                    // Create array of next start and end positions considering maximum column widths
                    for (int col = 1; col < colLengths.size(); col++) {
                        int printPrevColWidth = Math.max(colLengths.get(col - 1),
                                maxHeaderWidths[col - 1]);
                        int printCurrColWidth = Math.max(colLengths.get(col), maxHeaderWidths[col]);
                        colStrPositions[col] = colStrPositions[col - 1] + printPrevColWidth
                                + colSepSpaces.length();
                        colEndPositions[col] = colEndPositions[col - 1] + printCurrColWidth
                                + colSepSpaces.length();
                    }

// ????
                    /*
                    System.out.print("colStrPositions  :  ");
                    for (int idx = 0; idx < colStrPositions.length; idx++) {
                        System.out.print(colStrPositions[idx] + ", ");
                        System.out.print(colEndPositions[idx] + ",   ");
                    }
                    System.out.println();
                     */
                    // Blank print lines for prefix texts and resulting summary values
                    // -----------------
                    printLineSUM.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    printLineAVG.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    printLineMAX.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    printLineMIN.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    printLineCOUNT.append(getSpaces(colEndPositions[colLengths.size() - 1]));

                    // Definitions --;D = patterns for editing decimal columns
                    // ----------------
                    if (!patternArrayList.isEmpty()) {
                        for (int idx = 0; idx < patternArrayList.size(); idx++) {
                            patternValues = new String[2];
                            for (int i = 0; i < patternValues.length; i++) {
                                // Copy nonempty values
                                if (i < patternArrayList.get(idx).length) {
                                    patternValues[i] = patternArrayList.get(idx)[i];
                                }
                                // Set empty string for non-existing values
                                if (i >= patternArrayList.get(idx).length) {
                                    patternValues[i] = "";
                                }
                            }
                            // Add new values back to the array list
                            patternArrayList.set(idx, patternValues);
                        }
                    }

                    // Initial column values for comparison (--;T definition)
                    // ------------------------------------
                    ArrayList<String> levelColNames = new ArrayList<>();
                    ArrayList<String> levelColValues = new ArrayList<>();
                    // Column names begin at index 4
                    // (after values "before", "after", "null mark", "column spaces")
                    if (!totalArrayList.isEmpty() && totalValues.length > 4) {
                        for (int in = 4; in < totalValues.length; in++) {
                            levelColNames.add(totalValues[in].toUpperCase());
                            levelColValues.add("");
                        }
                    }

                    // ----------------------------------------
                    // Process REMAINING rows of the result set
                    // ----------------------------------------
                    do {
                        // Check if any column value is NULL
                        for (int col = 1; col <= colNames.size(); col++) {
                            if (rs.getObject(col) == null) {
                                isDataNull = true;
                            }
                        }

                        // Prepend data buffer with possible padding 
                        // which is non-empty only if the FIRST column is summarized
                        dataBuf.append(leftPrintLinePadding);

                        // If any column value is null and no level break processing is specified
                        // - insert a number of empty lines BEFORE the line
                        if (isDataNull && levelArrayList.isEmpty()) {
                            for (int idx = 0; idx < spaceB; idx++) {
                                dataBuf.append("\n");
                            }
                        }

                        // Process current row
                        // -------------------
                        for (int idx = 0; idx < colNames.size(); idx++) {
                            colData = editCurrentRowColumnValue(idx, colTypes, colNames, colIndexes);
                            // System.out.println("DETAIL CONTINUE colData: " + colData);

                            // Adjust column to the right or left
                            columnData = adjustColumn(idx, colData);

                            // Evaluate level change of a "group level column"
                            // ---------------------
                            // whose name was specified in the --;T definition.
                            // Blank out all repeating column values, print only the
                            // first value that changed against "old column value".
                            if (!totalArrayList.isEmpty()) {
                                for (int i = 0; i < levelColNames.size(); i++) {
                                    // The total level column is being processed
                                    if (levelColNames.get(i).equals(colNames.get(idx))) {
                                        // Column value differs from the "old column
                                        // value"
                                        if (!columnData.equals(
                                                levelColValues.get(levelColNames.indexOf(colNames.get(idx))))) {
                                            // Do not change the column value and
                                            // save the column value for later comparison
                                            levelColValues.set(levelColNames.indexOf(colNames.get(idx)),
                                                    columnData);
                                        } // Column value equals to the "old column value"
                                        else {
                                            // Set empty string as a column value
                                            columnData = getSpaces(maxHeaderWidths[idx]);
                                        }
                                    }
                                }
                            }
                            dataBuf.append(columnData).append(colSepSpaces);
                        } // End of current row processing

                        // If any column value is null and no level break processing is specified 
                        // - insert a number of empty lines AFTER the line
                        if (isDataNull && levelArrayList.isEmpty()) {
                            for (int idx = 0; idx < spaceA; idx++) {
                                dataBuf.append("\n");
                            }
                        }
                        // Set off the null data flag for detail rows
                        isDataNull = false;

                        // Process definitios --;L and --;S
                        // --------------------------------
                        // Get summary column indexes and types
                        // Only if both level and summary parameters are defined.
                        if (!levelArrayList.isEmpty() && !summaryArrayList.isEmpty()) {
                            // Get indexes of summary columns for control level processing
                            summaryColIndexes = new ArrayList<>();
                            summaryColTypes = new ArrayList<>();
                            for (int summ = 0; summ < summaryColNames.size(); summ++) {
                                for (int col = 0; col < colNames.size(); col++) {
                                    if (summaryColNames.get(summ).equals(colNames.get(col))) {
                                        sumCol = col;
                                        summaryColIndexes.add(sumCol);
                                        summaryColTypes.add(colTypes.get(col));
                                    }
                                }
                            }

                            // Process control level breaks (caused by changed value of the control column)
                            // Definitios --;L and --;S
                            // ----------------------------------------------------------------------------
                            processLevelBreaks();

                            // Check new page flags
                            // --------------------
                            // Different lines are flagged for different control break levels
                            for (int idx = 0; idx < newPage.length; idx++) {
                                if (newPage[idx]) {
                                    // Unicode binary 1 is used as an invisible flag for skip to new line.
                                    // The new page flag is appended at the end of the data buffer
                                    // for the printing program Q_PrintOneFile to check and interpret it.
                                    dataBuf.append('\u0001');
                                    // Reset the new page flag for next test in processLevelBreaks() method
                                    newPage[idx] = false;
                                }
                            }
                        }

                        // Append new line character at the end of data buffer
                        dataBuf.append("\n");

                        // Write data buffer to the text area
                        resultTextArea.append(dataBuf.toString());

                        // Clear print line
                        dataBuf.setLength(0);

                    } while (rs.next()); // Repeat the loop of result set processing 

                    // Process last control level (caused by end of result set)
                    // --------------------------------------------------------
                    // Only if level and summary parameters are defined.
                    if (!levelArrayList.isEmpty() && !summaryArrayList.isEmpty()) {
                        processLastLevelBreak();
                    }
                }

                // Close the result set
                rs.close();

                // Write query result into text files
                ArrayList<String> lines = new ArrayList<>();
                String[] lineArr = resultTextArea.getText().split("\n");
                for (int idx = 0; idx < lineArr.length; idx++) {
                    lines.add(idx, lineArr[idx]);
                }
                Files.write(workfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                Files.write(printfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

                // Display the query result
                // ------------------------
                new Q_PrintOneFile(scriptName, resultTextArea, nbrHdrLines, headerArrayList, printArrayList, columnHeaders);

                retCode[1] = "";
                return retCode; // OK

            } // End of query (SELECT statement) evaluation

        } catch (IllegalArgumentException iae) {
            // Catching error in decimal number
            retCode[1] = numParError + iae.getClass() + ", " + iae.toString() + "\n\n";
            retCode[0] += "PARAMETER_ERROR";
            // System.out.println("IllegalArgument retCode[1]: " + retCode[1]);
            iae.printStackTrace();
            reportError(resultTextArea, retCode[1]);
            return retCode; // error

        } // Catching SQL errors
        catch (SQLException sqle) {
            // Error in SQL statement
            retCode[1] = sqlError + sqle.getSQLState() + "  " + sqle.toString() + "\n\n";
            retCode[0] += "SQL_ERROR";
            // System.out.println("SQL error retCode[1]: " + retCode[1]);
            sqle.printStackTrace();
            reportError(resultTextArea, retCode[1]);
            return retCode; // error

            // Catching all other errors
        } catch (Exception e) {
            retCode[1] = otherError + " -  " + e.toString();
            retCode[0] += "OTHER_ERROR";
            // System.out.println("Other error runScript(): " + retCode[1]);
            e.printStackTrace();
            reportError(resultTextArea, retCode[1]);
            return retCode; // error
        }
        // retCode[1] = "";
        return retCode; // OK
    }

    /**
     * Common error message reported from "catch" blocks; display and save the statement result into files.
     *
     * @param resultTextArea
     * @param msg
     */
    protected void reportError(JTextArea resultTextArea, String msg) {
        // Text of the SQL statement and the error message is displayed
        resultTextArea.append(statement);
        resultTextArea.append(msg);
        // Display and print the query result if the message is non-empty
        if (!msg.equals("")) {
            new Q_PrintOneFile(scriptName, resultTextArea, nbrHdrLines, headerArrayList, printArrayList, columnHeaders);

            // Write the result of the statement into text files
            ArrayList<String> lines = new ArrayList<>();
            String[] lineArr = resultTextArea.getText().split("\n");
            for (int idx = 0; idx < lineArr.length; idx++) {
                lines.add(idx, lineArr[idx]);
            }
            try {
                Files.write(workfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                Files.write(printfilesTxt, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     *
     * @throws Exception
     */
    protected void buildColumnHeaders() throws Exception {

        // Initialize empty header arrays. Maximum width is 0 initially
        maxHeaderWidths = new Integer[colNames.size()];
        for (int i = 0; i < colNames.size(); i++) {
            maxHeaderWidths[i] = 0;
        }

        if (!headerArrayListSaved.isEmpty()) {
            headerArrayList.clear();

            // When user headers are defined, one or more of definition
            // comment lines --;H is specified.
            // Fill header values of the array list from header comment lines
            for (int hdr = 0; hdr < headerArrayListSaved.size(); hdr++) {
                // Create an empty array of header values
                headerValues = new String[headerArrayListSaved.size()][colNames.size()];
                // Initialize array elements - header values for columns           
                for (int col = 0; col < colNames.size(); col++) {
                    headerValues[hdr][col] = "";
                }
                // Copy headers of non-omitted columns
                for (int col = 0; col < colNames.size(); col++) {
                    // Copy nonempty values
                    if (col < headerArrayListSaved.get(hdr).length) {
                        if (!headerArrayListSaved.get(hdr)[col].isEmpty()) {
                            if (col < headerArrayListSaved.get(hdr).length - omittedColNames.size()) {
                                headerValues[hdr][col] = headerArrayListSaved.get(hdr)[colIndexes.get(col) - 1];
                            } else {
                                headerValues[hdr][col] = "";
                            }
                            //System.out.println("headerValues[" + hdr + "][" + col + "]: " + headerValues[hdr][col]);
                        }
                    }
                }

                // Put new (perhaps fewer) header values back into the header array
//                headerArrayList.set(hdr, headerValues[hdr]);
                headerArrayList.add(headerValues[hdr]);

                // Get maximum of each header value length and
                // previous maximum column width
                // and make it new maximum column width        
                for (int col = 0; col < colNames.size(); col++) {
                    if (headerValues[hdr][col].length() > maxHeaderWidths[col]) {
                        // Empty header values now have length 0
                        maxHeaderWidths[col] = headerValues[hdr][col].length();
                    }
                }
            }

            // Definitions --;H = user defined headers
            // ----------------
            nbrHdrLines = 0;
            for (int hdr = 0; hdr < headerArrayList.size(); hdr++) {
                // Get current header array from the newly accomodated array list
                headerValues[hdr] = headerArrayList.get(hdr);
                // Build the header line
                for (int i = 0; i < colNames.size(); i++) {
                    // Set column width as the maximum of header size and
                    // data column size
                    maxHeaderWidths[i] = Math.max(colLengths.get(i), maxHeaderWidths[i]);
                    // Headers for numeric columns are right adjusted
                    if (((colScales.get(i) > 0 || colTypes.get(i) == Types.NUMERIC
                            || colTypes.get(i) == Types.DECIMAL
                            || colTypes.get(i) == Types.INTEGER || colTypes.get(i) == Types.BIGINT
                            || colTypes.get(i) == Types.SMALLINT)
                            && colTypes.get(i) != Types.TIMESTAMP)) {
                        headerValues[hdr][i] = rightAdjust(headerValues[hdr][i], maxHeaderWidths[i]);
                    } // Headers for columns of other types are left adjusted
                    else {
                        headerValues[hdr][i] = leftAdjust(headerValues[hdr][i], maxHeaderWidths[i]);
                    }
                }
                // Increment number of header lines
                nbrHdrLines += 1;
            }
        } else {
            // Standard (automatic) headers
            // ----------------------------
            // No user headers are defined (header array list is empty).
            stdHdrValues = new String[rsmd.getColumnCount()];
            for (int col = 0; col < colNames.size(); col++) {
                stdHdrValues[col] = colNames.get(col);
                maxHeaderWidths[col] = Math.max(colLengths.get(col), colNames.get(col).length());
            }
            // Build the header line from header columns
            for (int col = 0; col < colNames.size(); col++) {
                // Numeric columns are right adjusted
                if (((colScales.get(col) > 0 || colTypes.get(col) == Types.NUMERIC
                        || colTypes.get(col) == Types.DECIMAL || colTypes.get(col) == Types.INTEGER
                        || colTypes.get(col) == Types.BIGINT || colTypes.get(col) == Types.SMALLINT)
                        && colTypes.get(col) != Types.TIMESTAMP)) {
                    stdHdrValues[col] = rightAdjust(stdHdrValues[col], maxHeaderWidths[col]);
                } // Columns of other data types are left adjusted (except time stamp)
                else {
                    stdHdrValues[col] = leftAdjust(stdHdrValues[col], maxHeaderWidths[col]);
                }
            }
            nbrHdrLines = 1; // Only 1 standard header line
        }

        // Restore the header array list with original entries from the saved list
        // for the next call from Q_PromptParameters class
        headerArrayList.clear();
        headerArrayList.addAll(headerArrayListSaved);
    }

    /**
     *
     * @param index
     * @param colTypes
     * @param colNames
     * @param colIndexes
     * @return
     * @throws SQLException
     */
    protected String editCurrentRowColumnValue(int index, ArrayList<Integer> colTypes, ArrayList<String> colNames, ArrayList<Integer> colIndexes) throws SQLException {
        // System.out.println("EDIT index: " + index);
        // System.out.println("EDIT colNames: " + colNames);
        // System.out.println("EDIT colTypes: " + colTypes);
        // System.out.println("EDIT colIndexes: " + colIndexes);

        // NULL column value
        if (rs.getObject(colIndexes.get(index)) == null) {
            colData = nullPrintMark;
            // System.out.println("NULL colData: " + colData);

        } // NON-NULL column value
        else if (colTypes.get(index) == Types.NUMERIC
                || colTypes.get(index) == Types.DECIMAL) {
            // System.out.println("EDIT rs.getBigDecimal(colIndexes.get(" + index + ")): " + rs.getBigDecimal(colIndexes.get(index)));
            // Decimal numbers are printed with or without the
            // pattern and are localized
            // Edit numeric and decimal numbers
            BigDecimal rs_DecimalValue = rs.getBigDecimal(colIndexes.get(index));
            colData = editNumericDecimal(rs_DecimalValue, colNames.get(index));
            // Edit binary values (in hexa characters)
        } else if (colTypes.get(index) == Types.BINARY
                || colTypes.get(index) == Types.VARBINARY) {
            // If column type is BINARY or VARBINARY translate
            // bytes in hexadecimal characters
            int length = rs.getBytes(colIndexes.get(index)).length;
            String hexString = "";
            for (int jdx = 0; jdx < length; jdx++) {
                hexString += byteToHex(rs.getBytes(colIndexes.get(index))[jdx]);
            }
            colData = hexString;
            // Any other types are automatically transformed as Object
        } else if (rs.getObject(colIndexes.get(index)) == null) {
            colData = nullPrintMark;
        } else {
            // System.out.println("EDIT rs.getObject(colIndexes.get(" + index + ")): " + rs.getObject(colIndexes.get(index)));
            // System.out.println("EDIT colIndexes.get(" + index + "): " + colIndexes.get(index));
            // System.out.println("EDIT colNames.get(" + index + "): " + colNames.get(index));
            // System.out.println("EDIT colTypes.get(" + index + "): " + colTypes.get(index));

            // Method getObject() transforms SQL data to Java Object type
            colData = rs.getObject(colIndexes.get(index)).toString();
        }
        return colData;
    }

    /**
     *
     * @throws java.sql.SQLException
     */
    protected void initializeAccumulators() throws SQLException {

        // Level summary accumulators are 5 for each level and summary column                        
        // Initialize level DECIMAL accumulators
        // -------------------------------------
        decAccumulators = new BigDecimal[levelArrayList.size()][summaryArrayList.size()][5];
        for (int level = 0; level < levelArrayList.size(); level++) {
            for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                for (int acc = 0; acc < 5; acc++) {
                    // Each break level has summary columns with 5 decAccumulators 
                    // (SUM, AVG, MAX, MIN, COUNT)
                    decAccumulators[level][sumNam][acc] = DECIMAL_ZERO;
                }
                decAccumulators[level][sumNam][MAX_INDEX] = getResetDecimalValue(summaryColNames.get(sumNam));
                decAccumulators[level][sumNam][MIN_INDEX] = getResetDecimalValue(summaryColNames.get(sumNam));
            }
        }
        // Initialize level BINARY accumulators
        // ------------------------------------
        binAccumulators = new Long[levelArrayList.size()][summaryArrayList.size()][5];
        // Level binary accumulators are all initialized by zero
        for (int level = 0; level < levelArrayList.size(); level++) {
            for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                for (int acc = 0; acc < 5; acc++) {
                    // Each break level has summary columns with 5 binAccumulators 
                    // (SUM, AVG, MAX, MIN, COUNT)
                    binAccumulators[level][sumNam][acc] = BINARY_ZERO;
                }
                binAccumulators[level][sumNam][MAX_INDEX] = getResetBinaryValue(summaryColNames.get(sumNam));
                binAccumulators[level][sumNam][MIN_INDEX] = getResetBinaryValue(summaryColNames.get(sumNam));
            }
        }
        // Initialize level DATE accumulators
        // ----------------------------------
        datAccumulators = new Date[levelArrayList.size()][summaryArrayList.size()][5];
        // Level date accumulators are all initialized by lowest date
        for (int level = 0; level < levelArrayList.size(); level++) {
            for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                for (int acc = 0; acc < 5; acc++) {
                    // Each break level has summary columns with 5 datAccumulators 
                    // (-, -, MAX, MIN, COUNT)
                    datAccumulators[level][sumNam][acc] = getResetDateValue(summaryColNames.get(sumNam));
                }
            }
        }
        // Initialize level TIME accumulators
        // ----------------------------------
        timAccumulators = new Time[levelArrayList.size()][summaryArrayList.size()][5];
        // Level time accumulators are all initialized by lowest date
        for (int level = 0; level < levelArrayList.size(); level++) {
            for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                for (int acc = 0; acc < 5; acc++) {
                    // Each break level has summary columns with 5 timAccumulators 
                    // (-, -, MAX, MIN, COUNT)
                    timAccumulators[level][sumNam][acc] = getResetTimeValue(summaryColNames.get(sumNam));
                }
            }
        }
        // Initialize level TIMESTAMP accumulators
        // ---------------------------------------
        timstAccumulators = new Timestamp[levelArrayList.size()][summaryArrayList.size()][5];
        // Level timestamp accumulators are all initialized by lowest date
        for (int level = 0; level < levelArrayList.size(); level++) {
            for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                for (int acc = 0; acc < 5; acc++) {
                    // Each break level has summary columns with 5 timstAccumulators 
                    // (-, -, MAX, MIN, COUNT)
                    timstAccumulators[level][sumNam][acc] = getResetTimestValue(summaryColNames.get(sumNam));
                }
            }
        }
        // Initialize level CHAR, VARCHAR (String) accumulators
        // ----------------------------------------------------
        strAccumulators = new String[levelArrayList.size()][summaryArrayList.size()][5];
        // Level char accumulators are all initialized by empty strings
        for (int level = 0; level < levelArrayList.size(); level++) {
            for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
                for (int acc = 0; acc < 5; acc++) {
                    // Each break level has summary columns with 5 strAccumulators 
                    // (-, -, MAX, MIN, COUNT)
                    strAccumulators[level][sumNam][acc] = CHAR_MIN;
                }
                strAccumulators[level][sumNam][MAX_INDEX] = getResetCharValue(summaryColNames.get(sumNam));
                strAccumulators[level][sumNam][MIN_INDEX] = getResetCharValue(summaryColNames.get(sumNam));
            }
        }
        // Total summary accumulators are 5 for each summary column                        
        // Initialize total DECIMAL accumulators
        // -------------------------------------      
        decTotalAccumulators = new BigDecimal[summaryArrayList.size()][5];
        for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
            for (int acc = 0; acc < 5; acc++) {
                // Each summary column has 5 accumulators 
                // ccumulators (SUM, AVG, MAX, MIN, COUNT)
                decTotalAccumulators[sumNam][acc] = DECIMAL_ZERO;
            }
            decTotalAccumulators[sumNam][MAX_INDEX] = getResetDecimalValue(summaryColNames.get(sumNam));
            decTotalAccumulators[sumNam][MIN_INDEX] = getResetDecimalValue(summaryColNames.get(sumNam));

        }
        // Initialize total BINARY accumulators
        // ------------------------------------
        binTotalAccumulators = new Long[summaryArrayList.size()][5];
        for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
            for (int acc = 0; acc < 5; acc++) {
                // Each break level has summary columns with 5 accumulators
                // (SUM, AVG, MAX, MIN, COUNT)
                binTotalAccumulators[sumNam][acc] = BINARY_ZERO;
            }
            binTotalAccumulators[sumNam][MAX_INDEX] = getResetBinaryValue(summaryColNames.get(sumNam));
            binTotalAccumulators[sumNam][MIN_INDEX] = getResetBinaryValue(summaryColNames.get(sumNam));
        }
        // Initialize total DATE accumulators
        // ----------------------------------
        datTotalAccumulators = new Date[summaryArrayList.size()][5];
        for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
            for (int acc = 0; acc < 5; acc++) {
                // Each break level has summary columns with 5 accumulators
                // (-, -, MAX, MIN, COUNT)
                datTotalAccumulators[sumNam][acc] = getResetDateValue(summaryColNames.get(sumNam));
            }
        }
        // Initialize total TIME accumulators
        // ----------------------------------
        timTotalAccumulators = new Time[summaryArrayList.size()][5];
        for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
            for (int acc = 0; acc < 5; acc++) {
                // Each break level has summary columns with 5 accumulators
                // (-, -, MAX, MIN, COUNT)
                timTotalAccumulators[sumNam][acc] = getResetTimeValue(summaryColNames.get(sumNam));
            }
        }
        // Initialize total TIMESTAMP accumulators
        // ---------------------------------------
        timstTotalAccumulators = new Timestamp[summaryArrayList.size()][5];
        for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
            for (int acc = 0; acc < 5; acc++) {
                // Each break level has summary columns with 5 accumulators
                // (-, -, MAX, MIN, COUNT)
                timstTotalAccumulators[sumNam][acc] = getResetTimestValue(summaryColNames.get(sumNam));
            }
        }
        // Initialize total CHAR, VARCHAR (String) accumulators
        // ----------------------------------------------------
        strTotalAccumulators = new String[summaryArrayList.size()][5];
        for (int sumNam = 0; sumNam < summaryColNames.size(); sumNam++) {
            for (int acc = 0; acc < 5; acc++) {
                // Each break level has summary columns with 5 accumulators
                // (-, -, MAX, MIN, COUNT)
                strTotalAccumulators[sumNam][acc] = CHAR_MIN;
            }
            strTotalAccumulators[sumNam][MAX_INDEX] = getResetCharValue(summaryColNames.get(sumNam));
            strTotalAccumulators[sumNam][MIN_INDEX] = getResetCharValue(summaryColNames.get(sumNam));
        }
    }

    /**
     * Processing of summary levels (Ln to L1)
     *
     * @throws java.lang.Exception
     */
    protected void processLevelBreaks() throws Exception {
        try {
            // Work variables
            BigDecimal dec;
            BigDecimal dec2;
            BigDecimal decimalColValue;
            Long bin;
            Long bin2;
            Long binaryColValue;
            Date dateColValue;
            Date dat;
            Time timeColValue;
            Time tim;
            Timestamp timestampColValue;
            Timestamp timst;
            String stringColValue;
            String str;
            String levelHeader;
            sumCol = 0;
            summaryTextArea = new JTextArea();

            // Total accumulators
            // ------------------
            for (int summ = 0; summ < summaryColNames.size(); summ++) {

                // Decimal values - DECIMAL or NUMERIC
                // -----------------------------------
                if (colTypes.get(summaryColIndexes.get(summ)) == Types.DECIMAL
                        || colTypes.get(summaryColIndexes.get(summ)) == Types.NUMERIC) {

                    // Get value of current summarized column
                    decimalColValue = rs.getBigDecimal(summaryColNames.get(summ));

                    // SUM totals
                    dec = decTotalAccumulators[summ][SUM_INDEX];
                    if (decimalColValue != null && dec != null) {
                        dec = dec.add(decimalColValue);
                    }
                    decTotalAccumulators[summ][SUM_INDEX] = dec;

                    // COUNT totals
                    dec = decTotalAccumulators[summ][COUNT_INDEX];
                    if (decimalColValue != null) {
                        dec = dec.add(DECIMAL_ONE);
                    }
                    decTotalAccumulators[summ][COUNT_INDEX] = dec;

                    // AVG totals
                    dec = decTotalAccumulators[summ][SUM_INDEX];
                    dec2 = decTotalAccumulators[summ][COUNT_INDEX];
                    if (dec != null && dec2 != null) {
                        if (dec2.compareTo(DECIMAL_ZERO) > 0) {
                            dec = dec.divide(dec2, RoundingMode.HALF_EVEN);
                        }
                        decTotalAccumulators[summ][AVG_INDEX] = dec;
                    }

                    // MAX totals
                    dec = decTotalAccumulators[summ][MAX_INDEX];
                    if (decimalColValue != null && dec != null) {
                        if (decimalColValue.compareTo(dec) > 0) {
                            dec = decimalColValue;
                        }
                        decTotalAccumulators[summ][MAX_INDEX] = dec;
                    }
                    if (decimalColValue != null && dec == null) {
                        decTotalAccumulators[summ][MAX_INDEX] = decimalColValue;
                    }
                    // MIN totals
                    dec = decTotalAccumulators[summ][MIN_INDEX];
                    if (decimalColValue != null && dec != null) {
                        if (decimalColValue.compareTo(dec) <= 0) {
                            dec = decimalColValue;
                        }
                        decTotalAccumulators[summ][MIN_INDEX] = dec;
                    }
                    if (decimalColValue != null && dec == null) {
                        decTotalAccumulators[summ][MIN_INDEX] = decimalColValue;
                    }
                } // End of total for DECIMAL and NUMERIC

                // Binary values - INTEGER, SMALLINT or BIGINT 
                // -------------------------------------------
                if (colTypes.get(summaryColIndexes.get(summ)) == Types.INTEGER
                        || colTypes.get(summaryColIndexes.get(summ)) == Types.SMALLINT
                        || colTypes.get(summaryColIndexes.get(summ)) == Types.BIGINT) {

                    // Get value of current aggregate column
                    // Object can only be tested for NULL! Long cannot.                    
                    Object objectColValue = rs.getObject(summaryColNames.get(summ));
                    binaryColValue = rs.getLong(summaryColNames.get(summ));

                    // SUM totals
                    bin = binTotalAccumulators[summ][SUM_INDEX];
                    if (objectColValue != null && bin != null) {
                        if (binaryColValue != 0) {
                            bin += binaryColValue;
                        }
                    }
                    binTotalAccumulators[summ][SUM_INDEX] = bin;

                    // COUNT totals
                    bin = binTotalAccumulators[summ][COUNT_INDEX];
                    if (objectColValue != null && bin != null) {
                        bin += 1;
                        binTotalAccumulators[summ][COUNT_INDEX] = bin;
                    }

                    // AVG totals
                    bin = binTotalAccumulators[summ][SUM_INDEX];
                    bin2 = binTotalAccumulators[summ][COUNT_INDEX];
                    if (bin != null && bin2 != null) {
                        if (bin2 != 0) {
                            bin /= bin2;
                        }
                    }
                    binTotalAccumulators[summ][AVG_INDEX] = bin;

                    // MAX totals
                    bin = binTotalAccumulators[summ][MAX_INDEX];
                    if (objectColValue != null && bin != null) {
                        if (binaryColValue > bin) {
                            bin = binaryColValue;
                            binTotalAccumulators[summ][MAX_INDEX] = bin;
                        }
                    }

                    // MIN totals
                    bin = binTotalAccumulators[summ][MIN_INDEX];
                    if (objectColValue != null && bin != null) {
                        if (binaryColValue > 0) {
                            if (binaryColValue <= bin) {
                                bin = binaryColValue;
                                binTotalAccumulators[summ][MIN_INDEX] = bin;
                            }
                        }
                    }
                } // End of total for INTEGER, SMALLINT or BIGINT

                // Date values - DATE
                // ------------------
                // (Date SUM and AVG are not summarized - they have no sense for DATE)                
                if (colTypes.get(summaryColIndexes.get(summ)) == Types.DATE) {

                    // Get value of current aggregate column
                    dateColValue = rs.getDate(summaryColNames.get(summ));

                    // COUNT of values 
                    dec = decTotalAccumulators[summ][COUNT_INDEX];
                    if (dateColValue != null) {
                        // Add 1 to COUNT accumulator
                        dec = dec.add(DECIMAL_ONE);
                    }
                    decTotalAccumulators[summ][COUNT_INDEX] = dec;

                    // MAX of values
                    dat = datTotalAccumulators[summ][MAX_INDEX];
                    if (dateColValue != null && dat != null) {
                        if (dateColValue.compareTo(dat) > 0) {
                            dat = dateColValue;
                        }
                        datTotalAccumulators[summ][MAX_INDEX] = dat;
                    }
                    if (dat == null) {
                        datTotalAccumulators[summ][MAX_INDEX] = dateColValue;
                    }
                    if (dateColValue == null) {
                        datTotalAccumulators[summ][MAX_INDEX] = dat;
                    }

                    // MIN of values
                    dat = datTotalAccumulators[summ][MIN_INDEX];
                    if (dateColValue != null && dat != null) {
                        if (dateColValue.compareTo(dat) <= 0) {
                            dat = dateColValue;
                        }
                        datTotalAccumulators[summ][MIN_INDEX] = dat;
                    }
                    if (dateColValue != null && dat == null) {
                        datTotalAccumulators[summ][MIN_INDEX] = dateColValue;
                    }
                } // End of total for DATE      

                // Time values - TIME
                // ------------------
                // (Time SUM and AVG are not summarized - they have no sense for TIME)                
                if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIME) {

                    // Get value of current aggregate column
                    timeColValue = rs.getTime(summaryColNames.get(summ));

                    // COUNT of values 
                    dec = decTotalAccumulators[summ][COUNT_INDEX];
                    if (timeColValue != null) {
                        // Add 1 to COUNT accumulator
                        dec = dec.add(DECIMAL_ONE);
                    }
                    decTotalAccumulators[summ][COUNT_INDEX] = dec;

                    // MAX of values
                    tim = timTotalAccumulators[summ][MAX_INDEX];
                    if (timeColValue != null && tim != null) {
                        if (timeColValue.compareTo(tim) > 0) {
                            tim = timeColValue;
                        }
                        timTotalAccumulators[summ][MAX_INDEX] = tim;
                    }
                    if (tim == null) {
                        timTotalAccumulators[summ][MAX_INDEX] = timeColValue;
                    }
                    if (timeColValue == null) {
                        timTotalAccumulators[summ][MAX_INDEX] = tim;
                    }

                    // MIN of values
                    tim = timTotalAccumulators[summ][MIN_INDEX];
                    if (timeColValue != null && tim != null) {
                        if (timeColValue.compareTo(tim) <= 0) {
                            tim = timeColValue;
                        }
                        timTotalAccumulators[summ][MIN_INDEX] = tim;
                    }
                    if (timeColValue != null && tim == null) {
                        timTotalAccumulators[summ][MIN_INDEX] = timeColValue;
                    }
                } // End of total for TIME      

                // Timestamp values - TIMESTAMP
                // ----------------------------
                // (Timestamp SUM and AVG are not summarized - they have no sense for TIMESTAMP)                
                if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIMESTAMP) {

                    // Get value of current aggregate column
                    timestampColValue = rs.getTimestamp(summaryColNames.get(summ));

                    // COUNT of values 
                    dec = decTotalAccumulators[summ][COUNT_INDEX];
                    if (timestampColValue != null) {
                        // Add 1 to COUNT accumulator
                        dec = dec.add(DECIMAL_ONE);
                    }
                    decTotalAccumulators[summ][COUNT_INDEX] = dec;

                    // MAX of values
                    timst = timstTotalAccumulators[summ][MAX_INDEX];
                    if (timestampColValue != null && timst != null) {
                        if (timestampColValue.compareTo(timst) > 0) {
                            timst = timestampColValue;
                        }
                        timstTotalAccumulators[summ][MAX_INDEX] = timst;
                    }
                    if (timst == null) {
                        timstTotalAccumulators[summ][MAX_INDEX] = timestampColValue;
                    }
                    if (timestampColValue == null) {
                        timstTotalAccumulators[summ][MAX_INDEX] = timst;
                    }

                    // MIN of values
                    timst = timstTotalAccumulators[summ][MIN_INDEX];
                    if (timestampColValue != null && timst != null) {
                        if (timestampColValue.compareTo(timst) <= 0) {
                            timst = timestampColValue;
                        }
                        timstTotalAccumulators[summ][MIN_INDEX] = timst;
                    }
                    if (timestampColValue != null && timst == null) {
                        timstTotalAccumulators[summ][MIN_INDEX] = timestampColValue;
                    }
                } // End of total for TIMESTAMP      

                // Character values - CHAR, VARCHAR, LONGVARCHAR
                // ---------------------------------------------
                // (Character SUM and AVG are not summarized - they have no sense for CHAR, VARCHAR, LONGVARCHAR)                
                if (colTypes.get(summaryColIndexes.get(summ)) == Types.CHAR
                        || colTypes.get(summaryColIndexes.get(summ)) == Types.VARCHAR
                        || colTypes.get(summaryColIndexes.get(summ)) == Types.LONGVARCHAR) {

                    // Get value of current aggregate column
                    stringColValue = rs.getString(summaryColNames.get(summ));

                    // COUNT of values 
                    dec = decTotalAccumulators[summ][COUNT_INDEX];
                    if (stringColValue != null) {
                        // Add 1 to COUNT accumulator (decimal)
                        dec = dec.add(DECIMAL_ONE);
                    }
                    decTotalAccumulators[summ][COUNT_INDEX] = dec;

                    // MAX of values
                    str = strTotalAccumulators[summ][MAX_INDEX];
                    if (stringColValue != null && str != null) {
                        if (stringColValue.compareTo(str) > 0) {
                            str = stringColValue;
                        }
                        strTotalAccumulators[summ][MAX_INDEX] = str;
                    }
                    if (str == null) {
                        strTotalAccumulators[summ][MAX_INDEX] = stringColValue;
                    }
                    if (stringColValue == null) {
                        strTotalAccumulators[summ][MAX_INDEX] = str;
                    }

                    // MIN of values
                    str = strTotalAccumulators[summ][MIN_INDEX];
                    if (stringColValue != null && str != null) {
                        if (stringColValue.compareTo(str) <= 0) {
                            str = stringColValue;
                        }
                        strTotalAccumulators[summ][MIN_INDEX] = str;
                    }
                    if (str == null) {
                        strTotalAccumulators[summ][MIN_INDEX] = stringColValue;
                    }
                    if (stringColValue == null) {
                        strTotalAccumulators[summ][MIN_INDEX] = str;
                    }

                } // End of total for CHAR, VARCHAR, LONGVARCHAR                    

            }

            // -----------------------------------------------
            // Break time - process all level breaks BACKWARDS
            // -----------------------------------------------
            for (level = levelBreakNames.size() - 1; level >= 0; level--) {

                // Level break time before Detail time (accumulating values)
                // ----------------
                // Current column value is now being compared to the control level value
                // prepared beforehand
                Object compareValue = rs.getObject(levelBreakNames.get(level));
                // If control level value and compare value are both null, replace them by the null print mark 
                if (compareValue == null) {
                    compareValue = nullPrintMark;
                    levelBreakValues.set(level, nullPrintMark);
                }
                // If a level break occurs (the control level value  differs from the compare value)
                // process summary of the control level group
                if (!levelBreakValues.get(level).toString().equals(compareValue.toString())) {
                    // System.out.println(levelBreakValues.get(level) + " =? " +  compareValue);

                    // If the NP (new page) is specified in the --;L definition line
                    // remember it in corresponding level flag so that it can be tested in detail time
                    // after the break processing. Different levels have different flags.
                    if (levelArrayList.get(level + 1)[3].toUpperCase().equals("NP")) {
                        newPage[level] = true;
                    }
                    // System.out.println("levelArrayList.get("+(level)+")[3].toUpperCase(): "+levelArrayList.get(level+1)[3].toUpperCase());   
                    // System.out.println("newPage["+level+"]: "+newPage[level]);                    

                    // Insert a number of empty lines BEFORE summary lines
                    if (level == levelBreakNames.size() - 1) {
                        for (int idx = 0; idx < spaceB; idx++) {
                            summaryTextArea.append("\n");
                        }
                    }
                    // Include LEVEL TEXT (at index 1) on separate line of
                    // result text area ending with new line
                    if (!levelArrayList.get(0)[0].equals("0")) {
                        // If L0 is not specified get text from current level
                        levelHeader = getValuesForVariables(levelArrayList.get(level)[1],
                                detColNames, detColValues);
                        summaryTextArea.append(levelHeader + "\n");
                    } else {
                        // If L0 is specified, get text from next level
                        levelHeader = getValuesForVariables(levelArrayList.get(level + 1)[1],
                                detColNames, detColValues);
                        summaryTextArea.append(levelHeader + "\n");
                    }

                    // SUM function
                    summaryForLevels(level, SUM_INDEX, leadingSumString, printLineSUM);
                    // AVG function
                    summaryForLevels(level, AVG_INDEX, leadingAvgString, printLineAVG);
                    // MAX function
                    summaryForLevels(level, MAX_INDEX, leadingMaxString, printLineMAX);
                    // MIN function
                    summaryForLevels(level, MIN_INDEX, leadingMinString, printLineMIN);
                    // COUNT function
                    summaryForLevels(level, COUNT_INDEX, leadingCountString, printLineCOUNT);

                    if (atLeastOneSUM) {
                        summaryTextArea.append(printLineSUM.toString());
                        summaryTextArea.append("\n");
                        // System.out.println("DET printLineSUM: " + printLineSUM);
                        printLineSUM = new StringBuilder();
                        printLineSUM.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    }
                    if (atLeastOneAVG) {
                        summaryTextArea.append(printLineAVG.toString());
                        summaryTextArea.append("\n");
                        // System.out.println("DET printLineAVG: " + printLineAVG);
                        printLineAVG = new StringBuilder();
                        printLineAVG.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    }
                    if (atLeastOneMAX) {
                        summaryTextArea.append(printLineMAX.toString());
                        summaryTextArea.append("\n");
                        // System.out.println("DET printLineMAX: " + printLineMAX);
                        printLineMAX = new StringBuilder();
                        printLineMAX.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    }
                    if (atLeastOneMIN) {
                        summaryTextArea.append(printLineMIN.toString());
                        summaryTextArea.append("\n");
                        // System.out.println("DET printLineMIN: " + printLineMIN);
                        printLineMIN = new StringBuilder();
                        printLineMIN.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    }
                    if (atLeastOneCOUNT) {
                        summaryTextArea.append(printLineCOUNT.toString());
                        summaryTextArea.append("\n");
                        // System.out.println("DET printLineCOUNT: " + printLineCOUNT);
                        printLineCOUNT = new StringBuilder();
                        printLineCOUNT.append(getSpaces(colEndPositions[colLengths.size() - 1]));
                    }

                    // Insert a number of enpty lines AFTER summary lines
                    for (int idx = 0; idx < spaceA; idx++) {
                        summaryTextArea.append("\n");
                    }

                    // Include summary text area into the result text area
                    // to complete the listing
                    resultTextArea.append(summaryTextArea.getText());

                    // Set current level column value as the next break comparing
                    // value
                    getNextLevelBreakValues(level);

                    // Clear summary text area for the next level
                    summaryTextArea.setText("");
                }

                // -----------
                // Detail time
                // -----------
                for (int summ = 0; summ < summaryColNames.size(); summ++) {

                    // Decimal values - DECIMAL or NUMERIC
                    // -----------------------------------
                    if (colTypes.get(summaryColIndexes.get(summ)) == Types.DECIMAL
                            || colTypes.get(summaryColIndexes.get(summ)) == Types.NUMERIC) {

                        // Get current detail value of the summary column
                        decimalColValue = rs.getBigDecimal(summaryColNames.get(summ));
                        // System.out.println("LEVEL DET decimalColValue: " + decimalColValue);

                        // SUM of values
                        dec = decAccumulators[level][summ][SUM_INDEX];
                        if (decimalColValue != null && dec != null) {
                            // Add value of summary column to SUM accumulator
                            dec = dec.add(decimalColValue);
                            decAccumulators[level][summ][SUM_INDEX] = dec;
                        }
                        // COUNT of values - always decimal
                        dec = decAccumulators[level][summ][COUNT_INDEX];
                        if (decimalColValue != null) {
                            // Add 1 to COUNT accumulator
                            dec = dec.add(DECIMAL_ONE);
                        }
                        decAccumulators[level][summ][COUNT_INDEX] = dec;

                        // AVERAGE of values
                        // Get sum accumulator value
                        dec = decAccumulators[level][summ][SUM_INDEX];
                        // Get count accumulator value
                        dec2 = decAccumulators[level][summ][COUNT_INDEX];
                        // Divide sum by count - divisor must be greater than zero
                        // System.out.println("Sum, Count: " + dec + ", " + dec2);
                        if (dec != null && dec2 != null) {
                            if (dec2.compareTo(DECIMAL_ZERO) > 0) {
                                // Divide sum by count
                                dec = dec.divide(dec2, RoundingMode.HALF_EVEN);
                            }
                        }
                        // System.out.println("Average : " + dec);
                        decAccumulators[level][summ][AVG_INDEX] = dec;

                        // MAX of values
                        dec = decAccumulators[level][summ][MAX_INDEX];
                        if (decimalColValue != null && dec != null) {
                            if (decimalColValue.compareTo(dec) > 0) {
                                dec = decimalColValue;
                            }
                            decAccumulators[level][summ][MAX_INDEX] = dec;
                        }
                        if (decimalColValue != null && dec == null) {
                            decAccumulators[level][summ][MAX_INDEX] = decimalColValue;
                        }

                        // MIN of values
                        dec = decAccumulators[level][summ][MIN_INDEX];
                        if (decimalColValue != null && dec != null) {
                            if (decimalColValue.compareTo(dec) <= 0) {
                                dec = decimalColValue;
                            }
                            decAccumulators[level][summ][MIN_INDEX] = dec;
                        }
                        if (decimalColValue != null && dec == null) {
                            decAccumulators[level][summ][MIN_INDEX] = decimalColValue;
                        }
                    } // End of detail for DECIMAL 

                    // Binary values - INTEGER, SMALLINT or BIGINT 
                    // -------------------------------------------
                    if (colTypes.get(summaryColIndexes.get(summ)) == Types.INTEGER
                            || colTypes.get(summaryColIndexes.get(summ)) == Types.SMALLINT
                            || colTypes.get(summaryColIndexes.get(summ)) == Types.BIGINT) {

                        // Get summary column current value
                        Object objectColValue = rs.getObject(summaryColNames.get(summ));
                        binaryColValue = rs.getLong(summaryColNames.get(summ));

                        // SUM of values
                        bin = binAccumulators[level][summ][SUM_INDEX];
                        if (objectColValue != null && bin != null) {
                            if (binaryColValue != 0) {
                                // Add value of summary column to SUM accumulator
                                bin += binaryColValue;
                                binAccumulators[level][summ][SUM_INDEX] = bin;
                            }
                        }
                        if (objectColValue != null && bin == null) {
                            binAccumulators[level][summ][SUM_INDEX] = binaryColValue;
                        }

                        // COUNT of values - always decimal
                        bin = binAccumulators[level][summ][COUNT_INDEX];
                        if (binaryColValue != 0) {
                            // Add 1 to COUNT accumulator
                            bin += BINARY_ONE;
                            binAccumulators[level][summ][COUNT_INDEX] = bin;
                        }

                        // AVERAGE of values
                        // Get sum accumulator value
                        bin = binAccumulators[level][summ][SUM_INDEX];
                        // Get count accumulator value
                        bin2 = binAccumulators[level][summ][COUNT_INDEX];
                        if (bin != null) {
                            // Divide sum by count - divisor must be greater than zero
                            // System.out.println("Sum, Count: " + bin + ", " + bin2);
                            if (!bin2.equals(BINARY_ZERO)) {
                                // Divide sum by count
                                bin /= bin2;
                            }
                        }
                        // System.out.println("Average : " + bin);
                        binAccumulators[level][summ][AVG_INDEX] = bin;

                        // MAX of values
                        bin = binAccumulators[level][summ][MAX_INDEX];
                        if (objectColValue != null && bin != null) {
                            if (binaryColValue > bin) {
                                bin = binaryColValue;
                                binAccumulators[level][summ][MAX_INDEX] = bin;
                            }
                        }

                        // MIN of values - Important! Object is tested for NULL.
                        bin = binAccumulators[level][summ][MIN_INDEX];
                        if (objectColValue != null && bin != null) {
                            if (binaryColValue <= binAccumulators[level][summ][MIN_INDEX]) {
                                bin = binaryColValue;
                                binAccumulators[level][summ][MIN_INDEX] = bin;
                            }
                        }
                    } // End of detail for BINARY 

                    // Date values - DATE
                    // ------------------
                    // (Date SUM and AVG are not summarized - they have no sense for DATE)             
                    if (colTypes.get(summaryColIndexes.get(summ)) == Types.DATE) {

                        // Get summary column current value
                        dateColValue = rs.getDate(summaryColNames.get(summ));

                        // COUNT of values - always decimal 
                        dec = decAccumulators[level][summ][COUNT_INDEX];
                        if (dateColValue != null) // Add 1 to COUNT accumulator
                        {
                            dec = dec.add(DECIMAL_ONE);
                        }
                        decAccumulators[level][summ][COUNT_INDEX] = dec;

                        // MAX of values
                        dat = datAccumulators[level][summ][MAX_INDEX];
                        if (dateColValue != null && dat != null) {
                            if (dateColValue.compareTo(dat) > 0) {
                                dat = dateColValue;
                            }
                            datAccumulators[level][summ][MAX_INDEX] = dat;
                        }
                        if (dat == null) {
                            datAccumulators[level][summ][MAX_INDEX] = dateColValue;
                        }
                        if (dateColValue == null) {
                            datAccumulators[level][summ][MAX_INDEX] = dat;
                        }

                        // MIN of values
                        dat = datAccumulators[level][summ][MIN_INDEX];
                        if (dateColValue != null && dat != null) {
                            if (dateColValue.compareTo(dat) <= 0) {
                                dat = dateColValue;
                                datAccumulators[level][summ][MIN_INDEX] = dat;
                            }
                        }
                        if (dateColValue != null && dat == null) {
                            datAccumulators[level][summ][MIN_INDEX] = dateColValue;
                        }
                    } // End of detail DATE     

                    // Time values - TIME
                    // ------------------
                    // (Time SUM and AVG are not summarized - they have no sense for TIME)             
                    if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIME) {

                        // Get summary column current value
                        timeColValue = rs.getTime(summaryColNames.get(summ));

                        // COUNT of values - always decimal
                        dec = decAccumulators[level][summ][COUNT_INDEX];
                        if (timeColValue != null) {
                            // Add 1 to COUNT accumulator
                            dec = dec.add(DECIMAL_ONE);
                        }
                        decAccumulators[level][summ][COUNT_INDEX] = dec;

                        // MAX of values
                        tim = timAccumulators[level][summ][MAX_INDEX];
                        if (timeColValue != null && tim != null) {
                            if (timeColValue.compareTo(tim) > 0) {
                                tim = timeColValue;
                            }
                            timAccumulators[level][summ][MAX_INDEX] = tim;
                        }
                        if (tim == null) {
                            timAccumulators[level][summ][MAX_INDEX] = timeColValue;
                        }
                        if (timeColValue == null) {
                            timAccumulators[level][summ][MAX_INDEX] = tim;
                        }

                        // MIN of values
                        tim = timAccumulators[level][summ][MIN_INDEX];
                        if (timeColValue != null && tim != null) {
                            if (timeColValue.compareTo(tim) <= 0) {
                                tim = timeColValue;
                                timAccumulators[level][summ][MIN_INDEX] = tim;
                            }
                        }
                        if (timeColValue != null && tim == null) {
                            timAccumulators[level][summ][MIN_INDEX] = timeColValue;
                        }
                    } // End of detail TIME     

                    // Timestamp values - TIMESTAMP
                    // ----------------------------
                    // (Timestamp SUM and AVG are not summarized - they have no sense for TIMESTAMP)             
                    if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIMESTAMP) {

                        // Get summary column current value
                        timestampColValue = rs.getTimestamp(summaryColNames.get(summ));

                        // COUNT of values - always decimal
                        dec = decAccumulators[level][summ][COUNT_INDEX];
                        if (timestampColValue != null) {
                            // Add 1 to COUNT accumulator
                            dec = dec.add(DECIMAL_ONE);
                        }
                        decAccumulators[level][summ][COUNT_INDEX] = dec;

                        // MAX of values
                        timst = timstAccumulators[level][summ][MAX_INDEX];
                        if (timestampColValue != null && timst != null) {
                            if (timestampColValue.compareTo(timst) > 0) {
                                timst = timestampColValue;
                            }
                            timstAccumulators[level][summ][MAX_INDEX] = timst;
                        }
                        if (timst == null) {
                            timstAccumulators[level][summ][MAX_INDEX] = timestampColValue;
                        }
                        if (timestampColValue == null) {
                            timstAccumulators[level][summ][MAX_INDEX] = timst;
                        }

                        // MIN of values
                        timst = timstAccumulators[level][summ][MIN_INDEX];
                        if (timestampColValue != null && timst != null) {
                            if (timestampColValue.compareTo(timst) <= 0) {
                                timst = timestampColValue;
                                timstAccumulators[level][summ][MIN_INDEX] = timst;
                            }
                        }
                        if (timestampColValue != null && timst == null) {
                            timstAccumulators[level][summ][MIN_INDEX] = timestampColValue;
                        }
                    } // End of detail TIMESTAMP     

                    // String values - CHAR, VARCHAR, LONGVARCHAR
                    // -----------------------------
                    // (String SUM and AVG are not summarized - they have no sense for CHAR, VARCHAR, LONGVARCHAR)             
                    if (colTypes.get(summaryColIndexes.get(summ)) == Types.CHAR
                            || colTypes.get(summaryColIndexes.get(summ)) == Types.VARCHAR
                            || colTypes.get(summaryColIndexes.get(summ)) == Types.LONGVARCHAR) {

                        // Get summary column current value                       
                        stringColValue = rs.getString(summaryColNames.get(summ));

                        // COUNT of values - always decimal 
                        dec = decAccumulators[level][summ][COUNT_INDEX];
                        if (stringColValue != null) {
                            // Add 1 to COUNT accumulator
                            dec = dec.add(DECIMAL_ONE);
                        }
                        decAccumulators[level][summ][COUNT_INDEX] = dec;

                        // MAX of values
                        str = strAccumulators[level][summ][MAX_INDEX];
                        if (stringColValue != null && str != null) {
                            if (stringColValue.compareTo(str) > 0) {
                                str = stringColValue;
                            }
                            strAccumulators[level][summ][MAX_INDEX] = str;
                        }
                        if (str == null) {
                            strAccumulators[level][summ][MAX_INDEX] = stringColValue;
                        }
                        if (stringColValue == null) {
                            strAccumulators[level][summ][MAX_INDEX] = str;
                        }

                        // MIN of values
                        str = strAccumulators[level][summ][MIN_INDEX];
                        if (stringColValue != null && str != null) {
                            if (stringColValue.compareTo(str) <= 0) {
                                str = stringColValue;
                            }
                            strAccumulators[level][summ][MIN_INDEX] = str;
                        }
                        if (str == null) {
                            strAccumulators[level][summ][MIN_INDEX] = stringColValue;
                        }
                        if (stringColValue == null) {
                            strAccumulators[level][summ][MIN_INDEX] = str;
                        }

                    } // End of detail for CHAR, VARCHAR, LONGVARCHAR                         
                }
            }

            // Get NEXT detail column values variables in the level separation line
            // after all level breaks
            for (int colNameIndex = 0; colNameIndex < allColNames.size(); colNameIndex++) {
                if (rs.getString(colNameIndex + 1) != null) {
                    detColNames.set(colNameIndex, allColNames.get(colNameIndex));
                    detColValues.set(colNameIndex, rs.getString(colNameIndex + 1));
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw e;
        }
    } // End of Level processing

    /**
     *
     * @throws Exception
     */
    protected void processLastLevelBreak() throws Exception {
        String levelHeader;
        // Process LAST RECORD through levels BACKWARDS
        for (int level = levelBreakNames.size() - 1; level >= 0; level--) {

            // Insert a number of empty lines BEFORE summary lines
            for (int idx = 0; idx < spaceB; idx++) {
                summaryTextArea.append("\n");
            }
            // Include LEVEL TEXT (at index 1) on separate line of
            // result text area ending with new line

            if (!levelArrayList.get(0)[0].equals("0")) {

                // If L0 is not specified, get text from current level
                levelHeader = getValuesForVariables(levelArrayList.get(level)[1], detColNames,
                        detColValues);
                // System.out.println("levelHeader 2: " + levelHeader);
                summaryTextArea.append(levelHeader + "\n");
            } else {
                // If L0 is specified, get text from next level
                levelHeader = getValuesForVariables(levelArrayList.get(level + 1)[1], detColNames,
                        detColValues);
                // System.out.println("levelHeader 2: " + levelHeader);
                summaryTextArea.append(levelHeader + "\n");
            }

            // SUM function
            summaryForTotals(level, SUM_INDEX, leadingSumString, printLineSUM);
            // AVG function
            summaryForTotals(level, AVG_INDEX, leadingAvgString, printLineAVG);
            // MAX function
            summaryForTotals(level, MAX_INDEX, leadingMaxString, printLineMAX);
            // MIN function
            summaryForTotals(level, MIN_INDEX, leadingMinString, printLineMIN);
            // COUNT function
            summaryForTotals(level, COUNT_INDEX, leadingCountString, printLineCOUNT);

            if (atLeastOneSUM) {
                summaryTextArea.append(printLineSUM.toString());
                summaryTextArea.append("\n");
                // System.out.println("SUM printLineSUM: " + printLineSUM);
                printLineSUM = new StringBuilder();
                printLineSUM.append(getSpaces(colEndPositions[colLengths.size() - 1]));
            }
            if (atLeastOneAVG) {
                summaryTextArea.append(printLineAVG.toString());
                summaryTextArea.append("\n");
                // System.out.println("AVG printLineAVG: " + printLineAVG);
                printLineAVG = new StringBuilder();
                printLineAVG.append(getSpaces(colEndPositions[colLengths.size() - 1]));
            }
            if (atLeastOneMAX) {
                summaryTextArea.append(printLineMAX.toString());
                summaryTextArea.append("\n");
                // System.out.println("MAX printLineMAX: " + printLineMAX);
                printLineMAX = new StringBuilder();
                printLineMAX.append(getSpaces(colEndPositions[colLengths.size() - 1]));
            }
            if (atLeastOneMIN) {
                summaryTextArea.append(printLineMIN.toString());
                summaryTextArea.append("\n");
                // System.out.println("MIN printLineMIN: " + printLineMIN);
                printLineMIN = new StringBuilder();
                printLineMIN.append(getSpaces(colEndPositions[colLengths.size() - 1]));
            }
            if (atLeastOneCOUNT) {
                summaryTextArea.append(printLineCOUNT.toString());
                summaryTextArea.append("\n");
                // System.out.println("COUNT printLineCOUNT: " + printLineCOUNT);
                printLineCOUNT = new StringBuilder();
                printLineCOUNT.append(getSpaces(colEndPositions[colLengths.size() - 1]));
            }

            /*
            // Insert a number of empty lines AFTER summary lines
            for (int idx = 0; idx < spaceA; idx++) {
                summaryTextArea.append("\n");
            }
             */
            // Include summary text area into the result text area
            // to complete the listing
            resultTextArea.append(summaryTextArea.getText());

            // Clear summary text area for the next level
            summaryTextArea.setText("");

        } // End of levels

        // Print GRAND TOTAL only when level L0 is specified
        // -----------------
        if (levelArrayList.get(0)[0].equals("0")) {

            // Insert a number of empty lines before summary lines
            for (int idx = 0; idx < spaceB; idx++) {
                summaryTextArea.append("\n");
            }

            // Include GRAND TOTAL TEXT (at index 1) on separate line of
            // result text area ending with new line
            summaryTextArea.append(levelArrayList.get(0)[1] + "\n");

            // SUM function
            summaryForGrandTotal(SUM_INDEX, leadingSumString, printLineSUM);
            // AVG function
            summaryForGrandTotal(AVG_INDEX, leadingAvgString, printLineAVG);
            // MAX function
            summaryForGrandTotal(MAX_INDEX, leadingMaxString, printLineMAX);
            // MIN function
            summaryForGrandTotal(MIN_INDEX, leadingMinString, printLineMIN);
            // COUNT function
            summaryForGrandTotal(COUNT_INDEX, leadingCountString, printLineCOUNT);

            if (atLeastOneSUM) {
                summaryTextArea.append(printLineSUM.toString());
                summaryTextArea.append("\n");
                // System.out.println("TOT SUM printLineSUM: " + printLineSUM);
            }
            if (atLeastOneAVG) {
                summaryTextArea.append(printLineAVG.toString());
                summaryTextArea.append("\n");
                // System.out.println("TOT AVG printLineAVG: " + printLineAVG);
            }
            if (atLeastOneMAX) {
                summaryTextArea.append(printLineMAX.toString());
                summaryTextArea.append("\n");
                // System.out.println("TOT MAX printLineMAX: " + printLineMAX);
            }
            if (atLeastOneMIN) {
                summaryTextArea.append(printLineMIN.toString());
                summaryTextArea.append("\n");
                // System.out.println("TOT MIN printLineMIN: " + printLineMIN);
            }
            if (atLeastOneCOUNT) {
                summaryTextArea.append(printLineCOUNT.toString());
                summaryTextArea.append("\n");
                // System.out.println("TOT COUNT printLineCOUNT: " + printLineCOUNT);
            }

            /*
            // Insert a number of enpty lines AFTER summary lines
            for (int idx = 0; idx < spaceA; idx++) {
                summaryTextArea.append("\n");
            }
             */
            // Include summary text area into the result text area
            // to complete the listing
            resultTextArea.append(summaryTextArea.getText());

        } // End of Level L0

    } // End of Total processing

    /**
     *
     * @throws SQLException
     */
    protected void getFirstLevelBreakValues() throws SQLException {
        levelBreakNames = new ArrayList<>();
        levelBreakValues = new ArrayList<>();
        lastNonNulllevelBreakValues = new ArrayList<>();
        int colNameIndex = 0;
        try {
            for (int level = 0; level < levelArrayList.size(); level++) {
                levelValues = levelArrayList.get(level);
                // Find column name matching the break level name from the parameter

                for (colNameIndex = 0; colNameIndex < allColNames.size(); colNameIndex++) {
                    // If the control level name equals an existing column name
                    // get the name and its value for comparison to array lists.
                    // Level L0 must not contain a column name!
                    if (levelValues[2].equals(allColNames.get(colNameIndex)) && !levelValues[0].equals("0")) {
                        // Column name is at index 2
                        // - after 0 (level number), 1 (level text) in the array of values
                        //System.out.println("FIRST BREAK rs.getObject(colNameIndex + 1): " + rs.getObject(colNameIndex + 1));
                        if (rs.getObject(colNameIndex + 1) != null) {
                            levelBreakNames.add(allColNames.get(colNameIndex));
                            levelBreakValues.add(rs.getObject(colNameIndex + 1).toString());
                            lastNonNulllevelBreakValues.add(rs.getObject(colNameIndex + 1).toString());
                            break; // Do not compare again if one matched
                        } else {
                            levelBreakValues.set(level, nullPrintMark);
                            break;
                        }
                    }
                    if (levelValues[3].toUpperCase().equals("NP")) {
                        // no operation
                    }
                }
            }

        } catch (Exception exc) {
            System.out.println("Error: Column " + allColNames.get(colNameIndex) + " level " + level 
                    + " has no value. System message: " + exc.toString());
            exc.printStackTrace();
        }
    }

    /**
     *
     * @param level
     * @throws SQLException
     */
    protected void getNextLevelBreakValues(int level) throws SQLException {

        // System.out.println("   NEXT BREAK rs.getObject(levelBreakNames.get("+level+")): " + rs.getObject(levelBreakNames.get(level)));
        // System.out.println("   NEXT BREAK lastNonNulllevelBreakValues: " + lastNonNulllevelBreakValues);
        if (rs.getObject(levelBreakNames.get(level)) != null) {
            levelBreakValues.set(level, rs.getObject(levelBreakNames.get(level)));
            lastNonNulllevelBreakValues.set(level, levelBreakValues.get(level));
        } else {
            levelBreakValues.set(level, nullPrintMark);
        }
        //System.out.println("NEXT BREAK levelBreakValues.get("+level+"): " + levelBreakValues.get(level));
    }

    /**
     * Processing of summary totals at end of result set
     *
     * @throws java.lang.Exception
     */
    /**
     * Summary of decimal values for control levels
     *
     * @param level
     * @param aggType
     * @param leadingString
     * @param printLine
     * @throws java.sql.SQLException
     */
    protected void summaryForLevels(int level, int aggType, String leadingString, StringBuilder printLine)
            throws SQLException {

        // Process all summary columns
        for (int summ = 0; summ < summaryColNames.size(); summ++) {
            // Prepend leading string (spaces + prefix)            
            printLine.replace(0, leadingString.length(), leadingString);
            // Decimal values - DECIMAL or NUMERIC
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.DECIMAL
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.NUMERIC) {
                // Edit the accumulated number
                if (aggType != COUNT_INDEX && decAccumulators[level][summ][aggType] != null) {
                    colData = editNumericDecimal(decAccumulators[level][summ][aggType],
                            summaryColNames.get(summ));
                } else if (decAccumulators[level][summ][aggType] == null) {
                    colData = nullPrintMark;
                } else {
                    colData = decAccumulators[level][summ][aggType].toString();
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
                // Reset accumulator for next level
                if (aggType == MAX_INDEX || aggType == MIN_INDEX) {
                    decAccumulators[level][summ][aggType] = getResetDecimalValue(summaryColNames.get(summ));
                } else {
                    decAccumulators[level][summ][aggType] = DECIMAL_ZERO;
                }
            }

            // Binary values - INTEGER, SMALLINT or BIGINT 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.INTEGER
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.SMALLINT
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.BIGINT) {
                // Edit the accumulated number
                colData = binAccumulators[level][summ][aggType].toString();
                adjustAndInsertToPrintLine(printLine, summ, aggType);
                // Reset accumulator for next level
                if (aggType == MAX_INDEX || aggType == MIN_INDEX) {
                    binAccumulators[level][summ][aggType] = getResetBinaryValue(summaryColNames.get(summ));
                } else {
                    binAccumulators[level][summ][aggType] = BINARY_ZERO;
                }
            }

            // Date values - DATE 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.DATE) {
                // DATE type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for DATE column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else if (datAccumulators[level][summ][aggType] != null) {
                        // Normal editing for DATE type
                        colData = datAccumulators[level][summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
                // Reset accumulator for next level 
                if (aggType != COUNT_INDEX) {
                    datAccumulators[level][summ][aggType] = getResetDateValue(summaryColNames.get(summ));
                } else {
                    decAccumulators[level][summ][aggType] = DECIMAL_ZERO;
                }
            }

            // Time values - TIME 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIME) {
                // TIME type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for TIME column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else if (timAccumulators[level][summ][aggType] != null) {
                        // Normal editing for TIME type
                        colData = timAccumulators[level][summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
                // Reset accumulator for next level 
                if (aggType != COUNT_INDEX) {
                    timAccumulators[level][summ][aggType] = getResetTimeValue(summaryColNames.get(summ));
                } else {
                    decAccumulators[level][summ][aggType] = DECIMAL_ZERO;
                }
            }

            // Timestamp values - TIMESTAMP 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIMESTAMP) {
                // TIMESTAMP type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for TIME column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else if (timstAccumulators[level][summ][aggType] != null) {
                        // Normal editing for TIMESTAMP type
                        colData = timstAccumulators[level][summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
                // Reset accumulator for next level 
                if (aggType != COUNT_INDEX) {
                    timstAccumulators[level][summ][aggType] = getResetTimestValue(summaryColNames.get(summ));
                } else {
                    decAccumulators[level][summ][aggType] = DECIMAL_ZERO;
                }
            }

            // String values - CHAR, VARCHAR, LONGVARCHAR 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.CHAR
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.VARCHAR
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.LONGVARCHAR) {
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    // CHAR type is summarized only for MAX, MIN, COUNT.
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for DATE column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else {
                        // No editing for CHAR, VARCHAR, LONGVARCHAR type
                        colData = strAccumulators[level][summ][aggType];
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
                // Reset accumulator for next level
                if (aggType != COUNT_INDEX) {
                    strAccumulators[level][summ][aggType] = getResetCharValue(summaryColNames.get(summ));
                } else {
                    decAccumulators[level][summ][aggType] = DECIMAL_ZERO;
                }
            }
        }
    }

    /**
     * Summary of decimal values for totals
     *
     * @param level
     * @param aggType
     * @param leadingString
     * @param printLine
     */
    protected void summaryForTotals(int level, int aggType, String leadingString, StringBuilder printLine) {

        // Append all aggregated values on the same line
        for (int summ = 0; summ < summaryColNames.size(); summ++) {
            // Prepend leading string (spaces + prefix)
            printLine.replace(0, leadingString.length(), leadingString);
            // Decimal values - DECIMAL or NUMERIC
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.DECIMAL || colTypes.get(summaryColIndexes.get(summ)) == Types.NUMERIC) {
                if (aggType != COUNT_INDEX && decAccumulators[level][summ][aggType] != null) {
                    // Edit the accumulated number
                    colData = editNumericDecimal(decAccumulators[level][summ][aggType],
                            summaryColNames.get(summ));
                } else if (decAccumulators[level][summ][aggType] == null) {
                    colData = nullPrintMark;
                } else {
                    colData = decAccumulators[level][summ][aggType].toString();
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }

            // Binary values - INTEGER, SMALLINT or BIGINT 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.INTEGER
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.SMALLINT
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.BIGINT) {
                // Prepend leading string (spaces + prefix)
                //               printLine.replace(0, leadingString.length(), leadingString);
                // Edit the accumulated number
                colData = binAccumulators[level][summ][aggType].toString();
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }

            // Date values - DATE 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.DATE) {
                // DATE type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for DATE column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else // Normal editing for DATE type
                    if (datAccumulators[level][summ][aggType] != null) {
                        colData = datAccumulators[level][summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }

            // Time values - TIME 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIME) {
                // TIME type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for TIME column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else // Normal editing for TIME type
                    if (timAccumulators[level][summ][aggType] != null) {
                        colData = timAccumulators[level][summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }

            // Timestamp values - TIMESTAMP 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIMESTAMP) {
                // TIMESTAMP type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for TIMESTAMP column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else // Normal editing for TIME type
                    if (timstAccumulators[level][summ][aggType] != null) {
                        colData = timstAccumulators[level][summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }

            // String values - CHAR, VARCHAR, LONGVARCHAR 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.CHAR
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.VARCHAR
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.LONGVARCHAR) {
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for CHAR column                    
                        colData = decAccumulators[level][summ][aggType].toString();
                    } else {
                        // Normal editing for CHAR type
                        colData = strAccumulators[level][summ][aggType];
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }
        }
    } // End of summaryForTotals

    /**
     * Summary of decimal values for grand total
     *
     * @param aggType
     * @param leadingString
     * @param printLine
     */
    protected void summaryForGrandTotal(int aggType, String leadingString, StringBuilder printLine) {
        for (int summ = 0; summ < summaryColNames.size(); summ++) {
            // Prepend leading string (spaces + prefix)
            printLine.replace(0, leadingString.length(), leadingString);

            // Decimal values - DECIMAL or NUMERIC
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.DECIMAL || colTypes.get(summaryColIndexes.get(summ)) == Types.NUMERIC) {
                // Edit the accumulated number
                if (!summaryIndications[summ][aggType + 1].isEmpty()) {
                    if (aggType != COUNT_INDEX && decTotalAccumulators[summ][aggType] != null) {
                        colData = editNumericDecimal(decTotalAccumulators[summ][aggType], summaryColNames.get(summ));
                    } else if (decTotalAccumulators[summ][aggType] == null) {
                        colData = nullPrintMark;
                    } else {
                        colData = decTotalAccumulators[summ][aggType].toString();
                    }
                    adjustAndInsertToPrintLine(printLine, summ, aggType);
                }
            }

            // Binary values - INTEGER, SMALLINT or BIGINT 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.INTEGER
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.SMALLINT
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.BIGINT) {
                // Do not edit binary number
                colData = binTotalAccumulators[summ][aggType].toString();
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }

            // Date values - DATE 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.DATE) {
                // DATE type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for DATE column                    
                        colData = decTotalAccumulators[summ][aggType].toString();
                    } else // Normal editing for DATE type
                    if (datTotalAccumulators[summ][aggType] != null) {
                        colData = datTotalAccumulators[summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                    adjustAndInsertToPrintLine(printLine, summ, aggType);
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
            }

            // Time values - TIME 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIME) {
                // TIME type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for TIME column                    
                        colData = decTotalAccumulators[summ][aggType].toString();
                    } else // Normal editing for TIME type
                    if (timTotalAccumulators[summ][aggType] != null) {
                        colData = timTotalAccumulators[summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }
            // Timestamp values - TIMESTAMP 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.TIMESTAMP) {
                // TIMESTAMP type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for TIMESTAMP column                    
                        colData = decTotalAccumulators[summ][aggType].toString();
                    } else // Normal editing for TIME type
                    if (timstTotalAccumulators[summ][aggType] != null) {
                        colData = timstTotalAccumulators[summ][aggType].toString();
                    } else {
                        colData = nullPrintMark;
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }
            // String values - CHAR, VARCHAR, LONGVARCHAR 
            if (colTypes.get(summaryColIndexes.get(summ)) == Types.CHAR
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.VARCHAR
                    || colTypes.get(summaryColIndexes.get(summ)) == Types.LONGVARCHAR) {
                // CHAR type is summarized only for MAX, MIN, COUNT.
                if (aggType == MAX_INDEX || aggType == MIN_INDEX || aggType == COUNT_INDEX) {
                    if (aggType == COUNT_INDEX) {
                        // Do not edit accumulated DECIMAL count for CHAR column                    
                        colData = decTotalAccumulators[summ][aggType].toString();
                    } else {
                        // Normal editing for CHAR, VARCHAR, LONGVARCHAR type
                        colData = strTotalAccumulators[summ][aggType];
                    }
                } else {
                    colData = "";
                    // Place for SUM and AVG is cleared
                    columnData = getSpaces(columnData.length());
                }
                adjustAndInsertToPrintLine(printLine, summ, aggType);
            }
        }
    } // End of summaryForGrandTotal

    /**
     *
     * @param printLine
     * @param summ
     * @param aggType
     */
    protected void adjustAndInsertToPrintLine(StringBuilder printLine, int summ, int aggType) {
        // Adjust an insert value in print line
        if (!summaryIndications[summ][aggType + 1].isEmpty()) {
            // If the column is to be summarized, adjust its value and 
            // put it to the print line from the starting position.                
            for (int col = 0; col < colNames.size(); col++) {
                if (summaryColNames.get(summ).equals(colNames.get(col))) {
                    sumCol = col;
                    columnData = adjustColumn(col, colData);
                    printLine.replace(colStrPositions[sumCol], colEndPositions[sumCol],
                            columnData + colSepSpaces);
                }
            }
        }
    }

    /**
     *
     * @param colName
     * @return
     */
    protected BigDecimal getResetDecimalValue(String colName) {
        BigDecimal returnVal;
        if (matchAllColNames(colName, Types.DECIMAL) > -1
                || matchAllColNames(colName, Types.NUMERIC) > -1) {
            try {
                returnVal = rs.getBigDecimal(colName);
            } catch (Exception ex) {
                // ex.printStackTrace();
                returnVal = null;
            }
        } else {
            returnVal = null;
        }
        // System.out.println("DECIMAL colName: " + colName);
        // System.out.println("DECIMAL returnVal: " + returnVal);
        return returnVal;
    }

    /**
     *
     * @param colName
     * @return
     */
    protected Long getResetBinaryValue(String colName) {
        Long returnVal;
        if (matchAllColNames(colName, Types.BIGINT) > -1
                || matchAllColNames(colName, Types.INTEGER) > -1
                || matchAllColNames(colName, Types.SMALLINT) > -1) {
            try {
                returnVal = rs.getLong(colName);
            } catch (Exception ex) {
                // ex.printStackTrace();
                returnVal = null;
            }
        } else {
            returnVal = null;
        }
        return returnVal;
    }

    /**
     *
     * @param colName
     * @return
     */
    protected Date getResetDateValue(String colName) {
        Date returnVal;
        if (matchAllColNames(colName, Types.DATE) > -1) {
            try {
                returnVal = rs.getDate(colName);
            } catch (Exception ex) {
                //ex.printStackTrace();
                returnVal = null;
            }
        } else {
            returnVal = null;
        }
        //System.out.println("DATE returnVal: " + returnVal);
        return returnVal;
    }

    /**
     *
     * @param colName
     * @return
     */
    protected Time getResetTimeValue(String colName) {
        Time returnVal;
        if (matchAllColNames(colName, Types.TIME) > -1) {
            try {
                returnVal = rs.getTime(colName);
            } catch (Exception ex) {
                //ex.printStackTrace();
                returnVal = null;
            }
        } else {
            returnVal = null;
        }
        //System.out.println("TIME returnVal: " + returnVal);
        return returnVal;
    }

    /**
     *
     * @param colName
     * @return
     */
    protected Timestamp getResetTimestValue(String colName) {
        Timestamp returnVal;
        if (matchAllColNames(colName, Types.TIMESTAMP) > -1) {
            try {
                returnVal = rs.getTimestamp(colName);
            } catch (Exception ex) {
                //ex.printStackTrace();
                returnVal = null;
            }
        } else {
            returnVal = null;
        }
        //System.out.println("TIMESTAMP returnVal: " + returnVal);
        return returnVal;
    }

    /**
     *
     * @param colName
     * @return
     */
    protected String getResetCharValue(String colName) {
        String returnVal;
        if (matchAllColNames(colName, Types.CHAR) > -1
                || matchAllColNames(colName, Types.VARCHAR) > -1) {
            try {
                returnVal = rs.getObject(colName).toString();
            } catch (Exception ex) {
                //ex.printStackTrace();
                returnVal = null;
            }
        } else {
            returnVal = null;
        }
        // System.out.println("CHAR colName: " + colName);
        // System.out.println("CHAR returnVal: " + returnVal);        
        return returnVal;
    }

    /**
     *
     * @param colName
     * @param javaSqlType
     * @return
     */
    protected int matchAllColNames(String colName, int javaSqlType) {
        boolean matches = false;
        int returnVal = -1;
        int matchingIndex = 0;
        for (int in = 0; in < allColNames.size(); in++) {
            // System.out.println("Index in: " + in);
            if (colName.equals(allColNames.get(in))) {
                matches = true;
                matchingIndex = in;
                break;
            }
        }
        if (matches) {
            // If the variable name matches, its value will be the index of
            // the matching column
            if (allColTypes.get(matchingIndex) == javaSqlType) {
//                System.out.println(matchingIndex);
//                System.out.println(allColNames.get(matchingIndex));
                returnVal = matchingIndex;
            }
        }
        return returnVal;
    }

    /**
     *
     * @param colName
     * @return
     */
    protected int matchOmittedColNames(String colName) {
        boolean matches = false;
        int returnVal = -1;
        int matchingIndex = 0;
        for (int in = 0; in < omittedColNames.size(); in++) {
            // System.out.println("Index in: " + in);
            if (colName.equals(omittedColNames.get(in))) {
                matches = true;
                matchingIndex = in;
                break;
            }
        }
        if (matches) {
            // If the variable name matches, the matching column
            returnVal = matchingIndex;
        }
        return returnVal;
    }

    /**
     * Get values for variables &NAME specified in title texts
     *
     * @param titleText
     * @param listNames
     * @param listValues
     * @return
     * @throws Exception
     */
    protected String getValuesForVariables(String titleText, ArrayList<String> listNames,
            ArrayList<String> listValues) throws Exception {
        String titleHeader = titleText + " ";
        variableNames = new ArrayList<>();
        variableValues = new ArrayList<>();
        variableStarts = new ArrayList<>();
        variableEnds = new ArrayList<>();
        String variableValue = "";
        String variableName;
        String titleHeaderUpper = titleHeader.toUpperCase();
        int start = 0;
        int end;
        // Find all variable names of the form &NAME; if any
        // and corresponding values of omitted columns if any

        // Find the first ampersand (&) from the start position 0
        end = titleHeaderUpper.indexOf("&", start);

        // Loop while an ampersand is being found
        while (end >= 0) {
            // Extract all variable names and their values and positions
            start = end + 1;
            end = titleHeaderUpper.indexOf(" ", start);
            // System.out.println("ZDE end mezera: " + end);
            if (end < 0) {
                end = titleHeaderUpper.length();
                // System.out.println("ZDE end nový ř.: " + end);
            }
            // Extract variable name as a substring from start to end position
            variableName = titleHeaderUpper.substring(start, end);
            // Non-empty list of column names is compared to the variable name
            if (!listNames.isEmpty()) {
                // Compare each variable name with corresponding column names
                // and get value of the column that matches.
                boolean matches = false;
                int matchingIndex = 0;
                for (int in = 0; in < listNames.size(); in++) {
                    // System.out.println("Index in: " + in);
                    if (variableName.equals(listNames.get(in))) {
                        matches = true;
                        matchingIndex = in;
                        break;
                    }
                }
                if (matches == true) {
                    // If the variable name matches, its value will be the value of
                    // the matching column
                    variableName = listNames.get(matchingIndex);
                    variableValue = listValues.get(matchingIndex);
                } else // If the variable name does not match, its value will be '?'
                {
                    variableValue = "?";
                }
            }
            // The names and values are put to array lists.
            // Starting and ending positions of '&' and ' ' are also
            // put in array lists.
            // Fill arrays for replacing variables by values
            variableNames.add(variableName);
            variableValues.add(variableValue);
            variableStarts.add(start);
            variableEnds.add(end);

            // System.out.println("variableNames: " + variableNames);
            // System.out.println("variableValues: " + variableValues);
            // System.out.println("variableStarts: " + variableStarts);
            // System.out.println("variableEnds: " + variableEnds);
            // Next startint position will be current end position plus 1.
            start = end + 1;
            // Find next ampersand
            end = titleHeaderUpper.indexOf("&", start);
        }
        // Variable names in the current title header are replaced
        // by their values. Loop goes BACKWARDS.
        // If no first ampersand found then the following loop does nothing
        // and the title header remains unchanged.
        for (int in = variableNames.size() - 1; in >= 0; in--) {
            titleHeader = titleHeader.substring(0, variableStarts.get(in) - 1) + variableValues.get(in)
                    + titleHeader.substring(variableEnds.get(in) + 1);
            // System.out.println("***titleHeader: " + titleHeader);
        }
        return titleHeader;
    }

    /**
     *
     * @param rs_DecimalValue BigDecimal
     * @param colName
     * @return
     */
    protected String editNumericDecimal(BigDecimal rs_DecimalValue, String colName) {
        nf = NumberFormat.getNumberInstance(currentLocale);
        df = (DecimalFormat) nf;
        // If any pattern --;D definition was specified
        if (!patternArrayList.isEmpty()) {
            decimalPattern = defaultDecPattern;
            // Process in-script patterns with their column names
            // Lookup matching in-script column name in pattern array list
            for (int in = 0; in < patternArrayList.size(); in++) {
                // If the current column name matches a name [1]
                // in pattern definition line,
                // get the pattern [0] from the definition line
                if (colName.equals(patternArrayList.get(in)[1].toUpperCase())) {
                    decimalPattern = patternArrayList.get(in)[0];
                    // First match ends the test
                    break;
                } else {
                    // Otherwise get the default pattern from application parameters                            
                    decimalPattern = defaultDecPattern;
                    retCode[0] += "PATTERN_ERROR";
                    retCode[1] = colNam + colName + noMatch;
                    // ???? reportError(retCode[1]);
                    // ???? return retCode[1];
                }
            }
            if (decimalPattern.isEmpty()) {
                // If in-script pattern is empty or missing, take default one
                decimalPattern = defaultDecPattern;
            }
            // Apply decimal pattern on the decimal format object
            df.applyPattern(decimalPattern);
            // Format the column value using the decimal format object
            decNumberEdited = df.format(rs_DecimalValue);
        } else // IF no number pattern --;D definition was specified            
        {
            if (defaultDecPattern.isEmpty()) {
                // If default pattern is empty take standard localized decimal number            

                nf = NumberFormat.getNumberInstance(currentLocale);
                df = (DecimalFormat) nf;
                decNumberEdited = df.format(rs_DecimalValue);
            } // If default pattern exists take that pattern
            else {
                decimalPattern = defaultDecPattern;
                // Apply decimal pattern on the decimal format
                // object
                df.applyPattern(decimalPattern);
                // Format the column value using the decimal format object
                // println("EDIT rs_DecimalValue: " + rs_DecimalValue);
                if (rs_DecimalValue != null) {
                    decNumberEdited = df.format(rs_DecimalValue);
                } else {
                    decNumberEdited = nullPrintMark;
                }
            }
        }
        return decNumberEdited;
    }

    /**
     * Adjust the result column value right or left according to the type in the wider output column
     * width
     *
     * @param column
     * @param colData
     * @return
     */
    protected String adjustColumn(int column, String colData) {

        // Numeric columns are right adjusted (except TIMESTAMP)
        if ((colScales.get(column) > 0 || colTypes.get(column) == Types.NUMERIC
                || colTypes.get(column) == Types.DECIMAL || colTypes.get(column) == Types.INTEGER
                || colTypes.get(column) == Types.BIGINT || colTypes.get(column) == Types.SMALLINT)
                && colTypes.get(column) != Types.TIMESTAMP) {
            columnData = rightAdjust(colData, maxHeaderWidths[column]);
        } // Columns with other data types are left adjusted
        else {
            if (colData == null) {
                colData = nullPrintMark;
            }
            columnData = leftAdjust(colData, maxHeaderWidths[column]);
        }
        return columnData;
    }

    /**
     * Right adjust - pad by blanks on the left to fill the column
     *
     * @param fieldValue raw field value
     * @param colLen column length
     * @return column value = fieldValue left padded by blanks
     */
    /**
     *
     * @param fieldValue
     * @param colLen
     * @return
     */
    protected static String rightAdjust(String fieldValue, int colLen) {
        int valueLength, diff;
        valueLength = fieldValue.length();

        diff = colLen - valueLength;
        char[] colArr = new char[colLen + 1];
        // If value length is shorter than column length
        // pad the field array with blanks
        if (diff > 0) {
            fieldValue.getChars(0, valueLength, colArr, diff);
            for (int i = 0; i < diff; i++) {
                colArr[i] = ' ';
            }
            fieldValue = String.valueOf(colArr, 0, colLen);
        }
        return fieldValue;
    }

    /**
     * Left adjust - pad by blanks on the right to fill the column
     *
     * @param fieldValue raw field value
     * @param colLen column length
     * @return column value = fieldValue right padded by blanks
     */
    protected static String leftAdjust(String fieldValue, int colLen) {
        int valueLength, diff;
        valueLength = fieldValue.length();

        diff = colLen - valueLength;
        char[] colArr = new char[colLen + 1];
        // If value length is shorter than column length
        // pad the field array with blanks
        if (diff > 0) {
            fieldValue.getChars(0, valueLength, colArr, 0);
            for (int i = 0; i < diff; i++) {
                colArr[valueLength + i] = ' ';
            }
            fieldValue = String.valueOf(colArr, 0, colLen);
        }
        return fieldValue;
    }

    /**
     * Center adjust - pad by blanks on both sides to fill the column
     *
     * @param fieldValue raw field value
     * @param colLen column length
     * @return column value = fieldValue left padded by blanks
     */
    protected static String centerAdjust(String fieldValue, int colLen) {
        int valueLength, diff;
        valueLength = fieldValue.length();

        diff = colLen - valueLength;
        char[] colArr = new char[colLen + 1];
        // If value length is shorter than column length
        // pad the field array with blanks on both sides by half the difference
        if (diff > 0) {
            int pad = diff / 2;
            fieldValue.getChars(0, valueLength, colArr, pad);
            for (int i = 0; i < pad; i++) {
                colArr[i] = ' ';
            }
            for (int i = pad + valueLength; i < colLen; i++) {
                colArr[i] = ' ';
            }
            fieldValue = String.valueOf(colArr, 0, colLen);
        }
        return fieldValue;
    }

    /**
     * Get space string - create space string with a given length
     *
     * @param length string length
     * @return String with fixed number of blank characters
     */
    protected static String getSpaces(int length) {
        String result = "";
        if (length >= 0) {
            char[] colArr = new char[length + 1];
            for (int i = 0; i < length; i++) {
                colArr[i] = ' ';
            }
            result = String.valueOf(colArr, 0, length);
        }
        return result;
    }

    /**
     * Translate single byte to hexadecimal character
     *
     * @param singleByte
     * @return
     */
    static String byteToHex(byte singleByte) {
        int bin = (singleByte < 0) ? (256 + singleByte) : singleByte;
        int bin0 = bin >>> 4; // horní půlbajt
        int bin1 = bin % 16; // dolní půlbajt
        String hex = Integer.toHexString(bin0) + Integer.toHexString(bin1);
        return hex;
    }

    /**
     * Translate hexadecimal character to single byte
     *
     * @param hexChar
     * @return
     */
    static byte hexToByte(String hexChar) {
        // Translation tables
        String args = "0123456789abcdef";
        int[] funcs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        // Two characters from the String traslated in lower case
        char charHigh = hexChar.toLowerCase().charAt(0);
        char charLow = hexChar.toLowerCase().charAt(1);
        int highHalf = 0, lowHalf = 0;
        // Find character in argument table
        if (args.indexOf(charHigh) > -1) // If found get corresponding function (int value)
        // If not found - result is 0
        {
            highHalf = funcs[args.indexOf(charHigh)];
        }
        if (args.indexOf(charLow) > -1) {
            lowHalf = funcs[args.indexOf(charLow)];
        }
        // Assemble high and low half-bytes in single byte
        int singleByte = (highHalf << 4) + lowHalf;
        return (byte) singleByte;
    }
}
