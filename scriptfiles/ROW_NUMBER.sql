--Číslování řádků v závodu
 --;HZávod;Sklad;Množství ;Pořadové číslo;
 --;H     ;     ;na skladě;
 --;H=====;=====;=========;==============;
 --;H
--;D #0.00; Maska pro celá čísla a čísla s nenulovými des. místy
SELECT ZAVOD, SKLAD, MNOZSTVI, 
       ROW_NUMBER() OVER(PARTITION BY ZAVOD ORDER BY ZAVOD, SKLAD)  AS PORADI
FROM STAVY
   JOIN CENY USING (CZBOZI)
   ORDER BY ZAVOD, SKLAD
