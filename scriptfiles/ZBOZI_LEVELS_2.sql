--Sumarizace zboží (CENY) podle skladů a závodů

  --;? 1; ; Zboží větší než: ; 00000

--;t  >>> Závod 4x:    &ZAVOD , &ZAVOD , &ZAVOD , &ZAVOD .              <<<
--;t  >>> Název zboží: &NAZ ,  Sklad: &SKLAD . <<<
--;t

--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
--;H===;===;=======;===========;=======;========;   ===========;
--;H

--;T b1; a1; - ; s2; ZAVOD; SKLAD

 --;O ZAVOD
--;O NAZ; 
 --;O SKLAD;

--;S CELKEM  ; S ; A; M; m; C
--;S mnozstvi; S ; A; M; m; C
--;S naz     ; M; m; C
--;S czbozi  ; M; m; C

--;s Součet ; Průměr ; Maximum ; Minimum ;  Počet 

--;L0;   .Celkem ****************
--;L1;   .Závod =====&ZAVOD ==========;      ZAVOD 
--;L2;   .Sklad -----&zavod --&sklad ------; SKLAD

   --;L3;  .Číslo zboží &czbozi ^^^^^^^; CZBOZI

--;P a4; fs14; L; lm5; rm5; tm5; bm5

SELECT 
   S.ZAVOD, 
   S.SKLAD, 
   S.CZBOZI, 
   SUBSTR(C.NAZZBO, 1, 15) AS NAZ, 
   DEC(C.CENAJ, 12, 2) AS CJ, 
   S.MNOZSTVI,
 --;D #0.000; Mnozstvi
   DECIMAL ( C.CENAJ*S.MNOZSTVI, 12, 5 ) AS CELKEM

FROM STAVY AS S
    FULL JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
--  WHERE S.CZBOZI >= ?
ORDER BY S.ZAVOD, S.SKLAD , C.CZBOZI desc
