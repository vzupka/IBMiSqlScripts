--Seznam jmen sloupců tabulky CENY2
--;HJméno tabulky;Typ sloupce;CCSID;Jméno sloupce;Délka;Des.místa;
--;H
SELECT varchar(TABLE_NAME, 10),
       varchar (DATA_TYPE),
       varchar (CCSID),
       varchar (COLUMN_NAME, 10),
       int(LENGTH), 
       int(NUMERIC_SCALE)
FROM QSYS2.SYSCOLUMNS2                                
WHERE TABLE_NAME = 'CENY2' AND                       
   TABLE_SCHEMA = 'VZTOOL'                          
