-- gateway.terminals definition

-- Drop table

-- DROP TABLE gateway.terminals;

CREATE TABLE gateway.terminals (
	id varchar(255) NOT NULL,
	"data" jsonb NOT NULL,
	status varchar(255) NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NULL,
	"source" varchar(10) DEFAULT 'KAFKA'::character varying NOT NULL,
	received_at timestamptz NULL,
	CONSTRAINT terminals_pkey PRIMARY KEY (id)
);