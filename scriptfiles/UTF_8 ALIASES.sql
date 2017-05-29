--Vytvořit alias objekty pro tabulku CENY2

CREATE ALIAS CENY2_MBR FOR VZTOOL.CENY2(CENY2_MBR);

CREATE ALIAS CENY2_01 FOR VZTOOL.CENY2(CENY2_01);
CREATE ALIAS CENY2_02 FOR VZTOOL.CENY2(CENY2_02);
CREATE ALIAS CENY2_03 FOR VZTOOL.CENY2(CENY2_03);

INSERT INTO CENY2_01 values ('00001', 8,99, 'PIŠKOTY OPAVIA');
INSERT INTO CENY2_01 values ('00002', 459,00, 'Zubní pasta Kalodont');
INSERT INTO CENY2_01 values ('00003', 1,25, 'Prádelní šňůra');

INSERT INTO CENY2_02 values ('00004', 10,50, 'Ponožky pánské tmavé');

INSERT INTO CENY2_02 values ('00005', 120,00, 'Tričko bílé');
INSERT INTO CENY2_02 values ('00006', 10,55, 'Ponožky pánské bílé, nové');

SET SCHEMA DEFAULT