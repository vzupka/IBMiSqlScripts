--EMPLOYEE selected list with title headers and omitted columns

--;? 1 ; DATE ; From date: ; 1925-01-01 ;
--;? 2 ; DATE ; To date  : ; 1940-01-01 ;
--;? 3 ; DEC ; Salary does not reach ; 30000.00

--;t    Employee number from the first line: &EMPNO , Birth date: &BIRTHDATE 
--;t

--;P a4 ; fs9; Landscape; lm5; rm30; tm5; bm5; -empno ; -birthdate

SELECT EMPNO, FIRSTNME, MIDINIT, LASTNAME, WORKDEPT, PHONENO, 
       HIREDATE, JOB, EDLEVEL, SEX, BIRTHDATE, SALARY, BONUS, COMM 
-- SELECT * 
   FROM EMPLOYEE
   WHERE BIRTHDATE BETWEEN ? AND ?
         AND SALARY <= ?
