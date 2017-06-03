# IBMiSqlScripts
IBM i SQL Scripts

Created by Vladimír Župka, 2017

vzupka@gmail.com

This is a NetBeans project. The project is not installed, it is ready to use as a Java application in the directory of the project. The application requires Java SE 8 installed in PC.

User documentation can be found in the subdirectory "documents".

Creation of this application was motivated by the fact that the popular utility Query/400 (later called Query for i) is unable to display and print all characters coded in character sets UCS-2 (CCSID 13488), UTF-16 (CCSID 1200), or UTF-8 (CCSID 1208).
This application enables creating, saving and running scripts of SQL statements for the IBM DB2 for i as well as flexible formating and printing of the results.

Script is defined here as a text of SQL statements written in a text file with the suffix .sql. One or more SQL statements delimited by a semicolon can be written in the script. The SQL statements may be of any kind (DDL or DML) and can contain parameters designated by question marks. Most of the time the script will be a query, i. e. a single SELECT statement. That means that the creator of the script must know the SQL language at least at the level of the SELECT statement.
Scripts are usually amended by specially structured comment lines which enable flexible formatting of the script result when displayed on the screen or printed on paper.

Programs are written in Java language and require version Java SE 8 or higher. They cooperate with IBM i Toolbox for Java (or JTOpen framework). The programs were created and tested Mac OS X, macOS, Windows 7, Windows 10 with remote Internet connection to system IBM i.

Start the application by double click on either Q_Menu.jar or Q_MenuUser.jar file.
