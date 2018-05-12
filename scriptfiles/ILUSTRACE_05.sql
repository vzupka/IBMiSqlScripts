set = "KOLEKCE";
--Sumarizace zboží (CENY) podle skladů a závodů, závod na nové stránce

--;HZav;Skl;Č.zboží;Název zboží;Cena/j.;Množství;   Cena celkem;
--;H===;===;=======;===========;=======;========;   ===========;
--;H

--;T 1; 1; - ; 2; ZAVOD; SKLAD

--;S CELKEM  ; S ; C
--;s Součet ; Průměr ; Maximum ; Minimum ;  Počet 

--;L0;   .CELKEM ZA PODNIK
--;L1;   .Závod  &ZAVOD ;         ZAVOD; NP
--;L2;   .Sklad  &ZAVOD  &SKLAD ; SKLAD

--;P a4; fs12; L; lm5; rm5; tm5; bm5

SELECT 
   S.ZAVOD                                          -- Číslo závodu
   ,S.SKLAD                                         -- Číslo skladu
   ,S.CZBOZI                                        -- Číslo zboží
   ,SUBSTR(C.NAZZBO, 1, 15) AS NAZ                  -- Název zboží
   ,DEC(C.CENAJ, 12, 2) AS CJ                       -- Cena za jednotku
   ,S.MNOZSTVI                                      -- Množství jednotek
   ,DECIMAL ( C.CENAJ*S.MNOZSTVI, 12, 5 ) AS CELKEM -- Cena množství

FROM STAVY AS S
    JOIN CENY AS C ON S.CZBOZI = C.CZBOZI
ORDER BY S.ZAVOD, S.SKLAD, C.CZBOZI
