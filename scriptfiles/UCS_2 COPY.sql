--Copy table CENY2 to CENY3

DROP TABLE VZTOOL.CENY3;

CREATE TABLE VZTOOL.CENY3 LIKE CENY2;

/* Příkaz měnící CCSID ve sloupci se neprovede! Odpovídá automaticky 
   na výzvu k potvrzení či zamítnutí zamítnutím. */
ALTER TABLE CENY3 ALTER COLUMN NAZZBO SET DATA TYPE CHAR(100) CCSID 870;

ALTER TABLE CENY3 ADD PRIMARY KEY (CZBOZI);

INSERT INTO CENY3 
   SELECT * FROM CENY2;

--Výpis nově vytvořené tabulky
--;Hč.zboží;cena/j.;název zboží

SELECT * FROM CENY3
