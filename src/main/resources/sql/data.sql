insert into core_bio.patients(preferred_name, first_name, middle_name, last_name, dob, gender)
values ('Greg', 'Gregory', 'John', 'Bowen', '1991-05-04', 'F'),
       (null,'John', null, 'Doe', '1990-01-01', 'M'),
       (null, 'David',null, 'Smith','1950-01-01','M');

insert into core_bio.phone(patient_id, phone_number, type)
values (1, '800-600-6014', 'H'),
       (1, '123-456-7890', 'M'),
       (2, '2056448258', 'W');

insert into core_bio.addresses(patient_id, address_line_1, city, state, zip, type)
values (1, '123 1rst St', 'New York', 'NY', '10001', 'H'),
       (2, '456 Main St', 'New York', 'NY', '10001', 'H'),
       (2, '1234 Cheery St', 'New York', 'NY', '10001', 'H');

insert into core_bio.emails (patient_id, email_address)
values (1, 'gbowen@mindful.care'),
       (2, 'david.smith@example.com');
