--Sumarizace zboží podle skladů a závodů

--;t  >>> Závod 4x:    &ZAVOD , &ZAVOD , &ZAVOD , &ZAVOD .                 <<<
--;t  >>> Název zboží: &NAZ ,  Sklad: &SKLAD . <<<
--;t

--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
--;H===;===;=======;===========;=======;========;   ===========;
--;H

--;T 1; 1; - ; 2 


 --;O NAZ; 

--;S SKLAD   ; M; m; C
--;S CZBOZI  ; M; m; C
--;S NAZ  ; M; m; C;
--;S mnozstvi; C ; S; M; m; A 
--;S CELKEM  ;  A ; m ; S ; M ; C

--;s Součet ; Průměr ; Maximum ; Minimum ;  Počet 

--;L0;   .Celkem ****************; za případným středníkem jen komentář
--;L ;   .Závod =====&ZAVOD ==========; ZAVOD
--;L ;   .Sklad -----&zavod --&sklad ------; SKLAD

--;L3;  .Číslo zboží &czbozi ^^^^^^^; CZBOZI

--;P a4; fs12; P; 10; 10; 10; 10

SELECT 
   S.ZAVOD, 
   S.SKLAD, 
   S.CZBOZI, 
   SUBSTR(C.NAZZBO, 1, 15) AS NAZ, 
   BIGINT(C.CENAJ) AS CJ, 
   S.MNOZSTVI,
 --;D #0.000; Mnozstvi
   INTEGER ( C.CENAJ*S.MNOZSTVI ) AS CELKEM

FROM ZBOZI_BIN AS S
   full JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
   ORDER BY S.ZAVOD, S.SKLAD, S.CZBOZI asc
