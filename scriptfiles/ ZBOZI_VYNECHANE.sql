--Zboží podle skladů a závodů - vynechané sloupce a proměnné v titulcích

--;t   Závod z prvního řádku: &ZAVOD , Celková cena z prvního řádku: &CELKEM , 
--;t   ~~~~~~~~~~~~~~~~~~~~~~     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--;t   Celková cena znovu: &Celkem
--;t

--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
--;H===;===;=======;===========;=======;========;   ===========;
--;H

/* Tři mezery mezi sloupci */
--;T  ;  ;  ; 3   
 
/* Dva vynechané sloupce */
--;O zavod ; 
--;O celkem

/* Parametry pro tisk */
--;P a4; fs9; Landscape; lm5; rm30; tm5; bm5;    

SELECT S.ZAVOD, S.SKLAD, S.CZBOZI, C.NAZZBO, 
   DEC(C.CENAJ, 12, 2) AS CJ,   S.MNOZSTVI,
   DECIMAL (C.CENAJ*S.MNOZSTVI, 12, 2 ) AS CELKEM
FROM STAVY AS S
   JOIN CENY AS C ON C.CZBOZI = S.CZBOZI
   WHERE S.ZAVOD = '02' AND S.CZBOZI <> '00002'
   ORDER BY S.SKLAD desc, S.CZBOZI
