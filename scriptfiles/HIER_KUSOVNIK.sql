--Kusovník hierarchickým dotazem s vyjádřením větví
 --;HÚroveň;Souč.;Pods.;Počet;Start;Větve ->
 --;H hier.;     ;     ;pods.;
SELECT 
   SMALLINT(LEVEL) AS LEVEL, 
   SOUCAST , 
   PODSOUCAST , 
   SMALLINT(POCET) AS POCET,
   CONNECT_BY_ROOT SOUCAST AS KOREN, 
   CAST(SYS_CONNECT_BY_PATH(SOUCAST, '- ') AS VARCHAR(50)) AS VETEV
FROM SOUCASTI
  START WITH SOUCAST = 'A'
  CONNECT BY SOUCAST = PRIOR PODSOUCAST
  ORDER SIBLINGS BY PODSOUCAST