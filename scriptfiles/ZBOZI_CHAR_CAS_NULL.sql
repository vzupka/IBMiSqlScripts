--Sumarizace zboží (CENY2) podle skladů a závodů - DEC, CHAR, DATE, TIME, TIMESTAMP

--;T 1; 1; ### ; 2; ZAVOD; SKLAD

--;S ZAVOD    ;   ;   ; M ; m ; C
--;S SKLAD    ;   ;   ; M ; m ; C
--;S CZBOZI   ;   ;   ; M ; m ; C
--;S CJ       ; S ; A ; M ; m ; C
--;S MNOZSTVI ; S ; A ; M ; m ; C
--;S CELKEM   ; S ; A ; M ; m ; C
--;S DATUM    ;   ;   ; M ; m ; C
--;S CAS      ;   ;   ; M ; m ; C
--;S RAZITKO  ;   ;   ; M ; m ; C
--;S NAZZBO   ;   ;   ; M ; m ; C

--;s Součet: ; Průměr: ; Maximum: ; Minimum: ;  Počet: 

--;L0;   .Celkem ****************
--;L1;   .Závod =====&ZAVOD ==========;      ZAVOD ; NP
--;L2;   .Sklad -----&ZAVOD --&SKLAD ------; SKLAD

SELECT 
   S.ZAVOD, 
   S.SKLAD, 
   S.CZBOZI,
   DEC(C.CENAJ, 12, 2) AS CJ, 
   S.MNOZSTVI,
   DECIMAL ( C.CENAJ*S.MNOZSTVI, 12, 2 ) AS CELKEM,
   DATUM,
   CAS,
   RAZITKO,
   NAZZBO 

FROM STAVY AS S   
   full JOIN CENY2 AS C ON S.CZBOZI = C.CZBOZI
   ORDER BY S.ZAVOD, S.SKLAD , C.CZBOZI desc
