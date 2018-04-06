# IBMiSqlScripts
IBM i SQL Scripts

Created by Vladimír Župka, 2017

vzupka@gmail.com

This project is not installed, it is ready to use as a Java application in the directory of the project. The application requires Java SE 8 installed in PC.

User documentation can be found in the subdirectory "documents".

Creation of this application was motivated by the fact that the popular utility Query/400 (later called Query for i) is unable to display and print all characters coded in character sets UCS-2 (CCSID 13488), UTF-16 (CCSID 1200), or UTF-8 (CCSID 1208).
This application enables creating, saving and running scripts of SQL statements for the IBM DB2 for i as well as flexible formatting and printing of the results.

Script is defined here as a text of SQL statements written in a text file with the suffix .sql. One or more SQL statements delimited by a semicolon can be written in the script. The SQL statements may be of any kind (DDL or DML) and can contain parameters designated by question marks. Most of the time the script will be a query, i. e. a single SELECT statement. That means that the creator of the script must know the SQL language at least at the level of the SELECT statement.
Scripts are usually amended by specially structured comment lines which enable flexible formatting of the script result when displayed on the screen or printed on paper.

Programs are written in Java language and require version Java SE 8 or higher. They cooperate with IBM i Toolbox for Java (or JTOpen framework). The programs were created and tested Mac OS X, macOS, Windows 7, Windows 10 with remote Internet connection to system IBM i.

Start the application by double click on either Q_Menu.jar or Q_MenuUser.jar file.

Version 01.01

- Sources of user documentation in Pages added to "documents" directory.
- Export and import - bug fixed, script files in IFS have now CCSID 1208.

- - - - - - - - - - 

Tento projekt se neinstaluje, je okamžitě použitelný jako Java aplikace v adresáři projektu. Aplikace vyžaduje instalaci Javy SE 8 v PC.

Uživatelská dokumentace je k dispozici v podadresáři "documents".

Motivem k vytvoření této aplikace bylo zjištění, že populární program Query/400 (s pozdějším názvem Query for i) není schopen zobrazovat a tisknout všechny znaky kódované v soustavě UCS-2 (CCSID 13488), UTF-16 (CCSID 1200) nebo UTF-8 (CCSID 1208).
Aplikace dovoluje vytvářet, ukládat a spouštět SQL skripty pro databázi IBM DB2 for i, jakož i pružně formátovat a tisknout výsledky. 

Skript je text příkazů jazyka SQL uložený v textovém souboru s koncovkou .sql. Do skriptu lze zapsat jeden nebo více příkazů SQL oddělených středníkem. Příkazy mohou být libovolného druhu a mohou obsahovat parametry označené otazníky. Nejčastěji ovšem půjde o dotazy, to znamená příkazy SELECT. Z uvedeného plyne, že tvůrce skriptů musí znát jazyk SQL alespoň na úrovni příkazu SELECT.
Do skriptů se kromě SQL příkazů obyčejně doplňují ještě další příkazy ve formě speciálních komentářových řádků, které dovolují pružně formátovat výstup výsledků na obrazovce a tisku na papír.

Programy jsou napsány v jazyku Java a vyžadují verzi Java SE 8 nebo vyšší. Spolupracují s programy soustavy IBM i Toolbox for Java (nebo JTOpen). Programy byly vytvořeny a testovány v systémech Mac OS X, macOS a Windows 7, Windows 10 se vzdáleným internetovým připojením k systému IBM i.

Aplikace se spouští poklepáním na soubor Q_Menu.jar nebo Q_MenuUser.jar.

Version 01.01

- Zdroje uživatelské dokumentace v Pages přidány do adresáře "documents".
- Export and import - opravena chyba, soubory skriptů v IFS mají nyní CCSID 1208.


