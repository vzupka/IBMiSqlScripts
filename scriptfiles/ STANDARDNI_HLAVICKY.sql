--Standardní hlavičky

SELECT S.ZAVOD, S.SKLAD,     
      DECIMAL( SUM(C.CENAJ*S.MNOZSTVI), 9, 2 ) AS "Součet",
      INT( AVG(C.CENAJ*S.MNOZSTVI) ) AS "Průměr",
      INT( MIN( C.CENAJ*S.MNOZSTVI) ) AS MIN,
      INT( MAX( C.CENAJ*S.MNOZSTVI) ) AS MAXIMUM
FROM STAVY AS S
   JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
   GROUP BY ROLLUP( S.ZAVOD, S.SKLAD )
   ORDER BY S.ZAVOD, S.SKLAD