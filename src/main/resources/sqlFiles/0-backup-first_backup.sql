--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.9
-- Dumped by pg_dump version 9.5.9

-- Started on 2017-11-11 10:10:41

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 8 (class 2615 OID 24696)
-- Name: customers; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA customers;


ALTER SCHEMA customers OWNER TO postgres;

--
-- TOC entry 9 (class 2615 OID 24697)
-- Name: items; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA items;


ALTER SCHEMA items OWNER TO postgres;

--
-- TOC entry 10 (class 2615 OID 24698)
-- Name: orders; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA orders;


ALTER SCHEMA orders OWNER TO postgres;

--
-- TOC entry 11 (class 2615 OID 24699)
-- Name: versions; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA versions;


ALTER SCHEMA versions OWNER TO postgres;

--
-- TOC entry 1 (class 3079 OID 12355)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2145 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = orders, pg_catalog;

--
-- TOC entry 557 (class 1247 OID 24701)
-- Name: order_status; Type: TYPE; Schema: orders; Owner: postgres
--

CREATE TYPE order_status AS ENUM (
    'canceled',
    'arrived',
    'shipped',
    'paid',
    'ordered'
);


ALTER TYPE order_status OWNER TO postgres;

SET search_path = customers, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 185 (class 1259 OID 24711)
-- Name: customers; Type: TABLE; Schema: customers; Owner: postgres
--

CREATE TABLE customers (
    name text NOT NULL,
    phone text
);


ALTER TABLE customers OWNER TO postgres;

SET search_path = items, pg_catalog;

--
-- TOC entry 186 (class 1259 OID 24717)
-- Name: items; Type: TABLE; Schema: items; Owner: postgres
--

CREATE TABLE items (
    id integer NOT NULL,
    name text
);


ALTER TABLE items OWNER TO postgres;

--
-- TOC entry 187 (class 1259 OID 24723)
-- Name: items_id_seq; Type: SEQUENCE; Schema: items; Owner: postgres
--

CREATE SEQUENCE items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE items_id_seq OWNER TO postgres;

--
-- TOC entry 2146 (class 0 OID 0)
-- Dependencies: 187
-- Name: items_id_seq; Type: SEQUENCE OWNED BY; Schema: items; Owner: postgres
--

ALTER SEQUENCE items_id_seq OWNED BY items.id;


SET search_path = orders, pg_catalog;

--
-- TOC entry 188 (class 1259 OID 24725)
-- Name: orders; Type: TABLE; Schema: orders; Owner: postgres
--

CREATE TABLE orders (
    id integer NOT NULL,
    order_date timestamp with time zone,
    customer_name text,
    item_id integer
);


ALTER TABLE orders OWNER TO postgres;

--
-- TOC entry 189 (class 1259 OID 24731)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: orders; Owner: postgres
--

CREATE SEQUENCE orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE orders_id_seq OWNER TO postgres;

--
-- TOC entry 2147 (class 0 OID 0)
-- Dependencies: 189
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: orders; Owner: postgres
--

ALTER SEQUENCE orders_id_seq OWNED BY orders.id;


SET search_path = versions, pg_catalog;

--
-- TOC entry 190 (class 1259 OID 24733)
-- Name: versions; Type: TABLE; Schema: versions; Owner: postgres
--

CREATE TABLE versions (
    version text NOT NULL,
    install_date timestamp with time zone NOT NULL,
    creator text,
    description text
);


ALTER TABLE versions OWNER TO postgres;

SET search_path = items, pg_catalog;

--
-- TOC entry 2006 (class 2604 OID 24739)
-- Name: id; Type: DEFAULT; Schema: items; Owner: postgres
--

ALTER TABLE ONLY items ALTER COLUMN id SET DEFAULT nextval('items_id_seq'::regclass);


SET search_path = orders, pg_catalog;

--
-- TOC entry 2007 (class 2604 OID 24740)
-- Name: id; Type: DEFAULT; Schema: orders; Owner: postgres
--

ALTER TABLE ONLY orders ALTER COLUMN id SET DEFAULT nextval('orders_id_seq'::regclass);


SET search_path = customers, pg_catalog;

--
-- TOC entry 2132 (class 0 OID 24711)
-- Dependencies: 185
-- Data for Name: customers; Type: TABLE DATA; Schema: customers; Owner: postgres
--



SET search_path = items, pg_catalog;

--
-- TOC entry 2133 (class 0 OID 24717)
-- Dependencies: 186
-- Data for Name: items; Type: TABLE DATA; Schema: items; Owner: postgres
--



--
-- TOC entry 2148 (class 0 OID 0)
-- Dependencies: 187
-- Name: items_id_seq; Type: SEQUENCE SET; Schema: items; Owner: postgres
--

SELECT pg_catalog.setval('items_id_seq', 1, false);


SET search_path = orders, pg_catalog;

--
-- TOC entry 2135 (class 0 OID 24725)
-- Dependencies: 188
-- Data for Name: orders; Type: TABLE DATA; Schema: orders; Owner: postgres
--



--
-- TOC entry 2149 (class 0 OID 0)
-- Dependencies: 189
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: orders; Owner: postgres
--

SELECT pg_catalog.setval('orders_id_seq', 1, false);


SET search_path = versions, pg_catalog;

--
-- TOC entry 2137 (class 0 OID 24733)
-- Dependencies: 190
-- Data for Name: versions; Type: TABLE DATA; Schema: versions; Owner: postgres
--



SET search_path = customers, pg_catalog;

--
-- TOC entry 2009 (class 2606 OID 24742)
-- Name: name_pk; Type: CONSTRAINT; Schema: customers; Owner: postgres
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT name_pk PRIMARY KEY (name);


SET search_path = items, pg_catalog;

--
-- TOC entry 2011 (class 2606 OID 24744)
-- Name: id_pk; Type: CONSTRAINT; Schema: items; Owner: postgres
--

ALTER TABLE ONLY items
    ADD CONSTRAINT id_pk PRIMARY KEY (id);


SET search_path = orders, pg_catalog;

--
-- TOC entry 2013 (class 2606 OID 24746)
-- Name: id_pk; Type: CONSTRAINT; Schema: orders; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT id_pk PRIMARY KEY (id);


SET search_path = versions, pg_catalog;

--
-- TOC entry 2015 (class 2606 OID 24748)
-- Name: version_pk; Type: CONSTRAINT; Schema: versions; Owner: postgres
--

ALTER TABLE ONLY versions
    ADD CONSTRAINT version_pk PRIMARY KEY (version);


SET search_path = orders, pg_catalog;

--
-- TOC entry 2016 (class 2606 OID 24749)
-- Name: customer_fk; Type: FK CONSTRAINT; Schema: orders; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT customer_fk FOREIGN KEY (customer_name) REFERENCES customers.customers(name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2017 (class 2606 OID 24754)
-- Name: item_fk; Type: FK CONSTRAINT; Schema: orders; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT item_fk FOREIGN KEY (item_id) REFERENCES items.items(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2144 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-11-11 10:10:42

--
-- PostgreSQL database dump complete
--

