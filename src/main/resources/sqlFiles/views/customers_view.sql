-- View: customers.customers_view

-- DROP VIEW customers.customers_view;

CREATE OR REPLACE VIEW customers.customers_view AS
 SELECT customers.name,
    customers.phone
   FROM customers.customers;

ALTER TABLE customers.customers_view
  OWNER TO postgres;
