--Vytvořit objekty typu ALIAS pro členy tabulky CENY

/*--------------------------------------------------- 
   Vytvořím fyzický soubor CENY.
 ************************************************
 *   Soubor CENY - Ceník zboží                   
 ************************************************
A                                      UNIQUE    
A          R CENYR                               
 *   Číslo zboží                                 
A            CZBOZI         5A                   
 *   Cena za jednotku (kus)                      
A            CENAJ          9P 2                 
 *   Název zboží                                 
A            NAZZBO        50A         CCSID(870)
 *   Definice klíče - Číslo zboží                
A          K CZBOZI                              

   Vytvořím logický soubor CENYL.
 *************************************************
 *   Soubor CENYL                                  
 *   Logický soubor                               
 *************************************************
A          R CENYR                     PFILE(CENY)
A            CZBOZI                               
A            CENAJ                                
A          K CZBOZI                               

   V souboru CENY definuji maximální počet členů.
CHGPF      FILE(VZTOOL/CENY) MAXMBRS(12) 

   Odstraním standardní člen stejného jména jako má soubor (CENY).
RMVM       FILE(VZTOOL/CENY) MBR(CENY)

   Přidám tři členy k souboru CENY.
ADDPFM     FILE(VZTOOL/CENY) MBR(CENY_01)
ADDPFM     FILE(VZTOOL/CENY) MBR(CENY_02)
ADDPFM     FILE(VZTOOL/CENY) MBR(CENY_03)

   V logickém souboru CENYL definuji maximální počet členů.
CHGLF  FILE(CENYL) MAXMBRS(12)

   K logickému souboru CENYL přidám také tři členy.
ADDLFM FILE(VZTOOL/CENYL) MBR(CENYL_01) DTAMBRS((VZTOOL/CENY (CENY_01)))
ADDLFM FILE(VZTOOL/CENYL) MBR(CENYL_02) DTAMBRS((VZTOOL/CENY (CENY_02)))
ADDLFM FILE(VZTOOL/CENYL) MBR(CENYL_03) DTAMBRS((VZTOOL/CENY (CENY_03)))

-------------------------------------------------*/

/* Předem odstraním objekty ALIAS pro členy fyzického a logického souboru */
DROP ALIAS VZTOOL/CENY_01 ;
DROP ALIAS VZTOOL/CENY_02 ;
DROP ALIAS VZTOOL/CENY_03 ;
DROP ALIAS VZTOOL.CENYL_01 ;
DROP ALIAS VZTOOL.CENYL_02 ;
DROP ALIAS VZTOOL.CENYL_03 ;

/* Vytvořím objekty ALIAS pro členy souboru CENY */
CREATE ALIAS VZTOOL.CENY_01 FOR VZTOOL.CENY(CENY_01) ;
CREATE ALIAS VZTOOL.CENY_02 FOR VZTOOL.CENY(CENY_02) ;
CREATE ALIAS VZTOOL.CENY_03 FOR VZTOOL.CENY(CENY_03) ;

/* Vytvořím objekty ALIAS pro členy logického souboru CENYL */
CREATE ALIAS VZTOOL.CENYL_01 FOR VZTOOL.CENY(CENY_01) ;
CREATE ALIAS VZTOOL.CENYL_02 FOR VZTOOL.CENY(CENY_02) ;
CREATE ALIAS VZTOOL.CENYL_03 FOR VZTOOL.CENY(CENY_03) ;

/* Vrátím předvolené schema */
SET SCHEMA DEFAULT;

/* Vymažu záznamy ze členů souboru CENY */
DELETE FROM CENY_01 ;
DELETE FROM CENY_02 ;
DELETE FROM CENY_03 ;

/* Vložím záznamy do členů souboru CENY */

INSERT INTO CENY_01 values ('00001', 8.99, 'PIŠKOTY OPAVIA') ;
INSERT INTO CENY_01 values ('00002', 459.00, 'Zubní pasta Kalodont') ;
INSERT INTO CENY_01 values ('00003', 1.25, 'Prádelní šňůra') ;

INSERT INTO CENY_02 values ('00004', 10.50, 'Ponožky pánské tmavé') ;
INSERT INTO CENY_02 values ('00005', 120.00, 'Tričko bílé') ;
INSERT INTO CENY_02 values ('00006', 10.55, 'Ponožky pánské bílé, nové') 
