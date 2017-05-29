--Stavy, detail po závodech a skladech součtovaný

--;S  MNOZSTVI; S


--;L 0 ; Celkem
--;L 1 ; Součet za závod č. &zavod ----; ZAVOD; 
--;L 2 ; Součet za sklad č. &sklad     ; SKLAD; 


select ZAVOD, SKLAD, CZBOZI, MNOZSTVI 
from ZBOZI_BIN
order by ZAVOD, SKLAD, CZBOZI
