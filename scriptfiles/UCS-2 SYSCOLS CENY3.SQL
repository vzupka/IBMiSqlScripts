--Seznam jmen sloupců tabulky CENY3

SELECT varchar(TABLE_NAME, 10) table,
       varchar (DATA_TYPE) type,
       varchar (CCSID) ccsid,
       varchar (COLUMN_NAME, 10) col_name,
       int(LENGTH) length, 
       int(NUMERIC_SCALE) scale
FROM QSYS2/SYSCOLUMNS2                                
WHERE TABLE_NAME = 'CENY3' AND                       
   TABLE_SCHEMA = 'VZTOOL'                          
