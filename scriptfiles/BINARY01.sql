--Drop, Create, Alter
DROP TABLE VZTOOL.BINARY01 ;
--Create table
CREATE TABLE VZTOOL.BINARY01 ( 
   ID INT,
   BINARKA BINARY (3),
   VARBINARKA VARBINARY (50) )
;
--;? 01; BINARY; Hexa 1; aabbcc;
--;? 02; VARBINARY; Hexa 2; aabbccddeeff;
insert into BINARY01 (ID, BINARKA, VARBINARKA) values(1, ?, ?) ;
--;? 01; BINARY; Hexa 1; 000102;
--;? 02; VARBINARY; Hexa 2; 00;
insert into BINARY01 (ID, BINARKA, VARBINARKA) values(2, ?, ?) ;
--;? 01; BINARY; Hexa 1; 001122;
--;? 02; VARBINARY; Hexa 2; 01;
insert into BINARY01 (ID, BINARKA, VARBINARKA) values(3, ?, ?) ;
--;? 01; BINARY; Hexa 1; 334455;
--;? 02; VARBINARY; Hexa 2; 02;
insert into BINARY01 (ID, BINARKA, VARBINARKA) values(4, ?, ?) ;

select * from VZTOOL.BINARY01 
