--Zboží podle skladů a závodů - vzorový příklad

/* 
   V parametrech --;P zadám různé kombinace papíru, orientace, písma a okrajů. 
   Sloupcové hlavičky se ale musí zapsat VŠECHNY v tom pořadí, jak by se tiskly,
   kdyby se žádné sloupce nevynechávaly.

   V parametrech --t; tisknu titulní hlavičky s možností zařadit hodnoty vynechaných sloupců
   z PRVNÍHO řádku. Titulní hlavičky se tisknou jen na první stránce ještě před 
   sloupcovými hlavičkami. To, že jsou tyto parametry zapsány níže, nevadí.
 */


--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
--;H===;===;=======;===========;=======;========;   ===========;
--;H


/* V parametrech --;T jsem přidal 4. hodnotu - počet mezer oddělujících sloupce. 
   Nahrazuje předvolenou hodnotu z parametrů aplikace.   
*/
--                 počet mezer mezi sloupci od nuly výše
--;T 1 ; 1 ;  ;    3

/* Parametry pro tisk                                */
/* --------------------------------------------------*/
/* Jména vynechaných sloupců s mínusem i bez něj */

--   velikost       velikost písma  orientace    okraje v mm ------------   vynechané sloupce
--   papíru   font size       landscape/L  left     right   top    bottom   (omitted columns)
--   A4 / A3                  portrait/P   margin   margin  margin margin   

--;P A4  ;           fs9;     Landscape;   lm5   ;  rm30  ; tm5 ;  bm5


/* Titulní řádky - ještě před sloupcovými hlavičkami */
/* ------------------------------------------------- */ 
--   Jména proměnných (vynechaných sloupců) začínají znakem ampersand (&) a končí středníkem (;).*/
/*   Lze zařadit jen jména vynechaných sloupců, a to nepovinně a třeba ne všechna a ani 
     nemusí být ve stejném řádku. */

--;t   Závod: &zavod , Celková cena: &celkem
--;t   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
--;t   Celková cena znovu: &celkem 
--;t

--;O zavod; celkem

SELECT S.ZAVOD, S.SKLAD, S.CZBOZI, C.NAZZBO, 
   DEC(C.CENAJ, 12, 2) AS CJ,   S.MNOZSTVI,
   DECIMAL ( SUM(C.CENAJ*S.MNOZSTVI), 12, 5 ) AS CELKEM
FROM STAVY AS S
   JOIN CENY AS C ON C.CZBOZI = S.CZBOZI
   GROUP BY ROLLUP( (S.ZAVOD, S.SKLAD, S.CZBOZI, C.NAZZBO, C.CENAJ, S.MNOZSTVI  ) )
   ORDER BY S.ZAVOD, S.SKLAD, S.CZBOZI



/******** 
Vytiskne se následující text:

Zboží podle skladů a závodů

Pondělí, 23. listopadu 2015 14:53:17

   Závod: 01, Celková cena: 8,99000
   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
   Celková cena znovu: 8,99000 
 
Skl   Č.zboží   Název zboží                             Cena/j.      Množství   
===   =======   ===========                             =======      ========   
                                                                                
01    00001     PIŠKOTY OPAVIA                             8,99          1,00   
01    00002     Zubní pasta Kalodont                     459,00          1,00   
01    00010     Koňak Gruzínský                         6500,00          1,00   
02    00003     Prádelní šňůra                             1,25          1,00   
02    00009     Whisky Balantine                         250,00          1,00   
02    00010     Koňak Gruzínský                         6500,00          1,00   
03    00003     Prádelní šňůra                             1,25          1,00   
01    00005     Tričko bílé                              120,00          2,00   
01    00006     Ponožky pánské bílé, nové                 10,55          2,00   
01    00008     Kalhoty džínové                         1700,00          2,00   
02    00009     Whisky Balantine                         250,00          2,00   
02    00011     Taška sportovní                          159,00          2,00   
02    00014     Sako tvídové, nadměr                    3500,00          2,00   
02    00018     Husí sádlo v konzervě                     56,00          2,00   


********/
