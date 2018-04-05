--Zboží podle skladů a závodů
 --;HZav;Skl;.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
 --;H===;===;=======;===========;=======;========;   ===========;
 --;H

--;T B1; A1; ; S5; ZAVOD ; SKLAD 

--;D 0000.000; CJ
--;D  00; MNOZSTVI
--;D 00000000.00000; CELKEM

SELECT S.ZAVOD, S.SKLAD, S.CZBOZI, 
--   SUBSTR(C.NAZZBO, 1, 15) AS NAZ, 
   DEC(C.CENAJ, 12, 2) AS CJ, S.MNOZSTVI,
   DECIMAL ( SUM(C.CENAJ*S.MNOZSTVI), 12, 5 ) AS CELKEM
FROM STAVY AS S
   JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
   GROUP BY ROLLUP( S.ZAVOD, ( S.SKLAD, S.CZBOZI, C.CENAJ, S.MNOZSTVI ) )
   ORDER BY S.ZAVOD, S.SKLAD, S.CZBOZI
