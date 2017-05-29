--Ceny objednávek mezi limity od určitého data - zkouška na datum

 --;HČ.obj.;Č.dod.;Datum obj.;Č. zboží;Obj. množství;Jedn. cena;Název zboží                              ;Název dodavatele;Adresa dodavatele;Cena celkem;
 --;H------;------;----------;--------;-------------;----------;-----------------------------------------;----------------;-----------------;-----------;   
--;T 1; 1 ; 3
--;L0 ; CELKEM
--;L1 ; ZA ZBOŽÍ ; D.CZBOZID

--;S DTOBJ ; M ; m ; C
--;S CENA_CELKEM ; S ; A ; C


    select 
        10000,
	DTOBJ, 
--      H.COBJ, 
--      H.CDOD, 

        D.CZBOZID, 
	(CENAJ * MNOBJ) as CENA_CELKEM 
     from OBJHLA as H                                               
     join OBJDET as D on  H.COBJ = D.COBJ                           
                     and  H.CDOD = D.CDOD                           
     join CENYD  as C on  D.CDOD    = C.CDOD                        
                     and  D.CZBOZID = C.CZBOZID                     
                                     
     order by D.CZBOZID 
