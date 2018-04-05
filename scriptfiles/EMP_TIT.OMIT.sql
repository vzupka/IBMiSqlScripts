--EMPLOYEE selected list with title headers and omitted columns

--;? 1 ; DATE ; From date: ; 1925-01-01 ;
--;? 2 ; DATE ; To date  : ; 1940-01-01 ;
--;? 3 ; DEC ; Salary does not reach ; 30000.00

--;tEmployee number: &EMPNO , Bonus: &BONUS
--;tBirthdate: &BIRTHDATE
--;t

--;O EMPNO ; MIDINIT; 
--;O BONUS; BIRTHDATE

SELECT EMPNO, FIRSTNME, MIDINIT, LASTNAME, 
       BIRTHDATE, SALARY, BONUS, COMM 
   FROM EMPLOYEE
   WHERE BIRTHDATE BETWEEN ? AND ?
         AND SALARY <= ?
