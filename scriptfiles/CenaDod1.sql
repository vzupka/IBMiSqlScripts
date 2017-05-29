--Ceny, počty a průměry dodávky podle objednávek, dodavatelů a zboží
--;? 01; DATE;Datum 1:; 2000-01-01;
--;? 02; DATE; Datum 2:; 2015-01-07;


--;HČ.obj.;Číslo;Název dodavatele;Název zboží;Č.zboží; ; Počet; Suma; Průměr;
--;H      ;dod. ;                ;           ;dod.
--;H
--;D#0.0000;PRUMER

--;T 0; 1; ; COBJ;CDOD;nazevdod
---   Tady se uměle sčítá i za číslo dodavatele, aby se mohlo tisknout
--- číslo objednávky. Když nechceme tisknout sloupec pro dodavatele, 
--- necháme tam prázdný znak a v hlavičce vynecháme zápis. 
---   Kdybychom chtěli tisknout i počet dodavatelů u objednávky,
--- můžeme zadat počet dodavatelů a tisknout i hlavičku 

select DISTINCT 
       H.COBJ, 
       D.CDOD,
       SUBSTR(DOD.NAZDOD, 1, 15) NAZEVDOD,
       C.NAZZBO,
--       D.CZBOZID,
       '',
       SMALLINT (COUNT(D.CDOD)) pocet, 
       DECIMAL( SUM(C.CENAJ * D.MNOBJ), 9, 2 ) suma,
       DECIMAL( AVG(C.CENAJ * D.MNOBJ), 12, 5 ) prumer      
   from OBJHLA  as H                               
   join OBJDET  as D   on H.COBJ    = D.COBJ   
   join CENYD_T as C   on D.CZBOZID = C.CZBOZID      
                      and D.CDOD    = C.CDOD   
   join DODAV_T as DOD on DOD.CDOD    = C.CDOD
   join DPH_T   as DPH on C.SAZBA_DPH = DPH.SAZBA_DPH
where DTOBJ between ? and ?   
group by ROLLUP (  H.COBJ , D.CDOD, DOD.NAZDOD, C.NAZZBO  )
 --group by ROLLUP (  H.COBJ , D.CDOD, C.NAZZBO, D.CZBOZID, '')
order by H.COBJ, D.CDOD, C.NAZZBO
