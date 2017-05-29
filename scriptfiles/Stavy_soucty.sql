--Stavy, součty za sklad a závod a celkem

--;HČ.záv.;Č.skl.;Cena;Cena;Cena;Cena;
--;H      ;      ;součet;průměr;minimum;maximum;
--;H
  -- posuv před tiskem; posuv po tisku; značka pro null;
--;T 0                ; 1             ;  -            ; ZAVOD ; 

 --;D 0000000.000
SELECT S.ZAVOD, S.SKLAD,     
      DECIMAL( SUM(C.CENAJ*S.MNOZSTVI), 9, 2 ) AS SOUCET,
      DEC( AVG(C.CENAJ*S.MNOZSTVI), 9, 2 ) as PRUMER,
      INT( MIN( C.CENAJ*S.MNOZSTVI) ) as MIN,
      INT( MAX( C.CENAJ*S.MNOZSTVI) ) as MAXIMUM
FROM STAVY AS S
   JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
   GROUP BY ROLLUP( S.ZAVOD, S.SKLAD )
   ORDER BY S.ZAVOD, S.SKLAD
