--UCS-2 Zkouška češtiny v kódování UCS-2
--;? 1 ;NUMERIC; Jednotková cena větší nebo rovná: ; 1.00;
--;? 2; TIMESTAMP ; Časové razítko:; 2016-01-18 17:34:55;

select CZBOZI, CENAJ, NAZZBO, DATUM, CAS, RAZITKO from CENY2
  where CENAJ <= ?
  and RAZITKO < ?
order by CZBOZI
