--Kusovník hierarchickým dotazem jednoduchý
--;HSouč.;Pods.;Počet;
--;H     ;     ;pods.;
SELECT 
   SOUCAST , 
   PODSOUCAST , 
   SMALLINT(POCET) AS POCET
FROM SOUCASTI
  START WITH SOUCAST = 'A'
  CONNECT BY SOUCAST = PRIOR PODSOUCAST
  ORDER SIBLINGS BY PODSOUCAST
