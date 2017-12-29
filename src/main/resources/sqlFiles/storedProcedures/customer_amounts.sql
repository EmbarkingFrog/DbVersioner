-- Function: customers.customersamount()

-- DROP FUNCTION customers.customersamount();

CREATE OR REPLACE FUNCTION customers.customersamount()
  RETURNS integer AS
$BODY$
declare
	total integer;
BEGIN
   SELECT count(*) into total FROM customers.customers;
   RETURN total;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION customers.customersamount()
  OWNER TO postgres;
