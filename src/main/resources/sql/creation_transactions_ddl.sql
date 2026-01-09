------------------------------------------------------------------------------
-- Transactions
drop schema if exists billing cascade;
create schema if not exists billing;

drop table if exists billing.transaction_desc;
create table if not exists billing.transaction_desc (code varchar(4) primary key,
                                                          description varchar(255),
                                                          charge char
);
insert into billing.transaction_desc (code, description, charge)
values ('APPT', 'Standard ', 'C'),
       ('PYMT', 'Payment', 'D'),
       ('LTFE', 'Late Fee', 'C'),
       ('PRMO', 'Promotion', 'D'),
       ('FEE', 'Fee', 'C'),
       ('INT', 'Interest', 'C');

-- main Transaction table
drop table if exists billing.transactions;
create table if not exists billing.transactions (pid int not null references core_bio.patients(patient_id),
                                                      seq_id int not null,
                                                      amount numeric(10,2) not null,
                                                      trans_date date not null,
                                                      trans_code varchar(4) references billing.transaction_desc(code),
                                                      trans_note varchar(255),
                                                      created_by varchar(20) not null,
                                                      created_date timestamp not null,
                                                      updated_by varchar(20) not null,
                                                      updated_date timestamp not null,
                                                      primary key (pid, seq_id)
);

CREATE OR REPLACE FUNCTION billing.transactions_biu()
    RETURNS trigger AS $$
BEGIN
    if TG_OP = 'INSERT' then
        -- Set the value of 'my_column' in the new row
        NEW.seq_id := (select coalesce(max(seq_id),0) + 1 from billing.transactions where pid =NEW.pid);
        NEW.pid := NEW.pid;
        NEW.created_by := current_user;
        NEW.created_date := now();
    end if;
    NEW.updated_by := current_user;
    NEW.updated_date := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE or replace TRIGGER transactions_biu
    BEFORE INSERT or UPDATE ON billing.transactions
    FOR EACH ROW
EXECUTE PROCEDURE billing.transactions_biu();
