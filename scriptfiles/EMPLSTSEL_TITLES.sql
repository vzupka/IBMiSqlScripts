--EMPLOYEE selected list with title headers

--;t    Employee number from the first line: &EMPNO , Birth date: &BIRTHDATE 
--;t

--;? 1 ; DATE ; From date: ; 1925-01-01 ;
--;? 2 ; DATE ; To date  : ; 1940-01-01 ;
--;? 3 ; DEC  ; Salary does not reach ; 30000.00

SELECT EMPNO, 
   FIRSTNME, 
--   MIDINIT, 
   LASTNAME, 
--   WORKDEPT, 
--   PHONENO,       
--   HIREDATE, 
--   JOB, 
--   EDLEVEL, 
--   SEX, 
   BIRTHDATE, 
   SALARY, 
--   BONUS, 
   COMM 
FROM EMPLOYEE
   WHERE BIRTHDATE BETWEEN ? AND ?
         AND SALARY <= ?
