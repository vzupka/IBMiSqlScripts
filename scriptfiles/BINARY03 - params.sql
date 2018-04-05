--Select - parameters
--;? 01; ; VARBINARKA - větší nebo rovno; 02;

select BINARKA, VARBINARKA from BINARY01
  where hex(VARBINARKA) >= ?
  order by VARBINARKA
