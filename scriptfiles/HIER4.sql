SELECT 
  CAST(SYS_CONNECT_BY_PATH(DEPTNAME, '  /') AS VARCHAR(80)) AS ORG
FROM DEPARTMENT
START WITH DEPTNO = 'A00'
CONNECT BY NOCYCLE PRIOR DEPTNO = ADMRDEPT
