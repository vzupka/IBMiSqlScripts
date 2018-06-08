--Update CENY2 první
--;? 1; VARGRAPHIC; Název zboží 001 *:; Artikl pro žíznivé zákazníky z mokré čtvrti
--;? 2; DECIMAL; Jednotková cena (dvě des. místa):; 2.23
--;? 3; GRAPHIC; Číslo zboží:; 2 
update CENY2 
   set NAZZBO = ?, CENAJ = ?
where CZBOZI = ?  ;

--Update CENY2 druhý
--;? 1; VARGRAPHIC; Název zboží: 002 *; Artikl pro žíznivé zákazníky z mokré čtvrti
--;? 2; DECIMAL; Jednotková cena (dvě des. místa):; 4.56
--;? 3; GRAPHIC; Číslo zboží:; 2 
update CENY2 
   set NAZZBO = ?, CENAJ = ?
where CZBOZI = ?
