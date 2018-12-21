--insert into tb1 select * from b,c;

--insert into tb2 select * from b1 left join c1 on 1=1;
DECLARE
  v orders%ROWTYPE;
BEGIN
  SELECT * INTO v FROM orders LIMIT 1;
  DBMS_OUTPUT.PUT_LINE('Item: ' || v.name || ' - ' || v.description);
END;