--------------------------------------------------------------------------------
-- core bio info
drop schema if exists core_bio cascade;
create schema if not exists core_bio;


-----------------------------------------------------------------
--  PRONOUNS
drop table if exists core_bio.pronouns;
create table core_bio.pronouns (
                                   id serial primary key,
                                   name character varying(255),
                                   inserted_at timestamp without time zone not null,
                                   updated_at timestamp without time zone not null
);

CREATE OR REPLACE FUNCTION core_bio.pronouns_biu()
    RETURNS trigger AS $$
BEGIN
    if TG_OP = 'INSERT' then
        NEW.inserted_at := now();
    end if;
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER pronouns_biu
    BEFORE INSERT or UPDATE ON core_bio.pronouns
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.pronouns_biu();

insert into core_bio.pronouns(name)
values('N/A'),
      ('He/Him'),
      ('She/Her'),
      ('They/Them');

-----------------------------------------------------------------
--  GENDERS
drop table if exists core_bio.genders;
create table core_bio.genders(
                                 id          varchar(2) primary key,
                                 name        varchar(255),
                                 inserted_at timestamp without time zone not null default now(),
                                 updated_at  timestamp without time zone not null default now()
);

CREATE OR REPLACE FUNCTION core_bio.genders_biu()
    RETURNS trigger AS $$
BEGIN
    if TG_OP = 'INSERT' then
        NEW.inserted_at := now();
    end if;
    NEW.id = upper(NEW.id);
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER genders_biu
    BEFORE INSERT or UPDATE ON core_bio.genders
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.genders_biu();

insert into core_bio.genders(id, name)
values ('M','Male'),
       ('F', 'Female'),
       ('NB','Non-binary'),
       ('T','Transgender');

-----------------------------------------------------------------
--  PATIENTS
drop table if exists core_bio.patients cascade;
CREATE TABLE core_bio.patients (patient_id BIGSERIAL PRIMARY KEY,
                                preferred_name VARCHAR(255),
                                first_name VARCHAR(255),
                                middle_name VARCHAR(255),
                                last_name VARCHAR(255),
                                dob DATE,
                                gender varchar(2) references core_bio.genders(id),
                                pronouns int references core_bio.pronouns(id),
                                strip_id varchar(255),
                                last_login timestamp without time zone,
                                created_by varchar(20) not null,
                                created_date timestamp without time zone not null,
                                updated_by varchar(20) not null,
                                updated_date timestamp without time zone not null
);

CREATE OR REPLACE FUNCTION core_bio.patients_biu()
    RETURNS trigger AS $$
BEGIN
    if TG_OP = 'INSERT' then
        NEW.created_by := current_user;
        NEW.created_date := now();
    end if;
    NEW.gender = upper(NEW.gender);
    NEW.updated_by := current_user;
    NEW.updated_date := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER patients_biu
    BEFORE INSERT or UPDATE ON core_bio.patients
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.patients_biu();

-----------------------------------------------------------------
-- PHONE TYPE
drop table if exists core_bio.phone_type;
create table if not exists core_bio.phone_type (code varchar(4) primary key,
                                                description varchar(20)
);

insert into core_bio.phone_type (code, description)
values ('H', 'Home'),
       ('W', 'Work'),
       ('M', 'Mobile'),
       ('O', 'Other');

-----------------------------------------------------------------
-- PHONE
drop table if eXists core_bio.phone;
CREATE TABLE if not exists core_bio.phone (id BIGSERIAL,
                                           patient_id BIGINT REFERENCES core_bio.patients(patient_id),
                                           seq_id int,
                                           phone_number VARCHAR(12),
                                           type char references core_bio.phone_type(code),
                                           sms boolean default false,
                                           effective_date timestamp without time zone not null default now(),
                                           expiration_date timestamp without time zone,
                                           primary key (patient_id, seq_id)
);

-- Insert into phone number table
drop FUNCTION if exists core_bio.phone_biu;
CREATE or replace FUNCTION core_bio.phone_biu()
    RETURNS trigger AS $$
BEGIN
    DECLARE
        row_count numeric := 0;
        v_phone_number core_bio.phone.phone_number%type;
    BEGIN
        update core_bio.phone
        set expiration_date = now()
        where patient_id = NEW.patient_id
          and type = NEW.type
          and expiration_date is null;
        -- get count of updated
        GET DIAGNOSTICS row_count = ROW_COUNT;
        -- display count of updated
        RAISE NOTICE 'Row(s) updated: %', row_count;

        if TG_OP = 'INSERT' then
            -- Format phone number - remove non-numerics
            v_phone_number := regexp_replace(NEW.phone_number, '[^0-9]+', '', 'g');
            if length(v_phone_number) =  10 then
                v_phone_number := substr(v_phone_number,0,4) || '-' || substr(v_phone_number,4,3) || '-' || substr(v_phone_number,7);
            end if;
            NEW.phone_number := v_phone_number;
            NEW.seq_id := (
                select coalesce(max(seq_id),0) + 1
                from core_bio.phone
                where patient_id = NEW.patient_id
            );
        END IF;
        return NEW;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'An error while inserting phone number: %', v_phone_number;
    END;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER phone_biu
    BEFORE INSERT or UPDATE ON core_bio.phone
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.phone_biu();


-----------------------------------------------------------------
-- Email
drop table if exists core_bio.emails;
CREATE TABLE if not exists core_bio.emails (id  BIGSERIAL unique,
                                           patient_id BIGINT REFERENCES core_bio.patients(patient_id),
                                           seq_id int,
                                           email VARCHAR(120),
                                           effective_date timestamp without time zone not null,
                                           expiration_date timestamp without time zone,
                                           primary key (patient_id, seq_id)
);

CREATE OR REPLACE FUNCTION core_bio.emails_biu()
    RETURNS trigger AS $$
BEGIN
    update core_bio.emails
    set expiration_date = now()
    where patient_id = NEW.patient_id
      and expiration_date is null;
    if TG_OP = 'INSERT' then
        NEW.seq_id := (select coalesce(max(seq_id),0) + 1
                       from core_bio.emails
                       where patient_id =NEW.patient_id);
        NEW.effective_date := now();
    end if;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER emails_biu
    BEFORE INSERT or UPDATE ON core_bio.emails
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.emails_biu();


-----------------------------------------------------------------
-- Address type
drop table if exists core_bio.address_types;
create table if not exists core_bio.address_types (code varchar(4) primary key,
                                                  description varchar(20)
);

insert into core_bio.address_types (code, description)
values ('H', 'Home'),
       ('B', 'Billing'),
       ('O', 'Other');


-----------------------------------------------------------------
-- Address
drop table if exists core_bio.addresses;
CREATE TABLE if not exists core_bio.addresses (id BIGSERIAL,
                                             patient_id BIGINT REFERENCES core_bio.patients(patient_id),
                                             seq_id int,
                                             address_line_1 VARCHAR(255) not null,
                                             address_line_2 VARCHAR(255),
                                             city VARCHAR(255) not null ,
                                             state VARCHAR(255) not null ,
                                             zip VARCHAR(255) not null ,
                                             type char references core_bio.address_types(code),
                                             effective_date timestamp without time zone not null,
                                             expiration_date timestamp without time zone,
                                             created_by varchar(20) not null,
                                             created_date timestamp without time zone not null,
                                             updated_by varchar(20) not null,
                                             updated_date timestamp without time zone not null,
                                             primary key (patient_id, seq_id)
);

CREATE OR REPLACE FUNCTION core_bio.addresses_biu()
    RETURNS trigger AS $$
BEGIN
    if TG_OP = 'INSERT' then
        NEW.seq_id := (select coalesce(max(seq_id),0) + 1 from core_bio.addresses where patient_id =NEW.patient_id);
        NEW.effective_date := now();
        NEW.created_by := current_user;
        NEW.created_date := now();
    end if;
    NEW.updated_by := current_user;
    NEW.updated_date := now();

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER addresses_biu
    BEFORE INSERT or UPDATE ON core_bio.addresses
    FOR EACH ROW
EXECUTE PROCEDURE core_bio.addresses_biu();
