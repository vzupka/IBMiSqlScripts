--Kusovník rozpad

WITH RECURSIVE RS (SOUCAST, PODSOUCAST, POCET) AS 
( SELECT ROOT.SOUCAST, ROOT.PODSOUCAST, ROOT.POCET
  FROM SOUCASTI ROOT
    WHERE ROOT.SOUCAST = 'A'
  UNION ALL
  SELECT CHILD.SOUCAST, CHILD.PODSOUCAST, CHILD.POCET 
  FROM RS PARENT, SOUCASTI CHILD
    WHERE PARENT.PODSOUCAST = CHILD.SOUCAST
)
SELECT  SOUCAST, PODSOUCAST, POCET FROM RS
ORDER BY SOUCAST, PODSOUCAST
