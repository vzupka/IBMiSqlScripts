--Vybírá řádky jako INNER JOIN a přidává řádky z levé tabulky, které nemají v pravé tabulce párový řádek. 
-- Sloupce z pravé tabulky mají hodnotu NULL.
--;? 01; CHAR; Číslo zboží; 00000;

SELECT DISTINCT C.CZBOZI, C.NAZZBO, S.CZBOZI, O.CZBOZI Z_OBRATU
FROM CENY C
   INNER JOIN STAVY S ON C. CZBOZI = S. CZBOZI
   LEFT JOIN OBRATY O ON S. CZBOZI = O. CZBOZI
WHERE C.CZBOZI >= ?
