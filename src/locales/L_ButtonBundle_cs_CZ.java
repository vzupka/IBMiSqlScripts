package locales;

import java.util.ListResourceBundle;
/**
 * 
 * @author Vladimír Župka 2016
 */
public class L_ButtonBundle_cs_CZ extends ListResourceBundle{
   @Override
   public Object[][] getContents() {
      return contents;
    }

    private final Object[][] contents = {
       // Q_Menu
       {"Run", "Spuštění"},
       {"Param", "Parametry"},
       {"Edit", "Editace"},
       {"Exp", "Export"},
       {"Imp", "Import"},       
       // Q_PrintOneFile
       {"Sav", "Uložte data"},  
       {"Exit", "Končit"},       
       {"Print", "Tisk"},       
       // Q_PromptParameters
       {"Dsp_orig", "Zobrazit původní"},       
       {"Dsp_input", "Zobrazit vstup"},       
       {"Enter", "Potvrdit"},  
       // Q_PromptForScriptName
       {"Run_imp", "Spustit import"},   
       // Q_ScriptEdit
       {"Undo", "Vrátit"}, 
       {"Redo", "Odebrat"}, 
       {"Cancel", "Končit"}, 
       {"Save", "Uložit skript"},      
       {"RunScript", "Spustit skript"},      
       {"ShortCaretText", "Krátký ukazatel"},      
       {"LongCaretText", "Dlouhý ukazatel"},      
       // Q_ScriptEditCall
       {"New_script", "Vytvořit nový skript"},       
       {"Edit_sel", "Upravit vybraný"},       
       {"Del_sel", "Zrušit vybraný"}, 
       {"Refresh", "Obnovit zobrazení"},       
       {"Sav_to_svr", "Uložit do serveru"},       
       {"Read_from_svr", "Načíst ze serveru"},    
       // Q_ScriptRunCall
       {"Run_sel", "Spustit vybraný"},       
       {"Imp_script", "Importovat skript"},  
       // Q_FilePrompt
       {"RemoveSelCols", "Odstranit vybrané sloupce"},       
       {"BuildColList", "Sestavit seznam sloupců"},       
    };
}
