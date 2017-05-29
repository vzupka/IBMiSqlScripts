package locales;

import java.util.ListResourceBundle;
/**
 * 
 * @author Vladimír Župka 2016
 */
public class L_ButtonBundle_en_US extends ListResourceBundle{
   @Override
   public Object[][] getContents() {
      return contents;
    }

    private final Object[][] contents = {
       // Q_Menu
       {"Run", "Run"},
       {"Param", "Parameters"},
       {"Edit", "Editing"},
       {"Exp", "Export"},
       {"Imp", "Import"},  
       // Q_PrintOneFile
       {"Sav", "Save data"}, 
       {"Exit", "Exit"},       
       {"Print", "Print"},  
       // Q_PromptParameters 
       {"Dsp_orig", "Display original"},       
       {"Dsp_input", "Display input"},       
       {"Enter", "Enter"},       
       // Q_PromptForScriptName
       {"Run_imp", "Run import"},    
       // Q_ScriptEdit
       {"Undo", "Undo"}, 
       {"Redo", "Redo"}, 
       {"Cancel", "Exit"},      
       {"Save", "Save script"},     
       {"RunScript", "Run script"},      
       // Q_ScriptEditCall
       {"New_script", "Create new script"},       
       {"Edit_sel", "Edit selected"},       
       {"Del_sel", "Delete selected"},    
       {"Refresh", "Refresh"},       
       {"Sav_to_svr", "Save to server"},       
       {"Read_from_svr", "Read from server"},   
       // Q_ScriptRunCall
       {"Run_sel", "Run selected"},       
       {"Imp_script", "Import script"},       
    };
}
