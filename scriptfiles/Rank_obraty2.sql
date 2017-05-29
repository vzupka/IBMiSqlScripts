--Zboží hodnocené podle níže obratu
--;D #0.00
SELECT DISTINCT S.ZAVOD, S.SKLAD, C.CZBOZI, C.CENAJ, O.MNOBRATU, 
                O.MNOBRATU * C.CENAJ AS CENA,
                DENSE_RANK() OVER(ORDER BY O.MNOBRATU * C.CENAJ) AS RANK
FROM CENY C 
  JOIN STAVY S ON C. CZBOZI = S. CZBOZI 
  JOIN OBRATY O ON S. CZBOZI = O. CZBOZI
ORDER BY RANK