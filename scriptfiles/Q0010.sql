--Součty cen větších než zadaná cena v rozmezí čisel zboží
--;? 01; DEC; Cena větší než:; 52.5
--;? 02; CHAR; Číslo zboží od:; 00000
--;? 03; CHAR; Číslo zboží do:; 00999

--;HZávod; Číslo zboží; Celkem
--;H-----; -----------; ------

SELECT S.ZAVOD, S.CZBOZI,
      DECIMAL( SUM(C.CENAJ*S.MNOZSTVI), 9, 2 ) AS CELKEM
FROM STAVY AS S
   INNER JOIN CENY AS C ON S.CZBOZI = C.CZBOZI 
   WHERE C.CENAJ*S.MNOZSTVI > ?
   GROUP BY S.ZAVOD, S.CZBOZI
   HAVING S.CZBOZI BETWEEN ? AND ? 
   ORDER BY S.ZAVOD, S.CZBOZI
