--Timestamp

UPDATE CENY2 SET RAZITKO = CURRENT_TIMESTAMP,
                 DATUM = CURRENT_DATE,
                 CAS = CURRENT_TIME
WHERE CZBOZI = '00007'
