--List of columns in a table
--;? 1 ; ; Table name ; CENY2
--;? 2 ; ; Schema name ; VZTOOL
--;HFile name;Column;CCSID;Column name;Length;Dec.;
--;H         ;type  ;     ;           ;      ;pos.;
--;H

SELECT varchar(TABLE_NAME, 10),
       varchar (DATA_TYPE),
       varchar (CCSID),
       varchar (COLUMN_NAME, 10),
       int(LENGTH), 
       int(NUMERIC_SCALE)
FROM QSYS2.SYSCOLUMNS2
   WHERE TABLE_NAME = ? AND
         TABLE_SCHEMA = ?
