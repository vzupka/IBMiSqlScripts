--Počet a součet obratů v závodech
SELECT DISTINCT S.ZAVOD, OBR.POCET, OBR.SOUCET                   
FROM STAVY S,                                                    
     TABLE (SELECT S.ZAVOD, COUNT(*) POCET, SUM(O.MNOBRATU) SOUCET
              FROM OBRATY O                                      
              WHERE S.ZAVOD = O.ZAVOD                            
           ) AS OBR                                          
WHERE S.ZAVOD = OBR.ZAVOD
