drop schema if exists security cascade;
create schema if not exists security;
------------------------------------------
------------------------------------------

drop table if exists security.passwords;
CREATE TABLE if not exists security.passwords (id  serial unique,
                                               patient_id INTEGER REFERENCES core_bio.patients(patient_id),
                                               seq_id int,
                                               hashed_password VARCHAR(120),
                                               effective_date timestamp without time zone not null default now(),
                                               expiration_date timestamp without time zone,
                                               primary key (patient_id, seq_id)
);

CREATE OR REPLACE FUNCTION security.passwords_biu()
    RETURNS trigger AS $$
BEGIN
    update security.passwords
    set expiration_date = now()
    where patient_id = NEW.patient_id
      and expiration_date is null;

    NEW.seq_id := (select coalesce(max(seq_id),0) + 1
                   from security.passwords
                   where patient_id =NEW.patient_id);
    NEW.effective_date := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER passwords_biu
    BEFORE INSERT ON security.passwords
    FOR EACH ROW
EXECUTE PROCEDURE security.passwords_biu();


drop table if exists security.password_resets;
create table security.password_resets(
                                         id                 serial primary key,
                                         patient_id         integer references core_bio.patients(patient_id),
                                         email_id           integer references core_bio.emails(id),
                                         hashed_token       varchar(255),
                                         successful_reset   boolean not null default false,
                                         requested_at       timestamp without time zone not null default now(),
                                         expires_at         timestamp without time zone not null,
                                         updated_at         timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION security.password_resets_biu()
    RETURNS trigger AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER password_resets_biu
    BEFORE INSERT or UPDATE ON security.password_resets
    FOR EACH ROW
EXECUTE PROCEDURE security.password_resets_biu();
-------------------------------------------------------------------
-------------------------------------------------------------------
drop table if exists security.event_types;
CREATE TABLE security.event_types (
                                      id   SERIAL PRIMARY KEY,
                                      name TEXT NOT NULL UNIQUE
);
insert into security.event_types (name) VALUES ('login_success'),
                                               ('login_failure'),
                                               ('logout'),
                                               ('mfa_challenge'),
                                               ('mfa_success'),
                                               ('session_revoked'),
                                               ('password_changed');

drop table if exists security.authentication_events;
CREATE TABLE security.authentication_events (
                                                id         BIGSERIAL PRIMARY KEY,
                                                user_id    int REFERENCES core_bio.patients(patient_id),
--                                       session_id UUID REFERENCES security.auth_sessions(id),
                                                event_type int references security.event_types(id) NOT NULL,
                                                ip_address INET,
                                                service_name TEXT,
                                                user_agent TEXT,
                                                created_by TEXT NOT NULL,
                                                created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
