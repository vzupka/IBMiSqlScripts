--Seznam tabulek/souborů ve schematu

--;HSchema;Tabulka;Typ tabulky;Textový popis;Typ souboru;Poč.;Délka;
--;H      ;       ;           ;             ;           ;sl. ;zázn.; 
SELECT varchar(TABLE_SCHEMA, 10) schema,
       varchar(TABLE_NAME, 10) table,

--     Table type:
       CASE TABLE_TYPE
            WHEN 'T' THEN
             CONCAT (TABLE_TYPE, ' = Table') 
            WHEN 'P' THEN
             CONCAT (TABLE_TYPE, ' = Physical file')
            WHEN 'L' THEN
             CONCAT (TABLE_TYPE, ' = Logical file')
            WHEN 'V' THEN
             CONCAT (TABLE_TYPE, ' = View')
            WHEN 'A' THEN
             CONCAT (TABLE_TYPE, ' = Alias')
            WHEN 'M' THEN
             CONCAT (TABLE_TYPE, ' = Materialized query table')
            END "Table_type",

       TABLE_TEXT,

--     File type:
       CASE WHEN FILE_TYPE = 'D' 
            THEN
             CONCAT (FILE_TYPE, ' = DATA') 
            ELSE
             CONCAT (FILE_TYPE, ' = SOURCE')
            END file_type,
       int (COLUMN_COUNT) col_cnt,
       int (ROW_LENGTH) row_len

FROM QSYS2.SYSTABLES                                
WHERE TABLE_SCHEMA = 'VZTOOL' 
        AND SUBSTRING(TABLE_NAME, 1, 1) <> 'Q'
        AND SUBSTRING(TABLE_NAME, 1, 3) <> 'SYS'       
