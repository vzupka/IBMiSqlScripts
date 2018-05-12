package locales;

import java.util.ListResourceBundle;

/**
 *
 * @author Vladimír Župka 2016
 */
public class L_TitleLabelBundle_en_US extends ListResourceBundle {

    @Override
    public Object[][] getContents() {
        return contents;
    }

    private final Object[][] contents = {
        // Q_Menu
        {"ParApp", "Edit parameters for the application"},
        {"SelRun", "Select script and run"},
        {"CrtEdit", "Create or edit SQL script"},
        {"DspPrt", "Display or print last script result \nresp. contents of different files"},
        {"ExpScr", "Export scripts from PC to IBM i"},
        {"ImpScr", "Import scripts from IBM i to PC"},
        {"TitMenu", "\nSQL scripts\n"},
        // Q_PrintOneFile
        {"TitView", "Result of the SQL script"},
        {"TitPage", "Page "},
        // Q_ParametersEdit
        {"DefParApp", "\nDefine application parameters\n"},
        {"AdrSvr", "Server address"},
        {"UsrName", "User name"},
        {"LibList", "List of libraries with database tables"},
        {"IfsDir", "IFS directory for central scripts repository"},
        {"AutWin", "Automatic size of the window with query results"},
        {"WinWidth", "Width of the window with query results"},
        {"WinHeight", "Height of the window with query results"},
        {"NullMark", "Mark for null values of columns,\n"
            + "if not otherwise specified in the script"},
        {"ColSpaces", "Number of spaces separating columns in the query result"},
        {"FontSize", "Size of the font in print points"},
        {"DecPattern", "Pattern for printing numbers, "
            + "e.g. #.00 \nto suppress leading zeros and preserving two decimals"},
        {"OrEnter", "or press ENTER"},
        // Q_PromptParameters   
        {"ScrName", "\nScript name: "},
        {"DefSelVals", "\nEnter parameter values"},
        // Q_PromptForScriptName
        {"ImpScript", "\nImport script from the directory in IBM i"},
        {"DefFileScr", "\nEnter file name - script name with suffix .sql"},
        // Q_ScriptEdit
        {"WrtScrN", "Write a script name with suffix .sql !"},
        {"DefNewScr", "Enter a new script name with suffix .sql, "
            + "write or copy the text and press Save or Run script."},
        {"EditScrN", "Edit the SQL script and press Save or Run script."},
        {"EditFontSize", "Font size:"},
        {"Highlight", "Highlight / not hihglight main SQL language elements."},
        // Q_ScriptEditCall
        {"ScriptNam", "Script name"},
        {"ScriptDes", "Script description"},
        {"TitEdit", "Maintenance of SQL scripts"},
        {"SearchScript", "Select scripts. Enter a search pattern (with * or ?) and press Enter key or \"Refresh\" button."},
        // Q_ScriptRunCall
        {"TitRun", "Running SQL scripts"},
        {"SearchScriptRun", "Select scripts. Enter part of the name and press Enter key or \"Refresh\" button."},
        // SQL properties
        {"DecSeparator", "."},
        {"SortLanguage", "ENU"},
        // U_ColumnJList
        {"TitleCol", "Building column lists"},
        {"SchemaLabel", "Select or enter a schema name."},
        {"TableLabel", "Select a table or a view."},
        {"DragNames", "Select column names on the left and drag them to the right."},
        {"BuildColsItem", "Build column list"},
        {"RemoveSelCols", "Remove selected items"},
        {"SelAllItems", "Select all items"},
        {"TitleResCol", "Resulting column list"},
        {"CopyToEditor", "Select the list and copy to the editor."},
        {"Horiz", "In a line"},
        {"Append", "Append at the end"},
        {"HorizTip", "The column list will be built in a single line."},
        {"AppendTip", "The new column list will be appended to that already existing."},
    };
}
