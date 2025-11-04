CREATE TABLE event_aggregation (
                                   id varchar(255) NOT NULL,
                                   exchange bytea NOT NULL,
                                   version BIGINT NOT NULL,
                                   constraint event_aggregation_pk PRIMARY KEY (id)
);

CREATE TABLE event_aggregation_completed (
                                             id varchar(255) NOT NULL,
                                             exchange bytea NOT NULL,
                                             version BIGINT NOT NULL,
                                             constraint event_aggregation_completed_pk PRIMARY KEY (id)
);
