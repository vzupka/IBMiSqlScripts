--First N natural numbers recursively
WITH RECURSIVE FIRST_NUMBERS (N) AS 
  (
    SELECT 1 
    FROM ( VALUES (1) ) as ROOT

    UNION ALL

    SELECT N + 1 
    FROM FIRST_NUMBERS 
      WHERE n < 10 
  )

  SELECT N FROM FIRST_NUMBERS
