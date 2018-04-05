--EMPLOYEE selected list with multiline headers

--;HEmployee;First       ;Mid. ;Last          ;Work;Phone ;Hire     ;Job      ;Ed.;Sex;Birth     ;  Salary;Bonus;Commission
--;Hnumber  ;name        ;init.;name          ;dept;number;date     ;         ;lv.;   ;date      ;        ;     ;   
--;H--------;------------;-----;--------------;----;------;---------;---------;---;---;----------;--------;-----;----------
--;H

--;? 1 ; DATE ; From date: ; 1925-01-01 ;
--;? 2 ; DATE ; To date  : ; 1940-01-01 ;
--;? 3 ; DEC  ; Salary does not reach ; 30000.00

SELECT * FROM EMPLOYEE
WHERE BIRTHDATE BETWEEN ? AND ?
      AND SALARY <= ?
