--Výpis souboru obratů OBRATY
--;T 0 ; 0 ;  ;
--;D #0.00
select MNOBRATU, ZAVOD, SKLAD from OBRATY
order by ZAVOD, SKLAD, CZBOZI
