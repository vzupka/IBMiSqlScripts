--Factorial recursively

WITH RECURSIVE IN_MEMORY (N, FACTORIAL) AS 
( 
  -- Initial Subquery
  SELECT 0, 1 
  FROM ( VALUES(0, 1) ) as ROOT 

  UNION ALL 

  -- Recursive Subquery
  SELECT N + 1, (N + 1) * FACTORIAL 
  FROM IN_MEMORY  
    WHERE N < 10
)

-- Final query
SELECT N, FACTORIAL FROM IN_MEMORY
