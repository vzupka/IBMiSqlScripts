--Stavy, součty za sklad a závod a celkem

 --;HČ.záv.;Č.skl.;Cena;Cena;Cena;Cena;
 --;H     ;      ;součet;průměr;minimum;maximum;
 --;H
  -- posuv před tiskem; posuv po tisku; značka pro null; skup. sl.
 --;T 1                ; 1             ;                ; ZAVOD    

 --;D 0000000.000; MNOZSTVI

SELECT S.ZAVOD, S.SKLAD,
--      MAX(S.CZBOZI),
      SUM(MNOZSTVI) as MNOZSTVI, 
      Decimal( AVG(( MNOZSTVI)) , 9, 3) as PRUMER_MNOZ,
      DECIMAL( AVG(C.CENAJ*S.MNOZSTVI), 9, 2 ) AS PRUMER_CEL,
      DECIMAL( SUM(C.CENAJ*S.MNOZSTVI), 9, 2 ) AS SOUCET,
--      INT( AVG(C.CENAJ*S.MNOZSTVI) ) as PRUMER,
      DEC( MIN( C.CENAJ*S.MNOZSTVI ), 9, 2 )  as MIN,

      INT( MAX( C.CENAJ*S.MNOZSTVI) ) as MAXIMUM
FROM STAVY AS S
   JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
   GROUP BY ROLLUP( S.ZAVOD, ( S.SKLAD, mnozstvi )  )
   ORDER BY S.ZAVOD, S.SKLAD
