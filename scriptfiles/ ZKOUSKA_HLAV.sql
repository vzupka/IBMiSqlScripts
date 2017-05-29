--Zkouška hlaviček


--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;Datum;Čas;Razítko
--;H===;===;=======;===========;=======;========;   ===========;=====;===;=======
--;H

--;T 1; 1;  ---  ; 1; 

--;O ZAVOD
 --;O SKLAD
 --;O CZBOZI
 --;O NAZZBO; 

--;? 1 ; CHAR ; Číslo zboží ; 00000


--;P a4; fs11; PORTRAIT ; lm1; rm5; tm5; bm5

SELECT 
   S.ZAVOD, 
   S.SKLAD, 
   S.CZBOZI,
   NAZZBO, 
   DEC(C.CENAJ, 12, 2) AS CJ, 
   S.MNOZSTVI,
 --;D #0.000; Mnozstvi
   DECIMAL ( C.CENAJ*S.MNOZSTVI, 12, 5 ) AS CELKEM,
   DATUM,
   CAS,
   RAZITKO



--   SUBSTR(C.NAZZBO, 1, 15) as nazzbo

FROM STAVY AS S   
   JOIN CENY2 AS C ON S.CZBOZI = C.CZBOZI
where S.CZBOZI >= ?
   ORDER BY S.ZAVOD, S.SKLAD , C.CZBOZI asc
