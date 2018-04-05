--Výstup několika stránek s širokými řádky k testování tisku na papír

/* Zkouším různé kombinace papíru, orientace, písma a okrajů 
   Navíc zkouším vynechat zvolené sloupce z výstupu (a pochopitelně i tisku) */

--;HZávod;Sklad;    Poř.číslo;      Množství; 
--;H     ;     ;             ;     na skladě;
--;H=====;=====;=============;==============;
--;H


--   velikost       velikost písma  orientace    okraje------------------------ 
--   papíru A4, A3  (font size)     landscape/L  left     right   top    bottom 
--                                  portrait/P   margin   margin  margin margin   

 --;O  sklad; zavod 

--;P a4  ;           fs9;           L       ;   lm5   ;  rm5  ; tm5 ;  bm5 ;     

--;t     | Závod &zavod , Množství &mnozstvi  |

--;D  0000000000000 ; poradi


/*************************************************************/
/****  Tady vychází hodně širokých řádků k testování tisku ****/ 
/*************************************************************/

SELECT ST.ZAVOD, ST.SKLAD, 
DEC(ROW_NUMBER() OVER(), 13, 0) as PORADI,
MNOZSTVI 


FROM STAVY as ST
   CROSS JOIN OBRATY
   CROSS JOIN CENY
   ORDER BY PORADI
