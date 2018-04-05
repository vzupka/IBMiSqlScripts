--Číslování řádků v celém podniku
 --;HZávod;Sklad;Množství ;Pořadové číslo;
 --;H     ;     ;na skladě;
 --;H=====;=====;=========;==============;
 --;H
--;D #0.00; Maska pro celá čísla a čísla s nenulovými des. místy
SELECT ZAVOD, SKLAD, MNOZSTVI, 
       SMALLINT(ROW_NUMBER() OVER() ) AS P
FROM STAVY
   JOIN CENY USING (CZBOZI)
   ORDER BY ZAVOD, SKLAD
