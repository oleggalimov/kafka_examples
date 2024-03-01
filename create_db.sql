CREATE SEQUENCE IF NOT EXISTS simple_messages_id_seq;

CREATE TABLE simple_messages (
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('simple_messages_id_seq'),
    message_id BIGINT,
    message VARCHAR(10),
    consumer INT4,
    row_insert_time TIMESTAMP
);
