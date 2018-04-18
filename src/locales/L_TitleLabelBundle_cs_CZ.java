package locales;

import java.util.ListResourceBundle;
/**
 * 
 * @author Vladimír Župka 2016
 */
public class L_TitleLabelBundle_cs_CZ extends ListResourceBundle {
   @Override
   public Object[][] getContents() {
      return contents;
   }

   private final Object[][] contents = {
         // Q_Menu
         { "ParApp", "Upravit parametry aplikace" },
         { "SelRun", "Vybrat skript a spustit" },
         { "CrtEdit", "Vytvořit nebo upravit SQL skript" },
         { "DspPrt",
               "Zobrazit či tisknout výsledek posledního skriptu \npopřípadě obsah jiných souborů" },
         { "ExpScr", "Export skriptů z PC do IBM i" },
         { "ImpScr", "Import skriptů z IBM i do PC" },
         { "TitMenu", "\nSQL skripty\n" }, 
         // Q_PrintOneFile
         { "TitView", "Výsledek SQL skriptu" },
         { "TitPage", "Strana " },
         // ParametersEdit
         { "DefParApp", "\nZadání parametrů aplikace\n" },
         { "AdrSvr", "Adresa serveru" },
         { "UsrName", "Jméno uživatele" },
         { "LibList", "Seznam knihoven s databázovými tabulkami" },
         { "IfsDir", "Adresář IFS k centrálnímu uložení skriptů" },
         { "AutWin", "Automatická velikost okna s výsledky dotazu" },
         { "WinWidth", "Šířka okna s výsledky dotazu" },
         { "WinHeight", "Výška okna s výsledky dotazu" },
         { "NullMark", "Značka pro prázdné hodnoty sloupců,\n"
               + "není-li ve skriptu zadána jiná" },
         { "ColSpaces", "Počet mezer oddělujících sloupce ve výsledku dotazu" },
         { "FontSize", "Výška písma v počtu tiskových bodů" },
         { "DecPattern", "Maska pro tisk čísel, např. #.00 \npro potlačení nul zleva a zachování dvou desetinných míst" },
         { "OrEnter", "nebo stiskněte ENTER" },  
         // Q_PromptParameters
         { "ScrName", "\nJméno skriptu: " },  
         { "DefSelVals", "\nZadejte hodnoty parametrů" },  
         // Q_PromptForScriptName
         { "ImpScript", "\nImport skriptu z adresáře v IBM i" },  
         { "DefFileScr", "\nZadejte jméno souboru - jméno skriptu s koncovkou .sql" },  
         // Q_ScriptEdit
         { "WrtScrN", "Zapište jméno skriptu s koncovkou .sql !" },  
         { "DefNewScr", "Zadejte jméno nového skriptu s příponou .sql, "
               + "zapište nebo přikopírujte text a stiskněte Uložit nebo Spustit skript." },  
         { "EditScrN", "Upravte SQL skript a stiskněte Uložit nebo Spustit skript." },  
         {"EditFontSize", "Velikost písma:"},
         {"Highlight", "Zvýraznit / nezvýraznit hlavní prvky jazyka SQL."},
         // Q_ScriptEditCall
         { "ScriptNam", "Název skriptu" },
         { "ScriptDes", "Popis skriptu" },         
         { "TitEdit", "Údržba skriptů SQL" }, 
         { "SearchScript", "Vybrat skripty. Napište vyhledávací vzorek (se znaky * nebo ?) a stiskněte klávesu Enter nebo tlačítko \"Obnovit zobrazení\"." }, 
         // Q_ScriptRunCall
         { "TitRun", "Spouštění skriptů SQL" }, 
         { "SearchScriptRun",  "Vybrat skripty. Napište část jména a stiskněte klávesu Enter nebo tlačítko \"Obnovit zobrazení\"." }, 
         // SQL properties
         { "DecSeparator", "," }, 
         { "SortLanguage", "CSY" },};
}
