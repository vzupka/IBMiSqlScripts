--Ceny objednávek mezi limity od určitého data 
--;? 01;DECIMAL; Cena od:; 5;
--;? 02;DEC; Cena do:; 23000
--;? 03;DATE; Od data:; 2014-01-29

--;HČ.obj.;Č.dod.;Datum obj.;Č. zboží;Obj. množství;Jedn. cena;Název zboží                              ;Název dodavatele;Adresa dodavatele;Cena celkem;
--;H------;------;----------;--------;-------------;----------;-----------------------------------------;----------------;-----------------;-----------;   

--;L0 ; CELKEM
--;L1 ; Č. obj. ; COBJ

--;S CENA_CELKEM ; S ; A

    select H.COBJ, H.CDOD, DTOBJ, D.CZBOZID, MNOBJ, CENAJ,         
             NAZZBO, NAZDOD, ADRDOD, (CENAJ * MNOBJ) as CENA_CELKEM 
     from OBJHLA as H                                               
     join OBJDET as D on  H.COBJ = D.COBJ                           
                     and  H.CDOD = D.CDOD                           
     join CENYD  as C on  D.CDOD    = C.CDOD                        
                     and  D.CZBOZID = C.CZBOZID                     
     join DODAV as DOD on H.CDOD = DOD.CDOD                         
     where (CENAJ * MNOBJ) between ? and ?                          
      and DTOBJ >= ?                                              
     order by COBJ   asc 
                                           
