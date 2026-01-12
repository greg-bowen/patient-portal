

-- insert into address table
drop procedure if exists core_bio.insert_address(patientid integer, street varchar, city varchar, state varchar, zip varchar);
CREATE or replace PROCEDURE core_bio.insert_address(p_patient_id int, p_street1 varchar, p_street2 varchar, p_city varchar, p_state varchar, p_zip varchar, p_type varchar)
    LANGUAGE plpgsql
AS $$
BEGIN
    DECLARE
        row_count numeric := 0;
    BEGIN
        update core_bio.address
        set expiration_date = now()
        where patient_id = p_patient_id
          and type = p_type
          and expiration_date is null;
        -- get count of updated
        GET DIAGNOSTICS row_count = ROW_COUNT;
        -- display count of updated
        RAISE NOTICE 'Row(s) updated: %', row_count;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'An error while expiring address for patientInfo: %', p_patient_id;
    END;

    BEGIN
        insert into core_bio.address (patient_id, address_line_1, address_line_2, city, state, zip, type)
        values (p_patient_id, p_street1, p_street2, p_city, p_state, p_zip, p_type);
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'An error while inserting address for patientInfo: %', p_patient_id;
    END;
END;
$$;

-- Insert into email table
drop procedure if exists core_bio.insert_email(p_patient_id int, p_email varchar);
CREATE or replace PROCEDURE core_bio.insert_email(p_patient_id int, p_email varchar)
    LANGUAGE plpgsql
AS $$
BEGIN
    DECLARE
        row_count numeric := 0;
    BEGIN
        update core_bio.email
        set expiration_date = now()
        where patient_id = p_patient_id
          and expiration_date is null;
        -- get count of updated
        GET DIAGNOSTICS row_count = ROW_COUNT;
        -- display count of updated
        RAISE NOTICE 'Row(s) updated: %', row_count;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'An error while expiring email for patientInfo: %', p_patient_id;
    END;

    BEGIN
        insert into core_bio.email (patient_id, email) values (p_patient_id, p_email);
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'An error while inserting email for patientInfo: %', p_patient_id;
    END;
END;
$$;