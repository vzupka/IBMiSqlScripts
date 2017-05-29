--Cena dodávky podle objednávek, dodavatelů a zboží
--;? 01; DATE;Datum 1:; 2000-01-01;
--;? 02; DATE; Datum 2:; 2015-01-07;

--HČ.obj;Dodav.;Zboží;Cena;Průměr
--   HČ.obj,,Cena,Průměr

---   Tady se uměle sčítá i za číslo dodavatele, aby se mohlo tisknout
--- číslo objednávky. Když nechceme tisknout sloupec pro dodavatele, 
--- necháme tam prázdný znak a v hlavičce vynecháme zápis. 
---   Kdybychom chtěli tisknout i počet dodavatelů u objednávky,
--- můžeme zadat počet dodavatelů a tisknout i hlavičku 

--;T 0; 1; ; COBJ; CDOD

select DISTINCT 
       H.COBJ, 
       D.CDOD,
       C.NAZZBO,
--       D.CZBOZID,
--       '',
--       COUNT(D.CDOD),
       DECIMAL( SUM(C.CENAJ * D.MNOBJ), 9, 2 ),
       DECIMAL( AVG(C.CENAJ * D.MNOBJ), 9, 2 )       
   from OBJHLA  as H                               
   join OBJDET  as D on H.COBJ    = D.COBJ         
   join CENYD_T as C on D.CZBOZID = C.CZBOZID      
                    and D.CDOD = C.CDOD            
   join DPH_T as DPH on C.SAZBA_DPH = DPH.SAZBA_DPH
where DTOBJ between ? and ?   
group by ROLLUP (  H.COBJ , D.CDOD, C.NAZZBO  )
order by H.COBJ

-- Cena dodávky podle objednávek
--  HČ.obj,Cena,Množství,%DPH,Datum obj.
--select distinct H.COBJ,  CENAJ, MNOBJ, PROC_DPH, DTOBJ                 
--   from OBJHLA  as H                               
--   join OBJDET  as D on H.COBJ    = D.COBJ         
--   join CENYD_T as C on D.CZBOZID = C.CZBOZID      
--                    and D.CDOD = C.CDOD            
--   join DPH_T as DPH on C.SAZBA_DPH = DPH.SAZBA_DPH
--where DTOBJ between ? and ?   
--order by H.COBJ
