-- processor.terminals definition

-- Drop table

-- DROP TABLE processor.terminals;

CREATE TABLE processor.terminals (
	id varchar(255) NOT NULL,
	"data" jsonb NOT NULL,
	status varchar(255) DEFAULT 'PENDING'::character varying NOT NULL,
	created_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamptz DEFAULT CURRENT_TIMESTAMP NULL,
	"source" varchar(10) DEFAULT 'UNDEF'::character varying NULL,
	received_at timestamptz NULL,
	CONSTRAINT terminals_pkey PRIMARY KEY (id)
);