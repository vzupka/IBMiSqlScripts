--Sumarizace zboží (CENY4) podle skladů a závodů - UTF-8

 --;? 1; ; Zboží větší než: ; 00010

--;t  §§§ Závod 4x:    &ZAVOD , &ZAVOD , &ZAVOD , &ZAVOD .              §§§
--;t  §§§ Název zboží: &NAZ ,  Sklad: &SKLAD . §§§
--;t

--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
--;H===;===;=======;===========;=======;========;   ===========;
--;H

--;T 1; 1; -  ; 2

--;O ZAVOD
--;O NAZ; 
--;O SKLAD;

--;S naz     ; M; m; C
--;S mnozstvi; S ; A; M; m; C
--;S CELKEM   ; S ; A; M; m; C
--;S ZAVOD    ; M; m; C  
--;S SKLAD    ; M; m; C 
--;S CZBOZI   ; M; m; C 

 --;S CELKEM ; S

 --;s Součet ; Průměr ; Maximum ; Minimum ;  Počet 

--;L0;   .Celkem ****************
--;L1;   .Závod =====&ZAVOD ==========;      ZAVOD ; NP 
--;L2;   .Sklad -----&zavod --&sklad ------; SKLAD ; NP

   --;L3;  .Číslo zboží &czbozi ^^^^^^^; CZBOZI

--;P a4; fs9; L; lm5; rm5; tm5; bm5

SELECT 
   S.ZAVOD, 
   S.SKLAD, 
   S.CZBOZI, 
--   SUBSTR(C.NAZZBO, 1, 15) AS NAZ, 
   C.NAZZBO as NAZ,
   DEC(C.CENAJ, 12, 2) AS CJ, 
   S.MNOZSTVI,
 --;D #0.000; Mnozstvi
   DECIMAL ( C.CENAJ*S.MNOZSTVI, 12, 5 ) AS CELKEM

FROM STAVY AS S
   JOIN CENY4 AS C ON S.CZBOZI = C.CZBOZI
 --WHERE S.CZBOZI <= ?
ORDER BY S.ZAVOD, S.SKLAD , C.CZBOZI desc
