--Vytvořit tabulku CENY4 s kódováním textu UTF-8

DROP TABLE VZTOOL.CENY4
;

CREATE TABLE VZTOOL.CENY4 
   ( CZBOZI CHAR(5), 
     CENAJ DEC(12, 2),
     NAZZBO CHAR(50) CCSID 1208
   )
;

SET SCHEMA DEFAULT
