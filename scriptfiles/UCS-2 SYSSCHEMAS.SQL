--Seznam schemat
SELECT varchar (SCHEMA_NAME, 10) schema_name,
       varchar (SCHEMA_OWNER, 10) owner,
       varchar (SCHEMA_CREATOR, 10) creator,
       CREATION_TIMESTAMP,
       SCHEMA_SIZE size,
       varchar (SCHEMA_TEXT, 20) schema_text,
       varchar (SYSTEM_SCHEMA_NAME, 10) sys_name,
       IASP_NUMBER isp
FROM QSYS2.SYSSCHEMAS
     WHERE substring(SCHEMA_NAME, 1, 1) <> 'Q'
     AND   substring(SCHEMA_NAME, 1, 3) <> 'SYS'
