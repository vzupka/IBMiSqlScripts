--Create ALIAS objects for file members

/*--------------------------------------------------- 
   Create physical file PRICES.
 ********************************************
 *   File PRICES - Item prices               
 ********************************************
A                                      UNIQUE
A          R PRICESR                         
 *   Item number                             
A            ITEMNBR        5A               
 *   Unit price                              
A            UNITPR         9P 2             
 *   Item description                        
A            DESCR         50A               
 *   Key - Item number                       
A          K ITEMNBR                         

   Create logical file PRICESL.
 ***************************************************
 *   File PRICESL                                   
 *   Logical file                                   
 ***************************************************
A          R PRICESR                   PFILE(PRICES)
A            ITEMNBR                                
A            UNITPR                                 
A          K ITEMNBR                                

   Define maximum number of members in file PRICES.
CHGPF      FILE(VZTOOL/PRICES) MAXMBRS(12)  

   Add three members to physical file PRICES                                             
ADDPFM     FILE(VZTOOL/PRICES) MBR(PRICES_01)
ADDPFM     FILE(VZTOOL/PRICES) MBR(PRICES_02)
ADDPFM     FILE(VZTOOL/PRICES) MBR(PRICES_03)

   Define maximum number of members in logical file PRICESL.
CHGLF  FILE(PRICESL) MAXMBRS(12)                                                

   Remove standard member from file PRICES.
RMVM       FILE(VZTOOL/PRICES) MBR(PRICES)

   Add three members also to logical file PRICESL.
ADDLFM FILE(VZTOOL/PRICESL) MBR(PRICESL_01) DTAMBRS((VZTOOL/PRICES(PRICES_01)))
ADDLFM FILE(VZTOOL/PRICESL) MBR(PRICESL_02) DTAMBRS((VZTOOL/PRICES(PRICES_02)))
ADDLFM FILE(VZTOOL/PRICESL) MBR(PRICESL_03) DTAMBRS((VZTOOL/PRICES(PRICES_03)))

-------------------------------------------------*/

/* Remove alias objects for the physical and logical file. */
DROP ALIAS VZTOOL/PRICES_01 ;
DROP ALIAS VZTOOL/PRICES_02 ;
DROP ALIAS VZTOOL/PRICES_03 ;
DROP ALIAS VZTOOL.PRICESL_01 ;
DROP ALIAS VZTOOL.PRICESL_02 ;
DROP ALIAS VZTOOL.PRICESL_03 ;

/* Create alias objects for members of physical file PRICES. */
CREATE ALIAS VZTOOL.PRICES_01 FOR VZTOOL.PRICES(PRICES_01) ;
CREATE ALIAS VZTOOL.PRICES_02 FOR VZTOOL.PRICES(PRICES_02) ;
CREATE ALIAS VZTOOL.PRICES_03 FOR VZTOOL.PRICES(PRICES_03) ;

/* Create alias objects for members of logical file PRICESL. */
CREATE ALIAS VZTOOL.PRICESL_01 FOR VZTOOL.PRICESL(PRICESL_01) ;
CREATE ALIAS VZTOOL.PRICESL_02 FOR VZTOOL.PRICESL(PRICESL_02) ;
CREATE ALIAS VZTOOL.PRICESL_03 FOR VZTOOL.PRICESL(PRICESL_03) ;

/* Delet records from physical file PRICES. */
DELETE FROM PRICES_01 ;
DELETE FROM PRICES_02 ;
DELETE FROM PRICES_03 ;

/* Insert records to two members of physical file PRICES. */
INSERT INTO PRICES_01 values ('00001', 8.99, 'Chocolate cakes') ;
INSERT INTO PRICES_01 values ('00002', 459.00, 'Tooth paste Kalodont') ;
INSERT INTO PRICES_01 values ('00003', 1.25, 'Washing line') ;

INSERT INTO PRICES_02 values ('00004', 10.50, 'Men''s socks black') ;
INSERT INTO PRICES_02 values ('00005', 120.00, 'T-shirt white') ;
INSERT INTO PRICES_02 values ('00006', 10.55, 'Men''s socks white, new') 
