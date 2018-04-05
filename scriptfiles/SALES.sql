--Sales summarized by person and region

--;HSales date;Sales person;Region;Sales
--;H----------;------------;------;-----
--;H

--;T 1; 1; null ;  ; SALES_PERSON; REGION 

--;L0 ;*** GRAND TOTAL ***;
--;L1 ;=== &SALES_PERSON  ===         ; SALES_PERSON ; NP
--;L2 ;--- &SALES_PERSON  --- &REGION ; REGION       ; 

--;S SALES ; S
--;S SALES ; S ; A ; M ; m ; C 

 --;s Sum of sales:
--;s Sum of sales: ; Average: ; Maximum: ; Minimum: ;  Work shifts:


 --;O REGION 
 --;O SALES_DATE
 --;O SALES_PERSON

SELECT  SALES_DATE , SALES_PERSON , REGION , SALES
FROM SALES
ORDER BY 
    SALES_PERSON , REGION , SALES_DATE
