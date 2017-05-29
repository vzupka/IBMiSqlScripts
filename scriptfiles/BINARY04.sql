--Update - parameters
--;? 01; BINARY; Hexa 1; aabbcc;
--;? 02; INT; Číslo ID; 1;
update BINARY01 set BINARKA = ? where ID = ? ;

select * from BINARY01
