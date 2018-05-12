package locales;

import java.util.ListResourceBundle;
/**
 * 
 * @author Vladimír Župka 2016
 */
public class L_MessageBundle_en_US extends ListResourceBundle {
   @Override
   public Object[][] getContents() {
      return contents;
   }

   private final Object[][] contents = { 
         // Q_ParametersEdit
         { "ParSaved", "Parameters have been saved to the file: " },
         // Q_Menu
         { "CurDir", "Current directory is: " },
         { "Wait", "Script files are being transferred..." },
         { "TransferEnd", "End of transfer." },
         // Q_ConnectDB
         { "Driver", "JDBC driver not found for the host " },
         { "ConnErr", "Connection error: " },
         // Q_ScriptRunCall
         { "ConnOk", "Connection established  with the server " },
         { "NoRowSel", "No row was selected. Select one." },
          { "InputError", "Input error reading script files - in the file following the file " },
        // Q_ScriptEditCall
         { "NoRowUpd", "No row was selected. Select one to edit." },
         { "NoRowDel", "No row was selected. Select one to delete." },
         { "NoRowSav", "No row was selected. Select one to save to the server." },
         { "File", "File  " },
         { "WasDelLoc", " was deleted from directory \"scriptfiles.\" " },
         { "WasDel", " was deleted from directory " },
         { "NotInDir", " does not exist in directory " },
         { "Script", "Script " },
         { "WasSavedTo", " was saved to " },
         // Q_PromptParameters
         { "OrderErr", "Error building parameters in the window: order " },
         { "CorrectRow", "Correct row " },
         { "OrOrig", " or restore original values." },
         { "DecNr", "decimal number" },
         { "IntNr", "integer" },
         { "DateForm", "date of the form yyyy-mm-dd" },
         { "TimeForm", "day time hh:mm:dd" },
         { "TimeStampForm", "time stamp of the form yyyy-mm-dd hh:mm:ss.mmmmmm" },
         { "OtherTypes", "other data types" },
         // Q_ScriptRun
         { "NoConn", "No connection with the server." },
         { "StmtSuccess", "Statement executed." },
         { "NumParError", "Invalid definition entries in comments: " },
         { "ColNam", "Column name " },
         { "NoMatch", " does not match the name in the pattern definition line.\n\n" },
         { "SqlError", "\nError in the statement:\n" },
         { "OtherError", "Other error in the script or in the program: " },
         { "MissingSummaryColumn", "Some summary column is missing. Nothing is summarized." },
         { "InvalidColumn", "Omitted column cannot be summarized: " },
         // Q_ScriptEdit
         { "IOError", "I/O error: " },
         { "NonAscii", "Script name contains an invalid character." },
         // Q_ExportOneToAS400
         { "WasExported", "  was EXPORTED to directory  " },
         { "NoFiles", "No files to transfer from directory  " },
         { "Directory", "Directory  " },
         { "NotFound", "  not found. System message:  " },
         // Q_ImportAllFromAS400
         { "WasImported", "  was IMPORTED from directory  " },
         { "ToDir", "  to directory \"scriptfiles\"." },
         { "NotFoundInDir", "  not found in directory  " },

   };
}
