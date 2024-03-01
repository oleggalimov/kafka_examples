CREATE SEQUENCE IF NOT EXISTS simple_messages_id_seq;

CREATE TABLE simple_messages (
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('simple_messages_id_seq'),
    message_id BIGINT,
    message VARCHAR(10) NOT NULL,
    consumer INT4,
    row_insert_time TIMESTAMP
);

CREATE TABLE invalid_simple_messages (
    id UUID,
    topic VARCHAR(50),
    row_insert_time TIMESTAMP,
    payload TEXT,
    cause TEXT
 );