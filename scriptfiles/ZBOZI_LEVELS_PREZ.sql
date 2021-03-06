--Sumarizace zboží (CENY) podle skladů a závodů

/* Titulní hlavičky - tisknou se jen na začátku */
--;t  >>> Závod 4x:    &ZAVOD , &ZAVOD , &ZAVOD , &ZAVOD .              <<<
--;t  >>> Název zboží: &NAZ ,  Sklad: &SKLAD . <<<
--;t

/* Sloupcové hlavičky - nadpisy sloupců */
--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;Cena celkem;
--;H===;===;=======;===========;=======;========;===========;
--;H

/* Úprava svislého a vodorovného členění */
--;T b1; a1; -; s2; ZAVOD; SKLAD

/* Vynechané sloupce */
--;O ZAVOD

/* Střádané sloupce */
--;S CELKEM  ; S ; A; M; m; C
--;S mnozstvi; S ; A; M; m; C
--;S naz     ; M; m; C
--;S czbozi  ; M; m; C

/* Označení způsobu střádání */
--;s Součet ; Průměr ; Maximum ; Minimum ;  Počet 

/* Definice skupinových úrovní */
--;L0;   .Celkem ****************
--;L1;   .Závod =====&ZAVOD ==========;      ZAVOD ; NP
--;L2;   .Sklad -----&zavod --&sklad ------; SKLAD

/* Parametry pro tisk */
--;P a4; fs14; L; lm5; rm5; tm5; bm5

/* SQL příkaz */
SELECT 
   S.ZAVOD, 
   S.SKLAD, 
   S.CZBOZI, 
   SUBSTR(C.NAZZBO, 1, 15) AS NAZ, 
   DEC(C.CENAJ, 12, 2) AS CJ, 
   S.MNOZSTVI,
   DECIMAL ( C.CENAJ*S.MNOZSTVI, 12, 5 ) AS CELKEM
FROM STAVY AS S
   FULL JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
ORDER BY S.ZAVOD, S.SKLAD, C.CZBOZI
