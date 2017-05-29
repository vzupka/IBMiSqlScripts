--Zboží hodnocené podle výše obratu
--;D #0.00
SELECT DISTINCT S.ZAVOD, S.SKLAD, C.CZBOZI, C.CENAJ, O.MNOBRATU, 
                O.MNOBRATU * C.CENAJ AS CENA,
                RANK() OVER(ORDER BY O.MNOBRATU * C.CENAJ DESC) AS RANK
FROM CENY C 
  JOIN STAVY S ON C. CZBOZI = S. CZBOZI 
  JOIN OBRATY O ON S. CZBOZI = O. CZBOZI
ORDER BY RANK
