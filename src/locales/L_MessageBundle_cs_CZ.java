package locales;

import java.util.ListResourceBundle;
/**
 * 
 * @author Vladimír Župka 2016
 */
public class L_MessageBundle_cs_CZ extends ListResourceBundle {
   @Override
   public Object[][] getContents() {
      return contents;
   }

   private final Object[][] contents = {
         // Q_ParametersEdit
         { "ParSaved", "Parametry byly uloženy do souboru: " },
         // Q_Menu
         { "CurDir", "Běžný adresář je: " },
         { "Wait", "Soubory skriptů se přenášejí..." },
         { "TransferEnd", "Konec přenosu." },
         // Q_ConnectDB
         { "Driver", "Nebyl nalezen ovladač JDBC k serveru " },
         { "ConnErr", "Chyba spojení: " },
         // Q_ScriptRunCall
         { "ConnOk", "Navázáno spojení se serverem " },
         { "NoRowSel", "Není vybrán žádný řádek. Vyberte jeden." },
         { "InputError", "Chyba vstupu při čtení souboru skriptů - v souboru následujícím za souborem " },
         // Q_ScriptEditCall
         { "NoRowUpd", "Není vybrán žádný řádek. Vyberte jeden k úpravě." },
         { "NoRowDel", "Není vybrán žádný řádek. Vyberte jeden ke zrušení." },
         { "NoRowSav", "Není vybrán žádný řádek. Vyberte jeden k uložení do serveru." },
         { "File", "Soubor  " },
         { "WasDelLoc", " byl smazán z adresáře \"scriptfiles.\" " },
         { "WasDel", " byl smazán z adresáře " },
         { "NotInDir", " neexistuje v adresáři " },
         { "Script", "Skript " },
         { "WasSavedTo", " byl uložen do " },
         // Q_PromptParameters
         { "OrderErr", "Chyba sestavení paramerů v okně: pořadí " },
         { "CorrectRow", "Opravte řádek " },
         { "OrOrig", " nebo obnovte původní hodnoty." },
         { "DecNr", "dekadické číslo" },
         { "IntNr", "celé číslo" },
         { "DateForm", "datum tvaru rrrr-mm-dd" },
         { "TimeForm", "denní čas hh:mm:dd" },
         { "TimeStampForm", "časové razítko tvaru rrrr-mm-dd hh:mm:ss.mmmmmm" },
         { "OtherTypes", "data ostatních typů" },
         // Q_ScriptRun
         { "NoConn", "Není spojení se serverem." },
         { "StmtSuccess", "Příkaz se provedl." },
         { "NumParError", "Chybné definiční údaje v komentářích: " },
         { "ColNam", "Jméno sloupce " },
         { "NoMatch", " nepáruje se jménem v definici masky.\n\n" },
         { "SqlError", "\nChyba v příkazu:\n" },
         { "OtherError", "Jiná chyba skriptu nebo programu: " },
         { "MissingSummaryColumn", "Chybí součtovací sloupec. Nic se nesumarizuje." },
         { "InvalidColumn", "Vynechávaný sloupec nelze sumarizovat: " },
         // Q_ScriptEdit
         { "IOError", "Chyba vstupu/výstupu:  " },
         { "NonAscii", "Jméno skriptu obsahuje nepovolený znak." },
         // Q_ExportOneToAS400
         { "WasExported", "  byl EXPORTOVÁN do adresáře  " },
         { "NoFiles", "Nejsou žádné soubory k přenosu z adresáře  " },
         { "Directory", "Adresář  " },
         { "NotFound", "  nenalezen. Systémová zpráva:  " },
         // Q_ImportAllFromAS400
         { "WasImported", "  byl IMPORTOVÁN z adresáře  " },
         { "ToDir", "  do adresáře \"scriptfiles\"." },
         { "NotFoundInDir", "  nebyl nalezen v adresáři  " },

   };
}
