drop table if exists core_bio.passwords;
CREATE TABLE if not exists core_bio.passwords (id  serial unique,
                                               patient_id INTEGER REFERENCES core_bio.patients(patient_id),
                                               seq_id int,
                                               hashed_password VARCHAR(120),
                                               effective_date timestamp without time zone not null default now(),
                                               expiration_date timestamp without time zone,
                                               primary key (patient_id, seq_id)
);

CREATE OR REPLACE FUNCTION core_bio.passwords_biu()
    RETURNS trigger AS $$
BEGIN
    update core_bio.passwords
    set expiration_date = now()
    where patient_id = NEW.patient_id
      and expiration_date is null;

    NEW.seq_id := (select coalesce(max(seq_id),0) + 1
                   from core_bio.passwords
                   where patient_id =NEW.patient_id);
    NEW.effective_date := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER passwords_biu
    BEFORE INSERT ON core_bio.passwords
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.passwords_biu();


drop table if exists core_bio.password_resets;
create table core_bio.password_resets(
                                         id                 serial primary key,
                                         patient_id         integer references core_bio.patients(patient_id),
                                         email_id           integer references core_bio.emails(id),
                                         hashed_token       varchar(255),
                                         successful_reset   boolean not null default false,
                                         requested_at       timestamp without time zone not null default now(),
                                         expires_at         timestamp without time zone not null,
                                         updated_at         timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION core_bio.password_resets_biu()
    RETURNS trigger AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER password_resets_biu
    BEFORE INSERT or UPDATE ON core_bio.password_resets
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.password_resets_biu();



