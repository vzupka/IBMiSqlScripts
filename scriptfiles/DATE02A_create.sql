--Create new table DATE02A from query DATE02

--*;? 01;DECIMAL; Cena od:; 5
--*;? 02;DEC; Cena do:; 23000
--*;? 03;DATE; Od data:; 2014-01-29

DROP TABLE DATE02A;
CREATE TABLE VZSQL.DATE02A AS
(
    select H.COBJ, H.CDOD, DTOBJ, D.CZBOZID, MNOBJ, CENAJ,         
             NAZZBO, NAZDOD, ADRDOD, (CENAJ * MNOBJ) as CENA_CELKEM 
     from OBJHLA as H                                               
     join OBJDET as D on  H.COBJ = D.COBJ                           
                     and  H.CDOD = D.CDOD                           
     join CENYD  as C on  D.CDOD    = C.CDOD                        
                     and  D.CZBOZID = C.CZBOZID                     
     join DODAV as DOD on H.CDOD = DOD.CDOD                         
     where (CENAJ * MNOBJ) between 5 and 23000                          
      and DTOBJ <= '2014-01-29'                                              
     order by COBJ   asc                                            
) WITH DATA
