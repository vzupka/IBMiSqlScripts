--Cena dodávky - detaily podle objednávek
--;? 01; DATE; Od data:; 2010-01-01; 
--;? 02; DATE; Do data:; 2016-01-01;
--;HČ.obj;Cena;Množství;%DPH;Datum obj.;
--;H-----;----;--------;----;----------;

select distinct H.COBJ,  CENAJ, MNOBJ, PROC_DPH, DTOBJ                 
   from OBJHLA  as H                               
   join OBJDET  as D on H.COBJ    = D.COBJ         
   join CENYD_T as C on D.CZBOZID = C.CZBOZID      
                    and D.CDOD = C.CDOD            
   join DPH_T as DPH on C.SAZBA_DPH = DPH.SAZBA_DPH
where DTOBJ between ? and ?   
order by H.COBJ
