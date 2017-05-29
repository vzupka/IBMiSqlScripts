--Drop, Create, Alter
DROP TABLE VZTOOL.CLOB01 ;
--Create table
CREATE TABLE VZTOOL.CLOB01 ( 
   ID INT,
   CHARLOB CLOB (200) ) ;

--;? 01; CLOB; Char lob; ;
insert into CLOB01 (ID, CHARLOB) values(1, ?)  ;

select * from VZTOOL.CLOB01
